package org.catrobat.paintroid.test.integration.tools;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
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

		// TODO get correct y points, sub toolbarhight
		// PointF calcPoint = PaintroidApplication.perspective.calculateFromCanvasToScreen(new PointF(0, 0));

		int yMiddle = mScreenHeight / 2;
		// int yTop = (int) (1 + calcPoint.y);
		int yBottom = mScreenHeight - 1;

		Point rightMiddle = new Point(xRight, yMiddle);
		Point leftMiddle = new Point(xLeft, yMiddle);
		// Point topMiddle = new Point(xMiddle, yTop);
		// Point bottomMiddle = new Point(xMiddle, yBottom);

		longpressOnPointAndCheckIfCanvasPointHasChanged(rightMiddle);
		mSolo.sleep(100);
		longpressOnPointAndCheckIfCanvasPointHasChanged(leftMiddle);
		mSolo.sleep(100);
		// longpressOnPointAndCheckIfCanvasPointHasChanged(topMiddle);
		// mSolo.sleep(100);
		// longpressOnPointAndCheckIfCanvasPointHasChanged(bottomMiddle);
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
