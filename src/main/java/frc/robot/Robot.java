/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  WPI_TalonSRX m_left1=null;
  WPI_VictorSPX m_left11=null;
  WPI_VictorSPX m_left12=null;
  WPI_TalonSRX m_right2=null;
  WPI_VictorSPX m_right21=null;
  WPI_VictorSPX m_right22=null;  
  DifferentialDrive m_drive =null;
  Joystick m_joy = null;
  int lastLeftEncoder = 0;
  int lastRightEncoder = 0;
  int leftInitialEncoder = 0;
  int rightInitialEncoder = 0;
  int count = 0;
  double leftEncoderRevolutions = 0;
  double rightEncoderRevolutions = 0;
  double RPM = 0;
  double averageRPM = 0;
  double lastAverageRPM = 0;
  double elapsedTime = 0;
  long lastNanoSeconds = 0;
  double initialHeading = 0;
  String autoState = null;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    m_left1=new WPI_TalonSRX(1);
    m_left11=new WPI_VictorSPX(11);
    m_left12=new WPI_VictorSPX(12);
    m_right2=new WPI_TalonSRX(2);
    m_right21=new WPI_VictorSPX(21);
    m_right22=new WPI_VictorSPX(22);
    m_left1.configFactoryDefault();
    m_left11.configFactoryDefault();
    m_left12.configFactoryDefault();
    m_right2.configFactoryDefault();
    m_right21.configFactoryDefault();
    m_right22.configFactoryDefault();
    m_left1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
    m_right2.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
    m_left11.follow(m_left1);
    m_left12.follow(m_left1);
    m_right21.follow(m_right2);
    m_right22.follow(m_right2);
    m_left1.setInverted(false); // <<<<<< Adjust this until robot drives forward when stick is forward
		m_right2.setInverted(true); // <<<<<< Adjust this until robot drives forward when stick is forward
		m_left11.setInverted(InvertType.FollowMaster);
		m_left12.setInverted(InvertType.FollowMaster);
		m_right21.setInverted(InvertType.FollowMaster);
    m_right22.setInverted(InvertType.FollowMaster);
    m_drive = new DifferentialDrive(m_left1, m_right2);
    m_drive.setRightSideInverted(false);

    /* Joystick for control */
    m_joy = new Joystick(0);
    
  }

  @Override
  public void autonomousInit() {
    autoState = "initial";
    leftInitialEncoder = -1 * m_left1.getSelectedSensorPosition(0);
    rightInitialEncoder = -1 * m_right2.getSelectedSensorPosition(0);
    initialHeading = Gyro.getYaw();
    m_drive.setSafetyEnabled(false);
  }

  @Override
  public void autonomousPeriodic() {
    int leftEncoder = -1 * m_left1.getSelectedSensorPosition(0) - leftInitialEncoder;
    int rightEncoder = -1 * m_right2.getSelectedSensorPosition(0) - rightInitialEncoder;
    double leftInches = leftEncoder / 4096.0 * Math.PI * 6;
    double rightInches = rightEncoder / 4096.0 * Math.PI * 6;
    double heading = Gyro.getYaw() - initialHeading;
    if (heading < -180) {
      heading += 360;
    }

    else if (heading > 180) {
      heading -= 360;
    }

    double distance = (rightInches + leftInches) / 2.0;
    SmartDashboard.putNumber("Distance", distance);
    SmartDashboard.putNumber("Left Position", leftEncoder);
    SmartDashboard.putNumber("Right Position", rightEncoder);
    SmartDashboard.putNumber("Left Distance", leftInches);
    SmartDashboard.putNumber("Right Distance", rightInches);
    SmartDashboard.putNumber("Heading", heading);
    if (autoState.equals("initial")) {
      if (distance >= 72) {
        m_left1.set(0);
        m_right2.set(0);
        autoState = "turn";
      }
      else if (distance >= 36) {
        m_left1.set(.3);
        m_right2.set(.3);
      }
      else if (distance >= 48) {
        m_left1.set(.2);
        m_right2.set(.2);
      }
      else if (distance >= 54) {
        m_left1.set(.1);
        m_right2.set(.1);
      }
      else {
        m_left1.set(.5);
        m_right2.set(.5);
      }
    }
    else if (autoState.equals("turn")) {
      if (heading < -90 || heading > 170) {
        autoState = "done";
        m_left1.set(0);
        m_right2.set(0);
      }
      else {
        m_left1.set(.25);
        m_right2.set(-.25);
      }
    }
  }

  @Override
  public void teleopInit() {
    m_left1.setSelectedSensorPosition(0, 0, 10);
    m_right2.setSelectedSensorPosition(0, 0, 10);
    Gyro.reset();
    m_drive.setSafetyEnabled(false);
  }

  @Override
  public void teleopPeriodic() {
  //  double forward = -1.0 * m_joy.getY();	// Sign this so forward is positive
	//	double turn = +1.0 * m_joy.getZ();       // Sign this so right is positive
        
        /* Deadband - within 10% joystick, make it zero */
	//	if (Math.abs(forward) < 0.10) {
	//		forward = 0;
	//	}
	//	if (Math.abs(turn) < 0.10) {
	//		turn = 0;
	//	}
        
		/**
		 * Print the joystick values to sign them, comment
		 * out this line after checking the joystick directions. 
		 */
   //     System.out.println("JoyY:" + forward + "  turn:" + turn );
        
		/**
		 * Drive the robot, 
		 */
   // m_drive.arcadeDrive(forward, turn);
    
    m_left1.set(0.5);
    m_right2.set(0.5);
    int leftEncoder = -1 * m_left1.getSelectedSensorPosition(0);
    int rightEncoder = -1 * m_right2.getSelectedSensorPosition(0);
    double leftInches = leftEncoder / 4096.0 * Math.PI * 6;
    double rightInches = rightEncoder / 4096.0 * Math.PI * 6;
    long nanoSeconds = System.nanoTime();
    elapsedTime = ((nanoSeconds - lastNanoSeconds) / 1000000000.0) / 60.0;
    leftEncoderRevolutions = ((leftEncoder - lastLeftEncoder) / 4096.0) / elapsedTime;
    rightEncoderRevolutions = ((rightEncoder - lastRightEncoder) / 4096.0) / elapsedTime;
    RPM = (leftEncoderRevolutions + rightEncoderRevolutions) / 2.0;
    averageRPM = (RPM + lastAverageRPM) / count;
    double heading = Gyro.getYaw();

    SmartDashboard.putNumber("Left RPM", leftEncoderRevolutions);
    SmartDashboard.putNumber("Right RPM", rightEncoderRevolutions);
    SmartDashboard.putNumber("Average RPM", averageRPM);
    SmartDashboard.putNumber("Left Position", leftEncoder);
    SmartDashboard.putNumber("Right Position", rightEncoder);
    SmartDashboard.putNumber("Left Distance", leftInches);
    SmartDashboard.putNumber("Right Distance", rightInches);
    SmartDashboard.putNumber("Heading", heading);
    lastAverageRPM = RPM;
    lastLeftEncoder = leftEncoder;
    lastRightEncoder = rightEncoder;
    lastNanoSeconds = nanoSeconds;
    count += 1;
  }

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }

}
