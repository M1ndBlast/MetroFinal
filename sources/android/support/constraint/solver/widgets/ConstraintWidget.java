package android.support.constraint.solver.widgets;

import android.support.constraint.solver.ArrayRow;
import android.support.constraint.solver.Cache;
import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import java.util.ArrayList;

public class ConstraintWidget {
    private static final boolean AUTOTAG_CENTER = false;
    public static final int CHAIN_PACKED = 2;
    public static final int CHAIN_SPREAD = 0;
    public static final int CHAIN_SPREAD_INSIDE = 1;
    public static float DEFAULT_BIAS = 0.5f;
    protected static final int DIRECT = 2;
    public static final int GONE = 8;
    public static final int HORIZONTAL = 0;
    public static final int INVISIBLE = 4;
    public static final int MATCH_CONSTRAINT_SPREAD = 0;
    public static final int MATCH_CONSTRAINT_WRAP = 1;
    protected static final int SOLVER = 1;
    public static final int UNKNOWN = -1;
    public static final int VERTICAL = 1;
    public static final int VISIBLE = 0;
    protected ArrayList<ConstraintAnchor> mAnchors;
    ConstraintAnchor mBaseline;
    int mBaselineDistance;
    ConstraintAnchor mBottom;
    boolean mBottomHasCentered;
    ConstraintAnchor mCenter;
    ConstraintAnchor mCenterX;
    ConstraintAnchor mCenterY;
    private Object mCompanionWidget;
    private int mContainerItemSkip;
    private String mDebugName;
    protected float mDimensionRatio;
    protected int mDimensionRatioSide;
    int mDistToBottom;
    int mDistToLeft;
    int mDistToRight;
    int mDistToTop;
    private int mDrawHeight;
    private int mDrawWidth;
    private int mDrawX;
    private int mDrawY;
    int mHeight;
    float mHorizontalBiasPercent;
    boolean mHorizontalChainFixedPosition;
    int mHorizontalChainStyle;
    DimensionBehaviour mHorizontalDimensionBehaviour;
    ConstraintWidget mHorizontalNextWidget;
    public int mHorizontalResolution;
    float mHorizontalWeight;
    boolean mHorizontalWrapVisited;
    ConstraintAnchor mLeft;
    boolean mLeftHasCentered;
    int mMatchConstraintDefaultHeight;
    int mMatchConstraintDefaultWidth;
    int mMatchConstraintMaxHeight;
    int mMatchConstraintMaxWidth;
    int mMatchConstraintMinHeight;
    int mMatchConstraintMinWidth;
    protected int mMinHeight;
    protected int mMinWidth;
    protected int mOffsetX;
    protected int mOffsetY;
    ConstraintWidget mParent;
    ConstraintAnchor mRight;
    boolean mRightHasCentered;
    private int mSolverBottom;
    private int mSolverLeft;
    private int mSolverRight;
    private int mSolverTop;
    ConstraintAnchor mTop;
    boolean mTopHasCentered;
    private String mType;
    float mVerticalBiasPercent;
    boolean mVerticalChainFixedPosition;
    int mVerticalChainStyle;
    DimensionBehaviour mVerticalDimensionBehaviour;
    ConstraintWidget mVerticalNextWidget;
    public int mVerticalResolution;
    float mVerticalWeight;
    boolean mVerticalWrapVisited;
    private int mVisibility;
    int mWidth;
    private int mWrapHeight;
    private int mWrapWidth;
    protected int mX;
    protected int mY;

    public enum ContentAlignment {
        BEGIN,
        MIDDLE,
        END,
        TOP,
        VERTICAL_MIDDLE,
        BOTTOM,
        LEFT,
        RIGHT
    }

    public enum DimensionBehaviour {
        FIXED,
        WRAP_CONTENT,
        MATCH_CONSTRAINT,
        MATCH_PARENT
    }

    public void reset() {
        this.mLeft.reset();
        this.mTop.reset();
        this.mRight.reset();
        this.mBottom.reset();
        this.mBaseline.reset();
        this.mCenterX.reset();
        this.mCenterY.reset();
        this.mCenter.reset();
        this.mParent = null;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDimensionRatio = 0.0f;
        this.mDimensionRatioSide = -1;
        this.mX = 0;
        this.mY = 0;
        this.mDrawX = 0;
        this.mDrawY = 0;
        this.mDrawWidth = 0;
        this.mDrawHeight = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBaselineDistance = 0;
        this.mMinWidth = 0;
        this.mMinHeight = 0;
        this.mWrapWidth = 0;
        this.mWrapHeight = 0;
        this.mHorizontalBiasPercent = DEFAULT_BIAS;
        this.mVerticalBiasPercent = DEFAULT_BIAS;
        this.mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mCompanionWidget = null;
        this.mContainerItemSkip = 0;
        this.mVisibility = 0;
        this.mDebugName = null;
        this.mType = null;
        this.mHorizontalWrapVisited = false;
        this.mVerticalWrapVisited = false;
        this.mHorizontalChainStyle = 0;
        this.mVerticalChainStyle = 0;
        this.mHorizontalChainFixedPosition = false;
        this.mVerticalChainFixedPosition = false;
        this.mHorizontalWeight = 0.0f;
        this.mVerticalWeight = 0.0f;
        this.mHorizontalResolution = -1;
        this.mVerticalResolution = -1;
    }

