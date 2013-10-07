package org.catrobat.paintroid.dialog.layerchooser;

public class LayerRow {

	public String name;
	public boolean visible;
	public boolean selected;

	public LayerRow(String name, boolean visible, boolean selected) {
		super();
		this.name = name;
		this.visible = visible;
		this.selected = selected;
	}

	public LayerRow() {
		super();
	}
}
