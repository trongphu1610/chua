package okhttp3.internal.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpRetryException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.CertificatePinner;
import okhttp3.EventListener;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RouteException;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http2.ConnectionShutdownException;

public final class RetryAndFollowUpInterceptor implements Interceptor {
    private static final int MAX_FOLLOW_UPS = 20;
    private Object callStackTrace;
    private volatile boolean canceled;
    private final OkHttpClient client;
    private final boolean forWebSocket;
    private volatile StreamAllocation streamAllocation;

    public RetryAndFollowUpInterceptor(OkHttpClient client2, boolean forWebSocket2) {
        this.client = client2;
        this.forWebSocket = forWebSocket2;
    }

    public void cancel() {
        this.canceled = true;
        StreamAllocation streamAllocation2 = this.streamAllocation;
        if (streamAllocation2 != null) {
            streamAllocation2.cancel();
        }
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCallStackTrace(Object callStackTrace2) {
        this.callStackTrace = callStackTrace2;
    }

    public StreamAllocation streamAllocation() {
        return this.streamAllocation;
    }

    /* JADX WARNING: type inference failed for: r12v1 */
    /* JADX WARNING: type inference failed for: r12v2, types: [okhttp3.internal.http.HttpCodec, java.io.IOException, okhttp3.internal.connection.RealConnection, okhttp3.ResponseBody] */
    /* JADX WARNING: type inference failed for: r12v3 */
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request followUp;
        int followUpCount;
        Response response;
        Request request = chain.request();
        RealInterceptorChain realChain = (RealInterceptorChain) chain;
        Call call = realChain.call();
        EventListener eventListener = realChain.eventListener();
        StreamAllocation streamAllocation2 = new StreamAllocation(this.client.connectionPool(), createAddress(request.url()), call, eventListener, this.callStackTrace);
        this.streamAllocation = streamAllocation2;
        int followUpCount2 = 0;
        ? r12 = 0;
        Request request2 = request;
        StreamAllocation streamAllocation3 = streamAllocation2;
        Response priorResponse = null;
        while (!this.canceled) {
            boolean requestSendStarted = true;
            try {
                Response response2 = realChain.proceed(request2, streamAllocation3, r12, r12);
                if (0 != 0) {
                    streamAllocation3.streamFailed(r12);
                    streamAllocation3.release();
                }
                if (priorResponse != null) {
                    response2 = response2.newBuilder().priorResponse(priorResponse.newBuilder().body(r12).build()).build();
                }
                Response response3 = response2;
                Request followUp2 = followUpRequest(response3, streamAllocation3.route());
                if (followUp2 == null) {
                    if (!this.forWebSocket) {
                        streamAllocation3.release();
                    }
                    return response3;
                }
                Util.closeQuietly((Closeable) response3.body());
                int followUpCount3 = followUpCount2 + 1;
                if (followUpCount3 > 20) {
                    streamAllocation3.release();
                    throw new ProtocolException("Too many follow-up requests: " + followUpCount3);
                } else if (followUp2.body() instanceof UnrepeatableRequestBody) {
                    streamAllocation3.release();
                    throw new HttpRetryException("Cannot retry streamed HTTP body", response3.code());
                } else {
                    if (!sameConnection(response3, followUp2.url())) {
                        streamAllocation3.release();
                        followUpCount = followUpCount3;
                        followUp = followUp2;
                        response = response3;
                        StreamAllocation streamAllocation4 = new StreamAllocation(this.client.connectionPool(), createAddress(followUp2.url()), call, eventListener, this.callStackTrace);
                        this.streamAllocation = streamAllocation4;
                        streamAllocation3 = streamAllocation4;
                    } else {
                        followUpCount = followUpCount3;
                        followUp = followUp2;
                        response = response3;
                        if (streamAllocation3.codec() != null) {
                            throw new IllegalStateException("Closing the body of " + response + " didn't close its backing stream. Bad interceptor?");
                        }
                    }
                    request2 = followUp;
                    priorResponse = response;
                    followUpCount2 = followUpCount;
                    r12 = 0;
                }
            } catch (RouteException e) {
                RouteException e2 = e;
                if (!recover(e2.getLastConnectException(), streamAllocation3, false, request2)) {
                    throw e2.getLastConnectException();
                }
                if (0 == 0) {
                }
                streamAllocation3.streamFailed((IOException) null);
                streamAllocation3.release();
            } catch (IOException e3) {
                IOException e4 = e3;
                if (e4 instanceof ConnectionShutdownException) {
                    requestSendStarted = false;
                }
                if (!recover(e4, streamAllocation3, requestSendStarted, request2)) {
                    throw e4;
                } else if (0 != 0) {
                    streamAllocation3.streamFailed((IOException) null);
                    streamAllocation3.release();
                }
            } catch (Throwable th) {
                Throwable th2 = th;
                if (1 != 0) {
                    streamAllocation3.streamFailed((IOException) null);
                    streamAllocation3.release();
                }
                throw th2;
            }
        }
        streamAllocation3.release();
        throw new IOException("Canceled");
    }

