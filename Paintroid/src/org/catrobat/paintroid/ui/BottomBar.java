package org.catrobat.paintroid.ui;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.BitmapCommand;
import org.catrobat.paintroid.command.implementation.CommandList;
import org.catrobat.paintroid.command.implementation.CropCommand;
import org.catrobat.paintroid.command.implementation.FlipCommand;
import org.catrobat.paintroid.dialog.ToolsDialog;
import org.catrobat.paintroid.dialog.layerchooser.LayerChooserDialog;
import org.catrobat.paintroid.tools.Tool;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class BottomBar implements View.OnTouchListener {
	private ImageButton mAttributeButton1;
	private ImageButton mAttributeButton2;
	private ImageButton mToolMenuButton;
	private ImageButton mLayerButton;

	private MainActivity mMainActivity;
	private int mCurrentLayer;
	private Point screenSize;

	public BottomBar(MainActivity mainActivity) {
		mMainActivity = mainActivity;

		mCurrentLayer = 0;
		PaintroidApplication.currentLayer = mCurrentLayer;
		screenSize = PaintroidApplication.getScreenSize();

		mAttributeButton1 = (ImageButton) mainActivity
				.findViewById(R.id.btn_bottom_attribute1);
		mAttributeButton1.setOnTouchListener(this);

		mAttributeButton2 = (ImageButton) mainActivity
				.findViewById(R.id.btn_bottom_attribute2);
		mAttributeButton2.setOnTouchListener(this);

		mToolMenuButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_bottom_tools);
		mToolMenuButton.setOnTouchListener(this);

		mLayerButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_bottom_layer);
		mLayerButton.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
			view.setBackgroundResource(R.color.transparent);
			switch (view.getId()) {
			case R.id.btn_bottom_attribute1:
				if (PaintroidApplication.currentTool != null) {
					PaintroidApplication.currentTool
							.attributeButtonClick(TopBar.ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1);
				}
				return true;
			case R.id.btn_bottom_attribute2:
				if (PaintroidApplication.currentTool != null) {
					PaintroidApplication.currentTool
							.attributeButtonClick(TopBar.ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2);
				}
				return true;
			case R.id.btn_bottom_tools:
				ToolsDialog.getInstance().show();
				return true;
			case R.id.btn_bottom_layer:
				onLayerTouch(motionEvent);
				return true;
			default:
				return false;
			}
		} else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			view.setBackgroundResource(R.color.abs__holo_blue_light);
		}
		return false;
	}

	private void onLayerTouch(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {

			PaintroidApplication.commandManager.saveCurrentCommandListPointer();
			generateThumbnail(PaintroidApplication.currentLayer);
			LayerChooserDialog.getInstance().show(
					mMainActivity.getSupportFragmentManager(), "layerchooser");
			LayerChooserDialog.getInstance().setInitialLayer(
					PaintroidApplication.currentLayer);
		}
	}

	private void generateThumbnail(int layer) {

		Bitmap b = PaintroidApplication.drawingSurface.getBitmapCopy();

		if (PaintroidApplication.commandManager.getAllCommandList().size() > 1) {
			b = Bitmap.createBitmap(PaintroidApplication.getScreenSize().x,
					PaintroidApplication.getScreenSize().y, Config.ARGB_8888);
			Canvas c = new Canvas(b);

			CommandList mList = PaintroidApplication.commandManager
					.getAllCommandList().get(PaintroidApplication.currentLayer);

			for (int i = 0; i < mList.getLastCommandCount(); i++) {
				Command command = mList.getCommands().get(i);

				if (!((command instanceof BitmapCommand) && i == 0)) {

					if (command instanceof FlipCommand) {
						Bitmap mTmp = ((FlipCommand) command).runLayer(c, b);

						if (mTmp != null) {
							b = mTmp;
						}
					} else if (command instanceof CropCommand) {
						continue;
					} else {
						command.run(c, b);
					}
				}
				c.drawBitmap(b, new Matrix(), null);
			}
			if (!CropCommand.isOriginal()) {
				b = PaintroidApplication.commandManager.getLastCropCommand()
						.runLayer(c, b);
			}
		}

		double ratioOriginal = (double) screenSize.x / (double) screenSize.y;
		double ratioNew = (double) b.getWidth() / (double) b.getHeight();

		double newWidth = 0;
		double newHeight = 0;

		// full width -> minimum scaling
		if (screenSize.x == b.getWidth()) {
			newWidth = screenSize.x / 10;
			newHeight = screenSize.y / 10;
		}
		// not full horizontal width
		else if (screenSize.x > b.getWidth()) {

			// if cropped image is like a "I" the width can not reach the
			// maximum, but the height can
			if (ratioNew < ratioOriginal) {
				newHeight = screenSize.y / 10;
				newWidth = newHeight * ratioNew;
			} else {
				newWidth = screenSize.x / 10;
				newHeight = newWidth / ratioNew;
			}
		}

		PaintroidApplication.commandManager.getCommandListByIndex(layer)
				.setThumbnail(
						Bitmap.createScaledBitmap(b, (int) newWidth,
								(int) newHeight, true));

	}

	public void setTool(Tool tool) {
		mAttributeButton1
				.setImageResource(tool
						.getAttributeButtonResource(TopBar.ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
		mAttributeButton2
				.setImageResource(tool
						.getAttributeButtonResource(TopBar.ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2));

	}
}
