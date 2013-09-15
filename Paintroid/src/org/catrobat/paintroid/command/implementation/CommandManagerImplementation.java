/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.command.implementation;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.UndoRedoManager;
import org.catrobat.paintroid.command.UndoRedoManager.StatusMode;
import org.catrobat.paintroid.command.implementation.layer.ChangeLayerCommand;
import org.catrobat.paintroid.command.implementation.layer.DeleteLayerCommand;
import org.catrobat.paintroid.command.implementation.layer.HideLayerCommand;
import org.catrobat.paintroid.command.implementation.layer.ShowLayerCommand;
import org.catrobat.paintroid.command.implementation.layer.SwitchLayerCommand;
import org.catrobat.paintroid.dialog.layerchooser.LayerChooserDialog;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Point;
import android.util.Log;

public class CommandManagerImplementation implements CommandManager, Observer {
	private static final int MAX_COMMANDS = 512;

	private LinkedList<Command> mCurrentCommandList;
	private final LinkedList<CommandList> mAllCommandLists;
	private int mCommandCounter;
	private int mCommandIndex;
	private Bitmap mOriginalBitmap;

	public Bitmap mBitmapAbove = null;
	public Bitmap mBitmapBelow = null;

	public boolean belowUsed = false;
	public boolean aboveUsed = false;

	private int lastLayer;

	public CommandManagerImplementation() {
		mAllCommandLists = new LinkedList<CommandList>();

		mCurrentCommandList = new LinkedList<Command>();
		// The first command in the list is needed to clear the image when
		// rolling back commands.
		mCurrentCommandList.add(new ClearCommand());
		mAllCommandLists.add(0, new CommandList(mCurrentCommandList));

		mCommandCounter = 1;
		mCommandIndex = 1;
		lastLayer = 0;
	}

	@Override
	public boolean hasCommands() {
		return mCommandCounter >= 1;
	}

	@Override
	public void setOriginalBitmap(Bitmap bitmap) {
		mOriginalBitmap = bitmap.copy(Config.ARGB_8888, true);
		// If we use some custom bitmap, this first command is used to restore
		// it (instead of clear).
		mCurrentCommandList.removeFirst().freeResources();
		mCurrentCommandList.addFirst(new BitmapCommand(mOriginalBitmap, false));
		incrementCounter();
	}

	@Override
	public synchronized void resetAndClear() {
		if (mOriginalBitmap != null && !mOriginalBitmap.isRecycled()) {
			mOriginalBitmap.recycle();
			mOriginalBitmap = null;
		}

		for (int z = 0; z < mAllCommandLists.size(); z++) {

			for (int i = 0; i < mAllCommandLists.get(z).size(); i++) {
				mAllCommandLists.get(z).getCommands().get(i).freeResources();
			}
			mAllCommandLists.get(z).clear();
		}
		mAllCommandLists.clear();

		mCurrentCommandList.clear();
		mCurrentCommandList.add(new ClearCommand());
		mAllCommandLists.add(0, new CommandList(mCurrentCommandList));

		mCommandCounter = 0;
		mCommandIndex = 0;

		UndoRedoManager.getInstance().update(StatusMode.DISABLE_REDO);
		UndoRedoManager.getInstance().update(StatusMode.DISABLE_UNDO);
	}

	@Override
	public synchronized Command getNextCommand() {

		if (mCommandIndex == 0 && mBitmapBelow != null && !belowUsed) {
			belowUsed = true;
			aboveUsed = false;
			return new StampCommand(mBitmapBelow, new Point(240, 400), 480f,
					800f, 0);

		} else if (mCommandIndex == 0 && mBitmapBelow == null && !belowUsed) {
			belowUsed = true;
			aboveUsed = false;
			return new BitmapCommand(mOriginalBitmap, false);
		}

		if (mCommandIndex < mCommandCounter) {

			Log.i("my", "" + mCommandIndex + " - " + mCommandCounter);

			if (mCurrentCommandList.get(mCommandIndex).isHidden()) {
				mCommandIndex++;

				return getNextCommand();
			}
			return mCurrentCommandList.get(mCommandIndex++);

		} else if (mBitmapAbove != null && mCommandIndex == mCommandCounter
				&& mCommandCounter == mCurrentCommandList.size() && !aboveUsed) {
			aboveUsed = true;
			belowUsed = false;

			return new StampCommand(mBitmapAbove, new Point(240, 400), 480f,
					800f, 0);
		} else {
			return null;
		}
	}

