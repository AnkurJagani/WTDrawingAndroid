package com.water.wtdrawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;

/**
 * WTDrawing
 *
 * Created by Water Zhang on 11/25/15
 */
public class WTDrawingView extends View {

    // Drawing mode
    private static final int DRAW = 1;
    private static final int ERASER = 2;

    // Default config
    private static final int DEFAULT_STROKE_COLOR = Color.BLACK;
    private static final float DEFAULT_STROKE_WIDTH = 2.0f;
    private static final float DEFAULT_ERASER_WIDTH = 20.0f;

    Context context;

    private boolean initialized;

    private Paint mPaint = new Paint(Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
    private Paint eraserPaint = new Paint(Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
    private Paint mBitmapPaint;
    private Canvas mCanvas;
    private Bitmap mBitmap;

    private WTBezierPath  mPath;
    private LinkedList<WTBezierPath> mPathArray;

    private int drawingMode;
    private PointF[] mPoints = new PointF[5];
    private int mPointIndex;
    private int mMovedPointCount;

    private int strokeColor;

    /**
     * Stroke width, unit(dp)
     */
    private float strokeWidth;

    /**
     * Eraser width, unit(dp)
     */
    private float eraserWidth;

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        mPaint.setColor(this.strokeColor);
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, strokeWidth, getResources().getDisplayMetrics());
        mPaint.setStrokeWidth(this.strokeWidth);
    }

    public float getEraserWidth() {
        return eraserWidth;
    }

    public void setEraserWidth(float eraserWidth) {
        this.eraserWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, eraserWidth, getResources().getDisplayMetrics());;
        eraserPaint.setStrokeWidth(this.eraserWidth);
    }

    public WTDrawingView(Context c, int width, int height) {
        this(c,null);
        init(width, height);
    }

    public WTDrawingView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
    }

    public void setEraserMode(boolean drawEraser) {
        drawingMode = drawEraser ? ERASER : DRAW;
    }

    public void undo() {
        Paint emptyPaint = new Paint();
        emptyPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(emptyPaint);

        if (!mPathArray.isEmpty()) {
            mPathArray.removeLast();
        }

        for (WTBezierPath path : mPathArray) {
            mPaint.setStyle(path.isCircle ? Paint.Style.FILL : Paint.Style.STROKE);
            if (path.isEraser) {
                mCanvas.drawPath(path,eraserPaint);
            }
            else {
                mPaint.setColor(path.strokeColor);
                mPaint.setStrokeWidth(path.strokeWidth);
                mCanvas.drawPath(path,mPaint);
            }
        }

        // Restore paint
        mPaint.setStrokeWidth(this.strokeWidth);
        mPaint.setColor(this.strokeColor);

        invalidate();
    }

    public void clear() {
        Paint emptyPaint = new Paint();
        emptyPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(emptyPaint);

        mPathArray.clear();
        if (mPath != null) {
            mPath.reset();
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.touchesBegan(new PointF(event.getX(), event.getY()));
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            this.touchesMoved(new PointF(event.getX(),event.getY()));
        }
        else if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL){

            this.touchesEnded(new PointF(event.getX(), event.getY()));
        }

        return true;
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (initialized) {
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        }
        else {
            init(this.getWidth(),this.getHeight());
        }
    }

    private void init(int width, int height) {
        if (!initialized) {
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            mPathArray = new LinkedList<WTBezierPath>();

            drawingMode = DRAW;

            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            mCanvas = new Canvas(mBitmap);

            setStrokeWidth(DEFAULT_STROKE_WIDTH);
            setStrokeColor(DEFAULT_STROKE_COLOR);
            setEraserWidth(DEFAULT_ERASER_WIDTH);

            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeJoin(Paint.Join.ROUND);

            eraserPaint.setAntiAlias(true);
            eraserPaint.setStyle(Paint.Style.STROKE);
            eraserPaint.setStrokeCap(Paint.Cap.ROUND);
            eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

            initialized = true;
        }
    }

    private Paint currentPaint() {
        return drawingMode == ERASER ? eraserPaint : mPaint;
    }

    private void touchesBegan(PointF p) {

        if (drawingMode != DRAW && drawingMode != ERASER) {
            drawingMode = DRAW;
        }

        mMovedPointCount = 0;
        mPointIndex = 0;
        mPoints[0] = p;

        mPaint.setStyle(Paint.Style.STROKE);

        mPath = new WTBezierPath();
        mPath.strokeColor = this.strokeColor;
        mPath.strokeWidth = this.strokeWidth;
        mPath.isEraser = drawingMode == ERASER;
    }

    private void touchesMoved(PointF p) {

        mMovedPointCount ++;
        mPointIndex ++;
        mPoints[mPointIndex] = p;

        // We got 5 points here, now we can draw a bezier path,
        // use 4 points to draw a bezier,and the last point is cached for next segment.
        if (mPointIndex == 4) {
            mPoints[3] = new PointF((mPoints[2].x + mPoints[4].x) / 2, (mPoints[2].y + mPoints[4].y) / 2);

            moveToPoint(mPoints[0]);
            addCurveToPoint(mPoints[3], mPoints[1], mPoints[2]);

            mCanvas.drawPath(mPath, currentPaint());

            invalidate();

            mPoints[0] = mPoints[3];
            mPoints[1] = mPoints[3]; // this is the "magic"
            mPoints[2] = mPoints[4];
            mPointIndex = 2;
        }
    }

    private void touchesEnded(PointF p) {

        // Handle if there are no engough points to draw a bezier,
        // draw a circle instead.
        if (mMovedPointCount < 3) {
            mPath.reset();
            mPath.isCircle = true;
            mPath.addCircle(mPoints[0].x, mPoints[0].y, mPaint.getStrokeWidth(), WTBezierPath.Direction.CW);
            mPaint.setStyle(Paint.Style.FILL);
            mCanvas.drawPath(mPath, currentPaint());
        }

        mMovedPointCount = 0;
        mPointIndex = 0;

        mPathArray.add(mPath);
        invalidate();
    }

    private void moveToPoint(PointF p) {
        mPath.moveTo(p.x, p.y);
    }

    private void addCurveToPoint(PointF p, PointF controlPoint1, PointF controlPoint2) {
        mPath.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p.x, p.y);
    }
}
