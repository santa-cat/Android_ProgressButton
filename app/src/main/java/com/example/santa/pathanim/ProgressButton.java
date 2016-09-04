package com.example.santa.pathanim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

/**
 * Created by santa on 16/8/23.
 */
public class ProgressButton extends Button implements View.OnClickListener{
    private int mBgColorDef = Color.BLUE;
    private int mBgColorErr = Color.RED;
    private int mBgColorSucc = Color.GREEN;
    private int mBgColorProgress = Color.WHITE;
    private int mTextColor = Color.WHITE;

    private String mStringDef = "Click";
    private String mStringErr = "Error";
    private String mStringSucc = "Success";

    private int mDuration = 600;

    //圆角度数
    private int mRadius = 100;
    private int mStorkeWidth = 5;
    private int mDefWidth;
    private int mDefHeight;


    private GradientDrawable mDrawable;
    private AnimatorSet mAnimProgress;
    private AnimatorSet mAnim;

    private float mPercent = 1f;

    private Paint mPaint = new Paint();
    private final static int STATE_IDEL = 0;
    private final static int STATE_PROGRESS = 1;
    private final static int STATE_SUCC = 2;
    private final static int STATE_ERR = 3;
    private int mState = STATE_IDEL;

    private boolean isAutoProgress = true;
    private int mStartAngle = 0;

