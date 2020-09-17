package com.squareup.picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.NetworkInfo;
import android.os.Build;
import com.squareup.picasso.NetworkRequestHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

class BitmapHunter implements Runnable {
    private static final Object DECODE_LOCK = new Object();
    private static final RequestHandler ERRORING_HANDLER = new RequestHandler() {
        public boolean canHandleRequest(Request data) {
            return true;
        }

        public RequestHandler.Result load(Request request, int networkPolicy) throws IOException {
            throw new IllegalStateException("Unrecognized type of request: " + request);
        }
    };
    private static final ThreadLocal<StringBuilder> NAME_BUILDER = new ThreadLocal<StringBuilder>() {
        /* access modifiers changed from: protected */
        public StringBuilder initialValue() {
            return new StringBuilder("Picasso-");
        }
    };
    private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();
    Action action;
    List<Action> actions;
    final Cache cache;
    final Request data;
    final Dispatcher dispatcher;
    Exception exception;
    int exifOrientation;
    Future<?> future;
    final String key;
    Picasso.LoadedFrom loadedFrom;
    final int memoryPolicy;
    int networkPolicy;
    final Picasso picasso;
    Picasso.Priority priority;
    final RequestHandler requestHandler;
    Bitmap result;
    int retryCount;
    final int sequence = SEQUENCE_GENERATOR.incrementAndGet();
    final Stats stats;

    BitmapHunter(Picasso picasso2, Dispatcher dispatcher2, Cache cache2, Stats stats2, Action action2, RequestHandler requestHandler2) {
        this.picasso = picasso2;
        this.dispatcher = dispatcher2;
        this.cache = cache2;
        this.stats = stats2;
        this.action = action2;
        this.key = action2.getKey();
        this.data = action2.getRequest();
        this.priority = action2.getPriority();
        this.memoryPolicy = action2.getMemoryPolicy();
        this.networkPolicy = action2.getNetworkPolicy();
        this.requestHandler = requestHandler2;
        this.retryCount = requestHandler2.getRetryCount();
    }

