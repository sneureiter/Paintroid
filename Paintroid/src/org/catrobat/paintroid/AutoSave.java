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

package org.catrobat.paintroid;

import java.io.File;

import org.catrobat.paintroid.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;

public class AutoSave {
	private final static String AUTOSAVE_DEFAULT_FILE_NAME = "autosave";
	private final static String AUTOSAVE_DIRECTORY_NAME = "autosave";
	private final static int AUTOSAVE_EVERY_X_STEPS = 10;

	private static Activity mActivity;
	private static File mAutoSaveFile;
	private static File mAutoSaveRelativeFile;
	private static int mExecutedCommandsCounter;

	public static void deleteAllAutoSaveImages() {
		File[] autoSaveFiles = mAutoSaveFile.getParentFile().listFiles();
		if (autoSaveFiles != null) {
			for (File file : autoSaveFiles) {
				file.delete();
			}
		}
	}

	public static boolean existsAutoSaveImage(String catroidPicturePath,
			Activity activity) {
		mActivity = activity;

		String mAutoSaveFileName;
		if (catroidPicturePath == null) {
			mAutoSaveFileName = AUTOSAVE_DEFAULT_FILE_NAME;
		} else {
			mAutoSaveFileName = Utils.md5Checksum(catroidPicturePath);
		}

		mAutoSaveRelativeFile = new File(AUTOSAVE_DIRECTORY_NAME
				+ File.separator + mAutoSaveFileName);
		mAutoSaveFile = new File(FileIO.createNewEmptyPictureFile(""),
				mAutoSaveRelativeFile.getPath());

		return mAutoSaveFile.exists();
	}

	public static void trigger() {
		mExecutedCommandsCounter++;

		if (mExecutedCommandsCounter % AUTOSAVE_EVERY_X_STEPS == 0) {
			mAutoSaveFile.getParentFile().mkdirs();
			FileIO.saveBitmap(mActivity,
					PaintroidApplication.drawingSurface.getBitmapCopy(),
					mAutoSaveRelativeFile.getPath());
			// TODO: Autosave Commands
		}
	}

	public static void takeAutoSaveImageOption() {
		AlertDialog.Builder autoSaveAlertDialogBuilder = new AlertDialog.Builder(
				mActivity);
		autoSaveAlertDialogBuilder
				.setMessage(R.string.dialog_autosave_image)
				.setCancelable(true)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								setDrawingSurface();
							}
						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog autoSaveDialog = autoSaveAlertDialogBuilder.create();
		autoSaveDialog.show();
	}

	private static void setDrawingSurface() {
		Bitmap bitmap = FileIO.getBitmapFromFile(mAutoSaveFile);
		PaintroidApplication.drawingSurface.setBitmap(bitmap);
	}
}
