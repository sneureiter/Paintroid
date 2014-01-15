package org.catrobat.paintroid.test.integration.dialog;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.layerchooser.LayerChooserDialog;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

public class LayerDialogIntegrationTest extends BaseIntegrationTestClass {

	private PointF pf;

	public LayerDialogIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
		pf = new PointF(mScreenWidth / 2, mScreenHeight / 2);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testLayerDialog() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		assertFalse("LayerChooserDialog is already visible", LayerChooserDialog.getInstance().isAdded());
		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);
		assertTrue("LayerChooserDialog is not yet added", LayerChooserDialog.getInstance().isAdded());
	}

	@Test
	public void testOpenAndCloseLayerDialogOnClickOnLayerButtonAndDone() {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		View listview = mSolo.getView(R.id.mListView);
		assertTrue("LayerChooser Listview not opening", mSolo.waitForView(listview, 1000, false));

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);
		assertTrue("LayerChooserDialog is still visible", !LayerChooserDialog.getInstance().isAdded());
	}

	@Test
	public void testOpenAndCloseLayerDialogOnClickOnLayerButtonAndReturn() {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		View listview = mSolo.getView(R.id.mListView);
		assertTrue("LayerChooser Listview not opening", mSolo.waitForView(listview, 1000, false));

		mSolo.goBack();
		mSolo.sleep(1000);
		assertTrue("LayerChooserDialog is still visible", !LayerChooserDialog.getInstance().isAdded());
	}

	@Test
	public void testChangeLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));
		int prev_layer = PaintroidApplication.currentLayer;

		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Adding the layer doesn't work properly", prev_layer == PaintroidApplication.currentLayer);

		assertTrue("Layer was not added properly", 2 == PaintroidApplication.commandManager.getAllCommandList().size());

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));
		mSolo.sleep(1000);

		assertTrue("Changing the layer with Buttons doesn't work properly", LayerChooserDialog.mSelectedLayerIndex == 1);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		prev_layer = PaintroidApplication.currentLayer;

		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(R.id.space));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		assertTrue("Changing the layer on touch doesn't work", prev_layer - 1 == PaintroidApplication.currentLayer);
	}

	@Test
	public void testMaxLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		// 10 Layers Max
		for (int i = 0; i < 10; i++) {
			mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		}
		assertTrue("More than 10 layers are possible",
				(((ListView) mSolo.getView(R.id.mListView)).getAdapter()).getCount() < 11);

	}

	@Test
	public void testMinLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);
		int prev_num_layers = listview.getAdapter().getCount();

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		assertTrue("Less than one layers is possible", listview.getAdapter().getCount() == prev_num_layers);

	}

	@Test
	public void testDeleteLayer() {

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		ListView listview = (ListView) mSolo.getView(R.id.mListView);
		int prev_num_layers = listview.getAdapter().getCount();

		assertTrue("Changing the layer with Buttons doesn't work properly", LayerChooserDialog.mSelectedLayerIndex == 0);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		assertTrue("It's possible to remove a single layer", listview.getAdapter().getCount() == prev_num_layers);
		assertTrue("Adding a layer didn't work",
				PaintroidApplication.commandManager.getAllCommandList().size() == prev_num_layers);
		assertTrue("Changing the layer with Buttons doesn't work properly", LayerChooserDialog.mSelectedLayerIndex == 0);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Adding a layer didn't work", listview.getAdapter().getCount() != prev_num_layers);
		assertTrue("Adding a layer didn't work",
				PaintroidApplication.commandManager.getAllCommandList().size() != prev_num_layers);
		assertTrue("Changing the layer with Delete doesn't work properly", LayerChooserDialog.mSelectedLayerIndex == 0);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button2));
		mSolo.sleep(1000);

		assertTrue("Securityquestion didn't work", listview.getAdapter().getCount() != prev_num_layers);
		assertTrue("Changing the layer with Buttons doesn't work properly", LayerChooserDialog.mSelectedLayerIndex == 0);

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_remove));
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		assertTrue("Removing a layer didn't work", listview.getAdapter().getCount() == prev_num_layers);
		assertTrue("Changing the layer with Buttons doesn't work properly", LayerChooserDialog.mSelectedLayerIndex == 0);
		assertTrue("Romoving a layer didn't work",
				PaintroidApplication.commandManager.getAllCommandList().size() == prev_num_layers);
	}

	@Test
	public void testAddLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);
		ListView listview = (ListView) mSolo.getView(R.id.mListView);
		int prev_num_layers = listview.getAdapter().getCount();

		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));
		mSolo.sleep(1000);

		assertTrue("Adding a layer didn't work", listview.getAdapter().getCount() == prev_num_layers + 1);
		assertTrue("Adding a layer didn't work", PaintroidApplication.commandManager.getAllCommandList().size() == 2);

	}

	@Test
	public void testMoveLayerUpAndDown() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_add));

		String oldname = (LayerChooserDialog.layer_data.get(0).name);

		mSolo.clickOnView(mSolo.getView(R.id.layerTitle));
		mSolo.sleep(1000);

		mSolo.enterText(0, "test");
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));

		assertTrue("The first layer should move down", oldname == (LayerChooserDialog.layer_data.get(0).name));

		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_down));

		assertTrue("The second layer shouldnt move down", oldname == (LayerChooserDialog.layer_data.get(0).name));

		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_up));

		assertTrue("The first layer can move up", oldname != (LayerChooserDialog.layer_data.get(0).name));

		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(R.id.btn_layerchooser_up));

		assertTrue("The first layer cashouldnt move up", oldname != (LayerChooserDialog.layer_data.get(0).name));

	}

	@Test
	public void testChangeLayerName() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		String oldname = (LayerChooserDialog.layer_data.get(0).name);

		mSolo.clickOnView(mSolo.getView(R.id.layerTitle));
		mSolo.sleep(1000);

		mSolo.enterText(0, "test");
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button1));
		mSolo.sleep(1000);

		assertTrue("Layername didn't changed", oldname != (LayerChooserDialog.layer_data.get(0).name));
		oldname = (LayerChooserDialog.layer_data.get(0).name);

		mSolo.clickOnView(mSolo.getView(R.id.layerTitle));
		mSolo.sleep(1000);

		mSolo.enterText(0, "test2");
		mSolo.sleep(1000);

		mSolo.clickOnView(mSolo.getView(android.R.id.button2));
		mSolo.sleep(1000);

		assertTrue("Layername changed, but it shouldn't", oldname == (LayerChooserDialog.layer_data.get(0).name));

		oldname = (LayerChooserDialog.layer_data.get(0).name);

		mSolo.clickOnView(mSolo.getView(R.id.layerTitle));
		mSolo.sleep(1000);
		mSolo.clickOnView(mSolo.getView(android.R.id.button1));

		assertTrue("Layername can be empty", oldname == LayerChooserDialog.layer_data.get(0).name);
	}

	@Test
	public void testShowAndHideLayer() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class, 1, TIMEOUT));

		mSolo.clickOnScreen(pf.x, pf.y);
		mSolo.sleep(1000);

		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		ImageView eyeButton = (ImageView) mSolo.getView(R.id.eyeIcon);
		Bitmap eyeBitmap = ((BitmapDrawable) eyeButton.getDrawable()).getBitmap();

		assertTrue("The layer is already hidden", LayerChooserDialog.layer_data.get(0).visible == true);
		mSolo.clickOnView(mSolo.getView(R.id.eyeIcon));
		mSolo.sleep(1000);

		assertTrue("The layer is still visible", LayerChooserDialog.layer_data.get(0).visible == false);

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

		mSolo.clickOnView(mMenuBottomLayer);
		mSolo.sleep(1000);

		assertTrue("The layer is back on visible", LayerChooserDialog.layer_data.get(0).visible == false);
		mSolo.clickOnView(mSolo.getView(R.id.eyeIcon));
		mSolo.sleep(1000);

		Bitmap eyeBitmap2 = ((BitmapDrawable) eyeButton.getDrawable()).getBitmap();

		assertTrue("Eye-symbols didn't change", eyeBitmap.equals(eyeBitmap2));

		mSolo.clickOnView(mSolo.getButton(mSolo.getString(R.string.done)));
		mSolo.sleep(1000);

	}
}