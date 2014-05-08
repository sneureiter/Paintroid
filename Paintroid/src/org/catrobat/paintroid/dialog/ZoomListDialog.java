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
	private int selection = 5;
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

	private void zoomOut() {
		applyZoomOnChangeControl = false;
		float scale = 1 / ZOOM_IN_SCALE;
		PaintroidApplication.perspective.multiplyScale(scale);
		PaintroidApplication.perspective.translate(0, 0);

	}

	private void zoomIn() {
		applyZoomOnChangeControl = false;
		float scale = ZOOM_IN_SCALE;
 		PaintroidApplication.perspective.multiplyScale(scale);
		PaintroidApplication.perspective.translate(0, 0);
	}

	public void changeZoomLevel(View v) {
		int itemPosition = zoomSelector.getSelectedItemPosition();
		if (!applyZoomOnChangeControl) {
			dismiss();
			return;
		}

		switch (itemPosition) {
		case 0:
           PaintroidApplication.perspective.setScale(5f);
			dismiss();
			break;
		case 1:
			PaintroidApplication.perspective.setScale(4f);
			dismiss();
			break;
		case 2:
			PaintroidApplication.perspective.setScale(3f);
			dismiss();
			break;
		case 3:
			PaintroidApplication.perspective.setScale(2f);
			dismiss();
			break;
		case 4:
			PaintroidApplication.perspective.setScale(1.5f);
			dismiss();
			break;
		case 5:
			PaintroidApplication.perspective.setScale(1f);
			dismiss();
			break;
		case 6:
			PaintroidApplication.perspective.setScale(0.9f);
			dismiss();
			break;
		case 7:
			PaintroidApplication.perspective.setScale(0.8f);
			dismiss();
			break;
		case 8:
			PaintroidApplication.perspective.setScale(0.7f);
			dismiss();
			break;
		case 9:
			PaintroidApplication.perspective.setScale(0.6f);
			dismiss();
			break;
		case 10:
			PaintroidApplication.perspective.setScale(0.5f);
			dismiss();
			break;
		case 11:
			PaintroidApplication.perspective.setScale(0.4f);
			dismiss();
			break;
		case 12:
			PaintroidApplication.perspective.setScale(0.3f);
			dismiss();
			break;
		case 13:
			PaintroidApplication.perspective.setScale(0.2f);
			selection = 13;
			dismiss();
			break;
		case 14:
			PaintroidApplication.perspective.setScale(0.1f);
			selection = 14;
			dismiss();
			break;
		}



	}
}
