package app.mosn.zdepthshadowsample.fam;

import android.content.Context;
import android.util.AttributeSet;

import app.mosn.library.ShadowView;

public class FloatingActionMenuToggle extends FloatingActionMenuButton {

    public FloatingActionMenuToggle(Context context) {
        super(context);
    }

    @Override
    protected void init(AttributeSet attrs, int defStyle) {
        super.init(attrs, defStyle);

        mButtonSizeDp = 56;
        mAttrShape = SHAPE_OVAL;
        mAttrZDepth = 2;
        mAttrZDepthPadding = 4;
        mAttrZDepthDoAnimation = true;
        mAttrZDepthAnimDuration = 150;
    }
}
