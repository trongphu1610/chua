package okhttp3;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import kotlin.text.Typography;
import okhttp3.internal.Util;
import okhttp3.internal.publicsuffix.PublicSuffixDatabase;
import okio.Buffer;

public final class HttpUrl {
    static final String FORM_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#&!$(),~";
    static final String FRAGMENT_ENCODE_SET = "";
    static final String FRAGMENT_ENCODE_SET_URI = " \"#<>\\^`{|}";
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    static final String PASSWORD_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    static final String PATH_SEGMENT_ENCODE_SET = " \"<>^`{}|/\\?#";
    static final String PATH_SEGMENT_ENCODE_SET_URI = "[]";
    static final String QUERY_COMPONENT_ENCODE_SET = " !\"#$&'(),/:;<=>?@[]\\^`{|}~";
    static final String QUERY_COMPONENT_ENCODE_SET_URI = "\\^`{|}";
    static final String QUERY_COMPONENT_REENCODE_SET = " \"'<>#&=";
    static final String QUERY_ENCODE_SET = " \"'<>#";
    static final String USERNAME_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    @Nullable
    private final String fragment;
    final String host;
    private final String password;
    private final List<String> pathSegments;
    final int port;
    @Nullable
    private final List<String> queryNamesAndValues;
    final String scheme;
    private final String url;
    private final String username;

    HttpUrl(Builder builder) {
        List<String> list;
        this.scheme = builder.scheme;
        this.username = percentDecode(builder.encodedUsername, false);
        this.password = percentDecode(builder.encodedPassword, false);
        this.host = builder.host;
        this.port = builder.effectivePort();
        this.pathSegments = percentDecode(builder.encodedPathSegments, false);
        String str = null;
        if (builder.encodedQueryNamesAndValues != null) {
            list = percentDecode(builder.encodedQueryNamesAndValues, true);
        } else {
            list = null;
        }
        this.queryNamesAndValues = list;
        this.fragment = builder.encodedFragment != null ? percentDecode(builder.encodedFragment, false) : str;
        this.url = builder.toString();
    }

