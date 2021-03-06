package org.firstinspires.ftc.teamcode.TELE;

import android.view.Display;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.STOP_AND_RESET_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

@Disabled
@TeleOp(name = "Compiled", group = "final")

public class compiled extends LinearOpMode {

    //DRIVE
    private DcMotor motorFrontLeft;
    private DcMotor motorBackLeft;
    private DcMotor motorFrontRight;
    private DcMotor motorBackRight;

    //LATCHING
    private DcMotor latchingDC;
    private ModernRoboticsTouchSensor latchUpperLimit;
    private ModernRoboticsTouchSensor latchLowerLimit;

    //COLLECTOR
    private DcMotor collectorDC;
    private CRServo sweeperServo;
    private Servo collectorServo;
    private Servo flapServo;
    private ModernRoboticsTouchSensor collectorLimit;

    private static double FO = 0.28, FC = 0.4;

    private double cPos;
    private double cMid;
    private double cInitial;
    private double cOpen;
    private double cClose;

//    private double cSpeed;
//    private double cExtPos;

    //DROPPING
    private DcMotor dropperDC;
    private Servo dropperServo;
    private ModernRoboticsTouchSensor dropperLimit;

    private double dLoad;
    private double dUnload;
    private double dPos;

    //DRIVE ENCODERS
    private Servo xServo;
    private Servo yServo;

    private double xUp;
    private double yUp;

    private double startTime;

