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

		LinkedList<CommandList> l = PaintroidApplication.commandManager
				.getCommandList();

		CommandList tmp = l.remove(firstLayer);
		l.add(firstLayer, l.get(secondLayer));
		l.add(secondLayer, tmp);
	}
}
