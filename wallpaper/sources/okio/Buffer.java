package okio;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import kotlin.jvm.internal.LongCompanionObject;
import kotlin.text.Typography;

public final class Buffer implements BufferedSource, BufferedSink, Cloneable, ByteChannel {
    private static final byte[] DIGITS = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    static final int REPLACEMENT_CHARACTER = 65533;
    @Nullable
    Segment head;
    long size;

    public long size() {
        return this.size;
    }

    public Buffer buffer() {
        return this;
    }

    public OutputStream outputStream() {
        return new OutputStream() {
            public void write(int b) {
                Buffer.this.writeByte((int) (byte) b);
            }

            public void write(byte[] data, int offset, int byteCount) {
                Buffer.this.write(data, offset, byteCount);
            }

            public void flush() {
            }

            public void close() {
            }

            public String toString() {
                return Buffer.this + ".outputStream()";
            }
        };
    }

    public Buffer emitCompleteSegments() {
        return this;
    }

    public BufferedSink emit() {
        return this;
    }

    public boolean exhausted() {
        return this.size == 0;
    }

    public void require(long byteCount) throws EOFException {
        if (this.size < byteCount) {
            throw new EOFException();
        }
    }

    public boolean request(long byteCount) {
        return this.size >= byteCount;
    }

    public InputStream inputStream() {
        return new InputStream() {
            public int read() {
                if (Buffer.this.size > 0) {
                    return Buffer.this.readByte() & 255;
                }
                return -1;
            }

            public int read(byte[] sink, int offset, int byteCount) {
                return Buffer.this.read(sink, offset, byteCount);
            }

            public int available() {
                return (int) Math.min(Buffer.this.size, 2147483647L);
            }

            public void close() {
            }

            public String toString() {
                return Buffer.this + ".inputStream()";
            }
        };
    }

    public Buffer copyTo(OutputStream out) throws IOException {
        return copyTo(out, 0, this.size);
    }

