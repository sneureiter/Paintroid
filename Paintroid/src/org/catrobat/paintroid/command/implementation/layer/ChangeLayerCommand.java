package org.catrobat.paintroid.command.implementation.layer;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.BitmapCommand;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;

public class ChangeLayerCommand extends BaseCommand {

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

	private Bitmap generateImageOfAboveLayers(int currentLayer) {

		if (currentLayer > 0) {
			Bitmap b = Bitmap.createBitmap(480, 800, Config.ARGB_8888);
			Canvas c = new Canvas();
			c.setBitmap(b);

			for (int i = currentLayer - 1; i >= 0; i--) {

				for (int k = 0; k < PaintroidApplication.commandManager
						.getAllCommandList().get(i).getCommands().size(); k++) {

					if (!PaintroidApplication.commandManager
							.getAllCommandList().get(i).isHidden()
							&& !((PaintroidApplication.commandManager
									.getAllCommandList().get(i).getCommands()
									.get(k) instanceof BitmapCommand) && k == 0)) {
						PaintroidApplication.commandManager.getAllCommandList()
								.get(i).getCommands().get(k).run(c, b);
					}
				}
			}
			return b;
		}
		return null;
	}

	private Bitmap generateImageOfBelowLayers(int currentLayer) {

		if (currentLayer < PaintroidApplication.commandManager
				.getAllCommandList().size() - 1) {
			Bitmap b = Bitmap.createBitmap(480, 800, Config.ARGB_8888);
			Canvas c = new Canvas();
			c.setBitmap(b);

			for (int i = PaintroidApplication.commandManager
					.getAllCommandList().size() - 1; i > currentLayer; i--) {

				for (int k = 0; k < PaintroidApplication.commandManager
						.getAllCommandList().get(i).getCommands().size(); k++) {

					if (!PaintroidApplication.commandManager
							.getAllCommandList().get(i).isHidden()
							&& !((PaintroidApplication.commandManager
									.getAllCommandList().get(i).getCommands()
									.get(k) instanceof BitmapCommand) && k == 0)) {
						PaintroidApplication.commandManager.getAllCommandList()
								.get(i).getCommands().get(k).run(c, b);
					}
				}
			}
			return b;
		}
		return null;
	}

	public ChangeLayerCommand() {
	}
}
