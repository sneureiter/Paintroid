package org.catrobat.paintroid.command.implementation;

import java.util.LinkedList;

import org.catrobat.paintroid.command.Command;

import android.graphics.Bitmap;

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
}
