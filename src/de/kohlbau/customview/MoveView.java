package de.kohlbau.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public final class MoveView extends View {

    private static final int INVALID_POINTER_ID = -1;
    private Bitmap bitmap;
    private Rect mImagePosition;
    private int mHeight;
    private int mWidth;
    private float mPosX;
    private float mPosY;
    private float mLastTouchX;
    private float mLastTouchY;

    private int mActivePointerId = INVALID_POINTER_ID;

    boolean mMoveable;


    private ArrayList<OnTouchInputListener> onTouchInputListeners = new ArrayList<OnTouchInputListener>();
    private ArrayList<OnPositionChangedListener> onPositionChangedListeners = new ArrayList<OnPositionChangedListener>();

    public interface OnTouchInputListener {
        void onTouchInput(float x, float y);
    }

    public interface OnPositionChangedListener {
        void onPositionChanged(float x, float y);
    }

    public void setOnTouchInputListener(OnTouchInputListener listener) {
        onTouchInputListeners.add(listener);
    }

    public void setOnPositionChangedListener(OnPositionChangedListener listener) {
        onPositionChangedListeners.add(listener);
    }

    public void unsetOnTouchInputListener(OnTouchInputListener listener) {
        onTouchInputListeners.remove(listener);
    }

    public void unsetPositionChangedListener(OnPositionChangedListener listener) {
        onPositionChangedListeners.remove(listener);
    }


    public MoveView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MoveView,
                0, 0);

        try {
            Drawable image = a.getDrawable(R.styleable.MoveView_image);
            bitmap = ((BitmapDrawable) image).getBitmap();
        } finally {
            a.recycle();
        }

        mHeight = bitmap.getHeight();
        mWidth = bitmap.getWidth();

        mImagePosition = new Rect();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPosX = w / 2;
        mPosY = h / 2;
    }

    @Override
    public void onDraw(Canvas canvas) {
        mImagePosition.left = (int) mPosX - mWidth / 2;
        mImagePosition.right = (int) mPosX + mWidth / 2;
        mImagePosition.top = (int) mPosY - mHeight / 2;
        mImagePosition.bottom = (int) mPosY + mHeight / 2;

        canvas.drawBitmap(bitmap, null, mImagePosition, null);
    }

    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);


        switch (action) {
            case MotionEvent.ACTION_DOWN: {

                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float y = MotionEventCompat.getY(event, pointerIndex);

                mLastTouchX = x;
                mLastTouchY = y;

                mActivePointerId = MotionEventCompat.getPointerId(event, 0);

                if (x >= mImagePosition.left && x <= mImagePosition.right && y >= mImagePosition.top && y <= mImagePosition.bottom) {
                    mMoveable = true;
                }

                for (int i = 0; i < onTouchInputListeners.size(); i++) {
                    onTouchInputListeners.get(i).onTouchInput(x, y);
                    unsetOnTouchInputListener(onTouchInputListeners.get(i));
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mMoveable) {
                    final int pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);

                    final float x = MotionEventCompat.getX(event, pointerIndex);
                    final float y = MotionEventCompat.getY(event, pointerIndex);

                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    if ((mPosX + dx - mWidth / 2) > 0 && (mPosX + dx + mWidth / 2) < getWidth() && (mPosY + dy - mHeight / 2) > 0 && (mPosY + dy + mHeight / 2) < getHeight()) {

                        mPosX += dx;
                        mPosY += dy;

                        invalidate();

                        for (int i = 0; i < onPositionChangedListeners.size(); i++) {
                            onPositionChangedListeners.get(i).onPositionChanged(mPosX, mPosY);
                        }
                    }

                    mLastTouchX = x;
                    mLastTouchY = y;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                mMoveable = false;
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);

                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = MotionEventCompat.getX(event, newPointerIndex);
                    mLastTouchY = MotionEventCompat.getY(event, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
                }
                break;
            }

        }
        super.onTouchEvent(event);
        return true;
    }


    public void setPosition(int x, int y) {
        if ((x - mWidth / 2) > 0 && (x + mWidth / 2) < getWidth() && (y - mHeight / 2) > 0 && (y + mHeight / 2) < getHeight()) {
            mPosX = x;
            mPosY = y;
            invalidate();
            for (int i = 0; i < onPositionChangedListeners.size(); i++) {
                onPositionChangedListeners.get(i).onPositionChanged(x, y);
            }
        }
    }

    public float getPositionX() {
        return mPosX;
    }

    public float getPositionY() {
        return mPosY;
    }
}
