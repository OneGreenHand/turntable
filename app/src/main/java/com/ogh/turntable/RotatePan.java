package com.ogh.turntable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
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
    private int color1 = getResources().getColor(R.color.color1);
    private int color2 = getResources().getColor(R.color.color2);
    private int color3 = getResources().getColor(R.color.color3);//转盘数量为奇数时有效
    private int textColor = Color.WHITE;
    private int textSize = 16;
    private List<String> mNames = new ArrayList<>();//转盘内容
    private List<Bitmap> mBitmaps = new ArrayList<>();//转盘图片
    private int panNum;//转盘总个数
    private int screenWidth, screeHeight;
    private static final long ONE_WHEEL_TIME = 500;//旋转一圈所需要的时间

    public RotatePan(Context context) {
        super(context);
        initView(context, null);
    }

    public RotatePan(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public RotatePan(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        initData();
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LuckPan);
            color1 = array.getColor(R.styleable.LuckPan_LPColor1, getResources().getColor(R.color.color1));
            color2 = array.getColor(R.styleable.LuckPan_LPColor2, getResources().getColor(R.color.color2));
            color3 = array.getColor(R.styleable.LuckPan_LPColor3, getResources().getColor(R.color.color3));
            textColor = array.getColor(R.styleable.LuckPan_LPTextColor, Color.WHITE);
            textSize = array.getInteger(R.styleable.LuckPan_LPTextSize, 16);
            array.recycle();
        }
        screeHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        InitAngle = 360 / panNum;
        verPanRadius = 360 / panNum;
        diffRadius = verPanRadius / 2;
        onePaint.setColor(color1);
        twoPaint.setColor(color2);
        threePaint.setColor(color3);
        textPaint.setColor(textColor);
        textPaint.setTextSize(dp2px(textSize));
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
        for (int i = 0; i < mBitmaps.size(); i++) {//绘制图片
            drawIcon(width / 2, height / 2, radius, (panNum % 4 == 0) ? InitAngle + diffRadius : InitAngle, i, canvas);
            InitAngle += verPanRadius;
        }
        for (int i = 0; i < mNames.size(); i++) {//绘制文字
            drawText((panNum % 4 == 0) ? InitAngle + diffRadius + (diffRadius * 3 / 4) : InitAngle + diffRadius, mNames.get(i), 2 * radius, textPaint, canvas, rectF);
            InitAngle += verPanRadius;
        }
    }

    private void drawIcon(int xx, int yy, int mRadius, float startAngle, int i, Canvas mCanvas) {
        int imgWidth = mRadius / 4;
        float angle = (float) Math.toRadians(verPanRadius + startAngle);
        float x = (float) (xx + (mRadius / 2 + mRadius / 12) * Math.cos(angle));   //确定图片在圆弧中 中心点的位置
        float y = (float) (yy + (mRadius / 2 + mRadius / 12) * Math.sin(angle));
        RectF rect = new RectF(x - imgWidth * 2 / 3, y - imgWidth * 2 / 3, x + imgWidth * 2 / 3, y + imgWidth * 2 / 3);  // 确定绘制图片的位置
        mCanvas.drawBitmap(mBitmaps.get(i), null, rect, null);
    }

    private void drawText(float startAngle, String string, int mRadius, Paint mTextPaint, Canvas mCanvas, RectF mRange) {
        Path path = new Path();
        path.addArc(mRange, startAngle, verPanRadius);
        float textWidth = mTextPaint.measureText(string);
        float hOffset = (panNum % 4 == 0) ? ((float) (mRadius * Math.PI / panNum / 2)) : ((float) (mRadius * Math.PI / panNum / 2 - textWidth / 2)); //圆弧的水平偏移
        float vOffset = mRadius / 2 / 6;//圆弧的垂直偏移
        mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
    }

    /**
     * 初始化数据,防止不写数据闪退
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
     * 设置数据
     */
    public void setNames(List<String> names) {
        if (names == null || names.isEmpty())
            return;
        this.panNum = names.size();
        this.mBitmaps.clear();
        this.mNames.clear();
        this.mNames.addAll(names);
        calculation();
    }

    public void setImgs(List<Bitmap> imgs) {
        if (imgs == null || imgs.isEmpty())
            return;
        this.panNum = imgs.size();
        this.mNames.clear();
        this.mBitmaps.clear();
        this.mBitmaps.addAll(imgs);
        calculation();
    }

    public void setDatas(List<String> names, List<Bitmap> imgs) {
        if (names == null || names.isEmpty() || imgs == null || imgs.isEmpty() || imgs.size() != names.size())
            return;
        this.panNum = names.size();
        this.mNames.clear();
        this.mNames.addAll(names);
        this.mBitmaps.clear();
        this.mBitmaps.addAll(imgs);
        calculation();
    }

    private void calculation() {
        InitAngle = 360 / panNum;
        verPanRadius = 360 / panNum;
        diffRadius = verPanRadius / 2;
        this.invalidate();
    }

    /**
     * 获取数据源
     */
    public List<String> getNames() {
        return mNames;
    }

    public List<Bitmap> getImgs() {
        return mBitmaps;
    }

    /**
     * 开始转动
     *
     * @param pos 如果 pos = -1 则随机，如果指定某个值，则转到某个指定区域
     */
    protected void startRotate(int pos) {
        int lap = (int) (Math.random() * 12) + 4;  //Rotate lap.
        int angle = 0; //Rotate angle.
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