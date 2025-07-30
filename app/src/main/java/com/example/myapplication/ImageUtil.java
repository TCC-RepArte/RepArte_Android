// Arquivo: ImageUtil.java
package com.example.myapplication; // Ou seu pacote de utilitários

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

public class ImageUtil {

    public static Bitmap createCircleBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int minEdge = Math.min(width, height);

        // Cria um bitmap quadrado a partir do centro do bitmap original
        Bitmap squaredBitmap = Bitmap.createBitmap(bitmap, (width - minEdge) / 2, (height - minEdge) / 2, minEdge, minEdge);

        Bitmap result = Bitmap.createBitmap(minEdge, minEdge, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        // BitmapShader preenche a forma (círculo) com o bitmap
        BitmapShader shader = new BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setAntiAlias(true);
        paint.setShader(shader);

        float radius = minEdge / 2f;
        canvas.drawCircle(radius, radius, radius, paint);

        if (squaredBitmap != bitmap) {
            // squaredBitmap.recycle();
        }

        return result;
    }
}

