package com.IilMetaio;

/**
 * Static constant values go here. 
 * 
 * @author tim.oppermann
 *
 */
public abstract class Configuration 
{
	public static final String signature = "C1woIsxWDa3J4WggC1PFvETyNQd+ry+GM0tUXOXRW18=";
	

	
	
	public abstract class Camera
	{
		public static final long resolutionX = 320;  	
		public static final long resolutionY = 240;
		/*
		 * 0: normal camera
		 * 1: front facing camera
		 */
		public static final int deviceId = 0;
	}
	
	

}
