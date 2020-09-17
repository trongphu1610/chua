package com.bumptech.glide.request;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pools;
import android.util.Log;
import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.drawable.DrawableDecoderCompat;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.TransitionFactory;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Util;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.StateVerifier;
import java.util.List;

public final class SingleRequest<R> implements Request, SizeReadyCallback, ResourceCallback, FactoryPools.Poolable {
    private static final String GLIDE_TAG = "Glide";
    private static final boolean IS_VERBOSE_LOGGABLE = Log.isLoggable(TAG, 2);
    private static final Pools.Pool<SingleRequest<?>> POOL = FactoryPools.simple(150, new FactoryPools.Factory<SingleRequest<?>>() {
        public SingleRequest<?> create() {
            return new SingleRequest<>();
        }
    });
    private static final String TAG = "Request";
    private TransitionFactory<? super R> animationFactory;
    private Context context;
    private Engine engine;
    private Drawable errorDrawable;
    private Drawable fallbackDrawable;
    private GlideContext glideContext;
    private int height;
    private boolean isCallingCallbacks;
    private Engine.LoadStatus loadStatus;
    @Nullable
    private Object model;
    private int overrideHeight;
    private int overrideWidth;
    private Drawable placeholderDrawable;
    private Priority priority;
    private RequestCoordinator requestCoordinator;
    @Nullable
    private List<RequestListener<R>> requestListeners;
    private RequestOptions requestOptions;
    private Resource<R> resource;
    private long startTime;
    private final StateVerifier stateVerifier;
    private Status status;
    @Nullable
    private final String tag;
    private Target<R> target;
    @Nullable
    private RequestListener<R> targetListener;
    private Class<R> transcodeClass;
    private int width;

    private enum Status {
        PENDING,
        RUNNING,
        WAITING_FOR_SIZE,
        COMPLETE,
        FAILED,
        CLEARED
    }

    public static <R> SingleRequest<R> obtain(Context context2, GlideContext glideContext2, Object model2, Class<R> transcodeClass2, RequestOptions requestOptions2, int overrideWidth2, int overrideHeight2, Priority priority2, Target<R> target2, RequestListener<R> targetListener2, @Nullable List<RequestListener<R>> requestListeners2, RequestCoordinator requestCoordinator2, Engine engine2, TransitionFactory<? super R> animationFactory2) {
        SingleRequest<R> request = POOL.acquire();
        if (request == null) {
            request = new SingleRequest<>();
        }
        request.init(context2, glideContext2, model2, transcodeClass2, requestOptions2, overrideWidth2, overrideHeight2, priority2, target2, targetListener2, requestListeners2, requestCoordinator2, engine2, animationFactory2);
        return request;
    }

    SingleRequest() {
        this.tag = IS_VERBOSE_LOGGABLE ? String.valueOf(super.hashCode()) : null;
        this.stateVerifier = StateVerifier.newInstance();
    }

    private void init(Context context2, GlideContext glideContext2, Object model2, Class<R> transcodeClass2, RequestOptions requestOptions2, int overrideWidth2, int overrideHeight2, Priority priority2, Target<R> target2, RequestListener<R> targetListener2, @Nullable List<RequestListener<R>> requestListeners2, RequestCoordinator requestCoordinator2, Engine engine2, TransitionFactory<? super R> animationFactory2) {
        this.context = context2;
        this.glideContext = glideContext2;
        this.model = model2;
        this.transcodeClass = transcodeClass2;
        this.requestOptions = requestOptions2;
        this.overrideWidth = overrideWidth2;
        this.overrideHeight = overrideHeight2;
        this.priority = priority2;
        this.target = target2;
        this.targetListener = targetListener2;
        this.requestListeners = requestListeners2;
        this.requestCoordinator = requestCoordinator2;
        this.engine = engine2;
        this.animationFactory = animationFactory2;
        this.status = Status.PENDING;
    }

    @NonNull
    public StateVerifier getVerifier() {
        return this.stateVerifier;
    }

