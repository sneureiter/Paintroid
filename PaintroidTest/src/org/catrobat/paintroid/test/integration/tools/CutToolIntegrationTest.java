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

package org.catrobat.paintroid.test.integration.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.tools.implementation.CutTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.PointF;

public class CutToolIntegrationTest extends BaseIntegrationTestClass {

	private static final int Y_CLICK_OFFSET = 25;
	private static final float SCALE_25 = 0.25f;
	private static final float STAMP_RESIZE_FACTOR = 1.5f;
	// Rotation test
	private static final float SQUARE_LENGTH = 300;
	private static final float MIN_ROTATION = -450f;
	private static final float MAX_ROTATION = 450f;
	private static final float ROTATION_STEPSIZE = 30.0f;
	private static final float ROTATION_TOLERANCE = 10;

	public CutToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		Thread.sleep(1500);
		super.tearDown();
		Thread.sleep(1000);
	}

	@Test
	public void testIconsInitial() {
		selectTool(ToolType.CUT);
		CutTool cutTool = (CutTool) PaintroidApplication.currentTool;
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_copy,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear_disabled,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testIconsAfterCutWithButton() {
		selectTool(ToolType.CUT);
		CutTool cutTool = (CutTool) PaintroidApplication.currentTool;
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.waitForDialogToClose(TIMEOUT);
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_paste,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testIconsAfterCutWithBox() {
		PointF surfaceCenterPoint = getScreenPointFromSurfaceCoordinates(getSurfaceCenterX(), getSurfaceCenterY());
		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y);
		selectTool(ToolType.CUT);
		CutTool cutTool = (CutTool) PaintroidApplication.currentTool;
		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y);
		mSolo.waitForDialogToClose(TIMEOUT);
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_paste,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testIconsAfterCutAndPasteWithButton() {
		selectTool(ToolType.CUT);
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.sleep(500);
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.sleep(500);
		CutTool cutTool = (CutTool) PaintroidApplication.currentTool;
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_paste,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testIconsAfterCutWithButtonAndPasteWithBox() {
		PointF surfaceCenterPoint = getScreenPointFromSurfaceCoordinates(getSurfaceCenterX(), getSurfaceCenterY());
		selectTool(ToolType.CUT);
		CutTool cutTool = (CutTool) PaintroidApplication.currentTool;
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.sleep(500);
		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y);
		mSolo.waitForDialogToClose(TIMEOUT);
		mSolo.sleep(500);
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_paste,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testIconsAfterCutWithBoxAndPasteWithButton() {
		PointF surfaceCenterPoint = getScreenPointFromSurfaceCoordinates(getSurfaceCenterX(), getSurfaceCenterY());
		selectTool(ToolType.CUT);
		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y);
		mSolo.clickOnView(mMenuBottomParameter1);
		CutTool cutTool = (CutTool) PaintroidApplication.currentTool;
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_paste,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testIconsAfterCutAndPasteWithBox() {
		PointF surfaceCenterPoint = getScreenPointFromSurfaceCoordinates(getSurfaceCenterX(), getSurfaceCenterY());
		selectTool(ToolType.CUT);
		CutTool cutTool = (CutTool) PaintroidApplication.currentTool;
		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y);
		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y);
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_paste,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		assertEquals("Wrong icon for parameter button 2", R.drawable.icon_menu_stamp_clear,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));
	}

	@Test
	public void testIconsAfterCutAndClearWithButton() {
		selectTool(ToolType.CUT);
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.sleep(500);
		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.sleep(500);
		CutTool cutTool = (CutTool) PaintroidApplication.currentTool;
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_copy,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
	}

	@Test
	public void testIconsAfterCutPasteAndClearWithButton() {
		selectTool(ToolType.CUT);
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.clickOnView(mMenuBottomParameter2);
		CutTool cutTool = (CutTool) PaintroidApplication.currentTool;
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_copy,
				cutTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
	}

	@Test
	public void testCutPixel() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		PointF surfaceCenterPoint = getScreenPointFromSurfaceCoordinates(getSurfaceCenterX(), getSurfaceCenterY());
		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET);

		selectTool(ToolType.CUT);

		CutTool cutTool = (CutTool) PaintroidApplication.currentTool;
		PointF toolPosition = new PointF(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET);
		PrivateAccess.setMemberValue(BaseToolWithShape.class, cutTool, "mToolPosition", toolPosition);

		mSolo.clickOnScreen(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET);
		assertTrue("Stamping timed out", hasProgressDialogFinished(LONG_WAIT_TRIES));

		PointF pixelCoordinateToControlColor = new PointF(surfaceCenterPoint.x, surfaceCenterPoint.y - Y_CLICK_OFFSET);
		PointF canvasPoint = Utils.convertFromScreenToSurface(pixelCoordinateToControlColor);
		int pixelToControl = PaintroidApplication.drawingSurface.getPixel(PaintroidApplication.perspective
				.getCanvasPointFromSurfacePoint(canvasPoint));

		assertEquals("First Pixel not Transparent after using cut", Color.TRANSPARENT, pixelToControl);

		int moveOffset = 100;

		toolPosition.y = toolPosition.y - moveOffset;
		PrivateAccess.setMemberValue(BaseToolWithShape.class, cutTool, "mToolPosition", toolPosition);

		mSolo.clickOnScreen(toolPosition.x, toolPosition.y);
		assertTrue("Stamping timed out", hasProgressDialogFinished(LONG_WAIT_TRIES));

		toolPosition.y = toolPosition.y - moveOffset;
		PrivateAccess.setMemberValue(BaseToolWithShape.class, cutTool, "mToolPosition", toolPosition);

		pixelCoordinateToControlColor = new PointF(toolPosition.x, toolPosition.y + moveOffset + Y_CLICK_OFFSET);
		canvasPoint = Utils.convertFromScreenToSurface(pixelCoordinateToControlColor);
		pixelToControl = PaintroidApplication.drawingSurface.getPixel(PaintroidApplication.perspective
				.getCanvasPointFromSurfacePoint(canvasPoint));

		assertEquals("Second Pixel not Black after using Stamp for copying", Color.BLACK, pixelToControl);
	}

	@Test
	public void testStampOutsideDrawingSurface() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnScreen(getSurfaceCenterX(), getSurfaceCenterY() + Utils.getActionbarHeight()
				+ getStatusbarHeight() - Y_CLICK_OFFSET);

		int screenWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
		int screenHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
		PrivateAccess.setMemberValue(Perspective.class, PaintroidApplication.perspective, "mSurfaceScale", SCALE_25);

		mSolo.sleep(500);

		selectTool(ToolType.CUT);

		CutTool cutTool = (CutTool) PaintroidApplication.currentTool;
		PointF toolPosition = new PointF(getSurfaceCenterX(), getSurfaceCenterY());
		PrivateAccess.setMemberValue(BaseToolWithShape.class, cutTool, "mToolPosition", toolPosition);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, cutTool, "mBoxWidth",
				(int) (screenWidth * STAMP_RESIZE_FACTOR));
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, cutTool, "mBoxHeight",
				(int) (screenHeight * STAMP_RESIZE_FACTOR));

		mSolo.clickOnScreen(getSurfaceCenterX(), getSurfaceCenterY() + Utils.getActionbarHeight()
				+ getStatusbarHeight() - Y_CLICK_OFFSET);
		assertTrue("Stamping timed out", hasProgressDialogFinished(LONG_WAIT_TRIES));

		Bitmap drawingBitmap = ((Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, cutTool,
				"mDrawingBitmap")).copy(Config.ARGB_8888, false);

		assertNotNull("After activating cut and stamp, mDrawingBitmap should not be null anymore", drawingBitmap);

		drawingBitmap.recycle();
		drawingBitmap = null;

	}

	private void invokeCreateAndSetBitmap(Object object, Object parameter) throws NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		Method method = object.getClass().getDeclaredMethod("createAndSetBitmap");
		method.setAccessible(true);

		Object[] parameters = new Object[0];
		method.invoke(object, parameters);
	}

}
