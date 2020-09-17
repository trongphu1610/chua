package androidx.navigation.fragment;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import androidx.navigation.NavGraphBuilder;
import androidx.navigation.Navigator;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000&\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a#\u0010\u0000\u001a\u00020\u0001\"\n\b\u0000\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\b\u001a<\u0010\u0000\u001a\u00020\u0001\"\n\b\u0000\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\b\b\u0001\u0010\u0005\u001a\u00020\u00062\u0017\u0010\u0007\u001a\u0013\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\b¢\u0006\u0002\b\nH\b¨\u0006\u000b"}, d2 = {"fragment", "", "F", "Landroid/support/v4/app/Fragment;", "Landroidx/navigation/NavGraphBuilder;", "id", "", "block", "Lkotlin/Function1;", "Landroidx/navigation/fragment/FragmentNavigatorDestinationBuilder;", "Lkotlin/ExtensionFunctionType;", "navigation-fragment-ktx_release"}, k = 2, mv = {1, 1, 10})
/* compiled from: FragmentNavigatorDestinationBuilder.kt */
public final class FragmentNavigatorDestinationBuilderKt {
    private static final <F extends Fragment> void fragment(@NotNull NavGraphBuilder $receiver, @IdRes int id) {
        NavGraphBuilder $receiver$iv = $receiver;
        Navigator navigator = $receiver$iv.getProvider().getNavigator(FragmentNavigator.class);
        Intrinsics.checkExpressionValueIsNotNull(navigator, "getNavigator(clazz.java)");
        Intrinsics.reifiedOperationMarker(4, "F");
        FragmentNavigatorDestinationBuilder fragmentNavigatorDestinationBuilder = new FragmentNavigatorDestinationBuilder((FragmentNavigator) navigator, id, Reflection.getOrCreateKotlinClass(Fragment.class));
        FragmentNavigatorDestinationBuilder fragmentNavigatorDestinationBuilder2 = fragmentNavigatorDestinationBuilder;
        $receiver$iv.destination(fragmentNavigatorDestinationBuilder);
    }

    private static final <F extends Fragment> void fragment(@NotNull NavGraphBuilder $receiver, @IdRes int id, Function1<? super FragmentNavigatorDestinationBuilder, Unit> block) {
        Navigator navigator = $receiver.getProvider().getNavigator(FragmentNavigator.class);
        Intrinsics.checkExpressionValueIsNotNull(navigator, "getNavigator(clazz.java)");
        Intrinsics.reifiedOperationMarker(4, "F");
        FragmentNavigatorDestinationBuilder fragmentNavigatorDestinationBuilder = new FragmentNavigatorDestinationBuilder((FragmentNavigator) navigator, id, Reflection.getOrCreateKotlinClass(Fragment.class));
        block.invoke(fragmentNavigatorDestinationBuilder);
        $receiver.destination(fragmentNavigatorDestinationBuilder);
    }
}
