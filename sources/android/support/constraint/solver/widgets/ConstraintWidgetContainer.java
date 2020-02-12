package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.constraint.solver.widgets.ConstraintWidget;
import java.util.ArrayList;
import java.util.Arrays;

public class ConstraintWidgetContainer extends WidgetContainer {
    static boolean ALLOW_ROOT_GROUP = USE_SNAPSHOT;
    private static final int CHAIN_FIRST = 0;
    private static final int CHAIN_FIRST_VISIBLE = 2;
    private static final int CHAIN_LAST = 1;
    private static final int CHAIN_LAST_VISIBLE = 3;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_LAYOUT = false;
    private static final boolean DEBUG_OPTIMIZE = false;
    private static final int FLAG_CHAIN_DANGLING = 1;
    private static final int FLAG_CHAIN_OPTIMIZE = 0;
    private static final int FLAG_RECOMPUTE_BOUNDS = 2;
    private static final int MAX_ITERATIONS = 8;
    public static final int OPTIMIZATION_ALL = 2;
    public static final int OPTIMIZATION_BASIC = 4;
    public static final int OPTIMIZATION_CHAIN = 8;
    public static final int OPTIMIZATION_NONE = 1;
    private static final boolean USE_SNAPSHOT = true;
    private static final boolean USE_THREAD = false;
    private boolean[] flags = new boolean[3];
    protected LinearSystem mBackgroundSystem = null;
    private ConstraintWidget[] mChainEnds = new ConstraintWidget[4];
    private boolean mHeightMeasuredTooSmall = false;
    private ConstraintWidget[] mHorizontalChainsArray = new ConstraintWidget[4];
    private int mHorizontalChainsSize = 0;
    private ConstraintWidget[] mMatchConstraintsChainedWidgets = new ConstraintWidget[4];
    private int mOptimizationLevel = 2;
    int mPaddingBottom;
    int mPaddingLeft;
    int mPaddingRight;
    int mPaddingTop;
    private Snapshot mSnapshot;
    protected LinearSystem mSystem = new LinearSystem();
    private ConstraintWidget[] mVerticalChainsArray = new ConstraintWidget[4];
    private int mVerticalChainsSize = 0;
    private boolean mWidthMeasuredTooSmall = false;
    int mWrapHeight;
    int mWrapWidth;

    public ConstraintWidgetContainer() {
    }

    public ConstraintWidgetContainer(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public ConstraintWidgetContainer(int width, int height) {
        super(width, height);
    }

    public void setOptimizationLevel(int value) {
        this.mOptimizationLevel = value;
    }

    public String getType() {
        return "ConstraintLayout";
    }

    public void reset() {
        this.mSystem.reset();
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.mPaddingTop = 0;
        this.mPaddingBottom = 0;
        super.reset();
    }

    public boolean isWidthMeasuredTooSmall() {
        return this.mWidthMeasuredTooSmall;
    }

    public boolean isHeightMeasuredTooSmall() {
        return this.mHeightMeasuredTooSmall;
    }

    public static ConstraintWidgetContainer createContainer(ConstraintWidgetContainer container, String name, ArrayList<ConstraintWidget> widgets, int padding) {
        Rectangle bounds = getBounds(widgets);
        if (bounds.width == 0 || bounds.height == 0) {
            return null;
        }
        if (padding > 0) {
            int maxPadding = Math.min(bounds.x, bounds.y);
            if (padding > maxPadding) {
                padding = maxPadding;
            }
            bounds.grow(padding, padding);
        }
        container.setOrigin(bounds.x, bounds.y);
        container.setDimension(bounds.width, bounds.height);
        container.setDebugName(name);
        ConstraintWidget parent = widgets.get(0).getParent();
        int widgetsSize = widgets.size();
        for (int i = 0; i < widgetsSize; i++) {
            ConstraintWidget widget = widgets.get(i);
            if (widget.getParent() == parent) {
                container.add(widget);
                widget.setX(widget.getX() - bounds.x);
                widget.setY(widget.getY() - bounds.y);
            }
        }
        return container;
    }

    public boolean addChildrenToSolver(LinearSystem system, int group) {
        addToSolver(system, group);
        int count = this.mChildren.size();
        boolean setMatchParent = false;
        int i = 0;
        if (this.mOptimizationLevel != 2 && this.mOptimizationLevel != 4) {
            setMatchParent = USE_SNAPSHOT;
        } else if (optimize(system)) {
            return false;
        }
        while (true) {
            int i2 = i;
            if (i2 >= count) {
                break;
            }
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i2);
            if (widget instanceof ConstraintWidgetContainer) {
                ConstraintWidget.DimensionBehaviour horizontalBehaviour = widget.mHorizontalDimensionBehaviour;
                ConstraintWidget.DimensionBehaviour verticalBehaviour = widget.mVerticalDimensionBehaviour;
                if (horizontalBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                }
                if (verticalBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                }
                widget.addToSolver(system, group);
                if (horizontalBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    widget.setHorizontalDimensionBehaviour(horizontalBehaviour);
                }
                if (verticalBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    widget.setVerticalDimensionBehaviour(verticalBehaviour);
                }
            } else {
                if (setMatchParent) {
                    Optimizer.checkMatchParent(this, system, widget);
                }
                widget.addToSolver(system, group);
            }
            i = i2 + 1;
        }
        if (this.mHorizontalChainsSize > 0) {
            applyHorizontalChain(system);
        }
        if (this.mVerticalChainsSize <= 0) {
            return USE_SNAPSHOT;
        }
        applyVerticalChain(system);
        return USE_SNAPSHOT;
    }

