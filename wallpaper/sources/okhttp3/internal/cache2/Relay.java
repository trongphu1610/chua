package okhttp3.internal.cache2;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.ByteString;
import okio.Source;
import okio.Timeout;

final class Relay {
    private static final long FILE_HEADER_SIZE = 32;
    static final ByteString PREFIX_CLEAN = ByteString.encodeUtf8("OkHttp cache v1\n");
    static final ByteString PREFIX_DIRTY = ByteString.encodeUtf8("OkHttp DIRTY :(\n");
    private static final int SOURCE_FILE = 2;
    private static final int SOURCE_UPSTREAM = 1;
    final Buffer buffer = new Buffer();
    final long bufferMaxSize;
    boolean complete;
    RandomAccessFile file;
    private final ByteString metadata;
    int sourceCount;
    Source upstream;
    final Buffer upstreamBuffer = new Buffer();
    long upstreamPos;
    Thread upstreamReader;

    private Relay(RandomAccessFile file2, Source upstream2, long upstreamPos2, ByteString metadata2, long bufferMaxSize2) {
        this.file = file2;
        this.upstream = upstream2;
        this.complete = upstream2 == null;
        this.upstreamPos = upstreamPos2;
        this.metadata = metadata2;
        this.bufferMaxSize = bufferMaxSize2;
    }

