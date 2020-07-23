package com.nosvisuals.engine;

import processing.core.*;
import oscP5.*;
import netP5.*;

//public class OscInterface {	
public class OscInterface implements OscEventListener{


	public PApplet papplet;
	public int incomingPort;
	public int outgoingPort;
	public String remoteIPAddress;
	public String myIpAddress;

	public OscP5 oscP5;
	public NetAddress myRemoteLocation;

	public boolean newMessage = false;
	public String oscAddr = "";
	public float[] oscData;
	public boolean isConnected = false;
	public boolean printEnable = true;

	public OscInterface(){

	}

	public OscInterface(int _incomingPort, String _remoteIP, int _outgoingPort){
		setIpAndPort(_incomingPort, _remoteIP, _outgoingPort);
		oscConnect();
	}

	public void setIpAndPort(int _incomingPort, String _remoteIP, int _outgoingPort){
		incomingPort = _incomingPort;
		remoteIPAddress = _remoteIP;
		outgoingPort = _outgoingPort;
	}

	public void oscConnect(){
		try{
			System.out.println("ASDqweASD1" + incomingPort);
			 oscP5 = new OscP5(papplet,incomingPort);
						System.out.println("ASDqweASD2");

			myRemoteLocation = new NetAddress(remoteIPAddress,outgoingPort);
						System.out.println("ASDqweASD3");

			isConnected = true;	
		} catch(Exception e){
			papplet.println(e);
			isConnected = false;
		}
		
	}

	public void oscStatus(OscStatus theStatus){

	}


	public void oscSendArray(String _tag, float[] _data) {
		OscMessage myMessage = new OscMessage(_tag);
		for (int i = 0; i < _data.length; i++) {
			myMessage.add(_data[i]);
		}
		oscP5.send(myMessage, myRemoteLocation);
		if(printEnable){
			System.out.println("OSC " + _tag);	
		}
		
	}

	// public void oscEvent() {
	public void oscEvent(oscP5.OscMessage theOscMessage) {
		newMessage = true;

		oscAddr = theOscMessage.addrPattern();
		oscData = new float[theOscMessage.typetag().length()];

		for (int i = 0; i < oscData.length; i++) {
			oscData[i] = theOscMessage.get(i).floatValue();
		}
		if(printEnable){
			System.out.print("OSC rec " + oscAddr);	
			for(int i = 0; i < oscData.length; i++){
				System.out.print(" - " + oscData[i]);
			}
			System.out.println();
		}
		

	}

}