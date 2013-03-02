package org.catrobat.paintroid.test.integration.statusbar;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Statusbar;
import org.catrobat.paintroid.ui.implementation.DrawingSurfaceImplementation;
import org.catrobat.paintroid.ui.implementation.PerspectiveImplementation;
import org.junit.Before;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageButton;

public class UndoRedoIntegrationTest extends BaseIntegrationTestClass {

	private static final String PRIVATE_ACCESS_STATUSBAR_NAME = "mStatusbar";
	private static final String PRIVATE_ACCESS_TRANSLATION_X = "mSurfaceTranslationX";

	protected Statusbar mStatusbar;

	public UndoRedoIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();

		try {
			mStatusbar = (Statusbar) PrivateAccess.getMemberValue(MainActivity.class, getActivity(),
					PRIVATE_ACCESS_STATUSBAR_NAME);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void testDisableEnableUndo() {

		ImageButton undoButton1 = (ImageButton) mSolo.getView(R.id.btn_status_undo);
		Bitmap bitmap1 = ((BitmapDrawable) undoButton1.getDrawable()).getBitmap();

		mSolo.clickOnView(mButtonTopUndo);
		Bitmap bitmap2 = ((BitmapDrawable) undoButton1.getDrawable()).getBitmap();
		assertEquals(bitmap1, bitmap2);

		PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2);

		mSolo.clickOnScreen(point.x, point.y);

		Bitmap bitmap3 = ((BitmapDrawable) undoButton1.getDrawable()).getBitmap();
		assertNotSame(bitmap1, bitmap3);

		mSolo.clickOnView(mButtonTopUndo);
		Bitmap bitmap4 = ((BitmapDrawable) undoButton1.getDrawable()).getBitmap();
		assertEquals(bitmap1, bitmap4);

	}

	public void testDisableEnableRedo() {

		ImageButton redoButton1 = (ImageButton) mSolo.getView(R.id.btn_status_redo);
		Bitmap bitmap1 = ((BitmapDrawable) redoButton1.getDrawable()).getBitmap();

		mSolo.clickOnView(mButtonTopRedo);
		Bitmap bitmap2 = ((BitmapDrawable) redoButton1.getDrawable()).getBitmap();
		assertEquals(bitmap1, bitmap2);

		PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2);

		mSolo.clickOnScreen(point.x, point.y);

		Bitmap bitmap3 = ((BitmapDrawable) redoButton1.getDrawable()).getBitmap();
		assertEquals(bitmap1, bitmap3);

