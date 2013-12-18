package org.catrobat.paintroid.test.integration.layercommands;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;

import android.graphics.PointF;

public class LayerIntegrationTestClass extends BaseIntegrationTestClass {

	@Override
	protected void setUp() {
		super.setUp();
		pf = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PaintroidApplication.currentTool.changePaintStrokeWidth(500);
	}

	public PointF pf;

	public LayerIntegrationTestClass() throws Exception {
		super();
	}

	public int getNumOfCommandsOfLayer(int i) {
		try {
			return PaintroidApplication.commandManager.getAllCommandList().get(i).getCommands().size();
		} catch (Exception e) {
			return 0;
		}
	}

}
