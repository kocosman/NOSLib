package com.nosvisuals.engine;

import processing.core.*;
import processing.data.*;
import processing.opengl.*;

import controlP5.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;

public class ControlFrame extends PApplet {

	public int w, h;
	public String surfaceTitle = "NOS GUI";
	public PApplet papplet;
	public ControlP5 cp5;
	public String osName;
	
	//STYLE
	public int mainYellow       ;
	public int mainBlue         ;
	public int mainBackground   ;
	public int panelBackground  ;
	public int label            ;
	public int dimYellow        ;
	public int thinLines        ;
	public int inactive         ;
	public int textColor        ;
	public int valueLabel       ;

	public PFont pfontLight, pfontBold, pfontRegular;
	public ControlFont fontLight, fontBold, fontRegular;

	//MARGINS
	public float borderMarginBig = 68;
	public float borderMarginSmall = 44;
	public float borderLinesThickness = 8;

	//PANELS
	public Rectangle thumbnailPanel = new Rectangle(76, 8, 1528, 292);
	public Rectangle visualSpecificParametersPanel = new Rectangle(76, 630, 1528, 342);
	public Rectangle presetsPanel = new Rectangle(942, 347, 120, 236);

	public Rectangle soundParametersPanel = new Rectangle(1092, 347+118, 512, 118);
	public Rectangle soundWavePanel = new Rectangle(1092, 347, 512, 118);

	public Rectangle previewPanel = new Rectangle(300,30,16*25,9*25);
	
	public Rectangle cameraPanel = new Rectangle(76, 347, 215, 236);
	public Rectangle colorPanel = new Rectangle(321, 347, 350, 236);
	public Rectangle connectorsPanel = new Rectangle(701, 347, 211, 236);

	//THUMBNAILS
	public int thumbnailImageWidth = 185;
	public int thumbnailImageHeight = 104;
	public float thumbnailImageSpacing = 47;

	public int thumbnailOffsetX = 740;
	public int thumbnailOffsetY = 30;

	public int thumbnailAmount = 8;
	public int thumbnailXAmount = 4;
	public int thumbnailYAmount = 2;

	public ArrayList<String> thumbnailNames;
	public ArrayList<PImage> thumbnailImages;

	public int selectedThumbnailIndex = -1;

	public boolean pageUp = false;
	public boolean pageDown = false;

	public int thumbnailPageIndex = 0;
	public boolean thumbnailClicked = false;

	public boolean saveNewThumbnail = false;

	//IMAGES
	public PImage logo, vsBg, about, noThumbnail;
	public PImage previewImage;
	public PGraphics previewGraphics;

	// SOUND
	public Rectangle soundFocusGUI;
	public float[] soundSpectrum;
	public float[] soundSpectrumLPF;

	public float guiSoundGain;
	public float guiSoundDecay;

	// COLOR
	public float strokeSaturation;
	public float strokeAlpha;
	public float fillSaturation;
	public float fillAlpha;
	public PImage colorPaletteImageFull;
	public String lastColorPaletteAddress = "";
	public boolean newColorPalette = false;

	// CONSOLE
	public Textarea myTextarea;
	public Println console;
	public boolean printDebug = true;

	public boolean guiReady = false;
	public boolean guiDone = false;
	public boolean guiLoaded = false;

	// VISUAL SPECIFIC PARAMETERS
	public PVector knobSize = new PVector(25, 25);
	public PVector faderSize = new PVector(24, 162);
	public PVector buttonSize = new PVector(24, 24);

	public PVector[] buttonPos = {
		new PVector(355, 130), 
		new PVector(355, 199), 
		new PVector(355, 268), 
		new PVector(506, 130), 
		new PVector(506, 199), 
		new PVector(506, 268), 
		new PVector(657, 130), 
		new PVector(657, 199), 
		new PVector(657, 268), 
		new PVector(808, 130), 
		new PVector(808, 199), 
		new PVector(808, 268), 
		new PVector(959, 130), 
		new PVector(959, 199), 
		new PVector(959, 268), 
		new PVector(1110, 130), 
		new PVector(1110, 199), 
		new PVector(1110, 268), 
		new PVector(1261, 130), 
		new PVector(1261, 199), 
		new PVector(1261, 268), 
		new PVector(1412, 130), 
		new PVector(1412, 199), 
		new PVector(1412, 268),
	};

	public PVector[] faderPos = {
		new PVector(412, 130), 
		new PVector(563, 130), 
		new PVector(714, 130), 
		new PVector(865, 130), 
		new PVector(1016, 130), 
		new PVector(1167, 130), 
		new PVector(1318, 130), 
		new PVector(1469, 130)
	};

	public PVector[] knobPos = {
		new PVector(395, 40), 
		new PVector(546, 40), 
		new PVector(697, 40), 
		new PVector(848, 40), 
		new PVector(999, 40), 
		new PVector(1150, 40), 
		new PVector(1301, 40), 
		new PVector(1452, 40)
	};

	// PRESETS
	public RadioButton presetsRadio;

	// PAPPLET
	public boolean mouseReleased = false;

	// OVERLAY
	public float trailAlpha;
	public float blackAlpha;

	// CAMERA
	public float rotX;
	public float rotY;
	public float rotZ;
	
	public boolean stopRotX;
	public boolean stopRotY;
	public boolean stopRotZ;

	public boolean rollPlus;
	public boolean rollMinus;

	public boolean pitchPlus;
	public boolean pitchMinus;

	public boolean yawPlus;
	public boolean yawMinus;

	public boolean resetRot;
	public boolean randomRot;

	// OSC
	public boolean oscConnected = false;
	public boolean oscAttempt = false;
	public boolean oscFlush = false;
	public String myIpAddress;
	public String remoteIpAddress;
	public int incomingPort;
	public int outgoingPort;
	public boolean oscConfigUpdate = false;

	// MIDI
	public boolean midiConnected = false;
	public String[] midiDeviceNames;
	public boolean midiListDevices = true;
	public int midiDeviceIndex = -1;
	public String midiDeviceName = "";
	public boolean midiAttempt = false;
	public boolean midiLearn = false;
	public String learnType = "";
	public int learnAddress = -1;

	// CONFIG
	public boolean exportAndroid = false;
	public boolean saveConfig = false;
	public boolean loadConfig = false;

	// COLOR
	public Rectangle colorFocusGUI;
	public Rectangle colorPreviewGUI;
	public int[] colorPaletteFocus = new int[0];
	public boolean colorFocusChanged = true;

	// PREVIEW
	public PreviewClient previewClient;
	public boolean previewEnable = true;


