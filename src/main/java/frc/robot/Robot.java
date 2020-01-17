/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  WPI_TalonSRX m_left1=null;
  WPI_TalonSRX m_left11=null;
  WPI_VictorSPX m_left12=null;
  WPI_TalonSRX m_right2=null;
  WPI_TalonSRX m_right21=null;
  WPI_VictorSPX m_right22=null;  
  DifferentialDrive m_drive =null;
  Joystick m_joy = null;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    m_left1=new WPI_TalonSRX(1);
    m_left11=new WPI_TalonSRX(11);
    m_left12=new WPI_VictorSPX(12);
    m_right2=new WPI_TalonSRX(2);
    m_right21=new WPI_TalonSRX(21);
    m_right22=new WPI_VictorSPX(22);
    m_left1.configFactoryDefault();
    m_left11.configFactoryDefault();
    m_left12.configFactoryDefault();
    m_right2.configFactoryDefault();
    m_right21.configFactoryDefault();
    m_right22.configFactoryDefault();
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

    /* Joystick for control */
	  m_joy = new Joystick(0);
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }

}