    public URL url() {
        try {
            return new URL(this.url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public URI uri() {
        String uri = newBuilder().reencodeForUri().toString();
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            try {
                return URI.create(uri.replaceAll("[\\u0000-\\u001F\\u007F-\\u009F\\p{javaWhitespace}]", ""));
            } catch (Exception e2) {
                throw new RuntimeException(e);
            }
        }
    }

    public String scheme() {
        return this.scheme;
    }

    public boolean isHttps() {
        return this.scheme.equals("https");
    }

    public String encodedUsername() {
        if (this.username.isEmpty()) {
            return "";
        }
        int usernameStart = this.scheme.length() + 3;
        return this.url.substring(usernameStart, Util.delimiterOffset(this.url, usernameStart, this.url.length(), ":@"));
    }

    public String username() {
        return this.username;
    }

    public String encodedPassword() {
        if (this.password.isEmpty()) {
            return "";
        }
        int passwordEnd = this.url.indexOf(64);
        return this.url.substring(this.url.indexOf(58, this.scheme.length() + 3) + 1, passwordEnd);
    }

    public String password() {
        return this.password;
    }

    public String host() {
        return this.host;
    }

    public int port() {
        return this.port;
    }

    public static int defaultPort(String scheme2) {
        if (scheme2.equals("http")) {
            return 80;
        }
        if (scheme2.equals("https")) {
            return 443;
        }
        return -1;
    }

    public int pathSize() {
        return this.pathSegments.size();
    }

    public String encodedPath() {
        int pathStart = this.url.indexOf(47, this.scheme.length() + 3);
        return this.url.substring(pathStart, Util.delimiterOffset(this.url, pathStart, this.url.length(), "?#"));
    }

    static void pathSegmentsToString(StringBuilder out, List<String> pathSegments2) {
        int size = pathSegments2.size();
        for (int i = 0; i < size; i++) {
            out.append('/');
            out.append(pathSegments2.get(i));
        }
    }

    public List<String> encodedPathSegments() {
        int pathStart = this.url.indexOf(47, this.scheme.length() + 3);
        int pathEnd = Util.delimiterOffset(this.url, pathStart, this.url.length(), "?#");
        List<String> result = new ArrayList<>();
        int i = pathStart;
        while (i < pathEnd) {
            int i2 = i + 1;
            int segmentEnd = Util.delimiterOffset(this.url, i2, pathEnd, '/');
            result.add(this.url.substring(i2, segmentEnd));
            i = segmentEnd;
        }
        return result;
    }

    public List<String> pathSegments() {
        return this.pathSegments;
    }

    @Nullable
    public String encodedQuery() {
        if (this.queryNamesAndValues == null) {
            return null;
        }
        int queryStart = this.url.indexOf(63) + 1;
        return this.url.substring(queryStart, Util.delimiterOffset(this.url, queryStart, this.url.length(), '#'));
    }

    static void namesAndValuesToQueryString(StringBuilder out, List<String> namesAndValues) {
        int size = namesAndValues.size();
        for (int i = 0; i < size; i += 2) {
            String name = namesAndValues.get(i);
            String value = namesAndValues.get(i + 1);
            if (i > 0) {
                out.append(Typography.amp);
            }
            out.append(name);
            if (value != null) {
                out.append('=');
                out.append(value);
            }
        }
    }

    static List<String> queryStringToNamesAndValues(String encodedQuery) {
        List<String> result = new ArrayList<>();
        int pos = 0;
        while (pos <= encodedQuery.length()) {
            int ampersandOffset = encodedQuery.indexOf(38, pos);
            if (ampersandOffset == -1) {
                ampersandOffset = encodedQuery.length();
            }
            int equalsOffset = encodedQuery.indexOf(61, pos);
            if (equalsOffset == -1 || equalsOffset > ampersandOffset) {
                result.add(encodedQuery.substring(pos, ampersandOffset));
                result.add((Object) null);
            } else {
                result.add(encodedQuery.substring(pos, equalsOffset));
                result.add(encodedQuery.substring(equalsOffset + 1, ampersandOffset));
            }
            pos = ampersandOffset + 1;
        }
        return result;
    }

    @Nullable
    public String query() {
        if (this.queryNamesAndValues == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        namesAndValuesToQueryString(result, this.queryNamesAndValues);
        return result.toString();
    }

    public int querySize() {
        if (this.queryNamesAndValues != null) {
            return this.queryNamesAndValues.size() / 2;
        }
        return 0;
    }

    @Nullable
    public String queryParameter(String name) {
        if (this.queryNamesAndValues == null) {
            return null;
        }
        int size = this.queryNamesAndValues.size();
        for (int i = 0; i < size; i += 2) {
            if (name.equals(this.queryNamesAndValues.get(i))) {
                return this.queryNamesAndValues.get(i + 1);
            }
        }
        return null;
    }

    public Set<String> queryParameterNames() {
        if (this.queryNamesAndValues == null) {
            return Collections.emptySet();
        }
        Set<String> result = new LinkedHashSet<>();
        int size = this.queryNamesAndValues.size();
        for (int i = 0; i < size; i += 2) {
            result.add(this.queryNamesAndValues.get(i));
        }
        return Collections.unmodifiableSet(result);
    }

    public List<String> queryParameterValues(String name) {
        if (this.queryNamesAndValues == null) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        int size = this.queryNamesAndValues.size();
        for (int i = 0; i < size; i += 2) {
            if (name.equals(this.queryNamesAndValues.get(i))) {
                result.add(this.queryNamesAndValues.get(i + 1));
            }
        }
        return Collections.unmodifiableList(result);
    }

    public String queryParameterName(int index) {
        if (this.queryNamesAndValues != null) {
            return this.queryNamesAndValues.get(index * 2);
        }
        throw new IndexOutOfBoundsException();
    }

    public String queryParameterValue(int index) {
        if (this.queryNamesAndValues != null) {
            return this.queryNamesAndValues.get((index * 2) + 1);
        }
        throw new IndexOutOfBoundsException();
    }

    @Nullable
    public String encodedFragment() {
        if (this.fragment == null) {
            return null;
        }
        return this.url.substring(this.url.indexOf(35) + 1);
    }

    @Nullable
    public String fragment() {
        return this.fragment;
    }

    public String redact() {
        return newBuilder("/...").username("").password("").build().toString();
    }

    @Nullable
    public HttpUrl resolve(String link) {
        Builder builder = newBuilder(link);
        if (builder != null) {
            return builder.build();
        }
        return null;
    }

    public Builder newBuilder() {
        Builder result = new Builder();
        result.scheme = this.scheme;
        result.encodedUsername = encodedUsername();
        result.encodedPassword = encodedPassword();
        result.host = this.host;
        result.port = this.port != defaultPort(this.scheme) ? this.port : -1;
        result.encodedPathSegments.clear();
        result.encodedPathSegments.addAll(encodedPathSegments());
        result.encodedQuery(encodedQuery());
        result.encodedFragment = encodedFragment();
        return result;
    }

    @Nullable
    public Builder newBuilder(String link) {
        Builder builder = new Builder();
        if (builder.parse(this, link) == Builder.ParseResult.SUCCESS) {
            return builder;
        }
        return null;
    }

    @Nullable
    public static HttpUrl parse(String url2) {
        Builder builder = new Builder();
        if (builder.parse((HttpUrl) null, url2) == Builder.ParseResult.SUCCESS) {
            return builder.build();
        }
        return null;
    }

    @Nullable
    public static HttpUrl get(URL url2) {
        return parse(url2.toString());
    }

    static HttpUrl getChecked(String url2) throws MalformedURLException, UnknownHostException {
        Builder builder = new Builder();
        Builder.ParseResult result = builder.parse((HttpUrl) null, url2);
        switch (result) {
            case SUCCESS:
                return builder.build();
            case INVALID_HOST:
                throw new UnknownHostException("Invalid host: " + url2);
            default:
                throw new MalformedURLException("Invalid URL: " + result + " for " + url2);
        }
    }

    @Nullable
    public static HttpUrl get(URI uri) {
        return parse(uri.toString());
    }

    public boolean equals(@Nullable Object other) {
        return (other instanceof HttpUrl) && ((HttpUrl) other).url.equals(this.url);
    }

    public int hashCode() {
        return this.url.hashCode();
    }

    public String toString() {
        return this.url;
    }

    @Nullable
    public String topPrivateDomain() {
        if (Util.verifyAsIpAddress(this.host)) {
            return null;
        }
        return PublicSuffixDatabase.get().getEffectiveTldPlusOne(this.host);
    }

    public static final class Builder {
        @Nullable
        String encodedFragment;
        String encodedPassword = "";
        final List<String> encodedPathSegments = new ArrayList();
        @Nullable
        List<String> encodedQueryNamesAndValues;
        String encodedUsername = "";
        @Nullable
        String host;
        int port = -1;
        @Nullable
        String scheme;

        enum ParseResult {
            SUCCESS,
            MISSING_SCHEME,
            UNSUPPORTED_SCHEME,
            INVALID_PORT,
            INVALID_HOST
        }

        public Builder() {
            this.encodedPathSegments.add("");
        }

        public Builder scheme(String scheme2) {
            if (scheme2 == null) {
                throw new NullPointerException("scheme == null");
            }
            if (scheme2.equalsIgnoreCase("http")) {
                this.scheme = "http";
            } else if (scheme2.equalsIgnoreCase("https")) {
                this.scheme = "https";
            } else {
                throw new IllegalArgumentException("unexpected scheme: " + scheme2);
            }
            return this;
        }

        public Builder username(String username) {
            if (username == null) {
                throw new NullPointerException("username == null");
            }
            this.encodedUsername = HttpUrl.canonicalize(username, " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
            return this;
        }

        public Builder encodedUsername(String encodedUsername2) {
            if (encodedUsername2 == null) {
                throw new NullPointerException("encodedUsername == null");
            }
            this.encodedUsername = HttpUrl.canonicalize(encodedUsername2, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
            return this;
        }

        public Builder password(String password) {
            if (password == null) {
                throw new NullPointerException("password == null");
            }
            this.encodedPassword = HttpUrl.canonicalize(password, " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
            return this;
        }

        public Builder encodedPassword(String encodedPassword2) {
            if (encodedPassword2 == null) {
                throw new NullPointerException("encodedPassword == null");
            }
            this.encodedPassword = HttpUrl.canonicalize(encodedPassword2, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
            return this;
        }

        public Builder host(String host2) {
            if (host2 == null) {
                throw new NullPointerException("host == null");
            }
            String encoded = canonicalizeHost(host2, 0, host2.length());
            if (encoded == null) {
                throw new IllegalArgumentException("unexpected host: " + host2);
            }
            this.host = encoded;
            return this;
        }

        public Builder port(int port2) {
            if (port2 <= 0 || port2 > 65535) {
                throw new IllegalArgumentException("unexpected port: " + port2);
            }
            this.port = port2;
            return this;
        }

        /* access modifiers changed from: package-private */
        public int effectivePort() {
            return this.port != -1 ? this.port : HttpUrl.defaultPort(this.scheme);
        }

        public Builder addPathSegment(String pathSegment) {
            if (pathSegment == null) {
                throw new NullPointerException("pathSegment == null");
            }
            push(pathSegment, 0, pathSegment.length(), false, false);
            return this;
        }

        public Builder addPathSegments(String pathSegments) {
            if (pathSegments != null) {
                return addPathSegments(pathSegments, false);
            }
            throw new NullPointerException("pathSegments == null");
        }

        public Builder addEncodedPathSegment(String encodedPathSegment) {
            if (encodedPathSegment == null) {
                throw new NullPointerException("encodedPathSegment == null");
            }
            push(encodedPathSegment, 0, encodedPathSegment.length(), false, true);
            return this;
        }

        public Builder addEncodedPathSegments(String encodedPathSegments2) {
            if (encodedPathSegments2 != null) {
                return addPathSegments(encodedPathSegments2, true);
            }
            throw new NullPointerException("encodedPathSegments == null");
        }

        private Builder addPathSegments(String pathSegments, boolean alreadyEncoded) {
            int offset = 0;
            do {
                int segmentEnd = Util.delimiterOffset(pathSegments, offset, pathSegments.length(), "/\\");
                push(pathSegments, offset, segmentEnd, segmentEnd < pathSegments.length(), alreadyEncoded);
                offset = segmentEnd + 1;
            } while (offset <= pathSegments.length());
            return this;
        }

        public Builder setPathSegment(int index, String pathSegment) {
            if (pathSegment == null) {
                throw new NullPointerException("pathSegment == null");
            }
            String canonicalPathSegment = HttpUrl.canonicalize(pathSegment, 0, pathSegment.length(), HttpUrl.PATH_SEGMENT_ENCODE_SET, false, false, false, true, (Charset) null);
            if (isDot(canonicalPathSegment) || isDotDot(canonicalPathSegment)) {
                throw new IllegalArgumentException("unexpected path segment: " + pathSegment);
            }
            this.encodedPathSegments.set(index, canonicalPathSegment);
            return this;
        }

        public Builder setEncodedPathSegment(int index, String encodedPathSegment) {
            if (encodedPathSegment == null) {
                throw new NullPointerException("encodedPathSegment == null");
            }
            String canonicalPathSegment = HttpUrl.canonicalize(encodedPathSegment, 0, encodedPathSegment.length(), HttpUrl.PATH_SEGMENT_ENCODE_SET, true, false, false, true, (Charset) null);
            this.encodedPathSegments.set(index, canonicalPathSegment);
            if (!isDot(canonicalPathSegment) && !isDotDot(canonicalPathSegment)) {
                return this;
            }
            throw new IllegalArgumentException("unexpected path segment: " + encodedPathSegment);
        }

        public Builder removePathSegment(int index) {
            this.encodedPathSegments.remove(index);
            if (this.encodedPathSegments.isEmpty()) {
                this.encodedPathSegments.add("");
            }
            return this;
        }

        public Builder encodedPath(String encodedPath) {
            if (encodedPath == null) {
                throw new NullPointerException("encodedPath == null");
            } else if (!encodedPath.startsWith("/")) {
                throw new IllegalArgumentException("unexpected encodedPath: " + encodedPath);
            } else {
                resolvePath(encodedPath, 0, encodedPath.length());
                return this;
            }
        }

        public Builder query(@Nullable String query) {
            List<String> list;
            if (query != null) {
                list = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(query, HttpUrl.QUERY_ENCODE_SET, false, false, true, true));
            } else {
                list = null;
            }
            this.encodedQueryNamesAndValues = list;
            return this;
        }

        public Builder encodedQuery(@Nullable String encodedQuery) {
            List<String> list;
            if (encodedQuery != null) {
                list = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(encodedQuery, HttpUrl.QUERY_ENCODE_SET, true, false, true, true));
            } else {
                list = null;
            }
            this.encodedQueryNamesAndValues = list;
            return this;
        }

        public Builder addQueryParameter(String name, @Nullable String value) {
            String str;
            if (name == null) {
                throw new NullPointerException("name == null");
            }
            if (this.encodedQueryNamesAndValues == null) {
                this.encodedQueryNamesAndValues = new ArrayList();
            }
            this.encodedQueryNamesAndValues.add(HttpUrl.canonicalize(name, HttpUrl.QUERY_COMPONENT_ENCODE_SET, false, false, true, true));
            List<String> list = this.encodedQueryNamesAndValues;
            if (value != null) {
                str = HttpUrl.canonicalize(value, HttpUrl.QUERY_COMPONENT_ENCODE_SET, false, false, true, true);
            } else {
                str = null;
            }
            list.add(str);
            return this;
        }

        public Builder addEncodedQueryParameter(String encodedName, @Nullable String encodedValue) {
            String str;
            if (encodedName == null) {
                throw new NullPointerException("encodedName == null");
            }
            if (this.encodedQueryNamesAndValues == null) {
                this.encodedQueryNamesAndValues = new ArrayList();
            }
            this.encodedQueryNamesAndValues.add(HttpUrl.canonicalize(encodedName, HttpUrl.QUERY_COMPONENT_REENCODE_SET, true, false, true, true));
            List<String> list = this.encodedQueryNamesAndValues;
            if (encodedValue != null) {
                str = HttpUrl.canonicalize(encodedValue, HttpUrl.QUERY_COMPONENT_REENCODE_SET, true, false, true, true);
            } else {
                str = null;
            }
            list.add(str);
            return this;
        }

        public Builder setQueryParameter(String name, @Nullable String value) {
            removeAllQueryParameters(name);
            addQueryParameter(name, value);
            return this;
        }

        public Builder setEncodedQueryParameter(String encodedName, @Nullable String encodedValue) {
            removeAllEncodedQueryParameters(encodedName);
            addEncodedQueryParameter(encodedName, encodedValue);
            return this;
        }

        public Builder removeAllQueryParameters(String name) {
            if (name == null) {
                throw new NullPointerException("name == null");
            } else if (this.encodedQueryNamesAndValues == null) {
                return this;
            } else {
                removeAllCanonicalQueryParameters(HttpUrl.canonicalize(name, HttpUrl.QUERY_COMPONENT_ENCODE_SET, false, false, true, true));
                return this;
            }
        }

        public Builder removeAllEncodedQueryParameters(String encodedName) {
            if (encodedName == null) {
                throw new NullPointerException("encodedName == null");
            } else if (this.encodedQueryNamesAndValues == null) {
                return this;
            } else {
                removeAllCanonicalQueryParameters(HttpUrl.canonicalize(encodedName, HttpUrl.QUERY_COMPONENT_REENCODE_SET, true, false, true, true));
                return this;
            }
        }

        private void removeAllCanonicalQueryParameters(String canonicalName) {
            for (int i = this.encodedQueryNamesAndValues.size() - 2; i >= 0; i -= 2) {
                if (canonicalName.equals(this.encodedQueryNamesAndValues.get(i))) {
                    this.encodedQueryNamesAndValues.remove(i + 1);
                    this.encodedQueryNamesAndValues.remove(i);
                    if (this.encodedQueryNamesAndValues.isEmpty()) {
                        this.encodedQueryNamesAndValues = null;
                        return;
                    }
                }
            }
        }

        public Builder fragment(@Nullable String fragment) {
            String str;
            if (fragment != null) {
                str = HttpUrl.canonicalize(fragment, "", false, false, false, false);
            } else {
                str = null;
            }
            this.encodedFragment = str;
            return this;
        }

        public Builder encodedFragment(@Nullable String encodedFragment2) {
            String str;
            if (encodedFragment2 != null) {
                str = HttpUrl.canonicalize(encodedFragment2, "", true, false, false, false);
            } else {
                str = null;
            }
            this.encodedFragment = str;
            return this;
        }

        /* access modifiers changed from: package-private */
        public Builder reencodeForUri() {
            int size = this.encodedPathSegments.size();
            for (int i = 0; i < size; i++) {
                this.encodedPathSegments.set(i, HttpUrl.canonicalize(this.encodedPathSegments.get(i), HttpUrl.PATH_SEGMENT_ENCODE_SET_URI, true, true, false, true));
            }
            if (this.encodedQueryNamesAndValues != null) {
                int size2 = this.encodedQueryNamesAndValues.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    String component = this.encodedQueryNamesAndValues.get(i2);
                    if (component != null) {
                        this.encodedQueryNamesAndValues.set(i2, HttpUrl.canonicalize(component, HttpUrl.QUERY_COMPONENT_ENCODE_SET_URI, true, true, true, true));
                    }
                }
            }
            if (this.encodedFragment != null) {
                this.encodedFragment = HttpUrl.canonicalize(this.encodedFragment, HttpUrl.FRAGMENT_ENCODE_SET_URI, true, true, false, false);
            }
            return this;
        }

        public HttpUrl build() {
            if (this.scheme == null) {
                throw new IllegalStateException("scheme == null");
            } else if (this.host != null) {
                return new HttpUrl(this);
            } else {
                throw new IllegalStateException("host == null");
            }
        }

        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append(this.scheme);
            result.append("://");
            if (!this.encodedUsername.isEmpty() || !this.encodedPassword.isEmpty()) {
                result.append(this.encodedUsername);
                if (!this.encodedPassword.isEmpty()) {
                    result.append(':');
                    result.append(this.encodedPassword);
                }
                result.append('@');
            }
            if (this.host.indexOf(58) != -1) {
                result.append('[');
                result.append(this.host);
                result.append(']');
            } else {
                result.append(this.host);
            }
            int effectivePort = effectivePort();
            if (effectivePort != HttpUrl.defaultPort(this.scheme)) {
                result.append(':');
                result.append(effectivePort);
            }
            HttpUrl.pathSegmentsToString(result, this.encodedPathSegments);
            if (this.encodedQueryNamesAndValues != null) {
                result.append('?');
                HttpUrl.namesAndValuesToQueryString(result, this.encodedQueryNamesAndValues);
            }
            if (this.encodedFragment != null) {
                result.append('#');
                result.append(this.encodedFragment);
            }
            return result.toString();
        }

        /* access modifiers changed from: package-private */
        public ParseResult parse(@Nullable HttpUrl base, String input) {
            char c;
            int pos;
            int componentDelimiterOffset;
            char c2;
            int i;
            int componentDelimiterOffset2;
            String str;
            HttpUrl httpUrl = base;
            String str2 = input;
            int pos2 = Util.skipLeadingAsciiWhitespace(str2, 0, input.length());
            int limit = Util.skipTrailingAsciiWhitespace(str2, pos2, input.length());
            int i2 = -1;
            if (schemeDelimiterOffset(str2, pos2, limit) != -1) {
                if (str2.regionMatches(true, pos2, "https:", 0, 6)) {
                    this.scheme = "https";
                    pos2 += "https:".length();
                } else if (!str2.regionMatches(true, pos2, "http:", 0, 5)) {
                    return ParseResult.UNSUPPORTED_SCHEME;
                } else {
                    this.scheme = "http";
                    pos2 += "http:".length();
                }
            } else if (httpUrl == null) {
                return ParseResult.MISSING_SCHEME;
            } else {
                this.scheme = httpUrl.scheme;
            }
            int slashCount = slashCount(str2, pos2, limit);
            int i3 = 35;
            if (slashCount >= 2 || httpUrl == null || !httpUrl.scheme.equals(this.scheme)) {
                boolean hasUsername = false;
                boolean hasPassword = false;
                int pos3 = pos2 + slashCount;
                while (true) {
                    componentDelimiterOffset = Util.delimiterOffset(str2, pos3, limit, "@/\\?#");
                    if (componentDelimiterOffset != limit) {
                        c2 = str2.charAt(componentDelimiterOffset);
                    } else {
                        c2 = 65535;
                    }
                    i = c2;
                    if (!(i == i2 || i == i3 || i == 47 || i == 92)) {
                        switch (i) {
                            case 63:
                                break;
                            case 64:
                                if (!hasPassword) {
                                    int passwordColonOffset = Util.delimiterOffset(str2, pos3, componentDelimiterOffset, ':');
                                    int passwordColonOffset2 = passwordColonOffset;
                                    char c3 = i;
                                    int componentDelimiterOffset3 = componentDelimiterOffset;
                                    int i4 = pos3;
                                    String canonicalUsername = HttpUrl.canonicalize(str2, pos3, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true, (Charset) null);
                                    if (hasUsername) {
                                        str = this.encodedUsername + "%40" + canonicalUsername;
                                    } else {
                                        str = canonicalUsername;
                                    }
                                    this.encodedUsername = str;
                                    int componentDelimiterOffset4 = componentDelimiterOffset3;
                                    if (passwordColonOffset2 != componentDelimiterOffset4) {
                                        hasPassword = true;
                                        componentDelimiterOffset2 = componentDelimiterOffset4;
                                        String str3 = canonicalUsername;
                                        this.encodedPassword = HttpUrl.canonicalize(str2, passwordColonOffset2 + 1, componentDelimiterOffset4, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true, (Charset) null);
                                    } else {
                                        componentDelimiterOffset2 = componentDelimiterOffset4;
                                        String str4 = canonicalUsername;
                                    }
                                    hasUsername = true;
                                } else {
                                    int c4 = i;
                                    componentDelimiterOffset2 = componentDelimiterOffset;
                                    this.encodedPassword += "%40" + HttpUrl.canonicalize(str2, pos3, componentDelimiterOffset2, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true, (Charset) null);
                                }
                                pos3 = componentDelimiterOffset2 + 1;
                                continue;
                            default:
                                continue;
                        }
                    }
                    HttpUrl httpUrl2 = base;
                    i3 = 35;
                    i2 = -1;
                }
                int i5 = i;
                int componentDelimiterOffset5 = componentDelimiterOffset;
                c = '#';
                int pos4 = pos3;
                int portColonOffset = portColonOffset(str2, pos4, componentDelimiterOffset5);
                if (portColonOffset + 1 < componentDelimiterOffset5) {
                    this.host = canonicalizeHost(str2, pos4, portColonOffset);
                    this.port = parsePort(str2, portColonOffset + 1, componentDelimiterOffset5);
                    if (this.port == -1) {
                        return ParseResult.INVALID_PORT;
                    }
                } else {
                    this.host = canonicalizeHost(str2, pos4, portColonOffset);
                    this.port = HttpUrl.defaultPort(this.scheme);
                }
                if (this.host == null) {
                    return ParseResult.INVALID_HOST;
                }
                pos2 = componentDelimiterOffset5;
            } else {
                this.encodedUsername = base.encodedUsername();
                this.encodedPassword = base.encodedPassword();
                this.host = httpUrl.host;
                this.port = httpUrl.port;
                this.encodedPathSegments.clear();
                this.encodedPathSegments.addAll(base.encodedPathSegments());
                if (pos2 == limit || str2.charAt(pos2) == '#') {
                    encodedQuery(base.encodedQuery());
                }
                c = '#';
            }
            int pathDelimiterOffset = Util.delimiterOffset(str2, pos2, limit, "?#");
            resolvePath(str2, pos2, pathDelimiterOffset);
            int pos5 = pathDelimiterOffset;
            if (pos5 >= limit || str2.charAt(pos5) != '?') {
                pos = pos5;
            } else {
                int queryDelimiterOffset = Util.delimiterOffset(str2, pos5, limit, c);
                int i6 = pos5;
                this.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(str2, pos5 + 1, queryDelimiterOffset, HttpUrl.QUERY_ENCODE_SET, true, false, true, true, (Charset) null));
                pos = queryDelimiterOffset;
            }
            if (pos >= limit || str2.charAt(pos) != c) {
            } else {
                int i7 = pos;
                this.encodedFragment = HttpUrl.canonicalize(str2, pos + 1, limit, "", true, false, false, false, (Charset) null);
            }
            return ParseResult.SUCCESS;
        }

        private void resolvePath(String input, int pos, int limit) {
            if (pos != limit) {
                char c = input.charAt(pos);
                if (c == '/' || c == '\\') {
                    this.encodedPathSegments.clear();
                    this.encodedPathSegments.add("");
                    pos++;
                } else {
                    this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, "");
                }
                int i = pos;
                while (i < limit) {
                    int pathSegmentDelimiterOffset = Util.delimiterOffset(input, i, limit, "/\\");
                    boolean segmentHasTrailingSlash = pathSegmentDelimiterOffset < limit;
                    push(input, i, pathSegmentDelimiterOffset, segmentHasTrailingSlash, true);
                    i = pathSegmentDelimiterOffset;
                    if (segmentHasTrailingSlash) {
                        i++;
                    }
                }
            }
        }

        private void push(String input, int pos, int limit, boolean addTrailingSlash, boolean alreadyEncoded) {
            String segment = HttpUrl.canonicalize(input, pos, limit, HttpUrl.PATH_SEGMENT_ENCODE_SET, alreadyEncoded, false, false, true, (Charset) null);
            if (!isDot(segment)) {
                if (isDotDot(segment)) {
                    pop();
                    return;
                }
                if (this.encodedPathSegments.get(this.encodedPathSegments.size() - 1).isEmpty()) {
                    this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, segment);
                } else {
                    this.encodedPathSegments.add(segment);
                }
                if (addTrailingSlash) {
                    this.encodedPathSegments.add("");
                }
            }
        }

        private boolean isDot(String input) {
            return input.equals(".") || input.equalsIgnoreCase("%2e");
        }

        private boolean isDotDot(String input) {
            return input.equals("..") || input.equalsIgnoreCase("%2e.") || input.equalsIgnoreCase(".%2e") || input.equalsIgnoreCase("%2e%2e");
        }

        private void pop() {
            if (!this.encodedPathSegments.remove(this.encodedPathSegments.size() - 1).isEmpty() || this.encodedPathSegments.isEmpty()) {
                this.encodedPathSegments.add("");
            } else {
                this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, "");
            }
        }