    private boolean optimize(LinearSystem system) {
        int count = this.mChildren.size();
        boolean done = false;
        int dv = 0;
        int dv2 = 0;
        int n = 0;
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
            widget.mHorizontalResolution = -1;
            widget.mVerticalResolution = -1;
            if (widget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || widget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                widget.mHorizontalResolution = 1;
                widget.mVerticalResolution = 1;
            }
        }
        while (!done) {
            int prev = dv;
            int preh = dv2;
            n++;
            int dh = 0;
            int dv3 = 0;
            for (int i2 = 0; i2 < count; i2++) {
                ConstraintWidget widget2 = (ConstraintWidget) this.mChildren.get(i2);
                if (widget2.mHorizontalResolution == -1) {
                    if (this.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                        widget2.mHorizontalResolution = 1;
                    } else {
                        Optimizer.checkHorizontalSimpleDependency(this, system, widget2);
                    }
                }
                if (widget2.mVerticalResolution == -1) {
                    if (this.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                        widget2.mVerticalResolution = 1;
                    } else {
                        Optimizer.checkVerticalSimpleDependency(this, system, widget2);
                    }
                }
                if (widget2.mVerticalResolution == -1) {
                    dv3++;
                }
                if (widget2.mHorizontalResolution == -1) {
                    dh++;
                }
            }
            if (dv3 == 0 && dh == 0) {
                done = USE_SNAPSHOT;
            } else if (prev == dv3 && preh == dh) {
                done = USE_SNAPSHOT;
            }
            dv = dv3;
            dv2 = dh;
        }
        int sv = 0;
        int sh = 0;
        for (int i3 = 0; i3 < count; i3++) {
            ConstraintWidget widget3 = (ConstraintWidget) this.mChildren.get(i3);
            if (widget3.mHorizontalResolution == 1 || widget3.mHorizontalResolution == -1) {
                sh++;
            }
            if (widget3.mVerticalResolution == 1 || widget3.mVerticalResolution == -1) {
                sv++;
            }
        }
        if (sh == 0 && sv == 0) {
            return USE_SNAPSHOT;
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:197:0x0524  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x0527  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void applyHorizontalChain(android.support.constraint.solver.LinearSystem r41) {
        /*
            r40 = this;
            r6 = r40
            r15 = r41
            r14 = 0
            r0 = r14
        L_0x0006:
            r13 = r0
            int r0 = r6.mHorizontalChainsSize
            if (r13 >= r0) goto L_0x05b6
            android.support.constraint.solver.widgets.ConstraintWidget[] r0 = r6.mHorizontalChainsArray
            r12 = r0[r13]
            android.support.constraint.solver.widgets.ConstraintWidget[] r2 = r6.mChainEnds
            android.support.constraint.solver.widgets.ConstraintWidget[] r0 = r6.mHorizontalChainsArray
            r3 = r0[r13]
            r4 = 0
            boolean[] r5 = r6.flags
            r0 = r6
            r1 = r15
            int r0 = r0.countMatchConstraintsChainedWidgets(r1, r2, r3, r4, r5)
            android.support.constraint.solver.widgets.ConstraintWidget[] r1 = r6.mChainEnds
            r2 = 2
            r1 = r1[r2]
            if (r1 != 0) goto L_0x002d
        L_0x0026:
            r20 = r13
            r28 = r14
            r0 = r15
            goto L_0x05ac
        L_0x002d:
            boolean[] r3 = r6.flags
            r4 = 1
            boolean r3 = r3[r4]
            if (r3 == 0) goto L_0x0058
            int r2 = r12.getDrawX()
        L_0x0038:
            if (r1 == 0) goto L_0x0026
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r1.mLeft
            android.support.constraint.solver.SolverVariable r3 = r3.mSolverVariable
            r15.addEquality(r3, r2)
            android.support.constraint.solver.widgets.ConstraintWidget r3 = r1.mHorizontalNextWidget
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mLeft
            int r4 = r4.getMargin()
            int r5 = r1.getWidth()
            int r4 = r4 + r5
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r1.mRight
            int r5 = r5.getMargin()
            int r4 = r4 + r5
            int r2 = r2 + r4
            r1 = r3
            goto L_0x0038
        L_0x0058:
            int r3 = r12.mHorizontalChainStyle
            if (r3 != 0) goto L_0x005e
            r3 = r4
            goto L_0x005f
        L_0x005e:
            r3 = r14
        L_0x005f:
            int r5 = r12.mHorizontalChainStyle
            if (r5 != r2) goto L_0x0065
            r5 = r4
            goto L_0x0066
        L_0x0065:
            r5 = r14
        L_0x0066:
            r11 = r12
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r7 = r6.mHorizontalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r8 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r7 != r8) goto L_0x006f
            r7 = r4
            goto L_0x0070
        L_0x006f:
            r7 = r14
        L_0x0070:
            r16 = r7
            int r7 = r6.mOptimizationLevel
            if (r7 == r2) goto L_0x007c
            int r7 = r6.mOptimizationLevel
            r8 = 8
            if (r7 != r8) goto L_0x0092
        L_0x007c:
            boolean[] r7 = r6.flags
            boolean r7 = r7[r14]
            if (r7 == 0) goto L_0x0092
            boolean r7 = r11.mHorizontalChainFixedPosition
            if (r7 == 0) goto L_0x0092
            if (r5 != 0) goto L_0x0092
            if (r16 != 0) goto L_0x0092
            int r7 = r12.mHorizontalChainStyle
            if (r7 != 0) goto L_0x0092
            android.support.constraint.solver.widgets.Optimizer.applyDirectResolutionHorizontalChain(r6, r15, r0, r11)
            goto L_0x0026
        L_0x0092:
            if (r0 == 0) goto L_0x038c
            if (r5 == 0) goto L_0x00a0
            r35 = r0
            r33 = r3
            r32 = r12
            r31 = r13
            goto L_0x0394
        L_0x00a0:
            r7 = 0
            r8 = 0
        L_0x00a2:
            if (r1 == 0) goto L_0x0167
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r9 = r1.mHorizontalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r2 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r9 == r2) goto L_0x0120
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r1.mLeft
            int r2 = r2.getMargin()
            if (r7 == 0) goto L_0x00b9
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r7.mRight
            int r9 = r9.getMargin()
            int r2 = r2 + r9
        L_0x00b9:
            r9 = 3
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r4 = r4.mHorizontalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r14 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r4 != r14) goto L_0x00c7
            r9 = 2
        L_0x00c7:
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mLeft
            android.support.constraint.solver.SolverVariable r4 = r4.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r1.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mTarget
            android.support.constraint.solver.SolverVariable r14 = r14.mSolverVariable
            r15.addGreaterThan(r4, r14, r2, r9)
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mRight
            int r2 = r4.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x0101
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            if (r4 != r1) goto L_0x0101
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mLeft
            int r4 = r4.getMargin()
            int r2 = r2 + r4
        L_0x0101:
            r4 = 3
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r1.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r9 = r9.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r9 = r9.mHorizontalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r14 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r9 != r14) goto L_0x010f
            r4 = 2
        L_0x010f:
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r1.mRight
            android.support.constraint.solver.SolverVariable r9 = r9.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r1.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mTarget
            android.support.constraint.solver.SolverVariable r14 = r14.mSolverVariable
            int r10 = -r2
            r15.addLowerThan(r9, r14, r10, r4)
            r10 = 1
            goto L_0x015f
        L_0x0120:
            float r2 = r1.mHorizontalWeight
            float r8 = r8 + r2
            r2 = 0
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x0144
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mRight
            int r2 = r4.getMargin()
            android.support.constraint.solver.widgets.ConstraintWidget[] r4 = r6.mChainEnds
            r9 = 3
            r4 = r4[r9]
            if (r1 == r4) goto L_0x0144
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mLeft
            int r4 = r4.getMargin()
            int r2 = r2 + r4
        L_0x0144:
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mRight
            android.support.constraint.solver.SolverVariable r4 = r4.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r1.mLeft
            android.support.constraint.solver.SolverVariable r9 = r9.mSolverVariable
            r10 = 1
            r14 = 0
            r15.addGreaterThan(r4, r9, r14, r10)
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mRight
            android.support.constraint.solver.SolverVariable r4 = r4.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r1.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            android.support.constraint.solver.SolverVariable r9 = r9.mSolverVariable
            int r14 = -r2
            r15.addLowerThan(r4, r9, r14, r10)
        L_0x015f:
            r7 = r1
            android.support.constraint.solver.widgets.ConstraintWidget r1 = r1.mHorizontalNextWidget
            r4 = r10
            r2 = 2
            r14 = 0
            goto L_0x00a2
        L_0x0167:
            r10 = r4
            if (r0 != r10) goto L_0x0203
            android.support.constraint.solver.widgets.ConstraintWidget[] r2 = r6.mMatchConstraintsChainedWidgets
            r14 = 0
            r2 = r2[r14]
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r2.mLeft
            int r4 = r4.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r2.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            if (r9 == 0) goto L_0x0184
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r2.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            int r9 = r9.getMargin()
            int r4 = r4 + r9
        L_0x0184:
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r2.mRight
            int r9 = r9.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r2.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r10.mTarget
            if (r10 == 0) goto L_0x0199
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r2.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r10.mTarget
            int r10 = r10.getMargin()
            int r9 = r9 + r10
        L_0x0199:
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r11.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r10.mTarget
            android.support.constraint.solver.SolverVariable r10 = r10.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintWidget[] r14 = r6.mChainEnds
            r17 = 3
            r14 = r14[r17]
            if (r2 != r14) goto L_0x01b5
            android.support.constraint.solver.widgets.ConstraintWidget[] r14 = r6.mChainEnds
            r29 = r1
            r1 = 1
            r14 = r14[r1]
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mTarget
            android.support.constraint.solver.SolverVariable r10 = r14.mSolverVariable
            goto L_0x01b8
        L_0x01b5:
            r29 = r1
            r1 = 1
        L_0x01b8:
            int r14 = r2.mMatchConstraintDefaultWidth
            if (r14 != r1) goto L_0x01e7
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r11.mLeft
            android.support.constraint.solver.SolverVariable r14 = r14.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r11.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r1.mTarget
            android.support.constraint.solver.SolverVariable r1 = r1.mSolverVariable
            r30 = r7
            r7 = 1
            r15.addGreaterThan(r14, r1, r4, r7)
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r11.mRight
            android.support.constraint.solver.SolverVariable r1 = r1.mSolverVariable
            int r14 = -r9
            r15.addLowerThan(r1, r10, r14, r7)
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r11.mRight
            android.support.constraint.solver.SolverVariable r1 = r1.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r11.mLeft
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            int r14 = r11.getWidth()
            r31 = r13
            r13 = 2
            r15.addEquality(r1, r7, r14, r13)
            goto L_0x0201
        L_0x01e7:
            r30 = r7
            r31 = r13
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r2.mLeft
            android.support.constraint.solver.SolverVariable r1 = r1.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r2.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            r13 = 1
            r15.addEquality(r1, r7, r4, r13)
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r2.mRight
            android.support.constraint.solver.SolverVariable r1 = r1.mSolverVariable
            int r7 = -r9
            r15.addEquality(r1, r10, r7, r13)
        L_0x0201:
            goto L_0x0385
        L_0x0203:
            r29 = r1
            r30 = r7
            r31 = r13
            r1 = 0
        L_0x020a:
            int r2 = r0 + -1
            if (r1 >= r2) goto L_0x0385
            android.support.constraint.solver.widgets.ConstraintWidget[] r2 = r6.mMatchConstraintsChainedWidgets
            r2 = r2[r1]
            android.support.constraint.solver.widgets.ConstraintWidget[] r4 = r6.mMatchConstraintsChainedWidgets
            int r7 = r1 + 1
            r4 = r4[r7]
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r2.mLeft
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r2.mRight
            android.support.constraint.solver.SolverVariable r9 = r9.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r4.mLeft
            android.support.constraint.solver.SolverVariable r10 = r10.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r13 = r4.mRight
            android.support.constraint.solver.SolverVariable r13 = r13.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintWidget[] r14 = r6.mChainEnds
            r17 = 3
            r14 = r14[r17]
            if (r4 != r14) goto L_0x023a
            android.support.constraint.solver.widgets.ConstraintWidget[] r14 = r6.mChainEnds
            r17 = 1
            r14 = r14[r17]
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mRight
            android.support.constraint.solver.SolverVariable r13 = r14.mSolverVariable
        L_0x023a:
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r2.mLeft
            int r14 = r14.getMargin()
            r32 = r12
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r2.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            if (r12 == 0) goto L_0x026f
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r2.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            if (r12 == 0) goto L_0x026f
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r2.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            if (r12 != r2) goto L_0x026f
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r2.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mRight
            int r12 = r12.getMargin()
            int r14 = r14 + r12
        L_0x026f:
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r2.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.SolverVariable r12 = r12.mSolverVariable
            r33 = r3
            r3 = 2
            r15.addGreaterThan(r7, r12, r14, r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r2.mRight
            int r3 = r3.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r2.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            if (r12 == 0) goto L_0x029e
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r2.mHorizontalNextWidget
            if (r12 == 0) goto L_0x029e
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r2.mHorizontalNextWidget
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            if (r12 == 0) goto L_0x029c
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r2.mHorizontalNextWidget
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mLeft
            int r14 = r12.getMargin()
            goto L_0x029d
        L_0x029c:
            r14 = 0
        L_0x029d:
            int r3 = r3 + r14
        L_0x029e:
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r2.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.SolverVariable r12 = r12.mSolverVariable
            int r14 = -r3
            r34 = r3
            r3 = 2
            r15.addLowerThan(r9, r12, r14, r3)
            int r3 = r1 + 1
            int r12 = r0 + -1
            if (r3 != r12) goto L_0x0339
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r4.mLeft
            int r3 = r3.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r4.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            if (r12 == 0) goto L_0x02e4
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r4.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            if (r12 == 0) goto L_0x02e4
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r4.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            if (r12 != r4) goto L_0x02e4
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r4.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mRight
            int r12 = r12.getMargin()
            int r3 = r3 + r12
        L_0x02e4:
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r4.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.SolverVariable r12 = r12.mSolverVariable
            r14 = 2
            r15.addGreaterThan(r10, r12, r3, r14)
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r4.mRight
            android.support.constraint.solver.widgets.ConstraintWidget[] r14 = r6.mChainEnds
            r17 = 3
            r14 = r14[r17]
            if (r4 != r14) goto L_0x0300
            android.support.constraint.solver.widgets.ConstraintWidget[] r14 = r6.mChainEnds
            r17 = 1
            r14 = r14[r17]
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r14.mRight
        L_0x0300:
            int r3 = r12.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r12.mTarget
            if (r14 == 0) goto L_0x0329
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r14 = r14.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mTarget
            if (r14 == 0) goto L_0x0329
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r14 = r14.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r14 = r14.mOwner
            if (r14 != r4) goto L_0x0329
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r14 = r14.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mLeft
            int r14 = r14.getMargin()
            int r3 = r3 + r14
        L_0x0329:
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r12.mTarget
            android.support.constraint.solver.SolverVariable r14 = r14.mSolverVariable
            r35 = r0
            int r0 = -r3
            r36 = r3
            r3 = 2
            r15.addLowerThan(r13, r14, r0, r3)
            r34 = r36
            goto L_0x033c
        L_0x0339:
            r35 = r0
            r3 = 2
        L_0x033c:
            int r0 = r11.mMatchConstraintMaxWidth
            if (r0 <= 0) goto L_0x0345
            int r0 = r11.mMatchConstraintMaxWidth
            r15.addLowerThan(r9, r7, r0, r3)
        L_0x0345:
            android.support.constraint.solver.ArrayRow r0 = r41.createRow()
            float r12 = r2.mHorizontalWeight
            float r14 = r4.mHorizontalWeight
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r2.mLeft
            int r22 = r3.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r2.mRight
            int r24 = r3.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r4.mLeft
            int r26 = r3.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r4.mRight
            int r28 = r3.getMargin()
            r17 = r0
            r18 = r12
            r19 = r8
            r20 = r14
            r21 = r7
            r23 = r9
            r25 = r10
            r27 = r13
            r17.createRowEqualDimension(r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28)
            r15.addConstraint(r0)
            int r1 = r1 + 1
            r12 = r32
            r3 = r33
            r0 = r35
            goto L_0x020a
        L_0x0385:
            r0 = r15
            r20 = r31
            r28 = 0
            goto L_0x05ac
        L_0x038c:
            r35 = r0
            r33 = r3
            r32 = r12
            r31 = r13
        L_0x0394:
            r0 = 0
            r2 = 0
            r3 = r1
            r4 = 0
            r17 = 0
            r18 = r4
            r4 = r2
            r2 = r0
            r0 = r17
        L_0x03a0:
            if (r1 == 0) goto L_0x0537
            android.support.constraint.solver.widgets.ConstraintWidget r0 = r1.mHorizontalNextWidget
            if (r0 != 0) goto L_0x03ae
            android.support.constraint.solver.widgets.ConstraintWidget[] r7 = r6.mChainEnds
            r8 = 1
            r4 = r7[r8]
            r7 = 1
            r18 = r7
        L_0x03ae:
            if (r5 == 0) goto L_0x0406
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r1.mLeft
            int r8 = r7.getMargin()
            if (r2 == 0) goto L_0x03bf
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r2.mRight
            int r9 = r9.getMargin()
            int r8 = r8 + r9
        L_0x03bf:
            r9 = 1
            if (r3 == r1) goto L_0x03c3
            r9 = 3
        L_0x03c3:
            android.support.constraint.solver.SolverVariable r10 = r7.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r7.mTarget
            android.support.constraint.solver.SolverVariable r12 = r12.mSolverVariable
            r15.addGreaterThan(r10, r12, r8, r9)
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r10 = r1.mHorizontalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r12 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r10 != r12) goto L_0x0402
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r1.mRight
            int r12 = r1.mMatchConstraintDefaultWidth
            r13 = 1
            if (r12 != r13) goto L_0x03ec
            int r12 = r1.mMatchConstraintMinWidth
            int r13 = r1.getWidth()
            int r12 = java.lang.Math.max(r12, r13)
            android.support.constraint.solver.SolverVariable r13 = r10.mSolverVariable
            android.support.constraint.solver.SolverVariable r14 = r7.mSolverVariable
            r6 = 3
            r15.addEquality(r13, r14, r12, r6)
            goto L_0x0403
        L_0x03ec:
            r6 = 3
            android.support.constraint.solver.SolverVariable r12 = r7.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r13 = r7.mTarget
            android.support.constraint.solver.SolverVariable r13 = r13.mSolverVariable
            int r14 = r7.mMargin
            r15.addGreaterThan(r12, r13, r14, r6)
            android.support.constraint.solver.SolverVariable r12 = r10.mSolverVariable
            android.support.constraint.solver.SolverVariable r13 = r7.mSolverVariable
            int r14 = r1.mMatchConstraintMinWidth
            r15.addLowerThan(r12, r13, r14, r6)
            goto L_0x0403
        L_0x0402:
            r6 = 3
        L_0x0403:
            r12 = r32
            goto L_0x0463
        L_0x0406:
            r6 = 3
            r7 = 5
            if (r33 != 0) goto L_0x0435
            if (r18 == 0) goto L_0x0435
            if (r2 == 0) goto L_0x0435
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r1.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r8.mTarget
            if (r8 != 0) goto L_0x0420
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r1.mRight
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            int r8 = r1.getDrawRight()
            r15.addEquality(r7, r8)
            goto L_0x0403
        L_0x0420:
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r1.mRight
            int r8 = r8.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r1.mRight
            android.support.constraint.solver.SolverVariable r9 = r9.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r4.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r10.mTarget
            android.support.constraint.solver.SolverVariable r10 = r10.mSolverVariable
            int r12 = -r8
            r15.addEquality(r9, r10, r12, r7)
            goto L_0x0403
        L_0x0435:
            if (r33 != 0) goto L_0x0472
            if (r18 != 0) goto L_0x0472
            if (r2 != 0) goto L_0x0472
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r1.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r8.mTarget
            if (r8 != 0) goto L_0x044d
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r1.mLeft
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            int r8 = r1.getDrawX()
            r15.addEquality(r7, r8)
            goto L_0x0403
        L_0x044d:
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r1.mLeft
            int r8 = r8.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r1.mLeft
            android.support.constraint.solver.SolverVariable r9 = r9.mSolverVariable
            r12 = r32
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r12.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r10.mTarget
            android.support.constraint.solver.SolverVariable r10 = r10.mSolverVariable
            r15.addEquality(r9, r10, r8, r7)
        L_0x0463:
            r39 = r0
            r25 = r6
            r26 = r11
            r38 = r12
            r0 = r15
            r20 = r31
            r28 = 0
            goto L_0x0521
        L_0x0472:
            r12 = r32
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r1.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r13 = r1.mRight
            int r10 = r14.getMargin()
            int r9 = r13.getMargin()
            android.support.constraint.solver.SolverVariable r7 = r14.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r14.mTarget
            android.support.constraint.solver.SolverVariable r8 = r8.mSolverVariable
            r6 = 1
            r15.addGreaterThan(r7, r8, r10, r6)
            android.support.constraint.solver.SolverVariable r7 = r13.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r13.mTarget
            android.support.constraint.solver.SolverVariable r8 = r8.mSolverVariable
            r37 = r10
            int r10 = -r9
            r15.addLowerThan(r7, r8, r10, r6)
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r14.mTarget
            if (r6 == 0) goto L_0x049f
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r14.mTarget
            android.support.constraint.solver.SolverVariable r6 = r6.mSolverVariable
            goto L_0x04a1
        L_0x049f:
            r6 = r17
        L_0x04a1:
            if (r2 != 0) goto L_0x04b3
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r12.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            if (r7 == 0) goto L_0x04b0
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r12.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            goto L_0x04b2
        L_0x04b0:
            r7 = r17
        L_0x04b2:
            r6 = r7
        L_0x04b3:
            if (r0 != 0) goto L_0x04c5
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r4.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            if (r7 == 0) goto L_0x04c2
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r4.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r7 = r7.mOwner
            goto L_0x04c4
        L_0x04c2:
            r7 = r17
        L_0x04c4:
            r0 = r7
        L_0x04c5:
            if (r0 == 0) goto L_0x0514
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r0.mLeft
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            if (r18 == 0) goto L_0x04dd
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r4.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r8.mTarget
            if (r8 == 0) goto L_0x04da
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r4.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r8.mTarget
            android.support.constraint.solver.SolverVariable r8 = r8.mSolverVariable
            goto L_0x04dc
        L_0x04da:
            r8 = r17
        L_0x04dc:
            r7 = r8
        L_0x04dd:
            r19 = r7
            if (r6 == 0) goto L_0x0514
            if (r19 == 0) goto L_0x0514
            android.support.constraint.solver.SolverVariable r8 = r14.mSolverVariable
            r20 = 1056964608(0x3f000000, float:0.5)
            android.support.constraint.solver.SolverVariable r10 = r13.mSolverVariable
            r21 = 4
            r7 = r15
            r22 = r9
            r9 = r6
            r24 = r10
            r23 = r37
            r25 = 3
            r10 = r23
            r26 = r11
            r11 = r20
            r38 = r12
            r12 = r19
            r27 = r13
            r20 = r31
            r13 = r24
            r24 = r14
            r28 = 0
            r14 = r22
            r39 = r0
            r0 = r15
            r15 = r21
            r7.addCentering(r8, r9, r10, r11, r12, r13, r14, r15)
            goto L_0x0521
        L_0x0514:
            r39 = r0
            r26 = r11
            r38 = r12
            r0 = r15
            r20 = r31
            r25 = 3
            r28 = 0
        L_0x0521:
            r2 = r1
            if (r18 == 0) goto L_0x0527
            r6 = r17
            goto L_0x0529
        L_0x0527:
            r6 = r39
        L_0x0529:
            r1 = r6
            r15 = r0
            r31 = r20
            r11 = r26
            r32 = r38
            r0 = r39
            r6 = r40
            goto L_0x03a0
        L_0x0537:
            r6 = r0
            r26 = r11
            r0 = r15
            r20 = r31
            r38 = r32
            r28 = 0
            if (r5 == 0) goto L_0x05aa
            android.support.constraint.solver.widgets.ConstraintAnchor r15 = r3.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r4.mRight
            int r19 = r15.getMargin()
            int r13 = r14.getMargin()
            r12 = r38
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r12.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            if (r7 == 0) goto L_0x055e
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r12.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            goto L_0x0560
        L_0x055e:
            r7 = r17
        L_0x0560:
            r21 = r7
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r4.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            if (r7 == 0) goto L_0x0571
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r4.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            r17 = r7
        L_0x0571:
            r11 = r17
            if (r21 == 0) goto L_0x05a7
            if (r11 == 0) goto L_0x05a7
            android.support.constraint.solver.SolverVariable r7 = r14.mSolverVariable
            int r8 = -r13
            r9 = 1
            r0.addLowerThan(r7, r11, r8, r9)
            android.support.constraint.solver.SolverVariable r8 = r15.mSolverVariable
            float r10 = r12.mHorizontalBiasPercent
            android.support.constraint.solver.SolverVariable r9 = r14.mSolverVariable
            r17 = 4
            r7 = r0
            r22 = r9
            r9 = r21
            r23 = r10
            r10 = r19
            r24 = r11
            r11 = r23
            r23 = r12
            r12 = r24
            r25 = r13
            r13 = r22
            r22 = r14
            r14 = r25
            r27 = r15
            r15 = r17
            r7.addCentering(r8, r9, r10, r11, r12, r13, r14, r15)
            goto L_0x05ac
        L_0x05a7:
            r23 = r12
            goto L_0x05ac
        L_0x05aa:
            r23 = r38
        L_0x05ac:
            int r1 = r20 + 1
            r15 = r0
            r0 = r1
            r14 = r28
            r6 = r40
            goto L_0x0006
        L_0x05b6:
            r0 = r15
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.ConstraintWidgetContainer.applyHorizontalChain(android.support.constraint.solver.LinearSystem):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:207:0x054a  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x054d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void applyVerticalChain(android.support.constraint.solver.LinearSystem r43) {
        /*
            r42 = this;
            r6 = r42
            r15 = r43
            r14 = 0
            r0 = r14
        L_0x0006:
            r13 = r0
            int r0 = r6.mVerticalChainsSize
            if (r13 >= r0) goto L_0x05dc
            android.support.constraint.solver.widgets.ConstraintWidget[] r0 = r6.mVerticalChainsArray
            r12 = r0[r13]
            android.support.constraint.solver.widgets.ConstraintWidget[] r2 = r6.mChainEnds
            android.support.constraint.solver.widgets.ConstraintWidget[] r0 = r6.mVerticalChainsArray
            r3 = r0[r13]
            r4 = 1
            boolean[] r5 = r6.flags
            r0 = r6
            r1 = r15
            int r0 = r0.countMatchConstraintsChainedWidgets(r1, r2, r3, r4, r5)
            android.support.constraint.solver.widgets.ConstraintWidget[] r1 = r6.mChainEnds
            r2 = 2
            r1 = r1[r2]
            if (r1 != 0) goto L_0x002d
        L_0x0026:
            r26 = r13
            r28 = r14
            r0 = r15
            goto L_0x05d2
        L_0x002d:
            boolean[] r3 = r6.flags
            r4 = 1
            boolean r3 = r3[r4]
            if (r3 == 0) goto L_0x0058
            int r2 = r12.getDrawY()
        L_0x0038:
            if (r1 == 0) goto L_0x0026
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r1.mTop
            android.support.constraint.solver.SolverVariable r3 = r3.mSolverVariable
            r15.addEquality(r3, r2)
            android.support.constraint.solver.widgets.ConstraintWidget r3 = r1.mVerticalNextWidget
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mTop
            int r4 = r4.getMargin()
            int r5 = r1.getHeight()
            int r4 = r4 + r5
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r1.mBottom
            int r5 = r5.getMargin()
            int r4 = r4 + r5
            int r2 = r2 + r4
            r1 = r3
            goto L_0x0038
        L_0x0058:
            int r3 = r12.mVerticalChainStyle
            if (r3 != 0) goto L_0x005e
            r3 = r4
            goto L_0x005f
        L_0x005e:
            r3 = r14
        L_0x005f:
            int r5 = r12.mVerticalChainStyle
            if (r5 != r2) goto L_0x0065
            r5 = r4
            goto L_0x0066
        L_0x0065:
            r5 = r14
        L_0x0066:
            r11 = r12
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r7 = r6.mVerticalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r8 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r7 != r8) goto L_0x006f
            r7 = r4
            goto L_0x0070
        L_0x006f:
            r7 = r14
        L_0x0070:
            r16 = r7
            int r7 = r6.mOptimizationLevel
            if (r7 == r2) goto L_0x007c
            int r7 = r6.mOptimizationLevel
            r8 = 8
            if (r7 != r8) goto L_0x0092
        L_0x007c:
            boolean[] r7 = r6.flags
            boolean r7 = r7[r14]
            if (r7 == 0) goto L_0x0092
            boolean r7 = r11.mVerticalChainFixedPosition
            if (r7 == 0) goto L_0x0092
            if (r5 != 0) goto L_0x0092
            if (r16 != 0) goto L_0x0092
            int r7 = r12.mVerticalChainStyle
            if (r7 != 0) goto L_0x0092
            android.support.constraint.solver.widgets.Optimizer.applyDirectResolutionVerticalChain(r6, r15, r0, r11)
            goto L_0x0026
        L_0x0092:
            if (r0 == 0) goto L_0x038c
            if (r5 == 0) goto L_0x00a0
            r35 = r0
            r33 = r3
            r32 = r12
            r31 = r13
            goto L_0x0394
        L_0x00a0:
            r7 = 0
            r8 = 0
        L_0x00a2:
            if (r1 == 0) goto L_0x0167
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r9 = r1.mVerticalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r2 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r9 == r2) goto L_0x0120
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r1.mTop
            int r2 = r2.getMargin()
            if (r7 == 0) goto L_0x00b9
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r7.mBottom
            int r9 = r9.getMargin()
            int r2 = r2 + r9
        L_0x00b9:
            r9 = 3
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r4 = r4.mVerticalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r14 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r4 != r14) goto L_0x00c7
            r9 = 2
        L_0x00c7:
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mTop
            android.support.constraint.solver.SolverVariable r4 = r4.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r1.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mTarget
            android.support.constraint.solver.SolverVariable r14 = r14.mSolverVariable
            r15.addGreaterThan(r4, r14, r2, r9)
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mBottom
            int r2 = r4.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x0101
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            if (r4 != r1) goto L_0x0101
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTop
            int r4 = r4.getMargin()
            int r2 = r2 + r4
        L_0x0101:
            r4 = 3
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r1.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r9 = r9.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r9 = r9.mVerticalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r14 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r9 != r14) goto L_0x010f
            r4 = 2
        L_0x010f:
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r1.mBottom
            android.support.constraint.solver.SolverVariable r9 = r9.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r1.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mTarget
            android.support.constraint.solver.SolverVariable r14 = r14.mSolverVariable
            int r10 = -r2
            r15.addLowerThan(r9, r14, r10, r4)
            r10 = 1
            goto L_0x015f
        L_0x0120:
            float r2 = r1.mVerticalWeight
            float r8 = r8 + r2
            r2 = 0
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x0144
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mBottom
            int r2 = r4.getMargin()
            android.support.constraint.solver.widgets.ConstraintWidget[] r4 = r6.mChainEnds
            r9 = 3
            r4 = r4[r9]
            if (r1 == r4) goto L_0x0144
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTop
            int r4 = r4.getMargin()
            int r2 = r2 + r4
        L_0x0144:
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mBottom
            android.support.constraint.solver.SolverVariable r4 = r4.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r1.mTop
            android.support.constraint.solver.SolverVariable r9 = r9.mSolverVariable
            r10 = 1
            r14 = 0
            r15.addGreaterThan(r4, r9, r14, r10)
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r1.mBottom
            android.support.constraint.solver.SolverVariable r4 = r4.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r1.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            android.support.constraint.solver.SolverVariable r9 = r9.mSolverVariable
            int r14 = -r2
            r15.addLowerThan(r4, r9, r14, r10)
        L_0x015f:
            r7 = r1
            android.support.constraint.solver.widgets.ConstraintWidget r1 = r1.mVerticalNextWidget
            r4 = r10
            r2 = 2
            r14 = 0
            goto L_0x00a2
        L_0x0167:
            r10 = r4
            if (r0 != r10) goto L_0x0203
            android.support.constraint.solver.widgets.ConstraintWidget[] r2 = r6.mMatchConstraintsChainedWidgets
            r14 = 0
            r2 = r2[r14]
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r2.mTop
            int r4 = r4.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r2.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            if (r9 == 0) goto L_0x0184
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r2.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            int r9 = r9.getMargin()
            int r4 = r4 + r9
        L_0x0184:
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r2.mBottom
            int r9 = r9.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r2.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r10.mTarget
            if (r10 == 0) goto L_0x0199
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r2.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r10.mTarget
            int r10 = r10.getMargin()
            int r9 = r9 + r10
        L_0x0199:
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r11.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r10.mTarget
            android.support.constraint.solver.SolverVariable r10 = r10.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintWidget[] r14 = r6.mChainEnds
            r17 = 3
            r14 = r14[r17]
            if (r2 != r14) goto L_0x01b5
            android.support.constraint.solver.widgets.ConstraintWidget[] r14 = r6.mChainEnds
            r29 = r1
            r1 = 1
            r14 = r14[r1]
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mTarget
            android.support.constraint.solver.SolverVariable r10 = r14.mSolverVariable
            goto L_0x01b8
        L_0x01b5:
            r29 = r1
            r1 = 1
        L_0x01b8:
            int r14 = r2.mMatchConstraintDefaultHeight
            if (r14 != r1) goto L_0x01e7
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r11.mTop
            android.support.constraint.solver.SolverVariable r14 = r14.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r11.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r1.mTarget
            android.support.constraint.solver.SolverVariable r1 = r1.mSolverVariable
            r30 = r7
            r7 = 1
            r15.addGreaterThan(r14, r1, r4, r7)
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r11.mBottom
            android.support.constraint.solver.SolverVariable r1 = r1.mSolverVariable
            int r14 = -r9
            r15.addLowerThan(r1, r10, r14, r7)
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r11.mBottom
            android.support.constraint.solver.SolverVariable r1 = r1.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r11.mTop
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            int r14 = r11.getHeight()
            r31 = r13
            r13 = 2
            r15.addEquality(r1, r7, r14, r13)
            goto L_0x0201
        L_0x01e7:
            r30 = r7
            r31 = r13
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r2.mTop
            android.support.constraint.solver.SolverVariable r1 = r1.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r2.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            r13 = 1
            r15.addEquality(r1, r7, r4, r13)
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r2.mBottom
            android.support.constraint.solver.SolverVariable r1 = r1.mSolverVariable
            int r7 = -r9
            r15.addEquality(r1, r10, r7, r13)
        L_0x0201:
            goto L_0x0385
        L_0x0203:
            r29 = r1
            r30 = r7
            r31 = r13
            r1 = 0
        L_0x020a:
            int r2 = r0 + -1
            if (r1 >= r2) goto L_0x0385
            android.support.constraint.solver.widgets.ConstraintWidget[] r2 = r6.mMatchConstraintsChainedWidgets
            r2 = r2[r1]
            android.support.constraint.solver.widgets.ConstraintWidget[] r4 = r6.mMatchConstraintsChainedWidgets
            int r7 = r1 + 1
            r4 = r4[r7]
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r2.mTop
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r2.mBottom
            android.support.constraint.solver.SolverVariable r9 = r9.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r4.mTop
            android.support.constraint.solver.SolverVariable r10 = r10.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r13 = r4.mBottom
            android.support.constraint.solver.SolverVariable r13 = r13.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintWidget[] r14 = r6.mChainEnds
            r17 = 3
            r14 = r14[r17]
            if (r4 != r14) goto L_0x023a
            android.support.constraint.solver.widgets.ConstraintWidget[] r14 = r6.mChainEnds
            r17 = 1
            r14 = r14[r17]
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mBottom
            android.support.constraint.solver.SolverVariable r13 = r14.mSolverVariable
        L_0x023a:
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r2.mTop
            int r14 = r14.getMargin()
            r32 = r12
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r2.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            if (r12 == 0) goto L_0x026f
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r2.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            if (r12 == 0) goto L_0x026f
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r2.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            if (r12 != r2) goto L_0x026f
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r2.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mBottom
            int r12 = r12.getMargin()
            int r14 = r14 + r12
        L_0x026f:
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r2.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.SolverVariable r12 = r12.mSolverVariable
            r33 = r3
            r3 = 2
            r15.addGreaterThan(r7, r12, r14, r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r2.mBottom
            int r3 = r3.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r2.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            if (r12 == 0) goto L_0x029e
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r2.mVerticalNextWidget
            if (r12 == 0) goto L_0x029e
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r2.mVerticalNextWidget
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            if (r12 == 0) goto L_0x029c
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r2.mVerticalNextWidget
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTop
            int r14 = r12.getMargin()
            goto L_0x029d
        L_0x029c:
            r14 = 0
        L_0x029d:
            int r3 = r3 + r14
        L_0x029e:
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r2.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.SolverVariable r12 = r12.mSolverVariable
            int r14 = -r3
            r34 = r3
            r3 = 2
            r15.addLowerThan(r9, r12, r14, r3)
            int r3 = r1 + 1
            int r12 = r0 + -1
            if (r3 != r12) goto L_0x0339
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r4.mTop
            int r3 = r3.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r4.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            if (r12 == 0) goto L_0x02e4
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r4.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            if (r12 == 0) goto L_0x02e4
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r4.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            if (r12 != r4) goto L_0x02e4
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r4.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r12 = r12.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mBottom
            int r12 = r12.getMargin()
            int r3 = r3 + r12
        L_0x02e4:
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r4.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r12.mTarget
            android.support.constraint.solver.SolverVariable r12 = r12.mSolverVariable
            r14 = 2
            r15.addGreaterThan(r10, r12, r3, r14)
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r4.mBottom
            android.support.constraint.solver.widgets.ConstraintWidget[] r14 = r6.mChainEnds
            r17 = 3
            r14 = r14[r17]
            if (r4 != r14) goto L_0x0300
            android.support.constraint.solver.widgets.ConstraintWidget[] r14 = r6.mChainEnds
            r17 = 1
            r14 = r14[r17]
            android.support.constraint.solver.widgets.ConstraintAnchor r12 = r14.mBottom
        L_0x0300:
            int r3 = r12.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r12.mTarget
            if (r14 == 0) goto L_0x0329
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r14 = r14.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mTarget
            if (r14 == 0) goto L_0x0329
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r14 = r14.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r14 = r14.mOwner
            if (r14 != r4) goto L_0x0329
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r12.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r14 = r14.mOwner
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r14.mTop
            int r14 = r14.getMargin()
            int r3 = r3 + r14
        L_0x0329:
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r12.mTarget
            android.support.constraint.solver.SolverVariable r14 = r14.mSolverVariable
            r35 = r0
            int r0 = -r3
            r36 = r3
            r3 = 2
            r15.addLowerThan(r13, r14, r0, r3)
            r34 = r36
            goto L_0x033c
        L_0x0339:
            r35 = r0
            r3 = 2
        L_0x033c:
            int r0 = r11.mMatchConstraintMaxHeight
            if (r0 <= 0) goto L_0x0345
            int r0 = r11.mMatchConstraintMaxHeight
            r15.addLowerThan(r9, r7, r0, r3)
        L_0x0345:
            android.support.constraint.solver.ArrayRow r0 = r43.createRow()
            float r12 = r2.mVerticalWeight
            float r14 = r4.mVerticalWeight
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r2.mTop
            int r22 = r3.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r2.mBottom
            int r24 = r3.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r4.mTop
            int r26 = r3.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r4.mBottom
            int r28 = r3.getMargin()
            r17 = r0
            r18 = r12
            r19 = r8
            r20 = r14
            r21 = r7
            r23 = r9
            r25 = r10
            r27 = r13
            r17.createRowEqualDimension(r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28)
            r15.addConstraint(r0)
            int r1 = r1 + 1
            r12 = r32
            r3 = r33
            r0 = r35
            goto L_0x020a
        L_0x0385:
            r0 = r15
            r26 = r31
            r28 = 0
            goto L_0x05d2
        L_0x038c:
            r35 = r0
            r33 = r3
            r32 = r12
            r31 = r13
        L_0x0394:
            r0 = 0
            r2 = 0
            r3 = r1
            r4 = 0
            r17 = 0
            r18 = r4
            r4 = r2
            r2 = r0
            r0 = r17
        L_0x03a0:
            if (r1 == 0) goto L_0x055d
            android.support.constraint.solver.widgets.ConstraintWidget r0 = r1.mVerticalNextWidget
            if (r0 != 0) goto L_0x03ae
            android.support.constraint.solver.widgets.ConstraintWidget[] r7 = r6.mChainEnds
            r8 = 1
            r4 = r7[r8]
            r7 = 1
            r18 = r7
        L_0x03ae:
            if (r5 == 0) goto L_0x042c
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r1.mTop
            int r8 = r7.getMargin()
            if (r2 == 0) goto L_0x03bf
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r2.mBottom
            int r9 = r9.getMargin()
            int r8 = r8 + r9
        L_0x03bf:
            r9 = 1
            if (r3 == r1) goto L_0x03c3
            r9 = 3
        L_0x03c3:
            r10 = 0
            r12 = 0
            android.support.constraint.solver.widgets.ConstraintAnchor r13 = r7.mTarget
            if (r13 == 0) goto L_0x03d0
            android.support.constraint.solver.SolverVariable r10 = r7.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r13 = r7.mTarget
            android.support.constraint.solver.SolverVariable r12 = r13.mSolverVariable
            goto L_0x03e5
        L_0x03d0:
            android.support.constraint.solver.widgets.ConstraintAnchor r13 = r1.mBaseline
            android.support.constraint.solver.widgets.ConstraintAnchor r13 = r13.mTarget
            if (r13 == 0) goto L_0x03e5
            android.support.constraint.solver.widgets.ConstraintAnchor r13 = r1.mBaseline
            android.support.constraint.solver.SolverVariable r10 = r13.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r13 = r1.mBaseline
            android.support.constraint.solver.widgets.ConstraintAnchor r13 = r13.mTarget
            android.support.constraint.solver.SolverVariable r12 = r13.mSolverVariable
            int r13 = r7.getMargin()
            int r8 = r8 - r13
        L_0x03e5:
            if (r10 == 0) goto L_0x03ec
            if (r12 == 0) goto L_0x03ec
            r15.addGreaterThan(r10, r12, r8, r9)
        L_0x03ec:
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r13 = r1.mVerticalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r14 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r13 != r14) goto L_0x042a
            android.support.constraint.solver.widgets.ConstraintAnchor r13 = r1.mBottom
            int r14 = r1.mMatchConstraintDefaultHeight
            r6 = 1
            if (r14 != r6) goto L_0x0410
            int r6 = r1.mMatchConstraintMinHeight
            int r14 = r1.getHeight()
            int r6 = java.lang.Math.max(r6, r14)
            android.support.constraint.solver.SolverVariable r14 = r13.mSolverVariable
            r37 = r8
            android.support.constraint.solver.SolverVariable r8 = r7.mSolverVariable
            r38 = r10
            r10 = 3
            r15.addEquality(r14, r8, r6, r10)
            goto L_0x042b
        L_0x0410:
            r37 = r8
            r38 = r10
            r10 = 3
            android.support.constraint.solver.SolverVariable r6 = r7.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r7.mTarget
            android.support.constraint.solver.SolverVariable r8 = r8.mSolverVariable
            int r14 = r7.mMargin
            r15.addGreaterThan(r6, r8, r14, r10)
            android.support.constraint.solver.SolverVariable r6 = r13.mSolverVariable
            android.support.constraint.solver.SolverVariable r8 = r7.mSolverVariable
            int r14 = r1.mMatchConstraintMinHeight
            r15.addLowerThan(r6, r8, r14, r10)
            goto L_0x042b
        L_0x042a:
            r10 = 3
        L_0x042b:
            goto L_0x0445
        L_0x042c:
            r10 = 3
            r6 = 5
            if (r33 != 0) goto L_0x045d
            if (r18 == 0) goto L_0x045d
            if (r2 == 0) goto L_0x045d
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r1.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            if (r7 != 0) goto L_0x0448
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r1.mBottom
            android.support.constraint.solver.SolverVariable r6 = r6.mSolverVariable
            int r7 = r1.getDrawBottom()
            r15.addEquality(r6, r7)
        L_0x0445:
            r12 = r32
            goto L_0x048b
        L_0x0448:
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r1.mBottom
            int r7 = r7.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r1.mBottom
            android.support.constraint.solver.SolverVariable r8 = r8.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r4.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            android.support.constraint.solver.SolverVariable r9 = r9.mSolverVariable
            int r12 = -r7
            r15.addEquality(r8, r9, r12, r6)
            goto L_0x0445
        L_0x045d:
            if (r33 != 0) goto L_0x049a
            if (r18 != 0) goto L_0x049a
            if (r2 != 0) goto L_0x049a
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r1.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            if (r7 != 0) goto L_0x0475
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r1.mTop
            android.support.constraint.solver.SolverVariable r6 = r6.mSolverVariable
            int r7 = r1.getDrawY()
            r15.addEquality(r6, r7)
            goto L_0x0445
        L_0x0475:
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r1.mTop
            int r7 = r7.getMargin()
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r1.mTop
            android.support.constraint.solver.SolverVariable r8 = r8.mSolverVariable
            r12 = r32
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r12.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r9 = r9.mTarget
            android.support.constraint.solver.SolverVariable r9 = r9.mSolverVariable
            r15.addEquality(r8, r9, r7, r6)
        L_0x048b:
            r41 = r0
            r24 = r10
            r25 = r11
            r40 = r12
            r0 = r15
            r26 = r31
            r28 = 0
            goto L_0x0547
        L_0x049a:
            r12 = r32
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r1.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r1.mBottom
            int r13 = r6.getMargin()
            int r9 = r14.getMargin()
            android.support.constraint.solver.SolverVariable r7 = r6.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r6.mTarget
            android.support.constraint.solver.SolverVariable r8 = r8.mSolverVariable
            r10 = 1
            r15.addGreaterThan(r7, r8, r13, r10)
            android.support.constraint.solver.SolverVariable r7 = r14.mSolverVariable
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r14.mTarget
            android.support.constraint.solver.SolverVariable r8 = r8.mSolverVariable
            r39 = r11
            int r11 = -r9
            r15.addLowerThan(r7, r8, r11, r10)
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r6.mTarget
            if (r7 == 0) goto L_0x04c7
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r6.mTarget
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            goto L_0x04c9
        L_0x04c7:
            r7 = r17
        L_0x04c9:
            if (r2 != 0) goto L_0x04db
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r12.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r8.mTarget
            if (r8 == 0) goto L_0x04d8
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r12.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r8.mTarget
            android.support.constraint.solver.SolverVariable r8 = r8.mSolverVariable
            goto L_0x04da
        L_0x04d8:
            r8 = r17
        L_0x04da:
            r7 = r8
        L_0x04db:
            r19 = r7
            if (r0 != 0) goto L_0x04ef
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r4.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            if (r7 == 0) goto L_0x04ec
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r4.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r7 = r7.mOwner
            goto L_0x04ee
        L_0x04ec:
            r7 = r17
        L_0x04ee:
            r0 = r7
        L_0x04ef:
            if (r0 == 0) goto L_0x053a
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r0.mTop
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            if (r18 == 0) goto L_0x0507
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r4.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r8.mTarget
            if (r8 == 0) goto L_0x0504
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r4.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r8.mTarget
            android.support.constraint.solver.SolverVariable r8 = r8.mSolverVariable
            goto L_0x0506
        L_0x0504:
            r8 = r17
        L_0x0506:
            r7 = r8
        L_0x0507:
            r20 = r7
            if (r19 == 0) goto L_0x053a
            if (r20 == 0) goto L_0x053a
            android.support.constraint.solver.SolverVariable r8 = r6.mSolverVariable
            r11 = 1056964608(0x3f000000, float:0.5)
            android.support.constraint.solver.SolverVariable r10 = r14.mSolverVariable
            r21 = 4
            r7 = r15
            r22 = r9
            r9 = r19
            r23 = r10
            r24 = 3
            r10 = r13
            r25 = r39
            r40 = r12
            r12 = r20
            r27 = r13
            r26 = r31
            r13 = r23
            r23 = r14
            r28 = 0
            r14 = r22
            r41 = r0
            r0 = r15
            r15 = r21
            r7.addCentering(r8, r9, r10, r11, r12, r13, r14, r15)
            goto L_0x0547
        L_0x053a:
            r41 = r0
            r40 = r12
            r0 = r15
            r26 = r31
            r25 = r39
            r24 = 3
            r28 = 0
        L_0x0547:
            r2 = r1
            if (r18 == 0) goto L_0x054d
            r6 = r17
            goto L_0x054f
        L_0x054d:
            r6 = r41
        L_0x054f:
            r1 = r6
            r15 = r0
            r11 = r25
            r31 = r26
            r32 = r40
            r0 = r41
            r6 = r42
            goto L_0x03a0
        L_0x055d:
            r6 = r0
            r25 = r11
            r0 = r15
            r26 = r31
            r40 = r32
            r28 = 0
            if (r5 == 0) goto L_0x05d0
            android.support.constraint.solver.widgets.ConstraintAnchor r15 = r3.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r14 = r4.mBottom
            int r19 = r15.getMargin()
            int r13 = r14.getMargin()
            r12 = r40
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r12.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            if (r7 == 0) goto L_0x0584
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r12.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            goto L_0x0586
        L_0x0584:
            r7 = r17
        L_0x0586:
            r20 = r7
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r4.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            if (r7 == 0) goto L_0x0597
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r4.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r7.mTarget
            android.support.constraint.solver.SolverVariable r7 = r7.mSolverVariable
            r17 = r7
        L_0x0597:
            r11 = r17
            if (r20 == 0) goto L_0x05cd
            if (r11 == 0) goto L_0x05cd
            android.support.constraint.solver.SolverVariable r7 = r14.mSolverVariable
            int r8 = -r13
            r9 = 1
            r0.addLowerThan(r7, r11, r8, r9)
            android.support.constraint.solver.SolverVariable r8 = r15.mSolverVariable
            float r10 = r12.mVerticalBiasPercent
            android.support.constraint.solver.SolverVariable r9 = r14.mSolverVariable
            r17 = 4
            r7 = r0
            r21 = r9
            r9 = r20
            r22 = r10
            r10 = r19
            r23 = r11
            r11 = r22
            r22 = r12
            r12 = r23
            r24 = r13
            r13 = r21
            r21 = r14
            r14 = r24
            r27 = r15
            r15 = r17
            r7.addCentering(r8, r9, r10, r11, r12, r13, r14, r15)
            goto L_0x05d2
        L_0x05cd:
            r22 = r12
            goto L_0x05d2
        L_0x05d0:
            r22 = r40
        L_0x05d2:
            int r1 = r26 + 1
            r15 = r0
            r0 = r1
            r14 = r28
            r6 = r42
            goto L_0x0006
        L_0x05dc:
            r0 = r15
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.ConstraintWidgetContainer.applyVerticalChain(android.support.constraint.solver.LinearSystem):void");
    }

    public void updateChildrenFromSolver(LinearSystem system, int group, boolean[] flags2) {
        flags2[2] = false;
        updateFromSolver(system, group);
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
            widget.updateFromSolver(system, group);
            if (widget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.getWidth() < widget.getWrapWidth()) {
                flags2[2] = USE_SNAPSHOT;
            }
            if (widget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.getHeight() < widget.getWrapHeight()) {
                flags2[2] = USE_SNAPSHOT;
            }
        }
    }

    public void setPadding(int left, int top, int right, int bottom) {
        this.mPaddingLeft = left;
        this.mPaddingTop = top;
        this.mPaddingRight = right;
        this.mPaddingBottom = bottom;
    }

    public void layout() {
        boolean needsSolving;
        int prex = this.mX;
        int prey = this.mY;
        int prew = Math.max(0, getWidth());
        int preh = Math.max(0, getHeight());
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        if (this.mParent != null) {
            if (this.mSnapshot == null) {
                this.mSnapshot = new Snapshot(this);
            }
            this.mSnapshot.updateFrom(this);
            setX(this.mPaddingLeft);
            setY(this.mPaddingTop);
            resetAnchors();
            resetSolverVariables(this.mSystem.getCache());
        } else {
            this.mX = 0;
            this.mY = 0;
        }
        boolean wrap_override = false;
        ConstraintWidget.DimensionBehaviour originalVerticalDimensionBehaviour = this.mVerticalDimensionBehaviour;
        ConstraintWidget.DimensionBehaviour originalHorizontalDimensionBehaviour = this.mHorizontalDimensionBehaviour;
        if (this.mOptimizationLevel == 2 && (this.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || this.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT)) {
            findWrapSize(this.mChildren, this.flags);
            wrap_override = this.flags[0];
            if (prew > 0 && preh > 0 && (this.mWrapWidth > prew || this.mWrapHeight > preh)) {
                wrap_override = false;
            }
            if (wrap_override) {
                if (this.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    this.mHorizontalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                    if (prew <= 0 || prew >= this.mWrapWidth) {
                        setWidth(Math.max(this.mMinWidth, this.mWrapWidth));
                    } else {
                        this.mWidthMeasuredTooSmall = USE_SNAPSHOT;
                        setWidth(prew);
                    }
                }
                if (this.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    this.mVerticalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                    if (preh <= 0 || preh >= this.mWrapHeight) {
                        setHeight(Math.max(this.mMinHeight, this.mWrapHeight));
                    } else {
                        this.mHeightMeasuredTooSmall = USE_SNAPSHOT;
                        setHeight(preh);
                    }
                }
            }
        }
        resetChains();
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
            if (widget instanceof WidgetContainer) {
                ((WidgetContainer) widget).layout();
            }
        }
        boolean needsSolving2 = USE_SNAPSHOT;
        boolean wrap_override2 = wrap_override;
        int countSolve = 0;
        while (needsSolving2) {
            countSolve++;
            try {
                this.mSystem.reset();
                needsSolving2 = addChildrenToSolver(this.mSystem, Integer.MAX_VALUE);
                if (needsSolving2) {
                    this.mSystem.minimize();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!needsSolving2) {
                updateFromSolver(this.mSystem, Integer.MAX_VALUE);
                int i2 = 0;
                while (true) {
                    if (i2 >= count) {
                        break;
                    }
                    ConstraintWidget widget2 = (ConstraintWidget) this.mChildren.get(i2);
                    if (widget2.mHorizontalDimensionBehaviour != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || widget2.getWidth() >= widget2.getWrapWidth()) {
                        if (widget2.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget2.getHeight() < widget2.getWrapHeight()) {
                            this.flags[2] = USE_SNAPSHOT;
                            break;
                        }
                        i2++;
                    } else {
                        this.flags[2] = USE_SNAPSHOT;
                        break;
                    }
                }
            } else {
                updateChildrenFromSolver(this.mSystem, Integer.MAX_VALUE, this.flags);
            }
            boolean needsSolving3 = false;
            if (countSolve >= 8 || !this.flags[2]) {
                needsSolving = false;
            } else {
                int maxY = 0;
                int maxX = 0;
                int i3 = 0;
                while (i3 < count) {
                    ConstraintWidget widget3 = (ConstraintWidget) this.mChildren.get(i3);
                    maxX = Math.max(maxX, widget3.mX + widget3.getWidth());
                    maxY = Math.max(maxY, widget3.mY + widget3.getHeight());
                    i3++;
                    needsSolving3 = needsSolving3;
                }
                needsSolving = needsSolving3;
                int maxX2 = Math.max(this.mMinWidth, maxX);
                int maxY2 = Math.max(this.mMinHeight, maxY);
                if (originalHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && getWidth() < maxX2) {
                    setWidth(maxX2);
                    this.mHorizontalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                    wrap_override2 = USE_SNAPSHOT;
                    needsSolving = true;
                }
                if (originalVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && getHeight() < maxY2) {
                    setHeight(maxY2);
                    this.mVerticalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                    wrap_override2 = USE_SNAPSHOT;
                    needsSolving = true;
                }
            }
            int width = Math.max(this.mMinWidth, getWidth());
            if (width > getWidth()) {
                setWidth(width);
                this.mHorizontalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                wrap_override2 = USE_SNAPSHOT;
                needsSolving = USE_SNAPSHOT;
            }
            int height = Math.max(this.mMinHeight, getHeight());
            if (height > getHeight()) {
                setHeight(height);
                this.mVerticalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                wrap_override2 = USE_SNAPSHOT;
                needsSolving = USE_SNAPSHOT;
            }
            if (!wrap_override2) {
                if (this.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && prew > 0 && getWidth() > prew) {
                    this.mWidthMeasuredTooSmall = USE_SNAPSHOT;
                    wrap_override2 = USE_SNAPSHOT;
                    this.mHorizontalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                    setWidth(prew);
                    needsSolving = USE_SNAPSHOT;
                }
                if (this.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && preh > 0 && getHeight() > preh) {
                    this.mHeightMeasuredTooSmall = USE_SNAPSHOT;
                    this.mVerticalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                    setHeight(preh);
                    wrap_override2 = true;
                    needsSolving2 = true;
                }
            }
            needsSolving2 = needsSolving;
        }
        if (this.mParent != null) {
            int width2 = Math.max(this.mMinWidth, getWidth());
            int height2 = Math.max(this.mMinHeight, getHeight());
            this.mSnapshot.applyTo(this);
            setWidth(this.mPaddingLeft + width2 + this.mPaddingRight);
            setHeight(this.mPaddingTop + height2 + this.mPaddingBottom);
        } else {
            this.mX = prex;
            this.mY = prey;
        }
        if (wrap_override2) {
            this.mHorizontalDimensionBehaviour = originalHorizontalDimensionBehaviour;
            this.mVerticalDimensionBehaviour = originalVerticalDimensionBehaviour;
        }
        resetSolverVariables(this.mSystem.getCache());
        if (this == getRootConstraintContainer()) {
            updateDrawPosition();
        }
    }

    static int setGroup(ConstraintAnchor anchor, int group) {
        int oldGroup = anchor.mGroup;
        if (anchor.mOwner.getParent() == null) {
            return group;
        }
        if (oldGroup <= group) {
            return oldGroup;
        }
        anchor.mGroup = group;
        ConstraintAnchor opposite = anchor.getOpposite();
        ConstraintAnchor target = anchor.mTarget;
        int group2 = opposite != null ? setGroup(opposite, group) : group;
        int group3 = target != null ? setGroup(target, group2) : group2;
        int group4 = opposite != null ? setGroup(opposite, group3) : group3;
        anchor.mGroup = group4;
        return group4;
    }

    public int layoutFindGroupsSimple() {
        int size = this.mChildren.size();
        for (int j = 0; j < size; j++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(j);
            widget.mLeft.mGroup = 0;
            widget.mRight.mGroup = 0;
            widget.mTop.mGroup = 1;
            widget.mBottom.mGroup = 1;
            widget.mBaseline.mGroup = 1;
        }
        return 2;
    }

    public void findHorizontalWrapRecursive(ConstraintWidget widget, boolean[] flags2) {
        boolean z = false;
        if (widget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.mDimensionRatio > 0.0f) {
            flags2[0] = false;
            return;
        }
        int w = widget.getOptimizerWrapWidth();
        if (widget.mHorizontalDimensionBehaviour != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || widget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || widget.mDimensionRatio <= 0.0f) {
            int distToRight = w;
            int distToLeft = w;
            ConstraintWidget leftWidget = null;
            ConstraintWidget rightWidget = null;
            widget.mHorizontalWrapVisited = USE_SNAPSHOT;
            if (widget instanceof Guideline) {
                Guideline guideline = (Guideline) widget;
                if (guideline.getOrientation() == 1) {
                    distToLeft = 0;
                    distToRight = 0;
                    if (guideline.getRelativeBegin() != -1) {
                        distToLeft = guideline.getRelativeBegin();
                    } else if (guideline.getRelativeEnd() != -1) {
                        distToRight = guideline.getRelativeEnd();
                    }
                }
            } else if (!widget.mRight.isConnected() && !widget.mLeft.isConnected()) {
                distToLeft += widget.getX();
            } else if (widget.mRight.mTarget == null || widget.mLeft.mTarget == null || (widget.mRight.mTarget != widget.mLeft.mTarget && (widget.mRight.mTarget.mOwner != widget.mLeft.mTarget.mOwner || widget.mRight.mTarget.mOwner == widget.mParent))) {
                if (widget.mRight.mTarget != null) {
                    rightWidget = widget.mRight.mTarget.mOwner;
                    distToRight += widget.mRight.getMargin();
                    if (!rightWidget.isRoot() && !rightWidget.mHorizontalWrapVisited) {
                        findHorizontalWrapRecursive(rightWidget, flags2);
                    }
                }
                if (widget.mLeft.mTarget != null) {
                    leftWidget = widget.mLeft.mTarget.mOwner;
                    distToLeft += widget.mLeft.getMargin();
                    if (!leftWidget.isRoot() && !leftWidget.mHorizontalWrapVisited) {
                        findHorizontalWrapRecursive(leftWidget, flags2);
                    }
                }
                if (widget.mRight.mTarget != null && !rightWidget.isRoot()) {
                    if (widget.mRight.mTarget.mType == ConstraintAnchor.Type.RIGHT) {
                        distToRight += rightWidget.mDistToRight - rightWidget.getOptimizerWrapWidth();
                    } else if (widget.mRight.mTarget.getType() == ConstraintAnchor.Type.LEFT) {
                        distToRight += rightWidget.mDistToRight;
                    }
                    widget.mRightHasCentered = rightWidget.mRightHasCentered || !(rightWidget.mLeft.mTarget == null || rightWidget.mRight.mTarget == null || rightWidget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                    if (widget.mRightHasCentered && (rightWidget.mLeft.mTarget == null || rightWidget.mLeft.mTarget.mOwner != widget)) {
                        distToRight += distToRight - rightWidget.mDistToRight;
                    }
                }
                if (widget.mLeft.mTarget != null && !leftWidget.isRoot()) {
                    if (widget.mLeft.mTarget.getType() == ConstraintAnchor.Type.LEFT) {
                        distToLeft += leftWidget.mDistToLeft - leftWidget.getOptimizerWrapWidth();
                    } else if (widget.mLeft.mTarget.getType() == ConstraintAnchor.Type.RIGHT) {
                        distToLeft += leftWidget.mDistToLeft;
                    }
                    if (leftWidget.mLeftHasCentered || !(leftWidget.mLeft.mTarget == null || leftWidget.mRight.mTarget == null || leftWidget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT)) {
                        z = true;
                    }
                    widget.mLeftHasCentered = z;
                    if (widget.mLeftHasCentered && (leftWidget.mRight.mTarget == null || leftWidget.mRight.mTarget.mOwner != widget)) {
                        distToLeft += distToLeft - leftWidget.mDistToLeft;
                    }
                }
            } else {
                flags2[0] = false;
                return;
            }
            if (widget.getVisibility() == 8) {
                distToLeft -= widget.mWidth;
                distToRight -= widget.mWidth;
            }
            widget.mDistToLeft = distToLeft;
            widget.mDistToRight = distToRight;
            return;
        }
        flags2[0] = false;
    }

    public void findVerticalWrapRecursive(ConstraintWidget widget, boolean[] flags2) {
        boolean z = false;
        if (widget.mVerticalDimensionBehaviour != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || widget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || widget.mDimensionRatio <= 0.0f) {
            int h = widget.getOptimizerWrapHeight();
            int distToTop = h;
            int distToBottom = h;
            ConstraintWidget topWidget = null;
            ConstraintWidget bottomWidget = null;
            widget.mVerticalWrapVisited = USE_SNAPSHOT;
            if (widget instanceof Guideline) {
                Guideline guideline = (Guideline) widget;
                if (guideline.getOrientation() == 0) {
                    distToTop = 0;
                    distToBottom = 0;
                    if (guideline.getRelativeBegin() != -1) {
                        distToTop = guideline.getRelativeBegin();
                    } else if (guideline.getRelativeEnd() != -1) {
                        distToBottom = guideline.getRelativeEnd();
                    }
                }
            } else if (widget.mBaseline.mTarget == null && widget.mTop.mTarget == null && widget.mBottom.mTarget == null) {
                distToTop += widget.getY();
            } else if (widget.mBottom.mTarget != null && widget.mTop.mTarget != null && (widget.mBottom.mTarget == widget.mTop.mTarget || (widget.mBottom.mTarget.mOwner == widget.mTop.mTarget.mOwner && widget.mBottom.mTarget.mOwner != widget.mParent))) {
                flags2[0] = false;
                return;
            } else if (widget.mBaseline.isConnected()) {
                ConstraintWidget baseLineWidget = widget.mBaseline.mTarget.getOwner();
                if (!baseLineWidget.mVerticalWrapVisited) {
                    findVerticalWrapRecursive(baseLineWidget, flags2);
                }
                int distToTop2 = Math.max((baseLineWidget.mDistToTop - baseLineWidget.mHeight) + h, h);
                int distToBottom2 = Math.max((baseLineWidget.mDistToBottom - baseLineWidget.mHeight) + h, h);
                if (widget.getVisibility() == 8) {
                    distToTop2 -= widget.mHeight;
                    distToBottom2 -= widget.mHeight;
                }
                widget.mDistToTop = distToTop2;
                widget.mDistToBottom = distToBottom2;
                return;
            } else {
                if (widget.mTop.isConnected()) {
                    topWidget = widget.mTop.mTarget.getOwner();
                    distToTop += widget.mTop.getMargin();
                    if (!topWidget.isRoot() && !topWidget.mVerticalWrapVisited) {
                        findVerticalWrapRecursive(topWidget, flags2);
                    }
                }
                if (widget.mBottom.isConnected()) {
                    bottomWidget = widget.mBottom.mTarget.getOwner();
                    distToBottom += widget.mBottom.getMargin();
                    if (!bottomWidget.isRoot() && !bottomWidget.mVerticalWrapVisited) {
                        findVerticalWrapRecursive(bottomWidget, flags2);
                    }
                }
                if (widget.mTop.mTarget != null && !topWidget.isRoot()) {
                    if (widget.mTop.mTarget.getType() == ConstraintAnchor.Type.TOP) {
                        distToTop += topWidget.mDistToTop - topWidget.getOptimizerWrapHeight();
                    } else if (widget.mTop.mTarget.getType() == ConstraintAnchor.Type.BOTTOM) {
                        distToTop += topWidget.mDistToTop;
                    }
                    widget.mTopHasCentered = topWidget.mTopHasCentered || !(topWidget.mTop.mTarget == null || topWidget.mTop.mTarget.mOwner == widget || topWidget.mBottom.mTarget == null || topWidget.mBottom.mTarget.mOwner == widget || topWidget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                    if (widget.mTopHasCentered && (topWidget.mBottom.mTarget == null || topWidget.mBottom.mTarget.mOwner != widget)) {
                        distToTop += distToTop - topWidget.mDistToTop;
                    }
                }
                if (widget.mBottom.mTarget != null && !bottomWidget.isRoot()) {
                    if (widget.mBottom.mTarget.getType() == ConstraintAnchor.Type.BOTTOM) {
                        distToBottom += bottomWidget.mDistToBottom - bottomWidget.getOptimizerWrapHeight();
                    } else if (widget.mBottom.mTarget.getType() == ConstraintAnchor.Type.TOP) {
                        distToBottom += bottomWidget.mDistToBottom;
                    }
                    if (bottomWidget.mBottomHasCentered || !(bottomWidget.mTop.mTarget == null || bottomWidget.mTop.mTarget.mOwner == widget || bottomWidget.mBottom.mTarget == null || bottomWidget.mBottom.mTarget.mOwner == widget || bottomWidget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT)) {
                        z = true;
                    }
                    widget.mBottomHasCentered = z;
                    if (widget.mBottomHasCentered && (bottomWidget.mTop.mTarget == null || bottomWidget.mTop.mTarget.mOwner != widget)) {
                        distToBottom += distToBottom - bottomWidget.mDistToBottom;
                    }
                }
            }
            if (widget.getVisibility() == 8) {
                distToTop -= widget.mHeight;
                distToBottom -= widget.mHeight;
            }
            widget.mDistToTop = distToTop;
            widget.mDistToBottom = distToBottom;
            return;
        }
        flags2[0] = false;
    }

    public void findWrapSize(ArrayList<ConstraintWidget> children, boolean[] flags2) {
        ArrayList<ConstraintWidget> arrayList = children;
        boolean[] zArr = flags2;
        int maxLeftDist = 0;
        int maxRightDist = 0;
        int maxConnectWidth = 0;
        int size = children.size();
        char c = 0;
        zArr[0] = USE_SNAPSHOT;
        int maxConnectHeight = 0;
        int maxBottomDist = 0;
        int maxTopDist = 0;
        int j = 0;
        while (j < size) {
            ConstraintWidget widget = arrayList.get(j);
            if (!widget.isRoot()) {
                if (!widget.mHorizontalWrapVisited) {
                    findHorizontalWrapRecursive(widget, zArr);
                }
                if (!widget.mVerticalWrapVisited) {
                    findVerticalWrapRecursive(widget, zArr);
                }
                if (zArr[c]) {
                    int connectWidth = (widget.mDistToLeft + widget.mDistToRight) - widget.getWidth();
                    int connectHeight = (widget.mDistToTop + widget.mDistToBottom) - widget.getHeight();
                    if (widget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
                        connectWidth = widget.getWidth() + widget.mLeft.mMargin + widget.mRight.mMargin;
                    }
                    if (widget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
                        connectHeight = widget.getHeight() + widget.mTop.mMargin + widget.mBottom.mMargin;
                    }
                    if (widget.getVisibility() == 8) {
                        connectWidth = 0;
                        connectHeight = 0;
                    }
                    maxLeftDist = Math.max(maxLeftDist, widget.mDistToLeft);
                    maxRightDist = Math.max(maxRightDist, widget.mDistToRight);
                    maxBottomDist = Math.max(maxBottomDist, widget.mDistToBottom);
                    maxTopDist = Math.max(maxTopDist, widget.mDistToTop);
                    maxConnectWidth = Math.max(maxConnectWidth, connectWidth);
                    maxConnectHeight = Math.max(maxConnectHeight, connectHeight);
                } else {
                    return;
                }
            }
            j++;
            c = 0;
        }
        this.mWrapWidth = Math.max(this.mMinWidth, Math.max(Math.max(maxLeftDist, maxRightDist), maxConnectWidth));
        this.mWrapHeight = Math.max(this.mMinHeight, Math.max(Math.max(maxTopDist, maxBottomDist), maxConnectHeight));
        for (int j2 = 0; j2 < size; j2++) {
            ConstraintWidget child = arrayList.get(j2);
            child.mHorizontalWrapVisited = false;
            child.mVerticalWrapVisited = false;
            child.mLeftHasCentered = false;
            child.mRightHasCentered = false;
            child.mTopHasCentered = false;
            child.mBottomHasCentered = false;
        }
    }

    public int layoutFindGroups() {
        int index;
        int i = 0;
        ConstraintAnchor.Type[] dir = {ConstraintAnchor.Type.LEFT, ConstraintAnchor.Type.RIGHT, ConstraintAnchor.Type.TOP, ConstraintAnchor.Type.BASELINE, ConstraintAnchor.Type.BOTTOM};
        int size = this.mChildren.size();
        int label = 1;
        for (int j = 0; j < size; j++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(j);
            ConstraintAnchor anchor = widget.mLeft;
            if (anchor.mTarget == null) {
                anchor.mGroup = Integer.MAX_VALUE;
            } else if (setGroup(anchor, label) == label) {
                label++;
            }
            ConstraintAnchor anchor2 = widget.mTop;
            if (anchor2.mTarget == null) {
                anchor2.mGroup = Integer.MAX_VALUE;
            } else if (setGroup(anchor2, label) == label) {
                label++;
            }
            ConstraintAnchor anchor3 = widget.mRight;
            if (anchor3.mTarget == null) {
                anchor3.mGroup = Integer.MAX_VALUE;
            } else if (setGroup(anchor3, label) == label) {
                label++;
            }
            ConstraintAnchor anchor4 = widget.mBottom;
            if (anchor4.mTarget == null) {
                anchor4.mGroup = Integer.MAX_VALUE;
            } else if (setGroup(anchor4, label) == label) {
                label++;
            }
            ConstraintAnchor anchor5 = widget.mBaseline;
            if (anchor5.mTarget == null) {
                anchor5.mGroup = Integer.MAX_VALUE;
            } else if (setGroup(anchor5, label) == label) {
                label++;
            }
        }
        int count = 0;
        boolean notDone = true;
        int j2 = 0;
        while (notDone) {
            notDone = false;
            count++;
            int fix = j2;
            int j3 = i;
            while (j3 < size) {
                ConstraintWidget widget2 = (ConstraintWidget) this.mChildren.get(j3);
                int fix2 = fix;
                boolean notDone2 = notDone;
                for (int i2 = i; i2 < dir.length; i2++) {
                    ConstraintAnchor anchor6 = null;
                    switch (dir[i2]) {
                        case LEFT:
                            anchor6 = widget2.mLeft;
                            break;
                        case TOP:
                            anchor6 = widget2.mTop;
                            break;
                        case RIGHT:
                            anchor6 = widget2.mRight;
                            break;
                        case BOTTOM:
                            anchor6 = widget2.mBottom;
                            break;
                        case BASELINE:
                            anchor6 = widget2.mBaseline;
                            break;
                    }
                    ConstraintAnchor target = anchor6.mTarget;
                    if (target != null) {
                        if (!(target.mOwner.getParent() == null || target.mGroup == anchor6.mGroup)) {
                            int i3 = anchor6.mGroup > target.mGroup ? target.mGroup : anchor6.mGroup;
                            anchor6.mGroup = i3;
                            target.mGroup = i3;
                            fix2++;
                            notDone2 = USE_SNAPSHOT;
                        }
                        ConstraintAnchor opposite = target.getOpposite();
                        if (!(opposite == null || opposite.mGroup == anchor6.mGroup)) {
                            int i4 = anchor6.mGroup > opposite.mGroup ? opposite.mGroup : anchor6.mGroup;
                            anchor6.mGroup = i4;
                            opposite.mGroup = i4;
                            fix2++;
                            notDone2 = true;
                        }
                    }
                }
                j3++;
                notDone = notDone2;
                fix = fix2;
                i = 0;
            }
            j2 = fix;
        }
        int index2 = 0;
        int[] table = new int[((this.mChildren.size() * dir.length) + 1)];
        Arrays.fill(table, -1);
        int j4 = 0;
        while (true) {
            int j5 = j4;
            if (j5 >= size) {
                return index2;
            }
            ConstraintWidget widget3 = (ConstraintWidget) this.mChildren.get(j5);
            ConstraintAnchor anchor7 = widget3.mLeft;
            if (anchor7.mGroup != Integer.MAX_VALUE) {
                int g = anchor7.mGroup;
                if (table[g] == -1) {
                    table[g] = index2;
                    index2++;
                }
                anchor7.mGroup = table[g];
            }
            ConstraintAnchor anchor8 = widget3.mTop;
            if (anchor8.mGroup != Integer.MAX_VALUE) {
                int g2 = anchor8.mGroup;
                if (table[g2] == -1) {
                    table[g2] = index2;
                    index2++;
                }
                anchor8.mGroup = table[g2];
            }
            ConstraintAnchor anchor9 = widget3.mRight;
            if (anchor9.mGroup != Integer.MAX_VALUE) {
                int g3 = anchor9.mGroup;
                if (table[g3] == -1) {
                    table[g3] = index2;
                    index2++;
                }
                anchor9.mGroup = table[g3];
            }
            ConstraintAnchor anchor10 = widget3.mBottom;
            if (anchor10.mGroup != Integer.MAX_VALUE) {
                int g4 = anchor10.mGroup;
                if (table[g4] == -1) {
                    table[g4] = index2;
                    index2++;
                }
                anchor10.mGroup = table[g4];
            }
            ConstraintAnchor anchor11 = widget3.mBaseline;
            if (anchor11.mGroup != Integer.MAX_VALUE) {
                int g5 = anchor11.mGroup;
                if (table[g5] == -1) {
                    index = index2 + 1;
                    table[g5] = index2;
                } else {
                    index = index2;
                }
                anchor11.mGroup = table[g5];
                index2 = index;
            }
            j4 = j5 + 1;
        }
    }

    public void layoutWithGroup(int numOfGroups) {
        int prex = this.mX;
        int prey = this.mY;
        if (this.mParent != null) {
            if (this.mSnapshot == null) {
                this.mSnapshot = new Snapshot(this);
            }
            this.mSnapshot.updateFrom(this);
            this.mX = 0;
            this.mY = 0;
            resetAnchors();
            resetSolverVariables(this.mSystem.getCache());
        } else {
            this.mX = 0;
            this.mY = 0;
        }
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
            if (widget instanceof WidgetContainer) {
                ((WidgetContainer) widget).layout();
            }
        }
        this.mLeft.mGroup = 0;
        this.mRight.mGroup = 0;
        this.mTop.mGroup = 1;
        this.mBottom.mGroup = 1;
        this.mSystem.reset();
        for (int i2 = 0; i2 < numOfGroups; i2++) {
            try {
                addToSolver(this.mSystem, i2);
                this.mSystem.minimize();
                updateFromSolver(this.mSystem, i2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateFromSolver(this.mSystem, -2);
        }
        if (this.mParent != null) {
            int width = getWidth();
            int height = getHeight();
            this.mSnapshot.applyTo(this);
            setWidth(width);
            setHeight(height);
        } else {
            this.mX = prex;
            this.mY = prey;
        }
        if (this == getRootConstraintContainer()) {
            updateDrawPosition();
        }
    }

    public boolean handlesInternalConstraints() {
        return false;
    }

    public ArrayList<Guideline> getVerticalGuidelines() {
        ArrayList<Guideline> guidelines = new ArrayList<>();
        int mChildrenSize = this.mChildren.size();
        for (int i = 0; i < mChildrenSize; i++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
            if (widget instanceof Guideline) {
                Guideline guideline = (Guideline) widget;
                if (guideline.getOrientation() == 1) {
                    guidelines.add(guideline);
                }
            }
        }
        return guidelines;
    }

    public ArrayList<Guideline> getHorizontalGuidelines() {
        ArrayList<Guideline> guidelines = new ArrayList<>();
        int mChildrenSize = this.mChildren.size();
        for (int i = 0; i < mChildrenSize; i++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
            if (widget instanceof Guideline) {
                Guideline guideline = (Guideline) widget;
                if (guideline.getOrientation() == 0) {
                    guidelines.add(guideline);
                }
            }
        }
        return guidelines;
    }

    public LinearSystem getSystem() {
        return this.mSystem;
    }

    private void resetChains() {
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
    }

    /* access modifiers changed from: package-private */
    public void addChain(ConstraintWidget constraintWidget, int type) {
        ConstraintWidget widget = constraintWidget;
        if (type == 0) {
            while (widget.mLeft.mTarget != null && widget.mLeft.mTarget.mOwner.mRight.mTarget != null && widget.mLeft.mTarget.mOwner.mRight.mTarget == widget.mLeft && widget.mLeft.mTarget.mOwner != widget) {
                widget = widget.mLeft.mTarget.mOwner;
            }
            addHorizontalChain(widget);
        } else if (type == 1) {
            while (widget.mTop.mTarget != null && widget.mTop.mTarget.mOwner.mBottom.mTarget != null && widget.mTop.mTarget.mOwner.mBottom.mTarget == widget.mTop && widget.mTop.mTarget.mOwner != widget) {
                widget = widget.mTop.mTarget.mOwner;
            }
            addVerticalChain(widget);
        }
    }

    private void addHorizontalChain(ConstraintWidget widget) {
        int i = 0;
        while (i < this.mHorizontalChainsSize) {
            if (this.mHorizontalChainsArray[i] != widget) {
                i++;
            } else {
                return;
            }
        }
        if (this.mHorizontalChainsSize + 1 >= this.mHorizontalChainsArray.length) {
            this.mHorizontalChainsArray = (ConstraintWidget[]) Arrays.copyOf(this.mHorizontalChainsArray, this.mHorizontalChainsArray.length * 2);
        }
        this.mHorizontalChainsArray[this.mHorizontalChainsSize] = widget;
        this.mHorizontalChainsSize++;
    }

    private void addVerticalChain(ConstraintWidget widget) {
        int i = 0;
        while (i < this.mVerticalChainsSize) {
            if (this.mVerticalChainsArray[i] != widget) {
                i++;
            } else {
                return;
            }
        }
        if (this.mVerticalChainsSize + 1 >= this.mVerticalChainsArray.length) {
            this.mVerticalChainsArray = (ConstraintWidget[]) Arrays.copyOf(this.mVerticalChainsArray, this.mVerticalChainsArray.length * 2);
        }
        this.mVerticalChainsArray[this.mVerticalChainsSize] = widget;
        this.mVerticalChainsSize++;
    }

    private int countMatchConstraintsChainedWidgets(LinearSystem system, ConstraintWidget[] chainEnds, ConstraintWidget widget, int direction, boolean[] flags2) {
        char c;
        char c2;
        ConstraintWidget last;
        LinearSystem linearSystem = system;
        ConstraintWidget widget2 = widget;
        flags2[0] = USE_SNAPSHOT;
        flags2[1] = false;
        ConstraintWidget constraintWidget = null;
        chainEnds[0] = null;
        chainEnds[2] = null;
        chainEnds[1] = null;
        chainEnds[3] = null;
        int i = 5;
        int i2 = 8;
        if (direction == 0) {
            boolean fixedPosition = USE_SNAPSHOT;
            ConstraintWidget first = widget2;
            if (!(widget2.mLeft.mTarget == null || widget2.mLeft.mTarget.mOwner == this)) {
                fixedPosition = false;
            }
            widget2.mHorizontalNextWidget = null;
            ConstraintWidget firstVisible = null;
            if (widget.getVisibility() != 8) {
                firstVisible = widget2;
            }
            int count = 0;
            ConstraintWidget firstVisible2 = firstVisible;
            ConstraintWidget last2 = null;
            while (widget2.mRight.mTarget != null) {
                widget2.mHorizontalNextWidget = constraintWidget;
                if (widget2.getVisibility() != 8) {
                    if (firstVisible2 == null) {
                        firstVisible2 = widget2;
                    }
                    if (!(firstVisible == null || firstVisible == widget2)) {
                        firstVisible.mHorizontalNextWidget = widget2;
                    }
                    firstVisible = widget2;
                } else {
                    linearSystem.addEquality(widget2.mLeft.mSolverVariable, widget2.mLeft.mTarget.mSolverVariable, 0, 5);
                    linearSystem.addEquality(widget2.mRight.mSolverVariable, widget2.mLeft.mSolverVariable, 0, 5);
                }
                if (widget2.getVisibility() != 8 && widget2.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    if (widget2.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                        flags2[0] = false;
                    }
                    if (widget2.mDimensionRatio <= 0.0f) {
                        flags2[0] = false;
                        if (count + 1 >= this.mMatchConstraintsChainedWidgets.length) {
                            this.mMatchConstraintsChainedWidgets = (ConstraintWidget[]) Arrays.copyOf(this.mMatchConstraintsChainedWidgets, this.mMatchConstraintsChainedWidgets.length * 2);
                        }
                        this.mMatchConstraintsChainedWidgets[count] = widget2;
                        count++;
                    }
                }
                if (widget2.mRight.mTarget.mOwner.mLeft.mTarget == null || widget2.mRight.mTarget.mOwner.mLeft.mTarget.mOwner != widget2 || widget2.mRight.mTarget.mOwner == widget2) {
                    break;
                }
                widget2 = widget2.mRight.mTarget.mOwner;
                last2 = widget2;
                constraintWidget = null;
            }
            if (!(widget2.mRight.mTarget == null || widget2.mRight.mTarget.mOwner == this)) {
                fixedPosition = false;
            }
            ConstraintWidget first2 = first;
            if (first2.mLeft.mTarget != null) {
                last = last2;
                if (last.mRight.mTarget != null) {
                    c2 = 1;
                    first2.mHorizontalChainFixedPosition = fixedPosition;
                    last.mHorizontalNextWidget = null;
                    chainEnds[0] = first2;
                    chainEnds[2] = firstVisible2;
                    chainEnds[c2] = last;
                    chainEnds[3] = firstVisible;
                    return count;
                }
            } else {
                last = last2;
            }
            c2 = 1;
            flags2[1] = USE_SNAPSHOT;
            first2.mHorizontalChainFixedPosition = fixedPosition;
            last.mHorizontalNextWidget = null;
            chainEnds[0] = first2;
            chainEnds[2] = firstVisible2;
            chainEnds[c2] = last;
            chainEnds[3] = firstVisible;
            return count;
        }
        boolean fixedPosition2 = USE_SNAPSHOT;
        ConstraintWidget first3 = widget2;
        if (!(widget2.mTop.mTarget == null || widget2.mTop.mTarget.mOwner == this)) {
            fixedPosition2 = false;
        }
        widget2.mVerticalNextWidget = null;
        ConstraintWidget firstVisible3 = null;
        if (widget.getVisibility() != 8) {
            firstVisible3 = widget2;
        }
        ConstraintWidget last3 = null;
        int count2 = 0;
        ConstraintWidget firstVisible4 = firstVisible3;
        while (widget2.mBottom.mTarget != null) {
            widget2.mVerticalNextWidget = null;
            if (widget2.getVisibility() != i2) {
                if (firstVisible4 == null) {
                    firstVisible4 = widget2;
                }
                if (!(firstVisible3 == null || firstVisible3 == widget2)) {
                    firstVisible3.mVerticalNextWidget = widget2;
                }
                firstVisible3 = widget2;
            } else {
                linearSystem.addEquality(widget2.mTop.mSolverVariable, widget2.mTop.mTarget.mSolverVariable, 0, i);
                linearSystem.addEquality(widget2.mBottom.mSolverVariable, widget2.mTop.mSolverVariable, 0, i);
            }
            i2 = 8;
            if (widget2.getVisibility() != 8 && widget2.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                if (widget2.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    flags2[0] = false;
                }
                if (widget2.mDimensionRatio <= 0.0f) {
                    flags2[0] = false;
                    if (count2 + 1 >= this.mMatchConstraintsChainedWidgets.length) {
                        this.mMatchConstraintsChainedWidgets = (ConstraintWidget[]) Arrays.copyOf(this.mMatchConstraintsChainedWidgets, this.mMatchConstraintsChainedWidgets.length * 2);
                    }
                    this.mMatchConstraintsChainedWidgets[count2] = widget2;
                    count2++;
                }
            }
            if (widget2.mBottom.mTarget.mOwner.mTop.mTarget == null || widget2.mBottom.mTarget.mOwner.mTop.mTarget.mOwner != widget2 || widget2.mBottom.mTarget.mOwner == widget2) {
                break;
            }
            widget2 = widget2.mBottom.mTarget.mOwner;
            last3 = widget2;
            i = 5;
        }
        if (!(widget2.mBottom.mTarget == null || widget2.mBottom.mTarget.mOwner == this)) {
            fixedPosition2 = false;
        }
        if (first3.mTop.mTarget == null || last3.mBottom.mTarget == null) {
            c = 1;
            flags2[1] = USE_SNAPSHOT;
        } else {
            c = 1;
        }
        first3.mVerticalChainFixedPosition = fixedPosition2;
        last3.mVerticalNextWidget = null;
        chainEnds[0] = first3;
        chainEnds[2] = firstVisible4;
        chainEnds[c] = last3;
        chainEnds[3] = firstVisible3;
        return count2;
    }
}
