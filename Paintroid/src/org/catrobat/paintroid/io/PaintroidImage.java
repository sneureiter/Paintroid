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

package org.catrobat.paintroid.io;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class PaintroidImage {

	private static final int DEFAULT_QUALITY = 100;
	private static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.PNG;

	private Bitmap mBitmap;
	private File mImageFile;
	private int mQuality;
	private CompressFormat mCompressFormat;

	public PaintroidImage(Bitmap bitmap) {
		mBitmap = bitmap;
		mQuality = DEFAULT_QUALITY;
		mCompressFormat = DEFAULT_COMPRESS_FORMAT;
	}

	public PaintroidImage(Bitmap bitmap, File imageFile) {
		this(bitmap);
		mImageFile = imageFile;
	}

}