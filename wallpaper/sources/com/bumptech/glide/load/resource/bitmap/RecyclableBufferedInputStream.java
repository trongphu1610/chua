package com.bumptech.glide.load.resource.bitmap;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RecyclableBufferedInputStream extends FilterInputStream {
    private volatile byte[] buf;
    private final ArrayPool byteArrayPool;
    private int count;
    private int marklimit;
    private int markpos;
    private int pos;

    public RecyclableBufferedInputStream(@NonNull InputStream in, @NonNull ArrayPool byteArrayPool2) {
        this(in, byteArrayPool2, 65536);
    }

    @VisibleForTesting
    RecyclableBufferedInputStream(@NonNull InputStream in, @NonNull ArrayPool byteArrayPool2, int bufferSize) {
        super(in);
        this.markpos = -1;
        this.byteArrayPool = byteArrayPool2;
        this.buf = (byte[]) byteArrayPool2.get(bufferSize, byte[].class);
    }

    public synchronized int available() throws IOException {
        InputStream localIn;
        localIn = this.in;
        if (this.buf != null) {
            if (localIn != null) {
            }
        }
        throw streamClosed();
        return (this.count - this.pos) + localIn.available();
    }

    private static IOException streamClosed() throws IOException {
        throw new IOException("BufferedInputStream is closed");
    }

    public synchronized void fixMarkLimit() {
        this.marklimit = this.buf.length;
    }

    public synchronized void release() {
        if (this.buf != null) {
            this.byteArrayPool.put(this.buf);
            this.buf = null;
        }
    }

    public void close() throws IOException {
        if (this.buf != null) {
            this.byteArrayPool.put(this.buf);
            this.buf = null;
        }
        InputStream localIn = this.in;
        this.in = null;
        if (localIn != null) {
            localIn.close();
        }
    }

    private int fillbuf(InputStream localIn, byte[] localBuf) throws IOException {
        if (this.markpos == -1 || this.pos - this.markpos >= this.marklimit) {
            int bytesread = localIn.read(localBuf);
            if (bytesread > 0) {
                this.markpos = -1;
                this.pos = 0;
                this.count = bytesread;
            }
            return bytesread;
        }
        if (this.markpos == 0 && this.marklimit > localBuf.length && this.count == localBuf.length) {
            int newLength = localBuf.length * 2;
            if (newLength > this.marklimit) {
                newLength = this.marklimit;
            }
            byte[] newbuf = (byte[]) this.byteArrayPool.get(newLength, byte[].class);
            System.arraycopy(localBuf, 0, newbuf, 0, localBuf.length);
            byte[] oldbuf = localBuf;
            this.buf = newbuf;
            localBuf = newbuf;
            this.byteArrayPool.put(oldbuf);
        } else if (this.markpos > 0) {
            System.arraycopy(localBuf, this.markpos, localBuf, 0, localBuf.length - this.markpos);
        }
        this.pos -= this.markpos;
        this.markpos = 0;
        this.count = 0;
        int bytesread2 = localIn.read(localBuf, this.pos, localBuf.length - this.pos);
        this.count = bytesread2 <= 0 ? this.pos : this.pos + bytesread2;
        return bytesread2;
    }

    public synchronized void mark(int readlimit) {
        this.marklimit = Math.max(this.marklimit, readlimit);
        this.markpos = this.pos;
    }

    public boolean markSupported() {
        return true;
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:12:0x0019=Splitter:B:12:0x0019, B:27:0x003c=Splitter:B:27:0x003c} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int read() throws java.io.IOException {
        /*
            r5 = this;
            monitor-enter(r5)
            byte[] r0 = r5.buf     // Catch:{ all -> 0x0041 }
            java.io.InputStream r1 = r5.in     // Catch:{ all -> 0x0041 }
            if (r0 == 0) goto L_0x003c
            if (r1 != 0) goto L_0x000a
            goto L_0x003c
        L_0x000a:
            int r2 = r5.pos     // Catch:{ all -> 0x0041 }
            int r3 = r5.count     // Catch:{ all -> 0x0041 }
            r4 = -1
            if (r2 < r3) goto L_0x0019
            int r2 = r5.fillbuf(r1, r0)     // Catch:{ all -> 0x0041 }
            if (r2 != r4) goto L_0x0019
            monitor-exit(r5)
            return r4
        L_0x0019:
            byte[] r2 = r5.buf     // Catch:{ all -> 0x0041 }
            if (r0 == r2) goto L_0x0027
            byte[] r2 = r5.buf     // Catch:{ all -> 0x0041 }
            r0 = r2
            if (r0 != 0) goto L_0x0027
            java.io.IOException r2 = streamClosed()     // Catch:{ all -> 0x0041 }
            throw r2     // Catch:{ all -> 0x0041 }
        L_0x0027:
            int r2 = r5.count     // Catch:{ all -> 0x0041 }
            int r3 = r5.pos     // Catch:{ all -> 0x0041 }
            int r2 = r2 - r3
            if (r2 <= 0) goto L_0x003a
            int r2 = r5.pos     // Catch:{ all -> 0x0041 }
            int r3 = r2 + 1
            r5.pos = r3     // Catch:{ all -> 0x0041 }
            byte r2 = r0[r2]     // Catch:{ all -> 0x0041 }
            r2 = r2 & 255(0xff, float:3.57E-43)
            monitor-exit(r5)
            return r2
        L_0x003a:
            monitor-exit(r5)
            return r4
        L_0x003c:
            java.io.IOException r2 = streamClosed()     // Catch:{ all -> 0x0041 }
            throw r2     // Catch:{ all -> 0x0041 }
        L_0x0041:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream.read():int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0044, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x005a, code lost:
        return r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0067, code lost:
        return r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int read(@android.support.annotation.NonNull byte[] r6, int r7, int r8) throws java.io.IOException {
        /*
            r5 = this;
            monitor-enter(r5)
            byte[] r0 = r5.buf     // Catch:{ all -> 0x009f }
            if (r0 != 0) goto L_0x000a
            java.io.IOException r1 = streamClosed()     // Catch:{ all -> 0x009f }
            throw r1     // Catch:{ all -> 0x009f }
        L_0x000a:
            if (r8 != 0) goto L_0x000f
            r1 = 0
            monitor-exit(r5)
            return r1
        L_0x000f:
            java.io.InputStream r1 = r5.in     // Catch:{ all -> 0x009f }
            if (r1 != 0) goto L_0x0018
            java.io.IOException r2 = streamClosed()     // Catch:{ all -> 0x009f }
            throw r2     // Catch:{ all -> 0x009f }
        L_0x0018:
            int r2 = r5.pos     // Catch:{ all -> 0x009f }
            int r3 = r5.count     // Catch:{ all -> 0x009f }
            if (r2 >= r3) goto L_0x0045
            int r2 = r5.count     // Catch:{ all -> 0x009f }
            int r3 = r5.pos     // Catch:{ all -> 0x009f }
            int r2 = r2 - r3
            if (r2 < r8) goto L_0x0027
            r2 = r8
            goto L_0x002c
        L_0x0027:
            int r2 = r5.count     // Catch:{ all -> 0x009f }
            int r3 = r5.pos     // Catch:{ all -> 0x009f }
            int r2 = r2 - r3
        L_0x002c:
            int r3 = r5.pos     // Catch:{ all -> 0x009f }
            java.lang.System.arraycopy(r0, r3, r6, r7, r2)     // Catch:{ all -> 0x009f }
            int r3 = r5.pos     // Catch:{ all -> 0x009f }
            int r3 = r3 + r2
            r5.pos = r3     // Catch:{ all -> 0x009f }
            if (r2 == r8) goto L_0x0043
            int r3 = r1.available()     // Catch:{ all -> 0x009f }
            if (r3 != 0) goto L_0x003f
            goto L_0x0043
        L_0x003f:
            int r7 = r7 + r2
            int r2 = r8 - r2
            goto L_0x0046
        L_0x0043:
            monitor-exit(r5)
            return r2
        L_0x0045:
            r2 = r8
        L_0x0046:
            int r3 = r5.markpos     // Catch:{ all -> 0x009f }
            r4 = -1
            if (r3 != r4) goto L_0x005b
            int r3 = r0.length     // Catch:{ all -> 0x009f }
            if (r2 < r3) goto L_0x005b
            int r3 = r1.read(r6, r7, r2)     // Catch:{ all -> 0x009f }
            if (r3 != r4) goto L_0x008e
            if (r2 != r8) goto L_0x0057
            goto L_0x0059
        L_0x0057:
            int r4 = r8 - r2
        L_0x0059:
            monitor-exit(r5)
            return r4
        L_0x005b:
            int r3 = r5.fillbuf(r1, r0)     // Catch:{ all -> 0x009f }
            if (r3 != r4) goto L_0x0068
            if (r2 != r8) goto L_0x0064
            goto L_0x0066
        L_0x0064:
            int r4 = r8 - r2
        L_0x0066:
            monitor-exit(r5)
            return r4
        L_0x0068:
            byte[] r3 = r5.buf     // Catch:{ all -> 0x009f }
            if (r0 == r3) goto L_0x0076
            byte[] r3 = r5.buf     // Catch:{ all -> 0x009f }
            r0 = r3
            if (r0 != 0) goto L_0x0076
            java.io.IOException r3 = streamClosed()     // Catch:{ all -> 0x009f }
            throw r3     // Catch:{ all -> 0x009f }
        L_0x0076:
            int r3 = r5.count     // Catch:{ all -> 0x009f }
            int r4 = r5.pos     // Catch:{ all -> 0x009f }
            int r3 = r3 - r4
            if (r3 < r2) goto L_0x007f
            r3 = r2
            goto L_0x0084
        L_0x007f:
            int r3 = r5.count     // Catch:{ all -> 0x009f }
            int r4 = r5.pos     // Catch:{ all -> 0x009f }
            int r3 = r3 - r4
        L_0x0084:
            int r4 = r5.pos     // Catch:{ all -> 0x009f }
            java.lang.System.arraycopy(r0, r4, r6, r7, r3)     // Catch:{ all -> 0x009f }
            int r4 = r5.pos     // Catch:{ all -> 0x009f }
            int r4 = r4 + r3
            r5.pos = r4     // Catch:{ all -> 0x009f }
        L_0x008e:
            int r2 = r2 - r3
            if (r2 != 0) goto L_0x0093
            monitor-exit(r5)
            return r8
        L_0x0093:
            int r4 = r1.available()     // Catch:{ all -> 0x009f }
            if (r4 != 0) goto L_0x009d
            int r4 = r8 - r2
            monitor-exit(r5)
            return r4
        L_0x009d:
            int r7 = r7 + r3
            goto L_0x0046
        L_0x009f:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream.read(byte[], int, int):int");
    }

    public synchronized void reset() throws IOException {
        if (this.buf == null) {
            throw new IOException("Stream is closed");
        } else if (-1 == this.markpos) {
            throw new InvalidMarkException("Mark has been invalidated, pos: " + this.pos + " markLimit: " + this.marklimit);
        } else {
            this.pos = this.markpos;
        }
    }

    public synchronized long skip(long byteCount) throws IOException {
        if (byteCount < 1) {
            return 0;
        }
        byte[] localBuf = this.buf;
        if (localBuf == null) {
            throw streamClosed();
        }
        InputStream localIn = this.in;
        if (localIn == null) {
            throw streamClosed();
        } else if (((long) (this.count - this.pos)) >= byteCount) {
            this.pos = (int) (((long) this.pos) + byteCount);
            return byteCount;
        } else {
            long read = ((long) this.count) - ((long) this.pos);
            this.pos = this.count;
            if (this.markpos == -1 || byteCount > ((long) this.marklimit)) {
                return read + localIn.skip(byteCount - read);
            } else if (fillbuf(localIn, localBuf) == -1) {
                return read;
            } else {
                if (((long) (this.count - this.pos)) >= byteCount - read) {
                    this.pos = (int) ((((long) this.pos) + byteCount) - read);
                    return byteCount;
                }
                long read2 = (read + ((long) this.count)) - ((long) this.pos);
                this.pos = this.count;
                return read2;
            }
        }
    }

    static class InvalidMarkException extends IOException {
        private static final long serialVersionUID = -4338378848813561757L;

        InvalidMarkException(String detailMessage) {
            super(detailMessage);
        }
    }
}