        private static int schemeDelimiterOffset(String input, int pos, int limit) {
            if (limit - pos < 2) {
                return -1;
            }
            char c0 = input.charAt(pos);
            if ((c0 < 'a' || c0 > 'z') && (c0 < 'A' || c0 > 'Z')) {
                return -1;
            }
            int i = pos + 1;
            while (i < limit) {
                char c = input.charAt(i);
                if ((c >= 'a' && c <= 'z') || ((c >= 'A' && c <= 'Z') || ((c >= '0' && c <= '9') || c == '+' || c == '-' || c == '.'))) {
                    i++;
                } else if (c == ':') {
                    return i;
                } else {
                    return -1;
                }
            }
            return -1;
        }

        private static int slashCount(String input, int pos, int limit) {
            int slashCount = 0;
            while (pos < limit) {
                char c = input.charAt(pos);
                if (c != '\\' && c != '/') {
                    break;
                }
                slashCount++;
                pos++;
            }
            return slashCount;
        }

        private static int portColonOffset(String input, int pos, int limit) {
            int i = pos;
            while (i < limit) {
                char charAt = input.charAt(i);
                if (charAt == ':') {
                    return i;
                }
                if (charAt == '[') {
                    do {
                        i++;
                        if (i >= limit) {
                            break;
                        }
                    } while (input.charAt(i) == ']');
                }
                i++;
            }
            return limit;
        }

