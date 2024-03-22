package vehiclesimulationcore;

import java.awt.event.*;
import java.awt.AWTEvent;
import java.util.Enumeration;
import javax.media.j3d.*;
//import com.sun.j3d.utils.behaviors.keyboard.KeyNavigator;
import javax.vecmath.Vector3d;
import javax.vecmath.Matrix3d;
import java.util.ArrayList;
import dynamics.*;
import java.util.Calendar;

//import javax.media.j3d.Behavior;
//import javax.media.j3d.TransformGroup;
//import javax.media.j3d.Transform3D;
/**
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *
 * <p>Company: FIT</p>
 *
 * @author Yasser Almohammad(Graphics Design and implementation), Ahmad Zoubi,
 *   Usama Rmelawi, Mohammad Askar,Qousai Dabour (vehicle case study, core
 *   functionality)
 * @version 1.0
 *
 * this file defines classes required for behaviours of other geometry classes
 * multiple geometry classes may share the same behaviour
 */
public class Behaviours {
    public Behaviours() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
    }
}


/**
 *
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *
 * <p>
 *  - keyboard naviagtion for the vehicle
 *  - navigation speed must be considered
 *  - KEYRELEASE also will be considerd when enableing speed raise and fade effects
 *  - W move forward, S move backwad, D rotate right, A rotate left, Q slide left, E slide Right
 *    numbers 1 through 8 are used for different camera settings
 *  - camera free navigation arround(only arround) the vehicle is enabled using the arrow keys
 * <p>
 *  - Camera is bended to a vehicle, as vehicle moves, the camera(view port, view platform...)
 *    remains looking at the vehicle from a fixed position relative to it
 *  - the mechanisim at which the keyboard is working is like this:
 *     when pressing a key we register a variable for the pressing action, we unregister it when
 *     release is done, this enables more than one key to be pressed simultaniously and all
 *     of them work.
 *     the movement is done inside a thread which runds all the time and monitors any registered
 *     key and makes the appropriate action.
 *     thought another method exists to do the same action using elapsed frames monitoring
 *     how ever the thread method approved to consume less processing and resources and works just
 *     fine and far better than the elpsed frames method.
 */
class KeyboardBehaviours extends Behavior {
    private VehicleGeom vehicleGeom; //vehicle geometry transform group
    private WakeupOnAWTEvent conditionKeyPressed = null;
    private WakeupOnAWTEvent conditionKeyReleased = null;
    private WakeupOnElapsedFrames conditionElapsedFrames = new
            WakeupOnElapsedFrames(0);
    private WakeupOr wakeupCondition = null; //or of the two previous conditions
    private KeyEvent keyEvent = null; //KeyEvent
    private double currentSpeed = 0.0; //changed in key_release event, can change by Square
    private double transStep = 0.1; //initial speed step.
    private double maxSpeed = 22;
    private double rotStep = 0.005;
    private double spinAngle=0;
    private double spinStep=0.5;
    //key states that interrest us in moving in conjunction with each other
    private boolean isRotatingLeft = false; //indicates direction of wheel rotation direction
    private boolean isRotatingRight = false;
    private boolean isMovingForward = false;
    private  boolean isMovingBackward = false;
    private boolean isSlidingLeft = false;
    private boolean isSlidingRight = false;
    //to test suspension by keyboard
    private boolean isShiftingUp=false;
    private boolean isShiftingDown=false;
    private boolean isPushingAhead=false; //when load infront is greater
    private boolean isPushingback=false;
    private boolean isPushingLeft=false;
    private boolean isPushingRight=false;
    private boolean isSpaceDown=false;
    //brake on the back left wheel only
    private boolean isBackLeftBrake=false;
    //brake on the back right wheel only
    private boolean isBackRightBrake=false;

    private boolean isThreadStarted=false;
    private UpdateVehicleUsingKeyStates stateThread=new UpdateVehicleUsingKeyStates();
    int updateMethod=0; //0 using frame wake up condition, 1 for thread update method

    /**
     * generaly this method should never be called
     */
    KeyboardBehaviours() {
    }

