package org.catrobat.paintroid;

import java.io.File;

import org.catrobat.paintroid.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;

public class AutoSave {
	private static String fileName;
	private static int mAutoSaveCounter;
	private static String mAutoSaveDirectory = FileIO
			.createNewEmptyPictureFile("autosave").getAbsolutePath()
			+ File.separator;
	private static Activity mActivity = null;
	static {
		new File(mAutoSaveDirectory).mkdir();
	}

	public static void clear() {
		clear(new File(""));
	}

	public static void clear(File currentFile) {
		File file = new File(mAutoSaveDirectory);
		if (file.listFiles() != null) {
			for (File f : file.listFiles()) {
				if (!f.equals(currentFile)) {
					f.delete();
				}
			}
		}
	}

	public static boolean autoSaveImageExists(String catroidPicturePath,
			Activity activity) {
		mActivity = activity;
		if (catroidPicturePath == null || catroidPicturePath.length() < 4) {
			fileName = "autosave";
		} else {
			fileName = Utils.md5Checksum(catroidPicturePath);
		}

		File file = new File(mAutoSaveDirectory + fileName + ".png");
		if (file.exists()) {
			return true;
		}
		return false;
	}

	public static void trigger() {
		handleBitmap();
	}

	private static void handleBitmap() {
		mAutoSaveCounter++;

		if (mAutoSaveCounter % 10 == 0) {
			FileIO.saveBitmap(mActivity,
					PaintroidApplication.drawingSurface.getBitmapCopy(),
					"autosave/" + fileName);
			// TODO: Autosave Commands
		}
	}

	public static void incrementCounter() {
		mAutoSaveCounter--;
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

	public static void setDrawingSurface() {
		Bitmap bitmap = FileIO.getBitmapFromFile(new File(mAutoSaveDirectory
				+ fileName + ".png"));
		PaintroidApplication.drawingSurface.setBitmap(bitmap);
	}
}
