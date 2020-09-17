package androidx.navigation.fragment;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import androidx.navigation.NavDestination;
import androidx.navigation.NavDestinationBuilder;
import androidx.navigation.NavDestinationDsl;
import androidx.navigation.fragment.FragmentNavigator;
import kotlin.Metadata;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\b\b\u0001\u0010\u0005\u001a\u00020\u0006\u0012\u000e\u0010\u0007\u001a\n\u0012\u0006\b\u0001\u0012\u00020\t0\b¢\u0006\u0002\u0010\nJ\b\u0010\u000b\u001a\u00020\u0002H\u0016R\u0016\u0010\u0007\u001a\n\u0012\u0006\b\u0001\u0012\u00020\t0\bX\u0004¢\u0006\u0002\n\u0000¨\u0006\f"}, d2 = {"Landroidx/navigation/fragment/FragmentNavigatorDestinationBuilder;", "Landroidx/navigation/NavDestinationBuilder;", "Landroidx/navigation/fragment/FragmentNavigator$Destination;", "navigator", "Landroidx/navigation/fragment/FragmentNavigator;", "id", "", "fragmentClass", "Lkotlin/reflect/KClass;", "Landroid/support/v4/app/Fragment;", "(Landroidx/navigation/fragment/FragmentNavigator;ILkotlin/reflect/KClass;)V", "build", "navigation-fragment-ktx_release"}, k = 1, mv = {1, 1, 10})
@NavDestinationDsl
/* compiled from: FragmentNavigatorDestinationBuilder.kt */
public final class FragmentNavigatorDestinationBuilder extends NavDestinationBuilder<FragmentNavigator.Destination> {
    private final KClass<? extends Fragment> fragmentClass;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public FragmentNavigatorDestinationBuilder(@NotNull FragmentNavigator navigator, @IdRes int id, @NotNull KClass<? extends Fragment> fragmentClass2) {
        super(navigator, id);
        Intrinsics.checkParameterIsNotNull(navigator, "navigator");
        Intrinsics.checkParameterIsNotNull(fragmentClass2, "fragmentClass");
        this.fragmentClass = fragmentClass2;
    }

    @NotNull
    public FragmentNavigator.Destination build() {
        NavDestination build = super.build();
        ((FragmentNavigator.Destination) build).setFragmentClass(JvmClassMappingKt.getJavaClass(this.fragmentClass));
        return (FragmentNavigator.Destination) build;
    }
}
