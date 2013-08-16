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

package org.catrobat.paintroid.test.junit.command;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.implementation.FlipCommand;
import org.catrobat.paintroid.command.implementation.FlipCommand.FlipDirection;
import org.catrobat.paintroid.test.junit.stubs.DrawingSurfaceStub;
import org.junit.Before;

import android.graphics.PointF;
import android.graphics.Rect;

public class FlipCommandTest extends CommandTestSetup {

	private int mBitmapHeigt;
	private int mBitmapWidth;
	private Rect mRectFullBitmap;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mBitmapHeigt = mBitmapUnderTest.getHeight();
		mBitmapWidth = mBitmapUnderTest.getWidth();
		PaintroidApplication.drawingSurface = new DrawingSurfaceStub(getContext());
		mRectFullBitmap = new Rect(0, 0, mBitmapWidth, mBitmapHeigt);
	}

	public void testFullVerticalFlip() {
		mCommandUnderTest = new FlipCommand(FlipDirection.FLIP_VERTICAL, mRectFullBitmap);
		mBitmapUnderTest.setPixel(0, mBitmapHeigt / 2, PAINT_BASE_COLOR);
		mCommandUnderTest.run(mCanvasUnderTest, mBitmapUnderTest);
		int pixel = PaintroidApplication.drawingSurface.getPixel(new PointF(mBitmapWidth - 1, mBitmapWidth / 2));
		assertEquals("pixel should be paint_base_color", PAINT_BASE_COLOR, pixel);
	}

	public void testFullHorizontalFlip() {
		mCommandUnderTest = new FlipCommand(FlipDirection.FLIP_HORIZONTAL, mRectFullBitmap);
		mBitmapUnderTest.setPixel(mBitmapWidth / 2, 0, PAINT_BASE_COLOR);
		mCommandUnderTest.run(mCanvasUnderTest, mBitmapUnderTest);
		int pixel = PaintroidApplication.drawingSurface.getPixel(new PointF(mBitmapWidth / 2, mBitmapWidth - 1));
		assertEquals("pixel should be paint_base_color", PAINT_BASE_COLOR, pixel);
	}

}
