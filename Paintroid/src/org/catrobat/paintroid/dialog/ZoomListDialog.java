package org.catrobat.paintroid.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;

public class ZoomListDialog extends BaseDialog {

	public static ZoomListDialog instance;

	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "ZoomListDialog has not been initialized. Call init() first!";
	private MainActivity mParent;
	private final static float ZOOM_IN_SCALE = 1.75f;
	private int selection = 9;
	private Spinner zoomSelector;
	private boolean applyZoomOnChangeControl;

	public ZoomListDialog(Context context) {
		super(context);
		mParent = (MainActivity) context;
	}

	public static ZoomListDialog getInstance() {
		if (instance == null) {
			throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
		}
		return instance;
	}

	public static void init(MainActivity mainActivity) {
		instance = new ZoomListDialog(mainActivity);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zoom_list);
		setTitle(R.string.dialog_zoom_list_title);
		setCanceledOnTouchOutside(true);

		zoomSelector = (Spinner) findViewById(R.id.zoomSelector);
		zoomSelector.setSelection(selection);
		zoomSelector
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> adapterView,
							View view, int i, long l) {
						applyZoomOnChangeControl = true;
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}
				});

		Log.d("zoomLevel", "apply selection: " + applyZoomOnChangeControl);


		Button submitBtn = (Button) findViewById(R.id.acceptBtn);
		submitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeZoomLevel(v);
			}
		});
	}

	@Override
	public void show() {
		applyZoomOnChangeControl = true;
		super.show();
	}


	public void changeZoomLevel(View v) {
		int itemPosition = zoomSelector.getSelectedItemPosition();
		if (!applyZoomOnChangeControl) {
			dismiss();
			return;
		}

        if(itemPosition < 10) {
            PaintroidApplication.perspective.setScale((itemPosition / 10f));
            dismiss();
        }
        else {
            PaintroidApplication.perspective.setScale(itemPosition - 9f);
            dismiss();
        }

	}
}
