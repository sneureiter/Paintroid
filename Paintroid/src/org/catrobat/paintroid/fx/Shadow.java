package org.catrobat.paintroid.fx;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class Shadow {
	public static Bitmap doMagic(Bitmap bitmap, int x_offset, int y_offset) {

		Bitmap tmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
				bitmap.getConfig());

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		for (int i = 0; i < h - y_offset; i++) {
			for (int j = 0; j < w - x_offset; j++) {
				// jede Zeile
				if (bitmap.getPixel(j, i) != Color.TRANSPARENT) {
					pix[(i + y_offset) * w + j + x_offset] = 0x66666666;
				}
			}
		}
		tmp.setPixels(pix, 0, w, 0, 0, w, h);
		tmp = Blur.doMagic(tmp, 20);

		pix = new int[w * h];

		for (int i = 0; i < h - y_offset; i++) {
			for (int j = 0; j < w - x_offset; j++) {
				if (bitmap.getPixel(j, i) == Color.TRANSPARENT) {
					pix[i * w + j] = tmp.getPixel(j, i);
				} else {
					pix[i * w + j] = bitmap.getPixel(j, i);
				}
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		Log.e("shadow", w + " " + h + " " + pix.length);
		return bitmap;
	}
}
