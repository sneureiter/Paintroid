package org.catrobat.paintroid.command.implementation;

import org.catrobat.paintroid.PaintroidApplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class DeleteLayerCommand extends BaseCommand {
	public int layerIndex;

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);
		for (int i = 1; i < PaintroidApplication.commandManager.getCommands()
				.size(); i++) {
			Log.i(PaintroidApplication.TAG, PaintroidApplication.commandManager
					.getCommands().get(i).toString());

			if (PaintroidApplication.commandManager.getCommands().get(i)
					.getCommandLayer() == this.layerIndex) {
				PaintroidApplication.commandManager.getCommands().remove(i)
						.freeResources();
				PaintroidApplication.commandManager.decrementCounter();
			}
		}
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);

	}

	public DeleteLayerCommand(int layerIndex) {
		this.layerIndex = layerIndex;

	}

}
