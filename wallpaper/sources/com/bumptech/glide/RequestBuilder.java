package com.bumptech.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.widget.ImageView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.ErrorRequestCoordinator;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestCoordinator;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.SingleRequest;
import com.bumptech.glide.request.ThumbnailRequestCoordinator;
import com.bumptech.glide.request.target.PreloadTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.signature.ApplicationVersionSignature;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RequestBuilder<TranscodeType> implements Cloneable, ModelTypes<RequestBuilder<TranscodeType>> {
    protected static final RequestOptions DOWNLOAD_ONLY_OPTIONS = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).priority(Priority.LOW).skipMemoryCache(true);
    private final Context context;
    private final RequestOptions defaultRequestOptions;
    @Nullable
    private RequestBuilder<TranscodeType> errorBuilder;
    private final Glide glide;
    private final GlideContext glideContext;
    private boolean isDefaultTransitionOptionsSet;
    private boolean isModelSet;
    private boolean isThumbnailBuilt;
    @Nullable
    private Object model;
    @Nullable
    private List<RequestListener<TranscodeType>> requestListeners;
    private final RequestManager requestManager;
    @NonNull
    protected RequestOptions requestOptions;
    @Nullable
    private Float thumbSizeMultiplier;
    @Nullable
    private RequestBuilder<TranscodeType> thumbnailBuilder;
    private final Class<TranscodeType> transcodeClass;
    @NonNull
    private TransitionOptions<?, ? super TranscodeType> transitionOptions;

    protected RequestBuilder(Glide glide2, RequestManager requestManager2, Class<TranscodeType> transcodeClass2, Context context2) {
        this.isDefaultTransitionOptionsSet = true;
        this.glide = glide2;
        this.requestManager = requestManager2;
        this.transcodeClass = transcodeClass2;
        this.defaultRequestOptions = requestManager2.getDefaultRequestOptions();
        this.context = context2;
        this.transitionOptions = requestManager2.getDefaultTransitionOptions(transcodeClass2);
        this.requestOptions = this.defaultRequestOptions;
        this.glideContext = glide2.getGlideContext();
    }

    protected RequestBuilder(Class<TranscodeType> transcodeClass2, RequestBuilder<?> other) {
        this(other.glide, other.requestManager, transcodeClass2, other.context);
        this.model = other.model;
        this.isModelSet = other.isModelSet;
        this.requestOptions = other.requestOptions;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> apply(@NonNull RequestOptions requestOptions2) {
        Preconditions.checkNotNull(requestOptions2);
        this.requestOptions = getMutableOptions().apply(requestOptions2);
        return this;
    }

    /* access modifiers changed from: protected */
    @NonNull
    public RequestOptions getMutableOptions() {
        return this.defaultRequestOptions == this.requestOptions ? this.requestOptions.clone() : this.requestOptions;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> transition(@NonNull TransitionOptions<?, ? super TranscodeType> transitionOptions2) {
        this.transitionOptions = (TransitionOptions) Preconditions.checkNotNull(transitionOptions2);
        this.isDefaultTransitionOptionsSet = false;
        return this;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> listener(@Nullable RequestListener<TranscodeType> requestListener) {
        this.requestListeners = null;
        return addListener(requestListener);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> addListener(@Nullable RequestListener<TranscodeType> requestListener) {
        if (requestListener != null) {
            if (this.requestListeners == null) {
                this.requestListeners = new ArrayList();
            }
            this.requestListeners.add(requestListener);
        }
        return this;
    }

    @NonNull
    public RequestBuilder<TranscodeType> error(@Nullable RequestBuilder<TranscodeType> errorBuilder2) {
        this.errorBuilder = errorBuilder2;
        return this;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> thumbnail(@Nullable RequestBuilder<TranscodeType> thumbnailRequest) {
        this.thumbnailBuilder = thumbnailRequest;
        return this;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> thumbnail(@Nullable RequestBuilder<TranscodeType>... thumbnails) {
        if (thumbnails == null || thumbnails.length == 0) {
            return thumbnail((RequestBuilder) null);
        }
        RequestBuilder<TranscodeType> previous = null;
        for (int i = thumbnails.length - 1; i >= 0; i--) {
            RequestBuilder<TranscodeType> current = thumbnails[i];
            if (current != null) {
                if (previous == null) {
                    previous = current;
                } else {
                    previous = current.thumbnail(previous);
                }
            }
        }
        return thumbnail(previous);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> thumbnail(float sizeMultiplier) {
        if (sizeMultiplier < 0.0f || sizeMultiplier > 1.0f) {
            throw new IllegalArgumentException("sizeMultiplier must be between 0 and 1");
        }
        this.thumbSizeMultiplier = Float.valueOf(sizeMultiplier);
        return this;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable Object model2) {
        return loadGeneric(model2);
    }

    @NonNull
    private RequestBuilder<TranscodeType> loadGeneric(@Nullable Object model2) {
        this.model = model2;
        this.isModelSet = true;
        return this;
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable Bitmap bitmap) {
        return loadGeneric(bitmap).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable Drawable drawable) {
        return loadGeneric(drawable).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable String string) {
        return loadGeneric(string);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable Uri uri) {
        return loadGeneric(uri);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable File file) {
        return loadGeneric(file);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable @RawRes @DrawableRes Integer resourceId) {
        return loadGeneric(resourceId).apply(RequestOptions.signatureOf(ApplicationVersionSignature.obtain(this.context)));
    }

    @Deprecated
    @CheckResult
    public RequestBuilder<TranscodeType> load(@Nullable URL url) {
        return loadGeneric(url);
    }

    @CheckResult
    @NonNull
    public RequestBuilder<TranscodeType> load(@Nullable byte[] model2) {
        RequestBuilder<TranscodeType> result = loadGeneric(model2);
        if (!result.requestOptions.isDiskCacheStrategySet()) {
            result = result.apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
        }
        if (!result.requestOptions.isSkipMemoryCacheSet()) {
            return result.apply(RequestOptions.skipMemoryCacheOf(true));
        }
        return result;
    }

    @CheckResult
    public RequestBuilder<TranscodeType> clone() {
        try {
            RequestBuilder<TranscodeType> result = (RequestBuilder) super.clone();
            result.requestOptions = result.requestOptions.clone();
            result.transitionOptions = result.transitionOptions.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public <Y extends Target<TranscodeType>> Y into(@NonNull Y target) {
        return into(target, (RequestListener) null);
    }

    /* access modifiers changed from: package-private */
    @NonNull
    public <Y extends Target<TranscodeType>> Y into(@NonNull Y target, @Nullable RequestListener<TranscodeType> targetListener) {
        return into(target, targetListener, getMutableOptions());
    }

    private <Y extends Target<TranscodeType>> Y into(@NonNull Y target, @Nullable RequestListener<TranscodeType> targetListener, @NonNull RequestOptions options) {
        Util.assertMainThread();
        Preconditions.checkNotNull(target);
        if (!this.isModelSet) {
            throw new IllegalArgumentException("You must call #load() before calling #into()");
        }
        RequestOptions options2 = options.autoClone();
        Request request = buildRequest(target, targetListener, options2);
        Request previous = target.getRequest();
        if (!request.isEquivalentTo(previous) || isSkipMemoryCacheWithCompletePreviousRequest(options2, previous)) {
            this.requestManager.clear((Target<?>) target);
            target.setRequest(request);
            this.requestManager.track(target, request);
            return target;
        }
        request.recycle();
        if (!((Request) Preconditions.checkNotNull(previous)).isRunning()) {
            previous.begin();
        }
        return target;
    }

    private boolean isSkipMemoryCacheWithCompletePreviousRequest(RequestOptions options, Request previous) {
        return !options.isMemoryCacheable() && previous.isComplete();
    }

    @NonNull
    public ViewTarget<ImageView, TranscodeType> into(@NonNull ImageView view) {
        Util.assertMainThread();
        Preconditions.checkNotNull(view);
        RequestOptions requestOptions2 = this.requestOptions;
        if (!requestOptions2.isTransformationSet() && requestOptions2.isTransformationAllowed() && view.getScaleType() != null) {
            switch (AnonymousClass2.$SwitchMap$android$widget$ImageView$ScaleType[view.getScaleType().ordinal()]) {
                case 1:
                    requestOptions2 = requestOptions2.clone().optionalCenterCrop();
                    break;
                case 2:
                    requestOptions2 = requestOptions2.clone().optionalCenterInside();
                    break;
                case 3:
                case 4:
                case 5:
                    requestOptions2 = requestOptions2.clone().optionalFitCenter();
                    break;
                case 6:
                    requestOptions2 = requestOptions2.clone().optionalCenterInside();
                    break;
            }
        }
        return (ViewTarget) into(this.glideContext.buildImageViewTarget(view, this.transcodeClass), (RequestListener) null, requestOptions2);
    }

    @Deprecated
    public FutureTarget<TranscodeType> into(int width, int height) {
        return submit(width, height);
    }

    @NonNull
    public FutureTarget<TranscodeType> submit() {
        return submit(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    @NonNull
    public FutureTarget<TranscodeType> submit(int width, int height) {
        final RequestFutureTarget<TranscodeType> target = new RequestFutureTarget<>(this.glideContext.getMainHandler(), width, height);
        if (Util.isOnBackgroundThread()) {
            this.glideContext.getMainHandler().post(new Runnable() {
                public void run() {
                    if (!target.isCancelled()) {
                        RequestBuilder.this.into(target, target);
                    }
                }
            });
        } else {
            into(target, target);
        }
        return target;
    }

    @NonNull
    public Target<TranscodeType> preload(int width, int height) {
        return into(PreloadTarget.obtain(this.requestManager, width, height));
    }

    @NonNull
    public Target<TranscodeType> preload() {
        return preload(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    @Deprecated
    @CheckResult
    public <Y extends Target<File>> Y downloadOnly(@NonNull Y target) {
        return getDownloadOnlyRequest().into(target);
    }

    @Deprecated
    @CheckResult
    public FutureTarget<File> downloadOnly(int width, int height) {
        return getDownloadOnlyRequest().submit(width, height);
    }

    /* access modifiers changed from: protected */
    @CheckResult
    @NonNull
    public RequestBuilder<File> getDownloadOnlyRequest() {
        return new RequestBuilder(File.class, this).apply(DOWNLOAD_ONLY_OPTIONS);
    }

    /* renamed from: com.bumptech.glide.RequestBuilder$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType = new int[ImageView.ScaleType.values().length];

        static {
            $SwitchMap$com$bumptech$glide$Priority = new int[Priority.values().length];
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.LOW.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.NORMAL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.HIGH.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.IMMEDIATE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER_CROP.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER_INSIDE.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_CENTER.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_START.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_END.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_XY.ordinal()] = 6;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER.ordinal()] = 7;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.MATRIX.ordinal()] = 8;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    @NonNull
    private Priority getThumbnailPriority(@NonNull Priority current) {
        switch (current) {
            case LOW:
                return Priority.NORMAL;
            case NORMAL:
                return Priority.HIGH;
            case HIGH:
            case IMMEDIATE:
                return Priority.IMMEDIATE;
            default:
                throw new IllegalArgumentException("unknown priority: " + this.requestOptions.getPriority());
        }
    }

    private Request buildRequest(Target<TranscodeType> target, @Nullable RequestListener<TranscodeType> targetListener, RequestOptions requestOptions2) {
        return buildRequestRecursive(target, targetListener, (RequestCoordinator) null, this.transitionOptions, requestOptions2.getPriority(), requestOptions2.getOverrideWidth(), requestOptions2.getOverrideHeight(), requestOptions2);
    }

    private Request buildRequestRecursive(Target<TranscodeType> target, @Nullable RequestListener<TranscodeType> targetListener, @Nullable RequestCoordinator parentCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions2, Priority priority, int overrideWidth, int overrideHeight, RequestOptions requestOptions2) {
        ErrorRequestCoordinator errorRequestCoordinator;
        RequestCoordinator parentCoordinator2;
        if (this.errorBuilder != null) {
            RequestCoordinator errorRequestCoordinator2 = new ErrorRequestCoordinator(parentCoordinator);
            errorRequestCoordinator = errorRequestCoordinator2;
            parentCoordinator2 = errorRequestCoordinator2;
        } else {
            errorRequestCoordinator = null;
            parentCoordinator2 = parentCoordinator;
        }
        Request mainRequest = buildThumbnailRequestRecursive(target, targetListener, parentCoordinator2, transitionOptions2, priority, overrideWidth, overrideHeight, requestOptions2);
        if (errorRequestCoordinator == null) {
            return mainRequest;
        }
        int errorOverrideWidth = this.errorBuilder.requestOptions.getOverrideWidth();
        int errorOverrideHeight = this.errorBuilder.requestOptions.getOverrideHeight();
        if (Util.isValidDimensions(overrideWidth, overrideHeight) && !this.errorBuilder.requestOptions.isValidOverride()) {
            errorOverrideWidth = requestOptions2.getOverrideWidth();
            errorOverrideHeight = requestOptions2.getOverrideHeight();
        }
        errorRequestCoordinator.setRequests(mainRequest, this.errorBuilder.buildRequestRecursive(target, targetListener, errorRequestCoordinator, this.errorBuilder.transitionOptions, this.errorBuilder.requestOptions.getPriority(), errorOverrideWidth, errorOverrideHeight, this.errorBuilder.requestOptions));
        return errorRequestCoordinator;
    }

    private Request buildThumbnailRequestRecursive(Target<TranscodeType> target, RequestListener<TranscodeType> targetListener, @Nullable RequestCoordinator parentCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions2, Priority priority, int overrideWidth, int overrideHeight, RequestOptions requestOptions2) {
        RequestCoordinator requestCoordinator = parentCoordinator;
        Priority priority2 = priority;
        if (this.thumbnailBuilder != null) {
            if (this.isThumbnailBuilt) {
                throw new IllegalStateException("You cannot use a request as both the main request and a thumbnail, consider using clone() on the request(s) passed to thumbnail()");
            }
            TransitionOptions<?, ? super TranscodeType> thumbTransitionOptions = this.thumbnailBuilder.transitionOptions;
            if (this.thumbnailBuilder.isDefaultTransitionOptionsSet) {
                thumbTransitionOptions = transitionOptions2;
            }
            TransitionOptions<?, ? super TranscodeType> thumbTransitionOptions2 = thumbTransitionOptions;
            Priority thumbPriority = this.thumbnailBuilder.requestOptions.isPrioritySet() ? this.thumbnailBuilder.requestOptions.getPriority() : getThumbnailPriority(priority2);
            int thumbOverrideWidth = this.thumbnailBuilder.requestOptions.getOverrideWidth();
            int thumbOverrideHeight = this.thumbnailBuilder.requestOptions.getOverrideHeight();
            if (Util.isValidDimensions(overrideWidth, overrideHeight) && !this.thumbnailBuilder.requestOptions.isValidOverride()) {
                thumbOverrideWidth = requestOptions2.getOverrideWidth();
                thumbOverrideHeight = requestOptions2.getOverrideHeight();
            }
            ThumbnailRequestCoordinator coordinator = new ThumbnailRequestCoordinator(requestCoordinator);
            Request fullRequest = obtainRequest(target, targetListener, requestOptions2, coordinator, transitionOptions2, priority2, overrideWidth, overrideHeight);
            this.isThumbnailBuilt = true;
            ThumbnailRequestCoordinator coordinator2 = coordinator;
            Request thumbRequest = this.thumbnailBuilder.buildRequestRecursive(target, targetListener, coordinator, thumbTransitionOptions2, thumbPriority, thumbOverrideWidth, thumbOverrideHeight, this.thumbnailBuilder.requestOptions);
            this.isThumbnailBuilt = false;
            coordinator2.setRequests(fullRequest, thumbRequest);
            return coordinator2;
        } else if (this.thumbSizeMultiplier == null) {
            return obtainRequest(target, targetListener, requestOptions2, requestCoordinator, transitionOptions2, priority2, overrideWidth, overrideHeight);
        } else {
            ThumbnailRequestCoordinator coordinator3 = new ThumbnailRequestCoordinator(requestCoordinator);
            RequestListener<TranscodeType> requestListener = targetListener;
            ThumbnailRequestCoordinator thumbnailRequestCoordinator = coordinator3;
            TransitionOptions<?, ? super TranscodeType> transitionOptions3 = transitionOptions2;
            int i = overrideWidth;
            int i2 = overrideHeight;
            coordinator3.setRequests(obtainRequest(target, requestListener, requestOptions2, thumbnailRequestCoordinator, transitionOptions3, priority2, i, i2), obtainRequest(target, requestListener, requestOptions2.clone().sizeMultiplier(this.thumbSizeMultiplier.floatValue()), thumbnailRequestCoordinator, transitionOptions3, getThumbnailPriority(priority2), i, i2));
            return coordinator3;
        }
    }

    private Request obtainRequest(Target<TranscodeType> target, RequestListener<TranscodeType> targetListener, RequestOptions requestOptions2, RequestCoordinator requestCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions2, Priority priority, int overrideWidth, int overrideHeight) {
        return SingleRequest.obtain(this.context, this.glideContext, this.model, this.transcodeClass, requestOptions2, overrideWidth, overrideHeight, priority, target, targetListener, this.requestListeners, requestCoordinator, this.glideContext.getEngine(), transitionOptions2.getTransitionFactory());
    }
}
