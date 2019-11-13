package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.hardware.bosch.BNO055IMU;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;


@SuppressWarnings("FieldCanBeLocal")
@TeleOp(name="Main DC", group="Iterative Opmode")
//@Disabled
public class DC_Code1920 extends OpMode
{
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeft;
    private DcMotor backLeft;
    private DcMotor frontRight;
    private DcMotor backRight;
    private DcMotor leftWheel;
    private DcMotor rightWheel;
    private Servo leftCollector;
    private Servo rightCollector;
    private Servo rightscorer;
    private Servo leftscorer;
    private Servo encoderlift;

    private double towerHeight = 1;
    private double dampener = 1; // slows the robot down on command
    private boolean upPressed; //checks if the up/down button is unpressed before running method code again
    private boolean downPressed;
    private boolean apressed;
    private double speed;
    private double driveangle;
    private boolean fieldCentric;


    BNO055IMU               imu;
    Orientation             lastAngles = new Orientation();
    double                  globalAngle, power = .30, correction;

    public void init()
    {
        frontLeft = hardwareMap.get(DcMotor.class, "front_left");
        frontLeft.setDirection(DcMotor.Direction.FORWARD);

        backLeft = hardwareMap.get(DcMotor.class, "back_left");
        backLeft.setDirection(DcMotor.Direction.FORWARD);

        frontRight = hardwareMap.get(DcMotor.class, "front_right");
        frontRight.setDirection(DcMotor.Direction.REVERSE);

        backRight = hardwareMap.get(DcMotor.class, "back_right");
        backRight.setDirection(DcMotor.Direction.REVERSE);

        leftCollector = hardwareMap.get(Servo.class, "left_collector");
        leftCollector.setPosition(1);

        rightCollector = hardwareMap.get(Servo.class, "right_collector");
        rightCollector.setPosition(0);

        leftWheel = hardwareMap.get(DcMotor.class, "Intake1");
        rightWheel = hardwareMap.get(DcMotor.class, "Intake2");

        leftscorer = hardwareMap.get(Servo.class, "left_scorer");
        leftscorer.setPosition(0);
        rightscorer = hardwareMap.get(Servo.class, "right_scorer");
        rightscorer.setPosition(1);

        encoderlift = hardwareMap.get(Servo.class, "encoderlift");
        encoderlift.setPosition(0.5);

        fieldCentric = false;
        apressed = false;

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;
        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        imu.initialize(parameters);

        telemetry.addData("Mode", "calibrating...");
        telemetry.update();

        // make sure the imu gyro is calibrated before continuing.
        while (!imu.isGyroCalibrated())
        {

        }

        telemetry.addData("Mode", "waiting for start");
        telemetry.addData("imu calib status", imu.getCalibrationStatus().toString());
        telemetry.update();

    }
    @Override
    public void start()
    {
        runtime.reset();
    }

