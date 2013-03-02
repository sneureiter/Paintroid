package org.catrobat.paintroid.test.integration.statusbar;

import java.util.ArrayList;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;

import android.widget.ImageButton;

public class StatusbarIntegrationTest extends BaseIntegrationTestClass {

	public StatusbarIntegrationTest() throws Exception {
		super();
	}

	public void testAllButtonsAreVisible() {
		ArrayList<Integer> expectedButtons = new ArrayList<Integer>();
		expectedButtons.add(R.id.btn_status_undo);
		expectedButtons.add(R.id.btn_status_redo);
		expectedButtons.add(R.id.btn_status_color);
		expectedButtons.add(R.id.btn_status_tool);

		ArrayList<ImageButton> imageButtons = mSolo.getCurrentImageButtons();
		for (ImageButton button : imageButtons) {
			expectedButtons.remove((Object) button.getId());
		}

		assertEquals("all buttons should be found", 0, expectedButtons.size());
	}
}
