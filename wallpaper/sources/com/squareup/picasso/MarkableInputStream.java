package com.squareup.picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

final class MarkableInputStream extends InputStream {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final int DEFAULT_LIMIT_INCREMENT = 1024;
    private boolean allowExpire;
    private long defaultMark;
    private final InputStream in;
    private long limit;
    private int limitIncrement;
    private long offset;
    private long reset;

    MarkableInputStream(InputStream in2) {
        this(in2, 4096);
    }

    MarkableInputStream(InputStream in2, int size) {
        this(in2, size, 1024);
    }

    private MarkableInputStream(InputStream in2, int size, int limitIncrement2) {
        this.defaultMark = -1;
        this.allowExpire = true;
        this.limitIncrement = -1;
        this.in = !in2.markSupported() ? new BufferedInputStream(in2, size) : in2;
        this.limitIncrement = limitIncrement2;
    }

    public void mark(int readLimit) {
        this.defaultMark = savePosition(readLimit);
    }

    public long savePosition(int readLimit) {
        long offsetLimit = this.offset + ((long) readLimit);
        if (this.limit < offsetLimit) {
            setLimit(offsetLimit);
        }
        return this.offset;
    }

    public void allowMarksToExpire(boolean allowExpire2) {
        this.allowExpire = allowExpire2;
    }

    private void setLimit(long limit2) {
        try {
            if (this.reset >= this.offset || this.offset > this.limit) {
                this.reset = this.offset;
                this.in.mark((int) (limit2 - this.offset));
            } else {
                this.in.reset();
                this.in.mark((int) (limit2 - this.reset));
                skip(this.reset, this.offset);
            }
            this.limit = limit2;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to mark: " + e);
        }
    }

    public void reset() throws IOException {
        reset(this.defaultMark);
    }

    public void reset(long token) throws IOException {
        if (this.offset > this.limit || token < this.reset) {
            throw new IOException("Cannot reset");
        }
        this.in.reset();
        skip(this.reset, token);
        this.offset = token;
    }

    private void skip(long current, long target) throws IOException {
        while (current < target) {
            long skipped = this.in.skip(target - current);
            if (skipped == 0) {
                if (read() != -1) {
                    skipped = 1;
                } else {
                    return;
                }
            }
            current += skipped;
        }
    }

    public int read() throws IOException {
        if (!this.allowExpire && this.offset + 1 > this.limit) {
            setLimit(this.limit + ((long) this.limitIncrement));
        }
        int result = this.in.read();
        if (result != -1) {
            this.offset++;
        }
        return result;
    }

    public int read(byte[] buffer) throws IOException {
        if (!this.allowExpire && this.offset + ((long) buffer.length) > this.limit) {
            setLimit(this.offset + ((long) buffer.length) + ((long) this.limitIncrement));
        }
        int count = this.in.read(buffer);
        if (count != -1) {
            this.offset += (long) count;
        }
        return count;
    }

    public int read(byte[] buffer, int offset2, int length) throws IOException {
        if (!this.allowExpire && this.offset + ((long) length) > this.limit) {
            setLimit(this.offset + ((long) length) + ((long) this.limitIncrement));
        }
        int count = this.in.read(buffer, offset2, length);
        if (count != -1) {
            this.offset += (long) count;
        }
        return count;
    }

    public long skip(long byteCount) throws IOException {
        if (!this.allowExpire && this.offset + byteCount > this.limit) {
            setLimit(this.offset + byteCount + ((long) this.limitIncrement));
        }
        long skipped = this.in.skip(byteCount);
        this.offset += skipped;
        return skipped;
    }

    public int available() throws IOException {
        return this.in.available();
    }

    public void close() throws IOException {
        this.in.close();
    }

    public boolean markSupported() {
        return this.in.markSupported();
    }
}
