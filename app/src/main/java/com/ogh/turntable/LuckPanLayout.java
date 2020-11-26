package com.ogh.turntable;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * https://github.com/Nipuream/LuckPan
 */
public class LuckPanLayout extends RelativeLayout {

    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint onePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint twoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int radius;
    private int CircleX, CircleY;
    private Canvas canvas;
    private boolean isYellow = false;
    private int delayTime = 500;
    private RotatePan rotatePan;
    private ImageView startBtn;
    private int screenWidth, screeHeight;
    private int MinValue;
    private int bgColor = getResources().getColor(R.color.bgColor);
    private int color1 = Color.WHITE;
    private int color2 = getResources().getColor(R.color.color1);
    /**
     * LuckPan 中间对应的Button必须设置tag为 startbtn.
     */
    private static final String START_BTN_TAG = "startbtn";
    public static final int DEFAULT_TIME_PERIOD = 500;

    public LuckPanLayout(Context context) {
        super(context);
        initView(context,null);
    }

    public LuckPanLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    public LuckPanLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }

    private void initView(Context context,AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LuckPan);
            bgColor = array.getColor(R.styleable.LuckPanLayot_LPLBgColor, getResources().getColor(R.color.bgColor));
            color1 = array.getColor(R.styleable.LuckPanLayot_LPLColor1, Color.WHITE);
            color2 = array.getColor(R.styleable.LuckPanLayot_LPLColor1, getResources().getColor(R.color.color1));
            array.recycle();
        }
        backgroundPaint.setColor(bgColor);
        onePaint.setColor(color1);
        twoPaint.setColor(color2);
        screeHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        startLuckLight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        MinValue = Math.min(screenWidth, screeHeight);
        MinValue -= dp2px(10) * 2;
        setMeasuredDimension(MinValue, MinValue);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;
        int MinValue = Math.min(width, height);
        radius = MinValue / 2;
        CircleX = getWidth() / 2;
        CircleY = getHeight() / 2;
        canvas.drawCircle(CircleX, CircleY, radius, backgroundPaint);
        drawSmallCircle(isYellow);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int centerX = (right - left) / 2;
        int centerY = (bottom - top) / 2;
        boolean panReady = false;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof RotatePan) {
                rotatePan = (RotatePan) child;
                int panWidth = child.getWidth();
                int panHeight = child.getHeight();
                child.layout(centerX - panWidth / 2, centerY - panHeight / 2, centerX + panWidth / 2, centerY + panHeight / 2);
                panReady = true;
            } else if (child instanceof ImageView) {
                if (TextUtils.equals((String) child.getTag(), START_BTN_TAG)) {
                    startBtn = (ImageView) child;
                    int btnWidth = child.getWidth();
                    int btnHeight = child.getHeight();
                    child.layout(centerX - btnWidth / 2, centerY - btnHeight / 2, centerX + btnWidth / 2, centerY + btnHeight / 2);
                }
            }
        }
        if (!panReady)
            throw new RuntimeException("Have you add RotatePan in LuckPanLayout element ?");
    }

    private void drawSmallCircle(boolean FirstYellow) {
        int pointDistance = radius - dp2px(10);
        for (int i = 0; i <= 360; i += 20) {
            int x = (int) (pointDistance * Math.sin((i * Math.PI / 180))) + CircleX;
            int y = (int) (pointDistance * Math.cos((i * Math.PI / 180))) + CircleY;
            if (FirstYellow)
                canvas.drawCircle(x, y, dp2px(4), twoPaint);
            else
                canvas.drawCircle(x, y, dp2px(4), onePaint);
            FirstYellow = !FirstYellow;
        }
    }

    private int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 开始旋转
     *
     * @param pos       转到指定的转盘，-1 则随机
     * @param delayTime 外围灯光闪烁的间隔时间
     */
    public void rotate(int pos, int delayTime) {
        rotatePan.startRotate(pos);
        setDelayTime(delayTime);
        setStartBtnEnable(false);
    }

    public List<String> getNames() {
        return rotatePan.getNames();
    }

    public List<Bitmap> getImgs() {
        return rotatePan.getImgs();
    }

    protected void setStartBtnEnable(boolean enable) {
        if (startBtn != null)
            startBtn.setEnabled(enable);
        else throw new RuntimeException("Have you add start button in LuckPanLayout element ?");
    }

    private void startLuckLight() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                isYellow = !isYellow;
                invalidate();
                postDelayed(this, delayTime);
            }
        }, delayTime);
    }

    protected void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public interface AnimationEndListener {
        void endAnimation(int position);
    }

    private AnimationEndListener endListener;

    public void setAnimationEndListener(AnimationEndListener endListener) {
        this.endListener = endListener;
    }

    public AnimationEndListener getAnimationEndListener() {
        return endListener;
    }

}
