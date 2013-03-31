package org.catrobat.paintroid.test.integration;

import java.io.File;

import org.catrobat.paintroid.AutoSave;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.test.ActivityInstrumentationTestCase2;

public class AutoSaveTriggerTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public AutoSaveTriggerTest() {
		super(MainActivity.class);
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		PrivateAccess.setMemberValue(AutoSave.class, null, "mExecutedCommandsCounter", 9);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		File autoSaveFile = (File) PrivateAccess.getMemberValue(AutoSave.class, null, "mAutoSaveFile");
		autoSaveFile.delete();
		autoSaveFile.getParentFile().delete();

		super.tearDown();
	}

	@Test
	public void testAutoSaveTrigger() throws Exception {
		assertFalse(AutoSave.existsAutoSaveImage(null, getActivity()));

		File autoSaveFile = new File(
				((File) PrivateAccess.getMemberValue(AutoSave.class, null, "mAutoSaveFile")).getAbsoluteFile() + ".png");

		assertFalse(autoSaveFile.exists());
		AutoSave.trigger();
		assertTrue(autoSaveFile.exists());
	}
}
