package org.catrobat.paintroid.test.fileIO;

import java.io.File;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.net.Uri;
import android.os.Environment;
import android.test.AndroidTestCase;

public class FileIOTest extends AndroidTestCase {

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetRealPathFromURI() {

		String pathToFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
				+ PaintroidApplication.applicationContext.getString(R.string.app_name) + "/"
				+ mContext.getResources().getString(R.string.temp_picture_name) + ".png";

		File tempFile = new File(pathToFile);
		Uri uri = Uri.fromFile(tempFile);
		assertEquals(pathToFile, FileIO.getRealPathFromURI(mContext, uri));

	}

}