    static Bitmap decodeStream(Source source, Request request) throws IOException {
        BufferedSource bufferedSource = Okio.buffer(source);
        boolean isWebPFile = Utils.isWebPFile(bufferedSource);
        boolean isPurgeable = request.purgeable && Build.VERSION.SDK_INT < 21;
        BitmapFactory.Options options = RequestHandler.createBitmapOptions(request);
        boolean calculateSize = RequestHandler.requiresInSampleSize(options);
        if (isWebPFile || isPurgeable) {
            byte[] bytes = bufferedSource.readByteArray();
            if (calculateSize) {
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                RequestHandler.calculateInSampleSize(request.targetWidth, request.targetHeight, options, request);
            }
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        }
        InputStream stream = bufferedSource.inputStream();
        if (calculateSize) {
            MarkableInputStream markStream = new MarkableInputStream(stream);
            stream = markStream;
            markStream.allowMarksToExpire(false);
            long mark = markStream.savePosition(1024);
            BitmapFactory.decodeStream(stream, (Rect) null, options);
            RequestHandler.calculateInSampleSize(request.targetWidth, request.targetHeight, options, request);
            markStream.reset(mark);
            markStream.allowMarksToExpire(true);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(stream, (Rect) null, options);
        if (bitmap != null) {
            return bitmap;
        }
        throw new IOException("Failed to decode stream.");
    }

    public void run() {
        try {
            updateThreadName(this.data);
            if (this.picasso.loggingEnabled) {
                Utils.log("Hunter", "executing", Utils.getLogIdsForHunter(this));
            }
            this.result = hunt();
            if (this.result == null) {
                this.dispatcher.dispatchFailed(this);
            } else {
                this.dispatcher.dispatchComplete(this);
            }
        } catch (NetworkRequestHandler.ResponseException e) {
            if (!NetworkPolicy.isOfflineOnly(e.networkPolicy) || e.code != 504) {
                this.exception = e;
            }
            this.dispatcher.dispatchFailed(this);
        } catch (IOException e2) {
            this.exception = e2;
            this.dispatcher.dispatchRetry(this);
        } catch (OutOfMemoryError e3) {
            StringWriter writer = new StringWriter();
            this.stats.createSnapshot().dump(new PrintWriter(writer));
            this.exception = new RuntimeException(writer.toString(), e3);
            this.dispatcher.dispatchFailed(this);
        } catch (Exception e4) {
            this.exception = e4;
            this.dispatcher.dispatchFailed(this);
        } catch (Throwable th) {
            Thread.currentThread().setName("Picasso-Idle");
            throw th;
        }
        Thread.currentThread().setName("Picasso-Idle");
    }

    /* access modifiers changed from: package-private */
    public Bitmap hunt() throws IOException {
        Bitmap bitmap = null;
        if (!MemoryPolicy.shouldReadFromMemoryCache(this.memoryPolicy) || (bitmap = this.cache.get(this.key)) == null) {
            this.networkPolicy = this.retryCount == 0 ? NetworkPolicy.OFFLINE.index : this.networkPolicy;
            RequestHandler.Result result2 = this.requestHandler.load(this.data, this.networkPolicy);
            if (result2 != null) {
                this.loadedFrom = result2.getLoadedFrom();
                this.exifOrientation = result2.getExifOrientation();
                bitmap = result2.getBitmap();
                if (bitmap == null) {
                    Source source = result2.getSource();
                    try {
                        bitmap = decodeStream(source, this.data);
                    } finally {
                        try {
                            source.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
            if (bitmap != null) {
                if (this.picasso.loggingEnabled) {
                    Utils.log("Hunter", "decoded", this.data.logId());
                }
                this.stats.dispatchBitmapDecoded(bitmap);
                if (this.data.needsTransformation() || this.exifOrientation != 0) {
                    synchronized (DECODE_LOCK) {
                        if (this.data.needsMatrixTransform() || this.exifOrientation != 0) {
                            bitmap = transformResult(this.data, bitmap, this.exifOrientation);
                            if (this.picasso.loggingEnabled) {
                                Utils.log("Hunter", "transformed", this.data.logId());
                            }
                        }
                        if (this.data.hasCustomTransformations()) {
                            bitmap = applyCustomTransformations(this.data.transformations, bitmap);
                            if (this.picasso.loggingEnabled) {
                                Utils.log("Hunter", "transformed", this.data.logId(), "from custom transformations");
                            }
                        }
                    }
                    if (bitmap != null) {
                        this.stats.dispatchBitmapTransformed(bitmap);
                    }
                }
            }
            return bitmap;
        }
        this.stats.dispatchCacheHit();
        this.loadedFrom = Picasso.LoadedFrom.MEMORY;
        if (this.picasso.loggingEnabled) {
            Utils.log("Hunter", "decoded", this.data.logId(), "from cache");
        }
        return bitmap;
    }

    /* access modifiers changed from: package-private */
    public void attach(Action action2) {
        boolean loggingEnabled = this.picasso.loggingEnabled;
        Request request = action2.request;
        if (this.action == null) {
            this.action = action2;
            if (!loggingEnabled) {
                return;
            }
            if (this.actions == null || this.actions.isEmpty()) {
                Utils.log("Hunter", "joined", request.logId(), "to empty hunter");
            } else {
                Utils.log("Hunter", "joined", request.logId(), Utils.getLogIdsForHunter(this, "to "));
            }
        } else {
            if (this.actions == null) {
                this.actions = new ArrayList(3);
            }
            this.actions.add(action2);
            if (loggingEnabled) {
                Utils.log("Hunter", "joined", request.logId(), Utils.getLogIdsForHunter(this, "to "));
            }
            Picasso.Priority actionPriority = action2.getPriority();
            if (actionPriority.ordinal() > this.priority.ordinal()) {
                this.priority = actionPriority;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void detach(Action action2) {
        boolean detached = false;
        if (this.action == action2) {
            this.action = null;
            detached = true;
        } else if (this.actions != null) {
            detached = this.actions.remove(action2);
        }
        if (detached && action2.getPriority() == this.priority) {
            this.priority = computeNewPriority();
        }
        if (this.picasso.loggingEnabled) {
            Utils.log("Hunter", "removed", action2.request.logId(), Utils.getLogIdsForHunter(this, "from "));
        }
    }

    private Picasso.Priority computeNewPriority() {
        Picasso.Priority newPriority = Picasso.Priority.LOW;
        boolean hasAny = false;
        boolean hasMultiple = this.actions != null && !this.actions.isEmpty();
        if (this.action != null || hasMultiple) {
            hasAny = true;
        }
        if (!hasAny) {
            return newPriority;
        }
        if (this.action != null) {
            newPriority = this.action.getPriority();
        }
        if (hasMultiple) {
            int n = this.actions.size();
            for (int i = 0; i < n; i++) {
                Picasso.Priority actionPriority = this.actions.get(i).getPriority();
                if (actionPriority.ordinal() > newPriority.ordinal()) {
                    newPriority = actionPriority;
                }
            }
        }
        return newPriority;
    }

    /* access modifiers changed from: package-private */
    public boolean cancel() {
        if (this.action != null) {
            return false;
        }
        if ((this.actions == null || this.actions.isEmpty()) && this.future != null && this.future.cancel(false)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isCancelled() {
        return this.future != null && this.future.isCancelled();
    }

    /* access modifiers changed from: package-private */
    public boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        if (!(this.retryCount > 0)) {
            return false;
        }
        this.retryCount--;
        return this.requestHandler.shouldRetry(airplaneMode, info);
    }

    /* access modifiers changed from: package-private */
    public boolean supportsReplay() {
        return this.requestHandler.supportsReplay();
    }

    /* access modifiers changed from: package-private */
    public Bitmap getResult() {
        return this.result;
    }

    /* access modifiers changed from: package-private */
    public String getKey() {
        return this.key;
    }

    /* access modifiers changed from: package-private */
    public int getMemoryPolicy() {
        return this.memoryPolicy;
    }

    /* access modifiers changed from: package-private */
    public Request getData() {
        return this.data;
    }

    /* access modifiers changed from: package-private */
    public Action getAction() {
        return this.action;
    }

    /* access modifiers changed from: package-private */
    public Picasso getPicasso() {
        return this.picasso;
    }

    /* access modifiers changed from: package-private */
    public List<Action> getActions() {
        return this.actions;
    }

    /* access modifiers changed from: package-private */
    public Exception getException() {
        return this.exception;
    }

    /* access modifiers changed from: package-private */
    public Picasso.LoadedFrom getLoadedFrom() {
        return this.loadedFrom;
    }

    /* access modifiers changed from: package-private */
    public Picasso.Priority getPriority() {
        return this.priority;
    }

    static void updateThreadName(Request data2) {
        String name = data2.getName();
        StringBuilder builder = NAME_BUILDER.get();
        builder.ensureCapacity("Picasso-".length() + name.length());
        builder.replace("Picasso-".length(), builder.length(), name);
        Thread.currentThread().setName(builder.toString());
    }

    static BitmapHunter forRequest(Picasso picasso2, Dispatcher dispatcher2, Cache cache2, Stats stats2, Action action2) {
        Request request = action2.getRequest();
        List<RequestHandler> requestHandlers = picasso2.getRequestHandlers();
        int count = requestHandlers.size();
        for (int i = 0; i < count; i++) {
            RequestHandler requestHandler2 = requestHandlers.get(i);
            if (requestHandler2.canHandleRequest(request)) {
                return new BitmapHunter(picasso2, dispatcher2, cache2, stats2, action2, requestHandler2);
            }
        }
        return new BitmapHunter(picasso2, dispatcher2, cache2, stats2, action2, ERRORING_HANDLER);
    }

    static Bitmap applyCustomTransformations(List<Transformation> transformations, Bitmap result2) {
        int i = 0;
        int count = transformations.size();
        while (i < count) {
            final Transformation transformation = transformations.get(i);
            try {
                Bitmap newResult = transformation.transform(result2);
                if (newResult == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Transformation ");
                    sb.append(transformation.key());
                    sb.append(" returned null after ");
                    sb.append(i);
                    final StringBuilder builder = sb.append(" previous transformation(s).\n\nTransformation list:\n");
                    for (Transformation t : transformations) {
                        builder.append(t.key());
                        builder.append(10);
                    }
                    Picasso.HANDLER.post(new Runnable() {
                        public void run() {
                            throw new NullPointerException(builder.toString());
                        }
                    });
                    return null;
                } else if (newResult == result2 && result2.isRecycled()) {
                    Picasso.HANDLER.post(new Runnable() {
                        public void run() {
                            throw new IllegalStateException("Transformation " + transformation.key() + " returned input Bitmap but recycled it.");
                        }
                    });
                    return null;
                } else if (newResult == result2 || result2.isRecycled()) {
                    result2 = newResult;
                    i++;
                } else {
                    Picasso.HANDLER.post(new Runnable() {
                        public void run() {
                            throw new IllegalStateException("Transformation " + transformation.key() + " mutated input Bitmap but failed to recycle the original.");
                        }
                    });
                    return null;
                }
            } catch (RuntimeException e) {
                Picasso.HANDLER.post(new Runnable() {
                    public void run() {
                        throw new RuntimeException("Transformation " + transformation.key() + " crashed with exception.", e);
                    }
                });
                return null;
            }
        }
        return result2;
    }

    static Bitmap transformResult(Request data2, Bitmap result2, int exifOrientation2) {
        int drawHeight;
        int drawWidth;
        int drawY;
        int drawX;
        boolean onlyScaleDown;
        int targetWidth;
        int inWidth;
        boolean onlyScaleDown2;
        int inHeight;
        float f;
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        float f7;
        float f8;
        int inHeight2;
        int inWidth2;
        float widthRatio;
        float f9;
        float f10;
        float scaleY;
        float scaleX;
        int drawY2;
        int drawWidth2;
        int drawX2;
        int drawX3;
        int drawY3;
        Request request = data2;
        int inWidth3 = result2.getWidth();
        int inHeight3 = result2.getHeight();
        boolean onlyScaleDown3 = request.onlyScaleDown;
        int drawWidth3 = inWidth3;
        int drawHeight2 = inHeight3;
        Matrix matrix = new Matrix();
        if (data2.needsMatrixTransform() || exifOrientation2 != 0) {
            int targetWidth2 = request.targetWidth;
            int targetHeight = request.targetHeight;
            float targetRotation = request.rotationDegrees;
            if (targetRotation != 0.0f) {
                double cosR = Math.cos(Math.toRadians((double) targetRotation));
                drawX = 0;
                drawY = 0;
                double sinR = Math.sin(Math.toRadians((double) targetRotation));
                if (request.hasRotationPivot) {
                    drawWidth = drawWidth3;
                    matrix.setRotate(targetRotation, request.rotationPivotX, request.rotationPivotY);
                    drawHeight = drawHeight2;
                    int i = targetWidth2;
                    int i2 = targetHeight;
                    double x1T = (((double) request.rotationPivotX) * (1.0d - cosR)) + (((double) request.rotationPivotY) * sinR);
                    inHeight = inHeight3;
                    onlyScaleDown2 = onlyScaleDown3;
                    double y1T = (((double) request.rotationPivotY) * (1.0d - cosR)) - (((double) request.rotationPivotX) * sinR);
                    inWidth = inWidth3;
                    double y2T = (((double) request.targetWidth) * sinR) + y1T;
                    double x2T = (((double) request.targetWidth) * cosR) + x1T;
                    double x3T = ((((double) request.targetWidth) * cosR) + x1T) - (((double) request.targetHeight) * sinR);
                    double y3T = (((double) request.targetWidth) * sinR) + y1T + (((double) request.targetHeight) * cosR);
                    double x4T = x1T - (((double) request.targetHeight) * sinR);
                    double d = sinR;
                    double y4T = (((double) request.targetHeight) * cosR) + y1T;
                    double d2 = cosR;
                    double x2T2 = x2T;
                    double y3T2 = y3T;
                    double y3T3 = x3T;
                    double maxX = Math.max(x4T, Math.max(y3T3, Math.max(x1T, x2T2)));
                    double minX = Math.min(x4T, Math.min(y3T3, Math.min(x1T, x2T2)));
                    double d3 = x4T;
                    double d4 = y3T3;
                    double y2T2 = y2T;
                    double d5 = x1T;
                    double x1T2 = y3T2;
                    double d6 = x2T2;
                    double x2T3 = y4T;
                    double maxY = Math.max(x2T3, Math.max(x1T2, Math.max(y1T, y2T2)));
                    double minY = Math.min(x2T3, Math.min(x1T2, Math.min(y1T, y2T2)));
                    double d7 = y2T2;
                    targetWidth = (int) Math.floor(maxX - minX);
                    targetHeight = (int) Math.floor(maxY - minY);
                    float f11 = targetRotation;
                } else {
                    inWidth = inWidth3;
                    inHeight = inHeight3;
                    onlyScaleDown2 = onlyScaleDown3;
                    double sinR2 = sinR;
                    drawWidth = drawWidth3;
                    drawHeight = drawHeight2;
                    int i3 = targetWidth2;
                    int i4 = targetHeight;
                    double cosR2 = cosR;
                    matrix.setRotate(targetRotation);
                    double x2T4 = ((double) request.targetWidth) * cosR2;
                    double y2T3 = ((double) request.targetWidth) * sinR2;
                    double x3T2 = (((double) request.targetWidth) * cosR2) - (((double) request.targetHeight) * sinR2);
                    double y3T4 = (((double) request.targetWidth) * sinR2) + (((double) request.targetHeight) * cosR2);
                    double x4T2 = -(((double) request.targetHeight) * sinR2);
                    float f12 = targetRotation;
                    double y4T2 = ((double) request.targetHeight) * cosR2;
                    double maxX2 = Math.max(x4T2, Math.max(x3T2, Math.max(0.0d, x2T4)));
                    double minX2 = Math.min(x4T2, Math.min(x3T2, Math.min(0.0d, x2T4)));
                    double d8 = x4T2;
                    double d9 = x2T4;
                    double y3T5 = y3T4;
                    double maxY2 = Math.max(y4T2, Math.max(y3T5, Math.max(0.0d, y2T3)));
                    double d10 = x3T2;
                    double minY2 = Math.min(y4T2, Math.min(y3T5, Math.min(0.0d, y2T3)));
                    int targetWidth3 = (int) Math.floor(maxX2 - minX2);
                    targetHeight = (int) Math.floor(maxY2 - minY2);
                    targetWidth = targetWidth3;
                }
            } else {
                inWidth = inWidth3;
                inHeight = inHeight3;
                onlyScaleDown2 = onlyScaleDown3;
                drawX = 0;
                drawY = 0;
                drawWidth = drawWidth3;
                drawHeight = drawHeight2;
                int i5 = targetHeight;
                float f13 = targetRotation;
                targetWidth = targetWidth2;
            }
            if (exifOrientation2 != 0) {
                int exifRotation = getExifRotation(exifOrientation2);
                int exifTranslation = getExifTranslation(exifOrientation2);
                if (exifRotation != 0) {
                    matrix.preRotate((float) exifRotation);
                    if (exifRotation == 90 || exifRotation == 270) {
                        int targetHeight2 = targetWidth;
                        targetWidth = targetHeight;
                        targetHeight = targetHeight2;
                    }
                }
                if (exifTranslation != 1) {
                    matrix.postScale((float) exifTranslation, 1.0f);
                }
            }
            int targetWidth4 = targetWidth;
            if (request.centerCrop) {
                if (targetWidth4 != 0) {
                    inWidth2 = inWidth;
                    widthRatio = ((float) targetWidth4) / ((float) inWidth2);
                    inHeight2 = inHeight;
                } else {
                    inWidth2 = inWidth;
                    inHeight2 = inHeight;
                    widthRatio = ((float) targetHeight) / ((float) inHeight2);
                }
                if (targetHeight != 0) {
                    f9 = (float) targetHeight;
                    f10 = (float) inHeight2;
                } else {
                    f9 = (float) targetWidth4;
                    f10 = (float) inWidth2;
                }
                float heightRatio = f9 / f10;
                if (widthRatio > heightRatio) {
                    int newSize = (int) Math.ceil((double) (((float) inHeight2) * (heightRatio / widthRatio)));
                    if ((request.centerCropGravity & 48) == 48) {
                        drawY3 = 0;
                    } else if ((request.centerCropGravity & 80) == 80) {
                        drawY3 = inHeight2 - newSize;
                    } else {
                        drawY3 = (inHeight2 - newSize) / 2;
                    }
                    int drawHeight3 = newSize;
                    scaleX = widthRatio;
                    scaleY = ((float) targetHeight) / ((float) drawHeight3);
                    drawY = drawY3;
                    drawY2 = drawHeight3;
                    drawWidth2 = drawWidth;
                } else {
                    if (widthRatio < heightRatio) {
                        drawWidth2 = (int) Math.ceil((double) (((float) inWidth2) * (widthRatio / heightRatio)));
                        if ((request.centerCropGravity & 3) == 3) {
                            drawX3 = 0;
                        } else if ((request.centerCropGravity & 5) == 5) {
                            drawX3 = inWidth2 - drawWidth2;
                        } else {
                            drawX3 = (inWidth2 - drawWidth2) / 2;
                        }
                        scaleX = ((float) targetWidth4) / ((float) drawWidth2);
                        scaleY = heightRatio;
                        drawX2 = drawX3;
                    } else {
                        scaleY = heightRatio;
                        scaleX = heightRatio;
                        drawX2 = 0;
                        drawWidth2 = inWidth2;
                    }
                    drawY2 = drawHeight;
                }
                float scaleX2 = scaleX;
                onlyScaleDown = onlyScaleDown2;
                if (shouldResize(onlyScaleDown, inWidth2, inHeight2, targetWidth4, targetHeight)) {
                    matrix.preScale(scaleX2, scaleY);
                }
                drawWidth = drawWidth2;
                drawHeight = drawY2;
            } else {
                int inHeight4 = inHeight;
                onlyScaleDown = onlyScaleDown2;
                int inWidth4 = inWidth;
                if (request.centerInside) {
                    if (targetWidth4 != 0) {
                        f5 = (float) targetWidth4;
                        f6 = (float) inWidth4;
                    } else {
                        f5 = (float) targetHeight;
                        f6 = (float) inHeight4;
                    }
                    float widthRatio2 = f5 / f6;
                    if (targetHeight != 0) {
                        f7 = (float) targetHeight;
                        f8 = (float) inHeight4;
                    } else {
                        f7 = (float) targetWidth4;
                        f8 = (float) inWidth4;
                    }
                    float heightRatio2 = f7 / f8;
                    float scale = widthRatio2 < heightRatio2 ? widthRatio2 : heightRatio2;
                    if (shouldResize(onlyScaleDown, inWidth4, inHeight4, targetWidth4, targetHeight)) {
                        matrix.preScale(scale, scale);
                    }
                } else if (!((targetWidth4 == 0 && targetHeight == 0) || (targetWidth4 == inWidth4 && targetHeight == inHeight4))) {
                    if (targetWidth4 != 0) {
                        f = (float) targetWidth4;
                        f2 = (float) inWidth4;
                    } else {
                        f = (float) targetHeight;
                        f2 = (float) inHeight4;
                    }
                    float sx = f / f2;
                    if (targetHeight != 0) {
                        f3 = (float) targetHeight;
                        f4 = (float) inHeight4;
                    } else {
                        f3 = (float) targetWidth4;
                        f4 = (float) inWidth4;
                    }
                    float sy = f3 / f4;
                    if (shouldResize(onlyScaleDown, inWidth4, inHeight4, targetWidth4, targetHeight)) {
                        matrix.preScale(sx, sy);
                    }
                }
            }
        } else {
            onlyScaleDown = onlyScaleDown3;
            drawX = 0;
            drawY = 0;
            drawWidth = drawWidth3;
            drawHeight = drawHeight2;
            int i6 = inWidth3;
            int drawX4 = inHeight3;
        }
        boolean z = onlyScaleDown;
        Bitmap newResult = Bitmap.createBitmap(result2, drawX, drawY, drawWidth, drawHeight, matrix, true);
        Bitmap result3 = result2;
        if (newResult == result3) {
            return result3;
        }
        result2.recycle();
        return newResult;
    }

    private static boolean shouldResize(boolean onlyScaleDown, int inWidth, int inHeight, int targetWidth, int targetHeight) {
        return !onlyScaleDown || (targetWidth != 0 && inWidth > targetWidth) || (targetHeight != 0 && inHeight > targetHeight);
    }

    static int getExifRotation(int orientation) {
        switch (orientation) {
            case 3:
            case 4:
                return 180;
            case 5:
            case 6:
                return 90;
            case 7:
            case 8:
                return 270;
            default:
                return 0;
        }
    }

    static int getExifTranslation(int orientation) {
        if (!(orientation == 2 || orientation == 7)) {
            switch (orientation) {
                case 4:
                case 5:
                    break;
                default:
                    return 1;
            }
        }
        return -1;
    }
}