		mSolo.clickOnView(mButtonTopUndo);
		Bitmap bitmap4 = ((BitmapDrawable) redoButton1.getDrawable()).getBitmap();
		assertNotSame(bitmap1, bitmap4);

	}

	public void testPreserveZoomAndMoveAfterUndo() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		DrawingSurface drawingSurface = (DrawingSurfaceImplementation) getActivity().findViewById(
				R.id.drawingSurfaceView);
		int xCoord = 100;
		int yCoord = 200;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);
		int colorOriginal = drawingSurface.getBitmapColor(pointOnBitmap);

		// fill bitmap
		selectTool(ToolType.FILL);
		int colorToFill = mStatusbar.getCurrentTool().getDrawPaint().getColor();

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.CURRENT_PERSPECTIVE.convertFromScreenToCanvas(pointOnScreen);
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);
		mSolo.sleep(4000);

		// move & zoom
		float scale = 0.5f;
		PaintroidApplication.CURRENT_PERSPECTIVE.setScale(scale); // done this way since robotium does not support > 1
																	// touch event
		mSolo.clickOnView(mButtonTopTool); // switch to move-tool
		mSolo.drag(pointOnScreen.x, pointOnScreen.x + 20, pointOnScreen.y, pointOnScreen.y + 20, 1);

		float translationXBeforeUndo = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				PaintroidApplication.CURRENT_PERSPECTIVE, PRIVATE_ACCESS_TRANSLATION_X);
		float translationYBeforeUndo = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				PaintroidApplication.CURRENT_PERSPECTIVE, PRIVATE_ACCESS_TRANSLATION_X);

		// press undo
		mSolo.clickOnView(mButtonTopUndo);
		mSolo.sleep(1000);

		// check perspective and undo
		int colorAfterFill = drawingSurface.getBitmapColor(pointOnBitmap);
		assertEquals("Pixel color should be the same", colorOriginal, colorAfterFill);

		float translationXAfterUndo = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				PaintroidApplication.CURRENT_PERSPECTIVE, PRIVATE_ACCESS_TRANSLATION_X);
		assertEquals("X-Translation should stay the same after undo", translationXBeforeUndo, translationXAfterUndo);

		float translationYAfterUndo = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				PaintroidApplication.CURRENT_PERSPECTIVE, PRIVATE_ACCESS_TRANSLATION_X);
		assertEquals("Y-Translation should stay the same after undo", translationYBeforeUndo, translationYAfterUndo);
		assertEquals("Scale should stay the same after undo", PaintroidApplication.CURRENT_PERSPECTIVE.getScale(),
				scale);
	}

	public void testPreserveZoomAndMoveAfterRedo() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		DrawingSurface drawingSurface = (DrawingSurfaceImplementation) getActivity().findViewById(
				R.id.drawingSurfaceView);
		int xCoord = 100;
		int yCoord = 200;
		PointF pointOnBitmap = new PointF(xCoord, yCoord);
		int colorOriginal = drawingSurface.getBitmapColor(pointOnBitmap);

		// fill bitmap
		selectTool(ToolType.FILL);
		int colorToFill = mStatusbar.getCurrentTool().getDrawPaint().getColor();

		PointF pointOnScreen = new PointF(pointOnBitmap.x, pointOnBitmap.y);
		PaintroidApplication.CURRENT_PERSPECTIVE.convertFromScreenToCanvas(pointOnScreen);
		mSolo.clickOnScreen(pointOnScreen.x, pointOnScreen.y);
		mSolo.sleep(4000);

		int colorAfterFill = drawingSurface.getBitmapColor(pointOnBitmap);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterFill);

		// press undo
		mSolo.clickOnView(mButtonTopUndo);
		mSolo.sleep(1000);

		int colorAfterUndo = drawingSurface.getBitmapColor(pointOnBitmap);
		assertEquals("Pixel color should be the same", colorOriginal, colorAfterUndo);

		// move & zoom
		float scale = 0.5f;
		PaintroidApplication.CURRENT_PERSPECTIVE.setScale(scale); // done this way since robotium does not support > 1
																	// touch event
		mSolo.clickOnView(mButtonTopTool); // switch to move-tool
		mSolo.drag(pointOnScreen.x, pointOnScreen.x + 20, pointOnScreen.y, pointOnScreen.y + 20, 1);

		float translationXBeforeUndo = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				PaintroidApplication.CURRENT_PERSPECTIVE, PRIVATE_ACCESS_TRANSLATION_X);
		float translationYBeforeUndo = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				PaintroidApplication.CURRENT_PERSPECTIVE, PRIVATE_ACCESS_TRANSLATION_X);

		// press redo
		mSolo.clickOnView(mButtonTopRedo);
		mSolo.sleep(3000);

		// check perspective and redo
		int colorAfterRedo = drawingSurface.getBitmapColor(pointOnBitmap);
		assertEquals("Pixel color should be the same", colorToFill, colorAfterRedo);

		float translationXAfterUndo = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				PaintroidApplication.CURRENT_PERSPECTIVE, PRIVATE_ACCESS_TRANSLATION_X);
		assertEquals("X-Translation should stay the same after undo", translationXBeforeUndo, translationXAfterUndo);

		float translationYAfterUndo = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				PaintroidApplication.CURRENT_PERSPECTIVE, PRIVATE_ACCESS_TRANSLATION_X);
		assertEquals("Y-Translation should stay the same after undo", translationYBeforeUndo, translationYAfterUndo);
		assertEquals("Scale should stay the same after undo", PaintroidApplication.CURRENT_PERSPECTIVE.getScale(),
				scale);
	}
}
