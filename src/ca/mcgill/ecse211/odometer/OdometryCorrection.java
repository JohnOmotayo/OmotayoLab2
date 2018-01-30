/*
 * OdometryCorrection.java
 */
package ca.mcgill.ecse211.odometer;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.Sound;

public class OdometryCorrection implements Runnable {
	
  private static final long CORRECTION_PERIOD = 10;
  private static final EV3ColorSensor cs = new EV3ColorSensor(SensorPort.S1);
  private Odometer odometer;
  
  double gridLength = 30.48;
  private double theta;
  private int xCount;
  private int yCount;
  private int filterOut;
  private double yDist;
  private double xDist;
  public static float lastLine;

  /**
   * This is the default class constructor. An existing instance of the odometer is used. This is to
   * ensure thread safety.
   * 
   * @throws OdometerExceptions
   */
  public OdometryCorrection() throws OdometerExceptions {
    this.odometer = Odometer.getOdometer();
    this.theta = odometer.getT();
  }

  /**
   * Here is where the odometer correction code should be run.
   * 
   * @throws OdometerExceptions
   */
  // run method (required for Thread)
  public void run() {
    long correctionStart, correctionEnd;
     //length in cm
    Sound.setVolume(40);

    while (true) {
      correctionStart = System.currentTimeMillis();
      int colorID = cs.getColorID();
      
      if (colorID == 7) { //must have sensed a line
    	  Sound.beep();
    	  filterOut = 0;
        	  if(theta < 10 || (theta < 356 && theta > 360)) { //the robot must be moving northward therefore change Y
        		  odometer.setTheta(0.0);
        		  yDist = yCount*gridLength;
            	  odometer.setY(yDist); 
            	  yCount++;
              }
        	  if(theta > 80 && theta < 100) { //the robot must have turned eastward
        		  odometer.setTheta(90.0);
        		  xDist = xCount*gridLength;
        		  odometer.setX(xDist);
        		  xCount++;
        	  }
        	  if(theta > 170 && theta < 190) { //the robot must have turned southward
        		  odometer.setTheta(180.0);
        		  yDist = yCount*gridLength;
        		  odometer.setY(yDist);
        		  yCount--;
        	  }
        	  if(theta > 260 && theta < 280) {//the robot must be moving eastward
        		  odometer.setTheta(270.0);
        		  xDist = xCount*gridLength;
        		  odometer.setX(xDist);
        		  xCount--;
        	  }
          }
      
      
     
      // TODO Trigger correction (When do I have information to correct?)
      // TODO Calculate new (accurate) robot position

      // TODO Update odometer with new calculated (and more accurate) values
      // this ensure the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        try {
          Thread.sleep(CORRECTION_PERIOD - (correctionEnd - correctionStart));
        } catch (InterruptedException e) {
          // there is nothing to be done here
        }
      }
    }
  }
}
