package okhttp3;

import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

public final class CacheControl {
    public static final CacheControl FORCE_CACHE = new Builder().onlyIfCached().maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS).build();
    public static final CacheControl FORCE_NETWORK = new Builder().noCache().build();
    @Nullable
    String headerValue;
    private final boolean immutable;
    private final boolean isPrivate;
    private final boolean isPublic;
    private final int maxAgeSeconds;
    private final int maxStaleSeconds;
    private final int minFreshSeconds;
    private final boolean mustRevalidate;
    private final boolean noCache;
    private final boolean noStore;
    private final boolean noTransform;
    private final boolean onlyIfCached;
    private final int sMaxAgeSeconds;

    private CacheControl(boolean noCache2, boolean noStore2, int maxAgeSeconds2, int sMaxAgeSeconds2, boolean isPrivate2, boolean isPublic2, boolean mustRevalidate2, int maxStaleSeconds2, int minFreshSeconds2, boolean onlyIfCached2, boolean noTransform2, boolean immutable2, @Nullable String headerValue2) {
        this.noCache = noCache2;
        this.noStore = noStore2;
        this.maxAgeSeconds = maxAgeSeconds2;
        this.sMaxAgeSeconds = sMaxAgeSeconds2;
        this.isPrivate = isPrivate2;
        this.isPublic = isPublic2;
        this.mustRevalidate = mustRevalidate2;
        this.maxStaleSeconds = maxStaleSeconds2;
        this.minFreshSeconds = minFreshSeconds2;
        this.onlyIfCached = onlyIfCached2;
        this.noTransform = noTransform2;
        this.immutable = immutable2;
        this.headerValue = headerValue2;
    }

    CacheControl(Builder builder) {
        this.noCache = builder.noCache;
        this.noStore = builder.noStore;
        this.maxAgeSeconds = builder.maxAgeSeconds;
        this.sMaxAgeSeconds = -1;
        this.isPrivate = false;
        this.isPublic = false;
        this.mustRevalidate = false;
        this.maxStaleSeconds = builder.maxStaleSeconds;
        this.minFreshSeconds = builder.minFreshSeconds;
        this.onlyIfCached = builder.onlyIfCached;
        this.noTransform = builder.noTransform;
        this.immutable = builder.immutable;
    }

    public boolean noCache() {
        return this.noCache;
    }

    public boolean noStore() {
        return this.noStore;
    }

    public int maxAgeSeconds() {
        return this.maxAgeSeconds;
    }

    public int sMaxAgeSeconds() {
        return this.sMaxAgeSeconds;
    }

    public boolean isPrivate() {
        return this.isPrivate;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

    public boolean mustRevalidate() {
        return this.mustRevalidate;
    }

    public int maxStaleSeconds() {
        return this.maxStaleSeconds;
    }

    public int minFreshSeconds() {
        return this.minFreshSeconds;
    }

    public boolean onlyIfCached() {
        return this.onlyIfCached;
    }

    public boolean noTransform() {
        return this.noTransform;
    }

    public boolean immutable() {
        return this.immutable;
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x00b7  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00c1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static okhttp3.CacheControl parse(okhttp3.Headers r40) {
        /*
            r0 = r40
            r1 = 0
            r2 = 0
            r3 = -1
            r4 = -1
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = -1
            r9 = -1
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 1
            r14 = 0
            r15 = 0
            int r16 = r40.size()
        L_0x0015:
            r31 = r16
            r32 = r12
            r12 = r31
            if (r15 >= r12) goto L_0x0174
            r33 = r12
            java.lang.String r12 = r0.name(r15)
            r34 = r11
            java.lang.String r11 = r0.value(r15)
            java.lang.String r0 = "Cache-Control"
            boolean r0 = r12.equalsIgnoreCase(r0)
            if (r0 == 0) goto L_0x0037
            if (r14 == 0) goto L_0x0035
            r13 = 0
            goto L_0x0040
        L_0x0035:
            r14 = r11
            goto L_0x0040
        L_0x0037:
            java.lang.String r0 = "Pragma"
            boolean r0 = r12.equalsIgnoreCase(r0)
            if (r0 == 0) goto L_0x0168
            r13 = 0
        L_0x0040:
            r0 = 0
        L_0x0041:
            r35 = r1
            int r1 = r11.length()
            if (r0 >= r1) goto L_0x015d
            r1 = r0
            r36 = r2
            java.lang.String r2 = "=,;"
            int r0 = okhttp3.internal.http.HttpHeaders.skipUntil(r11, r0, r2)
            java.lang.String r2 = r11.substring(r1, r0)
            java.lang.String r2 = r2.trim()
            r37 = r1
            int r1 = r11.length()
            if (r0 == r1) goto L_0x00a9
            char r1 = r11.charAt(r0)
            r38 = r3
            r3 = 44
            if (r1 == r3) goto L_0x00ab
            char r1 = r11.charAt(r0)
            r3 = 59
            if (r1 != r3) goto L_0x0075
            goto L_0x00ab
        L_0x0075:
            int r0 = r0 + 1
            int r0 = okhttp3.internal.http.HttpHeaders.skipWhitespace(r11, r0)
            int r1 = r11.length()
            if (r0 >= r1) goto L_0x0099
            char r1 = r11.charAt(r0)
            r3 = 34
            if (r1 != r3) goto L_0x0099
            int r0 = r0 + 1
            r1 = r0
            java.lang.String r3 = "\""
            int r0 = okhttp3.internal.http.HttpHeaders.skipUntil(r11, r0, r3)
            java.lang.String r3 = r11.substring(r1, r0)
            int r0 = r0 + 1
            goto L_0x00ae
        L_0x0099:
            r1 = r0
            java.lang.String r3 = ",;"
            int r0 = okhttp3.internal.http.HttpHeaders.skipUntil(r11, r0, r3)
            java.lang.String r3 = r11.substring(r1, r0)
            java.lang.String r3 = r3.trim()
            goto L_0x00ae
        L_0x00a9:
            r38 = r3
        L_0x00ab:
            int r0 = r0 + 1
            r3 = 0
        L_0x00ae:
            r1 = r3
            java.lang.String r3 = "no-cache"
            boolean r3 = r3.equalsIgnoreCase(r2)
            if (r3 == 0) goto L_0x00c1
            r3 = 1
            r39 = r0
            r1 = r3
        L_0x00bb:
            r2 = r36
        L_0x00bd:
            r3 = r38
            goto L_0x0159
        L_0x00c1:
            java.lang.String r3 = "no-store"
            boolean r3 = r3.equalsIgnoreCase(r2)
            if (r3 == 0) goto L_0x00d0
            r3 = 1
            r39 = r0
            r2 = r3
            r1 = r35
            goto L_0x00bd
        L_0x00d0:
            java.lang.String r3 = "max-age"
            boolean r3 = r3.equalsIgnoreCase(r2)
            r39 = r0
            r0 = -1
            if (r3 == 0) goto L_0x00e6
            int r0 = okhttp3.internal.http.HttpHeaders.parseSeconds(r1, r0)
            r3 = r0
            r1 = r35
            r2 = r36
            goto L_0x0159
        L_0x00e6:
            java.lang.String r3 = "s-maxage"
            boolean r3 = r3.equalsIgnoreCase(r2)
            if (r3 == 0) goto L_0x00f6
            int r0 = okhttp3.internal.http.HttpHeaders.parseSeconds(r1, r0)
            r4 = r0
        L_0x00f3:
            r1 = r35
            goto L_0x00bb
        L_0x00f6:
            java.lang.String r3 = "private"
            boolean r3 = r3.equalsIgnoreCase(r2)
            if (r3 == 0) goto L_0x0101
            r0 = 1
            r5 = r0
            goto L_0x00f3
        L_0x0101:
            java.lang.String r3 = "public"
            boolean r3 = r3.equalsIgnoreCase(r2)
            if (r3 == 0) goto L_0x010c
            r0 = 1
            r6 = r0
            goto L_0x00f3
        L_0x010c:
            java.lang.String r3 = "must-revalidate"
            boolean r3 = r3.equalsIgnoreCase(r2)
            if (r3 == 0) goto L_0x0117
            r0 = 1
            r7 = r0
            goto L_0x00f3
        L_0x0117:
            java.lang.String r3 = "max-stale"
            boolean r3 = r3.equalsIgnoreCase(r2)
            if (r3 == 0) goto L_0x0128
            r0 = 2147483647(0x7fffffff, float:NaN)
            int r0 = okhttp3.internal.http.HttpHeaders.parseSeconds(r1, r0)
            r8 = r0
            goto L_0x00f3
        L_0x0128:
            java.lang.String r3 = "min-fresh"
            boolean r3 = r3.equalsIgnoreCase(r2)
            if (r3 == 0) goto L_0x0136
            int r0 = okhttp3.internal.http.HttpHeaders.parseSeconds(r1, r0)
            r9 = r0
            goto L_0x00f3
        L_0x0136:
            java.lang.String r0 = "only-if-cached"
            boolean r0 = r0.equalsIgnoreCase(r2)
            if (r0 == 0) goto L_0x0141
            r0 = 1
            r10 = r0
            goto L_0x00f3
        L_0x0141:
            java.lang.String r0 = "no-transform"
            boolean r0 = r0.equalsIgnoreCase(r2)
            if (r0 == 0) goto L_0x014d
            r0 = 1
            r34 = r0
            goto L_0x00f3
        L_0x014d:
            java.lang.String r0 = "immutable"
            boolean r0 = r0.equalsIgnoreCase(r2)
            if (r0 == 0) goto L_0x00f3
            r0 = 1
            r32 = r0
            goto L_0x00f3
        L_0x0159:
            r0 = r39
            goto L_0x0041
        L_0x015d:
            r36 = r2
            r38 = r3
            r12 = r32
            r11 = r34
            r1 = r35
            goto L_0x016c
        L_0x0168:
            r12 = r32
            r11 = r34
        L_0x016c:
            int r15 = r15 + 1
            r16 = r33
            r0 = r40
            goto L_0x0015
        L_0x0174:
            r34 = r11
            if (r13 != 0) goto L_0x0179
            r14 = 0
        L_0x0179:
            okhttp3.CacheControl r0 = new okhttp3.CacheControl
            r17 = r0
            r18 = r1
            r19 = r2
            r20 = r3
            r21 = r4
            r22 = r5
            r23 = r6
            r24 = r7
            r25 = r8
            r26 = r9
            r27 = r10
            r28 = r34
            r29 = r32
            r30 = r14
            r17.<init>(r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28, r29, r30)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.CacheControl.parse(okhttp3.Headers):okhttp3.CacheControl");
    }

    public String toString() {
        String result = this.headerValue;
        if (result != null) {
            return result;
        }
        String headerValue2 = headerValue();
        this.headerValue = headerValue2;
        return headerValue2;
    }

    private String headerValue() {
        StringBuilder result = new StringBuilder();
        if (this.noCache) {
            result.append("no-cache, ");
        }
        if (this.noStore) {
            result.append("no-store, ");
        }
        if (this.maxAgeSeconds != -1) {
            result.append("max-age=");
            result.append(this.maxAgeSeconds);
            result.append(", ");
        }
        if (this.sMaxAgeSeconds != -1) {
            result.append("s-maxage=");
            result.append(this.sMaxAgeSeconds);
            result.append(", ");
        }
        if (this.isPrivate) {
            result.append("private, ");
        }
        if (this.isPublic) {
            result.append("public, ");
        }
        if (this.mustRevalidate) {
            result.append("must-revalidate, ");
        }
        if (this.maxStaleSeconds != -1) {
            result.append("max-stale=");
            result.append(this.maxStaleSeconds);
            result.append(", ");
        }
        if (this.minFreshSeconds != -1) {
            result.append("min-fresh=");
            result.append(this.minFreshSeconds);
            result.append(", ");
        }
        if (this.onlyIfCached) {
            result.append("only-if-cached, ");
        }
        if (this.noTransform) {
            result.append("no-transform, ");
        }
        if (this.immutable) {
            result.append("immutable, ");
        }
        if (result.length() == 0) {
            return "";
        }
        result.delete(result.length() - 2, result.length());
        return result.toString();
    }

    public static final class Builder {
        boolean immutable;
        int maxAgeSeconds = -1;
        int maxStaleSeconds = -1;
        int minFreshSeconds = -1;
        boolean noCache;
        boolean noStore;
        boolean noTransform;
        boolean onlyIfCached;

        public Builder noCache() {
            this.noCache = true;
            return this;
        }

        public Builder noStore() {
            this.noStore = true;
            return this;
        }

        public Builder maxAge(int maxAge, TimeUnit timeUnit) {
            int i;
            if (maxAge < 0) {
                throw new IllegalArgumentException("maxAge < 0: " + maxAge);
            }
            long maxAgeSecondsLong = timeUnit.toSeconds((long) maxAge);
            if (maxAgeSecondsLong > 2147483647L) {
                i = Integer.MAX_VALUE;
            } else {
                i = (int) maxAgeSecondsLong;
            }
            this.maxAgeSeconds = i;
            return this;
        }

        public Builder maxStale(int maxStale, TimeUnit timeUnit) {
            int i;
            if (maxStale < 0) {
                throw new IllegalArgumentException("maxStale < 0: " + maxStale);
            }
            long maxStaleSecondsLong = timeUnit.toSeconds((long) maxStale);
            if (maxStaleSecondsLong > 2147483647L) {
                i = Integer.MAX_VALUE;
            } else {
                i = (int) maxStaleSecondsLong;
            }
            this.maxStaleSeconds = i;
            return this;
        }

        public Builder minFresh(int minFresh, TimeUnit timeUnit) {
            int i;
            if (minFresh < 0) {
                throw new IllegalArgumentException("minFresh < 0: " + minFresh);
            }
            long minFreshSecondsLong = timeUnit.toSeconds((long) minFresh);
            if (minFreshSecondsLong > 2147483647L) {
                i = Integer.MAX_VALUE;
            } else {
                i = (int) minFreshSecondsLong;
            }
            this.minFreshSeconds = i;
            return this;
        }

        public Builder onlyIfCached() {
            this.onlyIfCached = true;
            return this;
        }

        public Builder noTransform() {
            this.noTransform = true;
            return this;
        }

        public Builder immutable() {
            this.immutable = true;
            return this;
        }

        public CacheControl build() {
            return new CacheControl(this);
        }
    }
}
