package org.catrobat.paintroid.test.integration.layercommands;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageButton;
import android.widget.ListView;

public class ChangeLayerCommandTest extends LayerIntegrationTestClass {

	public ChangeLayerCommandTest() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

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

	@Test
	public final void testChangeCurrentLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);
		assertTrue("Adding layers didn't work", listview.getAdapter().getCount() == 2);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 1", PaintroidApplication.currentLayer == 1);
	}

	@Test
	public final void testUndoSymbolAfterChange() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		ImageButton undoButton = (ImageButton) mSolo.getView(R.id.btn_top_undo);
		Bitmap bitmap1 = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();

		mSolo.clickOnView(mButtonTopUndo);
		Bitmap bitmap2 = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();
		assertEquals("The Undo-Symbol should stay disbled", bitmap1, bitmap2);
		mSolo.sleep(1000);

		mSolo.clickOnScreen(pf.x, pf.y);
		mSolo.sleep(1000);
		mSolo.clickOnScreen(pf.x, pf.y);

		bitmap2 = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();
		mSolo.sleep(1000);
		assertTrue("The Undo-Symbol should change", bitmap1 != bitmap2);

		bitmap2.recycle();

		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);
		assertTrue("Adding layers didn't work", listview.getAdapter().getCount() == 2);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		bitmap2.recycle();
		bitmap2 = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();

		assertTrue("Current Layer should be 1", PaintroidApplication.currentLayer == 1);

		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.space));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		bitmap2.recycle();
		bitmap2 = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		assertTrue("There shall be a undo left", bitmap1 != bitmap2);
	}

	@Test
	public final void testRedoSymbolAfterChange() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		ImageButton redoButton = (ImageButton) mSolo.getView(R.id.btn_top_redo);
		Bitmap bitmap1 = ((BitmapDrawable) redoButton.getDrawable()).getBitmap();

		mSolo.sleep(1000);

		mSolo.clickOnScreen(pf.x, pf.y);
		mSolo.sleep(1000);

		mSolo.clickOnView(mButtonTopUndo);
		mSolo.sleep(1000);

		Bitmap bitmap3 = ((BitmapDrawable) redoButton.getDrawable()).getBitmap();
		mSolo.sleep(1000);

		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);
		assertTrue("Adding layers didn't work", listview.getAdapter().getCount() == 2);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		bitmap3 = ((BitmapDrawable) redoButton.getDrawable()).getBitmap();
		mSolo.sleep(1000);

		assertTrue("Current Layer should be 1", PaintroidApplication.currentLayer == 1);

		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.space));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		bitmap3.recycle();
		mSolo.sleep(1000);
		bitmap3 = ((BitmapDrawable) redoButton.getDrawable()).getBitmap();
		assertTrue("Current Layer should be 0", PaintroidApplication.currentLayer == 0);

		mSolo.sleep(1000);
		assertTrue("There shall be a redo left", bitmap1 != bitmap3);
	}

}