    /**
     * we assign the behavioue to a vehicle geomtry
     *
     * @param vehicleGeom VehicleGeom
     */
    KeyboardBehaviours(VehicleGeom vehicleGeom) {
        this.vehicleGeom = vehicleGeom;

    }

    /**
     * manually set or update the vehicle geometry( also should not be called)
     * @param v VehicleGeom
     */
    void setVehicleGeom(VehicleGeom v) {
        vehicleGeom = v;
    }

    /**
     * this method is called not only at construction of an instance of this class
     * but also when the related object is hidden and viewed back again
     * and because we enable hiding and replacing the vehicle geom model thus
     * this method is called more than once and thread state should be checked before started
     * not to allow an exceptoin to occur
     *
     */
    public void initialize() {
        // set initial wakeup condition
        conditionKeyPressed = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
        conditionKeyReleased = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
        WakeupCriterion[] conditions = {conditionKeyPressed,
                                       conditionKeyReleased/*,
                                       conditionElapsedFrames*/};
        wakeupCondition = new WakeupOr(conditions);
        this.wakeupOn(wakeupCondition);
        if (!stateThread.isAlive()) {
            stateThread.start();
            stateThread.suspend();
        }

        this.setUpdateSceneUsingThreading(); //defualt
    }

    /**
     * we handle the wakeup criterias, we got one for keyPress events and
     * another for key release events, main commands are registed-unregistered here
     * using key press-release
     * commands includes: registeration of crusing and camera type
     *
     *
     * -  we got this case: we can't move forward and corner in the same time using the
     *    keyboard for some reason, so this is how we do it:
     *               when we press forward-backward button, we keep moving until the same key is released
     *    even when it's press event is not caught(only once at least) and another key is pressed, in the same time we process the currently
     *    caught event, the same apply to others: stop when release.
     *    so the solution won't depend on the number of times a key is pressed but only on one press-release
     *    event.
     *    we got two solutions to work on:
     *        first: make a thread, control time between two transform changes on key request, set priority
     *               it's resource consumer method (theoritically), but we control speed over any frame rate, in
     *               addition we prevent the wakeupcondition using elapsed frames(good).
     *        second: depend on frame count, change before specific frames are drawn, which might yield
     *    a good performance(theoritically) but different effects on different computer speeds(bad)
     *                (frame rate is harware dependant -window size dependant...)
     *
     *    so we'll try both solutions, make them enabled by code, and choose one of them.
     *    in both ways the sole rule of the key events of navigation is the registeration of states
     *    and the frame wake up handler is responsible for movement of the vehicle
     *
     *    after trail of both methods the thread method approved to be the best, both in performance
     *    and visualiation, so it's choosed by default
     *
     * @param criteria Enumeration
     */
    public void processStimulus(Enumeration criteria) {
        //get the key event and process it

        while (criteria.hasMoreElements()) { //iterate
            WakeupCriterion c = (WakeupCriterion) criteria.nextElement();

            if (c instanceof WakeupOnAWTEvent) {
                WakeupOnAWTEvent event = (WakeupOnAWTEvent) c;
                AWTEvent[] events = event.getAWTEvent();

                for (int i = 0; i < events.length; i++) {
                    keyEvent = (KeyEvent) events[i];
                    int code = keyEvent.getKeyCode();
                    if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                        switch (code) {
                        case KeyEvent.VK_W: //register move forward
                            isMovingForward = true;
                            break;
                        case KeyEvent.VK_S: //register move backward
                            isMovingBackward = true;
                            break;
                        case KeyEvent.VK_A: //register rotate left
                            isRotatingLeft = true;
                            //update wheel rotaion as a feed back

                            if(SimVals.enableOnline)
                                updateSteer();
                            else
                                vehicleGeom.rotateFrontWheels(0.5);
                            break;
                        case KeyEvent.VK_D: //register rotate right
                            isRotatingRight = true;
                        //    vehicleGeom.rotateFrontWheels(-0.5);
                            if(SimVals.enableOnline)
                                updateSteer();
                            else
                                vehicleGeom.rotateFrontWheels(0.5);

                            break;
                        case KeyEvent.VK_SPACE: //brake back wheels
                            isSpaceDown = true;
                            if (SimVals.enableOnline)
                                updateBrake();
                            break;
                        case KeyEvent.VK_V: //brake left wheel brake
                            isBackLeftBrake = true;
                            if (SimVals.enableOnline)
                                updateBrake();
                            break;
                        case KeyEvent.VK_B: //brake back wheels
                            isBackRightBrake = true;
                            if (SimVals.enableOnline)
                                updateBrake();
                            break;
                        case KeyEvent.VK_Q: //register slide right
                            isSlidingRight = true;
                            break;
                        case KeyEvent.VK_E: //register slide left
                            isSlidingLeft = true;
                            break;
                        case KeyEvent.VK_I: //increase head load for suspension
                            isPushingAhead = true;
                            break;
                        case KeyEvent.VK_K: //increase back load for suspension
                            isPushingback = true;
                            break;
                        case KeyEvent.VK_L: //increase right load for suspension
                            isPushingRight = true;
                            break;
                        case KeyEvent.VK_J: //increase left load for suspension
                            isPushingLeft = true;
                            break;
                        case KeyEvent.VK_M: //move up
                            isShiftingUp = true;
                            break;
                        case KeyEvent.VK_N: //move down
                            isShiftingDown = true;
                            break;

                        case KeyEvent.VK_R: //reset the object to world's origin
                            vehicleGeom.pos.set(0, 0, 0);
 //                           angle = 0;
                            vehicleGeom.angles.set(0,0,0);
                            vehicleGeom.updateCoords();
                            break;
/*
                        case KeyEvent.VK_B: //Bind Camera to Vehicle
                            Camera.isCameraBinded = !Camera.isCameraBinded;
                            vehicleGeom.bindCamera(Camera.isCameraBinded);
                            if (Camera.isCameraBinded) {
                                Camera.setTVPos1CameraType();
                                Camera.updatePos(vehicleGeom.pos, vehicleGeom.angles);
                            } else { //return to origin
                                Camera.setUnBindCameraType();
                            }
                            break;
*/
                            //following keys for camera position, active when binding is
                        case KeyEvent.VK_1: //camera view: tvPos1: behind vehicle, following it
                                Camera.angle=0; //reset
                                Camera.setTVPos1CameraType();
                                Camera.updatePos(vehicleGeom.pos, vehicleGeom.angles);
                            break;

                        case KeyEvent.VK_2: //side view looking at the center to have view of both front and back wheels


                                Camera.angle=0;
                                Camera.setWheelCameraType();
                                Camera.updatePos(vehicleGeom.pos,vehicleGeom.angles);

                            break;

                        case KeyEvent.VK_3: //driver view, camera in the middle of front seat looking-at-through the window

                                Camera.angle=0;
                                Camera.setDriverCameraType();
                                Camera.updatePos(vehicleGeom.pos, vehicleGeom.angles);
                            break;

                        case KeyEvent.VK_4: //camera view, camera at origin looking at the vehicle
                                Camera.angle=0;
                                Camera.setOriginCameraType();
                                Camera.updatePos(vehicleGeom.pos, vehicleGeom.angles);
                            break;
                        case KeyEvent.VK_5: //above view: camera looking down to the center
                                Camera.angle = 0;
                                Camera.setaBoveCameraType();
                                Camera.updatePos(vehicleGeom.pos,vehicleGeom.angles);
                            break;
                        case KeyEvent.VK_6: //above view: camera looking down to the center
                                Camera.angle = 0;
                                Camera.setaBove1CameraType();
                                Camera.updatePos(vehicleGeom.pos,vehicleGeom.angles);
                            break;
                        case KeyEvent.VK_7: //above view: camera looking down to the center
                                Camera.angle = 0;
                                Camera.setaBove2CameraType();
                                Camera.updatePos(vehicleGeom.pos,vehicleGeom.angles);
                            break;

                        /********** camer free rotation controls **************/

                        case KeyEvent.VK_LEFT: //rotate the camera left
                            Camera.angle+=0.1;
                            Camera.updatePos(vehicleGeom.pos,vehicleGeom.angles); //update normally
                            break;

                        case KeyEvent.VK_RIGHT: //rotate the camera right
                            Camera.angle-=0.1;
                            Camera.updatePos(vehicleGeom.pos,vehicleGeom.angles);
                            break;

                        case KeyEvent.VK_UP: //come closer to the lookAt point
                            Camera.decreaseCurDistance();
                            Camera.updatePos(vehicleGeom.pos,vehicleGeom.angles);
                            break;

                        case KeyEvent.VK_DOWN: //go further from the lookAt point
                           Camera.increaseCurDistance();
                            Camera.updatePos(vehicleGeom.pos,vehicleGeom.angles);
                            break;
                        }

                    }

                    //on key release unregister previous key press
                    if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
                        switch (code) {
                        case KeyEvent.VK_W: //register move forward
                            isMovingForward = false;
                            break;
                        case KeyEvent.VK_SPACE: //move backward
                           isSpaceDown = false;
                           SimVals.brakeBL=SimVals.brakeBR=0;
                           break;
                       case KeyEvent.VK_V: //move backward
                           isBackLeftBrake = false;
                           SimVals.brakeBL=0;
                            break;
                        case KeyEvent.VK_B: //move backward
                            isBackRightBrake = false;
                            SimVals.brakeBR=0;
                            break;

                        case KeyEvent.VK_S: //move backward
                            isMovingBackward = false;
                            break;
                        case KeyEvent.VK_A: //rotate left
                            isRotatingLeft = false;
                            vehicleGeom.rotateFrontWheels(0); //restore here better, wheel feedback
                            break;
                        case KeyEvent.VK_D: //rotate right
                            isRotatingRight = false;
                            vehicleGeom.rotateFrontWheels(0);
                            break;
                        case KeyEvent.VK_Q: //slide right
                            isSlidingRight = false;
                            break;
                        case KeyEvent.VK_E: //slide left
                            isSlidingLeft = false;
                            break;
                        case KeyEvent.VK_T: //switch key update method
                            if(isThreadStarted)
                                this.setUpdateSceneUsingFrames();
                            else
                                this.setUpdateSceneUsingThreading();
                            break;

                        case KeyEvent.VK_I: //increase head load for suspension
                            isPushingAhead = false;
                            break;
                        case KeyEvent.VK_K: //increase back load for suspension
                            isPushingback = false;
                            break;
                        case KeyEvent.VK_L: //increase right load for suspension
                            isPushingRight = false;
                            break;
                        case KeyEvent.VK_J: //increase left load for suspension
                            isPushingLeft = false;
                            break;
                        case KeyEvent.VK_M: //move up
                            isShiftingUp = false;
                            break;
                        case KeyEvent.VK_N: //move down
                            isShiftingDown = false;
                            break;

                        }
                    }

                }
            } else if (c instanceof WakeupOnElapsedFrames) { //elapsed frame count reached
               //after predefined frame count elpses, update stats
                //of geometry
               updateKeyStateUsingElapsedFrames(); //and isThreadState is false
            }

        }

        this.wakeupOn(wakeupCondition);
    }


    public void updateSteer() {
     //   SimVals.steerFL=0.3;
     //   SimVals.steerFL=0.3;

     if (isRotatingLeft) {
         if (SimVals.steerFL > -SimVals.maxSteerFL)
             SimVals.steerFL -= SimVals.steerStep;
     }

     if (isRotatingRight) {
         if (SimVals.steerFL < SimVals.maxSteerFL)
             SimVals.steerFL += SimVals.steerStep;

     }



    }
   public void updateBrake(){
       if(isSpaceDown){
           SimVals.brakeBL=SimVals.maxBrakeTorqueBL;
           SimVals.brakeBR=SimVals.maxBrakeTorqueBR;
           return;
       }

       if(isBackLeftBrake){
           SimVals.brakeBL=SimVals.maxBrakeTorqueBL;
           return;
       }

       if(isBackRightBrake){
           SimVals.brakeBR=SimVals.maxBrakeTorqueBR;
           return;
       }
   }
  //  Calendar calendar=Calendar.getInstance();
    long currentTime=0;
    long prevTime=0;
    long diff=0;
    long frames=0; //frame rate (frames/sec)

    /**
     * after a specified frame count is elapsed we update the vehicle depending
     * on the states of keyboard button states, pressed or release
     * multipe transformations can be applied depending on the count of keys pressed
     *
     * beacuase farme rate might change so we control updating of geometry using fixed time
     * elapsed between two updates
     */

    public void updateKeyStateUsingElapsedFrames() {
        /*
         control time between two update calls
      */
     //currentTime=Calendar.getInstance().getTimeInMillis();
      currentTime=1;
     diff=currentTime-prevTime;
//     frames++;
     if(diff<30)
         return;
//    System.out.println(1000*frames/50); //frame rate
//    frames=0;
    prevTime=currentTime;

     //output current frame rate

     //check key states
        if (isMovingForward) {
            vehicleGeom.pos.set(vehicleGeom.pos.x - Math.cos(vehicleGeom.angles.y) * transStep, vehicleGeom.pos.y,
                    vehicleGeom.pos.z + Math.sin(vehicleGeom.angles.y) * transStep);
            vehicleGeom.updatePos();
        }
        if (isMovingBackward) {
            vehicleGeom.pos.set(vehicleGeom.pos.x + Math.cos(vehicleGeom.angles.y) * transStep, vehicleGeom.pos.y,
                    vehicleGeom.pos.z - Math.sin(vehicleGeom.angles.y) * transStep);
            vehicleGeom.updatePos();
        }
        if (isRotatingLeft) {
            vehicleGeom.angles.y += rotStep;
            vehicleGeom.updateAngle();
        }
        if (isRotatingRight) {
            vehicleGeom.angles.y -= rotStep;
            vehicleGeom.updateAngle();
        }
        if (isSlidingLeft) {
            vehicleGeom.pos.set(vehicleGeom.pos.x - Math.sin(vehicleGeom.angles.y) * transStep, vehicleGeom.pos.y,
                    vehicleGeom.pos.z - Math.cos(vehicleGeom.angles.y) * transStep);
            vehicleGeom.setPos(vehicleGeom.pos);
        }
        if (isSlidingRight) {
            vehicleGeom.pos.set(vehicleGeom.pos.x + Math.sin(vehicleGeom.angles.y) * transStep, vehicleGeom.pos.y,
                    vehicleGeom.pos.z + Math.cos(vehicleGeom.angles.y) * transStep);
            vehicleGeom.updatePos();
        }
    }

    /**
     * changes some variables to enable keyboard scene update using a thread
     * instead of frame wake up condition, we change the wake up condition set
     */
    public void setUpdateSceneUsingThreading(){
        transStep = 0.3; //initial speed step.
        rotStep = 0.05;
        //conditionElapsedFrames no longer needed
        WakeupCriterion[] conditions = {conditionKeyPressed,
                                       conditionKeyReleased};
        wakeupCondition = new WakeupOr(conditions);
        this.wakeupOn(wakeupCondition);
        if(!isThreadStarted){
            stateThread.setPriority(Thread.MAX_PRIORITY);
           stateThread.resume();


            isThreadStarted = true;
        }
    }

    /**
     * changes some variables to enable wake up on elapsed frames to update scene
     * instead of using the thread method, we terminate the thread
     */
    public void setUpdateSceneUsingFrames(){
        transStep = 0.3; //initial speed step.
        rotStep = 0.05;
        WakeupCriterion[] conditions = {conditionKeyPressed,
                                       conditionKeyReleased,
                                       conditionElapsedFrames};
        wakeupCondition = new WakeupOr(conditions);
        this.wakeupOn(wakeupCondition);
        //stop thread
        if(isThreadStarted){
            stateThread.setPriority(Thread.MIN_PRIORITY);
            stateThread.suspend();
            isThreadStarted=false;
            //in release version: thread is destroyed
        }

    }

    /**
     * temporary test function
     */

    void generateSamplePathSimulation(VehicleGeom vehicleGeom) {
        /*
        ArrayList list = new ArrayList();
        for (int i = 0; i < 100; i++) {
            list.add(new Vector3d( -i / 2, 0, 0));
        }
        SimulateList s = new SimulateList("t");
        s.set(vehicleGeom, list, null, null,null,5);
        s.start();
*/
    }

    /**
     * this internal class, froks a thread for the sole rule of updating the
     * scene depending on keyboard strokes, to maintain the same speen over any
     * window size or platform, as alternate to using frame elapsed wake up
     * condition
     */
    class UpdateVehicleUsingKeyStates extends Thread{
        UpdateVehicleUsingKeyStates(){
            super();
        }

        public  void run(){
            while(true){
            if (isMovingForward) {
                       vehicleGeom.pos.set(vehicleGeom.pos.x - Math.cos(vehicleGeom.angles.y) * transStep, vehicleGeom.pos.y,
                              vehicleGeom. pos.z + Math.sin(vehicleGeom.angles.y) * transStep);
                       vehicleGeom.updatePos();
                       spinAngle+=spinStep;
                       vehicleGeom.spinWheels(spinAngle);
                   }
                   if (isMovingBackward) {
                       vehicleGeom.pos.set(vehicleGeom.pos.x + Math.cos(vehicleGeom.angles.y) * transStep, vehicleGeom.pos.y,
                              vehicleGeom.pos.z - Math.sin(vehicleGeom.angles.y) * transStep);
                       vehicleGeom.updatePos();
                       spinAngle-=spinStep;
                       vehicleGeom.spinWheels(spinAngle);

                   }
                   if (isRotatingLeft) {
                       if(SimVals.enableOnline)
                           updateSteer();
                       else{

                           vehicleGeom.angles.y += rotStep;
                           vehicleGeom.updateAngle();
                       }
                   }
                   if (isRotatingRight) {
                       if (SimVals.enableOnline)
                           updateSteer();
                       else{
                           vehicleGeom.angles.y -= rotStep;
                           vehicleGeom.updateAngle();
                       }
                   }
                   if (isSlidingLeft) {
                       vehicleGeom.pos.set(vehicleGeom.pos.x - Math.sin(vehicleGeom.angles.y) * transStep, vehicleGeom.pos.y,
                               vehicleGeom.pos.z - Math.cos(vehicleGeom.angles.y) * transStep);
                       vehicleGeom.updatePos();
                   }
                   if (isSlidingRight) {
                       vehicleGeom.pos.set(vehicleGeom.pos.x + Math.sin(vehicleGeom.angles.y) * transStep, vehicleGeom.pos.y,
                               vehicleGeom.pos.z + Math.cos(vehicleGeom.angles.y) * transStep);
                       vehicleGeom.updatePos();
                   }
                   if(isShiftingDown){
                       vehicleGeom.pos.y-=0.05;
                       vehicleGeom.updatePos();
                   }
                   if(isShiftingUp){
                       vehicleGeom.pos.y+=0.05;
                       vehicleGeom.updatePos();
                   }
                   if(isPushingAhead){
                       vehicleGeom.angles.z+=0.01;
                       vehicleGeom.updateAngle();
                   }
                   if(isPushingback){
                       vehicleGeom.angles.z-=0.01;
                       vehicleGeom.updateAngle();
                   }
                   if(isPushingLeft){
                       vehicleGeom.angles.x-=0.01;
                       vehicleGeom.updateAngle();
                   }
                   if(isPushingRight){
                       vehicleGeom.angles.x+=0.01;
                       vehicleGeom.updateAngle();
                   }

                try {
                    sleep(30); //choose best by testing one loop and calculating time elapsed then
                    //subtract from sample rate to get prefered time
                    //make feed back for time if u like to continually update the elapsed time
                    //incase of complicated scene which might vary the indivisual loop time
                } catch (InterruptedException ex) {
                }
        }
        }
    }
}