	@Override
	public synchronized boolean commitCommand(Command command) {

		UndoRedoManager.getInstance().update(StatusMode.DISABLE_REDO);

		// Switch-Layer-Command & Hide-/Show-Layer-Command & Change-Layer-
		// Command shall not be saved and just run once
		if (command instanceof SwitchLayerCommand
				|| command instanceof ShowLayerCommand
				|| command instanceof HideLayerCommand
				|| command instanceof ChangeLayerCommand
				|| command instanceof DeleteLayerCommand) {

			// if (hasRedosLeft(1)) {
			// UndoRedoManager.getInstance().update(StatusMode.ENABLE_REDO);
			// }
			//
			// UndoRedoManager.getInstance().update(StatusMode.DISABLE_UNDO);
			command.run(null, null);

			this.resetIndex();
			return mCurrentCommandList != null;
		}

		if (mCommandCounter == MAX_COMMANDS) {
			// TODO handle this and don't return false. Hint: apply first
			// command to bitmap.
			return false;
		} else {
			mCommandCounter++;
			UndoRedoManager.getInstance().update(
					UndoRedoManager.StatusMode.ENABLE_UNDO);
		}

		((BaseCommand) command).addObserver(this);
		PaintroidApplication.isSaved = false;
		command.setCommandLayer(PaintroidApplication.currentLayer);

		if (LayerChooserDialog.layer_data != null) {
			if (LayerChooserDialog.layer_data
					.get(PaintroidApplication.currentLayer).visible == false) {
				command.setHidden(true);
			}
		}

		// int position = findLastCallIndexSorted(mCurrentCommandList,
		// PaintroidApplication.currentLayer, false);

		mCurrentCommandList.add(command);

		if (mAllCommandLists.get(PaintroidApplication.currentLayer).isHidden()
				|| (PaintroidApplication.currentLayer != 0)) {
			this.resetIndex();
		}

		return mCurrentCommandList.get(mCommandIndex) != null;
	}

	// private int findLastCallIndexSorted(LinkedList<Command> mCommandList,
	// int currentLayer, boolean withUndone) {
	//
	// if (mCommandList.size() == 1) {
	// return 1;
	// } else {
	// if (currentLayer != lastLayer || mCommandList.size() == 2) {
	// mCommandList = sortList(mCommandList);
	// }
	// if (withUndone == false) {
	// for (int i = mCommandList.size() - 1; i >= 1; i--) {
	// if (mCommandList.get(i).getCommandLayer() == currentLayer
	// && mCommandList.get(i).isUndone() == false) {
	// lastLayer = currentLayer;
	// return i + 1;
	// }
	// }
	// } else {
	// for (int i = 1; i < mCommandList.size(); i++) {
	// if (mCommandList.get(i).getCommandLayer() == currentLayer
	// && mCommandList.get(i).isUndone() == true) {
	// lastLayer = currentLayer;
	// return i;
	// }
	// }
	// }
	// }
	//
	// return 1;
	// }
	//
	// private void printList() {
	// for (int i = 0; i < mCurrentCommandList.size(); i++) {
	// Log.i(PaintroidApplication.TAG, i + ":"
	// + mCurrentCommandList.get(i).toString() + " ; "
	// + mCurrentCommandList.get(i).getCommandLayer() + " "
	// + mCurrentCommandList.get(i).isUndone() + " ");
	// }
	//
	// }
	//
	// public LinkedList<Command> sortList(LinkedList<Command> cl) {
	// if (cl.size() > 0) {
	// Command firstCommand = cl.removeFirst();
	// Collections.sort(cl, new Comparator<Command>() {
	// @Override
	// public int compare(Command o1, Command o2) {
	// if (o1 instanceof DeleteLayerCommand
	// || o2 instanceof DeleteLayerCommand) {
	// return -1;
	// }
	// if (o1.getCommandLayer() > o2.getCommandLayer()) {
	// return -1;
	// }
	// if (o1.getCommandLayer() < o2.getCommandLayer()) {
	// return 1;
	// }
	// return 0;
	// }
	// });
	// cl.addFirst(firstCommand);
	// }
	// return cl;
	// }

	@Override
	public synchronized void undo() {
		if (mCommandCounter > 1) {
			mCommandCounter--;
			resetIndex();
			UndoRedoManager.getInstance().update(
					UndoRedoManager.StatusMode.ENABLE_REDO);
			if (mCommandCounter <= 1) {
				UndoRedoManager.getInstance().update(
						UndoRedoManager.StatusMode.DISABLE_UNDO);
			}
		}
	}

