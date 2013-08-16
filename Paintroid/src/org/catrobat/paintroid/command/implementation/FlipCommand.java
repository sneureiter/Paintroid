/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

public class FlipCommand extends BaseCommand {

	private FlipDirection mFlipDirection;
	private Rect mRectangleToFlip;

	public static enum FlipDirection {
		FLIP_HORIZONTAL, FLIP_VERTICAL
	};

	public FlipCommand(FlipDirection flipDirection, Rect rectangleToFlip) {
		mFlipDirection = flipDirection;
		mRectangleToFlip = rectangleToFlip;
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);
		if (mFlipDirection == null) {
			setChanged();
			notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
			return;
		}

		Matrix flipMatrix = new Matrix();

		switch (mFlipDirection) {
		case FLIP_HORIZONTAL:
			flipMatrix.setScale(1, -1);
			flipMatrix.postTranslate(0, mRectangleToFlip.bottom
					- mRectangleToFlip.top);
			break;
		case FLIP_VERTICAL:
			flipMatrix.setScale(-1, 1);
			flipMatrix.postTranslate(mRectangleToFlip.right
					- mRectangleToFlip.left, 0);
			break;
		default:
			setChanged();
			notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
			return;
		}

		Bitmap flippedPortionBitmap = Bitmap.createBitmap(
				mRectangleToFlip.right, mRectangleToFlip.bottom,
				bitmap.getConfig());
		Canvas flipPortionCanvas = new Canvas(flippedPortionBitmap);

		Rect rectFlippedPortion = new Rect(0, 0,
				flippedPortionBitmap.getWidth(),
				flippedPortionBitmap.getHeight());

		flipPortionCanvas.drawBitmap(flippedPortionBitmap, mRectangleToFlip,
				rectFlippedPortion, new Paint());
		flipPortionCanvas.drawBitmap(bitmap, flipMatrix, new Paint());

		Bitmap flippedBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), bitmap.getConfig());
		Canvas flippedCanvas = new Canvas(flippedBitmap);

		Rect rectFlipped = new Rect(0, 0, flippedBitmap.getWidth(),
				flippedBitmap.getHeight());

		flippedCanvas.drawBitmap(flippedPortionBitmap, rectFlippedPortion,
				rectFlipped, new Paint());

		if (PaintroidApplication.drawingSurface != null) {
			PaintroidApplication.drawingSurface.setBitmap(flippedBitmap);
		}

		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}
}
