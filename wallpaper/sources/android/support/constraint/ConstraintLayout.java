package android.support.constraint;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.constraint.solver.Metrics;
import android.support.constraint.solver.widgets.Analyzer;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.constraint.solver.widgets.ConstraintWidget;
import android.support.constraint.solver.widgets.ConstraintWidgetContainer;
import android.support.constraint.solver.widgets.Guideline;
import android.support.constraint.solver.widgets.ResolutionAnchor;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;

public class ConstraintLayout extends ViewGroup {
    static final boolean ALLOWS_EMBEDDED = false;
    private static final boolean CACHE_MEASURED_DIMENSION = false;
    private static final boolean DEBUG = false;
    public static final int DESIGN_INFO_ID = 0;
    private static final String TAG = "ConstraintLayout";
    private static final boolean USE_CONSTRAINTS_HELPER = true;
    public static final String VERSION = "ConstraintLayout-1.1.3";
    SparseArray<View> mChildrenByIds = new SparseArray<>();
    private ArrayList<ConstraintHelper> mConstraintHelpers = new ArrayList<>(4);
    private ConstraintSet mConstraintSet = null;
    private int mConstraintSetId = -1;
    private HashMap<String, Integer> mDesignIds = new HashMap<>();
    private boolean mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
    private int mLastMeasureHeight = -1;
    int mLastMeasureHeightMode = 0;
    int mLastMeasureHeightSize = -1;
    private int mLastMeasureWidth = -1;
    int mLastMeasureWidthMode = 0;
    int mLastMeasureWidthSize = -1;
    ConstraintWidgetContainer mLayoutWidget = new ConstraintWidgetContainer();
    private int mMaxHeight = Integer.MAX_VALUE;
    private int mMaxWidth = Integer.MAX_VALUE;
    private Metrics mMetrics;
    private int mMinHeight = 0;
    private int mMinWidth = 0;
    private int mOptimizationLevel = 7;
    private final ArrayList<ConstraintWidget> mVariableDimensionsWidgets = new ArrayList<>(100);

    public void setDesignInformation(int type, Object value1, Object value2) {
        if (type == 0 && (value1 instanceof String) && (value2 instanceof Integer)) {
            if (this.mDesignIds == null) {
                this.mDesignIds = new HashMap<>();
            }
            String name = (String) value1;
            int index = name.indexOf("/");
            if (index != -1) {
                name = name.substring(index + 1);
            }
            this.mDesignIds.put(name, Integer.valueOf(((Integer) value2).intValue()));
        }
    }

    public Object getDesignInformation(int type, Object value) {
        if (type != 0 || !(value instanceof String)) {
            return null;
        }
        String name = (String) value;
        if (this.mDesignIds == null || !this.mDesignIds.containsKey(name)) {
            return null;
        }
        return this.mDesignIds.get(name);
    }

    public ConstraintLayout(Context context) {
        super(context);
        init((AttributeSet) null);
    }

    public ConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void setId(int id) {
        this.mChildrenByIds.remove(getId());
        super.setId(id);
        this.mChildrenByIds.put(getId(), this);
    }

