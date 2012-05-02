/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.paintroid.test.integration;

import org.junit.After;
import org.junit.Before;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;

import com.jayway.android.robotium.solo.Solo;

public class BaseIntegrationTestClass extends ActivityInstrumentationTestCase2<MainActivity> {

	protected Solo mSolo;
	protected TextView mToolBarButtonMain;
	protected TextView mToolBarButtonOne;
	protected TextView mToolBarButtonTwo;
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected final int TIMEOUT = 2000;
	protected MainActivity mMainActivity;
	protected final int VERSION_HONEYCOMB = 11;

	public BaseIntegrationTestClass() throws Exception {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		Log.i("PAINTROID", "<setUp>: " + this.getName());

		try {
			Thread.sleep(500);
			int activityCount = 0;
			while (getActivity() == null && activityCount++ < 100) {
				Log.i("PAINTROID", "Activity counter " + activityCount);
				getActivity();
			}

			mSolo = new Solo(getInstrumentation(), getActivity());
			Log.i("PAINTROID", "<setUp>: 0  " + this.getName());
			mMainActivity = (MainActivity) mSolo.getCurrentActivity();
			Log.i("PAINTROID", "<setUp>: 1  " + this.getName());
			mToolBarButtonMain = (TextView) getActivity().findViewById(R.id.btn_Tool);
			Log.i("PAINTROID", "<setUp>: 2  " + this.getName());
			mToolBarButtonOne = (TextView) getActivity().findViewById(R.id.btn_Parameter1);
			Log.i("PAINTROID", "<setUp>: 3  " + this.getName());
			mToolBarButtonTwo = (TextView) getActivity().findViewById(R.id.btn_Parameter2);
			Log.i("PAINTROID", "<setUp>: 4  " + this.getName());
			mScreenWidth = mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
			Log.i("PAINTROID", "<setUp>: 5  " + this.getName());
			mScreenHeight = mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
			Log.i("PAINTROID", "</setUp:> " + this.getName());
		} catch (Exception e) {
			fail(this.getName() + ": " + e.getMessage());
		}
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		Log.i("PAINTROID", "<tearDown: " + this.getName());
		try {
			mMainActivity.finish();
			mSolo.finalize();
			// mSolo = null;
			// mMainActivity = null;
			// mToolBarButtonMain = null;
			// mToolBarButtonOne = null;
			// mToolBarButtonTwo = null;
			System.gc();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		super.tearDown();
		Log.i("PAINTROID", "</tearDown>: " + this.getName());
	}

}