    public static Relay edit(File file2, Source upstream2, ByteString metadata2, long bufferMaxSize2) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file2, "rw");
        Relay relay = new Relay(randomAccessFile, upstream2, 0, metadata2, bufferMaxSize2);
        randomAccessFile.setLength(0);
        relay.writeHeader(PREFIX_DIRTY, -1, -1);
        return relay;
    }

    public static Relay read(File file2) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file2, "rw");
        FileOperator fileOperator = new FileOperator(randomAccessFile.getChannel());
        Buffer header = new Buffer();
        fileOperator.read(0, header, FILE_HEADER_SIZE);
        if (!header.readByteString((long) PREFIX_CLEAN.size()).equals(PREFIX_CLEAN)) {
            throw new IOException("unreadable cache file");
        }
        long upstreamSize = header.readLong();
        long metadataSize = header.readLong();
        Buffer metadataBuffer = new Buffer();
        fileOperator.read(upstreamSize + FILE_HEADER_SIZE, metadataBuffer, metadataSize);
        Buffer buffer2 = metadataBuffer;
        return new Relay(randomAccessFile, (Source) null, upstreamSize, metadataBuffer.readByteString(), 0);
    }

    private void writeHeader(ByteString prefix, long upstreamSize, long metadataSize) throws IOException {
        Buffer header = new Buffer();
        header.write(prefix);
        header.writeLong(upstreamSize);
        header.writeLong(metadataSize);
        if (header.size() != FILE_HEADER_SIZE) {
            throw new IllegalArgumentException();
        }
        new FileOperator(this.file.getChannel()).write(0, header, FILE_HEADER_SIZE);
    }

    private void writeMetadata(long upstreamSize) throws IOException {
        Buffer metadataBuffer = new Buffer();
        metadataBuffer.write(this.metadata);
        new FileOperator(this.file.getChannel()).write(upstreamSize + FILE_HEADER_SIZE, metadataBuffer, (long) this.metadata.size());
    }

    /* access modifiers changed from: package-private */
    public void commit(long upstreamSize) throws IOException {
        writeMetadata(upstreamSize);
        this.file.getChannel().force(false);
        writeHeader(PREFIX_CLEAN, upstreamSize, (long) this.metadata.size());
        this.file.getChannel().force(false);
        synchronized (this) {
            this.complete = true;
        }
        Util.closeQuietly((Closeable) this.upstream);
        this.upstream = null;
    }

    /* access modifiers changed from: package-private */
    public boolean isClosed() {
        return this.file == null;
    }

    public ByteString metadata() {
        return this.metadata;
    }

    public Source newSource() {
        synchronized (this) {
            if (this.file == null) {
                return null;
            }
            this.sourceCount++;
            return new RelaySource();
        }
    }

    class RelaySource implements Source {
        private FileOperator fileOperator = new FileOperator(Relay.this.file.getChannel());
        private long sourcePos;
        private final Timeout timeout = new Timeout();

        RelaySource() {
        }

        /* JADX INFO: finally extract failed */
        /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
            java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
            	at java.util.ArrayList.rangeCheck(Unknown Source)
            	at java.util.ArrayList.get(Unknown Source)
            	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
            	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
            	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
            	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
            	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
            */
        public long read(okio.Buffer r26, long r27) throws java.io.IOException {
            /*
                r25 = this;
                r1 = r25
                r2 = r27
                okhttp3.internal.cache2.FileOperator r4 = r1.fileOperator
                if (r4 != 0) goto L_0x0010
                java.lang.IllegalStateException r4 = new java.lang.IllegalStateException
                java.lang.String r5 = "closed"
                r4.<init>(r5)
                throw r4
            L_0x0010:
                okhttp3.internal.cache2.Relay r4 = okhttp3.internal.cache2.Relay.this
                monitor-enter(r4)
            L_0x0013:
                long r5 = r1.sourcePos     // Catch:{ all -> 0x0187 }
                okhttp3.internal.cache2.Relay r7 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0187 }
                long r7 = r7.upstreamPos     // Catch:{ all -> 0x0187 }
                r9 = r7
                int r11 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                r5 = -1
                if (r11 != 0) goto L_0x0041
                okhttp3.internal.cache2.Relay r7 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0187 }
                boolean r7 = r7.complete     // Catch:{ all -> 0x0187 }
                if (r7 == 0) goto L_0x0028
                monitor-exit(r4)     // Catch:{ all -> 0x0187 }
                return r5
            L_0x0028:
                okhttp3.internal.cache2.Relay r7 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0187 }
                java.lang.Thread r7 = r7.upstreamReader     // Catch:{ all -> 0x0187 }
                if (r7 == 0) goto L_0x0036
                okio.Timeout r5 = r1.timeout     // Catch:{ all -> 0x0187 }
                okhttp3.internal.cache2.Relay r6 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0187 }
                r5.waitUntilNotified(r6)     // Catch:{ all -> 0x0187 }
                goto L_0x0013
            L_0x0036:
                okhttp3.internal.cache2.Relay r7 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0187 }
                java.lang.Thread r8 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0187 }
                r7.upstreamReader = r8     // Catch:{ all -> 0x0187 }
                r7 = 1
                monitor-exit(r4)     // Catch:{ all -> 0x0187 }
                goto L_0x0054
            L_0x0041:
                okhttp3.internal.cache2.Relay r7 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0187 }
                okio.Buffer r7 = r7.buffer     // Catch:{ all -> 0x0187 }
                long r7 = r7.size()     // Catch:{ all -> 0x0187 }
                r11 = 0
                long r11 = r9 - r7
                long r7 = r1.sourcePos     // Catch:{ all -> 0x0187 }
                int r13 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
                if (r13 >= 0) goto L_0x0165
                r7 = 2
                monitor-exit(r4)     // Catch:{ all -> 0x0187 }
            L_0x0054:
                r8 = r9
                r4 = 2
                r10 = 32
                if (r7 != r4) goto L_0x0076
                long r4 = r1.sourcePos
                long r12 = r8 - r4
                long r4 = java.lang.Math.min(r2, r12)
                okhttp3.internal.cache2.FileOperator r14 = r1.fileOperator
                long r12 = r1.sourcePos
                long r15 = r12 + r10
                r17 = r26
                r18 = r4
                r14.read(r15, r17, r18)
                long r10 = r1.sourcePos
                long r12 = r10 + r4
                r1.sourcePos = r12
                return r4
            L_0x0076:
                r4 = 0
                okhttp3.internal.cache2.Relay r12 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x014c }
                okio.Source r12 = r12.upstream     // Catch:{ all -> 0x014c }
                okhttp3.internal.cache2.Relay r13 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x014c }
                okio.Buffer r13 = r13.upstreamBuffer     // Catch:{ all -> 0x014c }
                okhttp3.internal.cache2.Relay r14 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x014c }
                long r14 = r14.bufferMaxSize     // Catch:{ all -> 0x014c }
                long r12 = r12.read(r13, r14)     // Catch:{ all -> 0x014c }
                int r14 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
                if (r14 != 0) goto L_0x00ab
                okhttp3.internal.cache2.Relay r10 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x00a3 }
                r10.commit(r8)     // Catch:{ all -> 0x00a3 }
                okhttp3.internal.cache2.Relay r10 = okhttp3.internal.cache2.Relay.this
                monitor-enter(r10)
                okhttp3.internal.cache2.Relay r11 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x009f }
                r11.upstreamReader = r4     // Catch:{ all -> 0x009f }
                okhttp3.internal.cache2.Relay r4 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x009f }
                r4.notifyAll()     // Catch:{ all -> 0x009f }
                monitor-exit(r10)     // Catch:{ all -> 0x009f }
                return r5
            L_0x009f:
                r0 = move-exception
                r4 = r0
                monitor-exit(r10)     // Catch:{ all -> 0x009f }
                throw r4
            L_0x00a3:
                r0 = move-exception
                r4 = r0
                r24 = r7
                r22 = r8
                goto L_0x0152
            L_0x00ab:
                long r5 = java.lang.Math.min(r12, r2)     // Catch:{ all -> 0x014c }
                okhttp3.internal.cache2.Relay r14 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x014c }
                okio.Buffer r14 = r14.upstreamBuffer     // Catch:{ all -> 0x014c }
                r16 = 0
                r15 = r26
                r18 = r5
                r14.copyTo((okio.Buffer) r15, (long) r16, (long) r18)     // Catch:{ all -> 0x014c }
                long r14 = r1.sourcePos     // Catch:{ all -> 0x014c }
                r16 = 0
                long r10 = r14 + r5
                r1.sourcePos = r10     // Catch:{ all -> 0x014c }
                okhttp3.internal.cache2.FileOperator r14 = r1.fileOperator     // Catch:{ all -> 0x014c }
                r10 = 0
                r10 = 32
                long r15 = r8 + r10
                okhttp3.internal.cache2.Relay r10 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x014c }
                okio.Buffer r10 = r10.upstreamBuffer     // Catch:{ all -> 0x014c }
                okio.Buffer r17 = r10.clone()     // Catch:{ all -> 0x014c }
                r18 = r12
                r14.write(r15, r17, r18)     // Catch:{ all -> 0x014c }
                okhttp3.internal.cache2.Relay r10 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x014c }
                monitor-enter(r10)     // Catch:{ all -> 0x014c }
                okhttp3.internal.cache2.Relay r11 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x013c }
                okio.Buffer r11 = r11.buffer     // Catch:{ all -> 0x013c }
                okhttp3.internal.cache2.Relay r14 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x013c }
                okio.Buffer r14 = r14.upstreamBuffer     // Catch:{ all -> 0x013c }
                r11.write((okio.Buffer) r14, (long) r12)     // Catch:{ all -> 0x013c }
                okhttp3.internal.cache2.Relay r11 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x013c }
                okio.Buffer r11 = r11.buffer     // Catch:{ all -> 0x013c }
                long r14 = r11.size()     // Catch:{ all -> 0x013c }
                okhttp3.internal.cache2.Relay r11 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x013c }
                r20 = r5
                long r4 = r11.bufferMaxSize     // Catch:{ all -> 0x0135 }
                int r6 = (r14 > r4 ? 1 : (r14 == r4 ? 0 : -1))
                if (r6 <= 0) goto L_0x0113
                okhttp3.internal.cache2.Relay r4 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0135 }
                okio.Buffer r4 = r4.buffer     // Catch:{ all -> 0x0135 }
                okhttp3.internal.cache2.Relay r5 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0135 }
                okio.Buffer r5 = r5.buffer     // Catch:{ all -> 0x0135 }
                long r5 = r5.size()     // Catch:{ all -> 0x0135 }
                okhttp3.internal.cache2.Relay r11 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0135 }
                long r14 = r11.bufferMaxSize     // Catch:{ all -> 0x0135 }
                r11 = 0
                r24 = r7
                r22 = r8
                long r7 = r5 - r14
                r4.skip(r7)     // Catch:{ all -> 0x0149 }
                goto L_0x0117
            L_0x0113:
                r24 = r7
                r22 = r8
            L_0x0117:
                okhttp3.internal.cache2.Relay r4 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0149 }
                long r5 = r4.upstreamPos     // Catch:{ all -> 0x0149 }
                r7 = 0
                long r7 = r5 + r12
                r4.upstreamPos = r7     // Catch:{ all -> 0x0149 }
                monitor-exit(r10)     // Catch:{ all -> 0x0149 }
                okhttp3.internal.cache2.Relay r4 = okhttp3.internal.cache2.Relay.this
                monitor-enter(r4)
                okhttp3.internal.cache2.Relay r5 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0131 }
                r6 = 0
                r5.upstreamReader = r6     // Catch:{ all -> 0x0131 }
                okhttp3.internal.cache2.Relay r5 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0131 }
                r5.notifyAll()     // Catch:{ all -> 0x0131 }
                monitor-exit(r4)     // Catch:{ all -> 0x0131 }
                return r20
            L_0x0131:
                r0 = move-exception
                r5 = r0
                monitor-exit(r4)     // Catch:{ all -> 0x0131 }
                throw r5
            L_0x0135:
                r0 = move-exception
                r24 = r7
                r22 = r8
                r4 = r0
                goto L_0x0144
            L_0x013c:
                r0 = move-exception
                r20 = r5
                r24 = r7
                r22 = r8
                r4 = r0
            L_0x0144:
                monitor-exit(r10)     // Catch:{ all -> 0x0149 }
                throw r4     // Catch:{ all -> 0x0146 }
            L_0x0146:
                r0 = move-exception
                r4 = r0
                goto L_0x0152
            L_0x0149:
                r0 = move-exception
                r4 = r0
                goto L_0x0144
            L_0x014c:
                r0 = move-exception
                r24 = r7
                r22 = r8
                r4 = r0
            L_0x0152:
                okhttp3.internal.cache2.Relay r5 = okhttp3.internal.cache2.Relay.this
                monitor-enter(r5)
                okhttp3.internal.cache2.Relay r6 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0161 }
                r7 = 0
                r6.upstreamReader = r7     // Catch:{ all -> 0x0161 }
                okhttp3.internal.cache2.Relay r6 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0161 }
                r6.notifyAll()     // Catch:{ all -> 0x0161 }
                monitor-exit(r5)     // Catch:{ all -> 0x0161 }
                throw r4
            L_0x0161:
                r0 = move-exception
                r4 = r0
                monitor-exit(r5)     // Catch:{ all -> 0x0161 }
                throw r4
            L_0x0165:
                long r5 = r1.sourcePos     // Catch:{ all -> 0x0187 }
                r7 = 0
                long r7 = r9 - r5
                long r5 = java.lang.Math.min(r2, r7)     // Catch:{ all -> 0x0187 }
                okhttp3.internal.cache2.Relay r7 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0187 }
                okio.Buffer r13 = r7.buffer     // Catch:{ all -> 0x0187 }
                long r7 = r1.sourcePos     // Catch:{ all -> 0x0187 }
                r14 = 0
                long r15 = r7 - r11
                r14 = r26
                r17 = r5
                r13.copyTo((okio.Buffer) r14, (long) r15, (long) r17)     // Catch:{ all -> 0x0187 }
                long r7 = r1.sourcePos     // Catch:{ all -> 0x0187 }
                r13 = 0
                long r13 = r7 + r5
                r1.sourcePos = r13     // Catch:{ all -> 0x0187 }
                monitor-exit(r4)     // Catch:{ all -> 0x0187 }
                return r5
            L_0x0187:
                r0 = move-exception
                r5 = r0
                monitor-exit(r4)     // Catch:{ all -> 0x0187 }
                throw r5
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache2.Relay.RelaySource.read(okio.Buffer, long):long");
        }

        public Timeout timeout() {
            return this.timeout;
        }

        public void close() throws IOException {
            if (this.fileOperator != null) {
                this.fileOperator = null;
                RandomAccessFile fileToClose = null;
                synchronized (Relay.this) {
                    Relay relay = Relay.this;
                    relay.sourceCount--;
                    if (Relay.this.sourceCount == 0) {
                        fileToClose = Relay.this.file;
                        Relay.this.file = null;
                    }
                }
                if (fileToClose != null) {
                    Util.closeQuietly((Closeable) fileToClose);
                }
            }
        }
    }
}