	public int canvasFPS = 0;
	public String path = "";
	public String selfPath = "";
	//---------------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------
	public ControlFrame(PApplet _papplet) {
		super();   
		papplet = _papplet;
		w=1680;
		h=1050;
		PApplet.runSketch(new String[]{this.getClass().getName()}, this);
	}

	public void settings() {
		size(w, h, P3D);
		osName = System.getProperty("os.name").toLowerCase();
		if(osName.indexOf("mac")!=-1){
			PJOGL.profile = 1;
		}
	}

	public void setup() {
		cp5 = new ControlP5(this);
		cp5.setAutoDraw(false);

		selfPath = ControlFrame.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		selfPath = selfPath.substring(0,selfPath.lastIndexOf('/')+1);
		
		colorMode(HSB);
		mainYellow       = color(27, 228, 251);
		mainBlue         = color(137, 252, 100);
		mainBackground   = color(0, 0, 52);
		panelBackground  = color(156, 16, 48);
		label            = color(0, 0, 202);
		dimYellow        = color(27, 255, 148);
		thinLines        = color(0, 0, 102);
		inactive         = color(0, 0, 61);
		textColor        = color(255, 0, 176);
		valueLabel 		 = label;

		colorMode(RGB);

		pfontLight		= createFont("PFDinTextCompPro-Light", 200, false);
		pfontBold		= createFont("PFDinTextCompPro-Bold", 200, false);
		pfontRegular	= createFont("PFDinTextCompPro-Regular", 200, false);
		
		fontLight		= new ControlFont(pfontLight, 18);
		fontBold		= new ControlFont(pfontBold, 18);
		fontRegular		= new ControlFont(pfontRegular, 18);

		fontLight.sharp();
		fontBold.sharp();
		fontRegular.sharp();

		cp5.setColorForeground(dimYellow);
		cp5.setColorActive(mainYellow);
		cp5.setColorBackground(mainBlue);
		cp5.setColorCaptionLabel(textColor);
		cp5.setColorValueLabel(valueLabel);

		logo					= loadImage(selfPath + "images/logo.png");
		vsBg 					= loadImage(selfPath + "images/vsGUIbg.png");
		about 					= loadImage(selfPath + "images/about.png");
		noThumbnail 			= loadImage(selfPath + "images/noThumbnail.jpg");
		colorPaletteImageFull 	= loadImage(selfPath + "images/defaultPalette.png");
		// colorPaletteImageFull 	= loadImage(selfPath + "images/c1.png");
		lastColorPaletteAddress = selfPath + "images/defaultPalette.png";


		previewImage = noThumbnail;
		previewGraphics = createGraphics(previewPanel.width, previewPanel.height, PConstants.P2D);

		myTextarea = cp5.addTextarea("txt")
		.setPosition(100, 650)
		.setSize(275, 300)
		.setFont(createFont("", 10))
		.setLineHeight(14)
		.setColor(color(200))
		.setColorBackground(color(52))
		.setColorForeground(color(255, 100));

		console = cp5.addConsole(myTextarea);
		consolePrint("Setup");

		initializeSoundGUI();
		consolePrint("SoundGUI initialized");

		thumbnailNames = new ArrayList<String>();
		thumbnailImages = new ArrayList<PImage>();

		initializeThumbnailGUI();
		consolePrint("ThumbnailGUI initialized");
		initializePresetGUI();
		consolePrint("PresetGUI initialized");
		initializeForegroundGUI();
		consolePrint("ForegroundGUI initialized");
		initializeCameraGUI();
		consolePrint("CameraGUI initialized");
		initializeColorGUI();
		consolePrint("ColorGUI initialized");
		initializeConnectors();
		consolePrint("Connectors initialized");
		initializeConfig();
		consolePrint("Config initialized");

		soundFocusGUI = new Rectangle(soundWavePanel.x, soundWavePanel.y+1, soundWavePanel.width, soundWavePanel.height-1);
		colorPreviewGUI = new Rectangle(colorPanel.x+50, colorPanel.y+10,290,50);
		colorFocusGUI = new Rectangle(colorPreviewGUI.x+5,colorPreviewGUI.y,colorPreviewGUI.width-5,colorPreviewGUI.height);

		previewClient = new PreviewClient(this);
		consolePrint("GUI Setup Finished");

		consolePrint("PATH: " + path);
		consolePrint("Size: " + width + " - " + height);

	}

	public void mouseWheel(processing.event.MouseEvent event) {
		float scrollSpeed = 1;

		if(osName.indexOf("windows")!=-1){
			scrollSpeed = 10;
		} else if(osName.indexOf("mac")!=-1){
			scrollSpeed = 2;
		}	

		if(soundWavePanel.contains(mouseX,mouseY)){
			soundFocusGUI.x -= event.getCount()*scrollSpeed;
			soundFocusGUI.width += event.getCount()*2*scrollSpeed;
			
			soundFocusGUI.width = constrain(soundFocusGUI.width,4,soundWavePanel.width-4);
			soundFocusGUI.x = constrain(soundFocusGUI.x,soundWavePanel.x,soundWavePanel.x+soundWavePanel.width-soundFocusGUI.width);
		}

		if(colorFocusGUI.contains(mouseX,mouseY)){
			colorFocusGUI.x -= event.getCount()*scrollSpeed;
			colorFocusGUI.width += event.getCount()*2*scrollSpeed;
			
			colorFocusGUI.width = constrain(colorFocusGUI.width,4,colorPreviewGUI.width);
			colorFocusGUI.x = constrain(colorFocusGUI.x,colorPreviewGUI.x,colorPreviewGUI.x+colorPreviewGUI.width-colorFocusGUI.width);
			colorFocusChanged = true;
		}
	}

	public void draw() {
		surface.setTitle(surfaceTitle + " - " + (int)frameRate + "fps" + " CanvasFPS: " + canvasFPS);
		background(mainBackground);

		noStroke();
		fill(mainYellow);
		rect(borderLinesThickness+borderMarginBig, 0, thumbnailPanel.width, borderLinesThickness);
		rect(borderLinesThickness+borderMarginBig, h-borderLinesThickness, thumbnailPanel.width, borderLinesThickness);

		fill(mainBlue);
		rect(0, 0, borderLinesThickness, h-borderMarginSmall-borderLinesThickness);
		rect(w-borderLinesThickness, 0, w, h-borderMarginSmall-borderLinesThickness);

		drawPanel("", soundParametersPanel);
		drawPanel("PRESETS", presetsPanel);
		drawPanel("SOUND WAVE", soundWavePanel);
		drawPanel("VISUAL SPECIFIC PARAMETERS", visualSpecificParametersPanel);
		drawPanel("", thumbnailPanel);
		drawPanel("CAMERA", cameraPanel);
		drawPanel("COLOR", colorPanel);
		drawPanel("CONNECTORS", connectorsPanel);

		image(logo, borderLinesThickness+borderMarginBig, borderLinesThickness+28);
		image(vsBg, visualSpecificParametersPanel.x, visualSpecificParametersPanel.y+1, visualSpecificParametersPanel.width, visualSpecificParametersPanel.height-1);

		drawSoundWavesPanel();

		guiReady = true;

		if(guiReady && guiDone && !guiLoaded){
			consolePrint("GUI Frame load thumbnails");
			loadThumbnails();
			guiLoaded = true;
		}

		drawThumbnails();
		drawColorPanel();
		drawPreview();
		drawConnectors();
		
		if(midiLearn){
			drawMidiLearnBoxes();
		}

		if(guiDone){
			cp5.draw();
		}

	}

