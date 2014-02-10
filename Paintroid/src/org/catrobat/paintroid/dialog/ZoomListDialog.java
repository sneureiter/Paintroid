package org.catrobat.paintroid.dialog;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class ZoomListDialog extends BaseDialog implements
		android.view.View.OnClickListener {

	public static ZoomListDialog instance;

	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "ZoomListDialog has not been initialized. Call init() first!";
	private MainActivity mParent;
	Spinner zoomSelector;

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
		setTitle(R.string.dialog_tools_title); // change to new string
		setCanceledOnTouchOutside(true);

		zoomSelector = (Spinner) findViewById(R.id.spinner1);
		zoomSelector.setSelection(5);
		Button submitBtn = (Button) findViewById(R.id.acceptBtn);
		submitBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int itemPosition = zoomSelector.getSelectedItemPosition();
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
			break;
		case 5:
			PaintroidApplication.perspective.setScale(0.95f);
			dismiss();
			break;
		case 6:
			PaintroidApplication.perspective.setScale(0.855f);
			dismiss();
			break;
		case 7:
			PaintroidApplication.perspective.setScale(0.76f);
			dismiss();
			break;
		case 8:
			PaintroidApplication.perspective.setScale(0.665f);
			dismiss();
			break;
		case 9:
			PaintroidApplication.perspective.setScale(0.57f);
			dismiss();
			break;
		case 10:
			PaintroidApplication.perspective.setScale(0.475f);
			dismiss();
			break;
		case 11:
			PaintroidApplication.perspective.setScale(0.38f);
			dismiss();
			break;
		case 12:
			PaintroidApplication.perspective.setScale(0.285f);
			dismiss();
			break;
		case 13:
			PaintroidApplication.perspective.setScale(0.19f);
			dismiss();
			break;
		case 14:
			PaintroidApplication.perspective.setScale(0.1f);
			dismiss();
			break;
		}

	}
}
