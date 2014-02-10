package org.catrobat.paintroid.test.integration.dialog;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.ZoomListDialog;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ZoomListDialogIntegrationTest extends BaseIntegrationTestClass {

	public ZoomListDialogIntegrationTest() throws Exception {
		super();
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
	public void testZoomListDialogOnBackPressed() {
		assertNotNull("ZoomListDialog not initialized", ZoomListDialog.instance);
		selectTool(ToolType.MOVE);
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.sleep(500);
		assertTrue("could not find title text", mSolo.searchText(mSolo.getString(R.string.dialog_zoom_list_title)));
		mSolo.goBack();
		mSolo.sleep(200);
		assertFalse("Dialog not closed", mSolo.searchText(mSolo.getString(R.string.dialog_zoom_list_title)));
		assertTrue("Waiting for DrawingSurface to be ready", mSolo.waitForActivity("MainActivity", TIMEOUT));
	}

}
