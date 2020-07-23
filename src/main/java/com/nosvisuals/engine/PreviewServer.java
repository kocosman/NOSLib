//OSX
// package com.nosvisuals.engine;

// import processing.core.*;
// import codeanticode.syphon.*;


// public class PreviewServer {
	// private PApplet papplet;
	// public SyphonServer previewServer;

	// public PreviewServer(PApplet papplet) {
		// previewServer = new SyphonServer(papplet, "NOS Syphon");
	// }

	// public void send(){
		// previewServer.sendScreen();
	// }
// }

//WINDOWS
package com.nosvisuals.engine;

import processing.core.*;
import spout.*;


public class PreviewServer {
	private PApplet papplet;
	public Spout previewServer;

	public PreviewServer(PApplet papplet) {
		previewServer = new Spout(papplet);
		previewServer.createSender("NOS Spout");
	}

	public void send(){
		previewServer.sendTexture();
	}
}
