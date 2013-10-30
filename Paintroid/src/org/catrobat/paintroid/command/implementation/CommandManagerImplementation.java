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

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class CommandManagerImplementation implements CommandManager, Observer {
	private static final int MAX_COMMANDS = 512;

	private LinkedList<Command> mCurrentCommandList;
	private LinkedList<CommandList> mAllCommandLists;

	private int mCommandCounter;
	private int mCommandIndex;
	private Bitmap mOriginalBitmap;
	private LinkedList<CropCommand> mCropCommandList;
	private CropCommand mOriginalCropCommand;

	private Bitmap mBitmapAbove = null;
	private Bitmap mBitmapBelow = null;

	public CommandManagerImplementation() {
		mAllCommandLists = new LinkedList<CommandList>();
		mCurrentCommandList = new LinkedList<Command>();
		mCropCommandList = new LinkedList<CropCommand>();

		mOriginalCropCommand = new CropCommand(0, 0,
				PaintroidApplication.getScreenSize().x,
				PaintroidApplication.getScreenSize().y);

		mCropCommandList.add(mOriginalCropCommand);
		// The first command in the list is needed to clear the image when
		// rolling back commands.
		mCurrentCommandList.add(new ClearCommand());
		mAllCommandLists.add(0, new CommandList(mCurrentCommandList));

		mCommandCounter = 1;
		mCommandIndex = 1;
	}

	@Override
	public boolean hasCommands() {
		return mCommandCounter > 1 || mAllCommandLists.size() > 1;
	}

	@Override
	public void setOriginalBitmap(Bitmap bitmap) {
		mOriginalBitmap = bitmap.copy(Config.ARGB_8888, true);
		// If we use some custom bitmap, this first command is used to restore
		// it (instead of clear).
		mCurrentCommandList.removeFirst().freeResources();
		mCurrentCommandList.addFirst(new BitmapCommand(mOriginalBitmap, false));
		mCommandCounter++;
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
		mAllCommandLists = new LinkedList<CommandList>();

		mCurrentCommandList.clear();
		mCurrentCommandList.add(new ClearCommand());
		mAllCommandLists.add(0, new CommandList(mCurrentCommandList));

		mCommandCounter = 0;
		mCommandIndex = 0;

		UndoRedoManager.getInstance().update(StatusMode.DISABLE_REDO);
		UndoRedoManager.getInstance().update(StatusMode.DISABLE_UNDO);

		mCropCommandList.clear();
		mCropCommandList.add(mOriginalCropCommand);
	}

	@Override
	public synchronized Command getNextCommand() {

		if (mCommandIndex < mCommandCounter) {

			if ((PaintroidApplication.commandManager.getCommandListByIndex(
					PaintroidApplication.currentLayer).isHidden() && mCommandIndex != 0)) {
				mCommandIndex++;
				return getNextCommand();

			}
			return mCurrentCommandList.get(mCommandIndex++);

		} else {
			return null;
		}
	}

	@Override
	public synchronized boolean commitCommand(Command command) {
		PaintroidApplication.hasChanged = true;

		// Switch-Layer-Command & Hide-/Show-Layer-Command & Change-Layer-
		// Command shall not be saved and just run once
		if (command instanceof SwitchLayerCommand
				|| command instanceof ShowLayerCommand
				|| command instanceof HideLayerCommand
				|| command instanceof ChangeLayerCommand
				|| command instanceof DeleteLayerCommand) {

			command.run(null, null);

			if (mCommandCounter > 1) {
				UndoRedoManager.getInstance().update(
						UndoRedoManager.StatusMode.ENABLE_UNDO);
			} else {
				UndoRedoManager.getInstance().update(
						UndoRedoManager.StatusMode.DISABLE_UNDO);
			}

			if (mCommandCounter < mCurrentCommandList.size()) {
				UndoRedoManager.getInstance().update(
						UndoRedoManager.StatusMode.ENABLE_REDO);
			} else if (mCommandCounter == mCurrentCommandList.size()) {
				UndoRedoManager.getInstance().update(
						UndoRedoManager.StatusMode.DISABLE_REDO);
			}

			this.resetIndex();
			return mCurrentCommandList != null;
		}
		// First remove any previously undone commands from the top of the
		// queue.
		if (mCurrentCommandList.size() > mCommandCounter) {

			for (int i = mCurrentCommandList.size(); i > mCommandCounter; i--) {
				mCurrentCommandList.removeLast().freeResources();
			}
			UndoRedoManager.getInstance().update(
					UndoRedoManager.StatusMode.DISABLE_REDO);
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

		if (command instanceof CropCommand) {
			saveCurrentCommandListPointer();
			// this.resetIndex();

			mCropCommandList.add((CropCommand) command);
			cropAllOtherLayers(command);

			setmBitmapAbove(ChangeLayerCommand
					.generateImageOfAboveLayers(PaintroidApplication.currentLayer));
			setmBitmapBelow(ChangeLayerCommand
					.generateImageOfBelowLayers(PaintroidApplication.currentLayer));
			PaintroidApplication.isCropped = true;
		}

		mCurrentCommandList.add(command);

		if (mAllCommandLists.get(PaintroidApplication.currentLayer).isHidden()) {
			this.resetIndex();
		}

		return mCurrentCommandList.get(mCommandIndex) != null;
	}

	private void cropAllOtherLayers(Command command) {
		for (int i = 0; i < mAllCommandLists.size(); i++) {
			if (i != PaintroidApplication.currentLayer) {
				mAllCommandLists
						.get(i)
						.getCommands()
						.add(mAllCommandLists.get(i).getLastCommandCount(),
								command);
				mAllCommandLists.get(i).setLastCommandCount(
						mAllCommandLists.get(i).getLastCommandCount() + 1);
			}
		}

	}

	@Override
	public synchronized void undo() {
		if (mCommandCounter > 1) {
			mCommandCounter--;

			if (mCurrentCommandList.get(mCommandCounter) instanceof CropCommand) {
				mCropCommandList.removeLast();
			}
			resetIndex();

			UndoRedoManager.getInstance().update(
					UndoRedoManager.StatusMode.ENABLE_REDO);
			if (mCommandCounter <= 1) {
				UndoRedoManager.getInstance().update(
						UndoRedoManager.StatusMode.DISABLE_UNDO);
			}
			PaintroidApplication.hasChanged = true;
		}
	}

	@Override
	public synchronized void redo() {
		if (mCommandCounter < mCurrentCommandList.size()) {

			mCommandIndex = mCommandCounter;
			mCommandCounter++;

			if (mCurrentCommandList.get(mCommandCounter - 1) instanceof CropCommand) {
				mCropCommandList.add((CropCommand) mCurrentCommandList
						.get(mCommandCounter - 1));
			}

			UndoRedoManager.getInstance().update(
					UndoRedoManager.StatusMode.ENABLE_UNDO);
			if (mCommandCounter == mCurrentCommandList.size()) {
				UndoRedoManager.getInstance().update(
						UndoRedoManager.StatusMode.DISABLE_REDO);
			}
			PaintroidApplication.hasChanged = true;
			// PaintroidApplication.isCropped = true;
		}
	}

	private synchronized void deleteFailedCommand(Command command) {
		int indexOfCommand = mCurrentCommandList.indexOf(command);
		((BaseCommand) mCurrentCommandList.remove(indexOfCommand))
				.freeResources();
		mCommandCounter--;
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
		LinkedList<Command> tmpCommandList = new LinkedList<Command>();
		CommandList mCommandList = new CommandList(tmpCommandList);

		tmpCommandList.add(new BitmapCommand(mOriginalBitmap, false));

		if (!CropCommand.isOriginal()) {
			tmpCommandList.add(getLastCropCommand());
			mCommandList.setLastCommandCount(2);
		} else {
			mCommandList.setLastCommandCount(1);
		}
		mCommandList.setLastCommandIndex(1);

		mCommandList.setThumbnail(null);
		mAllCommandLists.add(index, mCommandList);

	}

	@Override
	public void removeCommandList(int index) {
		mAllCommandLists.remove(index);
	}

	@Override
	public void changeCurrentCommandList(int index) {
		CommandList mCommandList = mAllCommandLists.get(index);

		mCurrentCommandList = mCommandList.getCommands();
		mCommandCounter = mCommandList.getLastCommandCount();
		mCommandIndex = mCommandList.getLastCommandIndex();

	}

	@Override
	public void saveCurrentCommandListPointer() {
		CommandList mCommandList = mAllCommandLists
				.get(PaintroidApplication.currentLayer);

		mCommandList.setLastCommandCount(mCommandCounter);
		mCommandList.setLastCommandIndex(mCommandIndex + 1);
	}

	@Override
	public CropCommand getLastCropCommand() {
		return mCropCommandList.getLast();
	}

	@Override
	public CropCommand getOriginalCropCommand() {
		return mOriginalCropCommand;
	}
}
