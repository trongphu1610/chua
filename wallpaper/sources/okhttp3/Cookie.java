package okhttp3;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import kotlin.jvm.internal.LongCompanionObject;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpDate;
import okhttp3.internal.publicsuffix.PublicSuffixDatabase;

public final class Cookie {
    private static final Pattern DAY_OF_MONTH_PATTERN = Pattern.compile("(\\d{1,2})[^\\d]*");
    private static final Pattern MONTH_PATTERN = Pattern.compile("(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*");
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{1,2}):(\\d{1,2}):(\\d{1,2})[^\\d]*");
    private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{2,4})[^\\d]*");
    private final String domain;
    private final long expiresAt;
    private final boolean hostOnly;
    private final boolean httpOnly;
    private final String name;
    private final String path;
    private final boolean persistent;
    private final boolean secure;
    private final String value;

    private Cookie(String name2, String value2, long expiresAt2, String domain2, String path2, boolean secure2, boolean httpOnly2, boolean hostOnly2, boolean persistent2) {
        this.name = name2;
        this.value = value2;
        this.expiresAt = expiresAt2;
        this.domain = domain2;
        this.path = path2;
        this.secure = secure2;
        this.httpOnly = httpOnly2;
        this.hostOnly = hostOnly2;
        this.persistent = persistent2;
    }

    Cookie(Builder builder) {
        if (builder.name == null) {
            throw new NullPointerException("builder.name == null");
        } else if (builder.value == null) {
            throw new NullPointerException("builder.value == null");
        } else if (builder.domain == null) {
            throw new NullPointerException("builder.domain == null");
        } else {
            this.name = builder.name;
            this.value = builder.value;
            this.expiresAt = builder.expiresAt;
            this.domain = builder.domain;
            this.path = builder.path;
            this.secure = builder.secure;
            this.httpOnly = builder.httpOnly;
            this.persistent = builder.persistent;
            this.hostOnly = builder.hostOnly;
        }
    }

    public String name() {
        return this.name;
    }

    public String value() {
        return this.value;
    }

    public boolean persistent() {
        return this.persistent;
    }

    public long expiresAt() {
        return this.expiresAt;
    }

    public boolean hostOnly() {
        return this.hostOnly;
    }

    public String domain() {
        return this.domain;
    }

    public String path() {
        return this.path;
    }

    public boolean httpOnly() {
        return this.httpOnly;
    }

    public boolean secure() {
        return this.secure;
    }

    public boolean matches(HttpUrl url) {
        boolean domainMatch;
        if (this.hostOnly) {
            domainMatch = url.host().equals(this.domain);
        } else {
            domainMatch = domainMatch(url.host(), this.domain);
        }
        if (!domainMatch || !pathMatch(url, this.path)) {
            return false;
        }
        if (!this.secure || url.isHttps()) {
            return true;
        }
        return false;
    }

    private static boolean domainMatch(String urlHost, String domain2) {
        if (urlHost.equals(domain2)) {
            return true;
        }
        if (!urlHost.endsWith(domain2) || urlHost.charAt((urlHost.length() - domain2.length()) - 1) != '.' || Util.verifyAsIpAddress(urlHost)) {
            return false;
        }
        return true;
    }

    private static boolean pathMatch(HttpUrl url, String path2) {
        String urlPath = url.encodedPath();
        if (urlPath.equals(path2)) {
            return true;
        }
        if (!urlPath.startsWith(path2)) {
            return false;
        }
        if (!path2.endsWith("/") && urlPath.charAt(path2.length()) != '/') {
            return false;
        }
        return true;
    }

    @Nullable
    public static Cookie parse(HttpUrl url, String setCookie) {
        return parse(System.currentTimeMillis(), url, setCookie);
    }

    @Nullable
    static Cookie parse(long currentTimeMillis, HttpUrl url, String setCookie) {
        long expiresAt2;
        String domain2;
        Cookie cookie;
        long deltaMilliseconds;
        String attributeValue;
        int limit;
        String str = setCookie;
        int limit2 = setCookie.length();
        char c = ';';
        int cookiePairEnd = Util.delimiterOffset(str, 0, limit2, ';');
        char c2 = '=';
        int pairEqualsSign = Util.delimiterOffset(str, 0, cookiePairEnd, '=');
        if (pairEqualsSign == cookiePairEnd) {
            return null;
        }
        String cookieName = Util.trimSubstring(str, 0, pairEqualsSign);
        if (cookieName.isEmpty()) {
            String str2 = cookieName;
            return null;
        } else if (Util.indexOfControlOrNonAscii(cookieName) != -1) {
            int i = limit2;
            String str3 = cookieName;
            return null;
        } else {
            String cookieValue = Util.trimSubstring(str, pairEqualsSign + 1, cookiePairEnd);
            if (Util.indexOfControlOrNonAscii(cookieValue) != -1) {
                return null;
            }
            long expiresAt3 = HttpDate.MAX_DATE;
            String domain3 = null;
            int pos = cookiePairEnd + 1;
            String path2 = null;
            boolean secureOnly = false;
            boolean httpOnly2 = false;
            boolean hostOnly2 = true;
            boolean persistent2 = false;
            long deltaSeconds = -1;
            while (pos < limit2) {
                int attributePairEnd = Util.delimiterOffset(str, pos, limit2, c);
                int attributeEqualsSign = Util.delimiterOffset(str, pos, attributePairEnd, c2);
                String attributeName = Util.trimSubstring(str, pos, attributeEqualsSign);
                if (attributeEqualsSign < attributePairEnd) {
                    int i2 = pos;
                    attributeValue = Util.trimSubstring(str, attributeEqualsSign + 1, attributePairEnd);
                } else {
                    attributeValue = "";
                }
                if (attributeName.equalsIgnoreCase("expires")) {
                    try {
                        limit = limit2;
                        try {
                            expiresAt3 = parseExpires(attributeValue, 0, attributeValue.length());
                            persistent2 = true;
                        } catch (IllegalArgumentException e) {
                        }
                    } catch (IllegalArgumentException e2) {
                        limit = limit2;
                    }
                } else {
                    limit = limit2;
                    if (attributeName.equalsIgnoreCase("max-age")) {
                        try {
                            persistent2 = true;
                            deltaSeconds = parseMaxAge(attributeValue);
                        } catch (NumberFormatException e3) {
                        }
                    } else if (attributeName.equalsIgnoreCase("domain")) {
                        try {
                            hostOnly2 = false;
                            domain3 = parseDomain(attributeValue);
                        } catch (IllegalArgumentException e4) {
                        }
                    } else if (attributeName.equalsIgnoreCase("path")) {
                        path2 = attributeValue;
                    } else if (attributeName.equalsIgnoreCase("secure")) {
                        secureOnly = true;
                    } else if (attributeName.equalsIgnoreCase("httponly")) {
                        httpOnly2 = true;
                    }
                }
                pos = attributePairEnd + 1;
                limit2 = limit;
                str = setCookie;
                c = ';';
                c2 = '=';
            }
            int i3 = pos;
            int i4 = limit2;
            if (deltaSeconds == Long.MIN_VALUE) {
                expiresAt2 = Long.MIN_VALUE;
            } else if (deltaSeconds != -1) {
                if (deltaSeconds <= 9223372036854775L) {
                    deltaMilliseconds = 1000 * deltaSeconds;
                } else {
                    deltaMilliseconds = LongCompanionObject.MAX_VALUE;
                }
                long expiresAt4 = currentTimeMillis + deltaMilliseconds;
                expiresAt2 = (expiresAt4 < currentTimeMillis || expiresAt4 > HttpDate.MAX_DATE) ? HttpDate.MAX_DATE : expiresAt4;
            } else {
                expiresAt2 = expiresAt3;
            }
            String urlHost = url.host();
            if (domain3 == null) {
                domain2 = urlHost;
                cookie = null;
            } else if (!domainMatch(urlHost, domain3)) {
                return null;
            } else {
                cookie = null;
                domain2 = domain3;
            }
            if (urlHost.length() != domain2.length() && PublicSuffixDatabase.get().getEffectiveTldPlusOne(domain2) == null) {
                return cookie;
            }
            String path3 = path2;
            if (path3 == null || !path3.startsWith("/")) {
                String encodedPath = url.encodedPath();
                int lastSlash = encodedPath.lastIndexOf(47);
                path3 = lastSlash != 0 ? encodedPath.substring(0, lastSlash) : "/";
            }
            String str4 = cookieName;
            String str5 = cookieValue;
            return new Cookie(cookieName, cookieValue, expiresAt2, domain2, path3, secureOnly, httpOnly2, hostOnly2, persistent2);
        }
    }

    private static long parseExpires(String s, int pos, int limit) {
        int pos2 = dateCharacterOffset(s, pos, limit, false);
        int hour = -1;
        int minute = -1;
        int second = -1;
        int dayOfMonth = -1;
        int month = -1;
        int year = -1;
        Matcher matcher = TIME_PATTERN.matcher(s);
        while (pos2 < limit) {
            int end = dateCharacterOffset(s, pos2 + 1, limit, true);
            matcher.region(pos2, end);
            if (hour == -1 && matcher.usePattern(TIME_PATTERN).matches()) {
                hour = Integer.parseInt(matcher.group(1));
                minute = Integer.parseInt(matcher.group(2));
                second = Integer.parseInt(matcher.group(3));
            } else if (dayOfMonth == -1 && matcher.usePattern(DAY_OF_MONTH_PATTERN).matches()) {
                dayOfMonth = Integer.parseInt(matcher.group(1));
            } else if (month == -1 && matcher.usePattern(MONTH_PATTERN).matches()) {
                month = MONTH_PATTERN.pattern().indexOf(matcher.group(1).toLowerCase(Locale.US)) / 4;
            } else if (year == -1 && matcher.usePattern(YEAR_PATTERN).matches()) {
                year = Integer.parseInt(matcher.group(1));
            }
            pos2 = dateCharacterOffset(s, end + 1, limit, false);
        }
        if (year >= 70 && year <= 99) {
            year += 1900;
        }
        if (year >= 0 && year <= 69) {
            year += 2000;
        }
        if (year < 1601) {
            throw new IllegalArgumentException();
        } else if (month == -1) {
            throw new IllegalArgumentException();
        } else if (dayOfMonth < 1 || dayOfMonth > 31) {
            throw new IllegalArgumentException();
        } else if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException();
        } else if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException();
        } else if (second < 0 || second > 59) {
            throw new IllegalArgumentException();
        } else {
            Calendar calendar = new GregorianCalendar(Util.UTC);
            calendar.setLenient(false);
            calendar.set(1, year);
            calendar.set(2, month - 1);
            calendar.set(5, dayOfMonth);
            calendar.set(11, hour);
            calendar.set(12, minute);
            calendar.set(13, second);
            calendar.set(14, 0);
            return calendar.getTimeInMillis();
        }
    }

    private static int dateCharacterOffset(String input, int pos, int limit, boolean invert) {
        for (int i = pos; i < limit; i++) {
            int c = input.charAt(i);
            if (((c < 32 && c != 9) || c >= 127 || (c >= 48 && c <= 57) || ((c >= 97 && c <= 122) || ((c >= 65 && c <= 90) || c == 58))) == (!invert)) {
                return i;
            }
        }
        return limit;
    }

    private static long parseMaxAge(String s) {
        try {
            long parsed = Long.parseLong(s);
            if (parsed <= 0) {
                return Long.MIN_VALUE;
            }
            return parsed;
        } catch (NumberFormatException e) {
            if (!s.matches("-?\\d+")) {
                throw e;
            } else if (s.startsWith("-")) {
                return Long.MIN_VALUE;
            } else {
                return LongCompanionObject.MAX_VALUE;
            }
        }
    }

    private static String parseDomain(String s) {
        if (s.endsWith(".")) {
            throw new IllegalArgumentException();
        }
        if (s.startsWith(".")) {
            s = s.substring(1);
        }
        String canonicalDomain = Util.canonicalizeHost(s);
        if (canonicalDomain != null) {
            return canonicalDomain;
        }
        throw new IllegalArgumentException();
    }

    public static List<Cookie> parseAll(HttpUrl url, Headers headers) {
        List<String> cookieStrings = headers.values("Set-Cookie");
        List<Cookie> cookies = null;
        int size = cookieStrings.size();
        for (int i = 0; i < size; i++) {
            Cookie cookie = parse(url, cookieStrings.get(i));
            if (cookie != null) {
                if (cookies == null) {
                    cookies = new ArrayList<>();
                }
                cookies.add(cookie);
            }
        }
        if (cookies != null) {
            return Collections.unmodifiableList(cookies);
        }
        return Collections.emptyList();
    }

    public static final class Builder {
        String domain;
        long expiresAt = HttpDate.MAX_DATE;
        boolean hostOnly;
        boolean httpOnly;
        String name;
        String path = "/";
        boolean persistent;
        boolean secure;
        String value;

        public Builder name(String name2) {
            if (name2 == null) {
                throw new NullPointerException("name == null");
            } else if (!name2.trim().equals(name2)) {
                throw new IllegalArgumentException("name is not trimmed");
            } else {
                this.name = name2;
                return this;
            }
        }

        public Builder value(String value2) {
            if (value2 == null) {
                throw new NullPointerException("value == null");
            } else if (!value2.trim().equals(value2)) {
                throw new IllegalArgumentException("value is not trimmed");
            } else {
                this.value = value2;
                return this;
            }
        }

        public Builder expiresAt(long expiresAt2) {
            if (expiresAt2 <= 0) {
                expiresAt2 = Long.MIN_VALUE;
            }
            if (expiresAt2 > HttpDate.MAX_DATE) {
                expiresAt2 = HttpDate.MAX_DATE;
            }
            this.expiresAt = expiresAt2;
            this.persistent = true;
            return this;
        }

        public Builder domain(String domain2) {
            return domain(domain2, false);
        }

        public Builder hostOnlyDomain(String domain2) {
            return domain(domain2, true);
        }

        private Builder domain(String domain2, boolean hostOnly2) {
            if (domain2 == null) {
                throw new NullPointerException("domain == null");
            }
            String canonicalDomain = Util.canonicalizeHost(domain2);
            if (canonicalDomain == null) {
                throw new IllegalArgumentException("unexpected domain: " + domain2);
            }
            this.domain = canonicalDomain;
            this.hostOnly = hostOnly2;
            return this;
        }

        public Builder path(String path2) {
            if (!path2.startsWith("/")) {
                throw new IllegalArgumentException("path must start with '/'");
            }
            this.path = path2;
            return this;
        }

        public Builder secure() {
            this.secure = true;
            return this;
        }

        public Builder httpOnly() {
            this.httpOnly = true;
            return this;
        }

        public Cookie build() {
            return new Cookie(this);
        }
    }

    public String toString() {
        return toString(false);
    }

    /* access modifiers changed from: package-private */
    public String toString(boolean forObsoleteRfc2965) {
        StringBuilder result = new StringBuilder();
        result.append(this.name);
        result.append('=');
        result.append(this.value);
        if (this.persistent) {
            if (this.expiresAt == Long.MIN_VALUE) {
                result.append("; max-age=0");
            } else {
                result.append("; expires=");
                result.append(HttpDate.format(new Date(this.expiresAt)));
            }
        }
        if (!this.hostOnly) {
            result.append("; domain=");
            if (forObsoleteRfc2965) {
                result.append(".");
            }
            result.append(this.domain);
        }
        result.append("; path=");
        result.append(this.path);
        if (this.secure) {
            result.append("; secure");
        }
        if (this.httpOnly) {
            result.append("; httponly");
        }
        return result.toString();
    }

    public boolean equals(@Nullable Object other) {
        if (!(other instanceof Cookie)) {
            return false;
        }
        Cookie that = (Cookie) other;
        if (that.name.equals(this.name) && that.value.equals(this.value) && that.domain.equals(this.domain) && that.path.equals(this.path) && that.expiresAt == this.expiresAt && that.secure == this.secure && that.httpOnly == this.httpOnly && that.persistent == this.persistent && that.hostOnly == this.hostOnly) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((((((((((((((((17 * 31) + this.name.hashCode()) * 31) + this.value.hashCode()) * 31) + this.domain.hashCode()) * 31) + this.path.hashCode()) * 31) + ((int) (this.expiresAt ^ (this.expiresAt >>> 32)))) * 31) + (this.secure ^ true ? 1 : 0)) * 31) + (this.httpOnly ^ true ? 1 : 0)) * 31) + (this.persistent ^ true ? 1 : 0)) * 31) + (this.hostOnly ^ true ? 1 : 0);
    }
}
