package okio;

import com.bumptech.glide.load.Key;
import java.nio.charset.Charset;

final class Util {
    public static final Charset UTF_8 = Charset.forName(Key.STRING_CHARSET_NAME);

    private Util() {
    }

    public static void checkOffsetAndCount(long size, long offset, long byteCount) {
        if ((offset | byteCount) < 0 || offset > size || size - offset < byteCount) {
            throw new ArrayIndexOutOfBoundsException(String.format("size=%s offset=%s byteCount=%s", new Object[]{Long.valueOf(size), Long.valueOf(offset), Long.valueOf(byteCount)}));
        }
    }

    public static short reverseBytesShort(short s) {
        int i = 65535 & s;
        return (short) (((65280 & i) >>> 8) | ((i & 255) << 8));
    }

    public static int reverseBytesInt(int i) {
        return ((-16777216 & i) >>> 24) | ((16711680 & i) >>> 8) | ((65280 & i) << 8) | ((i & 255) << 24);
    }

    public static long reverseBytesLong(long v) {
        return ((v & -72057594037927936L) >>> 56) | ((v & 71776119061217280L) >>> 40) | ((v & 280375465082880L) >>> 24) | ((v & 1095216660480L) >>> 8) | ((v & 4278190080L) << 8) | ((v & 16711680) << 24) | ((v & 65280) << 40) | ((v & 255) << 56);
    }

    public static void sneakyRethrow(Throwable t) {
        sneakyThrow2(t);
    }

    private static <T extends Throwable> void sneakyThrow2(Throwable t) throws Throwable {
        throw t;
    }

    public static boolean arrayRangeEquals(byte[] a, int aOffset, byte[] b, int bOffset, int byteCount) {
        for (int i = 0; i < byteCount; i++) {
            if (a[i + aOffset] != b[i + bOffset]) {
                return false;
            }
        }
        return true;
    }
}
