package com.squareup.picasso;

import android.net.NetworkInfo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestHandler;
import java.io.IOException;
import okhttp3.CacheControl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Source;

class NetworkRequestHandler extends RequestHandler {
    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";
    private final Downloader downloader;
    private final Stats stats;

    NetworkRequestHandler(Downloader downloader2, Stats stats2) {
        this.downloader = downloader2;
        this.stats = stats2;
    }

    public boolean canHandleRequest(Request data) {
        String scheme = data.uri.getScheme();
        return SCHEME_HTTP.equals(scheme) || SCHEME_HTTPS.equals(scheme);
    }

    public RequestHandler.Result load(Request request, int networkPolicy) throws IOException {
        Response response = this.downloader.load(createRequest(request, networkPolicy));
        ResponseBody body = response.body();
        if (!response.isSuccessful()) {
            body.close();
            throw new ResponseException(response.code(), request.networkPolicy);
        }
        Picasso.LoadedFrom loadedFrom = response.cacheResponse() == null ? Picasso.LoadedFrom.NETWORK : Picasso.LoadedFrom.DISK;
        if (loadedFrom == Picasso.LoadedFrom.DISK && body.contentLength() == 0) {
            body.close();
            throw new ContentLengthException("Received response with 0 content-length header.");
        }
        if (loadedFrom == Picasso.LoadedFrom.NETWORK && body.contentLength() > 0) {
            this.stats.dispatchDownloadFinished(body.contentLength());
        }
        return new RequestHandler.Result((Source) body.source(), loadedFrom);
    }

    /* access modifiers changed from: package-private */
    public int getRetryCount() {
        return 2;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        return info == null || info.isConnected();
    }

    /* access modifiers changed from: package-private */
    public boolean supportsReplay() {
        return true;
    }

    private static Request createRequest(Request request, int networkPolicy) {
        CacheControl cacheControl = null;
        if (networkPolicy != 0) {
            if (NetworkPolicy.isOfflineOnly(networkPolicy)) {
                cacheControl = CacheControl.FORCE_CACHE;
            } else {
                CacheControl.Builder builder = new CacheControl.Builder();
                if (!NetworkPolicy.shouldReadFromDiskCache(networkPolicy)) {
                    builder.noCache();
                }
                if (!NetworkPolicy.shouldWriteToDiskCache(networkPolicy)) {
                    builder.noStore();
                }
                cacheControl = builder.build();
            }
        }
        Request.Builder builder2 = new Request.Builder().url(request.uri.toString());
        if (cacheControl != null) {
            builder2.cacheControl(cacheControl);
        }
        return builder2.build();
    }

    static class ContentLengthException extends IOException {
        ContentLengthException(String message) {
            super(message);
        }
    }

    static final class ResponseException extends IOException {
        final int code;
        final int networkPolicy;

        ResponseException(int code2, int networkPolicy2) {
            super("HTTP " + code2);
            this.code = code2;
            this.networkPolicy = networkPolicy2;
        }
    }
}
