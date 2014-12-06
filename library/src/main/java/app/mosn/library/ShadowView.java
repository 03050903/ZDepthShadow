package app.mosn.library;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;


import app.mosn.library.shadow.Shadow;
import app.mosn.library.shadow.ShadowOval;
import app.mosn.library.shadow.ShadowRect;


public class ShadowView extends View {
    protected static final String TAG = "ShadowView";

    protected static final String ANIM_PROPERTY_ALPHA_TOP_SHADOW = "alphaTopShadow";
    protected static final String ANIM_PROPERTY_ALPHA_BOTTOM_SHADOW = "alphaBottomShadow";
    protected static final String ANIM_PROPERTY_OFFSET_TOP_SHADOW = "offsetTopShadow";
    protected static final String ANIM_PROPERTY_OFFSET_BOTTOM_SHADOW = "offsetBottomShadow";
    protected static final String ANIM_PROPERTY_BLUR_TOP_SHADOW = "blurTopShadow";
    protected static final String ANIM_PROPERTY_BLUR_BOTTOM_SHADOW = "blurBottomShadow";

    protected static final int DEFAULT_ATTR_SHAPE = 0;
    protected static final int DEFAULT_ATTR_ZDEPTH = 1;
    protected static final int DEFAULT_ATTR_ZDEPTH_PADDING = 5;
    protected static final int DEFAULT_ATTR_ZDEPTH_ANIM_DURATION = 150;
    protected static final boolean DEFAULT_ATTR_ZDEPTH_DO_ANIMATION = true;

    public static final int SHAPE_RECT = 0;
    public static final int SHAPE_OVAL = 1;

    protected Shadow mShadow;
    protected ZDepthParam mZDepthParam;
    protected int mZDepthPadding;
    protected long mZDepthAnimDuration;
    protected boolean mZDepthDoAnimation;

    protected int mAttrShape;
    protected int mAttrZDepth;
    protected int mAttrZDepthPadding;

    protected ShadowView(Context context) {
        super(context);
        init(null, 0);
    }

    protected ShadowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    protected ShadowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    protected void init(AttributeSet attrs, int defStyle) {
        setWillNotDraw(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        // Load attributes
        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ZDepthShadow, defStyle, 0);
        mAttrShape = typedArray.getInt(R.styleable.ZDepthShadow_z_depth_shape, DEFAULT_ATTR_SHAPE);
        mAttrZDepth = typedArray.getInt(R.styleable.ZDepthShadow_z_depth, DEFAULT_ATTR_ZDEPTH);
        mAttrZDepthPadding = typedArray.getInt(R.styleable.ZDepthShadow_z_depth_padding, DEFAULT_ATTR_ZDEPTH_PADDING);
        mZDepthAnimDuration = typedArray.getInt(R.styleable.ZDepthShadow_z_depth_animDuration, DEFAULT_ATTR_ZDEPTH_ANIM_DURATION);
        mZDepthDoAnimation = typedArray.getBoolean(R.styleable.ZDepthShadow_z_depth_doAnim, DEFAULT_ATTR_ZDEPTH_DO_ANIMATION);

        setShape(mAttrShape);
        setZDepth(mAttrZDepth);
        setZDepthPadding(mAttrZDepthPadding);

        typedArray.recycle();
    }

    protected void setZDepthDoAnimation(boolean doAnimation) {
        mZDepthDoAnimation = doAnimation;
    }

    protected void setZDepthAnimDuration(long duration) {
        mZDepthAnimDuration = duration;
    }

    protected void setZDepthPadding(int zDepthPaddingValue) {
        ZDepth zDepth = getZDepthWithAttributeValue(zDepthPaddingValue);
        setZDepthPadding(zDepth);
    }

    protected void setZDepthPadding(ZDepth zDepth) {
        float maxAboveBlurRadius = zDepth.getBlurTopShadowPx(getContext());
        float maxAboveOffset     = zDepth.getOffsetYTopShadowPx(getContext());
        float maxBelowBlurRadius = zDepth.getBlurBottomShadowPx(getContext());
        float maxBelowOffset     = zDepth.getOffsetYBottomShadowPx(getContext());

        float maxAboveSize = maxAboveBlurRadius + maxAboveOffset;
        float maxBelowSize = maxBelowBlurRadius + maxBelowOffset;

        mZDepthPadding = (int) Math.max(maxAboveSize, maxBelowSize);
    }

    protected int getZDepthPadding() {
        return mZDepthPadding;
    }

    protected void setShape(int shape) {
        switch (shape) {
            case SHAPE_RECT:
                mShadow = new ShadowRect();
                break;

            case SHAPE_OVAL:
                mShadow = new ShadowOval();
                break;

            default:
                throw new IllegalArgumentException("unknown shape value.");
        }
    }

    protected void setZDepth(int zDepthValue) {
        ZDepth zDepth = getZDepthWithAttributeValue(zDepthValue);
        setZDepth(zDepth);
    }

    protected void setZDepth(ZDepth zDepth) {
        mZDepthParam = new ZDepthParam();
        mZDepthParam.initZDepth(getContext(), zDepth);
    }

