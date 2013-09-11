package org.catrobat.paintroid.command.implementation;

import java.util.LinkedList;

import org.catrobat.paintroid.command.Command;

public class CommandList extends LinkedList<Command> {

	LinkedList<Command> mCommands;
	boolean isHidden = false;
	boolean isDeleted = false;

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

}
