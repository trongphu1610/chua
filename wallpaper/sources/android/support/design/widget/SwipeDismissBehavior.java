package android.support.design.widget;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class SwipeDismissBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    private static final float DEFAULT_ALPHA_END_DISTANCE = 0.5f;
    private static final float DEFAULT_ALPHA_START_DISTANCE = 0.0f;
    private static final float DEFAULT_DRAG_DISMISS_THRESHOLD = 0.5f;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_SETTLING = 2;
    public static final int SWIPE_DIRECTION_ANY = 2;
    public static final int SWIPE_DIRECTION_END_TO_START = 1;
    public static final int SWIPE_DIRECTION_START_TO_END = 0;
    float alphaEndSwipeDistance = 0.5f;
    float alphaStartSwipeDistance = 0.0f;
    private final ViewDragHelper.Callback dragCallback = new ViewDragHelper.Callback() {
        private static final int INVALID_POINTER_ID = -1;
        private int activePointerId = -1;
        private int originalCapturedViewLeft;

        public boolean tryCaptureView(View child, int pointerId) {
            return this.activePointerId == -1 && SwipeDismissBehavior.this.canSwipeDismissView(child);
        }

        public void onViewCaptured(View capturedChild, int activePointerId2) {
            this.activePointerId = activePointerId2;
            this.originalCapturedViewLeft = capturedChild.getLeft();
            ViewParent parent = capturedChild.getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }

        public void onViewDragStateChanged(int state) {
            if (SwipeDismissBehavior.this.listener != null) {
                SwipeDismissBehavior.this.listener.onDragStateChanged(state);
            }
        }

        public void onViewReleased(View child, float xvel, float yvel) {
            int targetLeft;
            this.activePointerId = -1;
            int childWidth = child.getWidth();
            boolean dismiss = false;
            if (shouldDismiss(child, xvel)) {
                targetLeft = child.getLeft() < this.originalCapturedViewLeft ? this.originalCapturedViewLeft - childWidth : this.originalCapturedViewLeft + childWidth;
                dismiss = true;
            } else {
                targetLeft = this.originalCapturedViewLeft;
            }
            if (SwipeDismissBehavior.this.viewDragHelper.settleCapturedViewAt(targetLeft, child.getTop())) {
                ViewCompat.postOnAnimation(child, new SettleRunnable(child, dismiss));
            } else if (dismiss && SwipeDismissBehavior.this.listener != null) {
                SwipeDismissBehavior.this.listener.onDismiss(child);
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:14:0x0025 A[ORIG_RETURN, RETURN, SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x003a A[ORIG_RETURN, RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean shouldDismiss(android.view.View r7, float r8) {
            /*
                r6 = this;
                r0 = 0
                int r1 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
                r2 = 0
                r3 = 1
                if (r1 == 0) goto L_0x0045
                int r1 = android.support.v4.view.ViewCompat.getLayoutDirection(r7)
                if (r1 != r3) goto L_0x0010
                r1 = 1
                goto L_0x0011
            L_0x0010:
                r1 = 0
            L_0x0011:
                android.support.design.widget.SwipeDismissBehavior r4 = android.support.design.widget.SwipeDismissBehavior.this
                int r4 = r4.swipeDirection
                r5 = 2
                if (r4 != r5) goto L_0x0019
                return r3
            L_0x0019:
                android.support.design.widget.SwipeDismissBehavior r4 = android.support.design.widget.SwipeDismissBehavior.this
                int r4 = r4.swipeDirection
                if (r4 != 0) goto L_0x002e
                if (r1 == 0) goto L_0x0028
                int r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
                if (r0 >= 0) goto L_0x0027
            L_0x0025:
                r2 = 1
                goto L_0x002d
            L_0x0027:
                goto L_0x002d
            L_0x0028:
                int r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
                if (r0 <= 0) goto L_0x0027
                goto L_0x0025
            L_0x002d:
                return r2
            L_0x002e:
                android.support.design.widget.SwipeDismissBehavior r4 = android.support.design.widget.SwipeDismissBehavior.this
                int r4 = r4.swipeDirection
                if (r4 != r3) goto L_0x0043
                if (r1 == 0) goto L_0x003d
                int r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
                if (r0 <= 0) goto L_0x003c
            L_0x003a:
                r2 = 1
                goto L_0x0042
            L_0x003c:
                goto L_0x0042
            L_0x003d:
                int r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
                if (r0 >= 0) goto L_0x003c
                goto L_0x003a
            L_0x0042:
                return r2
            L_0x0043:
                return r2
            L_0x0045:
                int r0 = r7.getLeft()
                int r1 = r6.originalCapturedViewLeft
                int r0 = r0 - r1
                int r1 = r7.getWidth()
                float r1 = (float) r1
                android.support.design.widget.SwipeDismissBehavior r4 = android.support.design.widget.SwipeDismissBehavior.this
                float r4 = r4.dragDismissThreshold
                float r1 = r1 * r4
                int r1 = java.lang.Math.round(r1)
                int r4 = java.lang.Math.abs(r0)
                if (r4 < r1) goto L_0x0063
                r2 = 1
            L_0x0063:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.design.widget.SwipeDismissBehavior.AnonymousClass1.shouldDismiss(android.view.View, float):boolean");
        }

        public int getViewHorizontalDragRange(View child) {
            return child.getWidth();
        }

        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int max;
            int min;
            boolean isRtl = ViewCompat.getLayoutDirection(child) == 1;
            if (SwipeDismissBehavior.this.swipeDirection == 0) {
                if (isRtl) {
                    min = this.originalCapturedViewLeft - child.getWidth();
                    max = this.originalCapturedViewLeft;
                } else {
                    min = this.originalCapturedViewLeft;
                    max = this.originalCapturedViewLeft + child.getWidth();
                }
            } else if (SwipeDismissBehavior.this.swipeDirection != 1) {
                min = this.originalCapturedViewLeft - child.getWidth();
                max = this.originalCapturedViewLeft + child.getWidth();
            } else if (isRtl) {
                min = this.originalCapturedViewLeft;
                max = this.originalCapturedViewLeft + child.getWidth();
            } else {
                min = this.originalCapturedViewLeft - child.getWidth();
                max = this.originalCapturedViewLeft;
            }
            return SwipeDismissBehavior.clamp(min, left, max);
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            return child.getTop();
        }

        public void onViewPositionChanged(View child, int left, int top, int dx, int dy) {
            float startAlphaDistance = ((float) this.originalCapturedViewLeft) + (((float) child.getWidth()) * SwipeDismissBehavior.this.alphaStartSwipeDistance);
            float endAlphaDistance = ((float) this.originalCapturedViewLeft) + (((float) child.getWidth()) * SwipeDismissBehavior.this.alphaEndSwipeDistance);
            if (((float) left) <= startAlphaDistance) {
                child.setAlpha(1.0f);
            } else if (((float) left) >= endAlphaDistance) {
                child.setAlpha(0.0f);
            } else {
                child.setAlpha(SwipeDismissBehavior.clamp(0.0f, 1.0f - SwipeDismissBehavior.fraction(startAlphaDistance, endAlphaDistance, (float) left), 1.0f));
            }
        }
    };
    float dragDismissThreshold = 0.5f;
    private boolean interceptingEvents;
    OnDismissListener listener;
    private float sensitivity = 0.0f;
    private boolean sensitivitySet;
    int swipeDirection = 2;
    ViewDragHelper viewDragHelper;

    public interface OnDismissListener {
        void onDismiss(View view);

        void onDragStateChanged(int i);
    }

    public void setListener(OnDismissListener listener2) {
        this.listener = listener2;
    }

    public void setSwipeDirection(int direction) {
        this.swipeDirection = direction;
    }

    public void setDragDismissDistance(float distance) {
        this.dragDismissThreshold = clamp(0.0f, distance, 1.0f);
    }

    public void setStartAlphaSwipeDistance(float fraction) {
        this.alphaStartSwipeDistance = clamp(0.0f, fraction, 1.0f);
    }

    public void setEndAlphaSwipeDistance(float fraction) {
        this.alphaEndSwipeDistance = clamp(0.0f, fraction, 1.0f);
    }

    public void setSensitivity(float sensitivity2) {
        this.sensitivity = sensitivity2;
        this.sensitivitySet = true;
    }

    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        boolean dispatchEventToHelper = this.interceptingEvents;
        int actionMasked = event.getActionMasked();
        if (actionMasked != 3) {
            switch (actionMasked) {
                case 0:
                    this.interceptingEvents = parent.isPointInChildBounds(child, (int) event.getX(), (int) event.getY());
                    dispatchEventToHelper = this.interceptingEvents;
                    break;
                case 1:
                    break;
            }
        }
        this.interceptingEvents = false;
        if (!dispatchEventToHelper) {
            return false;
        }
        ensureViewDragHelper(parent);
        return this.viewDragHelper.shouldInterceptTouchEvent(event);
    }

    public boolean onTouchEvent(CoordinatorLayout parent, V v, MotionEvent event) {
        if (this.viewDragHelper == null) {
            return false;
        }
        this.viewDragHelper.processTouchEvent(event);
        return true;
    }

    public boolean canSwipeDismissView(@NonNull View view) {
        return true;
    }

    private void ensureViewDragHelper(ViewGroup parent) {
        ViewDragHelper viewDragHelper2;
        if (this.viewDragHelper == null) {
            if (this.sensitivitySet) {
                viewDragHelper2 = ViewDragHelper.create(parent, this.sensitivity, this.dragCallback);
            } else {
                viewDragHelper2 = ViewDragHelper.create(parent, this.dragCallback);
            }
            this.viewDragHelper = viewDragHelper2;
        }
    }

    private class SettleRunnable implements Runnable {
        private final boolean dismiss;
        private final View view;

        SettleRunnable(View view2, boolean dismiss2) {
            this.view = view2;
            this.dismiss = dismiss2;
        }

        public void run() {
            if (SwipeDismissBehavior.this.viewDragHelper != null && SwipeDismissBehavior.this.viewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(this.view, this);
            } else if (this.dismiss && SwipeDismissBehavior.this.listener != null) {
                SwipeDismissBehavior.this.listener.onDismiss(this.view);
            }
        }
    }

    static float clamp(float min, float value, float max) {
        return Math.min(Math.max(min, value), max);
    }

    static int clamp(int min, int value, int max) {
        return Math.min(Math.max(min, value), max);
    }

    public int getDragState() {
        if (this.viewDragHelper != null) {
            return this.viewDragHelper.getViewDragState();
        }
        return 0;
    }

    static float fraction(float startValue, float endValue, float value) {
        return (value - startValue) / (endValue - startValue);
    }
}
