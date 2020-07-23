//OSX

// package com.nosvisuals.engine;

// import processing.core.*;
// import codeanticode.syphon.*;


// public class PreviewClient{
	// private PApplet papplet;
	// public SyphonClient previewClient;
	
	// public PreviewClient(PApplet papplet){
		// previewClient = new SyphonClient(papplet);
	// }

	// public processing.core.PImage getImage(processing.core.PImage preview){
		// return previewClient.getImage(preview,false);
	// }

	// public processing.core.PGraphics getImage(processing.core.PGraphics preview){
		// return previewClient.receiveTexture(preview);
		// return null;
	// }

// }


//WINDOWS
package com.nosvisuals.engine;

import processing.core.*;
import spout.*;


public class PreviewClient{
	private PApplet papplet;
	public Spout previewClient;
	
	public PreviewClient(PApplet papplet){
		previewClient = new Spout(papplet);
	}

	public processing.core.PImage getImage(processing.core.PImage preview){
	//	return previewClient.getImage(preview,false);	
		return null;
	}
	
	public processing.core.PGraphics getImage(processing.core.PGraphics preview){
		return previewClient.receiveTexture(preview);		
	}
}
