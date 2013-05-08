package org.catrobat.paintroid.test.junit.listener;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.listener.DrawingSurfaceListener;
import org.catrobat.paintroid.test.junit.stubs.PerspectiveStub;
import org.catrobat.paintroid.test.junit.stubs.ToolStub;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.annotation.TargetApi;
import android.graphics.PointF;
import android.os.Build;
import android.os.SystemClock;
import android.test.AndroidTestCase;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;

public class DrawingSurfaceListenerTest extends AndroidTestCase {

	private DrawingSurfaceListener mListenerUnderTest;
	private MotionEvent mEventToTest;

	enum TouchMode {
		DRAW, PINCH
	};

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		PaintroidApplication.currentTool = new ToolStub();
		PaintroidApplication.perspective = new PerspectiveStub();
		mListenerUnderTest = new DrawingSurfaceListener();
		mEventToTest = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
				MotionEvent.ACTION_DOWN, 250.0f, 250.0f, 0);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		mEventToTest.recycle();
		super.tearDown();
	}

	@Test
	public void testOnTouchActionDown() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		mEventToTest.setAction(MotionEvent.ACTION_DOWN);
		assertTrue("Listener should return true for ACTION_DOWN", mListenerUnderTest.onTouch(null, mEventToTest));
		assertEquals("ACTION_DOWN did no conversion from screen to perspective", 1,
				((PerspectiveStub) PaintroidApplication.perspective).getCallCount("convertFromScreenToCanvas"));
		assertEquals("Wrong count of tool handle down", 1,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleDown"));
		assertEquals("Wrong count of tool handle move counts", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleMove"));
		assertEquals("Wrong count of tool handle up counts", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleUp"));
	}

	@Test
	public void testOnTouchActionMoveOneEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		// testOnTouchActionDown();
		// mEventToTest.addBatch(SystemClock.uptimeMillis(), mEventToTest.getX() + 10.0f, mEventToTest.getY() + 10.0f,
		// 1,
		// 1, 0);
		mEventToTest.setAction(MotionEvent.ACTION_MOVE);
		assertTrue("Listener should return true for ACTION_MOVE", mListenerUnderTest.onTouch(null, mEventToTest));
		assertEquals("ACTION_MOVE did no conversion from screen to perspective", 1,
				((PerspectiveStub) PaintroidApplication.perspective).getCallCount("convertFromScreenToCanvas"));
		assertEquals("Should be in draw mode", TouchMode.valueOf("DRAW").toString(),
				PrivateAccess.getMemberValue(DrawingSurfaceListener.class, mListenerUnderTest, "mTouchMode").toString());
		assertEquals("Wrong count of tool handle down", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleDown"));
		assertEquals("Wrong count of tool handle move counts", 1,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleMove"));
		assertEquals("Wrong count of tool handle up counts", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleUp"));

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Test
	public void testOnTouchActionMoveMultipleEvents() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		// testOnTouchActionDown();
		// mEventToTest.addBatch(SystemClock.uptimeMillis(), mEventToTest.getX() + 10.0f, mEventToTest.getY() + 10.0f,
		// 1,
		// 1, 0);
		// testOnTouchActionMoveOneEvent();
		long uptimeMillis = SystemClock.uptimeMillis();
		// mEventToTest = MotionEvent.obtain(uptimeMillis, SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 250.0f,
		// 250.0f, 0);
		int[] pointerIDs = { 0, 1 };
		PointerCoords[] pointerCoords = new PointerCoords[2];
		pointerCoords[0] = new PointerCoords();
		pointerCoords[0].x = 0.0f;
		pointerCoords[0].y = 0.0f;
		pointerCoords[0].pressure = 1.0f;
		pointerCoords[0].size = 1.0f;
		pointerCoords[1] = new PointerCoords(pointerCoords[0]);
		pointerCoords[1].x = 3.0f;
		pointerCoords[1].y = 4.0f;

		mEventToTest.recycle();
		mEventToTest = MotionEvent.obtain(uptimeMillis, uptimeMillis, MotionEvent.ACTION_MOVE, pointerCoords.length,
				pointerIDs, pointerCoords, 0, 1.0f, 1.0f, 1, 0, 0, 0);

		assertTrue("Listener should return true for ACTION_MOVE", mListenerUnderTest.onTouch(null, mEventToTest));
		assertEquals("ACTION_MOVE did no conversion from screen to perspective", 1,
				((PerspectiveStub) PaintroidApplication.perspective).getCallCount("convertFromScreenToCanvas"));
		assertEquals("Should be in pinch mode", TouchMode.valueOf("PINCH").toString(),
				PrivateAccess.getMemberValue(DrawingSurfaceListener.class, mListenerUnderTest, "mTouchMode").toString());
		assertEquals("Wrong count of tool handle down", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleDown"));
		assertEquals("Wrong count of tool handle move counts", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleMove"));
		assertEquals("Wrong count of tool handle up counts", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleUp"));

		assertEquals("Wrong count of perspective multiplyScale", 0,
				((PerspectiveStub) PaintroidApplication.perspective).getCallCount("multiplyScale"));
		assertEquals("Wrong count of perspective translate", 0,
				((PerspectiveStub) PaintroidApplication.perspective).getCallCount("translate"));

		assertEquals("Pointer distance is wrong", 5.0f,
				PrivateAccess.getMemberValue(DrawingSurfaceListener.class, mListenerUnderTest, "mPointerDistance"));
		assertEquals("Pointer mean x is wrong", 1.5f, ((PointF) PrivateAccess.getMemberValue(
				DrawingSurfaceListener.class, mListenerUnderTest, "mPointerMean")).x);
		assertEquals("Pointer mean y is wrong", 2.0f, ((PointF) PrivateAccess.getMemberValue(
				DrawingSurfaceListener.class, mListenerUnderTest, "mPointerMean")).y);
	}

	@Test
	public void testOnTouchActionCancle() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		mEventToTest.setAction(MotionEvent.ACTION_DOWN);
		assertTrue("Listener should return true for ACTION_DOWN", mListenerUnderTest.onTouch(null, mEventToTest));
		assertEquals("ACTION_DOWN not forwarded to Tool", 1,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleDown"));
		assertEquals("ACTION_DOWN did no conversion from screen to perspective", 1,
				((PerspectiveStub) PaintroidApplication.perspective).getCallCount("convertFromScreenToCanvas"));

	}
}