    private Address createAddress(HttpUrl url) {
        SSLSocketFactory sslSocketFactory = null;
        HostnameVerifier hostnameVerifier = null;
        CertificatePinner certificatePinner = null;
        if (url.isHttps()) {
            sslSocketFactory = this.client.sslSocketFactory();
            hostnameVerifier = this.client.hostnameVerifier();
            certificatePinner = this.client.certificatePinner();
        }
        return new Address(url.host(), url.port(), this.client.dns(), this.client.socketFactory(), sslSocketFactory, hostnameVerifier, certificatePinner, this.client.proxyAuthenticator(), this.client.proxy(), this.client.protocols(), this.client.connectionSpecs(), this.client.proxySelector());
    }

    private boolean recover(IOException e, StreamAllocation streamAllocation2, boolean requestSendStarted, Request userRequest) {
        streamAllocation2.streamFailed(e);
        if (!this.client.retryOnConnectionFailure()) {
            return false;
        }
        if ((!requestSendStarted || !(userRequest.body() instanceof UnrepeatableRequestBody)) && isRecoverable(e, requestSendStarted) && streamAllocation2.hasMoreRoutes()) {
            return true;
        }
        return false;
    }

    private boolean isRecoverable(IOException e, boolean requestSendStarted) {
        if (e instanceof ProtocolException) {
            return false;
        }
        if (e instanceof InterruptedIOException) {
            if (!(e instanceof SocketTimeoutException) || requestSendStarted) {
                return false;
            }
            return true;
        } else if ((!(e instanceof SSLHandshakeException) || !(e.getCause() instanceof CertificateException)) && !(e instanceof SSLPeerUnverifiedException)) {
            return true;
        } else {
            return false;
        }
    }

    private Request followUpRequest(Response userResponse, Route route) throws IOException {
        String location;
        HttpUrl url;
        Proxy selectedProxy;
        if (userResponse == null) {
            throw new IllegalStateException();
        }
        int responseCode = userResponse.code();
        String method = userResponse.request().method();
        RequestBody requestBody = null;
        switch (responseCode) {
            case 300:
            case 301:
            case 302:
            case 303:
                break;
            case StatusLine.HTTP_TEMP_REDIRECT /*307*/:
            case StatusLine.HTTP_PERM_REDIRECT /*308*/:
                if (!method.equals("GET") && !method.equals("HEAD")) {
                    return null;
                }
            case 401:
                return this.client.authenticator().authenticate(route, userResponse);
            case 407:
                if (route != null) {
                    selectedProxy = route.proxy();
                } else {
                    selectedProxy = this.client.proxy();
                }
                if (selectedProxy.type() == Proxy.Type.HTTP) {
                    return this.client.proxyAuthenticator().authenticate(route, userResponse);
                }
                throw new ProtocolException("Received HTTP_PROXY_AUTH (407) code while not using proxy");
            case 408:
                if (!this.client.retryOnConnectionFailure() || (userResponse.request().body() instanceof UnrepeatableRequestBody)) {
                    return null;
                }
                if ((userResponse.priorResponse() == null || userResponse.priorResponse().code() != 408) && retryAfter(userResponse, 0) <= 0) {
                    return userResponse.request();
                }
                return null;
            case 503:
                if ((userResponse.priorResponse() == null || userResponse.priorResponse().code() != 503) && retryAfter(userResponse, Integer.MAX_VALUE) == 0) {
                    return userResponse.request();
                }
                return null;
            default:
                return null;
        }
        if (!this.client.followRedirects() || (location = userResponse.header("Location")) == null || (url = userResponse.request().url().resolve(location)) == null) {
            return null;
        }
        if (!url.scheme().equals(userResponse.request().url().scheme()) && !this.client.followSslRedirects()) {
            return null;
        }
        Request.Builder requestBuilder = userResponse.request().newBuilder();
        if (HttpMethod.permitsRequestBody(method)) {
            boolean maintainBody = HttpMethod.redirectsWithBody(method);
            if (HttpMethod.redirectsToGet(method)) {
                requestBuilder.method("GET", (RequestBody) null);
            } else {
                if (maintainBody) {
                    requestBody = userResponse.request().body();
                }
                requestBuilder.method(method, requestBody);
            }
            if (!maintainBody) {
                requestBuilder.removeHeader("Transfer-Encoding");
                requestBuilder.removeHeader("Content-Length");
                requestBuilder.removeHeader("Content-Type");
            }
        }
        if (!sameConnection(userResponse, url)) {
            requestBuilder.removeHeader("Authorization");
        }
        return requestBuilder.url(url).build();
    }

    private int retryAfter(Response userResponse, int defaultDelay) {
        String header = userResponse.header("Retry-After");
        if (header == null) {
            return defaultDelay;
        }
        if (header.matches("\\d+")) {
            return Integer.valueOf(header).intValue();
        }
        return Integer.MAX_VALUE;
    }

    private boolean sameConnection(Response response, HttpUrl followUp) {
        HttpUrl url = response.request().url();
        return url.host().equals(followUp.host()) && url.port() == followUp.port() && url.scheme().equals(followUp.scheme());
    }
}