        private static String canonicalizeHost(String input, int pos, int limit) {
            return Util.canonicalizeHost(HttpUrl.percentDecode(input, pos, limit, false));
        }

        private static int parsePort(String input, int pos, int limit) {
            try {
                int i = Integer.parseInt(HttpUrl.canonicalize(input, pos, limit, "", false, false, false, true, (Charset) null));
                if (i <= 0 || i > 65535) {
                    return -1;
                }
                return i;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
    }

    static String percentDecode(String encoded, boolean plusIsSpace) {
        return percentDecode(encoded, 0, encoded.length(), plusIsSpace);
    }

    private List<String> percentDecode(List<String> list, boolean plusIsSpace) {
        int size = list.size();
        List<String> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String s = list.get(i);
            result.add(s != null ? percentDecode(s, plusIsSpace) : null);
        }
        return Collections.unmodifiableList(result);
    }

    static String percentDecode(String encoded, int pos, int limit, boolean plusIsSpace) {
        for (int i = pos; i < limit; i++) {
            char c = encoded.charAt(i);
            if (c == '%' || (c == '+' && plusIsSpace)) {
                Buffer out = new Buffer();
                out.writeUtf8(encoded, pos, i);
                percentDecode(out, encoded, i, limit, plusIsSpace);
                return out.readUtf8();
            }
        }
        return encoded.substring(pos, limit);
    }

