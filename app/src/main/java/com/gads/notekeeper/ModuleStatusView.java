package com.gads.notekeeper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class ModuleStatusView extends View {
    private static final int EDIT_MODE_MODULE_COUNT = 7;
    private static final int INVALID_INDEX = -1;
    private static final int SHAPE_CIRCLE = 0;
    private static final float DEFAULT_OUTLINE_WIDTH_DP = 2f;
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;
    private float mOutlineWidth;
    private float mShapeSize;
    private float mSpacing;
    private Rect[] mModuleRectangles;
    private int mOutlineColor;
    private Paint mPaintOutline;
    private int mFillColor;
    private Paint mPaintFill;
    private float mRadius;
    private int mMaxHorizontalModules;
    private int mShape;

    public boolean[] getModuleStatus() {
        return mModuleStatus;
    }

    public void setModuleStatus(boolean[] moduleStatus) {
        mModuleStatus = moduleStatus;
    }

    // boolean array to store information on the modules in a course
    private boolean[] mModuleStatus;

    public ModuleStatusView(Context context) {
        super(context);
        init(null, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        if (isInEditMode())
            setupEditModeValues();

        // Using Device Independent Pixels
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        float displayDensity = dm.density;
        float defaultOutlineWidthPixels = displayDensity * DEFAULT_OUTLINE_WIDTH_DP;


        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ModuleStatusView, defStyle, 0);

        mOutlineColor = a.getColor(R.styleable.ModuleStatusView_outlineColor, Color.BLACK);
        mShape = a.getInt(R.styleable.ModuleStatusView_shape, SHAPE_CIRCLE);
        mOutlineWidth = a.getDimension(R.styleable.ModuleStatusView_outlineWidth, defaultOutlineWidthPixels);

        a.recycle();


//        mShapeSize = 144f;
        mShapeSize = displayDensity * 48f;
//        mSpacing = 30f;
        mSpacing = displayDensity * 10f;
        // circle radius to ensure that the outline stays within the rectangle
        mRadius = (mShapeSize - mOutlineWidth) / 2;


        // paint instance to draw the circles' outline in the rectangles
        mPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintOutline.setStyle(Paint.Style.STROKE);
        mPaintOutline.setStrokeWidth(mOutlineWidth);
        mPaintOutline.setColor(mOutlineColor);
        // paint fill instance
        mFillColor = getContext().getResources().getColor(R.color.pluralsight_orange);
        mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(mFillColor);
    }

    private void setupEditModeValues() {
        boolean[] exampleModuleValues = new boolean[EDIT_MODE_MODULE_COUNT];

        // setting half of the modules status to true
        int middle = EDIT_MODE_MODULE_COUNT / 2;
        for (int i = 0; i<middle; i++){
            exampleModuleValues[i] = true;
        }

        setModuleStatus(exampleModuleValues);
    }

    private void setupModuleRectangles(int width) {
        int availableWidth = width - getPaddingLeft() - getPaddingRight();
        int horizontalModulesThatCanFit = (int) (availableWidth / (mShapeSize + mSpacing));
        int maxHorizontalModules = Math.min(horizontalModulesThatCanFit, mModuleStatus.length);

        // store the array of the rectangles in Rect
        mModuleRectangles = new Rect[mModuleStatus.length];
        // loop to populate the array with rectangles
        for (int moduleIndex=0; moduleIndex<mModuleStatus.length; moduleIndex++){

            int column = moduleIndex % maxHorizontalModules;
            int row = moduleIndex/maxHorizontalModules;

            // position of the left edge of the rectangle for each of the module
            int x = getPaddingLeft() + (int) (column * (mShapeSize + mSpacing));
            // position of the top edge of the rectangle for each of the module
            int y = getPaddingTop() + (int) (row * (mShapeSize + mSpacing));
            // position of the right edge of the rectangle for each of the module
            int x_r = x + (int) mShapeSize;
            // position of the bottom edge of the rectangle for each of the module
            int y_b = y + (int) mShapeSize;
            // create the rectangle
            mModuleRectangles[moduleIndex] = new Rect(x, y, x_r, y_b);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 0;
        int desiredHeight = 0;

        // value passed into the widthMeasureSpec parameter
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        // available width for the custom view
        int availableWidth = specWidth - getPaddingLeft() - getPaddingRight();
        // determine how many module circles will fit into the available width
        int horizontalModulesThatCanFit = (int) (availableWidth / (mShapeSize + mSpacing));

        mMaxHorizontalModules = Math.min(horizontalModulesThatCanFit, mModuleStatus.length);

        desiredWidth = (int) (mMaxHorizontalModules * (mShapeSize + mSpacing) - mSpacing);
        desiredWidth += getPaddingLeft() + getPaddingRight();

        // no of rows required to draw all the modules
        int rows = ((mModuleStatus.length - 1)/ mMaxHorizontalModules) + 1;
        desiredHeight = (int) ((rows * (mShapeSize + mSpacing)) - mSpacing);
        desiredHeight += getPaddingTop() + getPaddingBottom();

        int width = resolveSizeAndState(desiredWidth, widthMeasureSpec, 0);
        int height = resolveSizeAndState(desiredHeight, heightMeasureSpec, 0);

        setMeasuredDimension(width, height);

    }




    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // method to calculate the rectangles to be drawn
        setupModuleRectangles(w);
    }

    // where the drawing is done
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // drawing the circles into the rectangles
        for (int moduleIndex=0; moduleIndex<mModuleStatus.length; moduleIndex++){
            if (mShape == SHAPE_CIRCLE) {
                // x co-ordinate of the circle's center point
                float x = mModuleRectangles[moduleIndex].centerX();
                // y co-ordinate of the circle's center point
                float y = mModuleRectangles[moduleIndex].centerY();

                // draw the filled in circle only when a module is complete
                if (mModuleStatus[moduleIndex])
                    // draw the filled in circle
                    canvas.drawCircle(x, y, mRadius, mPaintFill);
                // draw the outline circle
                canvas.drawCircle(x, y, mRadius, mPaintOutline);
            } else {
                drawSquare(canvas, moduleIndex);
            }

        }
    }

    private void drawSquare(Canvas canvas, int moduleIndex){
        Rect moduleRectangle = mModuleRectangles[moduleIndex];

        if (mModuleStatus[moduleIndex])
            canvas.drawRect(moduleRectangle, mPaintFill);

        canvas.drawRect(moduleRectangle.left + (mOutlineWidth/2),
                moduleRectangle.top + (mOutlineWidth/2),
                moduleRectangle.right - (mOutlineWidth/2),
                moduleRectangle.bottom - (mOutlineWidth/2),
                mPaintOutline);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                int moduleIndex = findItemAtPoint(event.getX(), event.getY());
                onModuleSelected(moduleIndex);
                return true;
        }

        return super.onTouchEvent(event);
    }

    private void onModuleSelected(int moduleIndex) {
        if (moduleIndex == INVALID_INDEX)
            return;

        mModuleStatus[moduleIndex] = ! mModuleStatus[moduleIndex];
        invalidate();
    }

    private int findItemAtPoint(float x, float y) {
        // find the index of the rectangle the user oched
        int moduleIndex = INVALID_INDEX;
        for (int i=0; i < mModuleRectangles.length; i++){
            if (mModuleRectangles[i].contains((int) x, (int) y)){
                moduleIndex = i;
                break;
            }
        }

        return moduleIndex;
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view"s example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view"s example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view"s example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view"s example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}