	public void drawMidiLearnBoxes(){
		for(int i = 0; i < buttonPos.length; i++){
			Rectangle buttonRectTemp = new Rectangle((int)(buttonPos[i].x+visualSpecificParametersPanel.x-5),(int)(buttonPos[i].y+visualSpecificParametersPanel.y-5),(int)(buttonSize.x+10),(int)(buttonSize.y+10));
			stroke(255);
			strokeWeight(2);
			noStroke();

			if(buttonRectTemp.contains((int)mouseX,(int)mouseY)){
				fill(255,127);
				if(mousePressed){
					println("Learning MIDI Address for button " + i);
					learnType = "button";
					learnAddress = i;
				}
			} else {
				fill(255,50);
			}
			
			rect(buttonRectTemp.x,buttonRectTemp.y,buttonRectTemp.width,buttonRectTemp.height);	
		}

		for(int i = 0; i < knobPos.length; i++){
			Rectangle knobRectTemp = new Rectangle((int)(knobPos[i].x+visualSpecificParametersPanel.x-knobSize.x-5),(int)(knobPos[i].y+visualSpecificParametersPanel.y-knobSize.y-5),(int)(knobSize.x*2+10),(int)(knobSize.y*2+10));
			stroke(255);
			strokeWeight(2);
			noStroke();

			if(knobRectTemp.contains((int)mouseX,(int)mouseY)){
				fill(255,127);
				if(mousePressed){
					println("Learning MIDI Address for knob " + i);
					learnType = "knob";
					learnAddress = i;
				}
			} else {
				fill(255,50);
			}
			
			rect(knobRectTemp.x,knobRectTemp.y,knobRectTemp.width,knobRectTemp.height);
		}

		for(int i = 0; i < faderPos.length; i++){
			Rectangle faderRectTemp = new Rectangle((int)(faderPos[i].x+visualSpecificParametersPanel.x-5),(int)(faderPos[i].y+visualSpecificParametersPanel.y-5),(int)(faderSize.x+10),(int)(faderSize.y+10));
			stroke(255);
			strokeWeight(2);
			noStroke();

			if(faderRectTemp.contains((int)mouseX,(int)mouseY)){
				fill(255,127);
				if(mousePressed){
					println("Learning MIDI Address for fader " + i);
					learnType = "fader";
					learnAddress = i;
				}
			} else {
				fill(255,50);
			}
			
			rect(faderRectTemp.x,faderRectTemp.y,faderRectTemp.width,faderRectTemp.height);	
		}

	}

	public void initializeConnectors(){
		cp5.addButton("connectOsc")
		.setPosition(connectorsPanel.x+10, connectorsPanel.y+10)
		.setSize(50,25)
		.setCaptionLabel("Osc");
		cp5.getController("connectOsc").getCaptionLabel().setFont(fontLight).setSize(16).toUpperCase(false);

		cp5.addButton("syncOsc")
		.setPosition(connectorsPanel.x+160, connectorsPanel.y+10)
		.setSize(40,25)
		.setCaptionLabel("Sync");
		cp5.getController("syncOsc").getCaptionLabel().setFont(fontLight).setSize(16).toUpperCase(false);

		cp5.addTextfield("incomingPortText")
		.setPosition(connectorsPanel.x+167, connectorsPanel.y+45)
		.setSize(30,20)
		.setFont(fontLight)
		.setFocus(true)
		.setAutoClear(false)
		.setCaptionLabel("")
		;
		cp5.get(Textfield.class, "incomingPortText").hide();

		cp5.addTextfield("outgoingPortText")
		.setPosition(connectorsPanel.x+167, connectorsPanel.y+67)
		.setSize(30,20)
		.setFont(fontLight)
		.setFocus(true)
		.setAutoClear(false)
		.setCaptionLabel("")
		;
		cp5.get(Textfield.class, "outgoingPortText").hide();

		cp5.addTextfield("remoteIpText")
		.setPosition(connectorsPanel.x+67, connectorsPanel.y+67)
		.setSize(65,20)
		.setFont(fontLight)
		.setFocus(true)
		.setAutoClear(false)
		.setCaptionLabel("")
		;
		cp5.get(Textfield.class, "remoteIpText").hide();

		cp5.addScrollableList("midiDevices")
		.setPosition(connectorsPanel.x+10, connectorsPanel.y+130)
		.setSize(100, 80)
		.setBarHeight(25)
		.setItemHeight(25)
		.setCaptionLabel("Midi Devices")
		//.addItems(l)
		;
		cp5.get(ScrollableList.class, "midiDevices").setType(ControlP5.DROPDOWN);
		cp5.get(ScrollableList.class, "midiDevices").getCaptionLabel().toUpperCase(false).setFont(fontLight).setSize(16).align(CENTER, BASELINE);
		cp5.get(ScrollableList.class, "midiDevices").close();

		cp5.addToggle("midiLearn")
		.setPosition(connectorsPanel.x+160, connectorsPanel.y+130)
		.setSize(40,25)
		.setCaptionLabel("Learn");

		cp5.getController("midiLearn").getCaptionLabel().setFont(fontLight).setSize(16).toUpperCase(false).align(CENTER,CENTER);

	}


	public void midiDevices(int n) {
		midiDeviceIndex = n;
		midiDeviceName = cp5.get(ScrollableList.class, "midiDevices").getItem(n).get("name").toString();
		println("Connecting to MIDI Device: " + midiDeviceIndex + " - " + midiDeviceName);
		midiAttempt = true;
	}


	public void incomingPortText(String theText) {
		println("Incoming Port: " + theText);
		cp5.get(Textfield.class, "incomingPortText").hide();
		if(!theText.equals("")){
			incomingPort = Integer.valueOf(theText);	
			oscConfigUpdate = true;
		}
	}

