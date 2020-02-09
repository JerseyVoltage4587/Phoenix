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

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.button.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  DifferentialDrive m_drive =null;
  WPI_TalonSRX m_left1=null;
  WPI_VictorSPX m_left11=null;
  WPI_VictorSPX m_left12=null;
  WPI_TalonSRX m_right2=null;
  WPI_VictorSPX m_right21=null;
  WPI_VictorSPX m_right22=null;  
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
  double totalRPM = 0;
  double elapsedTime = 0;
  double initialHeading = 0;
  double motorLevel = 0;
  long lastNanoSeconds = 0;
  String autoState = null;
  private static Robot me = null;

  public static Robot getInstance(){
    return me;
  }

  public void setLeftMotorLevel(double x){
    m_left1.set(x);
  }

  public void setRightMotorLevel(double x){
    m_right2.set(x);
  }

  public void setSafetyEnabled(boolean x){
    m_drive.setSafetyEnabled(x);
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    CameraServer.getInstance().startAutomaticCapture();
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
    me = this;
    JoystickButton buttonA1	= new JoystickButton(m_joy, 1);
    buttonA1.whenPressed(new Aim());
    
  }

  @Override
  public void autonomousInit() {
    CommandScheduler.getInstance().schedule(new TurnToAngle(15));
    m_drive.setSafetyEnabled(false);
  }

  @Override
  public void autonomousPeriodic() {
    CommandScheduler.getInstance().run();
    SmartDashboard.putNumber("Heading", Gyro.getYaw());
  }

  @Override
  public void teleopInit() {
    m_left1.setSelectedSensorPosition(0, 0, 10);
    m_right2.setSelectedSensorPosition(0, 0, 10);
    Gyro.reset();
    m_drive.setSafetyEnabled(true);
  }

  @Override
  public void teleopPeriodic() {
    CommandScheduler.getInstance().run();
    double forward = -1.0 * m_joy.getY();	// Sign this so forward is positive
    double turn = +1.0 * m_joy.getRawAxis(4);       // Sign this so right is positive
    if (m_drive.isSafetyEnabled() == false) {
      return;
    }
    
        
        /* Deadband - within 10% joystick, make it zero */
	  if (Math.abs(forward) < 0.10) {
			forward = 0;
		}
		if (Math.abs(turn) < 0.10) {
			turn = 0;
		}
     SmartDashboard.putNumber("Foward", forward);
     SmartDashboard.putNumber("Turn", turn);   
	/**
		 * Drive the robot, 
		 */
    m_drive.arcadeDrive(forward, turn);
    
    /*count += 1;
    
    int leftEncoder = -1 * m_left1.getSelectedSensorPosition(0);
    int rightEncoder = -1 * m_right2.getSelectedSensorPosition(0);
    double leftInches = leftEncoder / 4096.0 * Math.PI * 6;
    double rightInches = rightEncoder / 4096.0 * Math.PI * 6;
    long nanoSeconds = System.nanoTime();
    elapsedTime = ((nanoSeconds - lastNanoSeconds) / 1000000000.0) / 60.0;
    leftEncoderRevolutions = ((leftEncoder - lastLeftEncoder) / 4096.0) / elapsedTime;
    rightEncoderRevolutions = ((rightEncoder - lastRightEncoder) / 4096.0) / elapsedTime;
    RPM = (leftEncoderRevolutions + rightEncoderRevolutions) / 2;
    averageRPM = (RPM + lastAverageRPM) / 2;
    totalRPM += averageRPM + lastAverageRPM;
    if (lastAverageRPM < 445 || lastAverageRPM > 460) {
     double error = Math.abs(lastAverageRPM - 450);
      motorLevel = 0.7 + (.00009 * error);
    }
    m_left1.set(motorLevel);
    m_right2.set(motorLevel);
    double heading = Gyro.getYaw();

    SmartDashboard.putNumber("Left RPM", leftEncoderRevolutions);
    SmartDashboard.putNumber("Right RPM", rightEncoderRevolutions);
    SmartDashboard.putNumber("Average RPM", averageRPM);
    System.out.print("\nRPM: " + averageRPM + "\nMotor Level: " + motorLevel);
    SmartDashboard.putNumber("Total RPM", totalRPM);
    //SmartDashboard.putNumber("Count", count);
    //SmartDashboard.putNumber("Left Position", leftEncoder);
    //SmartDashboard.putNumber("Right Position", rightEncoder);
    //SmartDashboard.putNumber("Left Distance", leftInches);
    //SmartDashboard.putNumber("Right Distance", rightInches);
    //SmartDashboard.putNumber("Heading", heading);
    lastAverageRPM = RPM;
    lastLeftEncoder = leftEncoder;
    lastRightEncoder = rightEncoder;
    lastNanoSeconds = nanoSeconds;
    */
  }

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }

}
