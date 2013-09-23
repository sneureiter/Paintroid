package org.catrobat.paintroid.command.implementation.layer;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.CommandList;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class DeleteLayerCommand extends BaseCommand {
	public int layerIndex;

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);

		CommandList mList = PaintroidApplication.commandManager
				.getAllCommandList().get(layerIndex);

		if (mList.getThumbnail() != null) {
			mList.getThumbnail().recycle();
			mList.setThumbnail(null);
		}

		PaintroidApplication.commandManager.removeCommandList(layerIndex);

		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}

	public DeleteLayerCommand(int layerIndex) {
		this.layerIndex = layerIndex;
	}
}