    @Override
    public void runOpMode() throws InterruptedException {

        //DRIVING
        motorFrontLeft = hardwareMap.dcMotor.get("frontLeft");
        motorBackLeft = hardwareMap.dcMotor.get("backLeft");
        motorFrontRight = hardwareMap.dcMotor.get("frontRight");
        motorBackRight = hardwareMap.dcMotor.get("backRight");

        motorFrontLeft.setDirection(REVERSE);
        motorBackLeft.setDirection(REVERSE);

//        motorFrontRight.setMode(STOP_AND_RESET_ENCODER);
//        motorFrontLeft.setMode(STOP_AND_RESET_ENCODER);
//        motorBackRight.setMode(STOP_AND_RESET_ENCODER);
//        motorBackLeft.setMode(STOP_AND_RESET_ENCODER);
//
//        motorFrontRight.setMode(RUN_USING_ENCODER);
//        motorFrontLeft.setMode(RUN_USING_ENCODER);
//        motorBackRight.setMode(RUN_USING_ENCODER);
//        motorBackLeft.setMode(RUN_USING_ENCODER);

        //LATCHING
        latchingDC = hardwareMap.dcMotor.get("latchingDC");
        latchingDC.setDirection(REVERSE);
        latchUpperLimit = hardwareMap.get(ModernRoboticsTouchSensor.class, "LD");
        latchLowerLimit = hardwareMap.get(ModernRoboticsTouchSensor.class, "LU");

        //COLLECTOR
        collectorDC = hardwareMap.dcMotor.get("collectDC");
        sweeperServo = hardwareMap.crservo.get("sweepServo");
        sweeperServo.setDirection(REVERSE);
        flapServo = hardwareMap.servo.get("flapServo");
        collectorServo = hardwareMap.servo.get("cServo");
        collectorLimit = hardwareMap.get(ModernRoboticsTouchSensor.class, "C");

        //sweeperDC.setDirection(REVERSE);
        //collectorDC.setDirection(REVERSE);

        collectorDC.setMode(STOP_AND_RESET_ENCODER);
        collectorDC.setMode(RUN_USING_ENCODER);

//        cSpeed = 0.07;

//        cExtPos = collectorDC.getCurrentPosition();
        cOpen = 0.92;
        cClose = 0.03;
        cMid = 0.6;
        cInitial = 0.01;


        //DROPPING
        dropperDC = hardwareMap.dcMotor.get("dropDC");
        dropperServo = hardwareMap.servo.get("dServo");
        dropperLimit = hardwareMap.get(ModernRoboticsTouchSensor.class, "D");

        dLoad = 0.71;
        dUnload = 0.3;

        dropperDC.setDirection(REVERSE);

        dropperDC.setMode(STOP_AND_RESET_ENCODER);
        dropperDC.setMode(RUN_USING_ENCODER);

        //DRIVE ENCODERS
        xServo = hardwareMap.servo.get("xServo");
        yServo = hardwareMap.servo.get("yServo");

        xUp = 0.4;
        yUp = 0.59;

        //INITIALIZATION
        dropperServo.setPosition(dLoad);
        collectorServo.setPosition(cInitial);
        xServo.setPosition(xUp);
        yServo.setPosition(yUp);
        flapServo.setPosition(FC);
        while (collectorDC.getCurrentPosition() < 240) {
            collectorDC.setPower(0.2);
        }
        collectorDC.setPower(0);
        collectorServo.setPosition(cClose);
        collectorDC.setMode(STOP_AND_RESET_ENCODER);
        collectorDC.setMode(RUN_USING_ENCODER);

        waitForStart();

        while (opModeIsActive()) {

//            gamepad1.setJoystickDeadzone(0.3f); //THIS WAS NEVER ON

            /**DRIVING*/
            if (gamepad1.right_trigger<0.1) {

                double hypot = Math.hypot(gamepad1.left_stick_x, gamepad1.left_stick_y);
                telemetry.addData("r = ", hypot);

                double robotAngle = Math.atan2(-gamepad1.left_stick_y, gamepad1.left_stick_x) - Math.PI / 4;
                telemetry.addData("robotAngle = ", robotAngle);

                double rightStickModifier = -gamepad1.right_stick_x;
                telemetry.addData("rightX = ", rightStickModifier);

                final double v1 = hypot * Math.cos(robotAngle) + rightStickModifier;
                telemetry.addData("front left power = ", motorFrontLeft.getPower());

                final double v2 = hypot * Math.sin(robotAngle) - rightStickModifier;
                telemetry.addData("front right power = ", motorFrontRight.getPower());

                final double v3 = hypot * Math.sin(robotAngle) + rightStickModifier;
                telemetry.addData("back left power = ", motorBackLeft.getPower());

                final double v4 = hypot * Math.cos(robotAngle) - rightStickModifier;
                telemetry.addData("back right power = ", motorBackRight.getPower());

                telemetry.update();

                motorFrontRight.setPower(v2);
                motorFrontLeft.setPower(v1);
                motorBackLeft.setPower(v3);
                motorBackRight.setPower(v4);

            }

            /**INCHING*/
            if (gamepad1.right_trigger > 0.1) {

                telemetry.addData("INCHING", motorFrontRight.getPower());
                telemetry.update();

                if (!gamepad1.dpad_up && !gamepad1.dpad_down && !gamepad1.dpad_left && !gamepad1.dpad_right) {
                    motorFrontRight.setPower(0);
                    motorFrontLeft.setPower(0);
                    motorBackLeft.setPower(0);
                    motorBackRight.setPower(0);
                }

                if (gamepad1.dpad_up && !gamepad1.dpad_down && !gamepad1.dpad_left && !gamepad1.dpad_right) {
                    motorFrontRight.setPower(0.2);
                    motorFrontLeft.setPower(0.2);
                    motorBackLeft.setPower(0.2);
                    motorBackRight.setPower(0.2);
                }

                if (!gamepad1.dpad_up && gamepad1.dpad_down && !gamepad1.dpad_left && !gamepad1.dpad_right) {
                    motorFrontRight.setPower(-0.2);
                    motorFrontLeft.setPower(-0.2);
                    motorBackLeft.setPower(-0.2);
                    motorBackRight.setPower(-0.2);
                }

                if (!gamepad1.dpad_up && !gamepad1.dpad_down && gamepad1.dpad_left && !gamepad1.dpad_right) {
                    motorFrontRight.setPower(0.2);
                    motorFrontLeft.setPower(-0.2);
                    motorBackLeft.setPower(-0.2);
                    motorBackRight.setPower(0.2);
                }

                if (!gamepad1.dpad_up && !gamepad1.dpad_down && !gamepad1.dpad_left && gamepad1.dpad_right) {
                    motorFrontRight.setPower(-0.2);
                    motorFrontLeft.setPower(0.2);
                    motorBackLeft.setPower(0.2);
                    motorBackRight.setPower(-0.2);
                }

            }

            /**LATCHING*/
            if (!latchUpperLimit.isPressed()) {
                if (gamepad2.dpad_up && !gamepad2.dpad_down) {
                    latchingDC.setPower(1);
                }
            }
            if (latchUpperLimit.isPressed()) {
                if (gamepad2.dpad_up && !gamepad2.dpad_down) {
                    latchingDC.setPower(0);
                }
            }
            if (!latchLowerLimit.isPressed()) {
                if (!gamepad2.dpad_up && gamepad2.dpad_down) {
                    latchingDC.setPower(-1);
                }
            }
            if (latchLowerLimit.isPressed()) {
                if (!gamepad2.dpad_up && gamepad2.dpad_down) {
                    latchingDC.setPower(0);
                }
            }
            if (!gamepad2.dpad_up && !gamepad2.dpad_down) {
                latchingDC.setPower(0);
            }

            /**SWEEPER*/
            if (gamepad2.right_bumper && !gamepad2.left_bumper && gamepad2.left_trigger < 0.1) {
                sweeperServo.setPower(1);
            }
            if (gamepad2.left_trigger > 0.1 && !gamepad2.right_bumper && !gamepad1.left_bumper  && !gamepad2.b) {
                sweeperServo.setPower(-gamepad2.left_trigger);
            }
            if (gamepad2.left_bumper && !gamepad2.right_bumper && gamepad2.left_trigger < 0.1  && !gamepad2.b) {
                sweeperServo.setPower(0);
            }

            /**COLLECTOR*/

            telemetry.addData("collector: ", collectorDC.getCurrentPosition());
            telemetry.update();

            if (gamepad2.y && !gamepad2.x && !gamepad2.b && (gamepad2.right_trigger < 0.1)) {
//                startTime = System.currentTimeMillis();
                flapServo.setPosition(FC);
                while (collectorServo.getPosition() < cOpen && !gamepad2.b) {
                    cPos = collectorServo.getPosition();
                    cPos += 0.05;
                    collectorServo.setPosition(cPos);
                }
                if (cPos > cOpen) {
                    cPos = cOpen;
                    collectorServo.setPosition(cPos);
                }
            }
//            if (!collectorServoLimit.isPressed()) {
                if (!gamepad2.y && gamepad2.x && !gamepad2.b && (gamepad2.right_trigger < 0.1)) {
//                    startTime = System.currentTimeMillis();
                    while (collectorServo.getPosition() > cClose && !gamepad2.b) {
                        cPos = collectorServo.getPosition();
                        cPos -= 0.07;
                        collectorServo.setPosition(cPos);
                    }
                    if (cPos < cClose) {
                        cPos = cClose;
                        collectorServo.setPosition(cPos);
                    }
                }
//            }
            if (!gamepad2.y && !gamepad2.x && !gamepad2.b && (gamepad2.right_trigger > 0.1)) {
//                startTime = System.currentTimeMillis();
                while (collectorServo.getPosition() < cMid && !gamepad2.b) {
                    cPos = collectorServo.getPosition();
                    cPos += 0.05;
                    collectorServo.setPosition(cPos);
                }
                if (cPos > cMid) {
                    cPos = cMid;
                    collectorServo.setPosition(cMid);
                }
            }
            if (collectorDC.getCurrentPosition() < 1800) {
                if (gamepad2.b && !gamepad2.a && collectorDC.getCurrentPosition() < 1800) {
                    collectorDC.setPower(1);
                }
            }
            if (collectorDC.getCurrentPosition() >= 1800) {
                if (gamepad2.b && !gamepad2.a && collectorDC.getCurrentPosition() < 1800) {
                    collectorDC.setPower(0);
                }
            }
            if (!collectorLimit.isPressed()) {
                if (!gamepad2.b && gamepad2.a) {
                    collectorDC.setPower(-1);
                }
            }
            if (collectorLimit.isPressed()) {
                if (!gamepad2.b && gamepad2.a) {
                    collectorDC.setPower(0);
                }
            }

            if (!gamepad2.b && !gamepad2.a) {
                collectorDC.setPower(0);
            }


            if (gamepad2.right_stick_button) {
//                COLLECTOREXTCLOSE(0.8);
                sweeperServo.setPower(1);

                while (collectorServo.getPosition() > cMid) {
                    cPos = collectorServo.getPosition();
                    cPos -= 0.07;
                    collectorServo.setPosition(cPos);
                }

                if (cPos < cMid) {
                    cPos = cMid;
                    collectorServo.setPosition(cPos);
                }

                sweeperServo.setPower(0);

                while (collectorDC.getCurrentPosition()>200) {
                    collectorDC.setPower(-0.8);
                }

                collectorDC.setPower(0);

                while (collectorServo.getPosition() >= cClose) {
                    cPos = collectorServo.getPosition();
                    cPos -= 0.07;
                    collectorServo.setPosition(cPos);
                }

                if (cPos < cClose) {
                    cPos = cClose;
                    collectorServo.setPosition(cPos);
                }

                flapServo.setPosition(FO);
            }

            /**DROPPER*/
            if (dropperLimit.isPressed()) {
                dropperDC.setMode(STOP_AND_RESET_ENCODER);
                dropperDC.setMode(RUN_USING_ENCODER);
            }

            if (!gamepad1.right_bumper && !gamepad1.left_bumper && !gamepad1.a && gamepad1.b) {
//                startTime = System.currentTimeMillis();
                while (dropperServo.getPosition() > dUnload) {
                    dPos = dropperServo.getPosition();
                    dPos -= 0.09;
                    dropperServo.setPosition(dPos);
                }
                if (dPos < dUnload) {
                    dPos = dUnload;
                    dropperServo.setPosition(dPos);
                }
            }
            if (!gamepad1.b && gamepad1.a) {
               // startTime = System.currentTimeMillis();
                while (dropperServo.getPosition() < dLoad) {
                    dPos = dropperServo.getPosition();
                    dPos += 0.09;
                    dropperServo.setPosition(dPos);
                }
                if (dPos > dLoad) {
                    dPos = dLoad;
                    dropperServo.setPosition(dPos);
                }
            }

            if (gamepad1. right_stick_button) {
                DROPPERREXTCLOSE(0.4);
//                while (dropperServo.getPosition() > dLoad) {
//                    dPos = dropperServo.getPosition();
//                    dPos += 0.09;
//                    collectorServo.setPosition(dPos);
//                }
//                if (dPos < dLoad) {
//                    dPos = dLoad;
//                    dropperServo.setPosition(dPos);
//                }
//
//                while (!dropperExtLimit.isPressed()) {
//                    dropperDC.setPower(-0.4);
//                }
//                dropperDC.setPower(0);
            }

//            if (dropperDC.getCurrentPosition() < 1900) {
                if (gamepad1.right_bumper && !gamepad1.left_bumper) {
                    dropperDC.setPower(1);
                }
//            }
//            if (dropperDC.getCurrentPosition() >= 1900) {
//                if (gamepad1.right_bumper && !gamepad1.left_bumper) {
//                    dropperDC.setPower(0);
//                }
//            }
            if (dropperLimit.isPressed()) {
                if (!gamepad1.right_bumper && gamepad1.left_bumper) {
                    dropperDC.setPower(0);
                }
            }
            if (!dropperLimit.isPressed()) {
                if (!gamepad1.right_bumper && gamepad1.left_bumper) {
                    dropperDC.setPower(-0.4);
                }
            }
            if (!gamepad1.left_bumper && !gamepad1.right_bumper) {
                dropperDC.setPower(0);
            }

            telemetry.addData("ddc: ", dropperDC.getCurrentPosition());
            telemetry.update();

        }
    }

