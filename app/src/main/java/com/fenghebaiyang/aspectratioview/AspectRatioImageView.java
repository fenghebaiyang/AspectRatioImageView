package com.fenghebaiyang.aspectratioview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * <br/> Description:可设置宽高比的ImageView
 * <br/> Author: xiaojianfeng
 * <br/> Version: 1.0
 * <br/> Date: 2016/3/23
 */
public class AspectRatioImageView extends ImageView {

    /**
     * 图片的宽高比
     */
    private float ratio;

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView);
        String dimensionRatio = a.getString(R.styleable.AspectRatioImageView_ratio);
        float dimensionRatioValue = 0.0f / 0.0f;
        int dimensionRatioSide = -1;
        if (!TextUtils.isEmpty(dimensionRatio)) {
            int len = dimensionRatio.length();
            int commaIndex = dimensionRatio.indexOf(',');
            if (commaIndex > 0 && commaIndex < len - 1) {
                String colonIndex = dimensionRatio.substring(0, commaIndex);
                if (colonIndex.equalsIgnoreCase("W")) {
                    dimensionRatioSide = 0;
                } else if (colonIndex.equalsIgnoreCase("H")) {
                    dimensionRatioSide = 1;
                }

                ++commaIndex;
            } else {
                commaIndex = 0;
            }

            int colonIndex = dimensionRatio.indexOf(':');
            String r;
            if (colonIndex >= 0 && colonIndex < len - 1) {
                r = dimensionRatio.substring(commaIndex, colonIndex);
                String denominator = dimensionRatio.substring(colonIndex + 1);
                if (r.length() > 0 && denominator.length() > 0) {
                    try {
                        float nominatorValue = Float.parseFloat(r);
                        float denominatorValue = Float.parseFloat(denominator);
                        if (nominatorValue > 0.0F && denominatorValue > 0.0F) {
                            if (dimensionRatioSide == 1) {
                                dimensionRatioValue = Math.abs(denominatorValue / nominatorValue);
                            } else {
                                dimensionRatioValue = Math.abs(nominatorValue / denominatorValue);
                            }
                        }
                    } catch (NumberFormatException e) {
                        ;
                    }
                }
            } else {
                r = dimensionRatio.substring(commaIndex);
                if (r.length() > 0) {
                    try {
                        dimensionRatioValue = Float.parseFloat(r);
                    } catch (NumberFormatException e) {
                        ;
                    }
                }
            }
        }
        ratio = dimensionRatioValue;
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 父容器传过来的宽度方向上的模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 父容器传过来的高度方向上的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // 父容器传过来的宽度的值
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
                - getPaddingRight();
        // 父容器传过来的高度的值
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingLeft()
                - getPaddingRight();

        if (widthMode == MeasureSpec.EXACTLY
                && heightMode != MeasureSpec.EXACTLY && ratio != 0f) {
            // 判断条件为，宽度模式为Exactly，也就是填充父窗体或者是指定宽度；
            // 且高度模式不是Exaclty，代表设置的既不是fill_parent也不是具体的值，于是需要具体测量
            // 且图片的宽高比已经赋值完毕，不再是0.0f
            // 表示宽度确定，要测量高度
            height = (int) (width / ratio + 0.5f);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
        } else if (widthMode != MeasureSpec.EXACTLY
                && heightMode == MeasureSpec.EXACTLY && ratio != 0f) {
            // 判断条件跟上面的相反，宽度方向和高度方向的条件互换
            // 表示高度确定，要测量宽度
            width = (int) (height * ratio + 0.5f);

            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
                    MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }
}
