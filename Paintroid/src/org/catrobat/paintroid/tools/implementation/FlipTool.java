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

package org.catrobat.paintroid.tools.implementation;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.FlipCommand;
import org.catrobat.paintroid.command.implementation.FlipCommand.FlipDirection;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.Statusbar.ToolButtonIDs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

public class FlipTool extends BaseToolWithRectangleShape {

	public FlipTool(Context context, ToolType toolType) {
		super(context, toolType);
		setRotationEnabled(false);
		setRespectImageBounds(true);

		mBoxHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
		mBoxWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
		mToolPosition.x = mBoxWidth / 2f;
		mToolPosition.y = mBoxHeight / 2f;

		// TODO: How to reset after loading new Image?
	}

	@Override
	public void resetInternalState() {
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs toolButtonID) {
		switch (toolButtonID) {
		case BUTTON_ID_PARAMETER_BOTTOM_1:
			return R.drawable.icon_menu_flip_horizontal;
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			return R.drawable.icon_menu_flip_vertical;
		default:
			return super.getAttributeButtonResource(toolButtonID);
		}
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs toolButtonID) {
		FlipDirection flipDirection = null;
		switch (toolButtonID) {
		case BUTTON_ID_PARAMETER_BOTTOM_1:
			flipDirection = FlipDirection.FLIP_HORIZONTAL;
			break;
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			flipDirection = FlipDirection.FLIP_VERTICAL;
			break;
		default:
			return;
		}

		int left = (int) (mToolPosition.x - (mBoxWidth / 2));
		int top = (int) (mToolPosition.y - (mBoxHeight / 2));
		int right = (int) (mToolPosition.x + (mBoxWidth / 2));
		int bottom = (int) (mToolPosition.y + (mBoxHeight / 2));

		Rect rectangleToFlip = new Rect(left, top, right, bottom);

		Command command = new FlipCommand(flipDirection, rectangleToFlip);
		mProgressDialog.show();
		((FlipCommand) command).addObserver(this);
		PaintroidApplication.commandManager.commitCommand(command);
	}

	@Override
	public int getAttributeButtonColor(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP:
			return Color.TRANSPARENT;
		default:
			return super.getAttributeButtonColor(buttonNumber);
		}
	}

	@Override
	protected void onClickInBox() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void drawToolSpecifics(Canvas canvas) {
		// TODO Auto-generated method stub

	}

}