    void COLLECTOREXTCLOSE (double power) {
     //   sweeperDC.setPower(1);
//        startTime = System.currentTimeMillis();
        while (collectorServo.getPosition() > cMid) {
            cPos = collectorServo.getPosition();
            cPos -= 0.07;
            collectorServo.setPosition(cPos);
        }
        if (cPos < cMid) {
            cPos = cMid;
            collectorServo.setPosition(cPos);
        }
       // sweeperDC.setPower(0);
//        startTime = System.currentTimeMillis();
        while (!collectorLimit.isPressed()) {
            collectorDC.setPower(-power);
        }
        collectorDC.setPower(0);
//        startTime = System.currentTimeMillis();
        while (collectorServo.getPosition() >= cClose) {
            cPos = collectorServo.getPosition();
            cPos -= 0.07;
            collectorServo.setPosition(cPos);
        }
        if (cPos < cClose) {
            cPos = cClose;
            collectorServo.setPosition(cPos);
        }
        flapServo.setPosition(FO);
    }

    void DROPPERREXTCLOSE (double power) {
//        startTime = System.currentTimeMillis();
        while (dropperServo.getPosition() > dLoad) {
            dPos = dropperServo.getPosition();
            dPos += 0.09;
            collectorServo.setPosition(dPos);
        }
        if (dPos < dLoad) {
            dPos = dLoad;
            dropperServo.setPosition(dPos);
        }
//        startTime = System.currentTimeMillis();
        while (dropperDC.getCurrentPosition() > 200) {
            dropperDC.setPower(-1);
        }
        while (!dropperLimit.isPressed()) {
            dropperDC.setPower(-power);
        }
        dropperDC.setPower(0);
    }
}
//arm closed = 0.26