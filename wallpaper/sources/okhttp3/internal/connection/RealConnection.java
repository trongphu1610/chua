package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.net.ConnectException;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import kotlin.jvm.internal.LongCompanionObject;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.CertificatePinner;
import okhttp3.Connection;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.Version;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http1.Http1Codec;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Http2Codec;
import okhttp3.internal.http2.Http2Connection;
import okhttp3.internal.http2.Http2Stream;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.tls.OkHostnameVerifier;
import okhttp3.internal.ws.RealWebSocket;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

public final class RealConnection extends Http2Connection.Listener implements Connection {
    private static final int MAX_TUNNEL_ATTEMPTS = 21;
    private static final String NPE_THROW_WITH_NULL = "throw with null exception";
    public int allocationLimit = 1;
    public final List<Reference<StreamAllocation>> allocations = new ArrayList();
    private final ConnectionPool connectionPool;
    private Handshake handshake;
    private Http2Connection http2Connection;
    public long idleAtNanos = LongCompanionObject.MAX_VALUE;
    public boolean noNewStreams;
    private Protocol protocol;
    private Socket rawSocket;
    private final Route route;
    private BufferedSink sink;
    private Socket socket;
    private BufferedSource source;
    public int successCount;

    public RealConnection(ConnectionPool connectionPool2, Route route2) {
        this.connectionPool = connectionPool2;
        this.route = route2;
    }