    private void init(AttributeSet attrs) {
        this.mLayoutWidget.setCompanionWidget(this);
        this.mChildrenByIds.put(getId(), this);
        this.mConstraintSet = null;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_Layout);
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.ConstraintLayout_Layout_android_minWidth) {
                    this.mMinWidth = a.getDimensionPixelOffset(attr, this.mMinWidth);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_minHeight) {
                    this.mMinHeight = a.getDimensionPixelOffset(attr, this.mMinHeight);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_maxWidth) {
                    this.mMaxWidth = a.getDimensionPixelOffset(attr, this.mMaxWidth);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_maxHeight) {
                    this.mMaxHeight = a.getDimensionPixelOffset(attr, this.mMaxHeight);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_optimizationLevel) {
                    this.mOptimizationLevel = a.getInt(attr, this.mOptimizationLevel);
                } else if (attr == R.styleable.ConstraintLayout_Layout_constraintSet) {
                    int id = a.getResourceId(attr, 0);
                    try {
                        this.mConstraintSet = new ConstraintSet();
                        this.mConstraintSet.load(getContext(), id);
                    } catch (Resources.NotFoundException e) {
                        this.mConstraintSet = null;
                    }
                    this.mConstraintSetId = id;
                }
            }
            a.recycle();
        }
        this.mLayoutWidget.setOptimizationLevel(this.mOptimizationLevel);
    }

    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (Build.VERSION.SDK_INT < 14) {
            onViewAdded(child);
        }
    }

    public void removeView(View view) {
        super.removeView(view);
        if (Build.VERSION.SDK_INT < 14) {
            onViewRemoved(view);
        }
    }

    public void onViewAdded(View view) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onViewAdded(view);
        }
        ConstraintWidget widget = getViewWidget(view);
        if ((view instanceof Guideline) && !(widget instanceof Guideline)) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.widget = new Guideline();
            layoutParams.isGuideline = USE_CONSTRAINTS_HELPER;
            ((Guideline) layoutParams.widget).setOrientation(layoutParams.orientation);
        }
        if (view instanceof ConstraintHelper) {
            ConstraintHelper helper = (ConstraintHelper) view;
            helper.validateParams();
            ((LayoutParams) view.getLayoutParams()).isHelper = USE_CONSTRAINTS_HELPER;
            if (!this.mConstraintHelpers.contains(helper)) {
                this.mConstraintHelpers.add(helper);
            }
        }
        this.mChildrenByIds.put(view.getId(), view);
        this.mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
    }

    public void onViewRemoved(View view) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onViewRemoved(view);
        }
        this.mChildrenByIds.remove(view.getId());
        ConstraintWidget widget = getViewWidget(view);
        this.mLayoutWidget.remove(widget);
        this.mConstraintHelpers.remove(view);
        this.mVariableDimensionsWidgets.remove(widget);
        this.mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
    }

    public void setMinWidth(int value) {
        if (value != this.mMinWidth) {
            this.mMinWidth = value;
            requestLayout();
        }
    }

    public void setMinHeight(int value) {
        if (value != this.mMinHeight) {
            this.mMinHeight = value;
            requestLayout();
        }
    }

    public int getMinWidth() {
        return this.mMinWidth;
    }

    public int getMinHeight() {
        return this.mMinHeight;
    }

    public void setMaxWidth(int value) {
        if (value != this.mMaxWidth) {
            this.mMaxWidth = value;
            requestLayout();
        }
    }

    public void setMaxHeight(int value) {
        if (value != this.mMaxHeight) {
            this.mMaxHeight = value;
            requestLayout();
        }
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    public int getMaxHeight() {
        return this.mMaxHeight;
    }

    private void updateHierarchy() {
        int count = getChildCount();
        boolean recompute = false;
        int i = 0;
        while (true) {
            if (i >= count) {
                break;
            } else if (getChildAt(i).isLayoutRequested()) {
                recompute = USE_CONSTRAINTS_HELPER;
                break;
            } else {
                i++;
            }
        }
        if (recompute) {
            this.mVariableDimensionsWidgets.clear();
            setChildrenConstraints();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:125:0x01fb A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setChildrenConstraints() {
        /*
            r33 = this;
            r1 = r33
            boolean r2 = r33.isInEditMode()
            int r3 = r33.getChildCount()
            r4 = 0
            r5 = -1
            if (r2 == 0) goto L_0x004b
            r6 = 0
        L_0x000f:
            if (r6 >= r3) goto L_0x004b
            android.view.View r7 = r1.getChildAt(r6)
            android.content.res.Resources r8 = r33.getResources()     // Catch:{ NotFoundException -> 0x0047 }
            int r9 = r7.getId()     // Catch:{ NotFoundException -> 0x0047 }
            java.lang.String r8 = r8.getResourceName(r9)     // Catch:{ NotFoundException -> 0x0047 }
            int r9 = r7.getId()     // Catch:{ NotFoundException -> 0x0047 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ NotFoundException -> 0x0047 }
            r1.setDesignInformation(r4, r8, r9)     // Catch:{ NotFoundException -> 0x0047 }
            r9 = 47
            int r9 = r8.indexOf(r9)     // Catch:{ NotFoundException -> 0x0047 }
            if (r9 == r5) goto L_0x003b
            int r10 = r9 + 1
            java.lang.String r10 = r8.substring(r10)     // Catch:{ NotFoundException -> 0x0047 }
            r8 = r10
        L_0x003b:
            int r10 = r7.getId()     // Catch:{ NotFoundException -> 0x0047 }
            android.support.constraint.solver.widgets.ConstraintWidget r10 = r1.getTargetWidget(r10)     // Catch:{ NotFoundException -> 0x0047 }
            r10.setDebugName(r8)     // Catch:{ NotFoundException -> 0x0047 }
            goto L_0x0048
        L_0x0047:
            r0 = move-exception
        L_0x0048:
            int r6 = r6 + 1
            goto L_0x000f
        L_0x004b:
            r6 = 0
        L_0x004c:
            if (r6 >= r3) goto L_0x005f
            android.view.View r7 = r1.getChildAt(r6)
            android.support.constraint.solver.widgets.ConstraintWidget r8 = r1.getViewWidget(r7)
            if (r8 != 0) goto L_0x0059
            goto L_0x005c
        L_0x0059:
            r8.reset()
        L_0x005c:
            int r6 = r6 + 1
            goto L_0x004c
        L_0x005f:
            int r6 = r1.mConstraintSetId
            if (r6 == r5) goto L_0x0082
            r6 = 0
        L_0x0064:
            if (r6 >= r3) goto L_0x0082
            android.view.View r7 = r1.getChildAt(r6)
            int r8 = r7.getId()
            int r9 = r1.mConstraintSetId
            if (r8 != r9) goto L_0x007f
            boolean r8 = r7 instanceof android.support.constraint.Constraints
            if (r8 == 0) goto L_0x007f
            r8 = r7
            android.support.constraint.Constraints r8 = (android.support.constraint.Constraints) r8
            android.support.constraint.ConstraintSet r8 = r8.getConstraintSet()
            r1.mConstraintSet = r8
        L_0x007f:
            int r6 = r6 + 1
            goto L_0x0064
        L_0x0082:
            android.support.constraint.ConstraintSet r6 = r1.mConstraintSet
            if (r6 == 0) goto L_0x008b
            android.support.constraint.ConstraintSet r6 = r1.mConstraintSet
            r6.applyToInternal(r1)
        L_0x008b:
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r6 = r1.mLayoutWidget
            r6.removeAllChildren()
            java.util.ArrayList<android.support.constraint.ConstraintHelper> r6 = r1.mConstraintHelpers
            int r6 = r6.size()
            if (r6 <= 0) goto L_0x00a9
            r7 = 0
        L_0x0099:
            if (r7 >= r6) goto L_0x00a9
            java.util.ArrayList<android.support.constraint.ConstraintHelper> r8 = r1.mConstraintHelpers
            java.lang.Object r8 = r8.get(r7)
            android.support.constraint.ConstraintHelper r8 = (android.support.constraint.ConstraintHelper) r8
            r8.updatePreLayout(r1)
            int r7 = r7 + 1
            goto L_0x0099
        L_0x00a9:
            r7 = 0
        L_0x00aa:
            if (r7 >= r3) goto L_0x00bd
            android.view.View r8 = r1.getChildAt(r7)
            boolean r9 = r8 instanceof android.support.constraint.Placeholder
            if (r9 == 0) goto L_0x00ba
            r9 = r8
            android.support.constraint.Placeholder r9 = (android.support.constraint.Placeholder) r9
            r9.updatePreLayout(r1)
        L_0x00ba:
            int r7 = r7 + 1
            goto L_0x00aa
        L_0x00bd:
            r7 = 0
        L_0x00be:
            if (r7 >= r3) goto L_0x046e
            android.view.View r8 = r1.getChildAt(r7)
            android.support.constraint.solver.widgets.ConstraintWidget r15 = r1.getViewWidget(r8)
            if (r15 != 0) goto L_0x00d3
        L_0x00cb:
            r17 = r3
            r23 = r6
            r9 = 0
            r10 = -1
            goto L_0x0464
        L_0x00d3:
            android.view.ViewGroup$LayoutParams r9 = r8.getLayoutParams()
            r14 = r9
            android.support.constraint.ConstraintLayout$LayoutParams r14 = (android.support.constraint.ConstraintLayout.LayoutParams) r14
            r14.validate()
            boolean r9 = r14.helped
            if (r9 == 0) goto L_0x00e4
            r14.helped = r4
            goto L_0x0117
        L_0x00e4:
            if (r2 == 0) goto L_0x0117
            android.content.res.Resources r9 = r33.getResources()     // Catch:{ NotFoundException -> 0x0116 }
            int r10 = r8.getId()     // Catch:{ NotFoundException -> 0x0116 }
            java.lang.String r9 = r9.getResourceName(r10)     // Catch:{ NotFoundException -> 0x0116 }
            int r10 = r8.getId()     // Catch:{ NotFoundException -> 0x0116 }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ NotFoundException -> 0x0116 }
            r1.setDesignInformation(r4, r9, r10)     // Catch:{ NotFoundException -> 0x0116 }
            java.lang.String r10 = "id/"
            int r10 = r9.indexOf(r10)     // Catch:{ NotFoundException -> 0x0116 }
            int r10 = r10 + 3
            java.lang.String r10 = r9.substring(r10)     // Catch:{ NotFoundException -> 0x0116 }
            r9 = r10
            int r10 = r8.getId()     // Catch:{ NotFoundException -> 0x0116 }
            android.support.constraint.solver.widgets.ConstraintWidget r10 = r1.getTargetWidget(r10)     // Catch:{ NotFoundException -> 0x0116 }
            r10.setDebugName(r9)     // Catch:{ NotFoundException -> 0x0116 }
            goto L_0x0117
        L_0x0116:
            r0 = move-exception
        L_0x0117:
            int r9 = r8.getVisibility()
            r15.setVisibility(r9)
            boolean r9 = r14.isInPlaceholder
            if (r9 == 0) goto L_0x0127
            r9 = 8
            r15.setVisibility(r9)
        L_0x0127:
            r15.setCompanionWidget(r8)
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r9 = r1.mLayoutWidget
            r9.add((android.support.constraint.solver.widgets.ConstraintWidget) r15)
            boolean r9 = r14.verticalDimensionFixed
            if (r9 == 0) goto L_0x0137
            boolean r9 = r14.horizontalDimensionFixed
            if (r9 != 0) goto L_0x013c
        L_0x0137:
            java.util.ArrayList<android.support.constraint.solver.widgets.ConstraintWidget> r9 = r1.mVariableDimensionsWidgets
            r9.add(r15)
        L_0x013c:
            boolean r9 = r14.isGuideline
            r10 = 17
            if (r9 == 0) goto L_0x016c
            r9 = r15
            android.support.constraint.solver.widgets.Guideline r9 = (android.support.constraint.solver.widgets.Guideline) r9
            int r11 = r14.resolvedGuideBegin
            int r12 = r14.resolvedGuideEnd
            float r13 = r14.resolvedGuidePercent
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r10) goto L_0x0155
            int r11 = r14.guideBegin
            int r12 = r14.guideEnd
            float r13 = r14.guidePercent
        L_0x0155:
            r4 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r4 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1))
            if (r4 == 0) goto L_0x015f
            r9.setGuidePercent((float) r13)
            goto L_0x016a
        L_0x015f:
            if (r11 == r5) goto L_0x0165
            r9.setGuideBegin(r11)
            goto L_0x016a
        L_0x0165:
            if (r12 == r5) goto L_0x016a
            r9.setGuideEnd(r12)
        L_0x016a:
            goto L_0x00cb
        L_0x016c:
            int r4 = r14.leftToLeft
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.leftToRight
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.rightToLeft
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.rightToRight
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.startToStart
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.startToEnd
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.endToStart
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.endToEnd
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.topToTop
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.topToBottom
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.bottomToTop
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.bottomToBottom
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.baselineToBaseline
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.editorAbsoluteX
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.editorAbsoluteY
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.circleConstraint
            if (r4 != r5) goto L_0x01b4
            int r4 = r14.width
            if (r4 == r5) goto L_0x01b4
            int r4 = r14.height
            if (r4 != r5) goto L_0x00cb
        L_0x01b4:
            int r4 = r14.resolvedLeftToLeft
            int r9 = r14.resolvedLeftToRight
            int r11 = r14.resolvedRightToLeft
            int r12 = r14.resolvedRightToRight
            int r13 = r14.resolveGoneLeftMargin
            int r5 = r14.resolveGoneRightMargin
            float r10 = r14.resolvedHorizontalBias
            r17 = r3
            int r3 = android.os.Build.VERSION.SDK_INT
            r18 = r4
            r4 = 17
            if (r3 >= r4) goto L_0x0218
            int r3 = r14.leftToLeft
            int r4 = r14.leftToRight
            int r11 = r14.rightToLeft
            int r12 = r14.rightToRight
            int r9 = r14.goneLeftMargin
            int r5 = r14.goneRightMargin
            float r10 = r14.horizontalBias
            r13 = -1
            if (r3 != r13) goto L_0x01f4
            if (r4 != r13) goto L_0x01f4
            r19 = r3
            int r3 = r14.startToStart
            if (r3 == r13) goto L_0x01ed
            int r3 = r14.startToStart
            r32 = r4
            r4 = r3
            r3 = r32
            goto L_0x01f9
        L_0x01ed:
            int r3 = r14.startToEnd
            if (r3 == r13) goto L_0x01f6
            int r3 = r14.startToEnd
            goto L_0x01f7
        L_0x01f4:
            r19 = r3
        L_0x01f6:
            r3 = r4
        L_0x01f7:
            r4 = r19
        L_0x01f9:
            if (r11 != r13) goto L_0x020d
            if (r12 != r13) goto L_0x020d
            r20 = r3
            int r3 = r14.endToStart
            if (r3 == r13) goto L_0x0206
            int r11 = r14.endToStart
            goto L_0x020f
        L_0x0206:
            int r3 = r14.endToEnd
            if (r3 == r13) goto L_0x020f
            int r12 = r14.endToEnd
            goto L_0x020f
        L_0x020d:
            r20 = r3
        L_0x020f:
            r18 = r5
            r16 = r9
            r13 = r11
            r5 = r20
            r3 = -1
            goto L_0x0221
        L_0x0218:
            r3 = -1
            r16 = r13
            r4 = r18
            r18 = r5
            r5 = r9
            r13 = r11
        L_0x0221:
            r11 = r10
            int r9 = r14.circleConstraint
            if (r9 == r3) goto L_0x0242
            int r3 = r14.circleConstraint
            android.support.constraint.solver.widgets.ConstraintWidget r3 = r1.getTargetWidget(r3)
            if (r3 == 0) goto L_0x0235
            float r9 = r14.circleAngle
            int r10 = r14.circleRadius
            r15.connectCircularConstraint(r3, r9, r10)
        L_0x0235:
            r21 = r4
            r23 = r6
            r24 = r8
            r4 = r11
            r3 = r12
            r6 = r13
            r8 = r14
            goto L_0x03ae
        L_0x0242:
            r3 = -1
            if (r4 == r3) goto L_0x0276
            android.support.constraint.solver.widgets.ConstraintWidget r3 = r1.getTargetWidget(r4)
            if (r3 == 0) goto L_0x026b
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r10 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.LEFT
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r19 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.LEFT
            int r9 = r14.leftMargin
            r20 = r9
            r9 = r15
            r21 = r4
            r4 = r11
            r11 = r3
            r22 = r3
            r3 = r12
            r12 = r19
            r23 = r6
            r6 = r13
            r13 = r20
            r24 = r8
            r8 = r14
            r14 = r16
            r9.immediateConnect(r10, r11, r12, r13, r14)
            goto L_0x0275
        L_0x026b:
            r21 = r4
            r23 = r6
            r24 = r8
            r4 = r11
            r3 = r12
            r6 = r13
            r8 = r14
        L_0x0275:
            goto L_0x0297
        L_0x0276:
            r21 = r4
            r23 = r6
            r24 = r8
            r4 = r11
            r3 = r12
            r6 = r13
            r8 = r14
            r9 = -1
            if (r5 == r9) goto L_0x0297
            android.support.constraint.solver.widgets.ConstraintWidget r19 = r1.getTargetWidget(r5)
            if (r19 == 0) goto L_0x0297
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r10 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.LEFT
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r12 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.RIGHT
            int r13 = r8.leftMargin
            r9 = r15
            r11 = r19
            r14 = r16
            r9.immediateConnect(r10, r11, r12, r13, r14)
        L_0x0297:
            r9 = -1
            if (r6 == r9) goto L_0x02af
            android.support.constraint.solver.widgets.ConstraintWidget r19 = r1.getTargetWidget(r6)
            if (r19 == 0) goto L_0x02ae
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r10 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.RIGHT
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r12 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.LEFT
            int r13 = r8.rightMargin
            r9 = r15
            r11 = r19
            r14 = r18
            r9.immediateConnect(r10, r11, r12, r13, r14)
        L_0x02ae:
            goto L_0x02c6
        L_0x02af:
            r9 = -1
            if (r3 == r9) goto L_0x02c6
            android.support.constraint.solver.widgets.ConstraintWidget r19 = r1.getTargetWidget(r3)
            if (r19 == 0) goto L_0x02c6
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r10 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.RIGHT
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r12 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.RIGHT
            int r13 = r8.rightMargin
            r9 = r15
            r11 = r19
            r14 = r18
            r9.immediateConnect(r10, r11, r12, r13, r14)
        L_0x02c6:
            int r9 = r8.topToTop
            r10 = -1
            if (r9 == r10) goto L_0x02e2
            int r9 = r8.topToTop
            android.support.constraint.solver.widgets.ConstraintWidget r19 = r1.getTargetWidget(r9)
            if (r19 == 0) goto L_0x02e1
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r10 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.TOP
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r12 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.TOP
            int r13 = r8.topMargin
            int r14 = r8.goneTopMargin
            r9 = r15
            r11 = r19
            r9.immediateConnect(r10, r11, r12, r13, r14)
        L_0x02e1:
            goto L_0x02fd
        L_0x02e2:
            int r9 = r8.topToBottom
            r10 = -1
            if (r9 == r10) goto L_0x02fd
            int r9 = r8.topToBottom
            android.support.constraint.solver.widgets.ConstraintWidget r19 = r1.getTargetWidget(r9)
            if (r19 == 0) goto L_0x02fd
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r10 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.TOP
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r12 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.BOTTOM
            int r13 = r8.topMargin
            int r14 = r8.goneTopMargin
            r9 = r15
            r11 = r19
            r9.immediateConnect(r10, r11, r12, r13, r14)
        L_0x02fd:
            int r9 = r8.bottomToTop
            r10 = -1
            if (r9 == r10) goto L_0x0319
            int r9 = r8.bottomToTop
            android.support.constraint.solver.widgets.ConstraintWidget r19 = r1.getTargetWidget(r9)
            if (r19 == 0) goto L_0x0318
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r10 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.BOTTOM
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r12 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.TOP
            int r13 = r8.bottomMargin
            int r14 = r8.goneBottomMargin
            r9 = r15
            r11 = r19
            r9.immediateConnect(r10, r11, r12, r13, r14)
        L_0x0318:
            goto L_0x0334
        L_0x0319:
            int r9 = r8.bottomToBottom
            r10 = -1
            if (r9 == r10) goto L_0x0334
            int r9 = r8.bottomToBottom
            android.support.constraint.solver.widgets.ConstraintWidget r19 = r1.getTargetWidget(r9)
            if (r19 == 0) goto L_0x0334
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r10 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.BOTTOM
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r12 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.BOTTOM
            int r13 = r8.bottomMargin
            int r14 = r8.goneBottomMargin
            r9 = r15
            r11 = r19
            r9.immediateConnect(r10, r11, r12, r13, r14)
        L_0x0334:
            int r9 = r8.baselineToBaseline
            r10 = -1
            if (r9 == r10) goto L_0x038f
            android.util.SparseArray<android.view.View> r9 = r1.mChildrenByIds
            int r10 = r8.baselineToBaseline
            java.lang.Object r9 = r9.get(r10)
            android.view.View r9 = (android.view.View) r9
            int r10 = r8.baselineToBaseline
            android.support.constraint.solver.widgets.ConstraintWidget r10 = r1.getTargetWidget(r10)
            if (r10 == 0) goto L_0x038f
            if (r9 == 0) goto L_0x038f
            android.view.ViewGroup$LayoutParams r11 = r9.getLayoutParams()
            boolean r11 = r11 instanceof android.support.constraint.ConstraintLayout.LayoutParams
            if (r11 == 0) goto L_0x038f
            android.view.ViewGroup$LayoutParams r11 = r9.getLayoutParams()
            android.support.constraint.ConstraintLayout$LayoutParams r11 = (android.support.constraint.ConstraintLayout.LayoutParams) r11
            r12 = 1
            r8.needsBaseline = r12
            r11.needsBaseline = r12
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r12 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.BASELINE
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r15.getAnchor(r12)
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r13 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.BASELINE
            android.support.constraint.solver.widgets.ConstraintAnchor r13 = r10.getAnchor(r13)
            r27 = 0
            r28 = -1
            android.support.constraint.solver.widgets.ConstraintAnchor$Strength r29 = android.support.constraint.solver.widgets.ConstraintAnchor.Strength.STRONG
            r30 = 0
            r31 = 1
            r25 = r12
            r26 = r13
            r25.connect(r26, r27, r28, r29, r30, r31)
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r14 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.TOP
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r15.getAnchor(r14)
            r14.reset()
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r14 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.BOTTOM
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r15.getAnchor(r14)
            r14.reset()
        L_0x038f:
            r9 = 0
            int r10 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            r11 = 1056964608(0x3f000000, float:0.5)
            if (r10 < 0) goto L_0x039d
            int r10 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
            if (r10 == 0) goto L_0x039d
            r15.setHorizontalBiasPercent(r4)
        L_0x039d:
            float r10 = r8.verticalBias
            int r9 = (r10 > r9 ? 1 : (r10 == r9 ? 0 : -1))
            if (r9 < 0) goto L_0x03ae
            float r9 = r8.verticalBias
            int r9 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r9 == 0) goto L_0x03ae
            float r9 = r8.verticalBias
            r15.setVerticalBiasPercent(r9)
        L_0x03ae:
            if (r2 == 0) goto L_0x03c0
            int r9 = r8.editorAbsoluteX
            r10 = -1
            if (r9 != r10) goto L_0x03b9
            int r9 = r8.editorAbsoluteY
            if (r9 == r10) goto L_0x03c0
        L_0x03b9:
            int r9 = r8.editorAbsoluteX
            int r10 = r8.editorAbsoluteY
            r15.setOrigin(r9, r10)
        L_0x03c0:
            boolean r9 = r8.horizontalDimensionFixed
            if (r9 != 0) goto L_0x03ed
            int r9 = r8.width
            r10 = -1
            if (r9 != r10) goto L_0x03e3
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r9 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_PARENT
            r15.setHorizontalDimensionBehaviour(r9)
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r9 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.LEFT
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r15.getAnchor(r9)
            int r10 = r8.leftMargin
            r9.mMargin = r10
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r9 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.RIGHT
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r15.getAnchor(r9)
            int r10 = r8.rightMargin
            r9.mMargin = r10
            goto L_0x03f7
        L_0x03e3:
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r9 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            r15.setHorizontalDimensionBehaviour(r9)
            r9 = 0
            r15.setWidth(r9)
            goto L_0x03f7
        L_0x03ed:
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r9 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.FIXED
            r15.setHorizontalDimensionBehaviour(r9)
            int r9 = r8.width
            r15.setWidth(r9)
        L_0x03f7:
            boolean r9 = r8.verticalDimensionFixed
            if (r9 != 0) goto L_0x0425
            int r9 = r8.height
            r10 = -1
            if (r9 != r10) goto L_0x041b
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r9 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_PARENT
            r15.setVerticalDimensionBehaviour(r9)
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r9 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.TOP
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r15.getAnchor(r9)
            int r11 = r8.topMargin
            r9.mMargin = r11
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r9 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.BOTTOM
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r15.getAnchor(r9)
            int r11 = r8.bottomMargin
            r9.mMargin = r11
            r9 = 0
            goto L_0x0431
        L_0x041b:
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r9 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            r15.setVerticalDimensionBehaviour(r9)
            r9 = 0
            r15.setHeight(r9)
            goto L_0x0431
        L_0x0425:
            r9 = 0
            r10 = -1
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r11 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.FIXED
            r15.setVerticalDimensionBehaviour(r11)
            int r11 = r8.height
            r15.setHeight(r11)
        L_0x0431:
            java.lang.String r11 = r8.dimensionRatio
            if (r11 == 0) goto L_0x043a
            java.lang.String r11 = r8.dimensionRatio
            r15.setDimensionRatio(r11)
        L_0x043a:
            float r11 = r8.horizontalWeight
            r15.setHorizontalWeight(r11)
            float r11 = r8.verticalWeight
            r15.setVerticalWeight(r11)
            int r11 = r8.horizontalChainStyle
            r15.setHorizontalChainStyle(r11)
            int r11 = r8.verticalChainStyle
            r15.setVerticalChainStyle(r11)
            int r11 = r8.matchConstraintDefaultWidth
            int r12 = r8.matchConstraintMinWidth
            int r13 = r8.matchConstraintMaxWidth
            float r14 = r8.matchConstraintPercentWidth
            r15.setHorizontalMatchStyle(r11, r12, r13, r14)
            int r11 = r8.matchConstraintDefaultHeight
            int r12 = r8.matchConstraintMinHeight
            int r13 = r8.matchConstraintMaxHeight
            float r14 = r8.matchConstraintPercentHeight
            r15.setVerticalMatchStyle(r11, r12, r13, r14)
        L_0x0464:
            int r7 = r7 + 1
            r3 = r17
            r6 = r23
            r4 = 0
            r5 = -1
            goto L_0x00be
        L_0x046e:
            r17 = r3
            r23 = r6
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.ConstraintLayout.setChildrenConstraints():void");
    }

    private final ConstraintWidget getTargetWidget(int id) {
        if (id == 0) {
            return this.mLayoutWidget;
        }
        View view = this.mChildrenByIds.get(id);
        if (view == null && (view = findViewById(id)) != null && view != this && view.getParent() == this) {
            onViewAdded(view);
        }
        if (view == this) {
            return this.mLayoutWidget;
        }
        if (view == null) {
            return null;
        }
        return ((LayoutParams) view.getLayoutParams()).widget;
    }

    public final ConstraintWidget getViewWidget(View view) {
        if (view == this) {
            return this.mLayoutWidget;
        }
        if (view == null) {
            return null;
        }
        return ((LayoutParams) view.getLayoutParams()).widget;
    }

    private void internalMeasureChildren(int parentWidthSpec, int parentHeightSpec) {
        int heightPadding;
        int baseline;
        int childWidthMeasureSpec;
        int childHeightMeasureSpec;
        ConstraintLayout constraintLayout = this;
        int i = parentWidthSpec;
        int i2 = parentHeightSpec;
        int heightPadding2 = getPaddingTop() + getPaddingBottom();
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int widgetsCount = getChildCount();
        int i3 = 0;
        while (i3 < widgetsCount) {
            View child = constraintLayout.getChildAt(i3);
            if (child.getVisibility() != 8) {
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                ConstraintWidget widget = params.widget;
                if (params.isGuideline) {
                    heightPadding = heightPadding2;
                } else if (!params.isHelper) {
                    widget.setVisibility(child.getVisibility());
                    int width = params.width;
                    int height = params.height;
                    boolean didWrapMeasureWidth = false;
                    boolean didWrapMeasureHeight = false;
                    if ((params.horizontalDimensionFixed || params.verticalDimensionFixed || (!params.horizontalDimensionFixed && params.matchConstraintDefaultWidth == 1) || params.width == -1 || (!params.verticalDimensionFixed && (params.matchConstraintDefaultHeight == 1 || params.height == -1))) ? USE_CONSTRAINTS_HELPER : false) {
                        if (width == 0) {
                            childWidthMeasureSpec = getChildMeasureSpec(i, widthPadding, -2);
                            didWrapMeasureWidth = USE_CONSTRAINTS_HELPER;
                        } else if (width == -1) {
                            childWidthMeasureSpec = getChildMeasureSpec(i, widthPadding, -1);
                        } else {
                            if (width == -2) {
                                didWrapMeasureWidth = USE_CONSTRAINTS_HELPER;
                            }
                            childWidthMeasureSpec = getChildMeasureSpec(i, widthPadding, width);
                        }
                        int childWidthMeasureSpec2 = childWidthMeasureSpec;
                        if (height == 0) {
                            childHeightMeasureSpec = getChildMeasureSpec(i2, heightPadding2, -2);
                            didWrapMeasureHeight = USE_CONSTRAINTS_HELPER;
                        } else if (height == -1) {
                            childHeightMeasureSpec = getChildMeasureSpec(i2, heightPadding2, -1);
                        } else {
                            if (height == -2) {
                                didWrapMeasureHeight = USE_CONSTRAINTS_HELPER;
                            }
                            childHeightMeasureSpec = getChildMeasureSpec(i2, heightPadding2, height);
                        }
                        child.measure(childWidthMeasureSpec2, childHeightMeasureSpec);
                        if (constraintLayout.mMetrics != null) {
                            heightPadding = heightPadding2;
                            constraintLayout.mMetrics.measures++;
                        } else {
                            heightPadding = heightPadding2;
                        }
                        widget.setWidthWrapContent(width == -2 ? USE_CONSTRAINTS_HELPER : false);
                        widget.setHeightWrapContent(height == -2 ? USE_CONSTRAINTS_HELPER : false);
                        width = child.getMeasuredWidth();
                        height = child.getMeasuredHeight();
                    } else {
                        heightPadding = heightPadding2;
                    }
                    widget.setWidth(width);
                    widget.setHeight(height);
                    if (didWrapMeasureWidth) {
                        widget.setWrapWidth(width);
                    }
                    if (didWrapMeasureHeight) {
                        widget.setWrapHeight(height);
                    }
                    if (params.needsBaseline && (baseline = child.getBaseline()) != -1) {
                        widget.setBaselineDistance(baseline);
                    }
                }
                i3++;
                heightPadding2 = heightPadding;
                constraintLayout = this;
                i = parentWidthSpec;
                i2 = parentHeightSpec;
            }
            heightPadding = heightPadding2;
            i3++;
            heightPadding2 = heightPadding;
            constraintLayout = this;
            i = parentWidthSpec;
            i2 = parentHeightSpec;
        }
    }

    private void updatePostMeasures() {
        int widgetsCount = getChildCount();
        for (int i = 0; i < widgetsCount; i++) {
            View child = getChildAt(i);
            if (child instanceof Placeholder) {
                ((Placeholder) child).updatePostMeasure(this);
            }
        }
        int helperCount = this.mConstraintHelpers.size();
        if (helperCount > 0) {
            for (int i2 = 0; i2 < helperCount; i2++) {
                this.mConstraintHelpers.get(i2).updatePostMeasure(this);
            }
        }
    }

    private void internalMeasureDimensions(int parentWidthSpec, int parentHeightSpec) {
        int i;
        int widthPadding;
        int heightPadding;
        int i2;
        int widgetsCount;
        boolean resolveWidth;
        int widthPadding2;
        int widthPadding3;
        boolean resolveHeight;
        int childHeightMeasureSpec;
        int heightPadding2;
        int widgetsCount2;
        int widthPadding4;
        int heightPadding3;
        int heightPadding4;
        int widthPadding5;
        int widgetsCount3;
        int baseline;
        int i3 = parentWidthSpec;
        int i4 = parentHeightSpec;
        int heightPadding5 = getPaddingTop() + getPaddingBottom();
        int widthPadding6 = getPaddingLeft() + getPaddingRight();
        int widgetsCount4 = getChildCount();
        int i5 = 0;
        while (true) {
            i = 8;
            if (i5 >= widgetsCount4) {
                break;
            }
            View child = getChildAt(i5);
            if (child.getVisibility() != 8) {
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                ConstraintWidget widget = params.widget;
                if (params.isGuideline) {
                    heightPadding3 = heightPadding5;
                    widthPadding4 = widthPadding6;
                    widgetsCount2 = widgetsCount4;
                } else if (!params.isHelper) {
                    widget.setVisibility(child.getVisibility());
                    int width = params.width;
                    int height = params.height;
                    if (width == 0) {
                        heightPadding4 = heightPadding5;
                        widthPadding5 = widthPadding6;
                        widgetsCount3 = widgetsCount4;
                    } else if (height == 0) {
                        heightPadding4 = heightPadding5;
                        widthPadding5 = widthPadding6;
                        widgetsCount3 = widgetsCount4;
                    } else {
                        boolean didWrapMeasureWidth = false;
                        boolean didWrapMeasureHeight = false;
                        if (width == -2) {
                            didWrapMeasureWidth = USE_CONSTRAINTS_HELPER;
                        }
                        int childWidthMeasureSpec = getChildMeasureSpec(i3, widthPadding6, width);
                        if (height == -2) {
                            didWrapMeasureHeight = USE_CONSTRAINTS_HELPER;
                        }
                        child.measure(childWidthMeasureSpec, getChildMeasureSpec(i4, heightPadding5, height));
                        if (this.mMetrics != null) {
                            heightPadding3 = heightPadding5;
                            widthPadding4 = widthPadding6;
                            widgetsCount2 = widgetsCount4;
                            this.mMetrics.measures++;
                        } else {
                            heightPadding3 = heightPadding5;
                            widthPadding4 = widthPadding6;
                            widgetsCount2 = widgetsCount4;
                        }
                        widget.setWidthWrapContent(width == -2 ? USE_CONSTRAINTS_HELPER : false);
                        widget.setHeightWrapContent(height == -2 ? USE_CONSTRAINTS_HELPER : false);
                        int width2 = child.getMeasuredWidth();
                        int height2 = child.getMeasuredHeight();
                        widget.setWidth(width2);
                        widget.setHeight(height2);
                        if (didWrapMeasureWidth) {
                            widget.setWrapWidth(width2);
                        }
                        if (didWrapMeasureHeight) {
                            widget.setWrapHeight(height2);
                        }
                        if (params.needsBaseline && (baseline = child.getBaseline()) != -1) {
                            widget.setBaselineDistance(baseline);
                        }
                        if (params.horizontalDimensionFixed != 0 && params.verticalDimensionFixed) {
                            widget.getResolutionWidth().resolve(width2);
                            widget.getResolutionHeight().resolve(height2);
                        }
                    }
                    widget.getResolutionWidth().invalidate();
                    widget.getResolutionHeight().invalidate();
                }
                i5++;
                heightPadding5 = heightPadding3;
                widthPadding6 = widthPadding4;
                widgetsCount4 = widgetsCount2;
                i4 = parentHeightSpec;
            }
            heightPadding3 = heightPadding5;
            widthPadding4 = widthPadding6;
            widgetsCount2 = widgetsCount4;
            i5++;
            heightPadding5 = heightPadding3;
            widthPadding6 = widthPadding4;
            widgetsCount4 = widgetsCount2;
            i4 = parentHeightSpec;
        }
        int heightPadding6 = heightPadding5;
        int widthPadding7 = widthPadding6;
        int widgetsCount5 = widgetsCount4;
        this.mLayoutWidget.solveGraph();
        int i6 = 0;
        while (true) {
            int widgetsCount6 = widgetsCount5;
            if (i6 < widgetsCount6) {
                View child2 = getChildAt(i6);
                if (child2.getVisibility() != i) {
                    LayoutParams params2 = (LayoutParams) child2.getLayoutParams();
                    ConstraintWidget widget2 = params2.widget;
                    if (params2.isGuideline) {
                        i2 = i6;
                        widgetsCount = widgetsCount6;
                        heightPadding = heightPadding6;
                        widthPadding = widthPadding7;
                    } else if (!params2.isHelper) {
                        widget2.setVisibility(child2.getVisibility());
                        int width3 = params2.width;
                        int height3 = params2.height;
                        if (width3 == 0 || height3 == 0) {
                            ResolutionAnchor left = widget2.getAnchor(ConstraintAnchor.Type.LEFT).getResolutionNode();
                            ResolutionAnchor right = widget2.getAnchor(ConstraintAnchor.Type.RIGHT).getResolutionNode();
                            boolean bothHorizontal = (widget2.getAnchor(ConstraintAnchor.Type.LEFT).getTarget() == null || widget2.getAnchor(ConstraintAnchor.Type.RIGHT).getTarget() == null) ? false : USE_CONSTRAINTS_HELPER;
                            ResolutionAnchor top = widget2.getAnchor(ConstraintAnchor.Type.TOP).getResolutionNode();
                            ResolutionAnchor bottom = widget2.getAnchor(ConstraintAnchor.Type.BOTTOM).getResolutionNode();
                            boolean bothVertical = (widget2.getAnchor(ConstraintAnchor.Type.TOP).getTarget() == null || widget2.getAnchor(ConstraintAnchor.Type.BOTTOM).getTarget() == null) ? false : USE_CONSTRAINTS_HELPER;
                            if (width3 != 0 || height3 != 0 || !bothHorizontal || !bothVertical) {
                                boolean didWrapMeasureWidth2 = false;
                                boolean didWrapMeasureHeight2 = false;
                                widgetsCount = widgetsCount6;
                                boolean resolveWidth2 = this.mLayoutWidget.getHorizontalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT ? USE_CONSTRAINTS_HELPER : false;
                                i2 = i6;
                                boolean resolveHeight2 = this.mLayoutWidget.getVerticalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT ? USE_CONSTRAINTS_HELPER : false;
                                if (!resolveWidth2) {
                                    widget2.getResolutionWidth().invalidate();
                                }
                                if (!resolveHeight2) {
                                    widget2.getResolutionHeight().invalidate();
                                }
                                if (width3 == 0) {
                                    if (!resolveWidth2 || !widget2.isSpreadWidth() || !bothHorizontal || !left.isResolved() || !right.isResolved()) {
                                        widthPadding3 = widthPadding7;
                                        boolean z = resolveWidth2;
                                        widthPadding2 = getChildMeasureSpec(i3, widthPadding3, -2);
                                        didWrapMeasureWidth2 = USE_CONSTRAINTS_HELPER;
                                        resolveWidth2 = false;
                                    } else {
                                        width3 = (int) (right.getResolvedValue() - left.getResolvedValue());
                                        widget2.getResolutionWidth().resolve(width3);
                                        widthPadding3 = widthPadding7;
                                        widthPadding2 = getChildMeasureSpec(i3, widthPadding3, width3);
                                    }
                                    resolveWidth = resolveWidth2;
                                } else {
                                    resolveWidth = resolveWidth2;
                                    widthPadding3 = widthPadding7;
                                    if (width3 == -1) {
                                        widthPadding2 = getChildMeasureSpec(i3, widthPadding3, -1);
                                    } else {
                                        if (width3 == -2) {
                                            didWrapMeasureWidth2 = true;
                                        }
                                        widthPadding2 = getChildMeasureSpec(i3, widthPadding3, width3);
                                    }
                                }
                                int childWidthMeasureSpec2 = widthPadding2;
                                if (height3 != 0) {
                                    resolveHeight = resolveHeight2;
                                    ResolutionAnchor resolutionAnchor = left;
                                    heightPadding2 = heightPadding6;
                                    int i7 = parentHeightSpec;
                                    if (height3 == -1) {
                                        childHeightMeasureSpec = getChildMeasureSpec(i7, heightPadding2, -1);
                                    } else {
                                        if (height3 == -2) {
                                            didWrapMeasureHeight2 = true;
                                        }
                                        childHeightMeasureSpec = getChildMeasureSpec(i7, heightPadding2, height3);
                                    }
                                } else if (!resolveHeight2 || !widget2.isSpreadHeight() || !bothVertical || !top.isResolved() || !bottom.isResolved()) {
                                    heightPadding2 = heightPadding6;
                                    ResolutionAnchor resolutionAnchor2 = left;
                                    childHeightMeasureSpec = getChildMeasureSpec(parentHeightSpec, heightPadding2, -2);
                                    didWrapMeasureHeight2 = USE_CONSTRAINTS_HELPER;
                                    resolveHeight = false;
                                } else {
                                    height3 = (int) (bottom.getResolvedValue() - top.getResolvedValue());
                                    widget2.getResolutionHeight().resolve(height3);
                                    resolveHeight = resolveHeight2;
                                    heightPadding2 = heightPadding6;
                                    childHeightMeasureSpec = getChildMeasureSpec(parentHeightSpec, heightPadding2, height3);
                                    ResolutionAnchor resolutionAnchor3 = left;
                                }
                                int childHeightMeasureSpec2 = childHeightMeasureSpec;
                                child2.measure(childWidthMeasureSpec2, childHeightMeasureSpec2);
                                if (this.mMetrics != null) {
                                    heightPadding = heightPadding2;
                                    int i8 = childWidthMeasureSpec2;
                                    int i9 = childHeightMeasureSpec2;
                                    widthPadding = widthPadding3;
                                    this.mMetrics.measures++;
                                } else {
                                    heightPadding = heightPadding2;
                                    int i10 = childWidthMeasureSpec2;
                                    int i11 = childHeightMeasureSpec2;
                                    widthPadding = widthPadding3;
                                }
                                widget2.setWidthWrapContent(width3 == -2 ? USE_CONSTRAINTS_HELPER : false);
                                widget2.setHeightWrapContent(height3 == -2 ? USE_CONSTRAINTS_HELPER : false);
                                int width4 = child2.getMeasuredWidth();
                                int height4 = child2.getMeasuredHeight();
                                widget2.setWidth(width4);
                                widget2.setHeight(height4);
                                if (didWrapMeasureWidth2) {
                                    widget2.setWrapWidth(width4);
                                }
                                if (didWrapMeasureHeight2) {
                                    widget2.setWrapHeight(height4);
                                }
                                if (resolveWidth) {
                                    widget2.getResolutionWidth().resolve(width4);
                                } else {
                                    widget2.getResolutionWidth().remove();
                                }
                                if (resolveHeight) {
                                    widget2.getResolutionHeight().resolve(height4);
                                } else {
                                    widget2.getResolutionHeight().remove();
                                }
                                if (params2.needsBaseline) {
                                    int baseline2 = child2.getBaseline();
                                    if (baseline2 != -1) {
                                        widget2.setBaselineDistance(baseline2);
                                    }
                                }
                            }
                        }
                    }
                    i6 = i2 + 1;
                    widgetsCount5 = widgetsCount;
                    heightPadding6 = heightPadding;
                    widthPadding7 = widthPadding;
                    i3 = parentWidthSpec;
                    i = 8;
                }
                i2 = i6;
                widgetsCount = widgetsCount6;
                heightPadding = heightPadding6;
                widthPadding = widthPadding7;
                i6 = i2 + 1;
                widgetsCount5 = widgetsCount;
                heightPadding6 = heightPadding;
                widthPadding7 = widthPadding;
                i3 = parentWidthSpec;
                i = 8;
            } else {
                int i12 = heightPadding6;
                int i13 = widthPadding7;
                return;
            }
        }
    }

    public void fillMetrics(Metrics metrics) {
        this.mMetrics = metrics;
        this.mLayoutWidget.fillMetrics(metrics);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int REMEASURES_B;
        int heightPadding;
        int childState;
        int startingHeight;
        int startingWidth;
        int startingHeight2;
        boolean optimiseDimensions;
        boolean containerWrapWidth;
        int startingWidth2;
        int heightPadding2;
        int i;
        int startingWidth3;
        int startingHeight3;
        int widthSpec;
        int heightSpec;
        LayoutParams params;
        int baseline;
        int i2 = widthMeasureSpec;
        int i3 = heightMeasureSpec;
        long time = System.currentTimeMillis();
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        this.mLayoutWidget.setX(paddingLeft);
        this.mLayoutWidget.setY(paddingTop);
        this.mLayoutWidget.setMaxWidth(this.mMaxWidth);
        this.mLayoutWidget.setMaxHeight(this.mMaxHeight);
        if (Build.VERSION.SDK_INT >= 17) {
            this.mLayoutWidget.setRtl(getLayoutDirection() == 1 ? USE_CONSTRAINTS_HELPER : false);
        }
        setSelfDimensionBehaviour(widthMeasureSpec, heightMeasureSpec);
        int startingWidth4 = this.mLayoutWidget.getWidth();
        int startingHeight4 = this.mLayoutWidget.getHeight();
        boolean runAnalyzer = false;
        if (this.mDirtyHierarchy) {
            this.mDirtyHierarchy = false;
            updateHierarchy();
            runAnalyzer = USE_CONSTRAINTS_HELPER;
        }
        long j = time;
        boolean optimiseDimensions2 = (this.mOptimizationLevel & 8) == 8 ? USE_CONSTRAINTS_HELPER : false;
        if (optimiseDimensions2) {
            this.mLayoutWidget.preOptimize();
            this.mLayoutWidget.optimizeForDimensions(startingWidth4, startingHeight4);
            internalMeasureDimensions(widthMeasureSpec, heightMeasureSpec);
        } else {
            internalMeasureChildren(widthMeasureSpec, heightMeasureSpec);
        }
        updatePostMeasures();
        if (getChildCount() > 0 && runAnalyzer) {
            Analyzer.determineGroups(this.mLayoutWidget);
        }
        if (this.mLayoutWidget.mGroupsWrapOptimized) {
            if (this.mLayoutWidget.mHorizontalWrapOptimized && widthMode == Integer.MIN_VALUE) {
                if (this.mLayoutWidget.mWrapFixedWidth < widthSize) {
                    this.mLayoutWidget.setWidth(this.mLayoutWidget.mWrapFixedWidth);
                }
                this.mLayoutWidget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
            }
            if (this.mLayoutWidget.mVerticalWrapOptimized && heightMode == Integer.MIN_VALUE) {
                if (this.mLayoutWidget.mWrapFixedHeight < heightSize) {
                    this.mLayoutWidget.setHeight(this.mLayoutWidget.mWrapFixedHeight);
                }
                this.mLayoutWidget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
            }
        }
        int REMEASURES_A = 0;
        if ((this.mOptimizationLevel & 32) == 32) {
            int width = this.mLayoutWidget.getWidth();
            int height = this.mLayoutWidget.getHeight();
            if (this.mLastMeasureWidth == width || widthMode != 1073741824) {
                REMEASURES_B = 0;
            } else {
                REMEASURES_B = 0;
                Analyzer.setPosition(this.mLayoutWidget.mWidgetGroups, 0, width);
            }
            if (this.mLastMeasureHeight != height && heightMode == 1073741824) {
                Analyzer.setPosition(this.mLayoutWidget.mWidgetGroups, 1, height);
            }
            if (this.mLayoutWidget.mHorizontalWrapOptimized && this.mLayoutWidget.mWrapFixedWidth > widthSize) {
                Analyzer.setPosition(this.mLayoutWidget.mWidgetGroups, 0, widthSize);
            }
            if (this.mLayoutWidget.mVerticalWrapOptimized && this.mLayoutWidget.mWrapFixedHeight > heightSize) {
                Analyzer.setPosition(this.mLayoutWidget.mWidgetGroups, 1, heightSize);
            }
        } else {
            REMEASURES_B = 0;
        }
        if (getChildCount() > 0) {
            solveLinearSystem("First pass");
        }
        int sizeDependentWidgetsCount = this.mVariableDimensionsWidgets.size();
        int heightPadding3 = getPaddingBottom() + paddingTop;
        int widthPadding = paddingLeft + getPaddingRight();
        if (sizeDependentWidgetsCount > 0) {
            boolean needSolverPass = false;
            int i4 = widthMode;
            boolean containerWrapWidth2 = this.mLayoutWidget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT ? USE_CONSTRAINTS_HELPER : false;
            int i5 = widthSize;
            boolean containerWrapHeight = this.mLayoutWidget.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT ? USE_CONSTRAINTS_HELPER : false;
            int i6 = heightMode;
            int i7 = heightSize;
            int i8 = paddingLeft;
            childState = 0;
            int minWidth = Math.max(this.mLayoutWidget.getWidth(), this.mMinWidth);
            int minHeight = Math.max(this.mLayoutWidget.getHeight(), this.mMinHeight);
            int i9 = 0;
            while (i9 < sizeDependentWidgetsCount) {
                int paddingTop2 = paddingTop;
                ConstraintWidget widget = this.mVariableDimensionsWidgets.get(i9);
                int sizeDependentWidgetsCount2 = sizeDependentWidgetsCount;
                View child = (View) widget.getCompanionWidget();
                if (child == null) {
                    i = i9;
                    startingWidth3 = startingWidth4;
                    startingHeight3 = startingHeight4;
                } else {
                    startingHeight3 = startingHeight4;
                    LayoutParams params2 = (LayoutParams) child.getLayoutParams();
                    startingWidth3 = startingWidth4;
                    if (params2.isHelper != 0) {
                        i = i9;
                        heightPadding2 = heightPadding3;
                    } else if (params2.isGuideline) {
                        i = i9;
                    } else {
                        i = i9;
                        if (child.getVisibility() != 8 && (!optimiseDimensions2 || !widget.getResolutionWidth().isResolved() || !widget.getResolutionHeight().isResolved())) {
                            if (params2.width != -2 || !params2.horizontalDimensionFixed) {
                                widthSpec = View.MeasureSpec.makeMeasureSpec(widget.getWidth(), 1073741824);
                            } else {
                                widthSpec = getChildMeasureSpec(i2, widthPadding, params2.width);
                            }
                            if (params2.height != -2 || !params2.verticalDimensionFixed) {
                                heightSpec = View.MeasureSpec.makeMeasureSpec(widget.getHeight(), 1073741824);
                            } else {
                                heightSpec = getChildMeasureSpec(i3, heightPadding3, params2.height);
                            }
                            child.measure(widthSpec, heightSpec);
                            if (this.mMetrics != null) {
                                int i10 = heightSpec;
                                params = params2;
                                heightPadding2 = heightPadding3;
                                this.mMetrics.additionalMeasures++;
                            } else {
                                params = params2;
                                heightPadding2 = heightPadding3;
                            }
                            REMEASURES_A++;
                            int measuredWidth = child.getMeasuredWidth();
                            int measuredHeight = child.getMeasuredHeight();
                            if (measuredWidth != widget.getWidth()) {
                                widget.setWidth(measuredWidth);
                                if (optimiseDimensions2) {
                                    widget.getResolutionWidth().resolve(measuredWidth);
                                }
                                if (containerWrapWidth2 && widget.getRight() > minWidth) {
                                    minWidth = Math.max(minWidth, widget.getRight() + widget.getAnchor(ConstraintAnchor.Type.RIGHT).getMargin());
                                }
                                needSolverPass = USE_CONSTRAINTS_HELPER;
                            }
                            if (measuredHeight != widget.getHeight()) {
                                widget.setHeight(measuredHeight);
                                if (optimiseDimensions2) {
                                    widget.getResolutionHeight().resolve(measuredHeight);
                                }
                                if (containerWrapHeight && widget.getBottom() > minHeight) {
                                    minHeight = Math.max(minHeight, widget.getBottom() + widget.getAnchor(ConstraintAnchor.Type.BOTTOM).getMargin());
                                }
                                needSolverPass = USE_CONSTRAINTS_HELPER;
                            }
                            if (!(!params.needsBaseline || (baseline = child.getBaseline()) == -1 || baseline == widget.getBaselineDistance())) {
                                widget.setBaselineDistance(baseline);
                                needSolverPass = USE_CONSTRAINTS_HELPER;
                            }
                            if (Build.VERSION.SDK_INT >= 11) {
                                childState = combineMeasuredStates(childState, child.getMeasuredState());
                            }
                        }
                    }
                    i9 = i + 1;
                    paddingTop = paddingTop2;
                    sizeDependentWidgetsCount = sizeDependentWidgetsCount2;
                    startingHeight4 = startingHeight3;
                    startingWidth4 = startingWidth3;
                    heightPadding3 = heightPadding2;
                    i2 = widthMeasureSpec;
                    i3 = heightMeasureSpec;
                }
                heightPadding2 = heightPadding3;
                i9 = i + 1;
                paddingTop = paddingTop2;
                sizeDependentWidgetsCount = sizeDependentWidgetsCount2;
                startingHeight4 = startingHeight3;
                startingWidth4 = startingWidth3;
                heightPadding3 = heightPadding2;
                i2 = widthMeasureSpec;
                i3 = heightMeasureSpec;
            }
            int sizeDependentWidgetsCount3 = sizeDependentWidgetsCount;
            int i11 = paddingTop;
            int startingWidth5 = startingWidth4;
            int startingHeight5 = startingHeight4;
            heightPadding = heightPadding3;
            if (needSolverPass) {
                startingWidth = startingWidth5;
                this.mLayoutWidget.setWidth(startingWidth);
                startingHeight = startingHeight5;
                this.mLayoutWidget.setHeight(startingHeight);
                if (optimiseDimensions2) {
                    this.mLayoutWidget.solveGraph();
                }
                solveLinearSystem("2nd pass");
                boolean needSolverPass2 = false;
                if (this.mLayoutWidget.getWidth() < minWidth) {
                    this.mLayoutWidget.setWidth(minWidth);
                    needSolverPass2 = USE_CONSTRAINTS_HELPER;
                }
                if (this.mLayoutWidget.getHeight() < minHeight) {
                    this.mLayoutWidget.setHeight(minHeight);
                    needSolverPass2 = USE_CONSTRAINTS_HELPER;
                }
                if (needSolverPass2) {
                    solveLinearSystem("3rd pass");
                }
            } else {
                startingHeight = startingHeight5;
                startingWidth = startingWidth5;
            }
            int i12 = 0;
            while (true) {
                int i13 = i12;
                int sizeDependentWidgetsCount4 = sizeDependentWidgetsCount3;
                if (i13 >= sizeDependentWidgetsCount4) {
                    break;
                }
                ConstraintWidget widget2 = this.mVariableDimensionsWidgets.get(i13);
                View child2 = (View) widget2.getCompanionWidget();
                if (child2 == null || (child2.getMeasuredWidth() == widget2.getWidth() && child2.getMeasuredHeight() == widget2.getHeight())) {
                    startingWidth2 = startingWidth;
                    containerWrapWidth = containerWrapWidth2;
                    optimiseDimensions = optimiseDimensions2;
                    startingHeight2 = startingHeight;
                } else if (widget2.getVisibility() != 8) {
                    int widthSpec2 = View.MeasureSpec.makeMeasureSpec(widget2.getWidth(), 1073741824);
                    startingWidth2 = startingWidth;
                    int startingWidth6 = View.MeasureSpec.makeMeasureSpec(widget2.getHeight(), 1073741824);
                    child2.measure(widthSpec2, startingWidth6);
                    if (this.mMetrics != null) {
                        int i14 = startingWidth6;
                        containerWrapWidth = containerWrapWidth2;
                        optimiseDimensions = optimiseDimensions2;
                        startingHeight2 = startingHeight;
                        this.mMetrics.additionalMeasures++;
                    } else {
                        int heightSpec2 = startingWidth6;
                        containerWrapWidth = containerWrapWidth2;
                        optimiseDimensions = optimiseDimensions2;
                        startingHeight2 = startingHeight;
                    }
                    REMEASURES_B++;
                } else {
                    startingWidth2 = startingWidth;
                    containerWrapWidth = containerWrapWidth2;
                    optimiseDimensions = optimiseDimensions2;
                    startingHeight2 = startingHeight;
                }
                i12 = i13 + 1;
                sizeDependentWidgetsCount3 = sizeDependentWidgetsCount4;
                startingWidth = startingWidth2;
                containerWrapWidth2 = containerWrapWidth;
                optimiseDimensions2 = optimiseDimensions;
                startingHeight = startingHeight2;
            }
            boolean z = optimiseDimensions2;
            int i15 = startingHeight;
        } else {
            int i16 = widthMode;
            int i17 = widthSize;
            int i18 = heightMode;
            int i19 = heightSize;
            int i20 = paddingLeft;
            int i21 = paddingTop;
            int i22 = startingWidth4;
            int i23 = startingHeight4;
            heightPadding = heightPadding3;
            int widthSize2 = sizeDependentWidgetsCount;
            childState = 0;
        }
        int androidLayoutWidth = this.mLayoutWidget.getWidth() + widthPadding;
        int androidLayoutHeight = this.mLayoutWidget.getHeight() + heightPadding;
        if (Build.VERSION.SDK_INT >= 11) {
            int resolvedWidthSize = resolveSizeAndState(androidLayoutWidth, widthMeasureSpec, childState);
            int resolvedHeightSize = resolveSizeAndState(androidLayoutHeight, heightMeasureSpec, childState << 16);
            int resolvedWidthSize2 = resolvedWidthSize & ViewCompat.MEASURED_SIZE_MASK;
            int resolvedHeightSize2 = resolvedHeightSize & ViewCompat.MEASURED_SIZE_MASK;
            int resolvedWidthSize3 = Math.min(this.mMaxWidth, resolvedWidthSize2);
            int resolvedHeightSize3 = Math.min(this.mMaxHeight, resolvedHeightSize2);
            if (this.mLayoutWidget.isWidthMeasuredTooSmall()) {
                resolvedWidthSize3 |= 16777216;
            }
            if (this.mLayoutWidget.isHeightMeasuredTooSmall()) {
                resolvedHeightSize3 |= 16777216;
            }
            setMeasuredDimension(resolvedWidthSize3, resolvedHeightSize3);
            this.mLastMeasureWidth = resolvedWidthSize3;
            this.mLastMeasureHeight = resolvedHeightSize3;
            return;
        }
        int i24 = widthMeasureSpec;
        int i25 = heightMeasureSpec;
        setMeasuredDimension(androidLayoutWidth, androidLayoutHeight);
        this.mLastMeasureWidth = androidLayoutWidth;
        this.mLastMeasureHeight = androidLayoutHeight;
    }

    private void setSelfDimensionBehaviour(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int heightPadding = getPaddingTop() + getPaddingBottom();
        int widthPadding = getPaddingLeft() + getPaddingRight();
        ConstraintWidget.DimensionBehaviour widthBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
        ConstraintWidget.DimensionBehaviour heightBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
        int desiredWidth = 0;
        int desiredHeight = 0;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (widthMode == Integer.MIN_VALUE) {
            widthBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
            desiredWidth = widthSize;
        } else if (widthMode == 0) {
            widthBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        } else if (widthMode == 1073741824) {
            desiredWidth = Math.min(this.mMaxWidth, widthSize) - widthPadding;
        }
        if (heightMode == Integer.MIN_VALUE) {
            heightBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
            desiredHeight = heightSize;
        } else if (heightMode == 0) {
            heightBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        } else if (heightMode == 1073741824) {
            desiredHeight = Math.min(this.mMaxHeight, heightSize) - heightPadding;
        }
        this.mLayoutWidget.setMinWidth(0);
        this.mLayoutWidget.setMinHeight(0);
        this.mLayoutWidget.setHorizontalDimensionBehaviour(widthBehaviour);
        this.mLayoutWidget.setWidth(desiredWidth);
        this.mLayoutWidget.setVerticalDimensionBehaviour(heightBehaviour);
        this.mLayoutWidget.setHeight(desiredHeight);
        this.mLayoutWidget.setMinWidth((this.mMinWidth - getPaddingLeft()) - getPaddingRight());
        this.mLayoutWidget.setMinHeight((this.mMinHeight - getPaddingTop()) - getPaddingBottom());
    }

    /* access modifiers changed from: protected */
    public void solveLinearSystem(String reason) {
        this.mLayoutWidget.layout();
        if (this.mMetrics != null) {
            this.mMetrics.resolutions++;
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View content;
        int widgetsCount = getChildCount();
        boolean isInEditMode = isInEditMode();
        for (int i = 0; i < widgetsCount; i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            ConstraintWidget widget = params.widget;
            if ((child.getVisibility() != 8 || params.isGuideline || params.isHelper || isInEditMode) && !params.isInPlaceholder) {
                int l = widget.getDrawX();
                int t = widget.getDrawY();
                int r = widget.getWidth() + l;
                int b = widget.getHeight() + t;
                child.layout(l, t, r, b);
                if ((child instanceof Placeholder) && (content = ((Placeholder) child).getContent()) != null) {
                    content.setVisibility(0);
                    content.layout(l, t, r, b);
                }
            }
        }
        int helperCount = this.mConstraintHelpers.size();
        if (helperCount > 0) {
            for (int i2 = 0; i2 < helperCount; i2++) {
                this.mConstraintHelpers.get(i2).updatePostLayout(this);
            }
        }
    }

    public void setOptimizationLevel(int level) {
        this.mLayoutWidget.setOptimizationLevel(level);
    }

    public int getOptimizationLevel() {
        return this.mLayoutWidget.getOptimizationLevel();
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public void setConstraintSet(ConstraintSet set) {
        this.mConstraintSet = set;
    }

    public View getViewById(int id) {
        return this.mChildrenByIds.get(id);
    }

    public void dispatchDraw(Canvas canvas) {
        float ow;
        float ch;
        float cw;
        int count;
        super.dispatchDraw(canvas);
        if (isInEditMode()) {
            int count2 = getChildCount();
            float cw2 = (float) getWidth();
            float ch2 = (float) getHeight();
            float ow2 = 1080.0f;
            char c = 0;
            int i = 0;
            while (i < count2) {
                View child = getChildAt(i);
                if (child.getVisibility() == 8) {
                    count = count2;
                    cw = cw2;
                    ch = ch2;
                    ow = ow2;
                } else {
                    Object tag = child.getTag();
                    if (tag != null && (tag instanceof String)) {
                        String[] split = ((String) tag).split(",");
                        if (split.length == 4) {
                            int x = Integer.parseInt(split[c]);
                            int y = Integer.parseInt(split[1]);
                            int x2 = (int) ((((float) x) / ow2) * cw2);
                            int y2 = (int) ((((float) y) / 1920.0f) * ch2);
                            int w = (int) ((((float) Integer.parseInt(split[2])) / ow2) * cw2);
                            int h = (int) ((((float) Integer.parseInt(split[3])) / 1920.0f) * ch2);
                            Paint paint = new Paint();
                            count = count2;
                            paint.setColor(SupportMenu.CATEGORY_MASK);
                            cw = cw2;
                            ch = ch2;
                            ow = ow2;
                            Canvas canvas2 = canvas;
                            Paint paint2 = paint;
                            canvas2.drawLine((float) x2, (float) y2, (float) (x2 + w), (float) y2, paint2);
                            canvas2.drawLine((float) (x2 + w), (float) y2, (float) (x2 + w), (float) (y2 + h), paint2);
                            canvas2.drawLine((float) (x2 + w), (float) (y2 + h), (float) x2, (float) (y2 + h), paint2);
                            canvas2.drawLine((float) x2, (float) (y2 + h), (float) x2, (float) y2, paint2);
                            paint.setColor(-16711936);
                            canvas2.drawLine((float) x2, (float) y2, (float) (x2 + w), (float) (y2 + h), paint2);
                            canvas2.drawLine((float) x2, (float) (y2 + h), (float) (x2 + w), (float) y2, paint2);
                        }
                    }
                    count = count2;
                    cw = cw2;
                    ch = ch2;
                    ow = ow2;
                }
                i++;
                count2 = count;
                cw2 = cw;
                ch2 = ch;
                ow2 = ow;
                c = 0;
            }
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public static final int BASELINE = 5;
        public static final int BOTTOM = 4;
        public static final int CHAIN_PACKED = 2;
        public static final int CHAIN_SPREAD = 0;
        public static final int CHAIN_SPREAD_INSIDE = 1;
        public static final int END = 7;
        public static final int HORIZONTAL = 0;
        public static final int LEFT = 1;
        public static final int MATCH_CONSTRAINT = 0;
        public static final int MATCH_CONSTRAINT_PERCENT = 2;
        public static final int MATCH_CONSTRAINT_SPREAD = 0;
        public static final int MATCH_CONSTRAINT_WRAP = 1;
        public static final int PARENT_ID = 0;
        public static final int RIGHT = 2;
        public static final int START = 6;
        public static final int TOP = 3;
        public static final int UNSET = -1;
        public static final int VERTICAL = 1;
        public int baselineToBaseline;
        public int bottomToBottom;
        public int bottomToTop;
        public float circleAngle;
        public int circleConstraint;
        public int circleRadius;
        public boolean constrainedHeight;
        public boolean constrainedWidth;
        public String dimensionRatio;
        int dimensionRatioSide;
        float dimensionRatioValue;
        public int editorAbsoluteX;
        public int editorAbsoluteY;
        public int endToEnd;
        public int endToStart;
        public int goneBottomMargin;
        public int goneEndMargin;
        public int goneLeftMargin;
        public int goneRightMargin;
        public int goneStartMargin;
        public int goneTopMargin;
        public int guideBegin;
        public int guideEnd;
        public float guidePercent;
        public boolean helped;
        public float horizontalBias;
        public int horizontalChainStyle;
        boolean horizontalDimensionFixed;
        public float horizontalWeight;
        boolean isGuideline;
        boolean isHelper;
        boolean isInPlaceholder;
        public int leftToLeft;
        public int leftToRight;
        public int matchConstraintDefaultHeight;
        public int matchConstraintDefaultWidth;
        public int matchConstraintMaxHeight;
        public int matchConstraintMaxWidth;
        public int matchConstraintMinHeight;
        public int matchConstraintMinWidth;
        public float matchConstraintPercentHeight;
        public float matchConstraintPercentWidth;
        boolean needsBaseline;
        public int orientation;
        int resolveGoneLeftMargin;
        int resolveGoneRightMargin;
        int resolvedGuideBegin;
        int resolvedGuideEnd;
        float resolvedGuidePercent;
        float resolvedHorizontalBias;
        int resolvedLeftToLeft;
        int resolvedLeftToRight;
        int resolvedRightToLeft;
        int resolvedRightToRight;
        public int rightToLeft;
        public int rightToRight;
        public int startToEnd;
        public int startToStart;
        public int topToBottom;
        public int topToTop;
        public float verticalBias;
        public int verticalChainStyle;
        boolean verticalDimensionFixed;
        public float verticalWeight;
        ConstraintWidget widget;

        public void reset() {
            if (this.widget != null) {
                this.widget.reset();
            }
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.guideBegin = -1;
            this.guideEnd = -1;
            this.guidePercent = -1.0f;
            this.leftToLeft = -1;
            this.leftToRight = -1;
            this.rightToLeft = -1;
            this.rightToRight = -1;
            this.topToTop = -1;
            this.topToBottom = -1;
            this.bottomToTop = -1;
            this.bottomToBottom = -1;
            this.baselineToBaseline = -1;
            this.circleConstraint = -1;
            this.circleRadius = 0;
            this.circleAngle = 0.0f;
            this.startToEnd = -1;
            this.startToStart = -1;
            this.endToStart = -1;
            this.endToEnd = -1;
            this.goneLeftMargin = -1;
            this.goneTopMargin = -1;
            this.goneRightMargin = -1;
            this.goneBottomMargin = -1;
            this.goneStartMargin = -1;
            this.goneEndMargin = -1;
            this.horizontalBias = 0.5f;
            this.verticalBias = 0.5f;
            this.dimensionRatio = null;
            this.dimensionRatioValue = 0.0f;
            this.dimensionRatioSide = 1;
            this.horizontalWeight = -1.0f;
            this.verticalWeight = -1.0f;
            this.horizontalChainStyle = 0;
            this.verticalChainStyle = 0;
            this.matchConstraintDefaultWidth = 0;
            this.matchConstraintDefaultHeight = 0;
            this.matchConstraintMinWidth = 0;
            this.matchConstraintMinHeight = 0;
            this.matchConstraintMaxWidth = 0;
            this.matchConstraintMaxHeight = 0;
            this.matchConstraintPercentWidth = 1.0f;
            this.matchConstraintPercentHeight = 1.0f;
            this.editorAbsoluteX = -1;
            this.editorAbsoluteY = -1;
            this.orientation = -1;
            this.constrainedWidth = false;
            this.constrainedHeight = false;
            this.horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.needsBaseline = false;
            this.isGuideline = false;
            this.isHelper = false;
            this.isInPlaceholder = false;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolvedHorizontalBias = 0.5f;
            this.widget = new ConstraintWidget();
            this.helped = false;
            this.guideBegin = source.guideBegin;
            this.guideEnd = source.guideEnd;
            this.guidePercent = source.guidePercent;
            this.leftToLeft = source.leftToLeft;
            this.leftToRight = source.leftToRight;
            this.rightToLeft = source.rightToLeft;
            this.rightToRight = source.rightToRight;
            this.topToTop = source.topToTop;
            this.topToBottom = source.topToBottom;
            this.bottomToTop = source.bottomToTop;
            this.bottomToBottom = source.bottomToBottom;
            this.baselineToBaseline = source.baselineToBaseline;
            this.circleConstraint = source.circleConstraint;
            this.circleRadius = source.circleRadius;
            this.circleAngle = source.circleAngle;
            this.startToEnd = source.startToEnd;
            this.startToStart = source.startToStart;
            this.endToStart = source.endToStart;
            this.endToEnd = source.endToEnd;
            this.goneLeftMargin = source.goneLeftMargin;
            this.goneTopMargin = source.goneTopMargin;
            this.goneRightMargin = source.goneRightMargin;
            this.goneBottomMargin = source.goneBottomMargin;
            this.goneStartMargin = source.goneStartMargin;
            this.goneEndMargin = source.goneEndMargin;
            this.horizontalBias = source.horizontalBias;
            this.verticalBias = source.verticalBias;
            this.dimensionRatio = source.dimensionRatio;
            this.dimensionRatioValue = source.dimensionRatioValue;
            this.dimensionRatioSide = source.dimensionRatioSide;
            this.horizontalWeight = source.horizontalWeight;
            this.verticalWeight = source.verticalWeight;
            this.horizontalChainStyle = source.horizontalChainStyle;
            this.verticalChainStyle = source.verticalChainStyle;
            this.constrainedWidth = source.constrainedWidth;
            this.constrainedHeight = source.constrainedHeight;
            this.matchConstraintDefaultWidth = source.matchConstraintDefaultWidth;
            this.matchConstraintDefaultHeight = source.matchConstraintDefaultHeight;
            this.matchConstraintMinWidth = source.matchConstraintMinWidth;
            this.matchConstraintMaxWidth = source.matchConstraintMaxWidth;
            this.matchConstraintMinHeight = source.matchConstraintMinHeight;
            this.matchConstraintMaxHeight = source.matchConstraintMaxHeight;
            this.matchConstraintPercentWidth = source.matchConstraintPercentWidth;
            this.matchConstraintPercentHeight = source.matchConstraintPercentHeight;
            this.editorAbsoluteX = source.editorAbsoluteX;
            this.editorAbsoluteY = source.editorAbsoluteY;
            this.orientation = source.orientation;
            this.horizontalDimensionFixed = source.horizontalDimensionFixed;
            this.verticalDimensionFixed = source.verticalDimensionFixed;
            this.needsBaseline = source.needsBaseline;
            this.isGuideline = source.isGuideline;
            this.resolvedLeftToLeft = source.resolvedLeftToLeft;
            this.resolvedLeftToRight = source.resolvedLeftToRight;
            this.resolvedRightToLeft = source.resolvedRightToLeft;
            this.resolvedRightToRight = source.resolvedRightToRight;
            this.resolveGoneLeftMargin = source.resolveGoneLeftMargin;
            this.resolveGoneRightMargin = source.resolveGoneRightMargin;
            this.resolvedHorizontalBias = source.resolvedHorizontalBias;
            this.widget = source.widget;
        }

        private static class Table {
            public static final int ANDROID_ORIENTATION = 1;
            public static final int LAYOUT_CONSTRAINED_HEIGHT = 28;
            public static final int LAYOUT_CONSTRAINED_WIDTH = 27;
            public static final int LAYOUT_CONSTRAINT_BASELINE_CREATOR = 43;
            public static final int LAYOUT_CONSTRAINT_BASELINE_TO_BASELINE_OF = 16;
            public static final int LAYOUT_CONSTRAINT_BOTTOM_CREATOR = 42;
            public static final int LAYOUT_CONSTRAINT_BOTTOM_TO_BOTTOM_OF = 15;
            public static final int LAYOUT_CONSTRAINT_BOTTOM_TO_TOP_OF = 14;
            public static final int LAYOUT_CONSTRAINT_CIRCLE = 2;
            public static final int LAYOUT_CONSTRAINT_CIRCLE_ANGLE = 4;
            public static final int LAYOUT_CONSTRAINT_CIRCLE_RADIUS = 3;
            public static final int LAYOUT_CONSTRAINT_DIMENSION_RATIO = 44;
            public static final int LAYOUT_CONSTRAINT_END_TO_END_OF = 20;
            public static final int LAYOUT_CONSTRAINT_END_TO_START_OF = 19;
            public static final int LAYOUT_CONSTRAINT_GUIDE_BEGIN = 5;
            public static final int LAYOUT_CONSTRAINT_GUIDE_END = 6;
            public static final int LAYOUT_CONSTRAINT_GUIDE_PERCENT = 7;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_DEFAULT = 32;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_MAX = 37;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_MIN = 36;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_PERCENT = 38;
            public static final int LAYOUT_CONSTRAINT_HORIZONTAL_BIAS = 29;
            public static final int LAYOUT_CONSTRAINT_HORIZONTAL_CHAINSTYLE = 47;
            public static final int LAYOUT_CONSTRAINT_HORIZONTAL_WEIGHT = 45;
            public static final int LAYOUT_CONSTRAINT_LEFT_CREATOR = 39;
            public static final int LAYOUT_CONSTRAINT_LEFT_TO_LEFT_OF = 8;
            public static final int LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF = 9;
            public static final int LAYOUT_CONSTRAINT_RIGHT_CREATOR = 41;
            public static final int LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF = 10;
            public static final int LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF = 11;
            public static final int LAYOUT_CONSTRAINT_START_TO_END_OF = 17;
            public static final int LAYOUT_CONSTRAINT_START_TO_START_OF = 18;
            public static final int LAYOUT_CONSTRAINT_TOP_CREATOR = 40;
            public static final int LAYOUT_CONSTRAINT_TOP_TO_BOTTOM_OF = 13;
            public static final int LAYOUT_CONSTRAINT_TOP_TO_TOP_OF = 12;
            public static final int LAYOUT_CONSTRAINT_VERTICAL_BIAS = 30;
            public static final int LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE = 48;
            public static final int LAYOUT_CONSTRAINT_VERTICAL_WEIGHT = 46;
            public static final int LAYOUT_CONSTRAINT_WIDTH_DEFAULT = 31;
            public static final int LAYOUT_CONSTRAINT_WIDTH_MAX = 34;
            public static final int LAYOUT_CONSTRAINT_WIDTH_MIN = 33;
            public static final int LAYOUT_CONSTRAINT_WIDTH_PERCENT = 35;
            public static final int LAYOUT_EDITOR_ABSOLUTEX = 49;
            public static final int LAYOUT_EDITOR_ABSOLUTEY = 50;
            public static final int LAYOUT_GONE_MARGIN_BOTTOM = 24;
            public static final int LAYOUT_GONE_MARGIN_END = 26;
            public static final int LAYOUT_GONE_MARGIN_LEFT = 21;
            public static final int LAYOUT_GONE_MARGIN_RIGHT = 23;
            public static final int LAYOUT_GONE_MARGIN_START = 25;
            public static final int LAYOUT_GONE_MARGIN_TOP = 22;
            public static final int UNUSED = 0;
            public static final SparseIntArray map = new SparseIntArray();

            private Table() {
            }

            static {
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toLeftOf, 8);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toRightOf, 9);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_toLeftOf, 10);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_toRightOf, 11);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_toTopOf, 12);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_toBottomOf, 13);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toTopOf, 14);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toBottomOf, 15);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_toBaselineOf, 16);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircle, 2);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircleRadius, 3);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircleAngle, 4);
                map.append(R.styleable.ConstraintLayout_Layout_layout_editor_absoluteX, 49);
                map.append(R.styleable.ConstraintLayout_Layout_layout_editor_absoluteY, 50);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_begin, 5);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_end, 6);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_percent, 7);
                map.append(R.styleable.ConstraintLayout_Layout_android_orientation, 1);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintStart_toEndOf, 17);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintStart_toStartOf, 18);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toStartOf, 19);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toEndOf, 20);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginLeft, 21);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginTop, 22);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginRight, 23);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginBottom, 24);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginStart, 25);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginEnd, 26);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_bias, 29);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_bias, 30);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintDimensionRatio, 44);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_weight, 45);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_weight, 46);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_chainStyle, 47);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_chainStyle, 48);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constrainedWidth, 27);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constrainedHeight, 28);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_default, 31);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_default, 32);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_min, 33);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_max, 34);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_percent, 35);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_min, 36);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_max, 37);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_percent, 38);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_creator, 39);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_creator, 40);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_creator, 41);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_creator, 42);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_creator, 43);
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LayoutParams(android.content.Context r20, android.util.AttributeSet r21) {
            /*
                r19 = this;
                r1 = r19
                r19.<init>(r20, r21)
                r2 = -1
                r1.guideBegin = r2
                r1.guideEnd = r2
                r3 = -1082130432(0xffffffffbf800000, float:-1.0)
                r1.guidePercent = r3
                r1.leftToLeft = r2
                r1.leftToRight = r2
                r1.rightToLeft = r2
                r1.rightToRight = r2
                r1.topToTop = r2
                r1.topToBottom = r2
                r1.bottomToTop = r2
                r1.bottomToBottom = r2
                r1.baselineToBaseline = r2
                r1.circleConstraint = r2
                r4 = 0
                r1.circleRadius = r4
                r5 = 0
                r1.circleAngle = r5
                r1.startToEnd = r2
                r1.startToStart = r2
                r1.endToStart = r2
                r1.endToEnd = r2
                r1.goneLeftMargin = r2
                r1.goneTopMargin = r2
                r1.goneRightMargin = r2
                r1.goneBottomMargin = r2
                r1.goneStartMargin = r2
                r1.goneEndMargin = r2
                r6 = 1056964608(0x3f000000, float:0.5)
                r1.horizontalBias = r6
                r1.verticalBias = r6
                r7 = 0
                r1.dimensionRatio = r7
                r1.dimensionRatioValue = r5
                r7 = 1
                r1.dimensionRatioSide = r7
                r1.horizontalWeight = r3
                r1.verticalWeight = r3
                r1.horizontalChainStyle = r4
                r1.verticalChainStyle = r4
                r1.matchConstraintDefaultWidth = r4
                r1.matchConstraintDefaultHeight = r4
                r1.matchConstraintMinWidth = r4
                r1.matchConstraintMinHeight = r4
                r1.matchConstraintMaxWidth = r4
                r1.matchConstraintMaxHeight = r4
                r3 = 1065353216(0x3f800000, float:1.0)
                r1.matchConstraintPercentWidth = r3
                r1.matchConstraintPercentHeight = r3
                r1.editorAbsoluteX = r2
                r1.editorAbsoluteY = r2
                r1.orientation = r2
                r1.constrainedWidth = r4
                r1.constrainedHeight = r4
                r1.horizontalDimensionFixed = r7
                r1.verticalDimensionFixed = r7
                r1.needsBaseline = r4
                r1.isGuideline = r4
                r1.isHelper = r4
                r1.isInPlaceholder = r4
                r1.resolvedLeftToLeft = r2
                r1.resolvedLeftToRight = r2
                r1.resolvedRightToLeft = r2
                r1.resolvedRightToRight = r2
                r1.resolveGoneLeftMargin = r2
                r1.resolveGoneRightMargin = r2
                r1.resolvedHorizontalBias = r6
                android.support.constraint.solver.widgets.ConstraintWidget r3 = new android.support.constraint.solver.widgets.ConstraintWidget
                r3.<init>()
                r1.widget = r3
                r1.helped = r4
                int[] r3 = android.support.constraint.R.styleable.ConstraintLayout_Layout
                r6 = r20
                r8 = r21
                android.content.res.TypedArray r3 = r6.obtainStyledAttributes(r8, r3)
                int r9 = r3.getIndexCount()
                r10 = 0
            L_0x00a0:
                if (r10 >= r9) goto L_0x0471
                int r11 = r3.getIndex(r10)
                android.util.SparseIntArray r12 = android.support.constraint.ConstraintLayout.LayoutParams.Table.map
                int r12 = r12.get(r11)
                r13 = -2
                switch(r12) {
                    case 0: goto L_0x0465;
                    case 1: goto L_0x0458;
                    case 2: goto L_0x0441;
                    case 3: goto L_0x0433;
                    case 4: goto L_0x0417;
                    case 5: goto L_0x040b;
                    case 6: goto L_0x03ff;
                    case 7: goto L_0x03f3;
                    case 8: goto L_0x03dc;
                    case 9: goto L_0x03c5;
                    case 10: goto L_0x03ae;
                    case 11: goto L_0x0397;
                    case 12: goto L_0x0380;
                    case 13: goto L_0x0369;
                    case 14: goto L_0x0352;
                    case 15: goto L_0x033b;
                    case 16: goto L_0x0324;
                    case 17: goto L_0x030d;
                    case 18: goto L_0x02f6;
                    case 19: goto L_0x02df;
                    case 20: goto L_0x02c8;
                    case 21: goto L_0x02bc;
                    case 22: goto L_0x02b0;
                    case 23: goto L_0x02a4;
                    case 24: goto L_0x0298;
                    case 25: goto L_0x028c;
                    case 26: goto L_0x0280;
                    case 27: goto L_0x0274;
                    case 28: goto L_0x0268;
                    case 29: goto L_0x025c;
                    case 30: goto L_0x0250;
                    case 31: goto L_0x023b;
                    case 32: goto L_0x0226;
                    case 33: goto L_0x020e;
                    case 34: goto L_0x01f6;
                    case 35: goto L_0x01e7;
                    case 36: goto L_0x01cf;
                    case 37: goto L_0x01b7;
                    case 38: goto L_0x01a8;
                    case 39: goto L_0x01a6;
                    case 40: goto L_0x01a4;
                    case 41: goto L_0x01a2;
                    case 42: goto L_0x01a0;
                    case 43: goto L_0x00b0;
                    case 44: goto L_0x00e8;
                    case 45: goto L_0x00df;
                    case 46: goto L_0x00d6;
                    case 47: goto L_0x00cf;
                    case 48: goto L_0x00c8;
                    case 49: goto L_0x00bf;
                    case 50: goto L_0x00b6;
                    default: goto L_0x00b0;
                }
            L_0x00b0:
                r2 = 0
                r5 = 1
            L_0x00b2:
                r7 = -1
            L_0x00b3:
                r13 = 0
                goto L_0x0469
            L_0x00b6:
                int r13 = r1.editorAbsoluteY
                int r13 = r3.getDimensionPixelOffset(r11, r13)
                r1.editorAbsoluteY = r13
                goto L_0x00b0
            L_0x00bf:
                int r13 = r1.editorAbsoluteX
                int r13 = r3.getDimensionPixelOffset(r11, r13)
                r1.editorAbsoluteX = r13
                goto L_0x00b0
            L_0x00c8:
                int r13 = r3.getInt(r11, r4)
                r1.verticalChainStyle = r13
                goto L_0x00b0
            L_0x00cf:
                int r13 = r3.getInt(r11, r4)
                r1.horizontalChainStyle = r13
                goto L_0x00b0
            L_0x00d6:
                float r13 = r1.verticalWeight
                float r13 = r3.getFloat(r11, r13)
                r1.verticalWeight = r13
                goto L_0x00b0
            L_0x00df:
                float r13 = r1.horizontalWeight
                float r13 = r3.getFloat(r11, r13)
                r1.horizontalWeight = r13
                goto L_0x00b0
            L_0x00e8:
                java.lang.String r13 = r3.getString(r11)
                r1.dimensionRatio = r13
                r13 = 2143289344(0x7fc00000, float:NaN)
                r1.dimensionRatioValue = r13
                r1.dimensionRatioSide = r2
                java.lang.String r13 = r1.dimensionRatio
                if (r13 == 0) goto L_0x00b0
                java.lang.String r13 = r1.dimensionRatio
                int r13 = r13.length()
                java.lang.String r14 = r1.dimensionRatio
                r15 = 44
                int r14 = r14.indexOf(r15)
                if (r14 <= 0) goto L_0x012a
                int r15 = r13 + -1
                if (r14 >= r15) goto L_0x012a
                java.lang.String r15 = r1.dimensionRatio
                java.lang.String r15 = r15.substring(r4, r14)
                java.lang.String r2 = "W"
                boolean r2 = r15.equalsIgnoreCase(r2)
                if (r2 == 0) goto L_0x011d
                r1.dimensionRatioSide = r4
                goto L_0x0127
            L_0x011d:
                java.lang.String r2 = "H"
                boolean r2 = r15.equalsIgnoreCase(r2)
                if (r2 == 0) goto L_0x0127
                r1.dimensionRatioSide = r7
            L_0x0127:
                int r14 = r14 + 1
                goto L_0x012b
            L_0x012a:
                r14 = 0
            L_0x012b:
                java.lang.String r2 = r1.dimensionRatio
                r15 = 58
                int r2 = r2.indexOf(r15)
                if (r2 < 0) goto L_0x0188
                int r15 = r13 + -1
                if (r2 >= r15) goto L_0x0188
                java.lang.String r15 = r1.dimensionRatio
                java.lang.String r15 = r15.substring(r14, r2)
                java.lang.String r4 = r1.dimensionRatio
                int r7 = r2 + 1
                java.lang.String r4 = r4.substring(r7)
                int r7 = r15.length()
                if (r7 <= 0) goto L_0x0185
                int r7 = r4.length()
                if (r7 <= 0) goto L_0x0185
                float r7 = java.lang.Float.parseFloat(r15)     // Catch:{ NumberFormatException -> 0x0181 }
                float r16 = java.lang.Float.parseFloat(r4)     // Catch:{ NumberFormatException -> 0x0181 }
                int r17 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
                if (r17 <= 0) goto L_0x017e
                int r17 = (r16 > r5 ? 1 : (r16 == r5 ? 0 : -1))
                if (r17 <= 0) goto L_0x017e
                int r5 = r1.dimensionRatioSide     // Catch:{ NumberFormatException -> 0x0181 }
                r18 = r2
                r2 = 1
                if (r5 != r2) goto L_0x0175
                float r2 = r16 / r7
                float r2 = java.lang.Math.abs(r2)     // Catch:{ NumberFormatException -> 0x0173 }
                r1.dimensionRatioValue = r2     // Catch:{ NumberFormatException -> 0x0173 }
                goto L_0x0180
            L_0x0173:
                r0 = move-exception
                goto L_0x0184
            L_0x0175:
                float r2 = r7 / r16
                float r2 = java.lang.Math.abs(r2)     // Catch:{ NumberFormatException -> 0x0173 }
                r1.dimensionRatioValue = r2     // Catch:{ NumberFormatException -> 0x0173 }
                goto L_0x0180
            L_0x017e:
                r18 = r2
            L_0x0180:
                goto L_0x0187
            L_0x0181:
                r0 = move-exception
                r18 = r2
            L_0x0184:
                goto L_0x0187
            L_0x0185:
                r18 = r2
            L_0x0187:
                goto L_0x019e
            L_0x0188:
                r18 = r2
                java.lang.String r2 = r1.dimensionRatio
                java.lang.String r2 = r2.substring(r14)
                int r4 = r2.length()
                if (r4 <= 0) goto L_0x019e
                float r4 = java.lang.Float.parseFloat(r2)     // Catch:{ NumberFormatException -> 0x019d }
                r1.dimensionRatioValue = r4     // Catch:{ NumberFormatException -> 0x019d }
                goto L_0x019e
            L_0x019d:
                r0 = move-exception
            L_0x019e:
                goto L_0x00b0
            L_0x01a0:
                goto L_0x00b0
            L_0x01a2:
                goto L_0x00b0
            L_0x01a4:
                goto L_0x00b0
            L_0x01a6:
                goto L_0x00b0
            L_0x01a8:
                float r2 = r1.matchConstraintPercentHeight
                float r2 = r3.getFloat(r11, r2)
                r4 = 0
                float r2 = java.lang.Math.max(r4, r2)
                r1.matchConstraintPercentHeight = r2
                goto L_0x00b0
            L_0x01b7:
                int r2 = r1.matchConstraintMaxHeight     // Catch:{ Exception -> 0x01c1 }
                int r2 = r3.getDimensionPixelSize(r11, r2)     // Catch:{ Exception -> 0x01c1 }
                r1.matchConstraintMaxHeight = r2     // Catch:{ Exception -> 0x01c1 }
                goto L_0x00b0
            L_0x01c1:
                r0 = move-exception
                r2 = r0
                int r4 = r1.matchConstraintMaxHeight
                int r4 = r3.getInt(r11, r4)
                if (r4 != r13) goto L_0x01cd
                r1.matchConstraintMaxHeight = r13
            L_0x01cd:
                goto L_0x00b0
            L_0x01cf:
                int r2 = r1.matchConstraintMinHeight     // Catch:{ Exception -> 0x01d9 }
                int r2 = r3.getDimensionPixelSize(r11, r2)     // Catch:{ Exception -> 0x01d9 }
                r1.matchConstraintMinHeight = r2     // Catch:{ Exception -> 0x01d9 }
                goto L_0x00b0
            L_0x01d9:
                r0 = move-exception
                r2 = r0
                int r4 = r1.matchConstraintMinHeight
                int r4 = r3.getInt(r11, r4)
                if (r4 != r13) goto L_0x01e5
                r1.matchConstraintMinHeight = r13
            L_0x01e5:
                goto L_0x00b0
            L_0x01e7:
                float r2 = r1.matchConstraintPercentWidth
                float r2 = r3.getFloat(r11, r2)
                r4 = 0
                float r2 = java.lang.Math.max(r4, r2)
                r1.matchConstraintPercentWidth = r2
                goto L_0x00b0
            L_0x01f6:
                int r2 = r1.matchConstraintMaxWidth     // Catch:{ Exception -> 0x0200 }
                int r2 = r3.getDimensionPixelSize(r11, r2)     // Catch:{ Exception -> 0x0200 }
                r1.matchConstraintMaxWidth = r2     // Catch:{ Exception -> 0x0200 }
                goto L_0x00b0
            L_0x0200:
                r0 = move-exception
                r2 = r0
                int r4 = r1.matchConstraintMaxWidth
                int r4 = r3.getInt(r11, r4)
                if (r4 != r13) goto L_0x020c
                r1.matchConstraintMaxWidth = r13
            L_0x020c:
                goto L_0x00b0
            L_0x020e:
                int r2 = r1.matchConstraintMinWidth     // Catch:{ Exception -> 0x0218 }
                int r2 = r3.getDimensionPixelSize(r11, r2)     // Catch:{ Exception -> 0x0218 }
                r1.matchConstraintMinWidth = r2     // Catch:{ Exception -> 0x0218 }
                goto L_0x00b0
            L_0x0218:
                r0 = move-exception
                r2 = r0
                int r4 = r1.matchConstraintMinWidth
                int r4 = r3.getInt(r11, r4)
                if (r4 != r13) goto L_0x0224
                r1.matchConstraintMinWidth = r13
            L_0x0224:
                goto L_0x00b0
            L_0x0226:
                r2 = 0
                int r4 = r3.getInt(r11, r2)
                r1.matchConstraintDefaultHeight = r4
                int r2 = r1.matchConstraintDefaultHeight
                r4 = 1
                if (r2 != r4) goto L_0x00b0
                java.lang.String r2 = "ConstraintLayout"
                java.lang.String r4 = "layout_constraintHeight_default=\"wrap\" is deprecated.\nUse layout_height=\"WRAP_CONTENT\" and layout_constrainedHeight=\"true\" instead."
                android.util.Log.e(r2, r4)
                goto L_0x00b0
            L_0x023b:
                r2 = 0
                int r4 = r3.getInt(r11, r2)
                r1.matchConstraintDefaultWidth = r4
                int r4 = r1.matchConstraintDefaultWidth
                r5 = 1
                if (r4 != r5) goto L_0x00b2
                java.lang.String r4 = "ConstraintLayout"
                java.lang.String r7 = "layout_constraintWidth_default=\"wrap\" is deprecated.\nUse layout_width=\"WRAP_CONTENT\" and layout_constrainedWidth=\"true\" instead."
                android.util.Log.e(r4, r7)
                goto L_0x00b2
            L_0x0250:
                r2 = 0
                r5 = 1
                float r4 = r1.verticalBias
                float r4 = r3.getFloat(r11, r4)
                r1.verticalBias = r4
                goto L_0x00b2
            L_0x025c:
                r2 = 0
                r5 = 1
                float r4 = r1.horizontalBias
                float r4 = r3.getFloat(r11, r4)
                r1.horizontalBias = r4
                goto L_0x00b2
            L_0x0268:
                r2 = 0
                r5 = 1
                boolean r4 = r1.constrainedHeight
                boolean r4 = r3.getBoolean(r11, r4)
                r1.constrainedHeight = r4
                goto L_0x00b2
            L_0x0274:
                r2 = 0
                r5 = 1
                boolean r4 = r1.constrainedWidth
                boolean r4 = r3.getBoolean(r11, r4)
                r1.constrainedWidth = r4
                goto L_0x00b2
            L_0x0280:
                r2 = 0
                r5 = 1
                int r4 = r1.goneEndMargin
                int r4 = r3.getDimensionPixelSize(r11, r4)
                r1.goneEndMargin = r4
                goto L_0x00b2
            L_0x028c:
                r2 = 0
                r5 = 1
                int r4 = r1.goneStartMargin
                int r4 = r3.getDimensionPixelSize(r11, r4)
                r1.goneStartMargin = r4
                goto L_0x00b2
            L_0x0298:
                r2 = 0
                r5 = 1
                int r4 = r1.goneBottomMargin
                int r4 = r3.getDimensionPixelSize(r11, r4)
                r1.goneBottomMargin = r4
                goto L_0x00b2
            L_0x02a4:
                r2 = 0
                r5 = 1
                int r4 = r1.goneRightMargin
                int r4 = r3.getDimensionPixelSize(r11, r4)
                r1.goneRightMargin = r4
                goto L_0x00b2
            L_0x02b0:
                r2 = 0
                r5 = 1
                int r4 = r1.goneTopMargin
                int r4 = r3.getDimensionPixelSize(r11, r4)
                r1.goneTopMargin = r4
                goto L_0x00b2
            L_0x02bc:
                r2 = 0
                r5 = 1
                int r4 = r1.goneLeftMargin
                int r4 = r3.getDimensionPixelSize(r11, r4)
                r1.goneLeftMargin = r4
                goto L_0x00b2
            L_0x02c8:
                r2 = 0
                r5 = 1
                int r4 = r1.endToEnd
                int r4 = r3.getResourceId(r11, r4)
                r1.endToEnd = r4
                int r4 = r1.endToEnd
                r7 = -1
                if (r4 != r7) goto L_0x00b3
                int r4 = r3.getInt(r11, r7)
                r1.endToEnd = r4
                goto L_0x00b3
            L_0x02df:
                r2 = 0
                r5 = 1
                r7 = -1
                int r4 = r1.endToStart
                int r4 = r3.getResourceId(r11, r4)
                r1.endToStart = r4
                int r4 = r1.endToStart
                if (r4 != r7) goto L_0x00b3
                int r4 = r3.getInt(r11, r7)
                r1.endToStart = r4
                goto L_0x00b3
            L_0x02f6:
                r2 = 0
                r5 = 1
                r7 = -1
                int r4 = r1.startToStart
                int r4 = r3.getResourceId(r11, r4)
                r1.startToStart = r4
                int r4 = r1.startToStart
                if (r4 != r7) goto L_0x00b3
                int r4 = r3.getInt(r11, r7)
                r1.startToStart = r4
                goto L_0x00b3
            L_0x030d:
                r2 = 0
                r5 = 1
                r7 = -1
                int r4 = r1.startToEnd
                int r4 = r3.getResourceId(r11, r4)
                r1.startToEnd = r4
                int r4 = r1.startToEnd
                if (r4 != r7) goto L_0x00b3
                int r4 = r3.getInt(r11, r7)
                r1.startToEnd = r4
                goto L_0x00b3
            L_0x0324:
                r2 = 0
                r5 = 1
                r7 = -1
                int r4 = r1.baselineToBaseline
                int r4 = r3.getResourceId(r11, r4)
                r1.baselineToBaseline = r4
                int r4 = r1.baselineToBaseline
                if (r4 != r7) goto L_0x00b3
                int r4 = r3.getInt(r11, r7)
                r1.baselineToBaseline = r4
                goto L_0x00b3
            L_0x033b:
                r2 = 0
                r5 = 1
                r7 = -1
                int r4 = r1.bottomToBottom
                int r4 = r3.getResourceId(r11, r4)
                r1.bottomToBottom = r4
                int r4 = r1.bottomToBottom
                if (r4 != r7) goto L_0x00b3
                int r4 = r3.getInt(r11, r7)
                r1.bottomToBottom = r4
                goto L_0x00b3
            L_0x0352:
                r2 = 0
                r5 = 1
                r7 = -1
                int r4 = r1.bottomToTop
                int r4 = r3.getResourceId(r11, r4)
                r1.bottomToTop = r4
                int r4 = r1.bottomToTop
                if (r4 != r7) goto L_0x00b3
                int r4 = r3.getInt(r11, r7)
                r1.bottomToTop = r4
                goto L_0x00b3
            L_0x0369:
                r2 = 0
                r5 = 1
                r7 = -1
                int r4 = r1.topToBottom
                int r4 = r3.getResourceId(r11, r4)
                r1.topToBottom = r4
                int r4 = r1.topToBottom
                if (r4 != r7) goto L_0x00b3
                int r4 = r3.getInt(r11, r7)
                r1.topToBottom = r4
                goto L_0x00b3
            L_0x0380:
                r2 = 0
                r5 = 1
                r7 = -1
                int r4 = r1.topToTop
                int r4 = r3.getResourceId(r11, r4)
                r1.topToTop = r4
                int r4 = r1.topToTop
                if (r4 != r7) goto L_0x00b3
                int r4 = r3.getInt(r11, r7)
                r1.topToTop = r4
                goto L_0x00b3
            L_0x0397:
                r2 = 0
                r5 = 1
                r7 = -1
                int r4 = r1.rightToRight
                int r4 = r3.getResourceId(r11, r4)
                r1.rightToRight = r4
                int r4 = r1.rightToRight
                if (r4 != r7) goto L_0x00b3
                int r4 = r3.getInt(r11, r7)
                r1.rightToRight = r4
                goto L_0x00b3
            L_0x03ae:
                r2 = 0
                r5 = 1
                r7 = -1
                int r4 = r1.rightToLeft
                int r4 = r3.getResourceId(r11, r4)
                r1.rightToLeft = r4
                int r4 = r1.rightToLeft
                if (r4 != r7) goto L_0x00b3
                int r4 = r3.getInt(r11, r7)
                r1.rightToLeft = r4
                goto L_0x00b3
            L_0x03c5:
                r2 = 0
                r5 = 1
                r7 = -1
                int r4 = r1.leftToRight
                int r4 = r3.getResourceId(r11, r4)
                r1.leftToRight = r4
                int r4 = r1.leftToRight
                if (r4 != r7) goto L_0x00b3
                int r4 = r3.getInt(r11, r7)
                r1.leftToRight = r4
                goto L_0x00b3
            L_0x03dc:
                r2 = 0
                r5 = 1
                r7 = -1
                int r4 = r1.leftToLeft
                int r4 = r3.getResourceId(r11, r4)
                r1.leftToLeft = r4
                int r4 = r1.leftToLeft
                if (r4 != r7) goto L_0x00b3
                int r4 = r3.getInt(r11, r7)
                r1.leftToLeft = r4
                goto L_0x00b2
            L_0x03f3:
                r2 = 0
                r5 = 1
                float r4 = r1.guidePercent
                float r4 = r3.getFloat(r11, r4)
                r1.guidePercent = r4
                goto L_0x00b2
            L_0x03ff:
                r2 = 0
                r5 = 1
                int r4 = r1.guideEnd
                int r4 = r3.getDimensionPixelOffset(r11, r4)
                r1.guideEnd = r4
                goto L_0x00b2
            L_0x040b:
                r2 = 0
                r5 = 1
                int r4 = r1.guideBegin
                int r4 = r3.getDimensionPixelOffset(r11, r4)
                r1.guideBegin = r4
                goto L_0x00b2
            L_0x0417:
                r2 = 0
                r5 = 1
                float r4 = r1.circleAngle
                float r4 = r3.getFloat(r11, r4)
                r7 = 1135869952(0x43b40000, float:360.0)
                float r4 = r4 % r7
                r1.circleAngle = r4
                float r4 = r1.circleAngle
                r13 = 0
                int r4 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
                if (r4 >= 0) goto L_0x043f
                float r4 = r1.circleAngle
                float r4 = r7 - r4
                float r4 = r4 % r7
                r1.circleAngle = r4
                goto L_0x043f
            L_0x0433:
                r2 = 0
                r5 = 1
                r13 = 0
                int r4 = r1.circleRadius
                int r4 = r3.getDimensionPixelSize(r11, r4)
                r1.circleRadius = r4
            L_0x043f:
                r7 = -1
                goto L_0x0469
            L_0x0441:
                r2 = 0
                r5 = 1
                r13 = 0
                int r4 = r1.circleConstraint
                int r4 = r3.getResourceId(r11, r4)
                r1.circleConstraint = r4
                int r4 = r1.circleConstraint
                r7 = -1
                if (r4 != r7) goto L_0x0469
                int r4 = r3.getInt(r11, r7)
                r1.circleConstraint = r4
                goto L_0x0469
            L_0x0458:
                r2 = 0
                r5 = 1
                r7 = -1
                r13 = 0
                int r4 = r1.orientation
                int r4 = r3.getInt(r11, r4)
                r1.orientation = r4
                goto L_0x0469
            L_0x0465:
                r2 = 0
                r5 = 1
                r7 = -1
                r13 = 0
            L_0x0469:
                int r10 = r10 + 1
                r2 = -1
                r4 = 0
                r5 = 0
                r7 = 1
                goto L_0x00a0
            L_0x0471:
                r3.recycle()
                r19.validate()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.ConstraintLayout.LayoutParams.<init>(android.content.Context, android.util.AttributeSet):void");
        }

        public void validate() {
            this.isGuideline = false;
            this.horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            if (this.width == -2 && this.constrainedWidth) {
                this.horizontalDimensionFixed = false;
                this.matchConstraintDefaultWidth = 1;
            }
            if (this.height == -2 && this.constrainedHeight) {
                this.verticalDimensionFixed = false;
                this.matchConstraintDefaultHeight = 1;
            }
            if (this.width == 0 || this.width == -1) {
                this.horizontalDimensionFixed = false;
                if (this.width == 0 && this.matchConstraintDefaultWidth == 1) {
                    this.width = -2;
                    this.constrainedWidth = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
            }
            if (this.height == 0 || this.height == -1) {
                this.verticalDimensionFixed = false;
                if (this.height == 0 && this.matchConstraintDefaultHeight == 1) {
                    this.height = -2;
                    this.constrainedHeight = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
            }
            if (this.guidePercent != -1.0f || this.guideBegin != -1 || this.guideEnd != -1) {
                this.isGuideline = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                this.horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                this.verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                if (!(this.widget instanceof Guideline)) {
                    this.widget = new Guideline();
                }
                ((Guideline) this.widget).setOrientation(this.orientation);
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.guideBegin = -1;
            this.guideEnd = -1;
            this.guidePercent = -1.0f;
            this.leftToLeft = -1;
            this.leftToRight = -1;
            this.rightToLeft = -1;
            this.rightToRight = -1;
            this.topToTop = -1;
            this.topToBottom = -1;
            this.bottomToTop = -1;
            this.bottomToBottom = -1;
            this.baselineToBaseline = -1;
            this.circleConstraint = -1;
            this.circleRadius = 0;
            this.circleAngle = 0.0f;
            this.startToEnd = -1;
            this.startToStart = -1;
            this.endToStart = -1;
            this.endToEnd = -1;
            this.goneLeftMargin = -1;
            this.goneTopMargin = -1;
            this.goneRightMargin = -1;
            this.goneBottomMargin = -1;
            this.goneStartMargin = -1;
            this.goneEndMargin = -1;
            this.horizontalBias = 0.5f;
            this.verticalBias = 0.5f;
            this.dimensionRatio = null;
            this.dimensionRatioValue = 0.0f;
            this.dimensionRatioSide = 1;
            this.horizontalWeight = -1.0f;
            this.verticalWeight = -1.0f;
            this.horizontalChainStyle = 0;
            this.verticalChainStyle = 0;
            this.matchConstraintDefaultWidth = 0;
            this.matchConstraintDefaultHeight = 0;
            this.matchConstraintMinWidth = 0;
            this.matchConstraintMinHeight = 0;
            this.matchConstraintMaxWidth = 0;
            this.matchConstraintMaxHeight = 0;
            this.matchConstraintPercentWidth = 1.0f;
            this.matchConstraintPercentHeight = 1.0f;
            this.editorAbsoluteX = -1;
            this.editorAbsoluteY = -1;
            this.orientation = -1;
            this.constrainedWidth = false;
            this.constrainedHeight = false;
            this.horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.needsBaseline = false;
            this.isGuideline = false;
            this.isHelper = false;
            this.isInPlaceholder = false;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolvedHorizontalBias = 0.5f;
            this.widget = new ConstraintWidget();
            this.helped = false;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.guideBegin = -1;
            this.guideEnd = -1;
            this.guidePercent = -1.0f;
            this.leftToLeft = -1;
            this.leftToRight = -1;
            this.rightToLeft = -1;
            this.rightToRight = -1;
            this.topToTop = -1;
            this.topToBottom = -1;
            this.bottomToTop = -1;
            this.bottomToBottom = -1;
            this.baselineToBaseline = -1;
            this.circleConstraint = -1;
            this.circleRadius = 0;
            this.circleAngle = 0.0f;
            this.startToEnd = -1;
            this.startToStart = -1;
            this.endToStart = -1;
            this.endToEnd = -1;
            this.goneLeftMargin = -1;
            this.goneTopMargin = -1;
            this.goneRightMargin = -1;
            this.goneBottomMargin = -1;
            this.goneStartMargin = -1;
            this.goneEndMargin = -1;
            this.horizontalBias = 0.5f;
            this.verticalBias = 0.5f;
            this.dimensionRatio = null;
            this.dimensionRatioValue = 0.0f;
            this.dimensionRatioSide = 1;
            this.horizontalWeight = -1.0f;
            this.verticalWeight = -1.0f;
            this.horizontalChainStyle = 0;
            this.verticalChainStyle = 0;
            this.matchConstraintDefaultWidth = 0;
            this.matchConstraintDefaultHeight = 0;
            this.matchConstraintMinWidth = 0;
            this.matchConstraintMinHeight = 0;
            this.matchConstraintMaxWidth = 0;
            this.matchConstraintMaxHeight = 0;
            this.matchConstraintPercentWidth = 1.0f;
            this.matchConstraintPercentHeight = 1.0f;
            this.editorAbsoluteX = -1;
            this.editorAbsoluteY = -1;
            this.orientation = -1;
            this.constrainedWidth = false;
            this.constrainedHeight = false;
            this.horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.needsBaseline = false;
            this.isGuideline = false;
            this.isHelper = false;
            this.isInPlaceholder = false;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolvedHorizontalBias = 0.5f;
            this.widget = new ConstraintWidget();
            this.helped = false;
        }

        @TargetApi(17)
        public void resolveLayoutDirection(int layoutDirection) {
            int preLeftMargin = this.leftMargin;
            int preRightMargin = this.rightMargin;
            super.resolveLayoutDirection(layoutDirection);
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolveGoneLeftMargin = this.goneLeftMargin;
            this.resolveGoneRightMargin = this.goneRightMargin;
            this.resolvedHorizontalBias = this.horizontalBias;
            this.resolvedGuideBegin = this.guideBegin;
            this.resolvedGuideEnd = this.guideEnd;
            this.resolvedGuidePercent = this.guidePercent;
            if (1 == getLayoutDirection() ? ConstraintLayout.USE_CONSTRAINTS_HELPER : false) {
                boolean startEndDefined = false;
                if (this.startToEnd != -1) {
                    this.resolvedRightToLeft = this.startToEnd;
                    startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                } else if (this.startToStart != -1) {
                    this.resolvedRightToRight = this.startToStart;
                    startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
                if (this.endToStart != -1) {
                    this.resolvedLeftToRight = this.endToStart;
                    startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
                if (this.endToEnd != -1) {
                    this.resolvedLeftToLeft = this.endToEnd;
                    startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
                if (this.goneStartMargin != -1) {
                    this.resolveGoneRightMargin = this.goneStartMargin;
                }
                if (this.goneEndMargin != -1) {
                    this.resolveGoneLeftMargin = this.goneEndMargin;
                }
                if (startEndDefined) {
                    this.resolvedHorizontalBias = 1.0f - this.horizontalBias;
                }
                if (this.isGuideline && this.orientation == 1) {
                    if (this.guidePercent != -1.0f) {
                        this.resolvedGuidePercent = 1.0f - this.guidePercent;
                        this.resolvedGuideBegin = -1;
                        this.resolvedGuideEnd = -1;
                    } else if (this.guideBegin != -1) {
                        this.resolvedGuideEnd = this.guideBegin;
                        this.resolvedGuideBegin = -1;
                        this.resolvedGuidePercent = -1.0f;
                    } else if (this.guideEnd != -1) {
                        this.resolvedGuideBegin = this.guideEnd;
                        this.resolvedGuideEnd = -1;
                        this.resolvedGuidePercent = -1.0f;
                    }
                }
            } else {
                if (this.startToEnd != -1) {
                    this.resolvedLeftToRight = this.startToEnd;
                }
                if (this.startToStart != -1) {
                    this.resolvedLeftToLeft = this.startToStart;
                }
                if (this.endToStart != -1) {
                    this.resolvedRightToLeft = this.endToStart;
                }
                if (this.endToEnd != -1) {
                    this.resolvedRightToRight = this.endToEnd;
                }
                if (this.goneStartMargin != -1) {
                    this.resolveGoneLeftMargin = this.goneStartMargin;
                }
                if (this.goneEndMargin != -1) {
                    this.resolveGoneRightMargin = this.goneEndMargin;
                }
            }
            if (this.endToStart == -1 && this.endToEnd == -1 && this.startToStart == -1 && this.startToEnd == -1) {
                if (this.rightToLeft != -1) {
                    this.resolvedRightToLeft = this.rightToLeft;
                    if (this.rightMargin <= 0 && preRightMargin > 0) {
                        this.rightMargin = preRightMargin;
                    }
                } else if (this.rightToRight != -1) {
                    this.resolvedRightToRight = this.rightToRight;
                    if (this.rightMargin <= 0 && preRightMargin > 0) {
                        this.rightMargin = preRightMargin;
                    }
                }
                if (this.leftToLeft != -1) {
                    this.resolvedLeftToLeft = this.leftToLeft;
                    if (this.leftMargin <= 0 && preLeftMargin > 0) {
                        this.leftMargin = preLeftMargin;
                    }
                } else if (this.leftToRight != -1) {
                    this.resolvedLeftToRight = this.leftToRight;
                    if (this.leftMargin <= 0 && preLeftMargin > 0) {
                        this.leftMargin = preLeftMargin;
                    }
                }
            }
        }
    }

    public void requestLayout() {
        super.requestLayout();
        this.mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
        this.mLastMeasureWidth = -1;
        this.mLastMeasureHeight = -1;
        this.mLastMeasureWidthSize = -1;
        this.mLastMeasureHeightSize = -1;
        this.mLastMeasureWidthMode = 0;
        this.mLastMeasureHeightMode = 0;
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }
}
