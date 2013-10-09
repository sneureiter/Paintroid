package org.catrobat.paintroid.command.implementation.layer;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.BitmapCommand;
import org.catrobat.paintroid.command.implementation.CommandList;
import org.catrobat.paintroid.command.implementation.CropCommand;
import org.catrobat.paintroid.command.implementation.FlipCommand;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;

public class ChangeLayerCommand extends BaseCommand {
	static Point originalSize = PaintroidApplication.getScreenSize();

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);

		PaintroidApplication.commandManager
				.changeCurrentCommandList(PaintroidApplication.currentLayer);

		PaintroidApplication.commandManager
				.setmBitmapAbove(generateImageOfAboveLayers(PaintroidApplication.currentLayer));

		PaintroidApplication.commandManager
				.setmBitmapBelow(generateImageOfBelowLayers(PaintroidApplication.currentLayer));

		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}

	public static Bitmap generateImageOfAboveLayers(int currentLayer) {

		if (currentLayer > 0) {
			Bitmap b = Bitmap.createBitmap(originalSize.x, originalSize.y,
					Config.ARGB_8888);
			Canvas c = new Canvas();
			c.setBitmap(b);

			for (int i = currentLayer - 1; i >= 0; i--) {

				Bitmap tmp = Bitmap.createBitmap(originalSize.x,
						originalSize.y, Config.ARGB_8888);
				Canvas ctmp = new Canvas(tmp);

				CommandList mList = PaintroidApplication.commandManager
						.getAllCommandList().get(i);

				for (int k = 0; k < mList.getLastCommandCount(); k++) {

					if (!mList.isHidden()
							&& !((mList.getCommands().get(k) instanceof BitmapCommand) && k == 0)) {

						if (mList.getCommands().get(k) instanceof FlipCommand) {
							Bitmap mtmp = ((FlipCommand) mList.getCommands()
									.get(k)).runLayer(ctmp, tmp);
							if (mtmp != null) {
								tmp = mtmp;
							}
						} else if ((mList.getCommands().get(k) instanceof CropCommand)) {
							Bitmap mtmp = ((CropCommand) mList.getCommands()
									.get(k)).runLayer(ctmp, tmp);
							if (mtmp != null) {
								tmp = mtmp;
							}
						} else {
							mList.getCommands().get(k).run(ctmp, tmp);
						}
					}
				}
				if (tmp != null) {
					c.drawBitmap(tmp, new Matrix(), null);
					tmp.recycle();
					tmp = null;
					ctmp = null;
				}
			}
			return b;
		}
		return null;
	}

	public static Bitmap generateImageOfBelowLayers(int currentLayer) {

		if (currentLayer < PaintroidApplication.commandManager
				.getAllCommandList().size() - 1) {
			Bitmap b = Bitmap.createBitmap(originalSize.x, originalSize.y,
					Config.ARGB_8888);
			Canvas c = new Canvas();
			c.setBitmap(b);

			for (int i = PaintroidApplication.commandManager
					.getAllCommandList().size() - 1; i > currentLayer; i--) {

				Bitmap tmp = Bitmap.createBitmap(originalSize.x,
						originalSize.y, Config.ARGB_8888);
				Canvas ctmp = new Canvas(tmp);

				CommandList mList = PaintroidApplication.commandManager
						.getAllCommandList().get(i);

				for (int k = 0; k < mList.getLastCommandCount(); k++) {

					if (!mList.isHidden()
							&& !((mList.getCommands().get(k) instanceof BitmapCommand) && k == 0)) {

						if (mList.getCommands().get(k) instanceof FlipCommand) {
							Bitmap mtmp = ((FlipCommand) mList.getCommands()
									.get(k)).runLayer(ctmp, tmp);
							if (mtmp != null) {
								tmp = mtmp;
							}
						} else if ((mList.getCommands().get(k) instanceof CropCommand)) {
							Bitmap mtmp = ((CropCommand) mList.getCommands()
									.get(k)).runLayer(ctmp, tmp);
							if (mtmp != null) {
								tmp = mtmp;
							}
						} else {
							mList.getCommands().get(k).run(ctmp, tmp);
						}
					}
				}
				if (tmp != null) {
					c.drawBitmap(tmp, new Matrix(), null);
					tmp.recycle();
					tmp = null;

					ctmp = null;
				}
			}
			return b;
		}
		return null;
	}

	public ChangeLayerCommand() {
	}
}
