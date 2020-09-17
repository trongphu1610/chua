package okio;

import java.io.IOException;

public final class Pipe {
    final Buffer buffer = new Buffer();
    final long maxBufferSize;
    private final Sink sink = new PipeSink();
    boolean sinkClosed;
    private final Source source = new PipeSource();
    boolean sourceClosed;

    public Pipe(long maxBufferSize2) {
        if (maxBufferSize2 < 1) {
            throw new IllegalArgumentException("maxBufferSize < 1: " + maxBufferSize2);
        }
        this.maxBufferSize = maxBufferSize2;
    }

    public Source source() {
        return this.source;
    }

    public Sink sink() {
        return this.sink;
    }

    final class PipeSink implements Sink {
        final Timeout timeout = new Timeout();

        PipeSink() {
        }

        public void write(Buffer source, long byteCount) throws IOException {
            synchronized (Pipe.this.buffer) {
                try {
                    if (Pipe.this.sinkClosed) {
                        throw new IllegalStateException("closed");
                    }
                    while (byteCount > 0) {
                        if (Pipe.this.sourceClosed) {
                            throw new IOException("source is closed");
                        }
                        long bufferSpaceAvailable = Pipe.this.maxBufferSize - Pipe.this.buffer.size();
                        if (bufferSpaceAvailable == 0) {
                            this.timeout.waitUntilNotified(Pipe.this.buffer);
                        } else {
                            long bytesToWrite = Math.min(bufferSpaceAvailable, byteCount);
                            Pipe.this.buffer.write(source, bytesToWrite);
                            long byteCount2 = byteCount - bytesToWrite;
                            try {
                                Pipe.this.buffer.notifyAll();
                                byteCount = byteCount2;
                            } catch (Throwable th) {
                                th = th;
                                long j = byteCount2;
                                throw th;
                            }
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    throw th;
                }
            }
        }

        public void flush() throws IOException {
            synchronized (Pipe.this.buffer) {
                if (Pipe.this.sinkClosed) {
                    throw new IllegalStateException("closed");
                } else if (Pipe.this.sourceClosed && Pipe.this.buffer.size() > 0) {
                    throw new IOException("source is closed");
                }
            }
        }

        public void close() throws IOException {
            synchronized (Pipe.this.buffer) {
                if (!Pipe.this.sinkClosed) {
                    if (!Pipe.this.sourceClosed || Pipe.this.buffer.size() <= 0) {
                        Pipe.this.sinkClosed = true;
                        Pipe.this.buffer.notifyAll();
                        return;
                    }
                    throw new IOException("source is closed");
                }
            }
        }

        public Timeout timeout() {
            return this.timeout;
        }
    }

    final class PipeSource implements Source {
        final Timeout timeout = new Timeout();

        PipeSource() {
        }

        public long read(Buffer sink, long byteCount) throws IOException {
            synchronized (Pipe.this.buffer) {
                if (Pipe.this.sourceClosed) {
                    throw new IllegalStateException("closed");
                }
                while (Pipe.this.buffer.size() == 0) {
                    if (Pipe.this.sinkClosed) {
                        return -1;
                    }
                    this.timeout.waitUntilNotified(Pipe.this.buffer);
                }
                long result = Pipe.this.buffer.read(sink, byteCount);
                Pipe.this.buffer.notifyAll();
                return result;
            }
        }

        public void close() throws IOException {
            synchronized (Pipe.this.buffer) {
                Pipe.this.sourceClosed = true;
                Pipe.this.buffer.notifyAll();
            }
        }

        public Timeout timeout() {
            return this.timeout;
        }
    }
}
