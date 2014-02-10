package org.catrobat.paintroid.dialog;

import java.util.ArrayList;
import java.util.List;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ZoomListDialog extends BaseDialog {

	public static ZoomListDialog instance;

	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "ZoomListDialog has not been initialized. Call init() first!";
	private MainActivity mParent;

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

		Spinner zoomSelector = (Spinner) findViewById(R.id.spinner1);
		List<String> list = new ArrayList<String>();
		list.add("list 1");
		list.add("list 2");
		list.add("list 3");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mParent,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		zoomSelector.setAdapter(dataAdapter);
	}
}
