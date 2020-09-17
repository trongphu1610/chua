package com.dvt.monthlycalender.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\u0018\u0000 \u00032\u00020\u0001:\u0001\u0003B\u0005¢\u0006\u0002\u0010\u0002¨\u0006\u0004"}, d2 = {"Lcom/dvt/monthlycalender/util/Network;", "", "()V", "Companion", "app_debug"}, k = 1, mv = {1, 1, 11})
/* compiled from: Network.kt */
public final class Network {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);

    @Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006¨\u0006\u0007"}, d2 = {"Lcom/dvt/monthlycalender/util/Network$Companion;", "", "()V", "hasBeenConnected", "", "context", "Landroid/content/Context;", "app_debug"}, k = 1, mv = {1, 1, 11})
    /* compiled from: Network.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        public final boolean hasBeenConnected(@NotNull Context context) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Object systemService = context.getSystemService("connectivity");
            if (systemService == null) {
                throw new TypeCastException("null cannot be cast to non-null type android.net.ConnectivityManager");
            }
            NetworkInfo activeNetwork = ((ConnectivityManager) systemService).getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
    }
}
