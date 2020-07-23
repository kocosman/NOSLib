package com.nosvisuals.engine;

import processing.core.PApplet;

public class Temp {
	private PApplet papplet;

	public Temp(PApplet papplet) {
		this.papplet = papplet;
		papplet.println("Temp loaded");
	}

	public void test(){
		papplet.println("test");
	}

	public void printSketchInfo() {
		if (papplet != null) {
			papplet.println(
				"Sketch, size " + papplet.width + "x" + papplet.height + ", " +
				"is using renderer " + papplet.sketchRenderer()
			);
		}
	}
}
