package org.catrobat.paintroid.test.fileIO;

import java.io.File;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.net.Uri;
import android.os.Environment;

public class FileIOTest extends BaseIntegrationTestClass {

	public FileIOTest() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	private File tempFile;

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	@Override
	@After
	public void tearDown() throws Exception {

		if (tempFile.exists()) {
			tempFile.delete();
		}
	}

	@Test
	public void testGetRealPathFromURI() {

		String pathToFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
				+ PaintroidApplication.applicationContext.getString(R.string.app_name) + "/"
				+ mSolo.getString(R.string.temp_picture_name) + ".png";

		tempFile = new File(pathToFile);
		Uri uri = Uri.fromFile(tempFile);
		assertEquals(pathToFile, FileIO.getRealPathFromURI(getActivity(), uri));
	}

	public void testCreateFilePathFromURI() {
		String pathToFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
				+ PaintroidApplication.applicationContext.getString(R.string.app_name) + "/"
				+ mSolo.getString(R.string.temp_picture_name) + ".png";

		tempFile = new File(pathToFile);
		Uri uri = Uri.fromFile(tempFile);
		assertEquals(pathToFile, FileIO.createFilePathFromUri(getActivity(), uri));
	}
}
