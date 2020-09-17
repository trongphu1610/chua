package com.bumptech.glide.load.resource.bitmap;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class VideoDecoder<T> implements ResourceDecoder<T, Bitmap> {
    private static final MediaMetadataRetrieverFactory DEFAULT_FACTORY = new MediaMetadataRetrieverFactory();
    public static final long DEFAULT_FRAME = -1;
    @VisibleForTesting
    static final int DEFAULT_FRAME_OPTION = 2;
    public static final Option<Integer> FRAME_OPTION = Option.disk("com.bumptech.glide.load.resource.bitmap.VideoBitmapDecode.FrameOption", 2, new Option.CacheKeyUpdater<Integer>() {
        private final ByteBuffer buffer = ByteBuffer.allocate(4);

        public void update(@NonNull byte[] keyBytes, @NonNull Integer value, @NonNull MessageDigest messageDigest) {
            if (value != null) {
                messageDigest.update(keyBytes);
                synchronized (this.buffer) {
                    this.buffer.position(0);
                    messageDigest.update(this.buffer.putInt(value.intValue()).array());
                }
            }
        }
    });
    private static final String TAG = "VideoDecoder";
    public static final Option<Long> TARGET_FRAME = Option.disk("com.bumptech.glide.load.resource.bitmap.VideoBitmapDecode.TargetFrame", -1L, new Option.CacheKeyUpdater<Long>() {
        private final ByteBuffer buffer = ByteBuffer.allocate(8);

        public void update(@NonNull byte[] keyBytes, @NonNull Long value, @NonNull MessageDigest messageDigest) {
            messageDigest.update(keyBytes);
            synchronized (this.buffer) {
                this.buffer.position(0);
                messageDigest.update(this.buffer.putLong(value.longValue()).array());
            }
        }
    });
    private final BitmapPool bitmapPool;
    private final MediaMetadataRetrieverFactory factory;
    private final MediaMetadataRetrieverInitializer<T> initializer;

    @VisibleForTesting
    interface MediaMetadataRetrieverInitializer<T> {
        void initialize(MediaMetadataRetriever mediaMetadataRetriever, T t);
    }

    public static ResourceDecoder<AssetFileDescriptor, Bitmap> asset(BitmapPool bitmapPool2) {
        return new VideoDecoder(bitmapPool2, new AssetFileDescriptorInitializer());
    }

    public static ResourceDecoder<ParcelFileDescriptor, Bitmap> parcel(BitmapPool bitmapPool2) {
        return new VideoDecoder(bitmapPool2, new ParcelFileDescriptorInitializer());
    }

    VideoDecoder(BitmapPool bitmapPool2, MediaMetadataRetrieverInitializer<T> initializer2) {
        this(bitmapPool2, initializer2, DEFAULT_FACTORY);
    }

    @VisibleForTesting
    VideoDecoder(BitmapPool bitmapPool2, MediaMetadataRetrieverInitializer<T> initializer2, MediaMetadataRetrieverFactory factory2) {
        this.bitmapPool = bitmapPool2;
        this.initializer = initializer2;
        this.factory = factory2;
    }

    public boolean handles(@NonNull T t, @NonNull Options options) {
        return true;
    }

    public Resource<Bitmap> decode(@NonNull T resource, int outWidth, int outHeight, @NonNull Options options) throws IOException {
        Options options2 = options;
        long frameTimeMicros = ((Long) options2.get(TARGET_FRAME)).longValue();
        if (frameTimeMicros >= 0 || frameTimeMicros == -1) {
            Integer frameOption = (Integer) options2.get(FRAME_OPTION);
            if (frameOption == null) {
                frameOption = 2;
            }
            DownsampleStrategy downsampleStrategy = (DownsampleStrategy) options2.get(DownsampleStrategy.OPTION);
            if (downsampleStrategy == null) {
                downsampleStrategy = DownsampleStrategy.DEFAULT;
            }
            DownsampleStrategy downsampleStrategy2 = downsampleStrategy;
            MediaMetadataRetriever mediaMetadataRetriever = this.factory.build();
            try {
                try {
                    this.initializer.initialize(mediaMetadataRetriever, resource);
                    Bitmap result = decodeFrame(mediaMetadataRetriever, frameTimeMicros, frameOption.intValue(), outWidth, outHeight, downsampleStrategy2);
                    mediaMetadataRetriever.release();
                    return BitmapResource.obtain(result, this.bitmapPool);
                } catch (RuntimeException e) {
                    e = e;
                    try {
                        throw new IOException(e);
                    } catch (Throwable th) {
                        th = th;
                        Throwable th2 = th;
                        mediaMetadataRetriever.release();
                        throw th2;
                    }
                }
            } catch (RuntimeException e2) {
                e = e2;
                T t = resource;
                throw new IOException(e);
            } catch (Throwable th3) {
                th = th3;
                T t2 = resource;
                Throwable th22 = th;
                mediaMetadataRetriever.release();
                throw th22;
            }
        } else {
            throw new IllegalArgumentException("Requested frame must be non-negative, or DEFAULT_FRAME, given: " + frameTimeMicros);
        }
    }

    @Nullable
    private static Bitmap decodeFrame(MediaMetadataRetriever mediaMetadataRetriever, long frameTimeMicros, int frameOption, int outWidth, int outHeight, DownsampleStrategy strategy) {
        Bitmap result = null;
        if (!(Build.VERSION.SDK_INT < 27 || outWidth == Integer.MIN_VALUE || outHeight == Integer.MIN_VALUE || strategy == DownsampleStrategy.NONE)) {
            result = decodeScaledFrame(mediaMetadataRetriever, frameTimeMicros, frameOption, outWidth, outHeight, strategy);
        }
        if (result == null) {
            return decodeOriginalFrame(mediaMetadataRetriever, frameTimeMicros, frameOption);
        }
        return result;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:20:? A[RETURN, SYNTHETIC] */
    @android.annotation.TargetApi(27)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.graphics.Bitmap decodeScaledFrame(android.media.MediaMetadataRetriever r15, long r16, int r18, int r19, int r20, com.bumptech.glide.load.resource.bitmap.DownsampleStrategy r21) {
        /*
            r7 = r15
            r1 = 18
            java.lang.String r1 = r7.extractMetadata(r1)     // Catch:{ Throwable -> 0x0056 }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ Throwable -> 0x0056 }
            r2 = 19
            java.lang.String r2 = r7.extractMetadata(r2)     // Catch:{ Throwable -> 0x0056 }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ Throwable -> 0x0056 }
            r3 = 24
            java.lang.String r3 = r7.extractMetadata(r3)     // Catch:{ Throwable -> 0x0056 }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ Throwable -> 0x0056 }
            r8 = r3
            r3 = 90
            if (r8 == r3) goto L_0x002c
            r3 = 270(0x10e, float:3.78E-43)
            if (r8 != r3) goto L_0x0029
            goto L_0x002c
        L_0x0029:
            r9 = r1
            r10 = r2
            goto L_0x0030
        L_0x002c:
            r3 = r1
            r1 = r2
            r2 = r3
            goto L_0x0029
        L_0x0030:
            r11 = r19
            r12 = r20
            r13 = r21
            float r1 = r13.getScaleFactor(r9, r10, r11, r12)     // Catch:{ Throwable -> 0x0054 }
            r14 = r1
            float r1 = (float) r9     // Catch:{ Throwable -> 0x0054 }
            float r1 = r1 * r14
            int r5 = java.lang.Math.round(r1)     // Catch:{ Throwable -> 0x0054 }
            float r1 = (float) r10     // Catch:{ Throwable -> 0x0054 }
            float r1 = r1 * r14
            int r6 = java.lang.Math.round(r1)     // Catch:{ Throwable -> 0x0054 }
            r1 = r7
            r2 = r16
            r4 = r18
            android.graphics.Bitmap r1 = r1.getScaledFrameAtTime(r2, r4, r5, r6)     // Catch:{ Throwable -> 0x0054 }
            return r1
        L_0x0054:
            r0 = move-exception
            goto L_0x005d
        L_0x0056:
            r0 = move-exception
            r11 = r19
            r12 = r20
            r13 = r21
        L_0x005d:
            r1 = r0
            java.lang.String r2 = "VideoDecoder"
            r3 = 3
            boolean r2 = android.util.Log.isLoggable(r2, r3)
            if (r2 == 0) goto L_0x006e
            java.lang.String r2 = "VideoDecoder"
            java.lang.String r3 = "Exception trying to decode frame on oreo+"
            android.util.Log.d(r2, r3, r1)
        L_0x006e:
            r2 = 0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.bitmap.VideoDecoder.decodeScaledFrame(android.media.MediaMetadataRetriever, long, int, int, int, com.bumptech.glide.load.resource.bitmap.DownsampleStrategy):android.graphics.Bitmap");
    }

    private static Bitmap decodeOriginalFrame(MediaMetadataRetriever mediaMetadataRetriever, long frameTimeMicros, int frameOption) {
        return mediaMetadataRetriever.getFrameAtTime(frameTimeMicros, frameOption);
    }

    @VisibleForTesting
    static class MediaMetadataRetrieverFactory {
        MediaMetadataRetrieverFactory() {
        }

        public MediaMetadataRetriever build() {
            return new MediaMetadataRetriever();
        }
    }

    private static final class AssetFileDescriptorInitializer implements MediaMetadataRetrieverInitializer<AssetFileDescriptor> {
        private AssetFileDescriptorInitializer() {
        }

        public void initialize(MediaMetadataRetriever retriever, AssetFileDescriptor data) {
            retriever.setDataSource(data.getFileDescriptor(), data.getStartOffset(), data.getLength());
        }
    }

    static final class ParcelFileDescriptorInitializer implements MediaMetadataRetrieverInitializer<ParcelFileDescriptor> {
        ParcelFileDescriptorInitializer() {
        }

        public void initialize(MediaMetadataRetriever retriever, ParcelFileDescriptor data) {
            retriever.setDataSource(data.getFileDescriptor());
        }
    }
}