    public void recycle() {
        assertNotCallingCallbacks();
        this.context = null;
        this.glideContext = null;
        this.model = null;
        this.transcodeClass = null;
        this.requestOptions = null;
        this.overrideWidth = -1;
        this.overrideHeight = -1;
        this.target = null;
        this.requestListeners = null;
        this.targetListener = null;
        this.requestCoordinator = null;
        this.animationFactory = null;
        this.loadStatus = null;
        this.errorDrawable = null;
        this.placeholderDrawable = null;
        this.fallbackDrawable = null;
        this.width = -1;
        this.height = -1;
        POOL.release(this);
    }

    public void begin() {
        assertNotCallingCallbacks();
        this.stateVerifier.throwIfRecycled();
        this.startTime = LogTime.getLogTime();
        if (this.model == null) {
            if (Util.isValidDimensions(this.overrideWidth, this.overrideHeight)) {
                this.width = this.overrideWidth;
                this.height = this.overrideHeight;
            }
            onLoadFailed(new GlideException("Received null model"), getFallbackDrawable() == null ? 5 : 3);
        } else if (this.status == Status.RUNNING) {
            throw new IllegalArgumentException("Cannot restart a running request");
        } else if (this.status == Status.COMPLETE) {
            onResourceReady(this.resource, DataSource.MEMORY_CACHE);
        } else {
            this.status = Status.WAITING_FOR_SIZE;
            if (Util.isValidDimensions(this.overrideWidth, this.overrideHeight)) {
                onSizeReady(this.overrideWidth, this.overrideHeight);
            } else {
                this.target.getSize(this);
            }
            if ((this.status == Status.RUNNING || this.status == Status.WAITING_FOR_SIZE) && canNotifyStatusChanged()) {
                this.target.onLoadStarted(getPlaceholderDrawable());
            }
            if (IS_VERBOSE_LOGGABLE) {
                logV("finished run method in " + LogTime.getElapsedMillis(this.startTime));
            }
        }
    }

    private void cancel() {
        assertNotCallingCallbacks();
        this.stateVerifier.throwIfRecycled();
        this.target.removeCallback(this);
        if (this.loadStatus != null) {
            this.loadStatus.cancel();
            this.loadStatus = null;
        }
    }

    private void assertNotCallingCallbacks() {
        if (this.isCallingCallbacks) {
            throw new IllegalStateException("You can't start or clear loads in RequestListener or Target callbacks. If you're trying to start a fallback request when a load fails, use RequestBuilder#error(RequestBuilder). Otherwise consider posting your into() or clear() calls to the main thread using a Handler instead.");
        }
    }

    public void clear() {
        Util.assertMainThread();
        assertNotCallingCallbacks();
        this.stateVerifier.throwIfRecycled();
        if (this.status != Status.CLEARED) {
            cancel();
            if (this.resource != null) {
                releaseResource(this.resource);
            }
            if (canNotifyCleared()) {
                this.target.onLoadCleared(getPlaceholderDrawable());
            }
            this.status = Status.CLEARED;
        }
    }

    private void releaseResource(Resource<?> resource2) {
        this.engine.release(resource2);
        this.resource = null;
    }

    public boolean isRunning() {
        return this.status == Status.RUNNING || this.status == Status.WAITING_FOR_SIZE;
    }

    public boolean isComplete() {
        return this.status == Status.COMPLETE;
    }

    public boolean isResourceSet() {
        return isComplete();
    }

    public boolean isCleared() {
        return this.status == Status.CLEARED;
    }

    public boolean isFailed() {
        return this.status == Status.FAILED;
    }

    private Drawable getErrorDrawable() {
        if (this.errorDrawable == null) {
            this.errorDrawable = this.requestOptions.getErrorPlaceholder();
            if (this.errorDrawable == null && this.requestOptions.getErrorId() > 0) {
                this.errorDrawable = loadDrawable(this.requestOptions.getErrorId());
            }
        }
        return this.errorDrawable;
    }

    private Drawable getPlaceholderDrawable() {
        if (this.placeholderDrawable == null) {
            this.placeholderDrawable = this.requestOptions.getPlaceholderDrawable();
            if (this.placeholderDrawable == null && this.requestOptions.getPlaceholderId() > 0) {
                this.placeholderDrawable = loadDrawable(this.requestOptions.getPlaceholderId());
            }
        }
        return this.placeholderDrawable;
    }

