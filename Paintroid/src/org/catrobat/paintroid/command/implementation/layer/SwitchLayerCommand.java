package org.catrobat.paintroid.command.implementation.layer;

import java.util.LinkedList;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.CommandList;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class SwitchLayerCommand extends BaseCommand {

	public int firstLayer;
	public int secondLayer;

	public SwitchLayerCommand(int a, int b) {
		this.firstLayer = a;
		this.secondLayer = b;
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);

		LinkedList<CommandList> l = PaintroidApplication.commandManager
				.getAllCommandList();

		if (secondLayer > firstLayer) {
			CommandList tmpSecond = l.remove(secondLayer);
			CommandList tmpFirst = l.remove(firstLayer);

			l.add(firstLayer, tmpSecond);
			l.add(secondLayer, tmpFirst);
		} else {
			CommandList tmpFirst = l.remove(firstLayer);
			CommandList tmpSecond = l.remove(secondLayer);

			l.add(secondLayer, tmpFirst);
			l.add(firstLayer, tmpSecond);
		}

		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}
}
