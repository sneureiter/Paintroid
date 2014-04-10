/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

@SuppressLint("NewApi")
public abstract class FileIO {
	private static File PAINTROID_MEDIA_FILE = null;
	private static final int BUFFER_SIZE = 1024;

    public static enum ResizeType {
        STRETCH_TO_RECTANGLE, STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, FILL_RECTANGLE_WITH_SAME_ASPECT_RATIO, NO_RESIZE
    }

        private FileIO() {
	}

	public static File saveBitmap(Context context, Bitmap bitmap, String name) {
		if (initialisePaintroidMediaDirectory() == false) {
			return null;
		}

		final int QUALITY = 100;
		final String ENDING = ".png";
		final Bitmap.CompressFormat FORMAT = Bitmap.CompressFormat.PNG;
		File file = null;

		if (bitmap == null || bitmap.isRecycled() || name == null
				|| name.length() < 1) {
			Log.e(PaintroidApplication.TAG, "ERROR saving bitmap " + name);
		} else if (PaintroidApplication.savedBitmapFile != null
				&& !PaintroidApplication.saveCopy) {
			file = getFileFromPath(name);
		} else {
			file = createNewEmptyPictureFile(context, name + ENDING);
		}

		if (file != null) {
			try {
				if (file.exists() == false) {
					file.createNewFile();
				}
				bitmap.compress(FORMAT, QUALITY, new FileOutputStream(file));
				String[] paths = new String[] { file.getAbsolutePath() };
				MediaScannerConnection.scanFile(context, paths, null, null);
			} catch (Exception e) {
				Log.e(PaintroidApplication.TAG, "ERROR writing " + file, e);
			}
		}
		PaintroidApplication.savedBitmapFile = file;
		return file;
	}

	private static File getFileFromPath(String name) {
		String filePathAndName = PaintroidApplication.savedBitmapFile
				.getAbsolutePath();
		return new File(filePathAndName);
	}

    //copied from Pocket Code
    private static Bitmap scaleBitmap(Bitmap bitmap, int xSize, int ySize) {
        if (bitmap == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        float scaleWidth = (((float) xSize) / bitmap.getWidth());
        float scaleHeight = (((float) ySize) / bitmap.getHeight());
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (newBitmap.isMutable()) {
            return newBitmap;
        } else {
            return newBitmap.copy(Config.ARGB_8888, true);
        }
    }

    //copied from Pocket Code
    public static Bitmap getScaledBitmapFromFile(File imageFile, ResizeType resizeType) {
        if (imageFile.getAbsolutePath() == null) {
            return null;
        }

        int[] imageDimensions = getImageDimensions(imageFile.getAbsolutePath());
        int[] displayDimensions = getDisplayDimensions();

        int originalWidth = imageDimensions[0];
        int originalHeight = imageDimensions[1];
        int displayWidth = displayDimensions[0];
        int displayHeight = displayDimensions[1];
        int newWidth = originalHeight;
        int newHeight = originalWidth;
        int loadingSampleSize = 1;

        double sampleSizeWidth = originalWidth > displayWidth ? ((double) originalWidth) / (double) displayWidth : 1;
        double sampleSizeHeight = originalHeight > displayHeight ? ((double) originalHeight) / (double) displayHeight: 1;
        double sampleSizeMinimum = Math.min(sampleSizeWidth, sampleSizeHeight);
        double sampleSizeMaximum = Math.max(sampleSizeWidth, sampleSizeHeight);

        if (resizeType == ResizeType.NO_RESIZE) {
            newWidth = originalWidth;
            newHeight = originalHeight;
            Log.d("ScaleImageTag", "originalWidth = " + originalWidth + "  originalHeight = " + originalHeight);
        } else if (resizeType == ResizeType.STRETCH_TO_RECTANGLE) {
            newWidth = displayWidth;
            newHeight = displayHeight;
            loadingSampleSize = (int) Math.floor(sampleSizeMinimum);
        } else if (originalWidth > displayWidth || originalHeight > displayHeight) {
            newWidth = (int) Math.floor(originalWidth / sampleSizeMinimum);
            newHeight = (int) Math.floor(originalHeight / sampleSizeMinimum);
        }
            Log.d("ScaleImageTag", "newWidth = " + newWidth +  "  newHeight = " + newHeight);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = loadingSampleSize;

        Bitmap tempBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bitmapOptions);

        return scaleBitmap(tempBitmap, newWidth, newHeight);
    }

    public static int[] getDisplayDimensions() {
        int[] displayDimenstions = new int[2];

        DisplayMetrics metrics = new DisplayMetrics();
        Display display = ((WindowManager) PaintroidApplication.applicationContext
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getMetrics(metrics);
        displayDimenstions[0] = display.getWidth();
        displayDimenstions[1] = display.getHeight();

        return displayDimenstions;
    }

    //copied from Pocket Code
    public static int[] getImageDimensions(String imagePath) {
        int[] imageDimensions = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        imageDimensions[0] = options.outWidth;
        imageDimensions[1] = options.outHeight;

        return imageDimensions;
    }

	public static File createNewEmptyPictureFile(Context context,
			String filename) {
		if (initialisePaintroidMediaDirectory()) {
			return new File(PAINTROID_MEDIA_FILE, filename);
		} else {
			return null;
		}
	}

    private static boolean initialisePaintroidMediaDirectory() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            PAINTROID_MEDIA_FILE = new File(
                    Environment.getExternalStorageDirectory(),
                    "/"
                            + PaintroidApplication.applicationContext
                            .getString(R.string.app_name) + "/");
        } else {
            return false;
        }
        if (PAINTROID_MEDIA_FILE != null) {
            if (!PAINTROID_MEDIA_FILE.isDirectory()) {

                return PAINTROID_MEDIA_FILE.mkdirs();
            }
        } else {
            return false;
        }
        return true;
    }

	public static String getRealPathFromURI(Context context, Uri imageUri) {
		String path = null;
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(imageUri,
				filePathColumn, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			path = cursor.getString(columnIndex);
		} else {
			try {
				File file = new File(new java.net.URI(imageUri.toString()));
				path = file.getAbsolutePath();
			} catch (URISyntaxException e) {
				Log.e("PAINTROID", "URI ERROR ", e);
			}
		}

		return path;
	}

	public static String createFilePathFromUri(Activity activity, Uri uri) {
		// Problem here
        String filepath = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity
				.managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int columnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			filepath = cursor.getString(columnIndex);
		}

		if (filepath == null
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			String id = null;
            try {
                id = uri.getLastPathSegment();
            } catch (NullPointerException e)
            {
                //TODO catch nullPointerException
            }
            id = id.split(":")[1];
			final String[] imageColumns = { MediaStore.Images.Media.DATA };
			final String imageOrderBy = null;

			String state = Environment.getExternalStorageState();
			if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
				uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
			}
			uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

			cursor = activity.managedQuery(uri, imageColumns,
					MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);

			if (cursor.moveToFirst()) {
				filepath = cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
			}

		} else if (filepath == null) {
			filepath = uri.getPath();
		}
		return filepath;
	}

	public static void copyStream(InputStream inputStream,
			OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
	}
}
