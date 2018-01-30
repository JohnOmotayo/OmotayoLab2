/**
 * This class is meant as a skeleton for the odometer class to be used.
 * 
 * @author Rodrigo Silva
 * @author Dirk Dubois
 * @author Derek Yu
 * @author Karim El-Baba
 * @author Michael Smith
 */

package ca.mcgill.ecse211.odometer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends OdometerData implements Runnable {

	  // Motors and related variables
	  private double xVar;
	  private double yVar;
	  private double tVar;
	  //private OdometerData odoData;
	  private static Odometer odo = null; // Returned as singleton
	  
	  
	  private EV3LargeRegulatedMotor leftMotor;
	  private EV3LargeRegulatedMotor rightMotor;
	  private double Theta;

	  private final double TRACK;
	  private final double WHEEL_RAD;



	  private static final long ODOMETER_PERIOD = 25; // odometer update period in ms

	  /**
	   * This is the default constructor of this class. It initiates all motors and variables once.It
	   * cannot be accessed externally.
	   * 
	   * @param leftMotor
	   * @param rightMotor
	   * @throws OdometerExceptions
	   */
	  private Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
	      final double TRACK, final double WHEEL_RAD) throws OdometerExceptions {
		  
		
	    this.leftMotor = leftMotor;
	    this.rightMotor = rightMotor;

	    // Reset the values of x, y and z to 0
	    this.setXYT(0, 0, 0);
	    this.xVar = 0.0;
	    this.yVar = 0.0;
	    this.tVar = 0.0;
	    

	    this.TRACK = TRACK;
	    this.WHEEL_RAD = WHEEL_RAD;

	  }

	  /**
	   * This method is meant to ensure only one instance of the odometer is used throughout the code.
	   * 
	   * @param leftMotor
	   * @param rightMotor
	   * @return new or existing Odometer Object
	   * @throws OdometerExceptions
	   */
	  public synchronized static Odometer getOdometer(EV3LargeRegulatedMotor leftMotor,
	      EV3LargeRegulatedMotor rightMotor, final double TRACK, final double WHEEL_RAD)
	      throws OdometerExceptions {
	    if (odo != null) { // Return existing object
	      return odo;
	    } else { // create object and return it
	      odo = new Odometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
	      return odo;
	    }
	  }

	  /**
	   * This class is meant to return the existing Odometer Object. It is meant to be used only if an
	   * odometer object has been created
	   * 
	   * @return error if no previous odometer exists
	   */
	  public synchronized static Odometer getOdometer() throws OdometerExceptions {

	    if (odo == null) {
	      throw new OdometerExceptions("No previous Odometer exists.");

	    }
	    return odo;
	  }
	  
	  public double getTheta() {
		  return this.Theta;
	  }

	  /**
	   * This method is where the logic for the odometer will run. Use the methods provided from the
	   * OdometerData class to implement the odometer.
	   */
	  
	  public void setX(double x) {
		  this.xVar = x;
	  }
	  
	  public void setY(double y) {
		  this.yVar = y;

	  }
	  
	  public void setT(double t) {
		  this.tVar = t;

	  }
	  
	  public double getX() {
		  return this.xVar;
	  }
	  
	  public double getY() {
		  return this.yVar;
	  }
	  
	  public double getT() {
		  return this.tVar;
	  }
	  // run method (required for Thread)
	  public void run() {
	    long updateStart, updateEnd;
	    
	    double leftMotorTachoCount = 0;
	    double rightMotorTachoCount = 0;
	    double wR = WHEEL_RAD;
	    double previousLeftTacho = 0;
	    double previousRightTacho = 0;
	    double dtheta;
	    double wB = TRACK;	    
	    double distL, distR, deltaC, deltaT, dX, dY;

	    
	    while (true) {
	    	updateStart = System.currentTimeMillis();
	    	
	    	leftMotorTachoCount = leftMotor.getTachoCount();
	    	rightMotorTachoCount = rightMotor.getTachoCount();
	    	distL = (Math.PI)*wR*(leftMotorTachoCount - previousLeftTacho)/180;
	    	distR = (Math.PI)*wR*(rightMotorTachoCount - previousRightTacho)/180;
	    	previousLeftTacho = leftMotorTachoCount;
	    	previousRightTacho = rightMotorTachoCount;
	    	deltaC = 0.5*(distL + distR);
	    	deltaT = (distL - distR)/wB;
	    	Theta += deltaT;
	    	dX = deltaC*Math.sin(Theta);
	    	dY = deltaC*Math.cos(Theta);
	    	xVar = dX;
	    	yVar = dY;
	    	dtheta = Math.toDegrees(deltaT);

	    	
	    	 // TODO Update odometer values with new calculated values
	        odo.update(xVar, yVar, dtheta);
	        // this ensures that the odometer only runs once every period
	        updateEnd = System.currentTimeMillis();
	        if (updateEnd - updateStart < ODOMETER_PERIOD) {
	          try {
	            Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
	          } catch (InterruptedException e) {
	            // there is nothing to be done
	          }
	        }
	    	
	    }
	  }
}

