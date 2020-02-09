/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class TurnToAngle extends CommandBase {
  private double m_angle;
  private double m_finalAngle;
  private boolean m_ifInitialized = false;
  /**
   * Creates a new TurnToAngle.
   */
  public TurnToAngle(double angle) {
    // Use addRequirements() here to declare subsystem dependencies.
    m_angle = angle;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_ifInitialized = false;
    Robot.getInstance().setSafetyEnabled(false);
    SmartDashboard.putString("TurnToAngle", "initialize");
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double heading = Gyro.getYaw();
    if (m_ifInitialized == false){
      if (Gyro.IMU_IsCalibrating()) {
        return;
      }
      m_ifInitialized = true;
      m_finalAngle = heading + m_angle;
      SmartDashboard.putNumber("First Heading", heading);
    }
    double delta = m_finalAngle - heading;
    if (delta < -180){
      delta += 360;
    }

    if (delta > 180){
      delta -= 360;
    }

    SmartDashboard.putNumber("Heading", heading);
    SmartDashboard.putNumber("Difference", delta);

    if (delta > 10) {
      Robot.getInstance().setLeftMotorLevel(.25);
      Robot.getInstance().setRightMotorLevel(-.25);
    }

    else if (delta < -10) {
      Robot.getInstance().setLeftMotorLevel(-.25);
      Robot.getInstance().setRightMotorLevel(.25);
    }

    else if (delta > 0) {
      Robot.getInstance().setLeftMotorLevel(.2);
      Robot.getInstance().setRightMotorLevel(-.2);
    }

    else if (delta < 0) {
      Robot.getInstance().setLeftMotorLevel(-.2);
      Robot.getInstance().setRightMotorLevel(.2);
    } 
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    Robot.getInstance().setLeftMotorLevel(0);
    Robot.getInstance().setRightMotorLevel(0);
    Robot.getInstance().setSafetyEnabled(true);
    SmartDashboard.putString("TurnToAngle", "end");
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (m_ifInitialized == false) {
      return false;
    }
    double heading = Gyro.getYaw();
    double delta = Math.abs(heading - m_finalAngle);
    if (delta > 180){
      delta = 360 - delta;
    }
    if (delta <= 2){
      return true;
    }
    return false;
  }
}