    private Drawable getFallbackDrawable() {
        if (this.fallbackDrawable == null) {
            this.fallbackDrawable = this.requestOptions.getFallbackDrawable();
            if (this.fallbackDrawable == null && this.requestOptions.getFallbackId() > 0) {
                this.fallbackDrawable = loadDrawable(this.requestOptions.getFallbackId());
            }
        }
        return this.fallbackDrawable;
    }

    private Drawable loadDrawable(@DrawableRes int resourceId) {
        return DrawableDecoderCompat.getDrawable((Context) this.glideContext, resourceId, this.requestOptions.getTheme() != null ? this.requestOptions.getTheme() : this.context.getTheme());
    }

    private void setErrorPlaceholder() {
        if (canNotifyStatusChanged()) {
            Drawable error = null;
            if (this.model == null) {
                error = getFallbackDrawable();
            }
            if (error == null) {
                error = getErrorDrawable();
            }
            if (error == null) {
                error = getPlaceholderDrawable();
            }
            this.target.onLoadFailed(error);
        }
    }

    public void onSizeReady(int width2, int height2) {
        this.stateVerifier.throwIfRecycled();
        if (IS_VERBOSE_LOGGABLE) {
            logV("Got onSizeReady in " + LogTime.getElapsedMillis(this.startTime));
        }
        if (this.status == Status.WAITING_FOR_SIZE) {
            this.status = Status.RUNNING;
            float sizeMultiplier = this.requestOptions.getSizeMultiplier();
            this.width = maybeApplySizeMultiplier(width2, sizeMultiplier);
            this.height = maybeApplySizeMultiplier(height2, sizeMultiplier);
            if (IS_VERBOSE_LOGGABLE) {
                logV("finished setup for calling load in " + LogTime.getElapsedMillis(this.startTime));
            }
            float f = sizeMultiplier;
            Engine.LoadStatus load = this.engine.load(this.glideContext, this.model, this.requestOptions.getSignature(), this.width, this.height, this.requestOptions.getResourceClass(), this.transcodeClass, this.priority, this.requestOptions.getDiskCacheStrategy(), this.requestOptions.getTransformations(), this.requestOptions.isTransformationRequired(), this.requestOptions.isScaleOnlyOrNoTransform(), this.requestOptions.getOptions(), this.requestOptions.isMemoryCacheable(), this.requestOptions.getUseUnlimitedSourceGeneratorsPool(), this.requestOptions.getUseAnimationPool(), this.requestOptions.getOnlyRetrieveFromCache(), this);
            this.loadStatus = load;
            if (this.status != Status.RUNNING) {
                this.loadStatus = null;
            }
            if (IS_VERBOSE_LOGGABLE) {
                logV("finished onSizeReady in " + LogTime.getElapsedMillis(this.startTime));
            }
        }
    }

    private static int maybeApplySizeMultiplier(int size, float sizeMultiplier) {
        return size == Integer.MIN_VALUE ? size : Math.round(((float) size) * sizeMultiplier);
    }

    private boolean canSetResource() {
        return this.requestCoordinator == null || this.requestCoordinator.canSetImage(this);
    }

    private boolean canNotifyCleared() {
        return this.requestCoordinator == null || this.requestCoordinator.canNotifyCleared(this);
    }

    private boolean canNotifyStatusChanged() {
        return this.requestCoordinator == null || this.requestCoordinator.canNotifyStatusChanged(this);
    }

    private boolean isFirstReadyResource() {
        return this.requestCoordinator == null || !this.requestCoordinator.isAnyResourceSet();
    }

    private void notifyLoadSuccess() {
        if (this.requestCoordinator != null) {
            this.requestCoordinator.onRequestSuccess(this);
        }
    }

    private void notifyLoadFailed() {
        if (this.requestCoordinator != null) {
            this.requestCoordinator.onRequestFailed(this);
        }
    }