    public ConstraintWidget() {
        this.mHorizontalResolution = -1;
        this.mVerticalResolution = -1;
        this.mMatchConstraintDefaultWidth = 0;
        this.mMatchConstraintDefaultHeight = 0;
        this.mMatchConstraintMinWidth = 0;
        this.mMatchConstraintMaxWidth = 0;
        this.mMatchConstraintMinHeight = 0;
        this.mMatchConstraintMaxHeight = 0;
        this.mLeft = new ConstraintAnchor(this, ConstraintAnchor.Type.LEFT);
        this.mTop = new ConstraintAnchor(this, ConstraintAnchor.Type.TOP);
        this.mRight = new ConstraintAnchor(this, ConstraintAnchor.Type.RIGHT);
        this.mBottom = new ConstraintAnchor(this, ConstraintAnchor.Type.BOTTOM);
        this.mBaseline = new ConstraintAnchor(this, ConstraintAnchor.Type.BASELINE);
        this.mCenterX = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER_X);
        this.mCenterY = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER_Y);
        this.mCenter = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER);
        this.mAnchors = new ArrayList<>();
        this.mParent = null;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDimensionRatio = 0.0f;
        this.mDimensionRatioSide = -1;
        this.mSolverLeft = 0;
        this.mSolverTop = 0;
        this.mSolverRight = 0;
        this.mSolverBottom = 0;
        this.mX = 0;
        this.mY = 0;
        this.mDrawX = 0;
        this.mDrawY = 0;
        this.mDrawWidth = 0;
        this.mDrawHeight = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBaselineDistance = 0;
        this.mHorizontalBiasPercent = DEFAULT_BIAS;
        this.mVerticalBiasPercent = DEFAULT_BIAS;
        this.mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mContainerItemSkip = 0;
        this.mVisibility = 0;
        this.mDebugName = null;
        this.mType = null;
        this.mHorizontalChainStyle = 0;
        this.mVerticalChainStyle = 0;
        this.mHorizontalWeight = 0.0f;
        this.mVerticalWeight = 0.0f;
        this.mHorizontalNextWidget = null;
        this.mVerticalNextWidget = null;
        addAnchors();
    }

    public ConstraintWidget(int x, int y, int width, int height) {
        this.mHorizontalResolution = -1;
        this.mVerticalResolution = -1;
        this.mMatchConstraintDefaultWidth = 0;
        this.mMatchConstraintDefaultHeight = 0;
        this.mMatchConstraintMinWidth = 0;
        this.mMatchConstraintMaxWidth = 0;
        this.mMatchConstraintMinHeight = 0;
        this.mMatchConstraintMaxHeight = 0;
        this.mLeft = new ConstraintAnchor(this, ConstraintAnchor.Type.LEFT);
        this.mTop = new ConstraintAnchor(this, ConstraintAnchor.Type.TOP);
        this.mRight = new ConstraintAnchor(this, ConstraintAnchor.Type.RIGHT);
        this.mBottom = new ConstraintAnchor(this, ConstraintAnchor.Type.BOTTOM);
        this.mBaseline = new ConstraintAnchor(this, ConstraintAnchor.Type.BASELINE);
        this.mCenterX = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER_X);
        this.mCenterY = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER_Y);
        this.mCenter = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER);
        this.mAnchors = new ArrayList<>();
        this.mParent = null;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDimensionRatio = 0.0f;
        this.mDimensionRatioSide = -1;
        this.mSolverLeft = 0;
        this.mSolverTop = 0;
        this.mSolverRight = 0;
        this.mSolverBottom = 0;
        this.mX = 0;
        this.mY = 0;
        this.mDrawX = 0;
        this.mDrawY = 0;
        this.mDrawWidth = 0;
        this.mDrawHeight = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBaselineDistance = 0;
        this.mHorizontalBiasPercent = DEFAULT_BIAS;
        this.mVerticalBiasPercent = DEFAULT_BIAS;
        this.mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mContainerItemSkip = 0;
        this.mVisibility = 0;
        this.mDebugName = null;
        this.mType = null;
        this.mHorizontalChainStyle = 0;
        this.mVerticalChainStyle = 0;
        this.mHorizontalWeight = 0.0f;
        this.mVerticalWeight = 0.0f;
        this.mHorizontalNextWidget = null;
        this.mVerticalNextWidget = null;
        this.mX = x;
        this.mY = y;
        this.mWidth = width;
        this.mHeight = height;
        addAnchors();
        forceUpdateDrawPosition();
    }

    public ConstraintWidget(int width, int height) {
        this(0, 0, width, height);
    }

    public void resetSolverVariables(Cache cache) {
        this.mLeft.resetSolverVariable(cache);
        this.mTop.resetSolverVariable(cache);
        this.mRight.resetSolverVariable(cache);
        this.mBottom.resetSolverVariable(cache);
        this.mBaseline.resetSolverVariable(cache);
        this.mCenter.resetSolverVariable(cache);
        this.mCenterX.resetSolverVariable(cache);
        this.mCenterY.resetSolverVariable(cache);
    }

    public void resetGroups() {
        int numAnchors = this.mAnchors.size();
        for (int i = 0; i < numAnchors; i++) {
            this.mAnchors.get(i).mGroup = Integer.MAX_VALUE;
        }
    }

    private void addAnchors() {
        this.mAnchors.add(this.mLeft);
        this.mAnchors.add(this.mTop);
        this.mAnchors.add(this.mRight);
        this.mAnchors.add(this.mBottom);
        this.mAnchors.add(this.mCenterX);
        this.mAnchors.add(this.mCenterY);
        this.mAnchors.add(this.mBaseline);
    }

    public boolean isRoot() {
        return this.mParent == null;
    }

    public boolean isRootContainer() {
        return (this instanceof ConstraintWidgetContainer) && (this.mParent == null || !(this.mParent instanceof ConstraintWidgetContainer));
    }

    public boolean isInsideConstraintLayout() {
        ConstraintWidget widget = getParent();
        if (widget == null) {
            return false;
        }
        while (widget != null) {
            if (widget instanceof ConstraintWidgetContainer) {
                return true;
            }
            widget = widget.getParent();
        }
        return false;
    }

    public boolean hasAncestor(ConstraintWidget widget) {
        ConstraintWidget parent = getParent();
        if (parent == widget) {
            return true;
        }
        if (parent == widget.getParent()) {
            return false;
        }
        while (parent != null) {
            if (parent == widget || parent == widget.getParent()) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    public WidgetContainer getRootWidgetContainer() {
        ConstraintWidget root = this;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        if (root instanceof WidgetContainer) {
            return (WidgetContainer) root;
        }
        return null;
    }

    public ConstraintWidget getParent() {
        return this.mParent;
    }

    public void setParent(ConstraintWidget widget) {
        this.mParent = widget;
    }

    public String getType() {
        return this.mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public void setVisibility(int visibility) {
        this.mVisibility = visibility;
    }

    public int getVisibility() {
        return this.mVisibility;
    }

    public String getDebugName() {
        return this.mDebugName;
    }

    public void setDebugName(String name) {
        this.mDebugName = name;
    }

    public void setDebugSolverName(LinearSystem system, String name) {
        this.mDebugName = name;
        SolverVariable left = system.createObjectVariable(this.mLeft);
        SolverVariable top = system.createObjectVariable(this.mTop);
        SolverVariable right = system.createObjectVariable(this.mRight);
        SolverVariable bottom = system.createObjectVariable(this.mBottom);
        left.setName(name + ".left");
        top.setName(name + ".top");
        right.setName(name + ".right");
        bottom.setName(name + ".bottom");
        if (this.mBaselineDistance > 0) {
            SolverVariable baseline = system.createObjectVariable(this.mBaseline);
            baseline.setName(name + ".baseline");
        }
    }

    public String toString() {
        String str;
        String str2;
        StringBuilder sb = new StringBuilder();
        if (this.mType != null) {
            str = "type: " + this.mType + " ";
        } else {
            str = "";
        }
        sb.append(str);
        if (this.mDebugName != null) {
            str2 = "id: " + this.mDebugName + " ";
        } else {
            str2 = "";
        }
        sb.append(str2);
        sb.append("(");
        sb.append(this.mX);
        sb.append(", ");
        sb.append(this.mY);
        sb.append(") - (");
        sb.append(this.mWidth);
        sb.append(" x ");
        sb.append(this.mHeight);
        sb.append(")");
        sb.append(" wrap: (");
        sb.append(this.mWrapWidth);
        sb.append(" x ");
        sb.append(this.mWrapHeight);
        sb.append(")");
        return sb.toString();
    }

    /* access modifiers changed from: package-private */
    public int getInternalDrawX() {
        return this.mDrawX;
    }

    /* access modifiers changed from: package-private */
    public int getInternalDrawY() {
        return this.mDrawY;
    }

    public int getInternalDrawRight() {
        return this.mDrawX + this.mDrawWidth;
    }

    public int getInternalDrawBottom() {
        return this.mDrawY + this.mDrawHeight;
    }

    public int getX() {
        return this.mX;
    }

    public int getY() {
        return this.mY;
    }

    public int getWidth() {
        if (this.mVisibility == 8) {
            return 0;
        }
        return this.mWidth;
    }

    public int getOptimizerWrapWidth() {
        int w;
        int w2 = this.mWidth;
        if (this.mHorizontalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
            return w2;
        }
        if (this.mMatchConstraintDefaultWidth == 1) {
            w = Math.max(this.mMatchConstraintMinWidth, w2);
        } else if (this.mMatchConstraintMinWidth > 0) {
            w = this.mMatchConstraintMinWidth;
            this.mWidth = w;
        } else {
            w = 0;
        }
        if (this.mMatchConstraintMaxWidth <= 0 || this.mMatchConstraintMaxWidth >= w) {
            return w;
        }
        return this.mMatchConstraintMaxWidth;
    }

    public int getOptimizerWrapHeight() {
        int h;
        int h2 = this.mHeight;
        if (this.mVerticalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
            return h2;
        }
        if (this.mMatchConstraintDefaultHeight == 1) {
            h = Math.max(this.mMatchConstraintMinHeight, h2);
        } else if (this.mMatchConstraintMinHeight > 0) {
            h = this.mMatchConstraintMinHeight;
            this.mHeight = h;
        } else {
            h = 0;
        }
        if (this.mMatchConstraintMaxHeight <= 0 || this.mMatchConstraintMaxHeight >= h) {
            return h;
        }
        return this.mMatchConstraintMaxHeight;
    }

    public int getWrapWidth() {
        return this.mWrapWidth;
    }

    public int getHeight() {
        if (this.mVisibility == 8) {
            return 0;
        }
        return this.mHeight;
    }

    public int getWrapHeight() {
        return this.mWrapHeight;
    }

    public int getDrawX() {
        return this.mDrawX + this.mOffsetX;
    }

    public int getDrawY() {
        return this.mDrawY + this.mOffsetY;
    }

    public int getDrawWidth() {
        return this.mDrawWidth;
    }

    public int getDrawHeight() {
        return this.mDrawHeight;
    }

    public int getDrawBottom() {
        return getDrawY() + this.mDrawHeight;
    }

    public int getDrawRight() {
        return getDrawX() + this.mDrawWidth;
    }

    /* access modifiers changed from: protected */
    public int getRootX() {
        return this.mX + this.mOffsetX;
    }

    /* access modifiers changed from: protected */
    public int getRootY() {
        return this.mY + this.mOffsetY;
    }

    public int getMinWidth() {
        return this.mMinWidth;
    }

    public int getMinHeight() {
        return this.mMinHeight;
    }

    public int getLeft() {
        return getX();
    }

    public int getTop() {
        return getY();
    }

    public int getRight() {
        return getX() + this.mWidth;
    }

    public int getBottom() {
        return getY() + this.mHeight;
    }

    public float getHorizontalBiasPercent() {
        return this.mHorizontalBiasPercent;
    }

    public float getVerticalBiasPercent() {
        return this.mVerticalBiasPercent;
    }

    public boolean hasBaseline() {
        return this.mBaselineDistance > 0;
    }

    public int getBaselineDistance() {
        return this.mBaselineDistance;
    }

    public Object getCompanionWidget() {
        return this.mCompanionWidget;
    }

    public ArrayList<ConstraintAnchor> getAnchors() {
        return this.mAnchors;
    }

    public void setX(int x) {
        this.mX = x;
    }

    public void setY(int y) {
        this.mY = y;
    }

    public void setOrigin(int x, int y) {
        this.mX = x;
        this.mY = y;
    }

    public void setOffset(int x, int y) {
        this.mOffsetX = x;
        this.mOffsetY = y;
    }

    public void setGoneMargin(ConstraintAnchor.Type type, int goneMargin) {
        switch (type) {
            case LEFT:
                this.mLeft.mGoneMargin = goneMargin;
                return;
            case TOP:
                this.mTop.mGoneMargin = goneMargin;
                return;
            case RIGHT:
                this.mRight.mGoneMargin = goneMargin;
                return;
            case BOTTOM:
                this.mBottom.mGoneMargin = goneMargin;
                return;
            default:
                return;
        }
    }

    public void updateDrawPosition() {
        int left = this.mX;
        int top = this.mY;
        int right = this.mX + this.mWidth;
        int bottom = this.mY + this.mHeight;
        this.mDrawX = left;
        this.mDrawY = top;
        this.mDrawWidth = right - left;
        this.mDrawHeight = bottom - top;
    }

    public void forceUpdateDrawPosition() {
        int left = this.mX;
        int top = this.mY;
        int right = this.mX + this.mWidth;
        int bottom = this.mY + this.mHeight;
        this.mDrawX = left;
        this.mDrawY = top;
        this.mDrawWidth = right - left;
        this.mDrawHeight = bottom - top;
    }

    public void setDrawOrigin(int x, int y) {
        this.mDrawX = x - this.mOffsetX;
        this.mDrawY = y - this.mOffsetY;
        this.mX = this.mDrawX;
        this.mY = this.mDrawY;
    }

    public void setDrawX(int x) {
        this.mDrawX = x - this.mOffsetX;
        this.mX = this.mDrawX;
    }

    public void setDrawY(int y) {
        this.mDrawY = y - this.mOffsetY;
        this.mY = this.mDrawY;
    }

    public void setDrawWidth(int drawWidth) {
        this.mDrawWidth = drawWidth;
    }

    public void setDrawHeight(int drawHeight) {
        this.mDrawHeight = drawHeight;
    }

    public void setWidth(int w) {
        this.mWidth = w;
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
    }

    public void setHeight(int h) {
        this.mHeight = h;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
    }

    public void setHorizontalMatchStyle(int horizontalMatchStyle, int min, int max) {
        this.mMatchConstraintDefaultWidth = horizontalMatchStyle;
        this.mMatchConstraintMinWidth = min;
        this.mMatchConstraintMaxWidth = max;
    }

    public void setVerticalMatchStyle(int verticalMatchStyle, int min, int max) {
        this.mMatchConstraintDefaultHeight = verticalMatchStyle;
        this.mMatchConstraintMinHeight = min;
        this.mMatchConstraintMaxHeight = max;
    }

    public void setDimensionRatio(String ratio) {
        int commaIndex;
        if (ratio == null || ratio.length() == 0) {
            this.mDimensionRatio = 0.0f;
            return;
        }
        int dimensionRatioSide = -1;
        float dimensionRatio = 0.0f;
        int len = ratio.length();
        int commaIndex2 = ratio.indexOf(44);
        if (commaIndex2 <= 0 || commaIndex2 >= len - 1) {
            commaIndex = 0;
        } else {
            String dimension = ratio.substring(0, commaIndex2);
            if (dimension.equalsIgnoreCase("W")) {
                dimensionRatioSide = 0;
            } else if (dimension.equalsIgnoreCase("H")) {
                dimensionRatioSide = 1;
            }
            commaIndex = commaIndex2 + 1;
        }
        int colonIndex = ratio.indexOf(58);
        if (colonIndex < 0 || colonIndex >= len - 1) {
            String r = ratio.substring(commaIndex);
            if (r.length() > 0) {
                try {
                    dimensionRatio = Float.parseFloat(r);
                } catch (NumberFormatException e) {
                }
            }
        } else {
            String nominator = ratio.substring(commaIndex, colonIndex);
            String denominator = ratio.substring(colonIndex + 1);
            if (nominator.length() > 0 && denominator.length() > 0) {
                try {
                    float nominatorValue = Float.parseFloat(nominator);
                    float denominatorValue = Float.parseFloat(denominator);
                    if (nominatorValue > 0.0f && denominatorValue > 0.0f) {
                        dimensionRatio = dimensionRatioSide == 1 ? Math.abs(denominatorValue / nominatorValue) : Math.abs(nominatorValue / denominatorValue);
                    }
                } catch (NumberFormatException e2) {
                }
            }
        }
        if (dimensionRatio > 0.0f) {
            this.mDimensionRatio = dimensionRatio;
            this.mDimensionRatioSide = dimensionRatioSide;
        }
    }

    public void setDimensionRatio(float ratio, int dimensionRatioSide) {
        this.mDimensionRatio = ratio;
        this.mDimensionRatioSide = dimensionRatioSide;
    }

    public float getDimensionRatio() {
        return this.mDimensionRatio;
    }

    public int getDimensionRatioSide() {
        return this.mDimensionRatioSide;
    }

    public void setHorizontalBiasPercent(float horizontalBiasPercent) {
        this.mHorizontalBiasPercent = horizontalBiasPercent;
    }

    public void setVerticalBiasPercent(float verticalBiasPercent) {
        this.mVerticalBiasPercent = verticalBiasPercent;
    }

    public void setMinWidth(int w) {
        if (w < 0) {
            this.mMinWidth = 0;
        } else {
            this.mMinWidth = w;
        }
    }

    public void setMinHeight(int h) {
        if (h < 0) {
            this.mMinHeight = 0;
        } else {
            this.mMinHeight = h;
        }
    }

    public void setWrapWidth(int w) {
        this.mWrapWidth = w;
    }

    public void setWrapHeight(int h) {
        this.mWrapHeight = h;
    }

    public void setDimension(int w, int h) {
        this.mWidth = w;
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
        this.mHeight = h;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
    }

    public void setFrame(int left, int top, int right, int bottom) {
        int w = right - left;
        int h = bottom - top;
        this.mX = left;
        this.mY = top;
        if (this.mVisibility == 8) {
            this.mWidth = 0;
            this.mHeight = 0;
            return;
        }
        if (this.mHorizontalDimensionBehaviour == DimensionBehaviour.FIXED && w < this.mWidth) {
            w = this.mWidth;
        }
        if (this.mVerticalDimensionBehaviour == DimensionBehaviour.FIXED && h < this.mHeight) {
            h = this.mHeight;
        }
        this.mWidth = w;
        this.mHeight = h;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
    }

    public void setHorizontalDimension(int left, int right) {
        this.mX = left;
        this.mWidth = right - left;
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
    }

    public void setVerticalDimension(int top, int bottom) {
        this.mY = top;
        this.mHeight = bottom - top;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
    }

    public void setBaselineDistance(int baseline) {
        this.mBaselineDistance = baseline;
    }

    public void setCompanionWidget(Object companion) {
        this.mCompanionWidget = companion;
    }

    public void setContainerItemSkip(int skip) {
        if (skip >= 0) {
            this.mContainerItemSkip = skip;
        } else {
            this.mContainerItemSkip = 0;
        }
    }

    public int getContainerItemSkip() {
        return this.mContainerItemSkip;
    }

    public void setHorizontalWeight(float horizontalWeight) {
        this.mHorizontalWeight = horizontalWeight;
    }

    public void setVerticalWeight(float verticalWeight) {
        this.mVerticalWeight = verticalWeight;
    }

    public void setHorizontalChainStyle(int horizontalChainStyle) {
        this.mHorizontalChainStyle = horizontalChainStyle;
    }

    public int getHorizontalChainStyle() {
        return this.mHorizontalChainStyle;
    }

    public void setVerticalChainStyle(int verticalChainStyle) {
        this.mVerticalChainStyle = verticalChainStyle;
    }

    public int getVerticalChainStyle() {
        return this.mVerticalChainStyle;
    }

    public void connectedTo(ConstraintWidget source) {
    }

    public void immediateConnect(ConstraintAnchor.Type startType, ConstraintWidget target, ConstraintAnchor.Type endType, int margin, int goneMargin) {
        ConstraintAnchor startAnchor = getAnchor(startType);
        startAnchor.connect(target.getAnchor(endType), margin, goneMargin, ConstraintAnchor.Strength.STRONG, 0, true);
    }

    public void connect(ConstraintAnchor from, ConstraintAnchor to, int margin, int creator) {
        connect(from, to, margin, ConstraintAnchor.Strength.STRONG, creator);
    }

    public void connect(ConstraintAnchor from, ConstraintAnchor to, int margin) {
        connect(from, to, margin, ConstraintAnchor.Strength.STRONG, 0);
    }

    public void connect(ConstraintAnchor from, ConstraintAnchor to, int margin, ConstraintAnchor.Strength strength, int creator) {
        if (from.getOwner() == this) {
            connect(from.getType(), to.getOwner(), to.getType(), margin, strength, creator);
        }
    }

    public void connect(ConstraintAnchor.Type constraintFrom, ConstraintWidget target, ConstraintAnchor.Type constraintTo, int margin) {
        connect(constraintFrom, target, constraintTo, margin, ConstraintAnchor.Strength.STRONG);
    }

    public void connect(ConstraintAnchor.Type constraintFrom, ConstraintWidget target, ConstraintAnchor.Type constraintTo) {
        connect(constraintFrom, target, constraintTo, 0, ConstraintAnchor.Strength.STRONG);
    }

    public void connect(ConstraintAnchor.Type constraintFrom, ConstraintWidget target, ConstraintAnchor.Type constraintTo, int margin, ConstraintAnchor.Strength strength) {
        connect(constraintFrom, target, constraintTo, margin, strength, 0);
    }

    public void connect(ConstraintAnchor.Type constraintFrom, ConstraintWidget target, ConstraintAnchor.Type constraintTo, int margin, ConstraintAnchor.Strength strength, int creator) {
        int margin2;
        ConstraintAnchor bottom;
        ConstraintAnchor.Type type = constraintFrom;
        ConstraintWidget constraintWidget = target;
        ConstraintAnchor.Type type2 = constraintTo;
        int i = creator;
        if (type == ConstraintAnchor.Type.CENTER) {
            if (type2 == ConstraintAnchor.Type.CENTER) {
                ConstraintAnchor left = getAnchor(ConstraintAnchor.Type.LEFT);
                ConstraintAnchor right = getAnchor(ConstraintAnchor.Type.RIGHT);
                ConstraintAnchor top = getAnchor(ConstraintAnchor.Type.TOP);
                ConstraintAnchor bottom2 = getAnchor(ConstraintAnchor.Type.BOTTOM);
                boolean centerX = false;
                boolean centerY = false;
                if ((left == null || !left.isConnected()) && (right == null || !right.isConnected())) {
                    ConstraintWidget constraintWidget2 = constraintWidget;
                    ConstraintAnchor.Strength strength2 = strength;
                    bottom = bottom2;
                    int i2 = i;
                    connect(ConstraintAnchor.Type.LEFT, constraintWidget2, ConstraintAnchor.Type.LEFT, 0, strength2, i2);
                    connect(ConstraintAnchor.Type.RIGHT, constraintWidget2, ConstraintAnchor.Type.RIGHT, 0, strength2, i2);
                    centerX = true;
                } else {
                    bottom = bottom2;
                }
                if ((top == null || !top.isConnected()) && (bottom == null || !bottom.isConnected())) {
                    ConstraintWidget constraintWidget3 = constraintWidget;
                    ConstraintAnchor.Strength strength3 = strength;
                    int i3 = i;
                    connect(ConstraintAnchor.Type.TOP, constraintWidget3, ConstraintAnchor.Type.TOP, 0, strength3, i3);
                    connect(ConstraintAnchor.Type.BOTTOM, constraintWidget3, ConstraintAnchor.Type.BOTTOM, 0, strength3, i3);
                    centerY = true;
                }
                if (centerX && centerY) {
                    getAnchor(ConstraintAnchor.Type.CENTER).connect(constraintWidget.getAnchor(ConstraintAnchor.Type.CENTER), 0, i);
                } else if (centerX) {
                    getAnchor(ConstraintAnchor.Type.CENTER_X).connect(constraintWidget.getAnchor(ConstraintAnchor.Type.CENTER_X), 0, i);
                } else if (centerY) {
                    getAnchor(ConstraintAnchor.Type.CENTER_Y).connect(constraintWidget.getAnchor(ConstraintAnchor.Type.CENTER_Y), 0, i);
                }
            } else if (type2 == ConstraintAnchor.Type.LEFT || type2 == ConstraintAnchor.Type.RIGHT) {
                ConstraintWidget constraintWidget4 = constraintWidget;
                ConstraintAnchor.Type type3 = type2;
                ConstraintAnchor.Strength strength4 = strength;
                int i4 = i;
                connect(ConstraintAnchor.Type.LEFT, constraintWidget4, type3, 0, strength4, i4);
                connect(ConstraintAnchor.Type.RIGHT, constraintWidget4, type3, 0, strength4, i4);
                getAnchor(ConstraintAnchor.Type.CENTER).connect(target.getAnchor(constraintTo), 0, i);
            } else if (type2 == ConstraintAnchor.Type.TOP || type2 == ConstraintAnchor.Type.BOTTOM) {
                ConstraintWidget constraintWidget5 = constraintWidget;
                ConstraintAnchor.Type type4 = type2;
                ConstraintAnchor.Strength strength5 = strength;
                int i5 = i;
                connect(ConstraintAnchor.Type.TOP, constraintWidget5, type4, 0, strength5, i5);
                connect(ConstraintAnchor.Type.BOTTOM, constraintWidget5, type4, 0, strength5, i5);
                getAnchor(ConstraintAnchor.Type.CENTER).connect(target.getAnchor(constraintTo), 0, i);
            }
        } else if (type == ConstraintAnchor.Type.CENTER_X && (type2 == ConstraintAnchor.Type.LEFT || type2 == ConstraintAnchor.Type.RIGHT)) {
            ConstraintAnchor left2 = getAnchor(ConstraintAnchor.Type.LEFT);
            ConstraintAnchor targetAnchor = target.getAnchor(constraintTo);
            ConstraintAnchor right2 = getAnchor(ConstraintAnchor.Type.RIGHT);
            left2.connect(targetAnchor, 0, i);
            right2.connect(targetAnchor, 0, i);
            getAnchor(ConstraintAnchor.Type.CENTER_X).connect(targetAnchor, 0, i);
        } else if (type == ConstraintAnchor.Type.CENTER_Y && (type2 == ConstraintAnchor.Type.TOP || type2 == ConstraintAnchor.Type.BOTTOM)) {
            ConstraintAnchor targetAnchor2 = target.getAnchor(constraintTo);
            getAnchor(ConstraintAnchor.Type.TOP).connect(targetAnchor2, 0, i);
            getAnchor(ConstraintAnchor.Type.BOTTOM).connect(targetAnchor2, 0, i);
            getAnchor(ConstraintAnchor.Type.CENTER_Y).connect(targetAnchor2, 0, i);
        } else if (type == ConstraintAnchor.Type.CENTER_X && type2 == ConstraintAnchor.Type.CENTER_X) {
            getAnchor(ConstraintAnchor.Type.LEFT).connect(constraintWidget.getAnchor(ConstraintAnchor.Type.LEFT), 0, i);
            getAnchor(ConstraintAnchor.Type.RIGHT).connect(constraintWidget.getAnchor(ConstraintAnchor.Type.RIGHT), 0, i);
            getAnchor(ConstraintAnchor.Type.CENTER_X).connect(target.getAnchor(constraintTo), 0, i);
        } else if (type == ConstraintAnchor.Type.CENTER_Y && type2 == ConstraintAnchor.Type.CENTER_Y) {
            getAnchor(ConstraintAnchor.Type.TOP).connect(constraintWidget.getAnchor(ConstraintAnchor.Type.TOP), 0, i);
            getAnchor(ConstraintAnchor.Type.BOTTOM).connect(constraintWidget.getAnchor(ConstraintAnchor.Type.BOTTOM), 0, i);
            getAnchor(ConstraintAnchor.Type.CENTER_Y).connect(target.getAnchor(constraintTo), 0, i);
        } else {
            ConstraintAnchor fromAnchor = getAnchor(constraintFrom);
            ConstraintAnchor toAnchor = target.getAnchor(constraintTo);
            if (fromAnchor.isValidConnection(toAnchor)) {
                if (type == ConstraintAnchor.Type.BASELINE) {
                    ConstraintAnchor top2 = getAnchor(ConstraintAnchor.Type.TOP);
                    ConstraintAnchor bottom3 = getAnchor(ConstraintAnchor.Type.BOTTOM);
                    if (top2 != null) {
                        top2.reset();
                    }
                    if (bottom3 != null) {
                        bottom3.reset();
                    }
                    margin2 = 0;
                } else {
                    if (type == ConstraintAnchor.Type.TOP || type == ConstraintAnchor.Type.BOTTOM) {
                        ConstraintAnchor baseline = getAnchor(ConstraintAnchor.Type.BASELINE);
                        if (baseline != null) {
                            baseline.reset();
                        }
                        ConstraintAnchor center = getAnchor(ConstraintAnchor.Type.CENTER);
                        if (center.getTarget() != toAnchor) {
                            center.reset();
                        }
                        ConstraintAnchor opposite = getAnchor(constraintFrom).getOpposite();
                        ConstraintAnchor centerY2 = getAnchor(ConstraintAnchor.Type.CENTER_Y);
                        if (centerY2.isConnected()) {
                            opposite.reset();
                            centerY2.reset();
                        }
                    } else if (type == ConstraintAnchor.Type.LEFT || type == ConstraintAnchor.Type.RIGHT) {
                        ConstraintAnchor center2 = getAnchor(ConstraintAnchor.Type.CENTER);
                        if (center2.getTarget() != toAnchor) {
                            center2.reset();
                        }
                        ConstraintAnchor opposite2 = getAnchor(constraintFrom).getOpposite();
                        ConstraintAnchor centerX2 = getAnchor(ConstraintAnchor.Type.CENTER_X);
                        if (centerX2.isConnected()) {
                            opposite2.reset();
                            centerX2.reset();
                        }
                    }
                    margin2 = margin;
                }
                fromAnchor.connect(toAnchor, margin2, strength, i);
                toAnchor.getOwner().connectedTo(fromAnchor.getOwner());
                return;
            }
        }
        ConstraintAnchor.Strength strength6 = strength;
    }

    public void resetAllConstraints() {
        resetAnchors();
        setVerticalBiasPercent(DEFAULT_BIAS);
        setHorizontalBiasPercent(DEFAULT_BIAS);
        if (!(this instanceof ConstraintWidgetContainer)) {
            if (getHorizontalDimensionBehaviour() == DimensionBehaviour.MATCH_CONSTRAINT) {
                if (getWidth() == getWrapWidth()) {
                    setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
                } else if (getWidth() > getMinWidth()) {
                    setHorizontalDimensionBehaviour(DimensionBehaviour.FIXED);
                }
            }
            if (getVerticalDimensionBehaviour() != DimensionBehaviour.MATCH_CONSTRAINT) {
                return;
            }
            if (getHeight() == getWrapHeight()) {
                setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
            } else if (getHeight() > getMinHeight()) {
                setVerticalDimensionBehaviour(DimensionBehaviour.FIXED);
            }
        }
    }

    public void resetAnchor(ConstraintAnchor anchor) {
        if (getParent() == null || !(getParent() instanceof ConstraintWidgetContainer) || !((ConstraintWidgetContainer) getParent()).handlesInternalConstraints()) {
            ConstraintAnchor left = getAnchor(ConstraintAnchor.Type.LEFT);
            ConstraintAnchor right = getAnchor(ConstraintAnchor.Type.RIGHT);
            ConstraintAnchor top = getAnchor(ConstraintAnchor.Type.TOP);
            ConstraintAnchor bottom = getAnchor(ConstraintAnchor.Type.BOTTOM);
            ConstraintAnchor center = getAnchor(ConstraintAnchor.Type.CENTER);
            ConstraintAnchor centerX = getAnchor(ConstraintAnchor.Type.CENTER_X);
            ConstraintAnchor centerY = getAnchor(ConstraintAnchor.Type.CENTER_Y);
            if (anchor == center) {
                if (left.isConnected() && right.isConnected() && left.getTarget() == right.getTarget()) {
                    left.reset();
                    right.reset();
                }
                if (top.isConnected() && bottom.isConnected() && top.getTarget() == bottom.getTarget()) {
                    top.reset();
                    bottom.reset();
                }
                this.mHorizontalBiasPercent = 0.5f;
                this.mVerticalBiasPercent = 0.5f;
            } else if (anchor == centerX) {
                if (left.isConnected() && right.isConnected() && left.getTarget().getOwner() == right.getTarget().getOwner()) {
                    left.reset();
                    right.reset();
                }
                this.mHorizontalBiasPercent = 0.5f;
            } else if (anchor == centerY) {
                if (top.isConnected() && bottom.isConnected() && top.getTarget().getOwner() == bottom.getTarget().getOwner()) {
                    top.reset();
                    bottom.reset();
                }
                this.mVerticalBiasPercent = 0.5f;
            } else if (anchor == left || anchor == right) {
                if (left.isConnected() && left.getTarget() == right.getTarget()) {
                    center.reset();
                }
            } else if ((anchor == top || anchor == bottom) && top.isConnected() && top.getTarget() == bottom.getTarget()) {
                center.reset();
            }
            anchor.reset();
        }
    }

    public void resetAnchors() {
        ConstraintWidget parent = getParent();
        if (parent == null || !(parent instanceof ConstraintWidgetContainer) || !((ConstraintWidgetContainer) getParent()).handlesInternalConstraints()) {
            int mAnchorsSize = this.mAnchors.size();
            for (int i = 0; i < mAnchorsSize; i++) {
                this.mAnchors.get(i).reset();
            }
        }
    }

    public void resetAnchors(int connectionCreator) {
        ConstraintWidget parent = getParent();
        if (parent == null || !(parent instanceof ConstraintWidgetContainer) || !((ConstraintWidgetContainer) getParent()).handlesInternalConstraints()) {
            int mAnchorsSize = this.mAnchors.size();
            for (int i = 0; i < mAnchorsSize; i++) {
                ConstraintAnchor anchor = this.mAnchors.get(i);
                if (connectionCreator == anchor.getConnectionCreator()) {
                    if (anchor.isVerticalAnchor()) {
                        setVerticalBiasPercent(DEFAULT_BIAS);
                    } else {
                        setHorizontalBiasPercent(DEFAULT_BIAS);
                    }
                    anchor.reset();
                }
            }
        }
    }

    public void disconnectWidget(ConstraintWidget widget) {
        ArrayList<ConstraintAnchor> anchors = getAnchors();
        int anchorsSize = anchors.size();
        for (int i = 0; i < anchorsSize; i++) {
            ConstraintAnchor anchor = anchors.get(i);
            if (anchor.isConnected() && anchor.getTarget().getOwner() == widget) {
                anchor.reset();
            }
        }
    }

    public void disconnectUnlockedWidget(ConstraintWidget widget) {
        ArrayList<ConstraintAnchor> anchors = getAnchors();
        int anchorsSize = anchors.size();
        for (int i = 0; i < anchorsSize; i++) {
            ConstraintAnchor anchor = anchors.get(i);
            if (anchor.isConnected() && anchor.getTarget().getOwner() == widget && anchor.getConnectionCreator() == 2) {
                anchor.reset();
            }
        }
    }

    public ConstraintAnchor getAnchor(ConstraintAnchor.Type anchorType) {
        switch (anchorType) {
            case LEFT:
                return this.mLeft;
            case TOP:
                return this.mTop;
            case RIGHT:
                return this.mRight;
            case BOTTOM:
                return this.mBottom;
            case BASELINE:
                return this.mBaseline;
            case CENTER_X:
                return this.mCenterX;
            case CENTER_Y:
                return this.mCenterY;
            case CENTER:
                return this.mCenter;
            default:
                return null;
        }
    }

    public DimensionBehaviour getHorizontalDimensionBehaviour() {
        return this.mHorizontalDimensionBehaviour;
    }

    public DimensionBehaviour getVerticalDimensionBehaviour() {
        return this.mVerticalDimensionBehaviour;
    }

    public void setHorizontalDimensionBehaviour(DimensionBehaviour behaviour) {
        this.mHorizontalDimensionBehaviour = behaviour;
        if (this.mHorizontalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
            setWidth(this.mWrapWidth);
        }
    }

    public void setVerticalDimensionBehaviour(DimensionBehaviour behaviour) {
        this.mVerticalDimensionBehaviour = behaviour;
        if (this.mVerticalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
            setHeight(this.mWrapHeight);
        }
    }

    public boolean isInHorizontalChain() {
        if (this.mLeft.mTarget != null && this.mLeft.mTarget.mTarget == this.mLeft) {
            return true;
        }
        if (this.mRight.mTarget == null || this.mRight.mTarget.mTarget != this.mRight) {
            return false;
        }
        return true;
    }

    public ConstraintWidget getHorizontalChainControlWidget() {
        if (!isInHorizontalChain()) {
            return null;
        }
        ConstraintWidget found = null;
        ConstraintWidget tmp = this;
        while (found == null && tmp != null) {
            ConstraintAnchor anchor = tmp.getAnchor(ConstraintAnchor.Type.LEFT);
            ConstraintAnchor targetAnchor = null;
            ConstraintAnchor targetOwner = anchor == null ? null : anchor.getTarget();
            ConstraintWidget target = targetOwner == null ? null : targetOwner.getOwner();
            if (target == getParent()) {
                return tmp;
            }
            if (target != null) {
                targetAnchor = target.getAnchor(ConstraintAnchor.Type.RIGHT).getTarget();
            }
            if (targetAnchor == null || targetAnchor.getOwner() == tmp) {
                tmp = target;
            } else {
                found = tmp;
            }
        }
        return found;
    }

    public boolean isInVerticalChain() {
        if (this.mTop.mTarget != null && this.mTop.mTarget.mTarget == this.mTop) {
            return true;
        }
        if (this.mBottom.mTarget == null || this.mBottom.mTarget.mTarget != this.mBottom) {
            return false;
        }
        return true;
    }

    public ConstraintWidget getVerticalChainControlWidget() {
        if (!isInVerticalChain()) {
            return null;
        }
        ConstraintWidget found = null;
        ConstraintWidget tmp = this;
        while (found == null && tmp != null) {
            ConstraintAnchor anchor = tmp.getAnchor(ConstraintAnchor.Type.TOP);
            ConstraintAnchor targetAnchor = null;
            ConstraintAnchor targetOwner = anchor == null ? null : anchor.getTarget();
            ConstraintWidget target = targetOwner == null ? null : targetOwner.getOwner();
            if (target == getParent()) {
                return tmp;
            }
            if (target != null) {
                targetAnchor = target.getAnchor(ConstraintAnchor.Type.BOTTOM).getTarget();
            }
            if (targetAnchor == null || targetAnchor.getOwner() == tmp) {
                tmp = target;
            } else {
                found = tmp;
            }
        }
        return found;
    }

    public void addToSolver(LinearSystem system) {
        addToSolver(system, Integer.MAX_VALUE);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:206:0x03e3, code lost:
        if (r14 == -1) goto L_0x03e7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02c1  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x02c3  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x02ca  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x03b6  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03cb A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x03cc  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0547  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x05ac  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addToSolver(android.support.constraint.solver.LinearSystem r49, int r50) {
        /*
            r48 = this;
            r15 = r48
            r14 = r49
            r13 = r50
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r12 = 2147483647(0x7fffffff, float:NaN)
            if (r13 == r12) goto L_0x0016
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r15.mLeft
            int r5 = r5.mGroup
            if (r5 != r13) goto L_0x001c
        L_0x0016:
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r15.mLeft
            android.support.constraint.solver.SolverVariable r0 = r14.createObjectVariable(r5)
        L_0x001c:
            if (r13 == r12) goto L_0x0024
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r15.mRight
            int r5 = r5.mGroup
            if (r5 != r13) goto L_0x002a
        L_0x0024:
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r15.mRight
            android.support.constraint.solver.SolverVariable r1 = r14.createObjectVariable(r5)
        L_0x002a:
            if (r13 == r12) goto L_0x0035
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r15.mTop
            int r5 = r5.mGroup
            if (r5 != r13) goto L_0x0033
            goto L_0x0035
        L_0x0033:
            r11 = r2
            goto L_0x003c
        L_0x0035:
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r15.mTop
            android.support.constraint.solver.SolverVariable r2 = r14.createObjectVariable(r5)
            goto L_0x0033
        L_0x003c:
            if (r13 == r12) goto L_0x0047
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mBottom
            int r2 = r2.mGroup
            if (r2 != r13) goto L_0x0045
            goto L_0x0047
        L_0x0045:
            r10 = r3
            goto L_0x004e
        L_0x0047:
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mBottom
            android.support.constraint.solver.SolverVariable r3 = r14.createObjectVariable(r2)
            goto L_0x0045
        L_0x004e:
            if (r13 == r12) goto L_0x0059
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mBaseline
            int r2 = r2.mGroup
            if (r2 != r13) goto L_0x0057
            goto L_0x0059
        L_0x0057:
            r9 = r4
            goto L_0x0060
        L_0x0059:
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mBaseline
            android.support.constraint.solver.SolverVariable r4 = r14.createObjectVariable(r2)
            goto L_0x0057
        L_0x0060:
            r2 = 0
            r3 = 0
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r15.mParent
            r8 = 1
            r7 = 0
            if (r4 == 0) goto L_0x01d0
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x0078
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r15.mLeft
            if (r4 == r5) goto L_0x0088
        L_0x0078:
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x0090
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r15.mRight
            if (r4 != r5) goto L_0x0090
        L_0x0088:
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r4 = (android.support.constraint.solver.widgets.ConstraintWidgetContainer) r4
            r4.addChain(r15, r7)
            r2 = 1
        L_0x0090:
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x00a0
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r15.mTop
            if (r4 == r5) goto L_0x00b0
        L_0x00a0:
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x00b8
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r15.mBottom
            if (r4 != r5) goto L_0x00b8
        L_0x00b0:
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r4 = (android.support.constraint.solver.widgets.ConstraintWidgetContainer) r4
            r4.addChain(r15, r8)
            r3 = 1
        L_0x00b8:
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r4 = r4.getHorizontalDimensionBehaviour()
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r5 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r4 != r5) goto L_0x0144
            if (r2 != 0) goto L_0x0144
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x00ed
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r5 = r15.mParent
            if (r4 == r5) goto L_0x00d5
            goto L_0x00ed
        L_0x00d5:
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x0104
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r5 = r15.mParent
            if (r4 != r5) goto L_0x0104
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor$ConnectionType r5 = android.support.constraint.solver.widgets.ConstraintAnchor.ConnectionType.STRICT
            r4.setConnectionType(r5)
            goto L_0x0104
        L_0x00ed:
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mLeft
            android.support.constraint.solver.SolverVariable r4 = r14.createObjectVariable(r4)
            android.support.constraint.solver.ArrayRow r5 = r49.createRow()
            android.support.constraint.solver.SolverVariable r6 = r49.createSlackVariable()
            r5.createRowGreaterThan(r0, r4, r6, r7)
            r14.addConstraint(r5)
        L_0x0104:
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x012d
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r5 = r15.mParent
            if (r4 == r5) goto L_0x0115
            goto L_0x012d
        L_0x0115:
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x0144
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r5 = r15.mParent
            if (r4 != r5) goto L_0x0144
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor$ConnectionType r5 = android.support.constraint.solver.widgets.ConstraintAnchor.ConnectionType.STRICT
            r4.setConnectionType(r5)
            goto L_0x0144
        L_0x012d:
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mRight
            android.support.constraint.solver.SolverVariable r4 = r14.createObjectVariable(r4)
            android.support.constraint.solver.ArrayRow r5 = r49.createRow()
            android.support.constraint.solver.SolverVariable r6 = r49.createSlackVariable()
            r5.createRowGreaterThan(r4, r1, r6, r7)
            r14.addConstraint(r5)
        L_0x0144:
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r4 = r4.getVerticalDimensionBehaviour()
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r5 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r4 != r5) goto L_0x01d0
            if (r3 != 0) goto L_0x01d0
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x0179
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r5 = r15.mParent
            if (r4 == r5) goto L_0x0161
            goto L_0x0179
        L_0x0161:
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x0190
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r5 = r15.mParent
            if (r4 != r5) goto L_0x0190
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor$ConnectionType r5 = android.support.constraint.solver.widgets.ConstraintAnchor.ConnectionType.STRICT
            r4.setConnectionType(r5)
            goto L_0x0190
        L_0x0179:
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTop
            android.support.constraint.solver.SolverVariable r4 = r14.createObjectVariable(r4)
            android.support.constraint.solver.ArrayRow r5 = r49.createRow()
            android.support.constraint.solver.SolverVariable r6 = r49.createSlackVariable()
            r5.createRowGreaterThan(r11, r4, r6, r7)
            r14.addConstraint(r5)
        L_0x0190:
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x01b9
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r5 = r15.mParent
            if (r4 == r5) goto L_0x01a1
            goto L_0x01b9
        L_0x01a1:
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x01d0
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r4.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r5 = r15.mParent
            if (r4 != r5) goto L_0x01d0
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor$ConnectionType r5 = android.support.constraint.solver.widgets.ConstraintAnchor.ConnectionType.STRICT
            r4.setConnectionType(r5)
            goto L_0x01d0
        L_0x01b9:
            android.support.constraint.solver.widgets.ConstraintWidget r4 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mBottom
            android.support.constraint.solver.SolverVariable r4 = r14.createObjectVariable(r4)
            android.support.constraint.solver.ArrayRow r5 = r49.createRow()
            android.support.constraint.solver.SolverVariable r6 = r49.createSlackVariable()
            r5.createRowGreaterThan(r4, r10, r6, r7)
            r14.addConstraint(r5)
        L_0x01d0:
            r19 = r2
            r20 = r3
            int r2 = r15.mWidth
            int r3 = r15.mMinWidth
            if (r2 >= r3) goto L_0x01dc
            int r2 = r15.mMinWidth
        L_0x01dc:
            int r3 = r15.mHeight
            int r4 = r15.mMinHeight
            if (r3 >= r4) goto L_0x01e4
            int r3 = r15.mMinHeight
        L_0x01e4:
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r4 = r15.mHorizontalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r5 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r4 == r5) goto L_0x01ec
            r4 = r8
            goto L_0x01ed
        L_0x01ec:
            r4 = r7
        L_0x01ed:
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r5 = r15.mVerticalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r6 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r5 == r6) goto L_0x01f5
            r5 = r8
            goto L_0x01f6
        L_0x01f5:
            r5 = r7
        L_0x01f6:
            if (r4 != 0) goto L_0x020d
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r15.mLeft
            if (r6 == 0) goto L_0x020d
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r15.mRight
            if (r6 == 0) goto L_0x020d
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r6.mTarget
            if (r6 == 0) goto L_0x020c
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r6.mTarget
            if (r6 != 0) goto L_0x020d
        L_0x020c:
            r4 = 1
        L_0x020d:
            if (r5 != 0) goto L_0x0238
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r15.mTop
            if (r6 == 0) goto L_0x0238
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r15.mBottom
            if (r6 == 0) goto L_0x0238
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r6.mTarget
            if (r6 == 0) goto L_0x0223
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r6.mTarget
            if (r6 != 0) goto L_0x0238
        L_0x0223:
            int r6 = r15.mBaselineDistance
            if (r6 == 0) goto L_0x0237
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r15.mBaseline
            if (r6 == 0) goto L_0x0238
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r6.mTarget
            if (r6 == 0) goto L_0x0237
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r15.mBaseline
            android.support.constraint.solver.widgets.ConstraintAnchor r6 = r6.mTarget
            if (r6 != 0) goto L_0x0238
        L_0x0237:
            r5 = 1
        L_0x0238:
            r6 = 0
            int r7 = r15.mDimensionRatioSide
            float r8 = r15.mDimensionRatio
            float r12 = r15.mDimensionRatio
            r16 = 0
            int r12 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            r24 = r11
            if (r12 <= 0) goto L_0x029e
            int r12 = r15.mVisibility
            r11 = 8
            if (r12 == r11) goto L_0x029e
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r11 = r15.mHorizontalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r12 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            r16 = 1065353216(0x3f800000, float:1.0)
            if (r11 != r12) goto L_0x027c
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r11 = r15.mVerticalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r12 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r11 != r12) goto L_0x027c
            r6 = 1
            if (r4 == 0) goto L_0x0262
            if (r5 != 0) goto L_0x0262
            r7 = 0
            goto L_0x029e
        L_0x0262:
            if (r4 != 0) goto L_0x029e
            if (r5 == 0) goto L_0x029e
            r7 = 1
            int r11 = r15.mDimensionRatioSide
            r12 = -1
            if (r11 != r12) goto L_0x029e
            float r16 = r16 / r8
            r27 = r2
            r29 = r3
            r28 = r4
            r30 = r5
            r26 = r6
            r12 = r7
            r31 = r16
            goto L_0x02ab
        L_0x027c:
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r11 = r15.mHorizontalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r12 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r11 != r12) goto L_0x028a
            r7 = 0
            int r11 = r15.mHeight
            float r11 = (float) r11
            float r11 = r11 * r8
            int r2 = (int) r11
            r4 = 1
            goto L_0x029e
        L_0x028a:
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r11 = r15.mVerticalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r12 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r11 != r12) goto L_0x029e
            r7 = 1
            int r11 = r15.mDimensionRatioSide
            r12 = -1
            if (r11 != r12) goto L_0x0298
            float r8 = r16 / r8
        L_0x0298:
            int r11 = r15.mWidth
            float r11 = (float) r11
            float r11 = r11 * r8
            int r3 = (int) r11
            r5 = 1
        L_0x029e:
            r27 = r2
            r29 = r3
            r28 = r4
            r30 = r5
            r26 = r6
            r12 = r7
            r31 = r8
        L_0x02ab:
            if (r26 == 0) goto L_0x02b4
            if (r12 == 0) goto L_0x02b2
            r2 = -1
            if (r12 != r2) goto L_0x02b4
        L_0x02b2:
            r2 = 1
            goto L_0x02b5
        L_0x02b4:
            r2 = 0
        L_0x02b5:
            r32 = r2
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r2 = r15.mHorizontalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r3 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r2 != r3) goto L_0x02c3
            boolean r2 = r15 instanceof android.support.constraint.solver.widgets.ConstraintWidgetContainer
            if (r2 == 0) goto L_0x02c3
            r2 = 1
            goto L_0x02c4
        L_0x02c3:
            r2 = 0
        L_0x02c4:
            int r3 = r15.mHorizontalResolution
            r11 = 2
            r8 = 3
            if (r3 == r11) goto L_0x03b6
            r7 = 2147483647(0x7fffffff, float:NaN)
            if (r13 == r7) goto L_0x02ec
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mLeft
            int r3 = r3.mGroup
            if (r3 != r13) goto L_0x02dc
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mRight
            int r3 = r3.mGroup
            if (r3 != r13) goto L_0x02dc
            goto L_0x02ec
        L_0x02dc:
            r38 = r0
            r39 = r1
            r35 = r9
            r36 = r10
            r40 = r12
            r37 = r24
            r21 = 0
            goto L_0x03c4
        L_0x02ec:
            if (r32 == 0) goto L_0x0378
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.mTarget
            if (r3 == 0) goto L_0x0378
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.mTarget
            if (r3 == 0) goto L_0x0378
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mLeft
            android.support.constraint.solver.SolverVariable r6 = r14.createObjectVariable(r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mRight
            android.support.constraint.solver.SolverVariable r5 = r14.createObjectVariable(r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.getTarget()
            android.support.constraint.solver.SolverVariable r4 = r14.createObjectVariable(r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.getTarget()
            android.support.constraint.solver.SolverVariable r3 = r14.createObjectVariable(r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r15.mLeft
            int r7 = r7.getMargin()
            r14.addGreaterThan(r6, r4, r7, r8)
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r15.mRight
            int r7 = r7.getMargin()
            r16 = -1
            int r7 = r7 * r16
            r14.addLowerThan(r5, r3, r7, r8)
            if (r19 != 0) goto L_0x0366
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r15.mLeft
            int r7 = r7.getMargin()
            float r8 = r15.mHorizontalBiasPercent
            android.support.constraint.solver.widgets.ConstraintAnchor r11 = r15.mRight
            int r11 = r11.getMargin()
            r17 = 4
            r18 = r3
            r3 = r14
            r23 = r4
            r4 = r6
            r25 = r5
            r5 = r23
            r34 = r6
            r6 = r7
            r21 = 0
            r33 = 2147483647(0x7fffffff, float:NaN)
            r7 = r8
            r8 = r18
            r35 = r9
            r9 = r25
            r36 = r10
            r10 = r11
            r37 = r24
            r11 = r17
            r3.addCentering(r4, r5, r6, r7, r8, r9, r10, r11)
            goto L_0x0371
        L_0x0366:
            r35 = r9
            r36 = r10
            r37 = r24
            r21 = 0
            r33 = 2147483647(0x7fffffff, float:NaN)
        L_0x0371:
            r38 = r0
            r39 = r1
            r40 = r12
            goto L_0x03c4
        L_0x0378:
            r33 = r7
            r35 = r9
            r36 = r10
            r37 = r24
            r21 = 0
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r15.mRight
            int r6 = r15.mX
            int r3 = r15.mX
            int r7 = r3 + r27
            int r9 = r15.mMinWidth
            float r10 = r15.mHorizontalBiasPercent
            int r11 = r15.mMatchConstraintDefaultWidth
            int r8 = r15.mMatchConstraintMinWidth
            int r3 = r15.mMatchConstraintMaxWidth
            r38 = r0
            r0 = r15
            r39 = r1
            r1 = r14
            r16 = r3
            r3 = r28
            r17 = r8
            r8 = r27
            r18 = r11
            r11 = r32
            r40 = r12
            r12 = r19
            r13 = r18
            r14 = r17
            r15 = r16
            r0.applyConstraints(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
            goto L_0x03c4
        L_0x03b6:
            r38 = r0
            r39 = r1
            r35 = r9
            r36 = r10
            r40 = r12
            r37 = r24
            r21 = 0
        L_0x03c4:
            r15 = r48
            int r0 = r15.mVerticalResolution
            r1 = 2
            if (r0 != r1) goto L_0x03cc
            return
        L_0x03cc:
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r0 = r15.mVerticalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r1 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r0 != r1) goto L_0x03d8
            boolean r0 = r15 instanceof android.support.constraint.solver.widgets.ConstraintWidgetContainer
            if (r0 == 0) goto L_0x03d8
            r0 = 1
            goto L_0x03da
        L_0x03d8:
            r0 = r21
        L_0x03da:
            r2 = r0
            if (r26 == 0) goto L_0x03ea
            r14 = r40
            r13 = 1
            if (r14 == r13) goto L_0x03e6
            r0 = -1
            if (r14 != r0) goto L_0x03ee
            goto L_0x03e7
        L_0x03e6:
            r0 = -1
        L_0x03e7:
            r21 = r13
            goto L_0x03ee
        L_0x03ea:
            r14 = r40
            r0 = -1
            r13 = 1
        L_0x03ee:
            int r1 = r15.mBaselineDistance
            if (r1 <= 0) goto L_0x050d
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mBottom
            r12 = 5
            r10 = 2147483647(0x7fffffff, float:NaN)
            r11 = r50
            if (r11 == r10) goto L_0x0410
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mBottom
            int r3 = r3.mGroup
            if (r3 != r11) goto L_0x0409
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mBaseline
            int r3 = r3.mGroup
            if (r3 != r11) goto L_0x0409
            goto L_0x0410
        L_0x0409:
            r7 = r35
            r8 = r37
            r9 = r49
            goto L_0x041d
        L_0x0410:
            int r3 = r48.getBaselineDistance()
            r7 = r35
            r8 = r37
            r9 = r49
            r9.addEquality(r7, r8, r3, r12)
        L_0x041d:
            r6 = r29
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mBaseline
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.mTarget
            if (r3 == 0) goto L_0x042b
            int r3 = r15.mBaselineDistance
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mBaseline
            r29 = r3
        L_0x042b:
            if (r11 == r10) goto L_0x0442
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mTop
            int r3 = r3.mGroup
            if (r3 != r11) goto L_0x0438
            int r3 = r1.mGroup
            if (r3 != r11) goto L_0x0438
            goto L_0x0442
        L_0x0438:
            r23 = r7
            r15 = r8
            r1 = r9
        L_0x043c:
            r43 = r14
            r14 = r36
            goto L_0x0500
        L_0x0442:
            if (r21 == 0) goto L_0x04bc
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.mTarget
            if (r3 == 0) goto L_0x04bc
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.mTarget
            if (r3 == 0) goto L_0x04bc
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mTop
            android.support.constraint.solver.SolverVariable r12 = r9.createObjectVariable(r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mBottom
            android.support.constraint.solver.SolverVariable r5 = r9.createObjectVariable(r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.getTarget()
            android.support.constraint.solver.SolverVariable r4 = r9.createObjectVariable(r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.getTarget()
            android.support.constraint.solver.SolverVariable r3 = r9.createObjectVariable(r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r15.mTop
            int r10 = r10.getMargin()
            r11 = 3
            r9.addGreaterThan(r12, r4, r10, r11)
            android.support.constraint.solver.widgets.ConstraintAnchor r10 = r15.mBottom
            int r10 = r10.getMargin()
            int r0 = r0 * r10
            r9.addLowerThan(r5, r3, r0, r11)
            if (r20 != 0) goto L_0x04b3
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r15.mTop
            int r0 = r0.getMargin()
            float r10 = r15.mVerticalBiasPercent
            android.support.constraint.solver.widgets.ConstraintAnchor r11 = r15.mBottom
            int r11 = r11.getMargin()
            r16 = 4
            r17 = r3
            r3 = r9
            r18 = r4
            r4 = r12
            r22 = r5
            r5 = r18
            r41 = r6
            r6 = r0
            r23 = r7
            r7 = r10
            r0 = r8
            r8 = r17
            r10 = r9
            r9 = r22
            r10 = r11
            r11 = r16
            r3.addCentering(r4, r5, r6, r7, r8, r9, r10, r11)
            goto L_0x04b8
        L_0x04b3:
            r41 = r6
            r23 = r7
            r0 = r8
        L_0x04b8:
            r1 = r49
            r15 = r0
            goto L_0x043c
        L_0x04bc:
            r41 = r6
            r23 = r7
            r0 = r8
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mTop
            int r6 = r15.mY
            int r3 = r15.mY
            int r7 = r3 + r29
            int r9 = r15.mMinHeight
            float r10 = r15.mVerticalBiasPercent
            int r11 = r15.mMatchConstraintDefaultHeight
            int r8 = r15.mMatchConstraintMinHeight
            int r5 = r15.mMatchConstraintMaxHeight
            r3 = r0
            r0 = r15
            r16 = r1
            r1 = r49
            r42 = r3
            r3 = r30
            r17 = r5
            r5 = r16
            r18 = r8
            r8 = r29
            r22 = r11
            r11 = r21
            r12 = r20
            r13 = r22
            r43 = r14
            r14 = r18
            r15 = r17
            r0.applyConstraints(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
            r14 = r36
            r0 = r41
            r15 = r42
            r3 = 5
            r1.addEquality(r14, r15, r0, r3)
        L_0x0500:
            r45 = r2
            r46 = r14
            r47 = r15
            r0 = r48
            r2 = r50
            goto L_0x05e5
        L_0x050d:
            r43 = r14
            r23 = r35
            r14 = r36
            r15 = r37
            r1 = r49
            r12 = 2147483647(0x7fffffff, float:NaN)
            r13 = r50
            if (r13 == r12) goto L_0x0537
            r11 = r48
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r11.mTop
            int r3 = r3.mGroup
            if (r3 != r13) goto L_0x052d
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r11.mBottom
            int r3 = r3.mGroup
            if (r3 != r13) goto L_0x052d
            goto L_0x0539
        L_0x052d:
            r45 = r2
            r0 = r11
        L_0x0530:
            r2 = r13
            r46 = r14
            r47 = r15
            goto L_0x05e5
        L_0x0537:
            r11 = r48
        L_0x0539:
            if (r21 == 0) goto L_0x05ac
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r11.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.mTarget
            if (r3 == 0) goto L_0x05ac
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r11.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.mTarget
            if (r3 == 0) goto L_0x05ac
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r11.mTop
            android.support.constraint.solver.SolverVariable r10 = r1.createObjectVariable(r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r11.mBottom
            android.support.constraint.solver.SolverVariable r9 = r1.createObjectVariable(r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r11.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.getTarget()
            android.support.constraint.solver.SolverVariable r8 = r1.createObjectVariable(r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r11.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.getTarget()
            android.support.constraint.solver.SolverVariable r7 = r1.createObjectVariable(r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r11.mTop
            int r3 = r3.getMargin()
            r6 = 3
            r1.addGreaterThan(r10, r8, r3, r6)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r11.mBottom
            int r3 = r3.getMargin()
            int r0 = r0 * r3
            r1.addLowerThan(r9, r7, r0, r6)
            if (r20 != 0) goto L_0x05a8
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r11.mTop
            int r0 = r0.getMargin()
            float r5 = r11.mVerticalBiasPercent
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r11.mBottom
            int r16 = r3.getMargin()
            r17 = 4
            r3 = r1
            r4 = r10
            r18 = r5
            r5 = r8
            r6 = r0
            r0 = r7
            r7 = r18
            r18 = r8
            r8 = r0
            r22 = r9
            r24 = r10
            r10 = r16
            r44 = r0
            r0 = r11
            r11 = r17
            r3.addCentering(r4, r5, r6, r7, r8, r9, r10, r11)
            goto L_0x05a9
        L_0x05a8:
            r0 = r11
        L_0x05a9:
            r45 = r2
            goto L_0x0530
        L_0x05ac:
            r0 = r11
            android.support.constraint.solver.widgets.ConstraintAnchor r7 = r0.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r0.mBottom
            int r9 = r0.mY
            int r3 = r0.mY
            int r10 = r3 + r29
            int r11 = r0.mMinHeight
            float r6 = r0.mVerticalBiasPercent
            int r5 = r0.mMatchConstraintDefaultHeight
            int r4 = r0.mMatchConstraintMinHeight
            int r3 = r0.mMatchConstraintMaxHeight
            r18 = r3
            r3 = r0
            r17 = r4
            r4 = r1
            r16 = r5
            r5 = r2
            r22 = r6
            r6 = r30
            r24 = r11
            r11 = r29
            r45 = r2
            r2 = r12
            r12 = r24
            r2 = r13
            r13 = r22
            r46 = r14
            r14 = r21
            r47 = r15
            r15 = r20
            r3.applyConstraints(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)
        L_0x05e5:
            if (r26 == 0) goto L_0x067e
            android.support.constraint.solver.ArrayRow r3 = r49.createRow()
            r4 = 2147483647(0x7fffffff, float:NaN)
            if (r2 == r4) goto L_0x0609
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r0.mLeft
            int r4 = r4.mGroup
            if (r4 != r2) goto L_0x05fd
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r0.mRight
            int r4 = r4.mGroup
            if (r4 != r2) goto L_0x05fd
            goto L_0x0609
        L_0x05fd:
            r11 = r38
            r12 = r39
            r4 = r43
        L_0x0603:
            r14 = r46
            r13 = r47
            goto L_0x0688
        L_0x0609:
            r4 = r43
            if (r4 != 0) goto L_0x0624
            r5 = r3
            r6 = r39
            r7 = r38
            r8 = r46
            r9 = r47
            r10 = r31
            android.support.constraint.solver.ArrayRow r5 = r5.createRowDimensionRatio(r6, r7, r8, r9, r10)
            r1.addConstraint(r5)
        L_0x061f:
            r11 = r38
            r12 = r39
            goto L_0x0603
        L_0x0624:
            r5 = 1
            if (r4 != r5) goto L_0x063a
            r5 = r3
            r6 = r46
            r7 = r47
            r8 = r39
            r9 = r38
            r10 = r31
            android.support.constraint.solver.ArrayRow r5 = r5.createRowDimensionRatio(r6, r7, r8, r9, r10)
            r1.addConstraint(r5)
            goto L_0x061f
        L_0x063a:
            int r5 = r0.mMatchConstraintMinWidth
            if (r5 <= 0) goto L_0x0649
            int r5 = r0.mMatchConstraintMinWidth
            r11 = r38
            r12 = r39
            r6 = 3
            r1.addGreaterThan(r12, r11, r5, r6)
            goto L_0x064e
        L_0x0649:
            r11 = r38
            r12 = r39
            r6 = 3
        L_0x064e:
            int r5 = r0.mMatchConstraintMinHeight
            if (r5 <= 0) goto L_0x065c
            int r5 = r0.mMatchConstraintMinHeight
            r14 = r46
            r13 = r47
            r1.addGreaterThan(r14, r13, r5, r6)
            goto L_0x0660
        L_0x065c:
            r14 = r46
            r13 = r47
        L_0x0660:
            r15 = 4
            r5 = r3
            r6 = r12
            r7 = r11
            r8 = r14
            r9 = r13
            r10 = r31
            r5.createRowDimensionRatio(r6, r7, r8, r9, r10)
            android.support.constraint.solver.SolverVariable r5 = r49.createErrorVariable()
            android.support.constraint.solver.SolverVariable r6 = r49.createErrorVariable()
            r5.strength = r15
            r6.strength = r15
            r3.addError(r5, r6)
            r1.addConstraint(r3)
            goto L_0x0688
        L_0x067e:
            r11 = r38
            r12 = r39
            r4 = r43
            r14 = r46
            r13 = r47
        L_0x0688:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.ConstraintWidget.addToSolver(android.support.constraint.solver.LinearSystem, int):void");
    }

    private void applyConstraints(LinearSystem system, boolean wrapContent, boolean dimensionFixed, ConstraintAnchor beginAnchor, ConstraintAnchor endAnchor, int beginPosition, int endPosition, int dimension, int minDimension, float bias, boolean useRatio, boolean inChain, int matchConstraintDefault, int matchMinDimension, int matchMaxDimension) {
        boolean dimensionFixed2;
        int dimension2;
        int i;
        SolverVariable endTarget;
        LinearSystem linearSystem = system;
        int i2 = beginPosition;
        int i3 = endPosition;
        int i4 = minDimension;
        int i5 = matchMinDimension;
        int i6 = matchMaxDimension;
        SolverVariable begin = linearSystem.createObjectVariable(beginAnchor);
        SolverVariable end = linearSystem.createObjectVariable(endAnchor);
        SolverVariable beginTarget = linearSystem.createObjectVariable(beginAnchor.getTarget());
        SolverVariable endTarget2 = linearSystem.createObjectVariable(endAnchor.getTarget());
        int beginAnchorMargin = beginAnchor.getMargin();
        int endAnchorMargin = endAnchor.getMargin();
        if (this.mVisibility == 8) {
            dimension2 = 0;
            dimensionFixed2 = true;
        } else {
            dimensionFixed2 = dimensionFixed;
            dimension2 = dimension;
        }
        if (beginTarget == null && endTarget2 == null) {
            linearSystem.addConstraint(system.createRow().createRowEquals(begin, i2));
            if (!useRatio) {
                if (wrapContent) {
                    linearSystem.addConstraint(LinearSystem.createRowEquals(linearSystem, end, begin, i4, true));
                } else if (dimensionFixed2) {
                    linearSystem.addConstraint(LinearSystem.createRowEquals(linearSystem, end, begin, dimension2, false));
                } else {
                    linearSystem.addConstraint(system.createRow().createRowEquals(end, i3));
                }
            }
        } else if (beginTarget != null && endTarget2 == null) {
            linearSystem.addConstraint(system.createRow().createRowEquals(begin, beginTarget, beginAnchorMargin));
            if (wrapContent) {
                linearSystem.addConstraint(LinearSystem.createRowEquals(linearSystem, end, begin, i4, true));
            } else if (!useRatio) {
                if (dimensionFixed2) {
                    linearSystem.addConstraint(system.createRow().createRowEquals(end, begin, dimension2));
                } else {
                    linearSystem.addConstraint(system.createRow().createRowEquals(end, i3));
                }
            }
        } else if (beginTarget == null && endTarget2 != null) {
            linearSystem.addConstraint(system.createRow().createRowEquals(end, endTarget2, -1 * endAnchorMargin));
            if (wrapContent) {
                linearSystem.addConstraint(LinearSystem.createRowEquals(linearSystem, end, begin, i4, true));
            } else if (!useRatio) {
                if (dimensionFixed2) {
                    linearSystem.addConstraint(system.createRow().createRowEquals(end, begin, dimension2));
                } else {
                    linearSystem.addConstraint(system.createRow().createRowEquals(begin, i2));
                }
            }
        } else if (dimensionFixed2) {
            if (wrapContent) {
                linearSystem.addConstraint(LinearSystem.createRowEquals(linearSystem, end, begin, i4, true));
            } else {
                linearSystem.addConstraint(system.createRow().createRowEquals(end, begin, dimension2));
            }
            if (beginAnchor.getStrength() == endAnchor.getStrength()) {
                int beginAnchorMargin2 = beginAnchorMargin;
                if (beginTarget == endTarget2) {
                    SolverVariable endTarget3 = endTarget2;
                    SolverVariable end2 = end;
                    int i7 = endAnchorMargin;
                    int i8 = matchMinDimension;
                    linearSystem.addConstraint(LinearSystem.createRowCentering(linearSystem, begin, beginTarget, 0, 0.5f, endTarget3, end2, 0, true));
                    boolean z = dimensionFixed2;
                    int i9 = beginAnchorMargin2;
                    SolverVariable solverVariable = endTarget3;
                    SolverVariable solverVariable2 = beginTarget;
                    SolverVariable solverVariable3 = end2;
                    SolverVariable solverVariable4 = begin;
                    int i10 = dimension2;
                    return;
                }
                SolverVariable endTarget4 = endTarget2;
                SolverVariable beginTarget2 = beginTarget;
                SolverVariable end3 = end;
                SolverVariable begin2 = begin;
                int dimension3 = dimension2;
                int endAnchorMargin2 = endAnchorMargin;
                int beginAnchorMargin3 = beginAnchorMargin2;
                int i11 = matchMinDimension;
                if (!inChain) {
                    int beginAnchorMargin4 = beginAnchorMargin3;
                    SolverVariable beginTarget3 = beginTarget2;
                    SolverVariable begin3 = begin2;
                    linearSystem.addConstraint(LinearSystem.createRowGreaterThan(linearSystem, begin3, beginTarget3, beginAnchorMargin4, beginAnchor.getConnectionType() != ConstraintAnchor.ConnectionType.STRICT));
                    boolean useBidirectionalError = endAnchor.getConnectionType() != ConstraintAnchor.ConnectionType.STRICT;
                    SolverVariable endTarget5 = endTarget4;
                    SolverVariable end4 = end3;
                    linearSystem.addConstraint(LinearSystem.createRowLowerThan(linearSystem, end4, endTarget5, -1 * endAnchorMargin2, useBidirectionalError));
                    SolverVariable endTarget6 = endTarget5;
                    SolverVariable end5 = end4;
                    boolean z2 = useBidirectionalError;
                    SolverVariable solverVariable5 = beginTarget3;
                    SolverVariable solverVariable6 = begin3;
                    linearSystem.addConstraint(LinearSystem.createRowCentering(linearSystem, begin3, beginTarget3, beginAnchorMargin4, bias, endTarget6, end5, endAnchorMargin2, false));
                    boolean z3 = dimensionFixed2;
                    int i12 = dimension3;
                    SolverVariable solverVariable7 = endTarget6;
                    SolverVariable solverVariable8 = end5;
                    int i13 = beginAnchorMargin4;
                    return;
                }
                SolverVariable solverVariable9 = begin2;
                boolean z4 = dimensionFixed2;
                int i14 = beginAnchorMargin3;
                SolverVariable solverVariable10 = endTarget4;
                SolverVariable solverVariable11 = end3;
                int i15 = dimension3;
                return;
            } else if (beginAnchor.getStrength() == ConstraintAnchor.Strength.STRONG) {
                linearSystem.addConstraint(system.createRow().createRowEquals(begin, beginTarget, beginAnchorMargin));
                SolverVariable slack = system.createSlackVariable();
                ArrayRow row = system.createRow();
                row.createRowLowerThan(end, endTarget2, slack, -1 * endAnchorMargin);
                linearSystem.addConstraint(row);
            } else {
                SolverVariable slack2 = system.createSlackVariable();
                ArrayRow row2 = system.createRow();
                row2.createRowGreaterThan(begin, beginTarget, slack2, beginAnchorMargin);
                linearSystem.addConstraint(row2);
                linearSystem.addConstraint(system.createRow().createRowEquals(end, endTarget2, -1 * endAnchorMargin));
                SolverVariable solverVariable12 = endTarget2;
                SolverVariable solverVariable13 = beginTarget;
                SolverVariable solverVariable14 = end;
                SolverVariable solverVariable15 = begin;
                int i16 = dimension2;
                int i17 = endAnchorMargin;
                boolean z5 = dimensionFixed2;
                int i18 = beginAnchorMargin;
                int i19 = matchMinDimension;
                return;
            }
        } else {
            int beginAnchorMargin5 = beginAnchorMargin;
            SolverVariable endTarget7 = endTarget2;
            SolverVariable beginTarget4 = beginTarget;
            SolverVariable end6 = end;
            SolverVariable begin4 = begin;
            int dimension4 = dimension2;
            int endAnchorMargin3 = endAnchorMargin;
            int i20 = matchMinDimension;
            if (useRatio) {
                int beginAnchorMargin6 = beginAnchorMargin5;
                linearSystem.addGreaterThan(begin4, beginTarget4, beginAnchorMargin6, 3);
                SolverVariable endTarget8 = endTarget7;
                SolverVariable end7 = end6;
                linearSystem.addLowerThan(end7, endTarget8, -1 * endAnchorMargin3, 3);
                SolverVariable solverVariable16 = end7;
                boolean z6 = dimensionFixed2;
                int i21 = beginAnchorMargin6;
                linearSystem.addConstraint(LinearSystem.createRowCentering(linearSystem, begin4, beginTarget4, beginAnchorMargin6, bias, endTarget8, end7, endAnchorMargin3, true));
                int i22 = dimension4;
                SolverVariable solverVariable17 = endTarget8;
                return;
            }
            SolverVariable endTarget9 = endTarget7;
            SolverVariable end8 = end6;
            int beginAnchorMargin7 = beginAnchorMargin5;
            if (inChain) {
                SolverVariable solverVariable18 = endTarget9;
                return;
            } else if (matchConstraintDefault == 1) {
                int dimension5 = dimension4;
                if (i20 > dimension5) {
                    dimension5 = i20;
                }
                int dimension6 = matchMaxDimension;
                if (dimension6 > 0) {
                    if (dimension6 < dimension5) {
                        dimension5 = dimension6;
                    } else {
                        linearSystem.addLowerThan(end8, begin4, dimension6, 3);
                    }
                }
                linearSystem.addEquality(end8, begin4, dimension5, 3);
                linearSystem.addGreaterThan(begin4, beginTarget4, beginAnchorMargin7, 2);
                SolverVariable endTarget10 = endTarget9;
                linearSystem.addLowerThan(end8, endTarget10, -endAnchorMargin3, 2);
                linearSystem.addCentering(begin4, beginTarget4, beginAnchorMargin7, bias, endTarget10, end8, endAnchorMargin3, 4);
                int i23 = dimension5;
                SolverVariable solverVariable19 = endTarget10;
                return;
            } else {
                int dimension7 = dimension4;
                SolverVariable endTarget11 = endTarget9;
                if (i20 == 0) {
                    i = matchMaxDimension;
                    if (i == 0) {
                        linearSystem.addConstraint(system.createRow().createRowEquals(begin4, beginTarget4, beginAnchorMargin7));
                        SolverVariable endTarget12 = endTarget11;
                        linearSystem.addConstraint(system.createRow().createRowEquals(end8, endTarget12, -1 * endAnchorMargin3));
                        SolverVariable solverVariable20 = endTarget12;
                        int i24 = dimension7;
                        return;
                    }
                    endTarget = endTarget11;
                } else {
                    endTarget = endTarget11;
                    i = matchMaxDimension;
                }
                if (i > 0) {
                    linearSystem.addLowerThan(end8, begin4, i, 3);
                }
                linearSystem.addGreaterThan(begin4, beginTarget4, beginAnchorMargin7, 2);
                linearSystem.addLowerThan(end8, endTarget, -endAnchorMargin3, 2);
                SolverVariable solverVariable21 = endTarget;
                int i25 = dimension7;
                linearSystem.addCentering(begin4, beginTarget4, beginAnchorMargin7, bias, endTarget, end8, endAnchorMargin3, 4);
                return;
            }
        }
        int i26 = matchMinDimension;
    }

    public void updateFromSolver(LinearSystem system, int group) {
        if (group == Integer.MAX_VALUE) {
            setFrame(system.getObjectVariableValue(this.mLeft), system.getObjectVariableValue(this.mTop), system.getObjectVariableValue(this.mRight), system.getObjectVariableValue(this.mBottom));
        } else if (group == -2) {
            setFrame(this.mSolverLeft, this.mSolverTop, this.mSolverRight, this.mSolverBottom);
        } else {
            if (this.mLeft.mGroup == group) {
                this.mSolverLeft = system.getObjectVariableValue(this.mLeft);
            }
            if (this.mTop.mGroup == group) {
                this.mSolverTop = system.getObjectVariableValue(this.mTop);
            }
            if (this.mRight.mGroup == group) {
                this.mSolverRight = system.getObjectVariableValue(this.mRight);
            }
            if (this.mBottom.mGroup == group) {
                this.mSolverBottom = system.getObjectVariableValue(this.mBottom);
            }
        }
    }

    public void updateFromSolver(LinearSystem system) {
        updateFromSolver(system, Integer.MAX_VALUE);
    }
}
