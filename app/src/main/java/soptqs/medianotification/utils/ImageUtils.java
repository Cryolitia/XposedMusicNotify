package soptqs.medianotification.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import static android.content.ContentValues.TAG;

public class ImageUtils {


    public static Bitmap getVectorBitmap(Context context, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            try {
                return drawableToBitmap(ContextCompat.getDrawable(context, id));
            } catch (Resources.NotFoundException e) {
                return drawableToBitmap(null);
            }
        }

        Drawable drawable;
        try {
            drawable = VectorDrawableCompat.create(context.getResources(), id, context.getTheme());
        } catch (Resources.NotFoundException e1) {
            try {
                drawable = ContextCompat.getDrawable(context, id);
            } catch (Resources.NotFoundException e2) {
                return drawableToBitmap(null);
            }
        }

        if (drawable != null) {
            Bitmap result = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return result;
        }

        return null;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null)
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_4444);

        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null)
                return bitmapDrawable.getBitmap();
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0)
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        else
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap drawableToBitmap(Drawable drawable, float scale) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null)
                return bitmapDrawable.getBitmap();
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0)
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        else
            bitmap = Bitmap.createBitmap((int) (scale * drawable.getIntrinsicWidth()), (int) (scale * drawable.getIntrinsicHeight()), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap setBitmapColor(Bitmap bitmap, int color) {
        Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(result, 0, 0, paint);

        return result;
    }

    /**
     * @param bitmap 原图
     * @return 缩放截取正中部分后的位图。
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap) {


        int edgeLength;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();
        int xTopLeft;
        int yTopLeft;


        if (null == bitmap) {
            return null;
        }

        Bitmap result = bitmap;

        edgeLength = Math.min(widthOrg, heightOrg);


        if (widthOrg >= 0 || heightOrg >= 0) {
            //从图中截取正中间的正方形部分。
            if (widthOrg != heightOrg) {
//                edgeLength = heightOrg;
                xTopLeft = (widthOrg - edgeLength) / 2;
                yTopLeft = (heightOrg - edgeLength) / 2;
                try {
                    result = Bitmap.createBitmap(bitmap, xTopLeft, yTopLeft, edgeLength - 1, edgeLength - 1, null, false);
                } catch (Exception e) {
                    Log.e(TAG, "centerSquareScaleBitmap: Error croping " + e);
                }
                return result;
            } else if (widthOrg == heightOrg) {
                result = bitmap;
                return result;
            }
        }

        return result;
    }


////    渐变处理
//
//    public static Bitmap masklargeicon(Bitmap maskBitmap,Bitmap bitmap){
//
//        final int WITHOUT = -1;
//        final int FRAME = 0;
//        final int MASK = 1;
//
//        int[] mask = new int[]{
//                WITHOUT, R.drawable.icon_mask
//        };
//
//        int length = bitmap.getWidth();
//
//        Bitmap resultBitmap = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
//        maskBitmap = Bitmap.createScaledBitmap(maskBitmap,length,length,false);
//
//        int[] picPixels = new int[length*length];
//        int[] maskPixels = new int[length*length];
//        Log.e("mask", "lenth: "+length);
//        Log.e("mask", "bitmapw"+bitmap.getWidth());
//        Log.e("mask", "bitmaph: "+bitmap.getHeight());
//        Log.e("mask", "maskbitmapw"+maskBitmap.getWidth());
//        Log.e("mask", "maskbitmaph: "+maskBitmap.getHeight());
//
//        bitmap.getPixels(picPixels,0, length,0,0, length, length);
//        maskBitmap.getPixels(maskPixels,0, length,0,0, length, length);
//
////        for(int i = 0; i < maskPixels.length; i++){
////            if(maskPixels[i] == 0xFF000000){
////                picPixels[i] = 0;
////            }else if(maskPixels[i] == 0){
////                //donothing
////            }else{
////                //把mask的a通道应用与picBitmap
//////                maskPixels[i] &= 0xFF000000;
////                maskPixels[i] = 0xFF000000 - maskPixels[i];
//////                picPixels[i] &= 0x00FFFFFF;
//////                picPixels[i] |= maskPixels[i];
////            }
////        }
//
//        //生成前置图片添加蒙板后的bitmap:resultBitmap
//        resultBitmap.setPixels(picPixels, 0, length, 0, 0, length, length);
//
//        return resultBitmap;
//    }


}