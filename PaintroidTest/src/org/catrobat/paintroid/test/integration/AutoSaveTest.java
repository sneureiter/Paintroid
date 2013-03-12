package org.catrobat.paintroid.test.integration;

import java.io.File;

import org.catrobat.paintroid.AutoSave;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.After;
import org.junit.Before;

import android.util.Log;

public class AutoSaveTest extends BaseIntegrationTestClass {

	String mAutoSave = "AutoSave";
	String mAutoSaveDirectory;
	File AutoSaveDir;
	File mTestFile1;
	File mTestFile2;
	File mTestFile3;
	File mAutoSaveFile;

	public AutoSaveTest() throws Exception {
		super();
	}

	@Override
	@Before
	public void setUp() {
		super.setUp();
		int setup = 0;
		Log.d("AutoSaveTest test", "setup " + setup++);
		try {
			mAutoSaveDirectory = (String) PrivateAccess.getMemberValue(AutoSave.class, null, "mAutoSaveDirectory");
		} catch (Exception exception) {
			fail(exception.getMessage());
		}
		Log.d("AutoSaveTest test", "setup" + setup++);
		AutoSaveDir = new File(mAutoSaveDirectory);
		Log.d("AutoSaveTest test", "setup" + setup++);
		if (AutoSaveDir != null && AutoSaveDir.listFiles() != null) {
			for (File f : AutoSaveDir.listFiles()) {
				f.delete();
			}
		}
		Log.d("AutoSaveTest test", "setup " + setup++);
		mTestFile1 = new File(mAutoSaveDirectory + "f1" + ".png");
		mTestFile2 = new File(mAutoSaveDirectory + "f2" + ".png");
		mTestFile3 = new File(mAutoSaveDirectory + "f3" + ".png");
		Log.d("AutoSaveTest test", "setup" + setup++);
		mAutoSaveFile = new File(mAutoSaveDirectory + mAutoSave + ".png");
		Log.d(PaintroidApplication.TAG, "set up end");
	}

	@Override
	@After
	public void tearDown() throws Exception {
		int step = 0;
		Log.i(PaintroidApplication.TAG, "td " + step++);
		PrivateAccess.setMemberValue(AutoSave.class, null, "mAutoSaveCounter", 0);
		Log.i(PaintroidApplication.TAG, "td " + step++);
		if (AutoSaveDir != null && AutoSaveDir.listFiles() != null) {
			for (File f : AutoSaveDir.listFiles()) {
				f.delete();
			}
		}
		Log.i(PaintroidApplication.TAG, "td finish " + step++);
		super.tearDown();
		Log.i(PaintroidApplication.TAG, "td finish " + step++);
	}

	public void testHandleBitmap() {
		String location = "test";
		assertFalse(AutoSave.autoSaveImageExists(location, mSolo.getCurrentActivity()));
		// File pngFile = new File(mAutoSaveDirectory + "test" + ".png");
		// try {
		// pngFile.createNewFile();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// for (int i = 0; i < 10; i++)
		// AutoSave.trigger();
		// assertTrue(AutoSave.autoSaveImageExists(location, getActivity()));
	}
}