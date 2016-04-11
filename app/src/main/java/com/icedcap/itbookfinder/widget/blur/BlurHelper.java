package com.icedcap.itbookfinder.widget.blur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
//import android.renderscript.Allocation;
//import android.renderscript.Element;
//import android.renderscript.RenderScript;
//import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

/**
 * Author: doushuqi
 * Date: 16-3-28
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class BlurHelper {

    private static final int DEFAULT_BLUR_RADIUS = 10;

    public static Bitmap apply(Context context, Bitmap sentBitmap) {
        return apply(context, sentBitmap, DEFAULT_BLUR_RADIUS);
    }


    public static Bitmap apply(Context context, Bitmap sentBitmap, int radius) {
        if (null == sentBitmap){
            return null;
        }
        final Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        RenderScript rs = RenderScript.create(context);
        final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);

        sentBitmap.recycle();
        rs.destroy();
        input.destroy();
        output.destroy();
        script.destroy();

        return bitmap;
    }

    public static Bitmap getViewBitmap(View v) {
        if(v.getWidth() == 0 || v.getHeight() == 0)
            return null;
        Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }
}