    static void percentDecode(Buffer out, String encoded, int pos, int limit, boolean plusIsSpace) {
        int i = pos;
        while (i < limit) {
            int codePoint = encoded.codePointAt(i);
            if (codePoint == 37 && i + 2 < limit) {
                int d1 = Util.decodeHexDigit(encoded.charAt(i + 1));
                int d2 = Util.decodeHexDigit(encoded.charAt(i + 2));
                if (!(d1 == -1 || d2 == -1)) {
                    out.writeByte((d1 << 4) + d2);
                    i += 2;
                    i += Character.charCount(codePoint);
                }
            } else if (codePoint == 43 && plusIsSpace) {
                out.writeByte(32);
                i += Character.charCount(codePoint);
            }
            out.writeUtf8CodePoint(codePoint);
            i += Character.charCount(codePoint);
        }
    }

    static boolean percentEncoded(String encoded, int pos, int limit) {
        if (pos + 2 >= limit || encoded.charAt(pos) != '%' || Util.decodeHexDigit(encoded.charAt(pos + 1)) == -1 || Util.decodeHexDigit(encoded.charAt(pos + 2)) == -1) {
            return false;
        }
        return true;
    }

    static String canonicalize(String input, int pos, int limit, String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly, Charset charset) {
        int i;
        String str;
        String str2 = input;
        int i2 = limit;
        int i3 = pos;
        while (true) {
            i = i3;
            if (i < i2) {
                int codePoint = str2.codePointAt(i);
                if (codePoint >= 32 && codePoint != 127 && (codePoint < 128 || !asciiOnly)) {
                    str = encodeSet;
                    if (str.indexOf(codePoint) != -1 || ((codePoint == 37 && (!alreadyEncoded || (strict && !percentEncoded(str2, i, i2)))) || (codePoint == 43 && plusIsSpace))) {
                        break;
                    }
                    i3 = Character.charCount(codePoint) + i;
                } else {
                    str = encodeSet;
                }
            } else {
                String str3 = encodeSet;
                return input.substring(pos, limit);
            }
        }
        str = encodeSet;
        Buffer buffer = new Buffer();
        Buffer out = buffer;
        out.writeUtf8(str2, pos, i);
        int i4 = i;
        Buffer out2 = out;
        canonicalize(buffer, str2, i, i2, str, alreadyEncoded, strict, plusIsSpace, asciiOnly, charset);
        return out2.readUtf8();
    }

