package org.catrobat.paintroid.command.implementation;

import java.util.LinkedList;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;

public class CommandList extends LinkedList<Command> {

	LinkedList<Command> mCommands;
	boolean isHidden = false;
	int lastCommandIndex = 0;
	int lastCommandCount = 0;
	Bitmap thumbnail = null;

	public CommandList(LinkedList<Command> c) {
		this.mCommands = c;
	}

	public LinkedList<Command> getCommands() {
		return mCommands;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public int getLastCommandIndex() {
		return lastCommandIndex;
	}

	public void setLastCommandIndex(int lastCommandIndex) {
		this.lastCommandIndex = lastCommandIndex;
	}

	public int getLastCommandCount() {
		return lastCommandCount;
	}

	public void setLastCommandCount(int lastCommandCount) {
		this.lastCommandCount = lastCommandCount;
	}

	public Bitmap getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}

	public void generateThumbnail() {
		Point screenSize = PaintroidApplication.getScreenSize();
		Bitmap b = Bitmap.createBitmap(PaintroidApplication.getScreenSize().x,
				PaintroidApplication.getScreenSize().y, Config.ARGB_8888);

		Canvas c = new Canvas(b);

		for (int i = 0; i < this.getLastCommandCount(); i++) {
			Command command = mCommands.get(i);

			if (!((command instanceof BitmapCommand) && i == 0)) {

				if (command instanceof FlipCommand) {
					Bitmap mTmp = ((FlipCommand) command).runLayer(c, b);

					if (mTmp != null) {
						b = mTmp;
					}
				} else if ((command instanceof CropCommand)) {
					if (!CropCommand.isOriginal()) {
						Bitmap mTmp = ((CropCommand) command).runLayer(c, b);

						if (mTmp != null) {
							b = mTmp;
						}
					}
				} else {
					command.run(c, b);
				}
			}
			c.drawBitmap(b, new Matrix(), null);
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

		this.setThumbnail(Bitmap.createScaledBitmap(b, (int) newWidth,
				(int) newHeight, true));
		b.recycle();
	}
}
