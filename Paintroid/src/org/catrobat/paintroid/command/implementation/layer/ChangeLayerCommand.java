package org.catrobat.paintroid.command.implementation.layer;

import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.BaseCommand.NOTIFY_STATES;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ChangeLayerCommand extends BaseCommand {
	public int newLayerIndex;
	public int oldLayerIndex;

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);

		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}

	public ChangeLayerCommand(int newLayer, int oldLayer) {
		this.newLayerIndex = newLayer;
		this.oldLayerIndex = oldLayer;

	}

}
