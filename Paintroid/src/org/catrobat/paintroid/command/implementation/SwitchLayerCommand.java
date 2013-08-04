package org.catrobat.paintroid.command.implementation;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class SwitchLayerCommand extends BaseCommand {

	public SwitchLayerCommand(int a, int b) {
		LinkedList<Command> l = PaintroidApplication.commandManager
				.getCommands();
		sortList(l);

		int firstIndex = 0;
		int secondIndex = 0;

		for (int i = 0; i < PaintroidApplication.commandManager.getCommands()
				.size(); i++) {
			if (firstIndex == 0
					&& PaintroidApplication.commandManager.getCommands().get(i)
							.getCommandLayer() == a) {
				firstIndex = i;
			} else if (secondIndex == 0
					&& PaintroidApplication.commandManager.getCommands().get(i)
							.getCommandLayer() == b) {
				secondIndex = i;
			}
		}
	}

	private void sortList(LinkedList<Command> l) {
		Collections.sort(l, new Comparator<Command>() {
			@Override
			public int compare(Command o1, Command o2) {
				if (o1.getCommandLayer() < o2.getCommandLayer()) {
					return -1;
				}
				if (o1.getCommandLayer() > o2.getCommandLayer()) {
					return 1;
				}
				return 0;
			}
		});
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		// TODO Auto-generated method stub
	}

}
