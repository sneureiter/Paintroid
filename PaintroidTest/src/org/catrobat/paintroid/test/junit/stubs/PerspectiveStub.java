package org.catrobat.paintroid.test.junit.stubs;

import java.util.ArrayList;
import java.util.List;

import org.catrobat.paintroid.ui.Perspective;

import android.graphics.Point;
import android.graphics.PointF;

public class PerspectiveStub extends Perspective {

	private static final long serialVersionUID = 1L;
	protected BaseStub mBaseStub;

	public PerspectiveStub() {
		super(new SurfaceHolderStub());
		mBaseStub = new BaseStub();
	}

	public int getCallCount(String methodName) {
		return mBaseStub.getCallCount(methodName);
	}

	public List<Object> getCall(String methodName, int count) {
		return mBaseStub.getCall(methodName, count);
	}

	public void setReturnValue(String methodName, Object returnValue) {
		mBaseStub.setReturnValue(methodName, returnValue);
	}

	@Override
	public void convertFromScreenToCanvas(Point pointToConvert) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(pointToConvert);
		mBaseStub.addCall(throwable, arguments);
	}

	@Override
	public void convertFromScreenToCanvas(PointF pointToConvert) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(pointToConvert);
		mBaseStub.addCall(throwable, arguments);
	}

}
