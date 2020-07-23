package com.nosvisuals.engine;

import themidibus.*;
import processing.core.*;
import java.util.*;

public class MidiInterface{

	private PApplet papplet;
	private MidiBus midiBus;

	public String[] midiAvailableDevices;

	public int[] knobAddress;
	public int[] faderAddress;
	public int[] buttonAddress;
	public int[] extraButtons;

	// Addresses for Korg NanoKontrol2
	public int[] defaultKnobAddress = {
		16, 17, 18, 19, 20, 21, 22, 23
	};
	
	public int[] defaultFaderAddress = {
		0, 1, 2, 3, 4, 5, 6, 7
	};
	
	public int[] defaultButtonAddress = {
		32, 48, 64, 33, 49, 65, 34, 50, 66, 35, 51, 67, 36, 52, 68, 37, 53, 69, 38, 54, 70, 39, 55, 71
	};

	public int[] defaultExtraButtons = {
		58, 59, 46, 60, 61, 62, 43, 44, 42, 41, 45
	};


	public ArrayList<MidiData> midiData;

	public boolean printMidi = false;
	public boolean midiPlugged = false;

	public boolean newMidiData = false;
	public int midiInputIndex;
	public String midiInputName = "";

	public int newMidiDataAddress = -1;

	public MidiInterface(PApplet papplet){
		this.papplet = papplet;
		listDevices();

	}


	public void updateMidiAddresses(){
		midiData = new ArrayList<MidiData>();
		for(int i = 0; i < faderAddress.length; i++){
			midiData.add(new MidiData(faderAddress[i], "fader", i));
			midiData.add(new MidiData(knobAddress[i], "knob", i));
			
			midiData.add(new MidiData(buttonAddress[(i*3)], "toggle", (i*3)));
			midiData.add(new MidiData(buttonAddress[(i*3)+1], "toggle", (i*3)+1));
			midiData.add(new MidiData(buttonAddress[(i*3)+2], "toggle", (i*3)+2));
		}
	}

	public void connect(int _index, String _deviceName){
		//try{
		midiInputIndex = _index;
		midiInputName = _deviceName;
		midiBus = new MidiBus(this, midiInputIndex, -1);
		midiPlugged = true;
		papplet.println("MIDI Device plugged");
		//} catch(Exception e){
		//	papplet.println(e);
		//	midiPlugged = false;
		//	papplet.println("MIDI Device NOT found");
		//}
	}

	public void listDevices(){		
		midiAvailableDevices = MidiBus.availableInputs();
		MidiBus.list();
	}

	public void controllerChange(int channel, int number, int value) {
		newMidiData = true;
		newMidiDataAddress = number;
		if(printMidi){
			papplet.println("Midi add: " + number + " - value: " + value);
			papplet.println();
		}
		for(MidiData m : midiData){
			if(m.address == number){
				m.update(value);
			}
		}
	}
}