	public void outgoingPortText(String theText) {
		println("Outgoing Port: " + theText);
		cp5.get(Textfield.class, "outgoingPortText").hide();
		if(!theText.equals("")){
			outgoingPort = Integer.valueOf(theText);	
			oscConfigUpdate = true;
		}
	}

	public void remoteIpText(String theText) {
		println("Remote IP: " + theText);
		cp5.get(Textfield.class, "remoteIpText").hide();
		if(!theText.equals("")){
			remoteIpAddress = theText;	
			oscConfigUpdate = true;
		}
	}

	public void connectOsc(){
		println("Attempting to connect OSC");
		oscAttempt = true;
	}

	public void syncOsc(){
		println("Flushing all data to OSC");
		oscFlush = true;
	}

	public void drawConnectors(){
		strokeWeight(2);
		stroke(thinLines);
		line(connectorsPanel.x,connectorsPanel.y+connectorsPanel.height/2,connectorsPanel.x+connectorsPanel.width,connectorsPanel.y+connectorsPanel.height/2);
		strokeWeight(25);

		// osc indicator
		if(oscConnected){
			stroke(mainYellow);	
		} else {
			stroke(mainBlue);	
		}
		point(connectorsPanel.x+85, connectorsPanel.y+22);

		fill(textColor);
		textSize(11);
		text("Incoming: " + myIpAddress,connectorsPanel.x+10, connectorsPanel.y+60);
		text(" : " + incomingPort,connectorsPanel.x+157, connectorsPanel.y+60);

		text("Outgoing: " + remoteIpAddress,connectorsPanel.x+10, connectorsPanel.y+82);
		text(" : " + outgoingPort,connectorsPanel.x+157, connectorsPanel.y+82);


		if(midiConnected){
			stroke(mainYellow);	
		} else {
			stroke(mainBlue);	
		}
		point(connectorsPanel.x+135, connectorsPanel.y+142);


		strokeWeight(2);
	}



	public void drawPreview(){		
		stroke(mainYellow);
		strokeWeight(2);
		fill(0);
		rect(previewPanel.x, previewPanel.y, previewPanel.width, previewPanel.height);  //400-225
		if (guiDone) {
			if(previewEnable){
				try{
					if(osName.indexOf("windows")!=-1){
						previewGraphics = previewClient.getImage(previewGraphics); 
						image(previewGraphics, previewPanel.x, previewPanel.y, previewPanel.width, previewPanel.height);
					} else if(osName.indexOf("mac")!=-1){
						previewImage = previewClient.getImage(previewImage); 
						image(previewImage, previewPanel.x, previewPanel.y, previewPanel.width, previewPanel.height);	
					}	
				}catch(Exception e ){
					println("Gui frame Draw Preview problem");
				}	
			} else {
				image(noThumbnail, previewPanel.x, previewPanel.y, previewPanel.width, previewPanel.height);	

			}

		}
	}
	

	public void initializeColorGUI(){
		cp5.addSlider("strokeSaturation")
		.plugTo(strokeSaturation)
		.setPosition(colorPanel.x+10,colorPanel.y+75)   
		.setSize(300, 25)
		.setCaptionLabel("Str. S")
		.setRange(0,255);

		cp5.addSlider("strokeAlpha")
		.plugTo(strokeAlpha)
		.setPosition(colorPanel.x+10,colorPanel.y+75+40)   
		.setSize(300, 25)
		.setCaptionLabel("Str. A")
		.setRange(0,255);

		cp5.addSlider("fillSaturation")
		.plugTo(fillSaturation)
		.setPosition(colorPanel.x+10,colorPanel.y+75+80)   
		.setSize(300, 25)
		.setCaptionLabel("Fill S")
		.setRange(0,255);

		cp5.addSlider("fillAlpha")
		.plugTo(fillAlpha)
		.setPosition(colorPanel.x+10,colorPanel.y+75+120)   
		.setSize(300, 25)
		.setCaptionLabel("Fill A")
		.setRange(0,255);

		cp5.addButton("loadColorPalette")
		.setPosition(colorPanel.x+10,colorPanel.y+10)   
		.setSize(30,50)
		.setCaptionLabel("Load");

		cp5.getController("strokeSaturation").getCaptionLabel().setFont(fontLight).setSize(16).toUpperCase(false);
		cp5.getController("strokeAlpha").getCaptionLabel().setFont(fontLight).setSize(16).toUpperCase(false);
		cp5.getController("fillSaturation").getCaptionLabel().setFont(fontLight).setSize(16).toUpperCase(false);
		cp5.getController("fillAlpha").getCaptionLabel().setFont(fontLight).setSize(16).toUpperCase(false);
		cp5.getController("loadColorPalette").getCaptionLabel().setFont(fontLight).setSize(16).toUpperCase(false).align(CENTER, CENTER);

	}

	public void drawColorPanel(){
		try{
			if(colorPaletteImageFull != null){
				image(colorPaletteImageFull,colorPanel.x+50, colorPanel.y+10, colorPreviewGUI.width, colorPreviewGUI.height);
			} else {
	  			reinitializeColorPalette();
			}
			drawColorFocus();

		} catch(Exception e){
			consolePrint("Color ThumbnailImage Problem");
		}

	}

	public void adjustColorFocusPosition(){
				colorFocusGUI.x += mouseX-pmouseX;			
				colorFocusGUI.width = constrain(colorFocusGUI.width,4,colorPreviewGUI.width);
				colorFocusGUI.x = constrain(colorFocusGUI.x,colorPreviewGUI.x,colorPreviewGUI.x+colorPreviewGUI.width-colorFocusGUI.width);
				colorFocusChanged = true;
	}

	public void drawColorFocus(){
		if(colorFocusGUI.contains(mouseX,mouseY)){
			fill(0,50);
			if(mousePressed){
				adjustColorFocusPosition();
			}
		} else {
			fill(0,150);
		}
		noStroke();

		rect(colorPreviewGUI.x, colorPreviewGUI.y, colorFocusGUI.x-colorPreviewGUI.x, colorFocusGUI.height);
		rect(colorFocusGUI.x+colorFocusGUI.width, colorFocusGUI.y, colorPreviewGUI.width-(colorFocusGUI.x-colorPreviewGUI.x)-colorFocusGUI.width, colorFocusGUI.height);

		stroke(valueLabel);
		noFill();
		rect(colorFocusGUI.x,colorFocusGUI.y,colorFocusGUI.width,colorFocusGUI.height);

		float paletteImageRatio = (float)colorPaletteImageFull.width/(float)colorPreviewGUI.width;

		if(colorFocusChanged){
			colorPaletteFocus = new int[colorFocusGUI.width];
			//colorFocusChanged = false;
		}
		for(int i = 0; i < colorPaletteFocus.length; i++){
			colorPaletteFocus[i] = colorPaletteImageFull.get((int)(((colorFocusGUI.x-colorPreviewGUI.x)+i)*paletteImageRatio),colorPaletteImageFull.height/2);
		}

	}