    public void loop() {
        telemetry.addData("Running", " :)");
        telemetry.update();
        telemetry.clear();
        telemetry.addData("Running", ";)");
        telemetry.update();
        telemetry.clear();

        dampener = 1 - (0.7 * (gamepad1.left_trigger));
        driveangle = (Math.atan2(-gamepad1.left_stick_y, gamepad1.left_stick_x) - Math.PI / 4);
        speed = Math.hypot(gamepad1.left_stick_x, gamepad1.left_stick_y);
        telemetry.addData("DriveAngle", driveangle);

        if (fieldCentric)
        {
            frontLeft.setPower((Math.cos((driveangle-getRobotAngle())%(2*Math.PI)) * dampener * speed)+gamepad1.right_stick_x);
            frontRight.setPower((Math.sin((driveangle-getRobotAngle())%(2*Math.PI)) * dampener * speed)-gamepad1.right_stick_x);
            backLeft.setPower((Math.sin((driveangle-getRobotAngle())%(2*Math.PI)) * dampener * speed)+gamepad1.right_stick_x);
            backRight.setPower((Math.cos((driveangle-getRobotAngle())%(2*Math.PI)) * dampener * speed)-gamepad1.right_stick_x);
        }
        else
        {
            frontLeft.setPower(Math.cos(driveangle)*dampener*speed+gamepad1.right_stick_x);
            frontRight.setPower(Math.sin(driveangle)*dampener*speed-gamepad1.right_stick_x);
            backLeft.setPower(Math.sin(driveangle)*dampener*speed+gamepad1.right_stick_x);
            backRight.setPower(Math.cos(driveangle)*dampener*speed-gamepad1.right_stick_x);
        }
       // strafe(Math.hypot(gamepad1.left_stick_x,gamepad1.left_stick_y), getLeftStickAngle()-getRobotAngle());

    if(gamepad1.right_bumper)
    {
        //collector in
        leftCollector.setPosition(0.75);
        rightCollector.setPosition(0.25);

    }
    else
    {
        //collector open
        leftCollector.setPosition(0.6);
        rightCollector.setPosition(0.4);
    }

    if(gamepad1.y)
    {
        leftWheel.setPower(1);
        rightWheel.setPower(-1);
    }
    if(gamepad1.b)
        {
            leftWheel.setPower(-1);
            rightWheel.setPower(1);
        }
    if(gamepad1.x)
        {
            leftWheel.setPower(0);
            rightWheel.setPower(0);
        }
    if(gamepad1.a && !apressed)
    {
        if(fieldCentric)
        {
            fieldCentric = false;
        }
        else fieldCentric = true;
        apressed = true;
    }
    if(!gamepad1.a)apressed=false;



        if (!gamepad2.dpad_up)
        {
            upPressed = true;
        }
        if (!gamepad2.dpad_down)
        {
            downPressed = true;
        }

        if (gamepad2.dpad_up)
        {
            incrementTower();
        }
        if (gamepad2.dpad_down)
        {
            decrementTower();
        }
        if (gamepad2.x)
        {
            towerHeight = 1.0;
        }


        if(gamepad2.right_bumper) {
            leftscorer.setPosition(0.8);
            rightscorer.setPosition(0.2);
        }

        if(gamepad2.left_bumper)
        {
            leftscorer.setPosition(0);
            rightscorer.setPosition(1);
        }
        if(gamepad2.right_trigger>0.2)
        {
            leftscorer.setPosition(.7);
            rightscorer.setPosition(.3);
        }



     /*   telemetry.addData("heading: ", imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)  );
        telemetry.addData("left stick angle", getLeftStickAngle());
        telemetry.addData("x", gamepad1.left_stick_x);
        telemetry.addData("y", -1 * gamepad1.left_stick_y);
        telemetry.addData("GO ANGLE", getLeftStickAngle()-getRobotAngle());
        telemetry.addData("Z", getRobotAngle());
*/
        telemetry.addData("towerHeight:", towerHeight + " Inches");
        if(fieldCentric)telemetry.addData( "Mode:","Field-Centric");
        else telemetry.addData( "Mode:","Robo-Centric");


    }

    public double getLeftStickAngle()
    {
        double x = gamepad1.left_stick_x;
        double y = -1 * gamepad1.left_stick_y;
        if(x > 0 && y > 0)
        {
            return Math.toDegrees(Math.atan(x/y));
        }
        else if (x > 0 && y < 0)
        {
            return 180 - Math.toDegrees(Math.atan(x/-y));
        }
        else if (x < 0 && y < 0)
        {
            return 180 + Math.toDegrees(Math.atan(-x/-y));
        }
        else if (x < 0 && y > 0)
        {
            return 360 - Math.toDegrees(Math.atan(-x/y));
        }
        else if (x == 0 && y > 0)
        {
            return 0;
        }
        else if (x == 0 && y < 0)
        {
            return 180;
        }
        else if (x > 0 && y == 0)
        {
            return 90;
        }
        else if (x < 0 && y == 0)
        {
            return 270;
        }
        return 0;
    }

    public double getRobotAngle()
    {
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        return -1 * angles.firstAngle;
    }

    public void incrementTower()
    {
        towerHeight += 4;
        upPressed = false;
    }

    public void decrementTower()
    {
        if (towerHeight >= 5)
        {
            towerHeight -= 4;
        }
    }

    public void strafe(double power, double direction)
    {
        direction = 90 - direction;
        direction = Math.toRadians(direction);

        double x = Math.cos(direction);
        double y =  Math.sin(direction);

        frontLeft.setPower(((y + x) * power)+gamepad1.right_stick_x);
        frontRight.setPower(((y - x) * power)-gamepad1.right_stick_x);
        backLeft.setPower(((y - x) * power)+gamepad1.right_stick_x);
        backRight.setPower(((y + x) * power)-gamepad1.right_stick_x);

        telemetry.addData("cos:" , x);
        telemetry.addData("sin:", y);
        telemetry.update();
        telemetry.clear();
    }






    @Override
    public void stop() { }
}