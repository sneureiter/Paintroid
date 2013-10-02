package org.catrobat.paintroid.test.integration.tools;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;

import android.graphics.Point;

public class DrawToolIntegrationTest extends BaseIntegrationTestClass {

	@Override
	@Before
	protected void setUp() {
		super.setUp();
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public DrawToolIntegrationTest() throws Exception {
		super();
	}

	public void testScrollingViewByClickInBordersAndCorners() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		// zoom in
		selectTool(ToolType.MOVE);
		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.clickOnView(mMenuBottomParameter2);

		selectTool(ToolType.BRUSH);

		int xRight = mScreenWidth - 1;
		int xLeft = 1;
		int xMiddle = mScreenWidth / 2;

		int yMiddle = mScreenHeight / 2;
		int yTop = (int) (Utils.getActionbarHeight() + Utils.getStatusbarHeigt(getActivity()) + 1);
		int yBottom = mScreenHeight - ((int) Utils.getStatusbarHeigt(getActivity()) + 1);

		Point rightMiddle = new Point(xRight, yMiddle);
		Point leftMiddle = new Point(xLeft, yMiddle);
		Point topMiddle = new Point(xMiddle, yTop);
		Point bottomMiddle = new Point(xMiddle, yBottom);
		Point bottomRight = new Point(xRight, yBottom);
		Point topLeft = new Point(xLeft, yTop);
		Point bottomLeft = new Point(xLeft, yBottom);
		Point topRight = new Point(xRight, yTop);

		longpressOnPointAndCheckIfCanvasPointHasChanged(rightMiddle);
		mSolo.sleep(100);
		longpressOnPointAndCheckIfCanvasPointHasChanged(leftMiddle);
		mSolo.sleep(100);
		longpressOnPointAndCheckIfCanvasPointHasChanged(topMiddle);
		mSolo.sleep(100);
		longpressOnPointAndCheckIfCanvasPointHasChanged(bottomMiddle);
		mSolo.sleep(100);
		longpressOnPointAndCheckIfCanvasPointHasChanged(bottomRight);
		mSolo.sleep(100);
		longpressOnPointAndCheckIfCanvasPointHasChanged(topLeft);
		mSolo.sleep(100);
		longpressOnPointAndCheckIfCanvasPointHasChanged(bottomLeft);
		mSolo.sleep(100);
		longpressOnPointAndCheckIfCanvasPointHasChanged(topRight);
	}

	public void longpressOnPointAndCheckIfCanvasPointHasChanged(Point clickPoint) {
		Point startPointCanvas = new Point(clickPoint.x, clickPoint.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(startPointCanvas);

		mSolo.drag(clickPoint.x, clickPoint.x, clickPoint.y, clickPoint.y, LONG_WAIT_TRIES);

		Point endPointCanvas = new Point(clickPoint.x, clickPoint.y);
		PaintroidApplication.perspective.convertFromScreenToCanvas(endPointCanvas);

		assertFalse("scrolling did not work", startPointCanvas.equals(endPointCanvas));
	}
}
