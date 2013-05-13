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
		assertEquals("Pointer distance is wrong", 0.0f,
				PrivateAccess.getMemberValue(DrawingSurfaceListener.class, mListenerUnderTest, "mPointerDistance"));
	}

	@Test
	public void testOnTouchActionMoveOneEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
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
		assertEquals("Pointer distance is wrong", 0.0f,
				PrivateAccess.getMemberValue(DrawingSurfaceListener.class, mListenerUnderTest, "mPointerDistance"));
	}

	@Test
	public void testOnTouchDoNoForwardCommandsAfterPinch() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		testOnTouchActionMoveMultipleEvents();
		final int BLOCKING_TIMEOUT = 200;
		Thread.sleep(BLOCKING_TIMEOUT);
		final float pointerDistance = (Float) PrivateAccess.getMemberValue(DrawingSurfaceListener.class,
				mListenerUnderTest, "mPointerDistance");
		final PointF pointerMean = (PointF) PrivateAccess.getMemberValue(DrawingSurfaceListener.class,
				mListenerUnderTest, "mPointerMean");
		mEventToTest = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
				MotionEvent.ACTION_MOVE, mEventToTest.getX() * 2.0f + 10.0f, mEventToTest.getY() * 2.0f + 10.0f, 0);

		assertTrue("Listener should return true for ACTION_MOVE", mListenerUnderTest.onTouch(null, mEventToTest));

		assertEquals("ACTION_MOVE did no conversion from screen to perspective", 2,
				((PerspectiveStub) PaintroidApplication.perspective).getCallCount("convertFromScreenToCanvas"));
		assertEquals("Should be in PINCH mode", TouchMode.valueOf("PINCH").toString(),
				PrivateAccess.getMemberValue(DrawingSurfaceListener.class, mListenerUnderTest, "mTouchMode").toString());
		assertEquals("No further tool handleDown command should be called", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleDown"));
		assertEquals("No further tool handleMove command should be called", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleMove"));
		assertEquals("No further tool handleUp command should be called", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleUp"));
		assertEquals("Pointer distance should not have changed", pointerDistance,
				PrivateAccess.getMemberValue(DrawingSurfaceListener.class, mListenerUnderTest, "mPointerDistance"));
		assertEquals("Pointer mean x should not have changed", pointerMean.x, ((PointF) PrivateAccess.getMemberValue(
				DrawingSurfaceListener.class, mListenerUnderTest, "mPointerMean")).x);
		assertEquals("Pointer mean x should not have changed", pointerMean.y, ((PointF) PrivateAccess.getMemberValue(
				DrawingSurfaceListener.class, mListenerUnderTest, "mPointerMean")).y);

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Test
	public void testOnTouchActionMoveMultipleEvents() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		long uptimeMillis = SystemClock.uptimeMillis();
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

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Test
	public void testOnTouchActionMoveMultipleEventsWithOldValues() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		long uptimeMillis = SystemClock.uptimeMillis();
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
		assertTrue("Listener should return true for ACTION_MOVE", mListenerUnderTest.onTouch(null, mEventToTest));

		assertEquals("ACTION_MOVE did no conversion from screen to perspective", 2,
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
	public void testOnTouchActionCancleInDrawMode() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PrivateAccess.setMemberValue(DrawingSurfaceListener.class, mListenerUnderTest, "mPointerDistance", 999.9f);
		PrivateAccess.setMemberValue(DrawingSurfaceListener.class, mListenerUnderTest, "mPointerMean", new PointF(
				666.6f, 999.9f));
		mEventToTest.setAction(MotionEvent.ACTION_CANCEL);
		// initially in TouchMode.DRAW -> cancel==currentTool.handleUp

		assertTrue("Listener should return true for ACTION_CANCLE", mListenerUnderTest.onTouch(null, mEventToTest));
		assertEquals("ACTION_MOVE did no conversion from screen to perspective", 1,
				((PerspectiveStub) PaintroidApplication.perspective).getCallCount("convertFromScreenToCanvas"));
		assertEquals("Should be in draw mode", TouchMode.valueOf("DRAW").toString(),
				PrivateAccess.getMemberValue(DrawingSurfaceListener.class, mListenerUnderTest, "mTouchMode").toString());
		assertEquals("Should not call handle down", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleDown"));
		assertEquals("Should not call handle move", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleMove"));
		assertEquals("Should not reset internal state", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("resetInternalState"));
		assertEquals("Should call handle up", 1, ((ToolStub) PaintroidApplication.currentTool).getCallCount("handleUp"));
		assertEquals("No pointer distance reset!", 0.0f,
				PrivateAccess.getMemberValue(DrawingSurfaceListener.class, mListenerUnderTest, "mPointerDistance"));
		assertEquals("No pointer mean x reset!", 0.0f, ((PointF) PrivateAccess.getMemberValue(
				DrawingSurfaceListener.class, mListenerUnderTest, "mPointerMean")).x);
		assertEquals("No pointer mean y reset!", 0.0f, ((PointF) PrivateAccess.getMemberValue(
				DrawingSurfaceListener.class, mListenerUnderTest, "mPointerMean")).y);

	}

	@Test
	public void testOnTouchActionCancleNotInDrawMode() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {

		testOnTouchActionMoveMultipleEvents();// listener should be in pinch mode....
		// now in TouchMode.PINCH -> cancel==currentTool.resetInternalState
		PrivateAccess.setMemberValue(DrawingSurfaceListener.class, mListenerUnderTest, "mPointerDistance", 999.9f);
		PrivateAccess.setMemberValue(DrawingSurfaceListener.class, mListenerUnderTest, "mPointerMean", new PointF(
				666.6f, 999.9f));
		mEventToTest.setAction(MotionEvent.ACTION_CANCEL);

		assertTrue("Listener should return true for ACTION_CANCLE", mListenerUnderTest.onTouch(null, mEventToTest));
		assertEquals("ACTION_MOVE did no conversion from screen to perspective", 2,
				((PerspectiveStub) PaintroidApplication.perspective).getCallCount("convertFromScreenToCanvas"));
		assertEquals("Should still be in pinch mode", TouchMode.valueOf("PINCH").toString(), PrivateAccess
				.getMemberValue(DrawingSurfaceListener.class, mListenerUnderTest, "mTouchMode").toString());
		assertEquals("Should not call handle down", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleDown"));
		assertEquals("Should not call handle move", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleMove"));
		assertEquals("Should not call handle up", 0,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("handleUp"));
		assertEquals("Should reset internal state", 1,
				((ToolStub) PaintroidApplication.currentTool).getCallCount("resetInternalState"));
		assertEquals("No pointer distance reset!", 0.0f,
				PrivateAccess.getMemberValue(DrawingSurfaceListener.class, mListenerUnderTest, "mPointerDistance"));
		assertEquals("No pointer mean x reset!", 0.0f, ((PointF) PrivateAccess.getMemberValue(
				DrawingSurfaceListener.class, mListenerUnderTest, "mPointerMean")).x);
		assertEquals("No pointer mean y reset!", 0.0f, ((PointF) PrivateAccess.getMemberValue(
				DrawingSurfaceListener.class, mListenerUnderTest, "mPointerMean")).y);

	}
}
