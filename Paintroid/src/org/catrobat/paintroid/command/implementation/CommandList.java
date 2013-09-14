package org.catrobat.paintroid.command.implementation;

import java.util.LinkedList;

import org.catrobat.paintroid.command.Command;

public class CommandList extends LinkedList<Command> {

	LinkedList<Command> mCommands;
	boolean isHidden = false;
	boolean isDeleted = false;
	int lastCommandIndex = 0;
	int lastCommandCount = 0;

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

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
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
}