	public void loadColorPalette(){
		selectInput("Select Color Palette", "selectColorPalette");
	}

	public void selectColorPalette(File selection) {
		if (selection == null) {
			println("Window was closed or the user hit cancel.");
		} else {
			lastColorPaletteAddress = selection.getAbsolutePath();
			newColorPalette = true;
			println("User selected " + lastColorPaletteAddress);
			try{
				File f = new File(selection.getAbsolutePath());
				if(f.exists() && selection.getAbsolutePath().indexOf(".png") != -1){
					colorPaletteImageFull = loadImage(selection.getAbsolutePath());	
					colorFocusChanged = true;
				}
			} catch(Exception e){
				consolePrint("Color Palette Problem");
				consolePrint("Loading Default Palette");
				colorPaletteImageFull 	= loadImage(selfPath + "images/defaultPalette.png");
			}

			
		}
	}

	public void reinitializeColorPalette(){
		colorPaletteImageFull = loadImage(lastColorPaletteAddress);
		colorFocusChanged = true;

	}


	public void initializeConfig(){
		cp5.addButton("export")
		.setPosition(300,265)
		.setSize(50,25)
		.setCaptionLabel("Export")
		;

		cp5.getController("export").getCaptionLabel().setFont(fontLight).toUpperCase(false).align(CENTER,CENTER);

		cp5.addFrameRate().setInterval(10).setPosition(0,height - 10);

	}

	public void initializeCameraGUI(){
		cp5.addSlider("rotX")
		.plugTo(rotX)
		.setPosition(cameraPanel.x+100,cameraPanel.y+10)   
		.setSize(25, 150)
		.setCaptionLabel("Rot X")
		.setRange(-0.05f,0.05f);
		cp5.getController("rotX")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, BASELINE);
		cp5.getController("rotX").getCaptionLabel().getStyle().marginTop = 15;
		cp5.getController("rotX").getValueLabel().align(ControlP5.LEFT, ControlP5.TOP).setPaddingX(0);

