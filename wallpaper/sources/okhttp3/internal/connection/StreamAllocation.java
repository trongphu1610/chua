package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.ConnectionPool;
import okhttp3.EventListener;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RouteSelector;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http2.ConnectionShutdownException;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.StreamResetException;

public final class StreamAllocation {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public final Address address;
    public final Call call;
    private final Object callStackTrace;
    private boolean canceled;
    private HttpCodec codec;
    private RealConnection connection;
    private final ConnectionPool connectionPool;
    public final EventListener eventListener;
    private int refusedStreamCount;
    private boolean released;
    private boolean reportedAcquired;
    private Route route;
    private RouteSelector.Selection routeSelection;
    private final RouteSelector routeSelector;

    public StreamAllocation(ConnectionPool connectionPool2, Address address2, Call call2, EventListener eventListener2, Object callStackTrace2) {
        this.connectionPool = connectionPool2;
        this.address = address2;
        this.call = call2;
        this.eventListener = eventListener2;
        this.routeSelector = new RouteSelector(address2, routeDatabase(), call2, eventListener2);
        this.callStackTrace = callStackTrace2;
    }

    public HttpCodec newStream(OkHttpClient client, Interceptor.Chain chain, boolean doExtensiveHealthChecks) {
        try {
            HttpCodec resultCodec = findHealthyConnection(chain.connectTimeoutMillis(), chain.readTimeoutMillis(), chain.writeTimeoutMillis(), client.pingIntervalMillis(), client.retryOnConnectionFailure(), doExtensiveHealthChecks).newCodec(client, chain, this);
            synchronized (this.connectionPool) {
                this.codec = resultCodec;
            }
            return resultCodec;
        } catch (IOException e) {
            throw new RouteException(e);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0018, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
        if (r0.isHealthy(r9) != false) goto L_0x0018;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private okhttp3.internal.connection.RealConnection findHealthyConnection(int r4, int r5, int r6, int r7, boolean r8, boolean r9) throws java.io.IOException {
        /*
            r3 = this;
        L_0x0000:
            okhttp3.internal.connection.RealConnection r0 = r3.findConnection(r4, r5, r6, r7, r8)
            okhttp3.ConnectionPool r1 = r3.connectionPool
            monitor-enter(r1)
            int r2 = r0.successCount     // Catch:{ all -> 0x0019 }
            if (r2 != 0) goto L_0x000d
            monitor-exit(r1)     // Catch:{ all -> 0x0019 }
            return r0
        L_0x000d:
            monitor-exit(r1)     // Catch:{ all -> 0x0019 }
            boolean r1 = r0.isHealthy(r9)
            if (r1 != 0) goto L_0x0018
            r3.noNewStreams()
            goto L_0x0000
        L_0x0018:
            return r0
        L_0x0019:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0019 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.StreamAllocation.findHealthyConnection(int, int, int, int, boolean, boolean):okhttp3.internal.connection.RealConnection");
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x013f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0146, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x014d, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private okhttp3.internal.connection.RealConnection findConnection(int r19, int r20, int r21, int r22, boolean r23) throws java.io.IOException {
        /*
            r18 = this;
            r1 = r18
            r2 = 0
            r3 = 0
            r4 = 0
            okhttp3.ConnectionPool r5 = r1.connectionPool
            monitor-enter(r5)
            boolean r6 = r1.released     // Catch:{ all -> 0x0148 }
            if (r6 == 0) goto L_0x0014
            java.lang.IllegalStateException r6 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0148 }
            java.lang.String r7 = "released"
            r6.<init>(r7)     // Catch:{ all -> 0x0148 }
            throw r6     // Catch:{ all -> 0x0148 }
        L_0x0014:
            okhttp3.internal.http.HttpCodec r6 = r1.codec     // Catch:{ all -> 0x0148 }
            if (r6 == 0) goto L_0x0020
            java.lang.IllegalStateException r6 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0148 }
            java.lang.String r7 = "codec != null"
            r6.<init>(r7)     // Catch:{ all -> 0x0148 }
            throw r6     // Catch:{ all -> 0x0148 }
        L_0x0020:
            boolean r6 = r1.canceled     // Catch:{ all -> 0x0148 }
            if (r6 == 0) goto L_0x002c
            java.io.IOException r6 = new java.io.IOException     // Catch:{ all -> 0x0148 }
            java.lang.String r7 = "Canceled"
            r6.<init>(r7)     // Catch:{ all -> 0x0148 }
            throw r6     // Catch:{ all -> 0x0148 }
        L_0x002c:
            okhttp3.internal.connection.RealConnection r6 = r1.connection     // Catch:{ all -> 0x0148 }
            java.net.Socket r7 = r18.releaseIfNoNewStreams()     // Catch:{ all -> 0x0148 }
            okhttp3.internal.connection.RealConnection r8 = r1.connection     // Catch:{ all -> 0x0148 }
            if (r8 == 0) goto L_0x003a
            okhttp3.internal.connection.RealConnection r8 = r1.connection     // Catch:{ all -> 0x0148 }
            r3 = r8
            r6 = 0
        L_0x003a:
            boolean r8 = r1.reportedAcquired     // Catch:{ all -> 0x0148 }
            if (r8 != 0) goto L_0x003f
            r6 = 0
        L_0x003f:
            if (r3 != 0) goto L_0x0057
            okhttp3.internal.Internal r8 = okhttp3.internal.Internal.instance     // Catch:{ all -> 0x0148 }
            okhttp3.ConnectionPool r9 = r1.connectionPool     // Catch:{ all -> 0x0148 }
            okhttp3.Address r10 = r1.address     // Catch:{ all -> 0x0148 }
            r11 = 0
            r8.get(r9, r10, r1, r11)     // Catch:{ all -> 0x0148 }
            okhttp3.internal.connection.RealConnection r8 = r1.connection     // Catch:{ all -> 0x0148 }
            if (r8 == 0) goto L_0x0054
            r2 = 1
            okhttp3.internal.connection.RealConnection r8 = r1.connection     // Catch:{ all -> 0x0148 }
            r3 = r8
            goto L_0x0057
        L_0x0054:
            okhttp3.Route r8 = r1.route     // Catch:{ all -> 0x0148 }
            r4 = r8
        L_0x0057:
            monitor-exit(r5)     // Catch:{ all -> 0x0148 }
            okhttp3.internal.Util.closeQuietly((java.net.Socket) r7)
            if (r6 == 0) goto L_0x0064
            okhttp3.EventListener r5 = r1.eventListener
            okhttp3.Call r8 = r1.call
            r5.connectionReleased(r8, r6)
        L_0x0064:
            if (r2 == 0) goto L_0x006d
            okhttp3.EventListener r5 = r1.eventListener
            okhttp3.Call r8 = r1.call
            r5.connectionAcquired(r8, r3)
        L_0x006d:
            if (r3 == 0) goto L_0x0070
            return r3
        L_0x0070:
            r5 = 0
            if (r4 != 0) goto L_0x0088
            okhttp3.internal.connection.RouteSelector$Selection r8 = r1.routeSelection
            if (r8 == 0) goto L_0x007f
            okhttp3.internal.connection.RouteSelector$Selection r8 = r1.routeSelection
            boolean r8 = r8.hasNext()
            if (r8 != 0) goto L_0x0088
        L_0x007f:
            r5 = 1
            okhttp3.internal.connection.RouteSelector r8 = r1.routeSelector
            okhttp3.internal.connection.RouteSelector$Selection r8 = r8.next()
            r1.routeSelection = r8
        L_0x0088:
            r8 = r5
            okhttp3.ConnectionPool r9 = r1.connectionPool
            monitor-enter(r9)
            boolean r5 = r1.canceled     // Catch:{ all -> 0x0141 }
            if (r5 == 0) goto L_0x0098
            java.io.IOException r5 = new java.io.IOException     // Catch:{ all -> 0x0141 }
            java.lang.String r10 = "Canceled"
            r5.<init>(r10)     // Catch:{ all -> 0x0141 }
            throw r5     // Catch:{ all -> 0x0141 }
        L_0x0098:
            if (r8 == 0) goto L_0x00c4
            okhttp3.internal.connection.RouteSelector$Selection r5 = r1.routeSelection     // Catch:{ all -> 0x0141 }
            java.util.List r5 = r5.getAll()     // Catch:{ all -> 0x0141 }
            r10 = 0
            int r11 = r5.size()     // Catch:{ all -> 0x0141 }
        L_0x00a5:
            if (r10 >= r11) goto L_0x00c4
            java.lang.Object r12 = r5.get(r10)     // Catch:{ all -> 0x0141 }
            okhttp3.Route r12 = (okhttp3.Route) r12     // Catch:{ all -> 0x0141 }
            okhttp3.internal.Internal r13 = okhttp3.internal.Internal.instance     // Catch:{ all -> 0x0141 }
            okhttp3.ConnectionPool r14 = r1.connectionPool     // Catch:{ all -> 0x0141 }
            okhttp3.Address r15 = r1.address     // Catch:{ all -> 0x0141 }
            r13.get(r14, r15, r1, r12)     // Catch:{ all -> 0x0141 }
            okhttp3.internal.connection.RealConnection r13 = r1.connection     // Catch:{ all -> 0x0141 }
            if (r13 == 0) goto L_0x00c1
            r2 = 1
            okhttp3.internal.connection.RealConnection r13 = r1.connection     // Catch:{ all -> 0x0141 }
            r3 = r13
            r1.route = r12     // Catch:{ all -> 0x0141 }
            goto L_0x00c4
        L_0x00c1:
            int r10 = r10 + 1
            goto L_0x00a5
        L_0x00c4:
            if (r2 != 0) goto L_0x00df
            if (r4 != 0) goto L_0x00cf
            okhttp3.internal.connection.RouteSelector$Selection r5 = r1.routeSelection     // Catch:{ all -> 0x0141 }
            okhttp3.Route r5 = r5.next()     // Catch:{ all -> 0x0141 }
            r4 = r5
        L_0x00cf:
            r1.route = r4     // Catch:{ all -> 0x0141 }
            r5 = 0
            r1.refusedStreamCount = r5     // Catch:{ all -> 0x0141 }
            okhttp3.internal.connection.RealConnection r10 = new okhttp3.internal.connection.RealConnection     // Catch:{ all -> 0x0141 }
            okhttp3.ConnectionPool r11 = r1.connectionPool     // Catch:{ all -> 0x0141 }
            r10.<init>(r11, r4)     // Catch:{ all -> 0x0141 }
            r3 = r10
            r1.acquire(r3, r5)     // Catch:{ all -> 0x0141 }
        L_0x00df:
            monitor-exit(r9)     // Catch:{ all -> 0x0141 }
            if (r2 == 0) goto L_0x00ea
            okhttp3.EventListener r5 = r1.eventListener
            okhttp3.Call r9 = r1.call
            r5.connectionAcquired(r9, r3)
            return r3
        L_0x00ea:
            okhttp3.Call r5 = r1.call
            okhttp3.EventListener r9 = r1.eventListener
            r10 = r3
            r11 = r19
            r12 = r20
            r13 = r21
            r14 = r22
            r15 = r23
            r16 = r5
            r17 = r9
            r10.connect(r11, r12, r13, r14, r15, r16, r17)
            okhttp3.internal.connection.RouteDatabase r5 = r18.routeDatabase()
            okhttp3.Route r9 = r3.route()
            r5.connected(r9)
            r5 = 0
            okhttp3.ConnectionPool r10 = r1.connectionPool
            monitor-enter(r10)
            r9 = 1
            r1.reportedAcquired = r9     // Catch:{ all -> 0x0139 }
            okhttp3.internal.Internal r9 = okhttp3.internal.Internal.instance     // Catch:{ all -> 0x0139 }
            okhttp3.ConnectionPool r11 = r1.connectionPool     // Catch:{ all -> 0x0139 }
            r9.put(r11, r3)     // Catch:{ all -> 0x0139 }
            boolean r9 = r3.isMultiplexed()     // Catch:{ all -> 0x0139 }
            if (r9 == 0) goto L_0x012d
            okhttp3.internal.Internal r9 = okhttp3.internal.Internal.instance     // Catch:{ all -> 0x0139 }
            okhttp3.ConnectionPool r11 = r1.connectionPool     // Catch:{ all -> 0x0139 }
            okhttp3.Address r12 = r1.address     // Catch:{ all -> 0x0139 }
            java.net.Socket r9 = r9.deduplicate(r11, r12, r1)     // Catch:{ all -> 0x0139 }
            r5 = r9
            okhttp3.internal.connection.RealConnection r9 = r1.connection     // Catch:{ all -> 0x0139 }
            r3 = r9
        L_0x012d:
            monitor-exit(r10)     // Catch:{ all -> 0x0139 }
            okhttp3.internal.Util.closeQuietly((java.net.Socket) r5)
            okhttp3.EventListener r9 = r1.eventListener
            okhttp3.Call r10 = r1.call
            r9.connectionAcquired(r10, r3)
            return r3
        L_0x0139:
            r0 = move-exception
            r9 = r5
            r5 = r3
        L_0x013c:
            r3 = r0
            monitor-exit(r10)     // Catch:{ all -> 0x013f }
            throw r3
        L_0x013f:
            r0 = move-exception
            goto L_0x013c
        L_0x0141:
            r0 = move-exception
            r5 = r2
        L_0x0143:
            r2 = r0
            monitor-exit(r9)     // Catch:{ all -> 0x0146 }
            throw r2
        L_0x0146:
            r0 = move-exception
            goto L_0x0143
        L_0x0148:
            r0 = move-exception
            r6 = r2
        L_0x014a:
            r2 = r0
            monitor-exit(r5)     // Catch:{ all -> 0x014d }
            throw r2
        L_0x014d:
            r0 = move-exception
            goto L_0x014a
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.StreamAllocation.findConnection(int, int, int, int, boolean):okhttp3.internal.connection.RealConnection");
    }

    private Socket releaseIfNoNewStreams() {
        RealConnection allocatedConnection = this.connection;
        if (allocatedConnection == null || !allocatedConnection.noNewStreams) {
            return null;
        }
        return deallocate(false, false, true);
    }

    public void streamFinished(boolean noNewStreams, HttpCodec codec2, long bytesRead, IOException e) {
        Connection releasedConnection;
        Socket socket;
        boolean callEnd;
        this.eventListener.responseBodyEnd(this.call, bytesRead);
        synchronized (this.connectionPool) {
            if (codec2 != null) {
                if (codec2 == this.codec) {
                    if (!noNewStreams) {
                        this.connection.successCount++;
                    }
                    releasedConnection = this.connection;
                    socket = deallocate(noNewStreams, false, true);
                    if (this.connection != null) {
                        releasedConnection = null;
                    }
                    callEnd = this.released;
                }
            }
            throw new IllegalStateException("expected " + this.codec + " but was " + codec2);
        }
        Connection releasedConnection2 = releasedConnection;
        Util.closeQuietly(socket);
        if (releasedConnection2 != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection2);
        }
        if (e != null) {
            this.eventListener.callFailed(this.call, e);
        } else if (callEnd) {
            this.eventListener.callEnd(this.call);
        }
    }

    public HttpCodec codec() {
        HttpCodec httpCodec;
        synchronized (this.connectionPool) {
            httpCodec = this.codec;
        }
        return httpCodec;
    }

    private RouteDatabase routeDatabase() {
        return Internal.instance.routeDatabase(this.connectionPool);
    }

    public Route route() {
        return this.route;
    }

    public synchronized RealConnection connection() {
        return this.connection;
    }

    public void release() {
        Connection releasedConnection;
        Socket socket;
        synchronized (this.connectionPool) {
            releasedConnection = this.connection;
            socket = deallocate(false, true, false);
            if (this.connection != null) {
                releasedConnection = null;
            }
        }
        Connection releasedConnection2 = releasedConnection;
        Util.closeQuietly(socket);
        if (releasedConnection2 != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection2);
        }
    }

    public void noNewStreams() {
        Connection releasedConnection;
        Socket socket;
        synchronized (this.connectionPool) {
            releasedConnection = this.connection;
            socket = deallocate(true, false, false);
            if (this.connection != null) {
                releasedConnection = null;
            }
        }
        Connection releasedConnection2 = releasedConnection;
        Util.closeQuietly(socket);
        if (releasedConnection2 != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection2);
        }
    }

    private Socket deallocate(boolean noNewStreams, boolean released2, boolean streamFinished) {
        if (streamFinished) {
            this.codec = null;
        }
        if (released2) {
            this.released = true;
        }
        Socket socket = null;
        if (this.connection != null) {
            if (noNewStreams) {
                this.connection.noNewStreams = true;
            }
            if (this.codec == null && (this.released || this.connection.noNewStreams)) {
                release(this.connection);
                if (this.connection.allocations.isEmpty()) {
                    this.connection.idleAtNanos = System.nanoTime();
                    if (Internal.instance.connectionBecameIdle(this.connectionPool, this.connection)) {
                        socket = this.connection.socket();
                    }
                }
                this.connection = null;
            }
        }
        return socket;
    }

    public void cancel() {
        HttpCodec codecToCancel;
        RealConnection connectionToCancel;
        synchronized (this.connectionPool) {
            this.canceled = true;
            codecToCancel = this.codec;
            connectionToCancel = this.connection;
        }
        if (codecToCancel != null) {
            codecToCancel.cancel();
        } else if (connectionToCancel != null) {
            connectionToCancel.cancel();
        }
    }

    public void streamFailed(IOException e) {
        Connection releasedConnection;
        Socket socket;
        boolean noNewStreams = false;
        synchronized (this.connectionPool) {
            if (e instanceof StreamResetException) {
                StreamResetException streamResetException = (StreamResetException) e;
                if (streamResetException.errorCode == ErrorCode.REFUSED_STREAM) {
                    this.refusedStreamCount++;
                }
                if (streamResetException.errorCode != ErrorCode.REFUSED_STREAM || this.refusedStreamCount > 1) {
                    noNewStreams = true;
                    this.route = null;
                }
            } else if (this.connection != null && (!this.connection.isMultiplexed() || (e instanceof ConnectionShutdownException))) {
                noNewStreams = true;
                if (this.connection.successCount == 0) {
                    if (!(this.route == null || e == null)) {
                        this.routeSelector.connectFailed(this.route, e);
                    }
                    this.route = null;
                }
            }
            releasedConnection = this.connection;
            socket = deallocate(noNewStreams, false, true);
            if (this.connection != null || !this.reportedAcquired) {
                releasedConnection = null;
            }
        }
        Connection releasedConnection2 = releasedConnection;
        Util.closeQuietly(socket);
        if (releasedConnection2 != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection2);
        }
    }

    public void acquire(RealConnection connection2, boolean reportedAcquired2) {
        if (this.connection != null) {
            throw new IllegalStateException();
        }
        this.connection = connection2;
        this.reportedAcquired = reportedAcquired2;
        connection2.allocations.add(new StreamAllocationReference(this, this.callStackTrace));
    }

    private void release(RealConnection connection2) {
        int size = connection2.allocations.size();
        for (int i = 0; i < size; i++) {
            if (connection2.allocations.get(i).get() == this) {
                connection2.allocations.remove(i);
                return;
            }
        }
        throw new IllegalStateException();
    }

    public Socket releaseAndAcquire(RealConnection newConnection) {
        if (this.codec == null && this.connection.allocations.size() == 1) {
            Socket socket = deallocate(true, false, false);
            this.connection = newConnection;
            newConnection.allocations.add(this.connection.allocations.get(0));
            return socket;
        }
        throw new IllegalStateException();
    }

    public boolean hasMoreRoutes() {
        return this.route != null || (this.routeSelection != null && this.routeSelection.hasNext()) || this.routeSelector.hasNext();
    }

    public String toString() {
        RealConnection connection2 = connection();
        return connection2 != null ? connection2.toString() : this.address.toString();
    }

    public static final class StreamAllocationReference extends WeakReference<StreamAllocation> {
        public final Object callStackTrace;

        StreamAllocationReference(StreamAllocation referent, Object callStackTrace2) {
            super(referent);
            this.callStackTrace = callStackTrace2;
        }
    }
}