    static void canonicalize(Buffer out, String input, int pos, int limit, String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly, Charset charset) {
        Buffer buffer = out;
        String str = input;
        int i = limit;
        Charset charset2 = charset;
        Buffer encodedCharBuffer = null;
        int i2 = pos;
        while (i2 < i) {
            int codePoint = str.codePointAt(i2);
            if (!alreadyEncoded || !(codePoint == 9 || codePoint == 10 || codePoint == 12 || codePoint == 13)) {
                if (codePoint != 43 || !plusIsSpace) {
                    if (codePoint < 32 || codePoint == 127 || (codePoint >= 128 && asciiOnly)) {
                        String str2 = encodeSet;
                    } else if (encodeSet.indexOf(codePoint) == -1 && (codePoint != 37 || (alreadyEncoded && (!strict || percentEncoded(str, i2, i))))) {
                        buffer.writeUtf8CodePoint(codePoint);
                        i2 += Character.charCount(codePoint);
                    }
                    if (encodedCharBuffer == null) {
                        encodedCharBuffer = new Buffer();
                    }
                    if (charset2 == null || charset2.equals(Util.UTF_8)) {
                        encodedCharBuffer.writeUtf8CodePoint(codePoint);
                    } else {
                        encodedCharBuffer.writeString(str, i2, Character.charCount(codePoint) + i2, charset2);
                    }
                    while (!encodedCharBuffer.exhausted()) {
                        int b = encodedCharBuffer.readByte() & 255;
                        buffer.writeByte(37);
                        buffer.writeByte((int) HEX_DIGITS[(b >> 4) & 15]);
                        buffer.writeByte((int) HEX_DIGITS[b & 15]);
                    }
                    i2 += Character.charCount(codePoint);
                } else {
                    buffer.writeUtf8(alreadyEncoded ? "+" : "%2B");
                }
            }
            String str3 = encodeSet;
            i2 += Character.charCount(codePoint);
        }
        String str4 = encodeSet;
    }

    static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly, Charset charset) {
        return canonicalize(input, 0, input.length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly, charset);
    }

    static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly) {
        return canonicalize(input, 0, input.length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly, (Charset) null);
    }
}