    public Buffer copyTo(OutputStream out, long offset, long byteCount) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("out == null");
        }
        Util.checkOffsetAndCount(this.size, offset, byteCount);
        if (byteCount == 0) {
            return this;
        }
        Segment s = this.head;
        while (offset >= ((long) (s.limit - s.pos))) {
            s = s.next;
            offset -= (long) (s.limit - s.pos);
        }
        while (byteCount > 0) {
            int pos = (int) (((long) s.pos) + offset);
            int toCopy = (int) Math.min((long) (s.limit - pos), byteCount);
            out.write(s.data, pos, toCopy);
            offset = 0;
            s = s.next;
            byteCount -= (long) toCopy;
        }
        return this;
    }

    public Buffer copyTo(Buffer out, long offset, long byteCount) {
        if (out == null) {
            throw new IllegalArgumentException("out == null");
        }
        Util.checkOffsetAndCount(this.size, offset, byteCount);
        if (byteCount == 0) {
            return this;
        }
        out.size += byteCount;
        Segment s = this.head;
        while (offset >= ((long) (s.limit - s.pos))) {
            s = s.next;
            offset -= (long) (s.limit - s.pos);
        }
        while (byteCount > 0) {
            Segment copy = s.sharedCopy();
            copy.pos = (int) (((long) copy.pos) + offset);
            copy.limit = Math.min(copy.pos + ((int) byteCount), copy.limit);
            if (out.head == null) {
                copy.prev = copy;
                copy.next = copy;
                out.head = copy;
            } else {
                out.head.prev.push(copy);
            }
            offset = 0;
            s = s.next;
            byteCount -= (long) (copy.limit - copy.pos);
        }
        return this;
    }

    public Buffer writeTo(OutputStream out) throws IOException {
        return writeTo(out, this.size);
    }

    public Buffer writeTo(OutputStream out, long byteCount) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("out == null");
        }
        Util.checkOffsetAndCount(this.size, 0, byteCount);
        Segment s = this.head;
        while (byteCount > 0) {
            int toCopy = (int) Math.min(byteCount, (long) (s.limit - s.pos));
            out.write(s.data, s.pos, toCopy);
            s.pos += toCopy;
            this.size -= (long) toCopy;
            long byteCount2 = byteCount - ((long) toCopy);
            if (s.pos == s.limit) {
                Segment toRecycle = s;
                Segment pop = toRecycle.pop();
                s = pop;
                this.head = pop;
                SegmentPool.recycle(toRecycle);
            }
            byteCount = byteCount2;
        }
        return this;
    }

    public Buffer readFrom(InputStream in) throws IOException {
        readFrom(in, LongCompanionObject.MAX_VALUE, true);
        return this;
    }

    public Buffer readFrom(InputStream in, long byteCount) throws IOException {
        if (byteCount < 0) {
            throw new IllegalArgumentException("byteCount < 0: " + byteCount);
        }
        readFrom(in, byteCount, false);
        return this;
    }

    private void readFrom(InputStream in, long byteCount, boolean forever) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("in == null");
        }
        while (true) {
            if (byteCount > 0 || forever) {
                Segment tail = writableSegment(1);
                int bytesRead = in.read(tail.data, tail.limit, (int) Math.min(byteCount, (long) (8192 - tail.limit)));
                if (bytesRead != -1) {
                    tail.limit += bytesRead;
                    this.size += (long) bytesRead;
                    byteCount -= (long) bytesRead;
                } else if (!forever) {
                    throw new EOFException();
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    public long completeSegmentByteCount() {
        long result = this.size;
        if (result == 0) {
            return 0;
        }
        Segment tail = this.head.prev;
        if (tail.limit >= 8192 || !tail.owner) {
            return result;
        }
        return result - ((long) (tail.limit - tail.pos));
    }

    public byte readByte() {
        if (this.size == 0) {
            throw new IllegalStateException("size == 0");
        }
        Segment segment = this.head;
        int pos = segment.pos;
        int limit = segment.limit;
        int pos2 = pos + 1;
        byte pos3 = segment.data[pos];
        this.size--;
        if (pos2 == limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        } else {
            segment.pos = pos2;
        }
        return pos3;
    }

    public byte getByte(long pos) {
        Util.checkOffsetAndCount(this.size, pos, 1);
        if (this.size - pos > pos) {
            Segment s = this.head;
            while (true) {
                int segmentByteCount = s.limit - s.pos;
                if (pos < ((long) segmentByteCount)) {
                    return s.data[s.pos + ((int) pos)];
                }
                s = s.next;
                pos -= (long) segmentByteCount;
            }
        } else {
            long pos2 = pos - this.size;
            Segment s2 = this.head.prev;
            while (true) {
                long pos3 = pos2 + ((long) (s2.limit - s2.pos));
                if (pos3 >= 0) {
                    return s2.data[s2.pos + ((int) pos3)];
                }
                s2 = s2.prev;
                pos2 = pos3;
            }
        }
    }

    public short readShort() {
        if (this.size < 2) {
            throw new IllegalStateException("size < 2: " + this.size);
        }
        Segment segment = this.head;
        int pos = segment.pos;
        int limit = segment.limit;
        if (limit - pos < 2) {
            return (short) (((readByte() & 255) << 8) | (readByte() & 255));
        }
        byte[] data = segment.data;
        int pos2 = pos + 1;
        int pos3 = pos2 + 1;
        int s = ((data[pos] & 255) << 8) | (data[pos2] & 255);
        this.size -= 2;
        if (pos3 == limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        } else {
            segment.pos = pos3;
        }
        return (short) s;
    }

    public int readInt() {
        if (this.size < 4) {
            throw new IllegalStateException("size < 4: " + this.size);
        }
        Segment segment = this.head;
        int pos = segment.pos;
        int limit = segment.limit;
        if (limit - pos < 4) {
            return ((readByte() & 255) << 24) | ((readByte() & 255) << 16) | ((readByte() & 255) << 8) | (readByte() & 255);
        }
        byte[] data = segment.data;
        int pos2 = pos + 1;
        int pos3 = pos2 + 1;
        int i = ((data[pos] & 255) << 24) | ((data[pos2] & 255) << 16);
        int pos4 = pos3 + 1;
        int i2 = i | ((data[pos3] & 255) << 8);
        int pos5 = pos4 + 1;
        int i3 = i2 | (data[pos4] & 255);
        this.size -= 4;
        if (pos5 == limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        } else {
            segment.pos = pos5;
        }
        return i3;
    }

    public long readLong() {
        if (this.size < 8) {
            throw new IllegalStateException("size < 8: " + this.size);
        }
        Segment segment = this.head;
        int pos = segment.pos;
        int limit = segment.limit;
        if (limit - pos < 8) {
            return ((((long) readInt()) & 4294967295L) << 32) | (((long) readInt()) & 4294967295L);
        }
        byte[] data = segment.data;
        int pos2 = pos + 1;
        int pos3 = pos2 + 1;
        int pos4 = pos3 + 1;
        int pos5 = pos4 + 1;
        int pos6 = pos5 + 1;
        int pos7 = pos6 + 1;
        int pos8 = pos7 + 1;
        int pos9 = pos8 + 1;
        long v = ((((long) data[pos]) & 255) << 56) | ((((long) data[pos2]) & 255) << 48) | ((((long) data[pos3]) & 255) << 40) | ((((long) data[pos4]) & 255) << 32) | ((((long) data[pos5]) & 255) << 24) | ((((long) data[pos6]) & 255) << 16) | ((((long) data[pos7]) & 255) << 8) | (((long) data[pos8]) & 255);
        this.size -= 8;
        if (pos9 == limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        } else {
            segment.pos = pos9;
        }
        return v;
    }

    public short readShortLe() {
        return Util.reverseBytesShort(readShort());
    }

    public int readIntLe() {
        return Util.reverseBytesInt(readInt());
    }

    public long readLongLe() {
        return Util.reverseBytesLong(readLong());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00cd, code lost:
        if (r12 != r13) goto L_0x00d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00cf, code lost:
        r0.head = r10.pop();
        okio.SegmentPool.recycle(r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00d9, code lost:
        r10.pos = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00db, code lost:
        if (r5 != false) goto L_0x00e6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00df, code lost:
        if (r0.head != null) goto L_0x00e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00e6, code lost:
        r0.size -= (long) r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00ed, code lost:
        if (r4 == false) goto L_0x00f1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:?, code lost:
        return -r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:?, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long readDecimalLong() {
        /*
            r21 = this;
            r0 = r21
            long r1 = r0.size
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x0012
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.String r2 = "size == 0"
            r1.<init>(r2)
            throw r1
        L_0x0012:
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = -922337203685477580(0xf333333333333334, double:-8.390303882365713E246)
            r8 = -7
        L_0x001e:
            okio.Segment r10 = r0.head
            byte[] r11 = r10.data
            int r12 = r10.pos
            int r13 = r10.limit
        L_0x0026:
            if (r12 >= r13) goto L_0x00c7
            byte r14 = r11[r12]
            r15 = 48
            if (r14 < r15) goto L_0x008a
            r15 = 57
            if (r14 > r15) goto L_0x008a
            r15 = 48
            int r15 = r15 - r14
            int r16 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r16 < 0) goto L_0x0057
            int r16 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r16 != 0) goto L_0x0047
            r19 = r5
            r17 = r6
            long r5 = (long) r15
            int r7 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r7 >= 0) goto L_0x004b
            goto L_0x005b
        L_0x0047:
            r19 = r5
            r17 = r6
        L_0x004b:
            r5 = 10
            long r1 = r1 * r5
            long r5 = (long) r15
            long r15 = r1 + r5
            r20 = r11
            r1 = r15
            goto L_0x009c
        L_0x0057:
            r19 = r5
            r17 = r6
        L_0x005b:
            okio.Buffer r5 = new okio.Buffer
            r5.<init>()
            okio.Buffer r5 = r5.writeDecimalLong((long) r1)
            okio.Buffer r5 = r5.writeByte((int) r14)
            if (r4 != 0) goto L_0x006d
            r5.readByte()
        L_0x006d:
            java.lang.NumberFormatException r6 = new java.lang.NumberFormatException
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r20 = r11
            java.lang.String r11 = "Number too large: "
            r7.append(r11)
            java.lang.String r11 = r5.readUtf8()
            r7.append(r11)
            java.lang.String r7 = r7.toString()
            r6.<init>(r7)
            throw r6
        L_0x008a:
            r19 = r5
            r17 = r6
            r20 = r11
            r5 = 45
            if (r14 != r5) goto L_0x00a8
            if (r3 != 0) goto L_0x00a8
            r4 = 1
            r5 = 1
            long r15 = r8 - r5
            r8 = r15
        L_0x009c:
            int r12 = r12 + 1
            int r3 = r3 + 1
            r6 = r17
            r5 = r19
            r11 = r20
            goto L_0x0026
        L_0x00a8:
            if (r3 != 0) goto L_0x00c5
            java.lang.NumberFormatException r5 = new java.lang.NumberFormatException
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Expected leading [0-9] or '-' character but was 0x"
            r6.append(r7)
            java.lang.String r7 = java.lang.Integer.toHexString(r14)
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r5.<init>(r6)
            throw r5
        L_0x00c5:
            r5 = 1
            goto L_0x00cd
        L_0x00c7:
            r19 = r5
            r17 = r6
            r20 = r11
        L_0x00cd:
            if (r12 != r13) goto L_0x00d9
            okio.Segment r6 = r10.pop()
            r0.head = r6
            okio.SegmentPool.recycle(r10)
            goto L_0x00db
        L_0x00d9:
            r10.pos = r12
        L_0x00db:
            if (r5 != 0) goto L_0x00e6
            okio.Segment r6 = r0.head
            if (r6 != 0) goto L_0x00e2
            goto L_0x00e6
        L_0x00e2:
            r6 = r17
            goto L_0x001e
        L_0x00e6:
            long r6 = r0.size
            long r10 = (long) r3
            long r12 = r6 - r10
            r0.size = r12
            if (r4 == 0) goto L_0x00f1
            r6 = r1
            goto L_0x00f2
        L_0x00f1:
            long r6 = -r1
        L_0x00f2:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.readDecimalLong():long");
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x00a4  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ae  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00a2 A[EDGE_INSN: B:45:0x00a2->B:31:0x00a2 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0020  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long readHexadecimalUnsignedLong() {
        /*
            r17 = this;
            r0 = r17
            long r1 = r0.size
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x0012
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.String r2 = "size == 0"
            r1.<init>(r2)
            throw r1
        L_0x0012:
            r1 = 0
            r5 = 0
            r6 = 0
        L_0x0016:
            okio.Segment r7 = r0.head
            byte[] r8 = r7.data
            int r9 = r7.pos
            int r10 = r7.limit
        L_0x001e:
            if (r9 >= r10) goto L_0x00a2
            byte r11 = r8[r9]
            r12 = 48
            if (r11 < r12) goto L_0x002d
            r12 = 57
            if (r11 > r12) goto L_0x002d
            int r12 = r11 + -48
        L_0x002c:
            goto L_0x0047
        L_0x002d:
            r12 = 97
            if (r11 < r12) goto L_0x003a
            r12 = 102(0x66, float:1.43E-43)
            if (r11 > r12) goto L_0x003a
            int r12 = r11 + -97
            int r12 = r12 + 10
            goto L_0x002c
        L_0x003a:
            r12 = 65
            if (r11 < r12) goto L_0x0083
            r12 = 70
            if (r11 > r12) goto L_0x0083
            int r12 = r11 + -65
            int r12 = r12 + 10
            goto L_0x002c
        L_0x0047:
            r13 = -1152921504606846976(0xf000000000000000, double:-3.105036184601418E231)
            long r15 = r1 & r13
            int r13 = (r15 > r3 ? 1 : (r15 == r3 ? 0 : -1))
            if (r13 == 0) goto L_0x0078
            okio.Buffer r3 = new okio.Buffer
            r3.<init>()
            okio.Buffer r3 = r3.writeHexadecimalUnsignedLong((long) r1)
            okio.Buffer r3 = r3.writeByte((int) r11)
            java.lang.NumberFormatException r4 = new java.lang.NumberFormatException
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "Number too large: "
            r13.append(r14)
            java.lang.String r14 = r3.readUtf8()
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            r4.<init>(r13)
            throw r4
        L_0x0078:
            r13 = 4
            long r1 = r1 << r13
            long r13 = (long) r12
            long r11 = r1 | r13
            int r9 = r9 + 1
            int r5 = r5 + 1
            r1 = r11
            goto L_0x001e
        L_0x0083:
            if (r5 != 0) goto L_0x00a0
            java.lang.NumberFormatException r3 = new java.lang.NumberFormatException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r12 = "Expected leading [0-9a-fA-F] character but was 0x"
            r4.append(r12)
            java.lang.String r12 = java.lang.Integer.toHexString(r11)
            r4.append(r12)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            throw r3
        L_0x00a0:
            r6 = 1
        L_0x00a2:
            if (r9 != r10) goto L_0x00ae
            okio.Segment r11 = r7.pop()
            r0.head = r11
            okio.SegmentPool.recycle(r7)
            goto L_0x00b0
        L_0x00ae:
            r7.pos = r9
        L_0x00b0:
            if (r6 != 0) goto L_0x00b6
            okio.Segment r7 = r0.head
            if (r7 != 0) goto L_0x0016
        L_0x00b6:
            long r3 = r0.size
            long r7 = (long) r5
            long r9 = r3 - r7
            r0.size = r9
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.readHexadecimalUnsignedLong():long");
    }

    public ByteString readByteString() {
        return new ByteString(readByteArray());
    }

    public ByteString readByteString(long byteCount) throws EOFException {
        return new ByteString(readByteArray(byteCount));
    }

    public int select(Options options) {
        Segment s = this.head;
        if (s == null) {
            return options.indexOf(ByteString.EMPTY);
        }
        ByteString[] byteStrings = options.byteStrings;
        int listSize = byteStrings.length;
        int i = 0;
        while (true) {
            int listSize2 = listSize;
            if (i >= listSize2) {
                return -1;
            }
            ByteString b = byteStrings[i];
            if (this.size >= ((long) b.size())) {
                if (rangeEquals(s, s.pos, b, 0, b.size())) {
                    try {
                        skip((long) b.size());
                        return i;
                    } catch (EOFException e) {
                        throw new AssertionError(e);
                    }
                }
            }
            i++;
            listSize = listSize2;
        }
    }

    /* access modifiers changed from: package-private */
    public int selectPrefix(Options options) {
        Segment s = this.head;
        ByteString[] byteStrings = options.byteStrings;
        int listSize = byteStrings.length;
        int i = 0;
        while (true) {
            int listSize2 = listSize;
            if (i < listSize2) {
                ByteString b = byteStrings[i];
                int bytesLimit = (int) Math.min(this.size, (long) b.size());
                if (bytesLimit == 0) {
                    break;
                }
                if (rangeEquals(s, s.pos, b, 0, bytesLimit)) {
                    break;
                }
                i++;
                listSize = listSize2;
            } else {
                return -1;
            }
        }
        return i;
    }

    public void readFully(Buffer sink, long byteCount) throws EOFException {
        if (this.size < byteCount) {
            sink.write(this, this.size);
            throw new EOFException();
        } else {
            sink.write(this, byteCount);
        }
    }

    public long readAll(Sink sink) throws IOException {
        long byteCount = this.size;
        if (byteCount > 0) {
            sink.write(this, byteCount);
        }
        return byteCount;
    }

    public String readUtf8() {
        try {
            return readString(this.size, Util.UTF_8);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public String readUtf8(long byteCount) throws EOFException {
        return readString(byteCount, Util.UTF_8);
    }

    public String readString(Charset charset) {
        try {
            return readString(this.size, charset);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public String readString(long byteCount, Charset charset) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0, byteCount);
        if (charset == null) {
            throw new IllegalArgumentException("charset == null");
        } else if (byteCount > 2147483647L) {
            throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + byteCount);
        } else if (byteCount == 0) {
            return "";
        } else {
            Segment s = this.head;
            if (((long) s.pos) + byteCount > ((long) s.limit)) {
                return new String(readByteArray(byteCount), charset);
            }
            String result = new String(s.data, s.pos, (int) byteCount, charset);
            s.pos = (int) (((long) s.pos) + byteCount);
            this.size -= byteCount;
            if (s.pos == s.limit) {
                this.head = s.pop();
                SegmentPool.recycle(s);
            }
            return result;
        }
    }

    @Nullable
    public String readUtf8Line() throws EOFException {
        long newline = indexOf((byte) 10);
        if (newline != -1) {
            return readUtf8Line(newline);
        }
        if (this.size != 0) {
            return readUtf8(this.size);
        }
        return null;
    }

    public String readUtf8LineStrict() throws EOFException {
        return readUtf8LineStrict(LongCompanionObject.MAX_VALUE);
    }

    public String readUtf8LineStrict(long limit) throws EOFException {
        if (limit < 0) {
            throw new IllegalArgumentException("limit < 0: " + limit);
        }
        long scanLength = LongCompanionObject.MAX_VALUE;
        if (limit != LongCompanionObject.MAX_VALUE) {
            scanLength = limit + 1;
        }
        long newline = indexOf((byte) 10, 0, scanLength);
        if (newline != -1) {
            return readUtf8Line(newline);
        }
        if (scanLength < size() && getByte(scanLength - 1) == 13 && getByte(scanLength) == 10) {
            return readUtf8Line(scanLength);
        }
        Buffer data = new Buffer();
        copyTo(data, 0, Math.min(32, size()));
        throw new EOFException("\\n not found: limit=" + Math.min(size(), limit) + " content=" + data.readByteString().hex() + Typography.ellipsis);
    }

    /* access modifiers changed from: package-private */
    public String readUtf8Line(long newline) throws EOFException {
        if (newline <= 0 || getByte(newline - 1) != 13) {
            String result = readUtf8(newline);
            skip(1);
            return result;
        }
        String result2 = readUtf8(newline - 1);
        skip(2);
        return result2;
    }

    public int readUtf8CodePoint() throws EOFException {
        int min;
        int byteCount;
        int codePoint;
        if (this.size == 0) {
            throw new EOFException();
        }
        int b0 = getByte(0);
        if ((b0 & 128) == 0) {
            codePoint = b0 & 127;
            byteCount = 1;
            min = 0;
        } else if ((b0 & 224) == 192) {
            codePoint = b0 & 31;
            byteCount = 2;
            min = 128;
        } else if ((b0 & 240) == 224) {
            codePoint = b0 & 15;
            byteCount = 3;
            min = 2048;
        } else if ((b0 & 248) == 240) {
            codePoint = b0 & 7;
            byteCount = 4;
            min = 65536;
        } else {
            skip(1);
            return REPLACEMENT_CHARACTER;
        }
        if (this.size < ((long) byteCount)) {
            throw new EOFException("size < " + byteCount + ": " + this.size + " (to read code point prefixed 0x" + Integer.toHexString(b0) + ")");
        }
        int i = 1;
        while (i < byteCount) {
            int b = getByte((long) i);
            if ((b & 192) == 128) {
                codePoint = (codePoint << 6) | (b & 63);
                i++;
            } else {
                skip((long) i);
                return REPLACEMENT_CHARACTER;
            }
        }
        skip((long) byteCount);
        if (codePoint > 1114111) {
            return REPLACEMENT_CHARACTER;
        }
        if ((codePoint < 55296 || codePoint > 57343) && codePoint >= min) {
            return codePoint;
        }
        return REPLACEMENT_CHARACTER;
    }

    public byte[] readByteArray() {
        try {
            return readByteArray(this.size);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public byte[] readByteArray(long byteCount) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0, byteCount);
        if (byteCount > 2147483647L) {
            throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + byteCount);
        }
        byte[] result = new byte[((int) byteCount)];
        readFully(result);
        return result;
    }

    public int read(byte[] sink) {
        return read(sink, 0, sink.length);
    }

    public void readFully(byte[] sink) throws EOFException {
        int offset = 0;
        while (offset < sink.length) {
            int read = read(sink, offset, sink.length - offset);
            if (read == -1) {
                throw new EOFException();
            }
            offset += read;
        }
    }

    public int read(byte[] sink, int offset, int byteCount) {
        Util.checkOffsetAndCount((long) sink.length, (long) offset, (long) byteCount);
        Segment s = this.head;
        if (s == null) {
            return -1;
        }
        int toCopy = Math.min(byteCount, s.limit - s.pos);
        System.arraycopy(s.data, s.pos, sink, offset, toCopy);
        s.pos += toCopy;
        this.size -= (long) toCopy;
        if (s.pos == s.limit) {
            this.head = s.pop();
            SegmentPool.recycle(s);
        }
        return toCopy;
    }

    public int read(ByteBuffer sink) throws IOException {
        Segment s = this.head;
        if (s == null) {
            return -1;
        }
        int toCopy = Math.min(sink.remaining(), s.limit - s.pos);
        sink.put(s.data, s.pos, toCopy);
        s.pos += toCopy;
        this.size -= (long) toCopy;
        if (s.pos == s.limit) {
            this.head = s.pop();
            SegmentPool.recycle(s);
        }
        return toCopy;
    }

    public void clear() {
        try {
            skip(this.size);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public void skip(long byteCount) throws EOFException {
        while (byteCount > 0) {
            if (this.head == null) {
                throw new EOFException();
            }
            int toSkip = (int) Math.min(byteCount, (long) (this.head.limit - this.head.pos));
            this.size -= (long) toSkip;
            long byteCount2 = byteCount - ((long) toSkip);
            this.head.pos += toSkip;
            if (this.head.pos == this.head.limit) {
                Segment toRecycle = this.head;
                this.head = toRecycle.pop();
                SegmentPool.recycle(toRecycle);
            }
            byteCount = byteCount2;
        }
    }

    public Buffer write(ByteString byteString) {
        if (byteString == null) {
            throw new IllegalArgumentException("byteString == null");
        }
        byteString.write(this);
        return this;
    }

    public Buffer writeUtf8(String string) {
        return writeUtf8(string, 0, string.length());
    }

    public Buffer writeUtf8(String string, int beginIndex, int endIndex) {
        String str = string;
        int i = beginIndex;
        int i2 = endIndex;
        if (str == null) {
            throw new IllegalArgumentException("string == null");
        } else if (i < 0) {
            throw new IllegalArgumentException("beginIndex < 0: " + i);
        } else if (i2 < i) {
            throw new IllegalArgumentException("endIndex < beginIndex: " + i2 + " < " + i);
        } else if (i2 > string.length()) {
            throw new IllegalArgumentException("endIndex > string.length: " + i2 + " > " + string.length());
        } else {
            int i3 = i;
            while (i3 < i2) {
                char charAt = str.charAt(i3);
                if (charAt < 128) {
                    Segment tail = writableSegment(1);
                    byte[] data = tail.data;
                    int segmentOffset = tail.limit - i3;
                    int runLimit = Math.min(i2, 8192 - segmentOffset);
                    int i4 = i3 + 1;
                    data[i3 + segmentOffset] = (byte) charAt;
                    while (i4 < runLimit) {
                        charAt = str.charAt(i4);
                        if (charAt >= 128) {
                            break;
                        }
                        data[i4 + segmentOffset] = (byte) charAt;
                        i4++;
                    }
                    int runSize = (i4 + segmentOffset) - tail.limit;
                    tail.limit += runSize;
                    int i5 = runSize;
                    char c = charAt;
                    this.size += (long) runSize;
                    i3 = i4;
                } else if (charAt < 2048) {
                    writeByte((charAt >> 6) | 192);
                    writeByte((int) 128 | (charAt & '?'));
                    i3++;
                } else if (charAt < 55296 || charAt > 57343) {
                    writeByte((charAt >> 12) | 224);
                    writeByte(((charAt >> 6) & 63) | 128);
                    writeByte((int) 128 | (charAt & '?'));
                    i3++;
                } else {
                    int low = i3 + 1 < i2 ? str.charAt(i3 + 1) : 0;
                    if (charAt > 56319 || low < 56320 || low > 57343) {
                        writeByte(63);
                        i3++;
                    } else {
                        int codePoint = (((10239 & charAt) << 10) | (-56321 & low)) + 65536;
                        writeByte((codePoint >> 18) | 240);
                        writeByte(((codePoint >> 12) & 63) | 128);
                        writeByte(((codePoint >> 6) & 63) | 128);
                        writeByte(128 | (codePoint & 63));
                        i3 += 2;
                    }
                }
            }
            return this;
        }
    }

    public Buffer writeUtf8CodePoint(int codePoint) {
        if (codePoint < 128) {
            writeByte(codePoint);
        } else if (codePoint < 2048) {
            writeByte((codePoint >> 6) | 192);
            writeByte(128 | (codePoint & 63));
        } else if (codePoint < 65536) {
            if (codePoint < 55296 || codePoint > 57343) {
                writeByte((codePoint >> 12) | 224);
                writeByte(((codePoint >> 6) & 63) | 128);
                writeByte(128 | (codePoint & 63));
            } else {
                writeByte(63);
            }
        } else if (codePoint <= 1114111) {
            writeByte((codePoint >> 18) | 240);
            writeByte(((codePoint >> 12) & 63) | 128);
            writeByte(((codePoint >> 6) & 63) | 128);
            writeByte(128 | (codePoint & 63));
        } else {
            throw new IllegalArgumentException("Unexpected code point: " + Integer.toHexString(codePoint));
        }
        return this;
    }

    public Buffer writeString(String string, Charset charset) {
        return writeString(string, 0, string.length(), charset);
    }

    public Buffer writeString(String string, int beginIndex, int endIndex, Charset charset) {
        if (string == null) {
            throw new IllegalArgumentException("string == null");
        } else if (beginIndex < 0) {
            throw new IllegalAccessError("beginIndex < 0: " + beginIndex);
        } else if (endIndex < beginIndex) {
            throw new IllegalArgumentException("endIndex < beginIndex: " + endIndex + " < " + beginIndex);
        } else if (endIndex > string.length()) {
            throw new IllegalArgumentException("endIndex > string.length: " + endIndex + " > " + string.length());
        } else if (charset == null) {
            throw new IllegalArgumentException("charset == null");
        } else if (charset.equals(Util.UTF_8)) {
            return writeUtf8(string, beginIndex, endIndex);
        } else {
            byte[] data = string.substring(beginIndex, endIndex).getBytes(charset);
            return write(data, 0, data.length);
        }
    }

    public Buffer write(byte[] source) {
        if (source != null) {
            return write(source, 0, source.length);
        }
        throw new IllegalArgumentException("source == null");
    }

    public Buffer write(byte[] source, int offset, int byteCount) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        Util.checkOffsetAndCount((long) source.length, (long) offset, (long) byteCount);
        int limit = offset + byteCount;
        while (offset < limit) {
            Segment tail = writableSegment(1);
            int toCopy = Math.min(limit - offset, 8192 - tail.limit);
            System.arraycopy(source, offset, tail.data, tail.limit, toCopy);
            offset += toCopy;
            tail.limit += toCopy;
        }
        this.size += (long) byteCount;
        return this;
    }

    public int write(ByteBuffer source) throws IOException {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        int byteCount = source.remaining();
        int remaining = byteCount;
        while (remaining > 0) {
            Segment tail = writableSegment(1);
            int toCopy = Math.min(remaining, 8192 - tail.limit);
            source.get(tail.data, tail.limit, toCopy);
            remaining -= toCopy;
            tail.limit += toCopy;
        }
        this.size += (long) byteCount;
        return byteCount;
    }

    public long writeAll(Source source) throws IOException {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        long totalBytesRead = 0;
        while (true) {
            long read = source.read(this, 8192);
            long readCount = read;
            if (read == -1) {
                return totalBytesRead;
            }
            totalBytesRead += readCount;
        }
    }

    public BufferedSink write(Source source, long byteCount) throws IOException {
        while (byteCount > 0) {
            long read = source.read(this, byteCount);
            if (read == -1) {
                throw new EOFException();
            }
            byteCount -= read;
        }
        return this;
    }

    public Buffer writeByte(int b) {
        Segment tail = writableSegment(1);
        byte[] bArr = tail.data;
        int i = tail.limit;
        tail.limit = i + 1;
        bArr[i] = (byte) b;
        this.size++;
        return this;
    }

    public Buffer writeShort(int s) {
        Segment tail = writableSegment(2);
        byte[] data = tail.data;
        int limit = tail.limit;
        int limit2 = limit + 1;
        data[limit] = (byte) ((s >>> 8) & 255);
        data[limit2] = (byte) (s & 255);
        tail.limit = limit2 + 1;
        this.size += 2;
        return this;
    }

    public Buffer writeShortLe(int s) {
        return writeShort((int) Util.reverseBytesShort((short) s));
    }

    public Buffer writeInt(int i) {
        Segment tail = writableSegment(4);
        byte[] data = tail.data;
        int limit = tail.limit;
        int limit2 = limit + 1;
        data[limit] = (byte) ((i >>> 24) & 255);
        int limit3 = limit2 + 1;
        data[limit2] = (byte) ((i >>> 16) & 255);
        int limit4 = limit3 + 1;
        data[limit3] = (byte) ((i >>> 8) & 255);
        data[limit4] = (byte) (i & 255);
        tail.limit = limit4 + 1;
        this.size += 4;
        return this;
    }

    public Buffer writeIntLe(int i) {
        return writeInt(Util.reverseBytesInt(i));
    }

    public Buffer writeLong(long v) {
        Segment tail = writableSegment(8);
        byte[] data = tail.data;
        int limit = tail.limit;
        int limit2 = limit + 1;
        data[limit] = (byte) ((int) ((v >>> 56) & 255));
        int limit3 = limit2 + 1;
        data[limit2] = (byte) ((int) ((v >>> 48) & 255));
        int limit4 = limit3 + 1;
        data[limit3] = (byte) ((int) ((v >>> 40) & 255));
        int limit5 = limit4 + 1;
        data[limit4] = (byte) ((int) ((v >>> 32) & 255));
        int limit6 = limit5 + 1;
        data[limit5] = (byte) ((int) ((v >>> 24) & 255));
        int limit7 = limit6 + 1;
        data[limit6] = (byte) ((int) ((v >>> 16) & 255));
        int limit8 = limit7 + 1;
        data[limit7] = (byte) ((int) ((v >>> 8) & 255));
        data[limit8] = (byte) ((int) (v & 255));
        tail.limit = limit8 + 1;
        this.size += 8;
        return this;
    }

    public Buffer writeLongLe(long v) {
        return writeLong(Util.reverseBytesLong(v));
    }

    public Buffer writeDecimalLong(long v) {
        int width;
        if (v == 0) {
            return writeByte(48);
        }
        boolean negative = false;
        if (v < 0) {
            v = -v;
            if (v < 0) {
                return writeUtf8("-9223372036854775808");
            }
            negative = true;
        }
        if (v < 100000000) {
            width = v < 10000 ? v < 100 ? v < 10 ? 1 : 2 : v < 1000 ? 3 : 4 : v < 1000000 ? v < 100000 ? 5 : 6 : v < 10000000 ? 7 : 8;
        } else if (v < 1000000000000L) {
            width = v < 10000000000L ? v < 1000000000 ? 9 : 10 : v < 100000000000L ? 11 : 12;
        } else if (v < 1000000000000000L) {
            width = v < 10000000000000L ? 13 : v < 100000000000000L ? 14 : 15;
        } else {
            width = v < 100000000000000000L ? v < 10000000000000000L ? 16 : 17 : v < 1000000000000000000L ? 18 : 19;
        }
        if (negative) {
            width++;
        }
        Segment tail = writableSegment(width);
        byte[] data = tail.data;
        int pos = tail.limit + width;
        while (v != 0) {
            pos--;
            data[pos] = DIGITS[(int) (v % 10)];
            v /= 10;
        }
        if (negative) {
            data[pos - 1] = 45;
        }
        tail.limit += width;
        this.size += (long) width;
        return this;
    }

    public Buffer writeHexadecimalUnsignedLong(long v) {
        if (v == 0) {
            return writeByte(48);
        }
        int width = (Long.numberOfTrailingZeros(Long.highestOneBit(v)) / 4) + 1;
        Segment tail = writableSegment(width);
        byte[] data = tail.data;
        int start = tail.limit;
        for (int pos = (tail.limit + width) - 1; pos >= start; pos--) {
            data[pos] = DIGITS[(int) (v & 15)];
            v >>>= 4;
        }
        tail.limit += width;
        this.size += (long) width;
        return this;
    }

    /* access modifiers changed from: package-private */
    public Segment writableSegment(int minimumCapacity) {
        if (minimumCapacity < 1 || minimumCapacity > 8192) {
            throw new IllegalArgumentException();
        } else if (this.head == null) {
            this.head = SegmentPool.take();
            Segment segment = this.head;
            Segment segment2 = this.head;
            Segment segment3 = this.head;
            segment2.prev = segment3;
            segment.next = segment3;
            return segment3;
        } else {
            Segment tail = this.head.prev;
            if (tail.limit + minimumCapacity > 8192 || !tail.owner) {
                return tail.push(SegmentPool.take());
            }
            return tail;
        }
    }

    public void write(Buffer source, long byteCount) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        } else if (source == this) {
            throw new IllegalArgumentException("source == this");
        } else {
            Util.checkOffsetAndCount(source.size, 0, byteCount);
            while (byteCount > 0) {
                if (byteCount < ((long) (source.head.limit - source.head.pos))) {
                    Segment tail = this.head != null ? this.head.prev : null;
                    if (tail != null && tail.owner) {
                        if ((byteCount + ((long) tail.limit)) - ((long) (tail.shared ? 0 : tail.pos)) <= 8192) {
                            source.head.writeTo(tail, (int) byteCount);
                            source.size -= byteCount;
                            this.size += byteCount;
                            return;
                        }
                    }
                    source.head = source.head.split((int) byteCount);
                }
                Segment segmentToMove = source.head;
                long movedByteCount = (long) (segmentToMove.limit - segmentToMove.pos);
                source.head = segmentToMove.pop();
                if (this.head == null) {
                    this.head = segmentToMove;
                    Segment segment = this.head;
                    Segment segment2 = this.head;
                    Segment segment3 = this.head;
                    segment2.prev = segment3;
                    segment.next = segment3;
                } else {
                    this.head.prev.push(segmentToMove).compact();
                }
                source.size -= movedByteCount;
                this.size += movedByteCount;
                byteCount -= movedByteCount;
            }
        }
    }

    public long read(Buffer sink, long byteCount) {
        if (sink == null) {
            throw new IllegalArgumentException("sink == null");
        } else if (byteCount < 0) {
            throw new IllegalArgumentException("byteCount < 0: " + byteCount);
        } else if (this.size == 0) {
            return -1;
        } else {
            if (byteCount > this.size) {
                byteCount = this.size;
            }
            sink.write(this, byteCount);
            return byteCount;
        }
    }

    public long indexOf(byte b) {
        return indexOf(b, 0, LongCompanionObject.MAX_VALUE);
    }

    public long indexOf(byte b, long fromIndex) {
        return indexOf(b, fromIndex, LongCompanionObject.MAX_VALUE);
    }

    public long indexOf(byte b, long fromIndex, long toIndex) {
        long toIndex2;
        Segment s;
        long offset;
        long offset2 = 0;
        if (fromIndex < 0 || toIndex < fromIndex) {
            byte b2 = b;
            throw new IllegalArgumentException(String.format("size=%s fromIndex=%s toIndex=%s", new Object[]{Long.valueOf(this.size), Long.valueOf(fromIndex), Long.valueOf(toIndex)}));
        }
        if (toIndex > this.size) {
            toIndex2 = this.size;
        } else {
            toIndex2 = toIndex;
        }
        if (fromIndex == toIndex2 || (s = this.head) == null) {
            return -1;
        }
        if (this.size - fromIndex >= fromIndex) {
            while (true) {
                long j = offset + ((long) (s.limit - s.pos));
                long nextOffset = j;
                if (j >= fromIndex) {
                    break;
                }
                s = s.next;
                offset2 = nextOffset;
            }
        } else {
            offset = this.size;
            while (offset > fromIndex) {
                s = s.prev;
                offset -= (long) (s.limit - s.pos);
            }
        }
        long fromIndex2 = fromIndex;
        while (offset < toIndex2) {
            byte[] data = s.data;
            int limit = (int) Math.min((long) s.limit, (((long) s.pos) + toIndex2) - offset);
            for (int pos = (int) ((((long) s.pos) + fromIndex2) - offset); pos < limit; pos++) {
                if (data[pos] == b) {
                    return ((long) (pos - s.pos)) + offset;
                }
            }
            byte b3 = b;
            long offset3 = offset + ((long) (s.limit - s.pos));
            fromIndex2 = offset3;
            s = s.next;
            offset = offset3;
        }
        byte b4 = b;
        return -1;
    }

    public long indexOf(ByteString bytes) throws IOException {
        return indexOf(bytes, 0);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00bf, code lost:
        r18 = r3;
        r21 = r4;
        r7 = r5;
        r2 = r12 + ((long) (r7.limit - r7.pos));
        r16 = r2;
        r5 = r7.next;
        r12 = r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long indexOf(okio.ByteString r24, long r25) throws java.io.IOException {
        /*
            r23 = this;
            r6 = r23
            int r2 = r24.size()
            if (r2 != 0) goto L_0x0010
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "bytes is empty"
            r2.<init>(r3)
            throw r2
        L_0x0010:
            r2 = 0
            int r4 = (r25 > r2 ? 1 : (r25 == r2 ? 0 : -1))
            if (r4 >= 0) goto L_0x001e
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "fromIndex < 0"
            r2.<init>(r3)
            throw r2
        L_0x001e:
            okio.Segment r4 = r6.head
            r7 = -1
            if (r4 != 0) goto L_0x0025
            return r7
        L_0x0025:
            long r9 = r6.size
            long r11 = r9 - r25
            int r5 = (r11 > r25 ? 1 : (r11 == r25 ? 0 : -1))
            if (r5 >= 0) goto L_0x003f
            long r2 = r6.size
        L_0x002f:
            int r5 = (r2 > r25 ? 1 : (r2 == r25 ? 0 : -1))
            if (r5 <= 0) goto L_0x0051
            okio.Segment r4 = r4.prev
            int r5 = r4.limit
            int r9 = r4.pos
            int r5 = r5 - r9
            long r9 = (long) r5
            long r11 = r2 - r9
            r2 = r11
            goto L_0x002f
        L_0x003f:
        L_0x0040:
            int r5 = r4.limit
            int r9 = r4.pos
            int r5 = r5 - r9
            long r9 = (long) r5
            long r11 = r2 + r9
            r9 = r11
            int r5 = (r11 > r25 ? 1 : (r11 == r25 ? 0 : -1))
            if (r5 >= 0) goto L_0x0051
            okio.Segment r4 = r4.next
            r2 = r9
            goto L_0x0040
        L_0x0051:
            r5 = 0
            r9 = r24
            byte r10 = r9.getByte(r5)
            int r11 = r24.size()
            long r12 = r6.size
            long r14 = (long) r11
            long r16 = r12 - r14
            r12 = 1
            long r14 = r16 + r12
            r16 = r25
            r12 = r2
            r5 = r4
        L_0x0069:
            int r0 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r0 >= 0) goto L_0x00d5
            byte[] r4 = r5.data
            int r0 = r5.limit
            long r0 = (long) r0
            int r2 = r5.pos
            long r2 = (long) r2
            long r18 = r2 + r14
            long r2 = r18 - r12
            long r0 = java.lang.Math.min(r0, r2)
            int r3 = (int) r0
            int r0 = r5.pos
            long r0 = (long) r0
            long r18 = r0 + r16
            long r0 = r18 - r12
            int r0 = (int) r0
        L_0x0086:
            r2 = r0
            if (r2 >= r3) goto L_0x00bf
            byte r0 = r4[r2]
            if (r0 != r10) goto L_0x00ae
            int r18 = r2 + 1
            r19 = 1
            r0 = r6
            r1 = r5
            r20 = r2
            r2 = r18
            r18 = r3
            r3 = r9
            r21 = r4
            r4 = r19
            r7 = r5
            r5 = r11
            boolean r0 = r0.rangeEquals(r1, r2, r3, r4, r5)
            if (r0 == 0) goto L_0x00b5
            int r0 = r7.pos
            int r2 = r20 - r0
            long r0 = (long) r2
            long r2 = r0 + r12
            return r2
        L_0x00ae:
            r20 = r2
            r18 = r3
            r21 = r4
            r7 = r5
        L_0x00b5:
            int r0 = r20 + 1
            r5 = r7
            r3 = r18
            r4 = r21
            r7 = -1
            goto L_0x0086
        L_0x00bf:
            r18 = r3
            r21 = r4
            r7 = r5
            int r0 = r7.limit
            int r1 = r7.pos
            int r0 = r0 - r1
            long r0 = (long) r0
            long r2 = r12 + r0
            r16 = r2
            okio.Segment r5 = r7.next
            r12 = r2
            r7 = -1
            goto L_0x0069
        L_0x00d5:
            r7 = r5
            r0 = -1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.indexOf(okio.ByteString, long):long");
    }

    public long indexOfElement(ByteString targetBytes) {
        return indexOfElement(targetBytes, 0);
    }

    public long indexOfElement(ByteString targetBytes, long fromIndex) {
        long offset;
        Buffer buffer = this;
        ByteString byteString = targetBytes;
        long offset2 = 0;
        if (fromIndex < 0) {
            throw new IllegalArgumentException("fromIndex < 0");
        }
        Segment s = buffer.head;
        if (s == null) {
            return -1;
        }
        if (buffer.size - fromIndex >= fromIndex) {
            while (true) {
                long j = offset + ((long) (s.limit - s.pos));
                long nextOffset = j;
                if (j >= fromIndex) {
                    break;
                }
                s = s.next;
                offset2 = nextOffset;
            }
        } else {
            offset = buffer.size;
            while (offset > fromIndex) {
                s = s.prev;
                offset -= (long) (s.limit - s.pos);
            }
        }
        int i = 0;
        if (targetBytes.size() == 2) {
            byte b0 = byteString.getByte(0);
            byte b1 = byteString.getByte(1);
            long fromIndex2 = fromIndex;
            while (offset < buffer.size) {
                byte[] data = s.data;
                int limit = s.limit;
                for (int pos = (int) ((((long) s.pos) + fromIndex2) - offset); pos < limit; pos++) {
                    byte b = data[pos];
                    if (b == b0 || b == b1) {
                        return ((long) (pos - s.pos)) + offset;
                    }
                }
                long offset3 = offset + ((long) (s.limit - s.pos));
                fromIndex2 = offset3;
                s = s.next;
                offset = offset3;
            }
            return -1;
        }
        byte[] targetByteArray = targetBytes.internalArray();
        long fromIndex3 = fromIndex;
        while (offset < buffer.size) {
            byte[] data2 = s.data;
            int pos2 = (int) ((((long) s.pos) + fromIndex3) - offset);
            int limit2 = s.limit;
            while (pos2 < limit2) {
                byte b2 = data2[pos2];
                int length = targetByteArray.length;
                while (i < length) {
                    if (b2 == targetByteArray[i]) {
                        return ((long) (pos2 - s.pos)) + offset;
                    }
                    i++;
                    ByteString byteString2 = targetBytes;
                }
                pos2++;
                ByteString byteString3 = targetBytes;
                i = 0;
            }
            long offset4 = offset + ((long) (s.limit - s.pos));
            fromIndex3 = offset4;
            s = s.next;
            offset = offset4;
            buffer = this;
            ByteString byteString4 = targetBytes;
            i = 0;
        }
        return -1;
    }

    public boolean rangeEquals(long offset, ByteString bytes) {
        return rangeEquals(offset, bytes, 0, bytes.size());
    }

    public boolean rangeEquals(long offset, ByteString bytes, int bytesOffset, int byteCount) {
        if (offset < 0 || bytesOffset < 0 || byteCount < 0 || this.size - offset < ((long) byteCount) || bytes.size() - bytesOffset < byteCount) {
            return false;
        }
        for (int i = 0; i < byteCount; i++) {
            if (getByte(offset + ((long) i)) != bytes.getByte(bytesOffset + i)) {
                return false;
            }
        }
        return true;
    }

    private boolean rangeEquals(Segment segment, int segmentPos, ByteString bytes, int bytesOffset, int bytesLimit) {
        int segmentLimit = segment.limit;
        byte[] data = segment.data;
        Segment segment2 = segment;
        for (int i = bytesOffset; i < bytesLimit; i++) {
            if (segmentPos == segmentLimit) {
                segment2 = segment2.next;
                data = segment2.data;
                segmentPos = segment2.pos;
                segmentLimit = segment2.limit;
            }
            if (data[segmentPos] != bytes.getByte(i)) {
                return false;
            }
            segmentPos++;
        }
        return true;
    }

    public void flush() {
    }

    public boolean isOpen() {
        return true;
    }

    public void close() {
    }

    public Timeout timeout() {
        return Timeout.NONE;
    }

    /* access modifiers changed from: package-private */
    public List<Integer> segmentSizes() {
        if (this.head == null) {
            return Collections.emptyList();
        }
        List<Integer> result = new ArrayList<>();
        result.add(Integer.valueOf(this.head.limit - this.head.pos));
        Segment s = this.head;
        while (true) {
            s = s.next;
            if (s == this.head) {
                return result;
            }
            result.add(Integer.valueOf(s.limit - s.pos));
        }
    }

    public ByteString md5() {
        return digest("MD5");
    }

    public ByteString sha1() {
        return digest("SHA-1");
    }

    public ByteString sha256() {
        return digest("SHA-256");
    }

    public ByteString sha512() {
        return digest("SHA-512");
    }

    private ByteString digest(String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            if (this.head != null) {
                messageDigest.update(this.head.data, this.head.pos, this.head.limit - this.head.pos);
                for (Segment s = this.head.next; s != this.head; s = s.next) {
                    messageDigest.update(s.data, s.pos, s.limit - s.pos);
                }
            }
            return ByteString.of(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError();
        }
    }

    public ByteString hmacSha1(ByteString key) {
        return hmac("HmacSHA1", key);
    }

    public ByteString hmacSha256(ByteString key) {
        return hmac("HmacSHA256", key);
    }

    public ByteString hmacSha512(ByteString key) {
        return hmac("HmacSHA512", key);
    }

    private ByteString hmac(String algorithm, ByteString key) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key.toByteArray(), algorithm));
            if (this.head != null) {
                mac.update(this.head.data, this.head.pos, this.head.limit - this.head.pos);
                for (Segment s = this.head.next; s != this.head; s = s.next) {
                    mac.update(s.data, s.pos, s.limit - s.pos);
                }
            }
            return ByteString.of(mac.doFinal());
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError();
        } catch (InvalidKeyException e2) {
            throw new IllegalArgumentException(e2);
        }
    }

    /* JADX WARNING: type inference failed for: r18v0, types: [java.lang.Object] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r18) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = 1
            if (r0 != r1) goto L_0x0008
            return r2
        L_0x0008:
            boolean r3 = r1 instanceof okio.Buffer
            r4 = 0
            if (r3 != 0) goto L_0x000e
            return r4
        L_0x000e:
            r3 = r1
            okio.Buffer r3 = (okio.Buffer) r3
            long r5 = r0.size
            long r7 = r3.size
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x001a
            return r4
        L_0x001a:
            long r5 = r0.size
            r7 = 0
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x0023
            return r2
        L_0x0023:
            okio.Segment r5 = r0.head
            okio.Segment r6 = r3.head
            int r9 = r5.pos
            int r10 = r6.pos
        L_0x002c:
            long r11 = r0.size
            int r13 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r13 >= 0) goto L_0x0073
            int r11 = r5.limit
            int r11 = r11 - r9
            int r12 = r6.limit
            int r12 = r12 - r10
            int r11 = java.lang.Math.min(r11, r12)
            long r11 = (long) r11
            r13 = r10
            r10 = r9
            r9 = 0
        L_0x0040:
            long r14 = (long) r9
            int r16 = (r14 > r11 ? 1 : (r14 == r11 ? 0 : -1))
            if (r16 >= 0) goto L_0x005a
            byte[] r14 = r5.data
            int r15 = r10 + 1
            byte r10 = r14[r10]
            byte[] r14 = r6.data
            int r16 = r13 + 1
            byte r13 = r14[r13]
            if (r10 == r13) goto L_0x0054
            return r4
        L_0x0054:
            int r9 = r9 + 1
            r10 = r15
            r13 = r16
            goto L_0x0040
        L_0x005a:
            int r9 = r5.limit
            if (r10 != r9) goto L_0x0063
            okio.Segment r5 = r5.next
            int r9 = r5.pos
            goto L_0x0064
        L_0x0063:
            r9 = r10
        L_0x0064:
            int r10 = r6.limit
            if (r13 != r10) goto L_0x006d
            okio.Segment r6 = r6.next
            int r10 = r6.pos
            goto L_0x006e
        L_0x006d:
            r10 = r13
        L_0x006e:
            r13 = 0
            long r13 = r7 + r11
            r7 = r13
            goto L_0x002c
        L_0x0073:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        Segment s = this.head;
        if (s == null) {
            return 0;
        }
        int result = 1;
        do {
            int limit = s.limit;
            for (int pos = s.pos; pos < limit; pos++) {
                result = (result * 31) + s.data[pos];
            }
            s = s.next;
        } while (s != this.head);
        return result;
    }

    public String toString() {
        return snapshot().toString();
    }

    public Buffer clone() {
        Buffer result = new Buffer();
        if (this.size == 0) {
            return result;
        }
        result.head = this.head.sharedCopy();
        Segment segment = result.head;
        Segment segment2 = result.head;
        Segment segment3 = result.head;
        segment2.prev = segment3;
        segment.next = segment3;
        Segment s = this.head;
        while (true) {
            s = s.next;
            if (s != this.head) {
                result.head.prev.push(s.sharedCopy());
            } else {
                result.size = this.size;
                return result;
            }
        }
    }

    public ByteString snapshot() {
        if (this.size <= 2147483647L) {
            return snapshot((int) this.size);
        }
        throw new IllegalArgumentException("size > Integer.MAX_VALUE: " + this.size);
    }

    public ByteString snapshot(int byteCount) {
        if (byteCount == 0) {
            return ByteString.EMPTY;
        }
        return new SegmentedByteString(this, byteCount);
    }

    public UnsafeCursor readUnsafe() {
        return readUnsafe(new UnsafeCursor());
    }

    public UnsafeCursor readUnsafe(UnsafeCursor unsafeCursor) {
        if (unsafeCursor.buffer != null) {
            throw new IllegalStateException("already attached to a buffer");
        }
        unsafeCursor.buffer = this;
        unsafeCursor.readWrite = false;
        return unsafeCursor;
    }

    public UnsafeCursor readAndWriteUnsafe() {
        return readAndWriteUnsafe(new UnsafeCursor());
    }

    public UnsafeCursor readAndWriteUnsafe(UnsafeCursor unsafeCursor) {
        if (unsafeCursor.buffer != null) {
            throw new IllegalStateException("already attached to a buffer");
        }
        unsafeCursor.buffer = this;
        unsafeCursor.readWrite = true;
        return unsafeCursor;
    }

    public static final class UnsafeCursor implements Closeable {
        public Buffer buffer;
        public byte[] data;
        public int end = -1;
        public long offset = -1;
        public boolean readWrite;
        private Segment segment;
        public int start = -1;

        public int next() {
            if (this.offset == this.buffer.size) {
                throw new IllegalStateException();
            } else if (this.offset == -1) {
                return seek(0);
            } else {
                return seek(this.offset + ((long) (this.end - this.start)));
            }
        }

        public int seek(long offset2) {
            Segment next;
            long nextOffset;
            if (offset2 < -1 || offset2 > this.buffer.size) {
                throw new ArrayIndexOutOfBoundsException(String.format("offset=%s > size=%s", new Object[]{Long.valueOf(offset2), Long.valueOf(this.buffer.size)}));
            } else if (offset2 == -1 || offset2 == this.buffer.size) {
                this.segment = null;
                this.offset = offset2;
                this.data = null;
                this.start = -1;
                this.end = -1;
                return -1;
            } else {
                long min = 0;
                long max = this.buffer.size;
                Segment head = this.buffer.head;
                Segment tail = this.buffer.head;
                if (this.segment != null) {
                    long segmentOffset = this.offset - ((long) (this.start - this.segment.pos));
                    if (segmentOffset > offset2) {
                        max = segmentOffset;
                        tail = this.segment;
                    } else {
                        min = segmentOffset;
                        head = this.segment;
                    }
                }
                if (max - offset2 > offset2 - min) {
                    next = head;
                    nextOffset = min;
                    while (offset2 >= nextOffset + ((long) (next.limit - next.pos))) {
                        next = next.next;
                        nextOffset += (long) (next.limit - next.pos);
                    }
                } else {
                    Segment next2 = tail;
                    long nextOffset2 = max;
                    while (nextOffset > offset2) {
                        next2 = next.prev;
                        nextOffset2 = nextOffset - ((long) (next2.limit - next2.pos));
                    }
                }
                if (this.readWrite && next.shared) {
                    Segment unsharedNext = next.unsharedCopy();
                    if (this.buffer.head == next) {
                        this.buffer.head = unsharedNext;
                    }
                    next = next.push(unsharedNext);
                    next.prev.pop();
                }
                this.segment = next;
                this.offset = offset2;
                this.data = next.data;
                this.start = next.pos + ((int) (offset2 - nextOffset));
                this.end = next.limit;
                return this.end - this.start;
            }
        }

        public long resizeBuffer(long newSize) {
            if (this.buffer == null) {
                throw new IllegalStateException("not attached to a buffer");
            } else if (!this.readWrite) {
                throw new IllegalStateException("resizeBuffer() only permitted for read/write buffers");
            } else {
                long oldSize = this.buffer.size;
                if (newSize <= oldSize) {
                    if (newSize < 0) {
                        throw new IllegalArgumentException("newSize < 0: " + newSize);
                    }
                    long bytesToSubtract = oldSize - newSize;
                    while (true) {
                        if (bytesToSubtract <= 0) {
                            break;
                        }
                        Segment tail = this.buffer.head.prev;
                        int tailSize = tail.limit - tail.pos;
                        if (((long) tailSize) > bytesToSubtract) {
                            tail.limit = (int) (((long) tail.limit) - bytesToSubtract);
                            break;
                        }
                        this.buffer.head = tail.pop();
                        SegmentPool.recycle(tail);
                        bytesToSubtract -= (long) tailSize;
                    }
                    this.segment = null;
                    this.offset = newSize;
                    this.data = null;
                    this.start = -1;
                    this.end = -1;
                } else if (newSize > oldSize) {
                    boolean needsToSeek = true;
                    long bytesToAdd = newSize - oldSize;
                    while (bytesToAdd > 0) {
                        Segment tail2 = this.buffer.writableSegment(1);
                        int segmentBytesToAdd = (int) Math.min(bytesToAdd, (long) (8192 - tail2.limit));
                        tail2.limit += segmentBytesToAdd;
                        long bytesToAdd2 = bytesToAdd - ((long) segmentBytesToAdd);
                        if (needsToSeek) {
                            this.segment = tail2;
                            this.offset = oldSize;
                            this.data = tail2.data;
                            this.start = tail2.limit - segmentBytesToAdd;
                            this.end = tail2.limit;
                            needsToSeek = false;
                        }
                        bytesToAdd = bytesToAdd2;
                    }
                }
                this.buffer.size = newSize;
                return oldSize;
            }
        }

        public long expandBuffer(int minByteCount) {
            if (minByteCount <= 0) {
                throw new IllegalArgumentException("minByteCount <= 0: " + minByteCount);
            } else if (minByteCount > 8192) {
                throw new IllegalArgumentException("minByteCount > Segment.SIZE: " + minByteCount);
            } else if (this.buffer == null) {
                throw new IllegalStateException("not attached to a buffer");
            } else if (!this.readWrite) {
                throw new IllegalStateException("expandBuffer() only permitted for read/write buffers");
            } else {
                long oldSize = this.buffer.size;
                Segment tail = this.buffer.writableSegment(minByteCount);
                int result = 8192 - tail.limit;
                tail.limit = 8192;
                this.buffer.size = oldSize + ((long) result);
                this.segment = tail;
                this.offset = oldSize;
                this.data = tail.data;
                this.start = 8192 - result;
                this.end = 8192;
                return (long) result;
            }
        }

        public void close() {
            if (this.buffer == null) {
                throw new IllegalStateException("not attached to a buffer");
            }
            this.buffer = null;
            this.segment = null;
            this.offset = -1;
            this.data = null;
            this.start = -1;
            this.end = -1;
        }
    }
}
