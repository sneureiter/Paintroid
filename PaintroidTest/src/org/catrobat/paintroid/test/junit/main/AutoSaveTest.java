package org.catrobat.paintroid.test.junit.main;

import java.io.File;
import java.io.IOException;

import org.catrobat.paintroid.AutoSave;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.app.Activity;
import android.test.AndroidTestCase;
import android.util.Log;

public class AutoSaveTest extends AndroidTestCase {

	String mAutoSave = "AutoSave";
	String mAutoSaveDirectory;
	File AutoSaveDir;
	File mTestFile1;
	File mTestFile2;
	File mTestFile3;
	File mAutoSaveFile;

	@Override
	@Before
	public void setUp() {
		int setup = 0;
		Log.d("AutoSaveTest test", "setup " + setup++);
		try {
			mAutoSaveDirectory = (String) PrivateAccess.getMemberValue(AutoSave.class, null, "mAutoSaveDirectory");
		} catch (Exception exception) {
			fail("XXX"); // TODO
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

	@Test
	public void testTrigger() {
		try {
			int mAutoSaveCounter = (Integer) PrivateAccess.getMemberValue(AutoSave.class, null, "mAutoSaveCounter");
			assertEquals(0, mAutoSaveCounter);
			AutoSave.trigger();
			mAutoSaveCounter = (Integer) PrivateAccess.getMemberValue(AutoSave.class, null, "mAutoSaveCounter");
			assertEquals(1, mAutoSaveCounter);
			AutoSave.trigger();
			mAutoSaveCounter = (Integer) PrivateAccess.getMemberValue(AutoSave.class, null, "mAutoSaveCounter");
			assertEquals(2, mAutoSaveCounter);
			AutoSave.trigger();
			mAutoSaveCounter = (Integer) PrivateAccess.getMemberValue(AutoSave.class, null, "mAutoSaveCounter");
			assertEquals(3, mAutoSaveCounter);
		} catch (Exception exception) {
			fail("Couln't access private member");
		}
	}

	@Test
	public void testClear() {
		try {
			mTestFile1.createNewFile();
			mTestFile2.createNewFile();
			mTestFile3.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(3, AutoSaveDir.listFiles().length);
		AutoSave.clear(mTestFile1);
		assertEquals(1, AutoSaveDir.listFiles().length);
		try {
			mTestFile2.createNewFile();
			mTestFile3.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(3, AutoSaveDir.listFiles().length);
		AutoSave.clear();
		assertEquals(0, AutoSaveDir.listFiles().length);
	}

	public void testautoSaveImageExists() {
		Activity a = null;
		String location = null;

		assertFalse(AutoSave.autoSaveImageExists(location, a));
		try {
			mAutoSaveFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(AutoSave.autoSaveImageExists(location, a));

		for (File f : AutoSaveDir.listFiles()) {
			f.delete();
		}

		String pngPath = "TestPath/for/Image";
		String checksum = Utils.md5Checksum(pngPath);
		String AutoSaveFile = mAutoSaveDirectory + checksum;

		assertFalse(AutoSave.autoSaveImageExists(pngPath, a));

		File hashNameAutoSavePicture = new File(AutoSaveFile + ".png");
		try {
			hashNameAutoSavePicture.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(AutoSave.autoSaveImageExists(pngPath, a));

		for (File f : AutoSaveDir.listFiles()) {
			f.delete();
		}
		assertFalse(AutoSave.autoSaveImageExists(pngPath, a));
	}
}