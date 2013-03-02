package org.catrobat.paintroid.test.integration.overflow;

import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;

public class BitmapIntegrationTest extends BaseIntegrationTestClass {

	public BitmapIntegrationTest() throws Exception {
		super();
	}

	// TODO Should be renamed and fixed with FileI/O refactoring!
	/*
	 * public void testCenterBitmapSimulateLoad() throws SecurityException, IllegalArgumentException,
	 * NoSuchFieldException, IllegalAccessException {
	 * 
	 * try { mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu)); } catch (AssertionError er) {
	 * mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu_condensed)); }
	 * 
	 * Bitmap currentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
	 * PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap");
	 * 
	 * Point bottomrightCanvasPoint = new Point(currentDrawingSurfaceBitmap.getWidth() - 1,
	 * currentDrawingSurfaceBitmap.getHeight() - 1); Point originalBottomrightScreenPoint =
	 * org.catrobat.paintroid.test.utils.Utils.convertFromCanvasToScreen( bottomrightCanvasPoint,
	 * PaintroidApplication.CURRENT_PERSPECTIVE);
	 * 
	 * int widthOverflow = 10; int newBitmapHeight = 30; float canvasCenterTollerance = 100;
	 * 
	 * final Bitmap widthOverflowedBitmap = Bitmap.createBitmap(originalBottomrightScreenPoint.x + widthOverflow,
	 * newBitmapHeight, Bitmap.Config.ALPHA_8);
	 * 
	 * float surfaceScaleBeforeBitmapCommand = PaintroidApplication.CURRENT_PERSPECTIVE.getScale();
	 * 
	 * getActivity().runOnUiThread(new Runnable() {
	 * 
	 * @Override public void run() { PaintroidApplication.COMMAND_MANAGER.commitCommand(new
	 * BitmapCommand(widthOverflowedBitmap)); } });
	 * 
	 * mSolo.sleep(2000);
	 * 
	 * float surfaceScaleAfterBitmapCommand = PaintroidApplication.CURRENT_PERSPECTIVE.getScale();
	 * 
	 * assertTrue("Wrong Scale after setting new bitmap", surfaceScaleAfterBitmapCommand <
	 * surfaceScaleBeforeBitmapCommand);
	 * 
	 * mSolo.drag(originalBottomrightScreenPoint.x / 2, originalBottomrightScreenPoint.x / 2,
	 * originalBottomrightScreenPoint.y / 2, originalBottomrightScreenPoint.y / 2 + canvasCenterTollerance, 1); PointF
	 * canvasCenter = new PointF((originalBottomrightScreenPoint.x + widthOverflow) / 2, newBitmapHeight / 2);
	 * 
	 * mSolo.sleep(1000); assertTrue("Center not set", PaintroidApplication.DRAWING_SURFACE.getBitmapColor(canvasCenter)
	 * != Color.TRANSPARENT);
	 * 
	 * }
	 */
}
