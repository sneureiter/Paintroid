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

package org.catrobat.paintroid.tools.implementation;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.StampCommand;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.button.ToolbarButton.ToolButtonIDs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

public class StampTool extends BaseToolWithRectangleShape {

	private static final boolean ROTATION_ENABLED = true;
	private static final boolean RESPECT_IMAGE_BOUNDS = false;

	public StampTool(Context context, ToolType toolType) {
		super(context, toolType);

		setRotationEnabled(ROTATION_ENABLED);
		setRespectImageBounds(RESPECT_IMAGE_BOUNDS);
	}

	@Override
	public int getAttributeButtonColor(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP:

			return Color.TRANSPARENT;
		default:
			return super.getAttributeButtonColor(buttonNumber);
		}
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		// no clicks wanted
	}

	protected void createAndSetBitmap(DrawingSurface drawingSurface) {
		Point leftTopBoxCoord = new Point((int) mToolPosition.x
				- (int) mBoxWidth / 2, (int) mToolPosition.y - (int) mBoxHeight
				/ 2);
		Point rightBottomBoxCoord = new Point((int) mToolPosition.x
				+ (int) mBoxWidth / 2, (int) mToolPosition.y + (int) mBoxHeight
				/ 2);
		try {
			// create bitmap out of actual bitmap area
			Bitmap drawingSurfaceBitmap = drawingSurface.getBitmap();

			double outerRectWidth1 = (Math.sin(Math.toRadians(Math
					.abs(mBoxRotation))) * drawingSurfaceBitmap.getWidth());
			double outerRectWidth2 = (Math.sin(Math.toRadians(90.0d - Math
					.abs(mBoxRotation))) * drawingSurfaceBitmap.getHeight());
			float outerRectWidth = (float) outerRectWidth1
					+ (float) outerRectWidth2;

			double outerRectHeight1 = Math.sin(Math.toRadians(90.0d - Math
					.abs(mBoxRotation))) * drawingSurfaceBitmap.getWidth();
			double outerRectHeight2 = Math.sin(Math.toRadians(Math
					.abs(mBoxRotation))) * drawingSurfaceBitmap.getHeight();
			float outerRectHeight = (float) outerRectHeight1
					+ (float) outerRectHeight2;

			Bitmap largeDrawingSurfaceBitmap = Bitmap.createBitmap(
					(int) outerRectWidth, (int) outerRectHeight,
					Bitmap.Config.ARGB_8888);
			Canvas largeDrawingSurfaceCanvas = new Canvas(
					largeDrawingSurfaceBitmap);

			largeDrawingSurfaceCanvas.rotate(-mBoxRotation,
					(int) outerRectWidth / 2, (int) outerRectHeight / 2);
			largeDrawingSurfaceCanvas.drawBitmap(drawingSurfaceBitmap,
					(float) outerRectWidth1, 0, mBitmapPaint);
			largeDrawingSurfaceCanvas.rotate(mBoxRotation,
					(int) outerRectWidth / 2, (int) outerRectHeight / 2);

			// consider StampCommand translation/rotation!!!, look at old code

			float deltaX = mToolPosition.x
					- (drawingSurfaceBitmap.getWidth() / 2.0f);
			float deltaY = mToolPosition.y
					- (drawingSurfaceBitmap.getHeight() / 2.0f);
			drawingSurfaceCanvas.translate(deltaX, deltaY);

			mDrawingBitmap = Bitmap.createBitmap(drawingSurfaceBitmap, 0, 0,
					(int) mBoxWidth, (int) mBoxHeight);
			// transMatrix.preTranslate(deltaX, deltaY);
			// transMatrix.postRotate(-mBoxRotation);

			// Bitmap largerDrawingSurfaceBitmap = Bitmap
			// .createBitmap(;
			// Matrix scaleMatrix = new Matrix();
			// scaleMatrix.postScale((float) rectangleRelation,
			// (float) rectangleRelation);
			// Bitmap scaledDrawingSurfaceBitmap = Bitmap.createBitmap(
			// drawingSurfaceBitmap, 0, 0, (int) outerRectWidth,
			// Bitmap scaledDrawingSurfaceBitmap = Bitmap.createScaledBitmap(
			// drawingSurfaceBitmap, (int) outerRectWidth,
			// (int) outerRectHeight);
			// (int) outerRectHeight, scaleMatrix, false);
			// Canvas drawingSurfaceCanvas = new Canvas(drawingSurfaceBitmap);
			// drawingSurfaceCanvas.rotate(-mBoxRotation);

			// transMatrix.postTranslate(dx, dy)

			// mDrawingBitmap = Bitmap.createBitmap(scaledDrawingSurfaceBitmap,
			// leftTopBoxCoord.x, leftTopBoxCoord.y, rightBottomBoxCoord.x
			// - leftTopBoxCoord.x, rightBottomBoxCoord.y
			// - leftTopBoxCoord.y, transMatrix, true);
			// Log.d(PaintroidApplication.TAG, "created bitmap");
		} catch (IllegalArgumentException e) {
			// floatingBox is outside of image
			Log.e(PaintroidApplication.TAG,
					"error clip bitmap " + e.getMessage());
			Log.e(PaintroidApplication.TAG, "left top box coord : "
					+ leftTopBoxCoord.toString());
			Log.e(PaintroidApplication.TAG, "right bottom box coord : "
					+ rightBottomBoxCoord.toString());
			Log.e(PaintroidApplication.TAG,
					"drawing surface bitmap size : "
							+ drawingSurface.getBitmapHeight() + " x "
							+ drawingSurface.getBitmapWidth());

			if (mDrawingBitmap != null) {
				mDrawingBitmap.recycle();
				mDrawingBitmap = null;
			}
		}
	}

	@Override
	protected void onClickInBox() {
		if (mDrawingBitmap == null) {
			mProgressDialog.show();
			createAndSetBitmap(PaintroidApplication.DRAWING_SURFACE);
			mProgressDialog.dismiss();
		} else {
			Point intPosition = new Point((int) mToolPosition.x,
					(int) mToolPosition.y);
			Command command = new StampCommand(mDrawingBitmap, intPosition,
					mBoxWidth, mBoxHeight, mBoxRotation);
			((StampCommand) command).addObserver(this);
			mProgressDialog.show();
			PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
		}

	}

	@Override
	protected void drawToolSpecifics(Canvas canvas) {
		// TODO Auto-generated method stub
	}

	@Override
	public void resetInternalState() {
		// TODO Auto-generated method stub
	}
}
