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
import org.catrobat.paintroid.command.implementation.PathCommand;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.AsyncTask;

public class DrawTool extends BaseTool {
	// TODO put in PaintroidApplication and scale dynamically depending on
	// screen resolution.
	public static final int STROKE_1 = 1;
	public static final int STROKE_5 = 5;
	public static final int STROKE_15 = 15;
	public static final int STROKE_25 = 25;
	public static final int SCROLL_TOLERANCE = 50;

	protected final Path mPathToDraw;
	protected PointF mInitialEventCoordinate;
	protected final PointF movedDistance;
	protected MoveOnZoomAsyncTask mMoveAsync;

	public DrawTool(Context context, ToolType toolType) {
		super(context, toolType);
		mPathToDraw = new Path();
		mPathToDraw.incReserve(1);
		movedDistance = new PointF(0f, 0f);
		mMoveAsync = new MoveOnZoomAsyncTask();
	}

	@Override
	public void draw(Canvas canvas) {
		changePaintColor(mCanvasPaint.getColor());
		if (mCanvasPaint.getColor() == Color.TRANSPARENT) {
			mCanvasPaint.setColor(Color.BLACK);
			canvas.drawPath(mPathToDraw, mCanvasPaint);
			mCanvasPaint.setColor(Color.TRANSPARENT);
		} else {
			canvas.drawPath(mPathToDraw, mBitmapPaint);
		}
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		mInitialEventCoordinate = new PointF(coordinate.x, coordinate.y);
		mPreviousEventCoordinate = new PointF(coordinate.x, coordinate.y);
		mPathToDraw.moveTo(coordinate.x, coordinate.y);
		movedDistance.set(0, 0);
		return true;
	}

	protected void executeMoveOnZoom(PointF coordinateDelta) {

		if (mMoveAsync.getStatus() == AsyncTask.Status.PENDING) {
			mMoveAsync.execute(coordinateDelta);
		} else if (mMoveAsync.getStatus() == AsyncTask.Status.FINISHED) {
			mMoveAsync = new MoveOnZoomAsyncTask();
			mMoveAsync.execute(coordinateDelta);
		}
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		if (mInitialEventCoordinate == null || mPreviousEventCoordinate == null
				|| coordinate == null) {
			return false;
		}

		PointF calcPoint = PaintroidApplication.perspective
				.calculateFromCanvasToScreen(new PointF(coordinate.x,
						coordinate.y));

		if (calcPoint.x > (PaintroidApplication.drawingSurface.getWidth() - SCROLL_TOLERANCE)) {
			executeMoveOnZoom(new PointF(-1, 0));

		} else if (calcPoint.x < SCROLL_TOLERANCE) {
			executeMoveOnZoom(new PointF(1, 0));

		} else if (calcPoint.y > (PaintroidApplication.drawingSurface
				.getHeight() - SCROLL_TOLERANCE)) {
			executeMoveOnZoom(new PointF(0, -1));

		} else if (calcPoint.y < SCROLL_TOLERANCE) {
			executeMoveOnZoom(new PointF(0, 1));

		} else if (mMoveAsync.getStatus() == AsyncTask.Status.RUNNING) {
			mMoveAsync.cancel(true);
		}
		addToPath(coordinate);

		return true;
	}

	protected void addToPath(PointF coordinate) {

		final float cx = (mPreviousEventCoordinate.x + coordinate.x) / 2;
		final float cy = (mPreviousEventCoordinate.y + coordinate.y) / 2;
		mPathToDraw.quadTo(mPreviousEventCoordinate.x,
				mPreviousEventCoordinate.y, cx, cy);
		mPathToDraw.incReserve(1);
		movedDistance.set(
				movedDistance.x
						+ Math.abs(coordinate.x - mPreviousEventCoordinate.x),
				movedDistance.y
						+ Math.abs(coordinate.y - mPreviousEventCoordinate.y));
		mPreviousEventCoordinate.set(coordinate.x, coordinate.y);
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		mMoveAsync.cancel(true);
		if (mInitialEventCoordinate == null || mPreviousEventCoordinate == null
				|| coordinate == null) {
			return false;
		}
		movedDistance.set(
				movedDistance.x
						+ Math.abs(coordinate.x - mPreviousEventCoordinate.x),
				movedDistance.y
						+ Math.abs(coordinate.y - mPreviousEventCoordinate.y));
		boolean returnValue;
		if (MOVE_TOLERANCE < movedDistance.x
				|| MOVE_TOLERANCE < movedDistance.y) {
			returnValue = addPathCommand(coordinate);
		} else {
			returnValue = addPointCommand(mInitialEventCoordinate);
		}
		return returnValue;
	}

	protected boolean addPathCommand(PointF coordinate) {
		mPathToDraw.lineTo(coordinate.x, coordinate.y);
		Command command = new PathCommand(mBitmapPaint, mPathToDraw);
		PaintroidApplication.commandManager.commitCommand(command);
		return true;
	}

	protected boolean addPointCommand(PointF coordinate) {
		Command command = new PointCommand(mBitmapPaint, coordinate);
		PaintroidApplication.commandManager.commitCommand(command);
		return true;
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP:
			return getStrokeColorResource();
		case BUTTON_ID_PARAMETER_BOTTOM_1:
			return R.drawable.icon_menu_strokes;
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			return R.drawable.icon_menu_color_palette;
		default:
			return super.getAttributeButtonResource(buttonNumber);
		}
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_BOTTOM_1:
			showBrushPicker();
			break;
		case BUTTON_ID_PARAMETER_BOTTOM_2:
		case BUTTON_ID_PARAMETER_TOP:
			showColorPicker();
			break;
		default:
			break;
		}
	}

	@Override
	public void resetInternalState() {
		mPathToDraw.rewind();
		mInitialEventCoordinate = null;
		mPreviousEventCoordinate = null;
	}

	protected class MoveOnZoomAsyncTask extends
			AsyncTask<PointF, Integer, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(PointF... coordinateDeltas) {
			if (coordinateDeltas.length > 0) {
				while (!isCancelled()) {
					PaintroidApplication.perspective.translate(
							coordinateDeltas[0].x, coordinateDeltas[0].y);
					PointF coordinate = new PointF(mPreviousEventCoordinate.x
							- coordinateDeltas[0].x
							/ PaintroidApplication.perspective.getScale(),
							mPreviousEventCoordinate.y
									- coordinateDeltas[0].y
									/ PaintroidApplication.perspective
											.getScale());
					addToPath(coordinate);

					try {
						Thread.sleep(3);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void nothing) {
		}

	}
}
