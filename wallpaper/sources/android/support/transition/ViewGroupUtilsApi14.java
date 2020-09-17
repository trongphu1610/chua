package android.support.transition;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ViewGroupUtilsApi14 {
    private static final int LAYOUT_TRANSITION_CHANGING = 4;
    private static final String TAG = "ViewGroupUtilsApi14";
    private static Method sCancelMethod;
    private static boolean sCancelMethodFetched;
    private static LayoutTransition sEmptyLayoutTransition;
    private static Field sLayoutSuppressedField;
    private static boolean sLayoutSuppressedFieldFetched;

    static void suppressLayout(@NonNull ViewGroup group, boolean suppress) {
        if (sEmptyLayoutTransition == null) {
            sEmptyLayoutTransition = new LayoutTransition() {
                public boolean isChangingLayout() {
                    return true;
                }
            };
            sEmptyLayoutTransition.setAnimator(2, (Animator) null);
            sEmptyLayoutTransition.setAnimator(0, (Animator) null);
            sEmptyLayoutTransition.setAnimator(1, (Animator) null);
            sEmptyLayoutTransition.setAnimator(3, (Animator) null);
            sEmptyLayoutTransition.setAnimator(4, (Animator) null);
        }
        if (suppress) {
            LayoutTransition layoutTransition = group.getLayoutTransition();
            if (layoutTransition != null) {
                if (layoutTransition.isRunning()) {
                    cancelLayoutTransition(layoutTransition);
                }
                if (layoutTransition != sEmptyLayoutTransition) {
                    group.setTag(R.id.transition_layout_save, layoutTransition);
                }
            }
            group.setLayoutTransition(sEmptyLayoutTransition);
            return;
        }
        group.setLayoutTransition((LayoutTransition) null);
        if (!sLayoutSuppressedFieldFetched) {
            try {
                sLayoutSuppressedField = ViewGroup.class.getDeclaredField("mLayoutSuppressed");
                sLayoutSuppressedField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                Log.i(TAG, "Failed to access mLayoutSuppressed field by reflection");
            }
            sLayoutSuppressedFieldFetched = true;
        }
        boolean layoutSuppressed = false;
        if (sLayoutSuppressedField != null) {
            try {
                layoutSuppressed = sLayoutSuppressedField.getBoolean(group);
                if (layoutSuppressed) {
                    sLayoutSuppressedField.setBoolean(group, false);
                }
            } catch (IllegalAccessException e2) {
                Log.i(TAG, "Failed to get mLayoutSuppressed field by reflection");
            }
        }
        if (layoutSuppressed) {
            group.requestLayout();
        }
        LayoutTransition layoutTransition2 = (LayoutTransition) group.getTag(R.id.transition_layout_save);
        if (layoutTransition2 != null) {
            group.setTag(R.id.transition_layout_save, (Object) null);
            group.setLayoutTransition(layoutTransition2);
        }
    }

    private static void cancelLayoutTransition(LayoutTransition t) {
        if (!sCancelMethodFetched) {
            try {
                sCancelMethod = LayoutTransition.class.getDeclaredMethod("cancel", new Class[0]);
                sCancelMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Log.i(TAG, "Failed to access cancel method by reflection");
            }
            sCancelMethodFetched = true;
        }
        if (sCancelMethod != null) {
            try {
                sCancelMethod.invoke(t, new Object[0]);
            } catch (IllegalAccessException e2) {
                Log.i(TAG, "Failed to access cancel method by reflection");
            } catch (InvocationTargetException e3) {
                Log.i(TAG, "Failed to invoke cancel method by reflection");
            }
        }
    }

    private ViewGroupUtilsApi14() {
    }
}