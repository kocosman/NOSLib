# NOSLib
 NOS is a sound reactive visual performance tool developed and used by NOS Visuals (www.nosvisuals.com).
 
NOS is developed in Processing (compatible with 3.x) and relies heavily on the following libraries:
* **ControlP5:** Almost all GUI elements, separate windows for GUI and Canvas
* **PeasyCam:** 3D Canvas with interactions for zoom, pan and rotate
* **TheMidiBus:** Midi library for physical controllers (mostly Korg NanoKontrol 2)
* **Syphon/Spout:** Frame sharing for preview window and broadcasting to compatible software such as Resolume
* **Minim:** Interfacing with Microphone input and calculating FFT
* **OscP5:** Controlling parameters via network (experimental)


## NOS GUI Overview

![NOS GUI Overview Image](https://github.com/kocosman/NOSLib/blob/master/ReadMeAssets/NOS-GUI_Sections.jpg)

### 1. Canvas Effects
![Canvas effects GUI](https://github.com/kocosman/NOSLib/blob/master/ReadMeAssets/CanvasEffects.JPG)
* **Black:** Fades the whole canvas to black
* **Trail:** Changes the opacity of the previous frames to leave trails

### 2. Preview Screen
![Preview Screen GUI](https://github.com/kocosman/NOSLib/blob/master/ReadMeAssets/PreviewScreen.JPG)
* Receives the canvas broadcast via Syphon/Spout
* Export (development on-going): Exports visual specific components for Android app (experimental)

### 3. Visual Engine Selection
![Visual Engine Selection GUI](https://github.com/kocosman/NOSLib/blob/master/ReadMeAssets/VisualEngineSelection.JPG)
* Thumbnail buttons to select visual engines
* Supports page structure (8 thumbnails per page)

### 4. Camera Rotations
![Camera Rotations GUI](https://github.com/kocosman/NOSLib/blob/master/ReadMeAssets/CameraRotations.JPG)
* Resets the rotation of the camera
* 90 degree increment/decrement for 3 axes
* Constant rotation speed for 3 axes with stop buttons

### 5. Color
![Color GUI](https://github.com/kocosman/NOSLib/blob/master/ReadMeAssets/Color.gif)
* Load color palette image
* Adjustable color focus window (mouse wheel: size, mouse drag: position)
* Saturation and Alpha sliders for Stroke and Fill

### 6. Connectors (Experimental)
![Connectors GUI](https://github.com/kocosman/NOSLib/blob/master/ReadMeAssets/Connectors.JPG)
* IP and Port parameters for OSC connection
* Sync parameter values with remote controller  via OSC
* Dropdown list of midi devices
* Midi learn feature (experimental)

### 7. Presets
![Presets GUI](https://github.com/kocosman/NOSLib/blob/master/ReadMeAssets/Presets.JPG)
* Save and load presets
* 8 preset options

### 8. Sound Input
![Sound GUI](https://github.com/kocosman/NOSLib/blob/master/ReadMeAssets/Sound.gif)
* Raw FFT visualization of the input (Blue)
* Filtered FFT visualization of the input (Yellow)
* Gain slider
* Decay slider (makes the filtered frequency value move smoother)

### 9. Console
![Console GUI](https://github.com/kocosman/NOSLib/blob/master/ReadMeAssets/Console.JPG)
* Prints values changes, incoming messages, library initializations, internal messages

### 10. Visual Specific Parameters
![Visual Specific Parameters GUI](https://github.com/kocosman/NOSLib/blob/master/ReadMeAssets/VisualSpecificParameters.JPG)
* Shares the same layout as Korg NanoKontrol2 (8 knobs, 8 sliders, 3 buttons per channel) for easy physical-digital mapping
* Buttons can be programmed as bang, button, toggle and radio button

