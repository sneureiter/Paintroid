/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.junit.main;

import java.io.File;

import org.catrobat.paintroid.AutoSave;
import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.test.AndroidTestCase;
import android.util.Log;

public class AutoSaveTest extends AndroidTestCase {
	private File autoSaveFile;
	private File autoSaveDirectory;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		int setup = 0;
		Log.d("AutoSaveTest test", "setup " + setup++);

		File autoSaveRelativeFile = new File("autosavetest" + File.separator + "autosavetest");
		autoSaveFile = new File(FileIO.createNewEmptyPictureFile(""), autoSaveRelativeFile.getPath());
		autoSaveDirectory = autoSaveFile.getParentFile();

		autoSaveDirectory.mkdir();

		PrivateAccess.setMemberValue(AutoSave.class, null, "mAutoSaveFile", autoSaveFile);
		PrivateAccess.setMemberValue(AutoSave.class, null, "mAutoSaveRelativeFile", autoSaveRelativeFile);

		Log.d("AutoSaveTest test", "setup" + setup++);
		AutoSave.deleteAllAutoSaveImages();
		Log.d(PaintroidApplication.TAG, "set up end");
	}

	@Override
	@After
	public void tearDown() throws Exception {
		int step = 0;
		Log.i(PaintroidApplication.TAG, "td " + step++);
		PrivateAccess.setMemberValue(AutoSave.class, null, "mExecutedCommandsCounter", 0);

		Log.i(PaintroidApplication.TAG, "td " + step++);
		File[] autoSaveFiles = autoSaveDirectory.listFiles();
		if (autoSaveFiles != null) {
			for (File file : autoSaveFiles) {
				file.delete();
			}
		}
		autoSaveDirectory.delete();
		Log.i(PaintroidApplication.TAG, "td " + step++);
		super.tearDown();
		Log.i(PaintroidApplication.TAG, "td finish " + step++);
	}

	@Test
	public void testDefaultSettings() throws Exception {
		String autoSaveDefaultFileName = (String) PrivateAccess.getMemberValue(AutoSave.class, null,
				"AUTOSAVE_DEFAULT_FILE_NAME");
		String autoSaveDirectoryName = (String) PrivateAccess.getMemberValue(AutoSave.class, null,
				"AUTOSAVE_DIRECTORY_NAME");

		assertEquals("autosave", autoSaveDefaultFileName);
		assertEquals("autosave", autoSaveDirectoryName);
		assertEquals(10, PrivateAccess.getMemberValue(AutoSave.class, null, "AUTOSAVE_EVERY_X_STEPS"));
	}

	@Test
	public void testDefaultAutoSavePath() throws Exception {
		String autoSaveDefaultFileName = (String) PrivateAccess.getMemberValue(AutoSave.class, null,
				"AUTOSAVE_DEFAULT_FILE_NAME");
		checkAutoSavePath(null, autoSaveDefaultFileName);
	}

	@Test
	public void testCatroidAutoSavePath() throws Exception {
		String catroidPicturePath = "some/non/existing/image.png";
		String expectedAutoSaveFileName = Utils.md5Checksum(catroidPicturePath);
		checkAutoSavePath(catroidPicturePath, expectedAutoSaveFileName);
	}

	private void checkAutoSavePath(String catroidPicturePath, String expectedAutoSaveFileName) throws Exception {
		String autoSaveDirectoryName = (String) PrivateAccess.getMemberValue(AutoSave.class, null,
				"AUTOSAVE_DIRECTORY_NAME");

		AutoSave.existsAutoSaveImage(catroidPicturePath, null);
		File expectedAutoSaveRelativeFile = new File(autoSaveDirectoryName + File.separator + expectedAutoSaveFileName);
		File expectedAutoSaveFile = new File(FileIO.createNewEmptyPictureFile(""),
				expectedAutoSaveRelativeFile.getPath());

		assertEquals(expectedAutoSaveRelativeFile,
				PrivateAccess.getMemberValue(AutoSave.class, null, "mAutoSaveRelativeFile"));
		assertEquals(expectedAutoSaveFile, PrivateAccess.getMemberValue(AutoSave.class, null, "mAutoSaveFile"));
	}

	@Test
	public void testExistsAutoSaveImage() throws Exception {
		assertFalse(AutoSave.existsAutoSaveImage(null, null));
		File autoSaveFile = (File) PrivateAccess.getMemberValue(AutoSave.class, null, "mAutoSaveFile");

		autoSaveFile.getParentFile().mkdirs();
		autoSaveFile.createNewFile();
		assertTrue(AutoSave.existsAutoSaveImage(null, null));

		assertTrue(autoSaveFile.delete());
		assertTrue(autoSaveFile.getParentFile().delete());
	}

	@Test
	public void testTriggerCounter() throws Exception {
		for (int commandCounter = 0; commandCounter < 9; commandCounter++) {
			int executedCommandCounter = (Integer) PrivateAccess.getMemberValue(AutoSave.class, null,
					"mExecutedCommandsCounter");
			assertEquals("Wrong executedCommandsCounter", commandCounter, executedCommandCounter);
			AutoSave.trigger();
		}
	}

	@Test
	public void testDeleteAllAutoSaveImages() throws Exception {
		new File(autoSaveDirectory, "somefile.png").createNewFile();
		new File(autoSaveDirectory, "someotherfile.png").createNewFile();

		assertEquals(2, autoSaveDirectory.listFiles().length);
		AutoSave.deleteAllAutoSaveImages();
		assertEquals(0, autoSaveDirectory.listFiles().length);
	}
}
