package androidx.navigation;

import android.support.annotation.IdRes;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000 \n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\u001a\u0017\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\b\b\u0001\u0010\u0003\u001a\u00020\u0004H\u0002\u001a\u0017\u0010\u0005\u001a\u00020\u0006*\u00020\u00022\b\b\u0001\u0010\u0003\u001a\u00020\u0004H\n\u001a\u0015\u0010\u0007\u001a\u00020\b*\u00020\u00022\u0006\u0010\t\u001a\u00020\u0006H\n\u001a\u0015\u0010\n\u001a\u00020\b*\u00020\u00022\u0006\u0010\t\u001a\u00020\u0006H\n\u001a\u0015\u0010\n\u001a\u00020\b*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u0002H\n¨\u0006\f"}, d2 = {"contains", "", "Landroidx/navigation/NavGraph;", "id", "", "get", "Landroidx/navigation/NavDestination;", "minusAssign", "", "node", "plusAssign", "other", "navigation-common-ktx_release"}, k = 2, mv = {1, 1, 10})
/* compiled from: NavGraph.kt */
public final class NavGraphKt {
    @NotNull
    public static final NavDestination get(@NotNull NavGraph $receiver, @IdRes int id) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        NavDestination findNode = $receiver.findNode(id);
        if (findNode != null) {
            return findNode;
        }
        throw new IllegalArgumentException("No destination for " + id + " was found in " + $receiver);
    }

    public static final boolean contains(@NotNull NavGraph $receiver, @IdRes int id) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return $receiver.findNode(id) != null;
    }

    public static final void plusAssign(@NotNull NavGraph $receiver, @NotNull NavDestination node) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(node, "node");
        $receiver.addDestination(node);
    }

    public static final void plusAssign(@NotNull NavGraph $receiver, @NotNull NavGraph other) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        $receiver.addAll(other);
    }

    public static final void minusAssign(@NotNull NavGraph $receiver, @NotNull NavDestination node) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(node, "node");
        $receiver.remove(node);
    }
}
