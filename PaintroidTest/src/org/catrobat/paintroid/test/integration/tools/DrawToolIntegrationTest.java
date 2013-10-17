package org.catrobat.paintroid.test.integration.tools;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.After;
import org.junit.Before;

import android.graphics.Color;
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

	public void testScrollingViewByClickInBordersAndCornersZoomedIn() throws SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {

		PaintroidApplication.perspective.setScale(5);
		float surfaceWidth = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
				"mSurfaceWidth");
		float surfaceHeight = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
				"mSurfaceHeight");
		float xRight = surfaceWidth - 1;
		float xLeft = 1;
		float xMiddle = surfaceWidth / 2;

		float yMiddle = (surfaceHeight / 2 + Utils.getActionbarHeight() + Utils.getStatusbarHeight());
		float yTop = (Utils.getActionbarHeight() + Utils.getStatusbarHeight());
		float yBottom = surfaceHeight + yTop - 1;

		PointF rightMiddle = new PointF(xRight, yMiddle);
		PointF leftMiddle = new PointF(xLeft, yMiddle);
		PointF topMiddle = new PointF(xMiddle, yTop);
		PointF bottomMiddle = new PointF(xMiddle, yBottom);
		PointF topLeft = new PointF(xLeft, yTop);
		PointF bottomRight = new PointF(xRight, yBottom);
		PointF bottomLeft = new PointF(xLeft, yBottom);
		PointF topRight = new PointF(xRight, yTop);

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

	public void testClickInBordersAndCornersZoomedOut() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		PaintroidApplication.perspective.setScale(0.3f);
		float surfaceWidth = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
				"mSurfaceWidth");
		float surfaceHeight = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
				"mSurfaceHeight");
		float xRight = surfaceWidth - 1;
		float xLeft = 1;
		float xMiddle = surfaceWidth / 2;

		float yMiddle = (surfaceHeight / 2 + Utils.getActionbarHeight() + Utils.getStatusbarHeight());
		float yTop = (Utils.getActionbarHeight() + Utils.getStatusbarHeight());
		float yBottom = surfaceHeight + yTop - 1;

		PointF rightMiddle = new PointF(xRight, yMiddle);
		PointF leftMiddle = new PointF(xLeft, yMiddle);
		PointF topMiddle = new PointF(xMiddle, yTop);
		PointF bottomMiddle = new PointF(xMiddle, yBottom);
		PointF topLeft = new PointF(xLeft, yTop);
		PointF bottomRight = new PointF(xRight, yBottom);
		PointF bottomLeft = new PointF(xLeft, yBottom);
		PointF topRight = new PointF(xRight, yTop);

		longpressOnPointAndCheckIfCanvasPointHasNotChanged(rightMiddle);
		mSolo.sleep(100);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(leftMiddle);
		mSolo.sleep(100);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(topMiddle);
		mSolo.sleep(100);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(bottomMiddle);
		mSolo.sleep(100);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(bottomRight);
		mSolo.sleep(100);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(topLeft);
		mSolo.sleep(100);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(bottomLeft);
		mSolo.sleep(100);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(topRight);
	}

	public void longpressOnPointAndCheckIfCanvasPointHasChanged(PointF clickPoint) {
		PointF startPointSurface = Utils.convertFromScreenToSurface(clickPoint);

		PointF startPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);
		mSolo.clickLongOnScreen(clickPoint.x, clickPoint.y, 2000);
		PointF endPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		int startPointColor = PaintroidApplication.drawingSurface.getPixel(startPointCanvas);
		int endPointColor = PaintroidApplication.drawingSurface.getPixel(endPointCanvas);
		assertEquals("start", Color.BLACK, startPointColor);
		assertEquals("end", Color.BLACK, endPointColor);
		assertFalse("scrolling did not work", startPointCanvas.equals(endPointCanvas));
	}

	public void longpressOnPointAndCheckIfCanvasPointHasNotChanged(PointF clickPoint) {
		PointF startPointSurface = Utils.convertFromScreenToSurface(clickPoint);

		PointF startPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);
		mSolo.clickLongOnScreen(clickPoint.x, clickPoint.y, 2000);
		PointF endPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		assertTrue("view should not scroll", startPointCanvas.equals(endPointCanvas));
	}

}