    private ZDepth getZDepthWithAttributeValue(int zDepthValue) {
        switch (zDepthValue) {
            case 0: return ZDepth.Depth0;
            case 1: return ZDepth.Depth1;
            case 2: return ZDepth.Depth2;
            case 3: return ZDepth.Depth3;
            case 4: return ZDepth.Depth4;
            case 5: return ZDepth.Depth5;
            default: throw new IllegalArgumentException("unknown zDepth value.");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        switch (wMode) {
            case MeasureSpec.EXACTLY:
                // NOP
                break;

            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                wSize = 0;
                break;
        }

        switch (hMode) {
            case MeasureSpec.EXACTLY:
                // NOP
                break;

            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                hSize = 0;
                break;
        }

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(wSize, wMode),
                MeasureSpec.makeMeasureSpec(hSize, hMode));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int parentWidth  = (right - left);
        int parentHeight = (bottom - top);

        mShadow.setParameter(mZDepthParam,
                mZDepthPadding,
                mZDepthPadding,
                parentWidth  - mZDepthPadding,
                parentHeight - mZDepthPadding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mShadow.onDraw(canvas);
    }

    protected void changeZDepth(ZDepth zDepth) {

        if (!mZDepthDoAnimation) {
            mZDepthParam.mColorAlphaTopShadow = zDepth.getColorAlphaTopShadow();
            mZDepthParam.mColorAlphaBottomShadow = zDepth.getColorAlphaBottomShadow();
            mZDepthParam.mOffsetYTopShadowPx = zDepth.getOffsetYTopShadowPx(getContext());
            mZDepthParam.mOffsetYBottomShadowPx = zDepth.getOffsetYBottomShadowPx(getContext());
            mZDepthParam.mBlurRadiusTopShadowPx = zDepth.getBlurTopShadowPx(getContext());
            mZDepthParam.mBlurRadiusBottomShadowPx = zDepth.getBlurBottomShadowPx(getContext());

            mShadow.setParameter(mZDepthParam, mZDepthPadding, mZDepthPadding, getWidth() - mZDepthPadding, getHeight() - mZDepthPadding);
            invalidate();
            return;
        }

        PropertyValuesHolder alphaTopShadowHolder     = PropertyValuesHolder
                .ofInt(ANIM_PROPERTY_ALPHA_TOP_SHADOW, mZDepthParam.mColorAlphaTopShadow, zDepth.getColorAlphaTopShadow());
        PropertyValuesHolder alphaBottomShadowHolder  = PropertyValuesHolder
                .ofInt(ANIM_PROPERTY_ALPHA_BOTTOM_SHADOW, mZDepthParam.mColorAlphaBottomShadow, zDepth.getColorAlphaBottomShadow());
        PropertyValuesHolder offsetTopShadowHolder    = PropertyValuesHolder
                .ofFloat(ANIM_PROPERTY_OFFSET_TOP_SHADOW, mZDepthParam.mOffsetYTopShadowPx, zDepth.getOffsetYTopShadowPx(getContext()));
        PropertyValuesHolder offsetBottomShadowHolder = PropertyValuesHolder
                .ofFloat(ANIM_PROPERTY_OFFSET_BOTTOM_SHADOW, mZDepthParam.mOffsetYBottomShadowPx, zDepth.getOffsetYBottomShadowPx(getContext()));
        PropertyValuesHolder blurTopShadowHolder      = PropertyValuesHolder
                .ofFloat(ANIM_PROPERTY_BLUR_TOP_SHADOW, mZDepthParam.mBlurRadiusTopShadowPx, zDepth.getBlurTopShadowPx(getContext()));
        PropertyValuesHolder blurBottomShadowHolder   = PropertyValuesHolder
                .ofFloat(ANIM_PROPERTY_BLUR_BOTTOM_SHADOW, mZDepthParam.mBlurRadiusBottomShadowPx, zDepth.getBlurBottomShadowPx(getContext()));

        ValueAnimator anim = ValueAnimator
                .ofPropertyValuesHolder(
                        alphaTopShadowHolder,
                        alphaBottomShadowHolder,
                        offsetTopShadowHolder,
                        offsetBottomShadowHolder,
                        blurTopShadowHolder,
                        blurBottomShadowHolder);
        anim.setDuration(mZDepthAnimDuration);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int   alphaTopShadow     = (Integer) animation.getAnimatedValue(ANIM_PROPERTY_ALPHA_TOP_SHADOW);
                int   alphaBottomShadow  = (Integer) animation.getAnimatedValue(ANIM_PROPERTY_ALPHA_BOTTOM_SHADOW);
                float offsetTopShadow    = (Float) animation.getAnimatedValue(ANIM_PROPERTY_OFFSET_TOP_SHADOW);
                float offsetBottomShadow = (Float) animation.getAnimatedValue(ANIM_PROPERTY_OFFSET_BOTTOM_SHADOW);
                float blurTopShadow      = (Float) animation.getAnimatedValue(ANIM_PROPERTY_BLUR_TOP_SHADOW);
                float blurBottomShadow   = (Float) animation.getAnimatedValue(ANIM_PROPERTY_BLUR_BOTTOM_SHADOW);

                mZDepthParam.mColorAlphaTopShadow = alphaTopShadow;
                mZDepthParam.mColorAlphaBottomShadow = alphaBottomShadow;
                mZDepthParam.mOffsetYTopShadowPx = offsetTopShadow;
                mZDepthParam.mOffsetYBottomShadowPx = offsetBottomShadow;
                mZDepthParam.mBlurRadiusTopShadowPx = blurTopShadow;
                mZDepthParam.mBlurRadiusBottomShadowPx = blurBottomShadow;

                mShadow.setParameter(mZDepthParam, mZDepthPadding, mZDepthPadding, getWidth() - mZDepthPadding, getHeight() - mZDepthPadding); 

                invalidate();
             }
         });
        anim.start();
    }
}