	// @Override
	// public boolean hasUndosLeft(int pos) {
	// for (int i = 1; i < pos; i++) {
	// if (mCurrentCommandList.get(i).getCommandLayer() ==
	// PaintroidApplication.currentLayer
	// && !mCurrentCommandList.get(i).isUndone()) {
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// @Override
	// public boolean hasRedosLeft(int pos) {
	// for (int i = mCurrentCommandList.size() - 1; i > pos; i--) {
	// if (mCurrentCommandList.get(i).getCommandLayer() ==
	// PaintroidApplication.currentLayer
	// && mCurrentCommandList.get(i).isUndone()) {
	// return true;
	// }
	// }
	// return false;
	// }

	@Override
	public synchronized void redo() {
		if (mCommandCounter < mCurrentCommandList.size()) {
			mCommandIndex = mCommandCounter;
			mCommandCounter++;
			UndoRedoManager.getInstance().update(
					UndoRedoManager.StatusMode.ENABLE_UNDO);
			if (mCommandCounter == mCurrentCommandList.size()) {
				UndoRedoManager.getInstance().update(
						UndoRedoManager.StatusMode.DISABLE_REDO);
			}
		}
	}

	private synchronized void deleteFailedCommand(Command command) {
		int indexOfCommand = mCurrentCommandList.indexOf(command);
		((BaseCommand) mCurrentCommandList.remove(indexOfCommand))
				.freeResources();
		decrementCounter();
		mCommandIndex--;
		if (mCommandCounter == 1) {
			UndoRedoManager.getInstance().update(
					UndoRedoManager.StatusMode.DISABLE_UNDO);
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof BaseCommand.NOTIFY_STATES) {
			if (BaseCommand.NOTIFY_STATES.COMMAND_FAILED == data) {
				if (observable instanceof Command) {
					deleteFailedCommand((Command) observable);
				}
			}
		}
	}

	@Override
	public LinkedList<Command> getCommands() {
		return mCurrentCommandList;
	}

	@Override
	public void decrementCounter() {
		mCommandCounter--;
		if (mCommandCounter == 1) {
			UndoRedoManager.getInstance().update(
					UndoRedoManager.StatusMode.DISABLE_UNDO);
		}

	}

	@Override
	public void incrementCounter() {
		mCommandCounter++;
		if (mCommandCounter == getCommands().size()) {
			UndoRedoManager.getInstance().update(
					UndoRedoManager.StatusMode.DISABLE_REDO);
		}

	}

	private void showAllCommands() {
		for (int j = 0; j < PaintroidApplication.commandManager.getCommands()
				.size(); j++) {
			Log.i(PaintroidApplication.TAG,
					String.valueOf(j)
							+ " "
							+ PaintroidApplication.commandManager.getCommands()
									.get(j).toString()
							+ " "
							+ String.valueOf(PaintroidApplication.commandManager
									.getCommands().get(j).getCommandLayer()));
		}

	}

	@Override
	public void resetIndex() {
		mCommandIndex = 0;
	}

	@Override
	public int getNumberOfCommands() {
		return mCommandCounter;
	}

	@Override
	public CommandList getCommandListByIndex(int number) {
		return mAllCommandLists.get(number);
	}

	@Override
	public LinkedList<CommandList> getAllCommandList() {
		return mAllCommandLists;
	}

	@Override
	public Bitmap getmBitmapAbove() {
		return mBitmapAbove;
	}

	@Override
	public void setmBitmapAbove(Bitmap mBitmapAbove) {
		this.mBitmapAbove = mBitmapAbove;
	}

	@Override
	public Bitmap getmBitmapBelow() {
		return mBitmapBelow;
	}

	@Override
	public void setmBitmapBelow(Bitmap mBitmapBelow) {
		this.mBitmapBelow = mBitmapBelow;
	}

	@Override
	public void addEmptyCommandList(int index) {
		LinkedList<Command> com = new LinkedList<Command>();
		com.add(new BitmapCommand(mOriginalBitmap, false));
		mAllCommandLists.add(index, new CommandList(com));
		mAllCommandLists.get(index).setLastCommandCount(1);
		mAllCommandLists.get(index).setLastCommandIndex(1);
	}

	@Override
	public void removeCommandList(int index) {
		mAllCommandLists.remove(index);
	}

	@Override
	public void changeCurrentCommandList(int index) {
		mCurrentCommandList = mAllCommandLists.get(index).getCommands();
		mCommandCounter = mAllCommandLists.get(index).getLastCommandCount();
		mCommandIndex = mAllCommandLists.get(index).getLastCommandIndex();
	}

	@Override
	public void saveCurrentCommandListPointer() {
		mAllCommandLists.get(PaintroidApplication.currentLayer)
				.setLastCommandCount(mCommandCounter);
		mAllCommandLists.get(PaintroidApplication.currentLayer)
				.setLastCommandIndex(mCommandIndex);
	}
}