    public ProgressButton(Context context) {
        this(context, null);
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mRadius, dm);
        mStorkeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mStorkeWidth, dm);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton);
        mBgColorDef = array.getColor(R.styleable.ProgressButton_pb_bgColorDef, mBgColorDef);
        mBgColorErr = array.getColor(R.styleable.ProgressButton_pb_bgColorErr, mBgColorErr);
        mBgColorSucc = array.getColor(R.styleable.ProgressButton_pb_bgColorSucc, mBgColorSucc);
        mBgColorProgress = array.getColor(R.styleable.ProgressButton_pb_bgColorProgress, mBgColorProgress);
        mRadius = array.getDimensionPixelSize(R.styleable.ProgressButton_pb_bgRadius, mRadius);
        mStorkeWidth = array.getDimensionPixelSize(R.styleable.ProgressButton_pb_storkeWidth, mStorkeWidth);
        String textDef = array.getString(R.styleable.ProgressButton_pb_textDef);
        String textSucc = array.getString(R.styleable.ProgressButton_pb_textSucc);
        String textErr = array.getString(R.styleable.ProgressButton_pb_textErr);
        mDuration = array.getInt(R.styleable.ProgressButton_pb_duration, mDuration);
        array.recycle();

        if (null != textDef) {
            mStringDef = textDef;
        }
        if (null != textSucc) {
            mStringSucc = textSucc;
        }
        if (null != textErr) {
            mStringErr = textErr;
        }

        mDrawable = new GradientDrawable();
        mDrawable.setColor(mBgColorDef);
        mDrawable.setCornerRadius(mRadius);
        mDrawable.setStroke(mStorkeWidth, mBgColorDef);
        setTextColor(mTextColor);
        setText(mStringDef);
        setBackground(mDrawable);
        setGravity(Gravity.CENTER);
        setOnClickListener(this);

        initPaint();
    }

    private void initPaint(){
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mBgColorProgress);
        mPaint.setStrokeWidth(mStorkeWidth+2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mDefWidth = getMeasuredWidth();
        mDefHeight = getMeasuredHeight();
        initAnimAll();

    }


    @Override
    public void onClick(View v) {
        if (mState == STATE_IDEL && (null == mAnim || !mAnim.isRunning() )|| mState == STATE_ERR) {
            int startColor =  (mState == STATE_ERR) ? mBgColorErr : mBgColorDef;
            AnimatorSet anim = creatAnim(mDefWidth, mDefHeight, startColor, mBgColorProgress, startColor, mBgColorDef, mRadius, mDefHeight/2);
            anim.addListener(new DefAnimListener(){
                @Override
                public void onAnimationStart(Animator animation) {
                    mState = STATE_IDEL;
                    if (null != mListener) {
                        mListener.onStart();
                    }
                    setText("");
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mState = STATE_PROGRESS;
                    mAnimProgress.start();
                }
            });
            anim.start();
        } else if (mState == STATE_SUCC && mListener != null){
            mListener.onSuccess();
        } else if (mState == STATE_PROGRESS) {
            mState = STATE_IDEL;
            stopAutoProgress();
            AnimatorSet anim = creatAnim(mDefHeight, mDefWidth, mBgColorProgress, mBgColorDef, mBgColorDef, mBgColorDef, mDefHeight/2, mRadius);
            anim.addListener(new DefAnimListener(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    setText(mStringDef);
                }
            });
            anim.start();
        }
    }




    private AnimatorSet creatAnim(int startWidth, int endWidth, int startBgColor, int endBgColor,
                                  int startStrokeColor, int endStrokeColor, int startRadius, int endRadius) {

        if (null != mAnim && !mAnim.isStarted()) {
            mAnim.cancel();
        }
        ValueAnimator animWidth = ObjectAnimator.ofInt(startWidth, endWidth);
        animWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                int half = val/2;
                mDrawable.setBounds(mDefWidth/2 - half, 0, mDefWidth/2 + half, mDefHeight);
            }
        });
        ObjectAnimator animBgColor = ObjectAnimator.ofArgb(mDrawable, "color", startBgColor, endBgColor);
        ObjectAnimator animStorkeColor = ObjectAnimator.ofArgb(mDrawable, "strokeColor", startStrokeColor, endStrokeColor);
        animStorkeColor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                mDrawable.setStroke(mStorkeWidth, val);

            }
        });
        ObjectAnimator cornerAnimation =
                ObjectAnimator.ofFloat(mDrawable, "cornerRadius", startRadius, endRadius);
        mAnim = new AnimatorSet();

        mAnim.playTogether(animWidth, animBgColor, animStorkeColor, cornerAnimation);
        mAnim.setDuration(mDuration);
        return mAnim;

    }


    private void initAnimAll() {
        initProgressAnim();
    }

    private void initProgressAnim() {
        ValueAnimator anim = ObjectAnimator.ofFloat(0.1f, 0.9f);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setDuration(1000);


        mAnimProgress = new AnimatorSet();
        mAnimProgress.playTogether(anim);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (STATE_PROGRESS == mState && mPercent < 1.0f && !isAutoProgress) {
            int angle = (int) (-360*(1-mPercent));
            canvas.drawArc(mDefWidth / 2 - mDefHeight / 2 + mStorkeWidth / 2, mStorkeWidth / 2, mDefWidth / 2 + mDefHeight / 2 - mStorkeWidth / 2, mDefHeight - mStorkeWidth / 2, -90, angle, false, mPaint);
        } else if (STATE_PROGRESS == mState && mPercent < 1.0f && isAutoProgress){
            int angle = (int) (-360*(1-mPercent));
            mStartAngle += 5 ;
            canvas.drawArc(mDefWidth / 2 - mDefHeight / 2 + mStorkeWidth / 2, mStorkeWidth / 2, mDefWidth / 2 + mDefHeight / 2 - mStorkeWidth / 2, mDefHeight - mStorkeWidth / 2, mStartAngle, angle, false, mPaint);
        }
    }

    private void toFinish() {
        if (STATE_SUCC == mState) {
            AnimatorSet anim = creatAnim(mDefHeight, mDefWidth, mBgColorProgress,  mBgColorSucc, mBgColorDef, mBgColorSucc, mDefHeight/2, mRadius);
            anim.addListener(new DefAnimListener(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (null != mListener) {
                        mListener.onFinish();
                    }
                    setText(mStringSucc);
                }

            });
            anim.start();
        } else if (STATE_ERR == mState) {
            AnimatorSet anim = creatAnim(mDefHeight, mDefWidth, mBgColorProgress,  mBgColorErr, mBgColorDef, mBgColorErr, mDefHeight/2, mRadius);
            anim.addListener(new DefAnimListener(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    setText(mStringErr);
                }
            });
            anim.start();
        }
    }

    private void stopAutoProgress() {
        if (mAnimProgress.isRunning()) {
            isAutoProgress = false;
            mAnimProgress.end();
        }
    }

    public void setPercent(float percent){
        if (mState != STATE_PROGRESS) {
            return;
        }
        mPercent = percent;
        if (percent > 0 && percent < 1.0f) {
            stopAutoProgress();
            postInvalidate();
        } else {
            if (percent < 0) {
                mState = STATE_ERR;
            } else if (percent >= 1.0f) {
                mState = STATE_SUCC;
            }
            toFinish();
        }
    }


    private class DefAnimListener implements Animator.AnimatorListener{
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    private OnProgressClickListener mListener;

    public void setOnProgressClickListener(OnProgressClickListener listener) {
        mListener = listener;
    }

    public interface OnProgressClickListener{
        void onStart();
        void onFinish();
        void onSuccess();
    }

}
