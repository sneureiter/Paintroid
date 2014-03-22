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

import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.implementation.CommandManagerImplementation;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.actionbarsherlock.view.Menu;

public class PaintroidApplication extends Application {
	public static final String TAG = "PAINTROID";

	public static Context applicationContext;
	public static DrawingSurface drawingSurface;
	public static CommandManager commandManager;
	public static Tool currentTool;
	public static Perspective perspective;
	public static boolean openedFromCatroid = false;
	public static boolean isPlainImage = true;
	// head
	public static Menu menu;
	// old
	public static boolean isSaved = true;
	public static File savedBitmapFile = null;
	public static boolean saveCopy = false;

	public static int currentLayer;
	public static boolean hasChanged = false;

	@Override
	public void onCreate() {
		super.onCreate();
		applicationContext = getApplicationContext();
		commandManager = new CommandManagerImplementation();
	}

	public static String getVersionName(Context context) {
		String versionName = "unknown";
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException nameNotFoundException) {
			Log.e(PaintroidApplication.TAG, "Name not found",
					nameNotFoundException);
		}
		return versionName;
	}

	@SuppressWarnings("deprecation")
	@TargetApi(13)
	public static Point getScreenSize() {
		int Measuredwidth = 0;
		int Measuredheight = 0;
		Point size = new Point();
		WindowManager w = (WindowManager) applicationContext
				.getSystemService(Context.WINDOW_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			w.getDefaultDisplay().getSize(size);
			Measuredwidth = size.x;
			Measuredheight = size.y;
		} else {
			Display d = w.getDefaultDisplay();
			Measuredwidth = d.getWidth();
			Measuredheight = d.getHeight();
		}
		return new Point(Measuredwidth, Measuredheight);
	}

}