    public static RealConnection testConnection(ConnectionPool connectionPool2, Route route2, Socket socket2, long idleAtNanos2) {
        RealConnection result = new RealConnection(connectionPool2, route2);
        result.socket = socket2;
        result.idleAtNanos = idleAtNanos2;
        return result;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0086 A[Catch:{ IOException -> 0x00f4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00cd  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00da  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x012c  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0135  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void connect(int r18, int r19, int r20, int r21, boolean r22, okhttp3.Call r23, okhttp3.EventListener r24) {
        /*
            r17 = this;
            r7 = r17
            r8 = r23
            r9 = r24
            okhttp3.Protocol r1 = r7.protocol
            if (r1 == 0) goto L_0x0012
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.String r2 = "already connected"
            r1.<init>(r2)
            throw r1
        L_0x0012:
            r1 = 0
            okhttp3.Route r2 = r7.route
            okhttp3.Address r2 = r2.address()
            java.util.List r10 = r2.connectionSpecs()
            okhttp3.internal.connection.ConnectionSpecSelector r2 = new okhttp3.internal.connection.ConnectionSpecSelector
            r2.<init>(r10)
            r11 = r2
            okhttp3.Route r2 = r7.route
            okhttp3.Address r2 = r2.address()
            javax.net.ssl.SSLSocketFactory r2 = r2.sslSocketFactory()
            if (r2 != 0) goto L_0x007d
            okhttp3.ConnectionSpec r2 = okhttp3.ConnectionSpec.CLEARTEXT
            boolean r2 = r10.contains(r2)
            if (r2 != 0) goto L_0x0044
            okhttp3.internal.connection.RouteException r2 = new okhttp3.internal.connection.RouteException
            java.net.UnknownServiceException r3 = new java.net.UnknownServiceException
            java.lang.String r4 = "CLEARTEXT communication not enabled for client"
            r3.<init>(r4)
            r2.<init>(r3)
            throw r2
        L_0x0044:
            okhttp3.Route r2 = r7.route
            okhttp3.Address r2 = r2.address()
            okhttp3.HttpUrl r2 = r2.url()
            java.lang.String r2 = r2.host()
            okhttp3.internal.platform.Platform r3 = okhttp3.internal.platform.Platform.get()
            boolean r3 = r3.isCleartextTrafficPermitted(r2)
            if (r3 != 0) goto L_0x007d
            okhttp3.internal.connection.RouteException r3 = new okhttp3.internal.connection.RouteException
            java.net.UnknownServiceException r4 = new java.net.UnknownServiceException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "CLEARTEXT communication to "
            r5.append(r6)
            r5.append(r2)
            java.lang.String r6 = " not permitted by network security policy"
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r4.<init>(r5)
            r3.<init>(r4)
            throw r3
        L_0x007d:
            r12 = r1
        L_0x007e:
            okhttp3.Route r1 = r7.route     // Catch:{ IOException -> 0x00f4 }
            boolean r1 = r1.requiresTunnel()     // Catch:{ IOException -> 0x00f4 }
            if (r1 == 0) goto L_0x00a3
            r1 = r7
            r2 = r18
            r3 = r19
            r4 = r20
            r5 = r8
            r6 = r9
            r1.connectTunnel(r2, r3, r4, r5, r6)     // Catch:{ IOException -> 0x00f4 }
            java.net.Socket r1 = r7.rawSocket     // Catch:{ IOException -> 0x00f4 }
            if (r1 != 0) goto L_0x009e
            r13 = r18
            r14 = r19
            r15 = r21
            goto L_0x00c1
        L_0x009e:
            r13 = r18
            r14 = r19
            goto L_0x00aa
        L_0x00a3:
            r13 = r18
            r14 = r19
            r7.connectSocket(r13, r14, r8, r9)     // Catch:{ IOException -> 0x00f2 }
        L_0x00aa:
            r15 = r21
            r7.establishProtocol(r11, r15, r8, r9)     // Catch:{ IOException -> 0x00f0 }
            okhttp3.Route r1 = r7.route     // Catch:{ IOException -> 0x00f0 }
            java.net.InetSocketAddress r1 = r1.socketAddress()     // Catch:{ IOException -> 0x00f0 }
            okhttp3.Route r2 = r7.route     // Catch:{ IOException -> 0x00f0 }
            java.net.Proxy r2 = r2.proxy()     // Catch:{ IOException -> 0x00f0 }
            okhttp3.Protocol r3 = r7.protocol     // Catch:{ IOException -> 0x00f0 }
            r9.connectEnd(r8, r1, r2, r3)     // Catch:{ IOException -> 0x00f0 }
        L_0x00c1:
            okhttp3.Route r1 = r7.route
            boolean r1 = r1.requiresTunnel()
            if (r1 == 0) goto L_0x00da
            java.net.Socket r1 = r7.rawSocket
            if (r1 != 0) goto L_0x00da
            java.net.ProtocolException r1 = new java.net.ProtocolException
            java.lang.String r2 = "Too many tunnel connections attempted: 21"
            r1.<init>(r2)
            okhttp3.internal.connection.RouteException r2 = new okhttp3.internal.connection.RouteException
            r2.<init>(r1)
            throw r2
        L_0x00da:
            okhttp3.internal.http2.Http2Connection r1 = r7.http2Connection
            if (r1 == 0) goto L_0x00ef
            okhttp3.ConnectionPool r1 = r7.connectionPool
            monitor-enter(r1)
            okhttp3.internal.http2.Http2Connection r2 = r7.http2Connection     // Catch:{ all -> 0x00eb }
            int r2 = r2.maxConcurrentStreams()     // Catch:{ all -> 0x00eb }
            r7.allocationLimit = r2     // Catch:{ all -> 0x00eb }
            monitor-exit(r1)     // Catch:{ all -> 0x00eb }
            goto L_0x00ef
        L_0x00eb:
            r0 = move-exception
            r2 = r0
            monitor-exit(r1)     // Catch:{ all -> 0x00eb }
            throw r2
        L_0x00ef:
            return
        L_0x00f0:
            r0 = move-exception
            goto L_0x00fb
        L_0x00f2:
            r0 = move-exception
            goto L_0x00f9
        L_0x00f4:
            r0 = move-exception
            r13 = r18
            r14 = r19
        L_0x00f9:
            r15 = r21
        L_0x00fb:
            r1 = r0
            r6 = r1
            java.net.Socket r1 = r7.socket
            okhttp3.internal.Util.closeQuietly((java.net.Socket) r1)
            java.net.Socket r1 = r7.rawSocket
            okhttp3.internal.Util.closeQuietly((java.net.Socket) r1)
            r1 = 0
            r7.socket = r1
            r7.rawSocket = r1
            r7.source = r1
            r7.sink = r1
            r7.handshake = r1
            r7.protocol = r1
            r7.http2Connection = r1
            okhttp3.Route r1 = r7.route
            java.net.InetSocketAddress r3 = r1.socketAddress()
            okhttp3.Route r1 = r7.route
            java.net.Proxy r4 = r1.proxy()
            r5 = 0
            r1 = r9
            r2 = r8
            r16 = r6
            r1.connectFailed(r2, r3, r4, r5, r6)
            if (r12 != 0) goto L_0x0135
            okhttp3.internal.connection.RouteException r1 = new okhttp3.internal.connection.RouteException
            r2 = r16
            r1.<init>(r2)
            r12 = r1
            goto L_0x013a
        L_0x0135:
            r2 = r16
            r12.addConnectException(r2)
        L_0x013a:
            if (r22 == 0) goto L_0x0145
            boolean r3 = r11.connectionFailed(r2)
            if (r3 != 0) goto L_0x0143
            goto L_0x0145
        L_0x0143:
            goto L_0x007e
        L_0x0145:
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.RealConnection.connect(int, int, int, int, boolean, okhttp3.Call, okhttp3.EventListener):void");
    }

    private void connectTunnel(int connectTimeout, int readTimeout, int writeTimeout, Call call, EventListener eventListener) throws IOException {
        Request tunnelRequest = createTunnelRequest();
        HttpUrl url = tunnelRequest.url();
        int i = 0;
        while (i < 21) {
            connectSocket(connectTimeout, readTimeout, call, eventListener);
            tunnelRequest = createTunnel(readTimeout, writeTimeout, tunnelRequest, url);
            if (tunnelRequest != null) {
                Util.closeQuietly(this.rawSocket);
                this.rawSocket = null;
                this.sink = null;
                this.source = null;
                eventListener.connectEnd(call, this.route.socketAddress(), this.route.proxy(), (Protocol) null);
                i++;
            } else {
                return;
            }
        }
    }

    private void connectSocket(int connectTimeout, int readTimeout, Call call, EventListener eventListener) throws IOException {
        Socket socket2;
        Proxy proxy = this.route.proxy();
        Address address = this.route.address();
        if (proxy.type() == Proxy.Type.DIRECT || proxy.type() == Proxy.Type.HTTP) {
            socket2 = address.socketFactory().createSocket();
        } else {
            socket2 = new Socket(proxy);
        }
        this.rawSocket = socket2;
        eventListener.connectStart(call, this.route.socketAddress(), proxy);
        this.rawSocket.setSoTimeout(readTimeout);
        try {
            Platform.get().connectSocket(this.rawSocket, this.route.socketAddress(), connectTimeout);
            try {
                this.source = Okio.buffer(Okio.source(this.rawSocket));
                this.sink = Okio.buffer(Okio.sink(this.rawSocket));
            } catch (NullPointerException npe) {
                if (NPE_THROW_WITH_NULL.equals(npe.getMessage())) {
                    throw new IOException(npe);
                }
            }
        } catch (ConnectException e) {
            ConnectException ce = new ConnectException("Failed to connect to " + this.route.socketAddress());
            ce.initCause(e);
            throw ce;
        }
    }

    private void establishProtocol(ConnectionSpecSelector connectionSpecSelector, int pingIntervalMillis, Call call, EventListener eventListener) throws IOException {
        if (this.route.address().sslSocketFactory() == null) {
            this.protocol = Protocol.HTTP_1_1;
            this.socket = this.rawSocket;
            return;
        }
        eventListener.secureConnectStart(call);
        connectTls(connectionSpecSelector);
        eventListener.secureConnectEnd(call, this.handshake);
        if (this.protocol == Protocol.HTTP_2) {
            this.socket.setSoTimeout(0);
            this.http2Connection = new Http2Connection.Builder(true).socket(this.socket, this.route.address().url().host(), this.source, this.sink).listener(this).pingIntervalMillis(pingIntervalMillis).build();
            this.http2Connection.start();
        }
    }

    private void connectTls(ConnectionSpecSelector connectionSpecSelector) throws IOException {
        Protocol protocol2;
        Address address = this.route.address();
        String maybeProtocol = null;
        try {
            SSLSocket sslSocket = (SSLSocket) address.sslSocketFactory().createSocket(this.rawSocket, address.url().host(), address.url().port(), true);
            ConnectionSpec connectionSpec = connectionSpecSelector.configureSecureSocket(sslSocket);
            if (connectionSpec.supportsTlsExtensions()) {
                Platform.get().configureTlsExtensions(sslSocket, address.url().host(), address.protocols());
            }
            sslSocket.startHandshake();
            SSLSession sslSocketSession = sslSocket.getSession();
            if (!isValid(sslSocketSession)) {
                throw new IOException("a valid ssl session was not established");
            }
            Handshake unverifiedHandshake = Handshake.get(sslSocketSession);
            if (!address.hostnameVerifier().verify(address.url().host(), sslSocketSession)) {
                X509Certificate cert = (X509Certificate) unverifiedHandshake.peerCertificates().get(0);
                throw new SSLPeerUnverifiedException("Hostname " + address.url().host() + " not verified:\n    certificate: " + CertificatePinner.pin(cert) + "\n    DN: " + cert.getSubjectDN().getName() + "\n    subjectAltNames: " + OkHostnameVerifier.allSubjectAltNames(cert));
            }
            address.certificatePinner().check(address.url().host(), unverifiedHandshake.peerCertificates());
            if (connectionSpec.supportsTlsExtensions()) {
                maybeProtocol = Platform.get().getSelectedProtocol(sslSocket);
            }
            this.socket = sslSocket;
            this.source = Okio.buffer(Okio.source(this.socket));
            this.sink = Okio.buffer(Okio.sink(this.socket));
            this.handshake = unverifiedHandshake;
            if (maybeProtocol != null) {
                protocol2 = Protocol.get(maybeProtocol);
            } else {
                protocol2 = Protocol.HTTP_1_1;
            }
            this.protocol = protocol2;
            if (sslSocket != null) {
                Platform.get().afterHandshake(sslSocket);
            }
            if (1 == 0) {
                Util.closeQuietly((Socket) sslSocket);
            }
        } catch (AssertionError e) {
            if (Util.isAndroidGetsocknameError(e)) {
                throw new IOException(e);
            }
            throw e;
        } catch (Throwable th) {
            if (0 != 0) {
                Platform.get().afterHandshake((SSLSocket) null);
            }
            if (0 == 0) {
                Util.closeQuietly((Socket) null);
            }
            throw th;
        }
    }

    private boolean isValid(SSLSession sslSocketSession) {
        return !"NONE".equals(sslSocketSession.getProtocol()) && !"SSL_NULL_WITH_NULL_NULL".equals(sslSocketSession.getCipherSuite());
    }

    private Request createTunnel(int readTimeout, int writeTimeout, Request tunnelRequest, HttpUrl url) throws IOException {
        Response response;
        String requestLine = "CONNECT " + Util.hostHeader(url, true) + " HTTP/1.1";
        do {
            Http1Codec tunnelConnection = new Http1Codec((OkHttpClient) null, (StreamAllocation) null, this.source, this.sink);
            this.source.timeout().timeout((long) readTimeout, TimeUnit.MILLISECONDS);
            this.sink.timeout().timeout((long) writeTimeout, TimeUnit.MILLISECONDS);
            tunnelConnection.writeRequest(tunnelRequest.headers(), requestLine);
            tunnelConnection.finishRequest();
            response = tunnelConnection.readResponseHeaders(false).request(tunnelRequest).build();
            long contentLength = HttpHeaders.contentLength(response);
            if (contentLength == -1) {
                contentLength = 0;
            }
            Source body = tunnelConnection.newFixedLengthSource(contentLength);
            Util.skipAll(body, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
            body.close();
            int code = response.code();
            if (code != 200) {
                if (code != 407) {
                    throw new IOException("Unexpected response code for CONNECT: " + response.code());
                }
                tunnelRequest = this.route.address().proxyAuthenticator().authenticate(this.route, response);
                if (tunnelRequest == null) {
                    throw new IOException("Failed to authenticate with proxy");
                }
            } else if (this.source.buffer().exhausted() && this.sink.buffer().exhausted()) {
                return null;
            } else {
                throw new IOException("TLS tunnel buffered too many bytes!");
            }
        } while (!"close".equalsIgnoreCase(response.header("Connection")));
        return tunnelRequest;
    }

    private Request createTunnelRequest() {
        return new Request.Builder().url(this.route.address().url()).header("Host", Util.hostHeader(this.route.address().url(), true)).header("Proxy-Connection", "Keep-Alive").header("User-Agent", Version.userAgent()).build();
    }

    public boolean isEligible(Address address, @Nullable Route route2) {
        if (this.allocations.size() >= this.allocationLimit || this.noNewStreams || !Internal.instance.equalsNonHost(this.route.address(), address)) {
            return false;
        }
        if (address.url().host().equals(route().address().url().host())) {
            return true;
        }
        if (this.http2Connection == null || route2 == null || route2.proxy().type() != Proxy.Type.DIRECT || this.route.proxy().type() != Proxy.Type.DIRECT || !this.route.socketAddress().equals(route2.socketAddress()) || route2.address().hostnameVerifier() != OkHostnameVerifier.INSTANCE || !supportsUrl(address.url())) {
            return false;
        }
        try {
            address.certificatePinner().check(address.url().host(), handshake().peerCertificates());
            return true;
        } catch (SSLPeerUnverifiedException e) {
            return false;
        }
    }

    public boolean supportsUrl(HttpUrl url) {
        if (url.port() != this.route.address().url().port()) {
            return false;
        }
        if (url.host().equals(this.route.address().url().host())) {
            return true;
        }
        if (this.handshake == null || !OkHostnameVerifier.INSTANCE.verify(url.host(), (X509Certificate) this.handshake.peerCertificates().get(0))) {
            return false;
        }
        return true;
    }

    public HttpCodec newCodec(OkHttpClient client, Interceptor.Chain chain, StreamAllocation streamAllocation) throws SocketException {
        if (this.http2Connection != null) {
            return new Http2Codec(client, chain, streamAllocation, this.http2Connection);
        }
        this.socket.setSoTimeout(chain.readTimeoutMillis());
        this.source.timeout().timeout((long) chain.readTimeoutMillis(), TimeUnit.MILLISECONDS);
        this.sink.timeout().timeout((long) chain.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
        return new Http1Codec(client, streamAllocation, this.source, this.sink);
    }

    public RealWebSocket.Streams newWebSocketStreams(StreamAllocation streamAllocation) {
        final StreamAllocation streamAllocation2 = streamAllocation;
        return new RealWebSocket.Streams(true, this.source, this.sink) {
            public void close() throws IOException {
                streamAllocation2.streamFinished(true, streamAllocation2.codec(), -1, (IOException) null);
            }
        };
    }

    public Route route() {
        return this.route;
    }

    public void cancel() {
        Util.closeQuietly(this.rawSocket);
    }

    public Socket socket() {
        return this.socket;
    }

    public boolean isHealthy(boolean doExtensiveChecks) {
        int readTimeout;
        if (this.socket.isClosed() || this.socket.isInputShutdown() || this.socket.isOutputShutdown()) {
            return false;
        }
        if (this.http2Connection != null) {
            return !this.http2Connection.isShutdown();
        }
        if (doExtensiveChecks) {
            try {
                readTimeout = this.socket.getSoTimeout();
                this.socket.setSoTimeout(1);
                if (this.source.exhausted()) {
                    this.socket.setSoTimeout(readTimeout);
                    return false;
                }
                this.socket.setSoTimeout(readTimeout);
                return true;
            } catch (SocketTimeoutException e) {
            } catch (IOException e2) {
                return false;
            } catch (Throwable th) {
                this.socket.setSoTimeout(readTimeout);
                throw th;
            }
        }
        return true;
    }

    public void onStream(Http2Stream stream) throws IOException {
        stream.close(ErrorCode.REFUSED_STREAM);
    }

    public void onSettings(Http2Connection connection) {
        synchronized (this.connectionPool) {
            this.allocationLimit = connection.maxConcurrentStreams();
        }
    }

    public Handshake handshake() {
        return this.handshake;
    }

    public boolean isMultiplexed() {
        return this.http2Connection != null;
    }

    public Protocol protocol() {
        return this.protocol;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Connection{");
        sb.append(this.route.address().url().host());
        sb.append(":");
        sb.append(this.route.address().url().port());
        sb.append(", proxy=");
        sb.append(this.route.proxy());
        sb.append(" hostAddress=");
        sb.append(this.route.socketAddress());
        sb.append(" cipherSuite=");
        sb.append(this.handshake != null ? this.handshake.cipherSuite() : "none");
        sb.append(" protocol=");
        sb.append(this.protocol);
        sb.append('}');
        return sb.toString();
    }
}