		cp5.addSlider("rotY")
		.plugTo(rotY)
		.setPosition(cameraPanel.x+140,cameraPanel.y+10)   
		.setSize(25, 150)
		.setCaptionLabel("Rot Y")
		.setRange(-0.05f,0.05f);
		cp5.getController("rotY")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, BASELINE);
		cp5.getController("rotY").getCaptionLabel().getStyle().marginTop = 15;
		cp5.getController("rotY").getValueLabel().align(ControlP5.LEFT, ControlP5.TOP).setPaddingX(0);

		cp5.addSlider("rotZ")
		.plugTo(rotZ)
		.setPosition(cameraPanel.x+180,cameraPanel.y+10)   
		.setSize(25, 150)
		.setCaptionLabel("Rot Z")
		.setRange(-0.05f,0.05f);
		cp5.getController("rotZ")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, BASELINE);
		cp5.getController("rotZ").getCaptionLabel().getStyle().marginTop = 15;
		cp5.getController("rotZ").getValueLabel().align(ControlP5.LEFT, ControlP5.TOP).setPaddingX(0);

		cp5.addToggle("stopRotX")
		.plugTo(stopRotX)
		.setPosition(cameraPanel.x+100, cameraPanel.y+185)
		.setSize(25,25)
		.setCaptionLabel("Stop X");
		cp5.getController("stopRotX")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, BASELINE);
		cp5.getController("stopRotX").getCaptionLabel().getStyle().marginTop = 15;

		cp5.addToggle("stopRotY")
		.plugTo(stopRotY)
		.setPosition(cameraPanel.x+140, cameraPanel.y+185)
		.setSize(25,25)
		.setCaptionLabel("Stop Y");
		cp5.getController("stopRotY")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, BASELINE);
		cp5.getController("stopRotY").getCaptionLabel().getStyle().marginTop = 15;

		cp5.addToggle("stopRotZ")
		.plugTo(stopRotZ)
		.setPosition(cameraPanel.x+180, cameraPanel.y+185)
		.setSize(25,25)
		.setCaptionLabel("Stop Z");
		cp5.getController("stopRotZ")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, BASELINE);
		cp5.getController("stopRotZ").getCaptionLabel().getStyle().marginTop = 15;

		cp5.addButton("randomAngle")
		.setPosition(cameraPanel.x+10, cameraPanel.y+55)
		.setSize(65,25)
		.setCaptionLabel("Random");
		cp5.getController("randomAngle")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, CENTER);
		// cp5.getController("randomAngle").getCaptionLabel().getStyle().marginTop = 15;

		cp5.addButton("resetAngle")
		.setPosition(cameraPanel.x+10, cameraPanel.y+10)
		.setSize(65,25)
		.setCaptionLabel("Reset");
		cp5.getController("resetAngle")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, CENTER);
		// cp5.getController("resetAngle").getCaptionLabel().getStyle().marginTop = 15;


		cp5.addButton("plusRoll")
		.setPosition(cameraPanel.x+10, cameraPanel.y+95)
		.setSize(25,25)
		.setCaptionLabel("Roll +");
		cp5.getController("plusRoll")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, BASELINE);
		cp5.getController("plusRoll").getCaptionLabel().getStyle().marginTop = 15;

		cp5.addButton("minusRoll")
		.setPosition(cameraPanel.x+50, cameraPanel.y+95)
		.setSize(25,25)
		.setCaptionLabel("Roll -");
		cp5.getController("minusRoll")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, BASELINE);
		cp5.getController("minusRoll").getCaptionLabel().getStyle().marginTop = 15;


		cp5.addButton("plusPitch")
		.setPosition(cameraPanel.x+10, cameraPanel.y+140)
		.setSize(25,25)
		.setCaptionLabel("Pitch +");
		cp5.getController("plusPitch")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, BASELINE);
		cp5.getController("plusPitch").getCaptionLabel().getStyle().marginTop = 15;

		cp5.addButton("minusPitch")
		.setPosition(cameraPanel.x+50, cameraPanel.y+140)
		.setSize(25,25)
		.setCaptionLabel("Pitch -");
		cp5.getController("minusPitch")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, BASELINE);
		cp5.getController("minusPitch").getCaptionLabel().getStyle().marginTop = 15;

		cp5.addButton("plusYaw")
		.setPosition(cameraPanel.x+10, cameraPanel.y+185)
		.setSize(25,25)
		.setCaptionLabel("Yaw +");
		cp5.getController("plusYaw")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, BASELINE);
		cp5.getController("plusYaw").getCaptionLabel().getStyle().marginTop = 15;

		cp5.addButton("minusYaw")
		.setPosition(cameraPanel.x+50, cameraPanel.y+185)
		.setSize(25,25)
		.setCaptionLabel("Yaw -");
		cp5.getController("minusYaw")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, BASELINE);
		cp5.getController("minusYaw").getCaptionLabel().getStyle().marginTop = 15;

	}

	public void plusRoll(){
		rollPlus = true;
	}
	public void minusRoll(){
		rollMinus = true;
	}
	public void plusPitch(){
		pitchPlus = true;
	}
	public void minusPitch(){
		pitchMinus = true;
	}
	public void plusYaw(){
		yawPlus = true;
	}
	public void minusYaw(){
		yawMinus = true;
	}

	public void randomAngle(){
		randomRot = true;
		println("Random Angle");
	}

	public void resetAngle(){
		resetRot = true;
		println("Reset Angle");
	}


	public void initializeForegroundGUI(){

		cp5.addSlider("blackAlpha")
		.setPosition(90, 105)   
		.setSize(25, 170)
		.setCaptionLabel("Black")
		.setRange(0.f, 255.f);
		cp5.getController("blackAlpha")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, BASELINE);
		cp5.getController("blackAlpha").getCaptionLabel().getStyle().marginTop = 15;
		//cp5.getController("blackAlpha").getValueLabel().setFont(fontRegular);
		cp5.getController("blackAlpha").getValueLabel().align(ControlP5.LEFT, ControlP5.TOP).setPaddingX(0);

		cp5.addSlider("trailAlpha")
		.setPosition(130, 105)   
		.setSize(25, 170)
		.setCaptionLabel("Trail")
		.setRange(180.f, 255.f);
		cp5.getController("trailAlpha")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(16)
		.toUpperCase(false)
		.align(CENTER, BASELINE);
		cp5.getController("trailAlpha").getCaptionLabel().getStyle().marginTop = 15;
		cp5.getController("trailAlpha").getValueLabel().align(ControlP5.LEFT, ControlP5.TOP).setPaddingX(0);
	}

	public controlP5.Controller addKnob(String _name, int _index, float _min, float _max, String _label){
		cp5.addKnob(_name)
		.setPosition(knobPos[_index].x+visualSpecificParametersPanel.x-knobSize.x, knobPos[_index].y+visualSpecificParametersPanel.y-knobSize.y)   
		.setRadius(knobSize.x)
		.setColorValueLabel(valueLabel)
		.setRange(_min, _max)
		.setViewStyle(Knob.ARC)
		.setCaptionLabel(_label);
		cp5.getController(_name)
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(15);
		return cp5.getController(_name);
	}

	public controlP5.Controller addFader(String _name, int _index, float _min, float _max, String _label){
		cp5.addSlider(_name)
		.setPosition(faderPos[_index].x+visualSpecificParametersPanel.x, faderPos[_index].y+visualSpecificParametersPanel.y)   
		.setSize((int)faderSize.x, (int)faderSize.y)
		.setRange(_min, _max)
		.setCaptionLabel(_label);
		cp5.getController(_name)
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(15)
		.align(CENTER, BASELINE);
		cp5.getController(_name).getCaptionLabel().getStyle().marginTop = 20;
		cp5.getController(_name).getValueLabel().align(ControlP5.LEFT, ControlP5.TOP).setPaddingX(0);

		return cp5.getController(_name);
	}

	public controlP5.Controller addToggle(String _name, int _index, String _label) {
		cp5.addToggle(_name)
		.setPosition(buttonPos[_index].x+visualSpecificParametersPanel.x, buttonPos[_index].y+visualSpecificParametersPanel.y)   
		.setSize((int)buttonSize.x, (int)buttonSize.y)
		.setCaptionLabel(_label); 

		cp5.getController(_name)
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(15)
		.align(CENTER, BASELINE);
		cp5.getController(_name).getCaptionLabel().getStyle().marginTop = 20;
		return cp5.getController(_name);
	}

	public ArrayList<controlP5.Controller> addRadioButton(String _name, int _index, int _length, String _label) {
		ArrayList<controlP5.Controller> radio = new ArrayList<controlP5.Controller>();

		for(int i = 0; i < _length; i++){
			cp5.addToggle(_name+i)
			.setPosition(buttonPos[_index+i].x+visualSpecificParametersPanel.x, buttonPos[_index+i].y+visualSpecificParametersPanel.y)   
			.setSize((int)buttonSize.x, (int)buttonSize.y)
			.setCaptionLabel(_label+i); 

			cp5.getController(_name+i)
			.getCaptionLabel()
			.setFont(fontLight)
			.setSize(15)
			.align(CENTER, BASELINE);
			cp5.getController(_name+i).getCaptionLabel().getStyle().marginTop = 20;
			radio.add(cp5.getController(_name+i));
		}

		cp5.addSlider(_name+"Value")
		.setRange(-1,_length)
		.hide();
		radio.add(cp5.getController(_name+"Value"));

		return radio;
	}

	public void initializePresetGUI(){

		presetsRadio = cp5.addRadioButton("presetsRadio")
		.setPosition(presetsPanel.x, presetsPanel.y)
		.setItemsPerRow(4)
		.setItemHeight(25)
		.setItemWidth(50)
		.addItem(cp5.addToggle("preset1"), 	0)
		.addItem(cp5.addToggle("preset2"), 	1)
		.addItem(cp5.addToggle("preset3"), 	2)
		.addItem(cp5.addToggle("preset4"), 	3)
		.addItem(cp5.addToggle("preset5"), 	4)
		.addItem(cp5.addToggle("preset6"), 	5)
		.addItem(cp5.addToggle("preset7"), 	6)
		.addItem(cp5.addToggle("preset8"), 	7)
		;

		presetsRadio.getItem("preset1").setPosition(10,60).setCaptionLabel("Preset 1");
		presetsRadio.getItem("preset2").setPosition(70,60).setCaptionLabel("Preset 2");
		presetsRadio.getItem("preset3").setPosition(10,105).setCaptionLabel("Preset 3");
		presetsRadio.getItem("preset4").setPosition(70,105).setCaptionLabel("Preset 4");
		presetsRadio.getItem("preset5").setPosition(10,150).setCaptionLabel("Preset 5");
		presetsRadio.getItem("preset6").setPosition(70,150).setCaptionLabel("Preset 6");
		presetsRadio.getItem("preset7").setPosition(10,195).setCaptionLabel("Preset 7");
		presetsRadio.getItem("preset8").setPosition(70,195).setCaptionLabel("Preset 8");

		presetsRadio.setValue(-1);

		for(Toggle t : presetsRadio.getItems()){
			t.getCaptionLabel()
			.setFont(fontLight)
			.setSize(16)
			.align(CENTER, BASELINE)
			.toUpperCase(false);
			t.getCaptionLabel().getStyle().marginTop = 15;
		}

		cp5.addToggle("savePreset")
		.setPosition(presetsPanel.x+10, presetsPanel.y+10)   
		.setSize(100,25)
		.setCaptionLabel("SAVE"); 

		cp5.getController("savePreset")
		.getCaptionLabel()
		.setFont(fontLight)
		.setSize(15)
		.align(CENTER, BASELINE);
		cp5.getController("savePreset").getCaptionLabel().getStyle().marginTop = 15;
	}

	public void initializeThumbnailGUI() {

		for (int i = 0; i < thumbnailAmount; i++) {
			cp5.addButton("ve"+i)
			.setPosition(thumbnailOffsetX + (200*(i%thumbnailXAmount)), thumbnailOffsetY + (140*(i/thumbnailXAmount)))
			.setSize(185,104)
			;
			cp5.getController("ve"+i).hide();
		}

		selectedThumbnailIndex = 0;

		cp5.addButton("pageUp")
		.setPosition(thumbnailOffsetX+810, thumbnailOffsetY)
		.setSize(30, 104)
		.setCaptionLabel("^");

		cp5.addButton("pageDown")
		.setPosition(thumbnailOffsetX+810, thumbnailOffsetY+140)
		.setSize(30, 104)
		.setCaptionLabel("v");

	}

	public void loadThumbnails(){
		println("------------------------");
		for(int i = 0; i < thumbnailNames.size(); i++){
			println(i + " - " + thumbnailNames.get(i));

			File f = new File(path+"/data/thumbnails/"+thumbnailNames.get(i)+".jpg");
			if (f.exists())
			{
				println(thumbnailNames.get(i) + " thumbnail exist");
				try{
					thumbnailImages.add(loadImage(path+"/data/thumbnails/"+thumbnailNames.get(i)+".jpg"));		
				} catch(Exception e){
					println("Load Thumbnail Problem");
					thumbnailImages.add(loadImage(path+"/data/thumbnails/"+thumbnailNames.get(i)+".JPG"));		
				}


			} else {
				println(thumbnailNames.get(i) + " thumbnail not found");
				thumbnailImages.add(noThumbnail);
			}
		}
		updateThumbnails();
	}

	public void updateThumbnails(){
		for(int i = 0; i < thumbnailAmount; i++){
			int tempThumbnailIndex = i+(thumbnailAmount*thumbnailPageIndex);
			if(tempThumbnailIndex < thumbnailNames.size()){
				cp5.getController("ve"+i).setImage(thumbnailImages.get(tempThumbnailIndex));
				cp5.getController("ve"+i).updateSize();
				cp5.getController("ve"+i).show();
			} else {
				cp5.getController("ve"+i).hide();			
			}
		}
	}

	public void drawThumbnails(){
		stroke(mainYellow);
		strokeWeight(2);
		rectMode(CORNER);
		if((selectedThumbnailIndex >= (thumbnailAmount*thumbnailPageIndex)) && (selectedThumbnailIndex < (thumbnailAmount*(thumbnailPageIndex+1)))){
			rect(cp5.getController("ve"+(selectedThumbnailIndex%thumbnailAmount)).getPosition()[0],
				cp5.getController("ve"+(selectedThumbnailIndex%thumbnailAmount)).getPosition()[1],
				cp5.getController("ve"+(selectedThumbnailIndex%thumbnailAmount)).getWidth(),
				cp5.getController("ve"+(selectedThumbnailIndex%thumbnailAmount)).getHeight()
				);
		}

		for(int i = 0; i < thumbnailAmount; i++){
			int tempThumbnailIndex = i+(thumbnailAmount*thumbnailPageIndex);
			if(tempThumbnailIndex < thumbnailNames.size()){
				if(selectedThumbnailIndex == tempThumbnailIndex){
					fill(mainYellow);	
				} else {
					fill(textColor);
				}
				textSize(18);
				text(thumbnailNames.get(tempThumbnailIndex).toUpperCase(),
					cp5.getController("ve"+i).getPosition()[0], 
					cp5.getController("ve"+i).getPosition()[1]+cp5.getController("ve"+i).getHeight()+(textAscent()+textDescent()));		
			} 
		}
		thumbnailClicked = false;
	}

	public void ve0(){
		selectedThumbnailIndex = 0+(thumbnailPageIndex*thumbnailAmount);
		thumbnailClicked = true;
	}
	public void ve1(){
		selectedThumbnailIndex = 1+(thumbnailPageIndex*thumbnailAmount);
		thumbnailClicked = true;
	}
	public void ve2(){
		selectedThumbnailIndex = 2+(thumbnailPageIndex*thumbnailAmount);
		thumbnailClicked = true;
	}
	public void ve3(){
		selectedThumbnailIndex = 3+(thumbnailPageIndex*thumbnailAmount);
		thumbnailClicked = true;
	}
	public void ve4(){
		selectedThumbnailIndex = 4+(thumbnailPageIndex*thumbnailAmount);
		thumbnailClicked = true;
	}
	public void ve5(){
		selectedThumbnailIndex = 5+(thumbnailPageIndex*thumbnailAmount);
		thumbnailClicked = true;
	}
	public void ve6(){
		selectedThumbnailIndex = 6+(thumbnailPageIndex*thumbnailAmount);
		thumbnailClicked = true;
	}
	public void ve7(){
		selectedThumbnailIndex = 7+(thumbnailPageIndex*thumbnailAmount);
		thumbnailClicked = true;
	}

	public void pageUp(){
		thumbnailPageIndex++;
		thumbnailPageIndex = constrain(thumbnailPageIndex,0,thumbnailNames.size()/thumbnailAmount);
		updateThumbnails();
	}

	public void pageDown(){
		thumbnailPageIndex--;
		thumbnailPageIndex = constrain(thumbnailPageIndex,0,thumbnailNames.size()/thumbnailAmount);
		updateThumbnails();
	}

	public void drawPanel(String panelName, Rectangle panelRect) {
		pushStyle();
		fill(panelBackground);
		noStroke();
		rect(panelRect.x, panelRect.y, panelRect.width, panelRect.height);
		stroke(mainYellow);
		line(panelRect.x, panelRect.y, panelRect.x+panelRect.width, panelRect.y);
		stroke(thinLines);
		line(panelRect.x, panelRect.y+panelRect.height, panelRect.x+panelRect.width, panelRect.y+panelRect.height);
		stroke(255);
		fill(textColor);
		textFont(pfontLight, 24);
		text(panelName, panelRect.x, panelRect.y-2);
		popStyle();
	}

	public void setSoundWaveArrays(float[] rawSpectrum, float[] lpfSpectrum){
		soundSpectrum = rawSpectrum;
		soundSpectrumLPF = lpfSpectrum;
	}

	public void drawSoundWavesPanel(){
		colorMode(HSB);
		noStroke();
		fill(mainBlue);
		drawFFT(soundWavePanel.x,soundWavePanel.y,soundWavePanel.width,soundWavePanel.height, soundSpectrum);

		noStroke();
		fill(dimYellow);
		drawFFT(soundWavePanel.x,soundWavePanel.y,soundWavePanel.width,soundWavePanel.height, soundSpectrumLPF);

		drawSoundFocus(soundWavePanel.x,soundWavePanel.y,soundWavePanel.width,soundWavePanel.height);
	}

	public void drawSoundFocus(int x_, int y_, int w_, int h_){
		if(soundFocusGUI.contains(mouseX,mouseY)){
			fill(255,50);
			if(mousePressed){
				soundFocusGUI.x += mouseX-pmouseX;
				soundFocusGUI.width = constrain(soundFocusGUI.width,4,soundWavePanel.width);
				soundFocusGUI.x = constrain(soundFocusGUI.x,soundWavePanel.x,soundWavePanel.x+soundWavePanel.width-soundFocusGUI.width);
			}
		} else {
			fill(255,10);
		}
		noStroke();
		rect(soundFocusGUI.x,soundFocusGUI.y,soundFocusGUI.width,soundFocusGUI.height);
	}

	public void drawFFT(int x_, int y_, int w_, int h_, float[] fftIn){
		float fftDrawThickness = (float)w_/(float)fftIn.length;
		for(int i = 0; i < fftIn.length; i++){
			float freqVal = constrain(fftIn[i],0,h_/2);
			rect(
				x_+(i*fftDrawThickness),
				y_+((h_/2)-freqVal),
				fftDrawThickness,
				freqVal*2
				);
		}
	}

	public void initializeSoundGUI() {
		String[] parameterNames = {
			"guiSoundGain", "guiSoundDecay"
		};
		int soundParametersGUISep = 10;


		cp5.addSlider("guiSoundGain")
		.plugTo(papplet, "guiSoundGain")
		.setPosition(soundParametersPanel.x+soundParametersGUISep,soundParametersPanel.y+soundParametersGUISep)   
		.setRange(0.f, 5.f)
		.setValue(1.f)
		.setSize(soundParametersPanel.width-soundParametersGUISep*2,soundParametersPanel.height/4)
		.setCaptionLabel("Gain");  

		cp5.addSlider("guiSoundDecay")
		.plugTo(papplet, "guiSoundDecay")
		.setPosition(soundParametersPanel.x+soundParametersGUISep,soundParametersPanel.y+soundParametersPanel.height/2f+soundParametersGUISep)   
		.setRange(0.01f, 0.5f)
		.setValue(0.2f)
		.setSize(soundParametersPanel.width-soundParametersGUISep*2,soundParametersPanel.height/4)
		.setCaptionLabel("Decay");  

		cp5.getController("guiSoundGain").getCaptionLabel().getStyle().marginLeft = -cp5.getController("guiSoundGain").getWidth()-4;
		cp5.getController("guiSoundGain").getCaptionLabel().getStyle().marginTop = 26;

		cp5.getController("guiSoundDecay").getCaptionLabel().getStyle().marginLeft = -cp5.getController("guiSoundDecay").getWidth()-4;
		cp5.getController("guiSoundDecay").getCaptionLabel().getStyle().marginTop = 26;

		for (int i = 0; i < parameterNames.length; i++) {
			cp5.getController(parameterNames[i])
			.getCaptionLabel()
			.setFont(fontLight)
			.toUpperCase(false)
			.setSize(16);
		}
	}

	public void mousePressed(){
		// println(mouseX + " - " + mouseY);

		Rectangle incomingPortRect = new Rectangle((int)cp5.get(Textfield.class, "incomingPortText").getPosition()[0], (int)cp5.get(Textfield.class, "incomingPortText").getPosition()[1], (int)cp5.get(Textfield.class, "incomingPortText").getWidth(),(int)cp5.get(Textfield.class, "incomingPortText").getHeight());
		if(incomingPortRect.contains(mouseX,mouseY)){
			cp5.get(Textfield.class, "incomingPortText").show();
			cp5.get(Textfield.class, "incomingPortText").setText("");
			cp5.get(Textfield.class, "incomingPortText").setFocus(true);
		}

		Rectangle outgoingPortRect = new Rectangle((int)cp5.get(Textfield.class, "outgoingPortText").getPosition()[0], (int)cp5.get(Textfield.class, "outgoingPortText").getPosition()[1], (int)cp5.get(Textfield.class, "outgoingPortText").getWidth(),(int)cp5.get(Textfield.class, "outgoingPortText").getHeight());
		if(outgoingPortRect.contains(mouseX,mouseY)){
			cp5.get(Textfield.class, "outgoingPortText").show();
			cp5.get(Textfield.class, "outgoingPortText").setText("");
			cp5.get(Textfield.class, "outgoingPortText").setFocus(true);
		}

		Rectangle remoteIpRect = new Rectangle((int)cp5.get(Textfield.class, "remoteIpText").getPosition()[0], (int)cp5.get(Textfield.class, "remoteIpText").getPosition()[1], (int)cp5.get(Textfield.class, "remoteIpText").getWidth(),(int)cp5.get(Textfield.class, "remoteIpText").getHeight());
		if(remoteIpRect.contains(mouseX,mouseY)){
			cp5.get(Textfield.class, "remoteIpText").show();
			cp5.get(Textfield.class, "remoteIpText").setText("");
			cp5.get(Textfield.class, "remoteIpText").setFocus(true);
		}

	}

	public void mouseReleased(){
		mouseReleased = true;
	}

	public void export(){
		consolePrint("Export Pressed");
		exportAndroid = true;

	}


	public void keyPressed(){
		if(key == 'S'){
			saveNewThumbnail = true;
		}

	}

	public void consolePrint(String _s){	
		if(printDebug){
			println("GUI: " + _s);	
		}	
		
	}
}
