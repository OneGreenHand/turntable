package com.ogh.turntable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.core.view.ViewCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/Nipuream/LuckPan
 */
public class RotatePan extends View {

    private Paint onePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint twoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint threePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int InitAngle;
    private int radius;
    private int verPanRadius;
    private int diffRadius;
    private List<String> mNames = new ArrayList<>();//转盘内容
    private int panNum;//转盘总个数
    private int screenWidth, screeHeight;
    private static final long ONE_WHEEL_TIME = 500;//旋转一圈所需要的时间

    public RotatePan(Context context) {
        super(context);
        initView(context);
    }

    public RotatePan(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RotatePan(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        initData();
        screeHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        InitAngle = 360 / panNum;
        verPanRadius = 360 / panNum;
        diffRadius = verPanRadius / 2;
        onePaint.setColor(Color.parseColor("#FE645A"));
        twoPaint.setColor(Color.parseColor("#169C2D"));
        threePaint.setColor(Color.parseColor("#FEB043"));
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(dp2px(16));
        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int MinValue = Math.min(screenWidth, screeHeight);
        MinValue -= dp2px(38) * 2;
        setMeasuredDimension(MinValue, MinValue);
    }

    private int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;
        int MinValue = Math.min(width, height);
        radius = MinValue / 2;
        RectF rectF = new RectF(getPaddingLeft(), getPaddingTop(), width, height);
        int angle = (panNum % 4 == 0) ? InitAngle : InitAngle - diffRadius;
        if (panNum % 2 == 0) {//偶数就只有两种颜色
            for (int i = 0; i < panNum; i++) {
                canvas.drawArc(rectF, angle, verPanRadius, true, i % 2 == 0 ? onePaint : twoPaint);
                angle += verPanRadius;
            }
        } else {//奇数有三种颜色
            if (panNum % 3 == 1) {//需要特殊处理 7 13 19 25等数字（不然首尾颜色会一样）
                for (int i = 1; i <= panNum; i++) {
                    if (i == panNum) {//最后一个
                        canvas.drawArc(rectF, angle, verPanRadius, true, twoPaint);
                    } else if (i % 3 == 1) {//第一个
                        canvas.drawArc(rectF, angle, verPanRadius, true, onePaint);
                    } else if (i % 3 == 2) {//第二个
                        canvas.drawArc(rectF, angle, verPanRadius, true, twoPaint);
                    } else//第三个
                        canvas.drawArc(rectF, angle, verPanRadius, true, threePaint);
                    angle += verPanRadius;
                }
            } else {
                for (int i = 1; i <= panNum; i++) {
                    if (i % 3 == 1) {//第1个
                        canvas.drawArc(rectF, angle, verPanRadius, true, onePaint);
                    } else if (i % 3 == 2) {//第二个
                        canvas.drawArc(rectF, angle, verPanRadius, true, twoPaint);
                    } else //第三个
                        canvas.drawArc(rectF, angle, verPanRadius, true, threePaint);
                    angle += verPanRadius;
                }
            }
        }
        for (int i = 0; i < panNum; i++) {
            drawText((panNum % 4 == 0) ? InitAngle + diffRadius + (diffRadius * 3 / 4) : InitAngle + diffRadius, mNames.get(i), 2 * radius, textPaint, canvas, rectF);
            InitAngle += verPanRadius;
        }
    }

    private void drawText(float startAngle, String string, int mRadius, Paint mTextPaint, Canvas mCanvas, RectF mRange) {
        Path path = new Path();
        path.addArc(mRange, startAngle, verPanRadius);
        float textWidth = mTextPaint.measureText(string);
        //圆弧的水平偏移
        float hOffset = (panNum % 4 == 0) ? ((float) (mRadius * Math.PI / panNum / 2)) : ((float) (mRadius * Math.PI / panNum / 2 - textWidth / 2));
        //圆弧的垂直偏移
        float vOffset = mRadius / 2 / 6;
        mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
    }

    /**
     * 初始化数据,防止不写数据闪退问题
     */
    private void initData() {
        mNames.add("牛扒");
        mNames.add("挂面");
        mNames.add("饺子");
        mNames.add("泡面");
        mNames.add("火锅");
        mNames.add("西餐");
        mNames.add("包子");
        mNames.add("喝粥");
        panNum = mNames.size();
    }

    /**
     * 设置数据源
     */
    public void setDatas(List<String> names) {
        if (names == null || names.isEmpty())
            return;
        this.panNum = names.size();
        this.mNames.clear();
        this.mNames.addAll(names);
        InitAngle = 360 / panNum;
        verPanRadius = 360 / panNum;
        diffRadius = verPanRadius / 2;
        this.invalidate();
    }

    /**
     * 获取数据源
     */
    public List<String> getDatas() {
        return mNames;
    }

    /**
     * 开始转动
     *
     * @param pos 如果 pos = -1 则随机，如果指定某个值，则转到某个指定区域
     */
    protected void startRotate(int pos) {
        //Rotate lap.
        int lap = (int) (Math.random() * 12) + 4;
        //Rotate angle.
        int angle = 0;
        if (pos < 0) {
            angle = (int) (Math.random() * 360);
        } else {
            int initPos = queryPosition();
            if (pos > initPos) {
                angle = (pos - initPos) * verPanRadius;
                lap -= 1;
                angle = 360 - angle;
            } else if (pos < initPos) {
                angle = (initPos - pos) * verPanRadius;
            }
        }
        //All of the rotate angle.
        int increaseDegree = lap * 360 + angle;
        long time = (lap + angle / 360) * ONE_WHEEL_TIME;
        int DesRotate = increaseDegree + InitAngle;
        // 为了每次都能旋转到转盘的中间位置
        int offRotate = DesRotate % 360 % verPanRadius;
        DesRotate -= offRotate;
        DesRotate += diffRadius;
        ValueAnimator animtor = ValueAnimator.ofInt(InitAngle, DesRotate);
        animtor.setInterpolator(new AccelerateDecelerateInterpolator());
        animtor.setDuration(time);
        animtor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int updateValue = (int) animation.getAnimatedValue();
                InitAngle = (updateValue % 360 + 360) % 360;
                ViewCompat.postInvalidateOnAnimation(RotatePan.this);
            }
        });
        animtor.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (((LuckPanLayout) getParent()).getAnimationEndListener() != null) {
                    ((LuckPanLayout) getParent()).setStartBtnEnable(true);
                    ((LuckPanLayout) getParent()).setDelayTime(LuckPanLayout.DEFAULT_TIME_PERIOD);
                    ((LuckPanLayout) getParent()).getAnimationEndListener().endAnimation(queryPosition());
                }
            }
        });
        animtor.start();
    }

    private int queryPosition() {
        InitAngle = (InitAngle % 360 + 360) % 360;
        int pos = InitAngle / verPanRadius;
        if (panNum == 2 || panNum == 7 || panNum == 9 || panNum == 10) {
            int num = calcumAngle(pos);
            return num == panNum - 1 ? 0 : ++num;
        } else if (panNum == 4) pos++;
        return calcumAngle(pos);
    }

    private int calcumAngle(int pos) {
        if (pos >= 0 && pos <= panNum / 2) {
            pos = panNum / 2 - pos;
        } else
            pos = (panNum - pos) + panNum / 2;
        return pos;
    }

    @Override
    protected void onDetachedFromWindow() {
        clearAnimation();
        if (getParent() instanceof LuckPanLayout)
            ((LuckPanLayout) getParent()).getHandler().removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

}