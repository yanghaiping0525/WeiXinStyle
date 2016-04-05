package com.yang.weixin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

import com.yang.weixin.R;

/**
 * Created by YangHaiPing on 2016/3/22.
 */
public class ColorChangeable extends View implements ViewTreeObserver.OnGlobalLayoutListener {
    private int mColor;
    private Bitmap mIconBitmap;
    private String mText = "微信";
    private int mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
    private Canvas mCanvas;
    private Bitmap mIconBitmapBackground;
    private Paint mIconPaint;
    private Paint mBackgroundPaint;
    private Paint mTextPaint;
    private float mAlpha = 0f;
    private Rect mIconRect;
    private Rect mTextRect;
    private Rect mBackgroundRect;
    private static float mScale = 0.7f;
    private int mIconCenterX, mIconCenterY, mIconWidth;
    private boolean isOnce = false;
    private static final String INSTANCE_STATUS = "instance_status";
    private static final String STATUS_ALPHA = "status_alpha";

    public ColorChangeable(Context context) {
        this(context, null);
    }

    public ColorChangeable(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorChangeable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        //先保存父类的状态
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        //保存自定义View的私有状态
        bundle.putFloat(STATUS_ALPHA, mAlpha);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mAlpha = bundle.getFloat(STATUS_ALPHA);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ColorChangeable);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int index = array.getIndex(i);
            switch (index) {
                case R.styleable.ColorChangeable_icon_color:
                    mColor = array.getColor(index, 0xff45c01a);
                    break;
                case R.styleable.ColorChangeable_text:
                    mText = array.getString(index);
                    break;
                case R.styleable.ColorChangeable_icon_:
                    BitmapDrawable drawable = (BitmapDrawable) array.getDrawable(index);
                    if (drawable != null) {
                        mIconBitmap = drawable.getBitmap();
                        if (mIconBitmap == null) {
                            mIconBitmap = BitmapFactory.decodeResource(getResources(), array.getResourceId(index, 0));
                        }
                    }
                    break;
                case R.styleable.ColorChangeable_text_size:
                    mTextSize = (int) array.getDimension(index, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
            }
        }
        array.recycle();
        mIconPaint = new Paint();
        mIconPaint.setAntiAlias(true);
        mIconPaint.setDither(true);

        mTextRect = new Rect();
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0xcccccc);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int alpha = (int) Math.ceil(255 * mAlpha);
        if (mIconBitmap != null && mIconRect != null) {
            mIconPaint.setAlpha(255 - alpha);
            canvas.drawBitmap(mIconBitmap, null, mIconRect, mIconPaint);
        }
        setupTargetBitmap(alpha);
        canvas.drawBitmap(mIconBitmapBackground, 0, 0, null);
        drawSourceText(canvas, alpha);
        drawTargetText(canvas, alpha);
    }

    private void setupTargetBitmap(int alpha) {
        //制作纯色背景
        mIconBitmapBackground = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mIconBitmapBackground);
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mColor);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setDither(true);//防抖动
        mBackgroundPaint.setFilterBitmap(true);//图像过滤
        mBackgroundPaint.setAlpha(alpha);
        //绘制纯色背景
        //mCanvas.drawRect(mIconRect, mBackgroundPaint);
        mBackgroundRect.left = (int) (mIconRect.left + mIconRect.width() * mAlpha);
        mCanvas.drawRect(mBackgroundRect, mBackgroundPaint);
        //融合效果
        mBackgroundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // mBackgroundPaint.setAlpha(255);
        mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mBackgroundPaint);
//        mCanvas.drawBitmap(mIconBitmap, null, mBackgroundRect, mBackgroundPaint);
    }

    private void drawTargetText(Canvas canvas, int alpha) {
        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        mTextPaint.setTextSize(mTextSize / (1 - mAlpha * (1 - mScale)));
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
        canvas.drawText(mText, getMeasuredWidth() / 2 - mTextRect.width() / 2, mIconRect.bottom + mTextRect.height(), mTextPaint);
    }

    private void drawSourceText(Canvas canvas, int alpha) {
        mTextPaint.setColor(0xcccccc);
        mTextPaint.setAlpha(255 - alpha);
        mTextPaint.setTextSize(mTextSize / (1 - mAlpha * (1 - mScale)));
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
        canvas.drawText(mText, getMeasuredWidth() / 2 - mTextRect.width() / 2, mIconRect.bottom + mTextRect.height(), mTextPaint);
    }


    @Override
    public void onGlobalLayout() {
        if (!isOnce && mTextRect != null) {
            mIconWidth = (int) (Math.min(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                    getMeasuredHeight() - getPaddingBottom() - getPaddingTop() - mTextRect.height() / mScale) * mScale);
            int left = (getMeasuredWidth() - mIconWidth) / 2;
            int top = (getMeasuredHeight() - (int) (mTextRect.height() / mScale) - mIconWidth) / 2;
            mIconCenterX = left + mIconWidth / 2;
            mIconCenterY = top + mIconWidth / 2;
            mIconRect = new Rect(left, top, left + mIconWidth, top + mIconWidth);
            mBackgroundRect = new Rect(left - mIconWidth, top, left, top + mIconWidth);
            isOnce = true;
        }
    }


    public void setIconAlpha(float alpha) {
        this.mAlpha = alpha;
        if (mIconRect != null && mBackgroundRect != null) {
            int currentWidth = (int) (mIconWidth / (1 - (1 - mScale) * alpha));
            mIconRect.set(mIconCenterX - currentWidth / 2, mIconCenterY - currentWidth / 2, mIconCenterX + currentWidth / 2, mIconCenterY + currentWidth / 2);
            mBackgroundRect.set(mIconRect.left - currentWidth, mIconRect.top, mIconRect.left, mIconRect.bottom);
        }

        invalidateView();
    }

    private void invalidateView() {
        //UI线程
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

}
