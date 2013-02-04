/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.command.implementation;

import org.catrobat.paintroid.PaintroidApplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

public class ColorToTransparencyCommand extends BaseCommand {

	private static final double THRESHOLD = 10.0;

	protected Point mColorPixel;

	public ColorToTransparencyCommand(Paint paint, PointF coordinate) {
		super(paint);
		if (coordinate != null) {
			mColorPixel = new Point((int) coordinate.x, (int) coordinate.y);
		} else {
			mColorPixel = new Point(-1, -1);
		}
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		if (bitmap == null) {
			return;
		}
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		if ((bitmapWidth <= mColorPixel.x)
				|| (bitmapHeight <= mColorPixel.y || (0 > mColorPixel.x) || (0 > mColorPixel.y))) {
			Log.w(PaintroidApplication.TAG,
					"Point is out of range " + this.toString());
			setChanged();
			notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
			return;
		}

		int pixelColor = bitmap.getPixel(mColorPixel.x, mColorPixel.y);
		int bitmapPixels = bitmapHeight * bitmapWidth;
		int colorToReplaceWith = mPaint.getColor();
		if (colorToReplaceWith == pixelColor) {
			Log.i(PaintroidApplication.TAG, "Same colour nothing to replace");
			setChanged();
			notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
			return;
		}
		int[] pixelArray = new int[bitmapPixels];

		bitmap.getPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth,
				bitmapHeight);

		for (int index = 0; index < bitmapPixels; index++) {
			if (isSimilarColor(pixelColor, pixelArray[index])) {
				pixelArray[index] = colorToReplaceWith;
			}
		}

		bitmap.setPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth,
				bitmapHeight);
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}

	private boolean isSimilarColor(int color1, int color2) {

		int red1 = Color.red(color1);
		int red2 = Color.red(color2);
		int green1 = Color.red(color1);
		int green2 = Color.red(color2);
		int blue1 = Color.red(color1);
		int blue2 = Color.red(color2);

		double diff = Math
				.sqrt(Math.pow((red2 - red1), 2)
						+ Math.pow((green2 - green1), 2)
						+ Math.pow((blue2 - blue1), 2));

		return diff < THRESHOLD;
	}

}
