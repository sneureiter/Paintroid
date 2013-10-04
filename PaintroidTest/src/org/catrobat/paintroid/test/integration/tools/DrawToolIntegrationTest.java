package org.catrobat.paintroid.test.integration.tools;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.After;
import org.junit.Before;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;

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
		PaintroidApplication.perspective.setScale(5);
		float surfaceWidth = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
				"mSurfaceWidth");
		float surfaceHeight = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
				"mSurfaceHeight");
		int xRight = (int) surfaceWidth - 1;
		int xLeft = 1;
		int xMiddle = (int) surfaceWidth / 2;

		int yMiddle = (int) (surfaceHeight / 2 + Utils.getActionbarHeight() + Utils.getStatusbarHeight(getActivity()));
		int yTop = (int) (Utils.getActionbarHeight() + Utils.getStatusbarHeight(getActivity()));
		int yBottom = (int) surfaceHeight + yTop -1;

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

		mSolo.drag(clickPoint.x, clickPoint.x, clickPoint.y, clickPoint.y, 200);
		mSolo.sleep(1000);
		Point endPointCanvas = new Point(clickPoint.x,
				(int) (clickPoint.y - Utils.getStatusbarHeight(getActivity()) - Utils.getActionbarHeight()));
		PaintroidApplication.perspective.convertFromScreenToCanvas(endPointCanvas);
		int color = PaintroidApplication.drawingSurface.getPixel(new PointF(endPointCanvas.x, endPointCanvas.y));
		assertEquals(Color.BLACK, color);
		assertFalse("scrolling did not work", startPointCanvas.equals(endPointCanvas));
	}

}