    public void onResourceReady(Resource<?> resource2, DataSource dataSource) {
        this.stateVerifier.throwIfRecycled();
        this.loadStatus = null;
        if (resource2 == null) {
            onLoadFailed(new GlideException("Expected to receive a Resource<R> with an object of " + this.transcodeClass + " inside, but instead got null."));
            return;
        }
        Object received = resource2.get();
        if (received == null || !this.transcodeClass.isAssignableFrom(received.getClass())) {
            releaseResource(resource2);
            StringBuilder sb = new StringBuilder();
            sb.append("Expected to receive an object of ");
            sb.append(this.transcodeClass);
            sb.append(" but instead got ");
            sb.append(received != null ? received.getClass() : "");
            sb.append("{");
            sb.append(received);
            sb.append("} inside Resource{");
            sb.append(resource2);
            sb.append("}.");
            sb.append(received != null ? "" : " To indicate failure return a null Resource object, rather than a Resource object containing null data.");
            onLoadFailed(new GlideException(sb.toString()));
        } else if (!canSetResource()) {
            releaseResource(resource2);
            this.status = Status.COMPLETE;
        } else {
            onResourceReady(resource2, received, dataSource);
        }
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00ad A[Catch:{ all -> 0x00bf }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onResourceReady(com.bumptech.glide.load.engine.Resource<R> r12, R r13, com.bumptech.glide.load.DataSource r14) {
        /*
            r11 = this;
            boolean r6 = r11.isFirstReadyResource()
            com.bumptech.glide.request.SingleRequest$Status r0 = com.bumptech.glide.request.SingleRequest.Status.COMPLETE
            r11.status = r0
            r11.resource = r12
            com.bumptech.glide.GlideContext r0 = r11.glideContext
            int r0 = r0.getLogLevel()
            r1 = 3
            if (r0 > r1) goto L_0x006a
            java.lang.String r0 = "Glide"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Finished loading "
            r1.append(r2)
            java.lang.Class r2 = r13.getClass()
            java.lang.String r2 = r2.getSimpleName()
            r1.append(r2)
            java.lang.String r2 = " from "
            r1.append(r2)
            r1.append(r14)
            java.lang.String r2 = " for "
            r1.append(r2)
            java.lang.Object r2 = r11.model
            r1.append(r2)
            java.lang.String r2 = " with size ["
            r1.append(r2)
            int r2 = r11.width
            r1.append(r2)
            java.lang.String r2 = "x"
            r1.append(r2)
            int r2 = r11.height
            r1.append(r2)
            java.lang.String r2 = "] in "
            r1.append(r2)
            long r2 = r11.startTime
            double r2 = com.bumptech.glide.util.LogTime.getElapsedMillis(r2)
            r1.append(r2)
            java.lang.String r2 = " ms"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r0, r1)
        L_0x006a:
            r7 = 1
            r11.isCallingCallbacks = r7
            r0 = 0
            r8 = 0
            java.util.List<com.bumptech.glide.request.RequestListener<R>> r1 = r11.requestListeners     // Catch:{ all -> 0x00bf }
            if (r1 == 0) goto L_0x0093
            java.util.List<com.bumptech.glide.request.RequestListener<R>> r1 = r11.requestListeners     // Catch:{ all -> 0x00bf }
            java.util.Iterator r9 = r1.iterator()     // Catch:{ all -> 0x00bf }
            r10 = r0
        L_0x007a:
            boolean r0 = r9.hasNext()     // Catch:{ all -> 0x00bf }
            if (r0 == 0) goto L_0x0094
            java.lang.Object r0 = r9.next()     // Catch:{ all -> 0x00bf }
            com.bumptech.glide.request.RequestListener r0 = (com.bumptech.glide.request.RequestListener) r0     // Catch:{ all -> 0x00bf }
            java.lang.Object r2 = r11.model     // Catch:{ all -> 0x00bf }
            com.bumptech.glide.request.target.Target<R> r3 = r11.target     // Catch:{ all -> 0x00bf }
            r1 = r13
            r4 = r14
            r5 = r6
            boolean r1 = r0.onResourceReady(r1, r2, r3, r4, r5)     // Catch:{ all -> 0x00bf }
            r10 = r10 | r1
            goto L_0x007a
        L_0x0093:
            r10 = r0
        L_0x0094:
            com.bumptech.glide.request.RequestListener<R> r0 = r11.targetListener     // Catch:{ all -> 0x00bf }
            if (r0 == 0) goto L_0x00a8
            com.bumptech.glide.request.RequestListener<R> r0 = r11.targetListener     // Catch:{ all -> 0x00bf }
            java.lang.Object r2 = r11.model     // Catch:{ all -> 0x00bf }
            com.bumptech.glide.request.target.Target<R> r3 = r11.target     // Catch:{ all -> 0x00bf }
            r1 = r13
            r4 = r14
            r5 = r6
            boolean r0 = r0.onResourceReady(r1, r2, r3, r4, r5)     // Catch:{ all -> 0x00bf }
            if (r0 == 0) goto L_0x00a8
            goto L_0x00a9
        L_0x00a8:
            r7 = 0
        L_0x00a9:
            r0 = r10 | r7
            if (r0 != 0) goto L_0x00b8
            com.bumptech.glide.request.transition.TransitionFactory<? super R> r1 = r11.animationFactory     // Catch:{ all -> 0x00bf }
            com.bumptech.glide.request.transition.Transition r1 = r1.build(r14, r6)     // Catch:{ all -> 0x00bf }
            com.bumptech.glide.request.target.Target<R> r2 = r11.target     // Catch:{ all -> 0x00bf }
            r2.onResourceReady(r13, r1)     // Catch:{ all -> 0x00bf }
        L_0x00b8:
            r11.isCallingCallbacks = r8
            r11.notifyLoadSuccess()
            return
        L_0x00bf:
            r0 = move-exception
            r11.isCallingCallbacks = r8
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.request.SingleRequest.onResourceReady(com.bumptech.glide.load.engine.Resource, java.lang.Object, com.bumptech.glide.load.DataSource):void");
    }

    public void onLoadFailed(GlideException e) {
        onLoadFailed(e, 5);
    }

    /* JADX INFO: finally extract failed */
    private void onLoadFailed(GlideException e, int maxLogLevel) {
        this.stateVerifier.throwIfRecycled();
        int logLevel = this.glideContext.getLogLevel();
        if (logLevel <= maxLogLevel) {
            Log.w(GLIDE_TAG, "Load failed for " + this.model + " with size [" + this.width + "x" + this.height + "]", e);
            if (logLevel <= 4) {
                e.logRootCauses(GLIDE_TAG);
            }
        }
        this.loadStatus = null;
        this.status = Status.FAILED;
        boolean z = true;
        this.isCallingCallbacks = true;
        boolean anyListenerHandledUpdatingTarget = false;
        try {
            if (this.requestListeners != null) {
                for (RequestListener<R> listener : this.requestListeners) {
                    anyListenerHandledUpdatingTarget |= listener.onLoadFailed(e, this.model, this.target, isFirstReadyResource());
                }
            }
            if (this.targetListener == null || !this.targetListener.onLoadFailed(e, this.model, this.target, isFirstReadyResource())) {
                z = false;
            }
            if (!z && !anyListenerHandledUpdatingTarget) {
                setErrorPlaceholder();
            }
            this.isCallingCallbacks = false;
            notifyLoadFailed();
        } catch (Throwable th) {
            this.isCallingCallbacks = false;
            throw th;
        }
    }

    public boolean isEquivalentTo(Request o) {
        if (!(o instanceof SingleRequest)) {
            return false;
        }
        SingleRequest<?> that = (SingleRequest) o;
        if (this.overrideWidth != that.overrideWidth || this.overrideHeight != that.overrideHeight || !Util.bothModelsNullEquivalentOrEquals(this.model, that.model) || !this.transcodeClass.equals(that.transcodeClass) || !this.requestOptions.equals(that.requestOptions) || this.priority != that.priority || !listenerCountEquals(this, that)) {
            return false;
        }
        return true;
    }

    private static boolean listenerCountEquals(SingleRequest<?> first, SingleRequest<?> second) {
        if ((first.requestListeners == null ? 0 : first.requestListeners.size()) == (second.requestListeners == null ? 0 : second.requestListeners.size())) {
            return true;
        }
        return false;
    }

    private void logV(String message) {
        Log.v(TAG, message + " this: " + this.tag);
    }
}
