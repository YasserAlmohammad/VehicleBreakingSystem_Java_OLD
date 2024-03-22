package vehiclesimulationcore;

import javax.media.j3d.TransformGroup;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Quat4d;
import javax.media.j3d.BoundingSphere;
import java.util.LinkedList;
import javax.media.j3d.BranchGroup;

/**
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *
 * <hr>
 * <P>
 * VehicleGeom constsis of two classes: BodyGeom and WheelGeom<br>
 * this class holds information and data of the vehicle geometry and responsible<br>
 * for all transformations application on it through applying appropriate behaviors<br>
 * separation of the vehicle model into two component classes is better for organizational
 * issue so the VehicleGeom wont be a miss with so many feilds and members
 * <p>
 * the vehicle has one TransformGroup for it's body ( and thus a it's own coordinate system you can say)
 * and a TransformGroup for each wheel( and thus a separate coordinate sytem for each wheel)
 * wheels are relative to the body, the body is relative to world coordinates
 * <p>
 * we assign a behaviour with the vehicle to enable crusiing using the keyboard
 */
public class VehicleGeom {
    public BodyGeom body = null;
    public WheelGeom FLWheel = null; //left front wheel
    public WheelGeom BLWheel = null; //left back wheel
    public WheelGeom FRWheel = null; //right front wheen
    public WheelGeom BRWheel = null; //right Back wheel
    //four wheels, each one may have different attributes
    public TransformGroup vehicleTG = new TransformGroup(); //holds the whole vehicle parts
    public Transform3D transform = new Transform3D();
    public KeyboardBehaviours navigator = null;

    /**
     * position of the vehicle
     */
    public Vector3d pos = new Vector3d(0, 0, 0);
    /**
     * geomerty dimensions
     */
    double length = 0;
    double width = 0;
    double height = 0;
    /**
     * rotational angles arround the three axises
     */
    Vector3d angles = new Vector3d(0, 0, 0);
    Point3d FRCornerCoords = new Point3d();
    Point3d FLCornerCoords = new Point3d();
    Point3d BRCornerCoords = new Point3d();
    Point3d BLCornerCoords = new Point3d();
    /**
     * rotation matrix to update the rotational component of the vehicle without touching others
     * this matrix is the multiplication of three rotational matrixes in the three axises
     */
    private Matrix3d rotMat = new Matrix3d();
    /**
     * single matrix rotational components
     */
    private Matrix3d rotXMat = new Matrix3d();
    private Matrix3d rotYMat = new Matrix3d();
    private Matrix3d rotZMat = new Matrix3d();

    /**
     * passing a Body geometry object and a geomerty object for each wheel we ensemble
     * the vehicle, we also create the behaviour
     * <p>
     * body and wheel are loaded the Loader class and well ensembled using the VehicleCoords class
     *
     * @param bodyGeom BodyGeom
     * @param LF WheelGeom
     * @param LB WheelGeom
     * @param RF WheelGeom
     * @param RB WheelGeom
     */
    public VehicleGeom(BodyGeom bodyGeom, WheelGeom LF, WheelGeom LB,
                       WheelGeom RF, WheelGeom RB) {
        body = bodyGeom;
        FLWheel = LF;
        BLWheel = LB;
        FRWheel = RF;
        BRWheel = RB;
        gatherParts();

        //allow changing the transformation properties
        vehicleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        vehicleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        navigator = new KeyboardBehaviours(this);
        navigator.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000));
        navigator.setVehicleGeom(this); //temp
        vehicleTG.addChild(navigator);

        rotXMat.rotX(0);
        rotYMat.rotY(0);
        rotZMat.rotZ(0);
    }

    /**
     * to set the body geom dimensions
     * @param l double  the length of the vehicle
     * @param w double  width of it
     * @param h double  it's height
     */
    public void setDims(double l, double w, double h) {
        length = l;
        width = w;
        height = h;
    }

    /**
     * if you want to switch parts
     */

    void setBodyGeom(BodyGeom geom) {
        body = geom;
    }

    /**
     * initially all wheels have the same shape, and thus are loaded from
     * the same model
     */
    void setWheelsGeom(WheelGeom LF, WheelGeom LB, WheelGeom RF, WheelGeom RB) {
        FLWheel = LF;
        BLWheel = LB;
        FRWheel = RF;
        BRWheel = RB;
    }

    /**
     * gather partial geometries into one parant transform group to make the vehicle
     */
    void gatherParts() {
        vehicleTG.addChild(body.geom);
        vehicleTG.addChild(FLWheel.coordinateSys);
        vehicleTG.addChild(BLWheel.coordinateSys);
        vehicleTG.addChild(FRWheel.coordinateSys);
        vehicleTG.addChild(BRWheel.coordinateSys);
    }

    /**
     * just rotate the front wheels with the same angle arround thier vertical axis
     * so just call the wheel's functions
     * @param angle double
     */
    public void rotateFrontWheels(double angle) {
        FLWheel.steerWheel(angle);
        FRWheel.steerWheel(angle);
        updateCorners();
    }

    /**
     * issue a rotate command rotate in absolute, assuming the at init position
     * the vehicle was at 0,0,0 and looking towards the negative X axis
     * we pass a vector of 3 values representing the rotation angles arround x-y-z
     * using this method we rotate the vehicle starting from the original rotation angle
     * we only change this component
     *<p>
     * the way we do this is: for each angle we got a rotation matrix 3 X 3 matrix
     * which is set to the rotation matrix arround it's axis, then we multiply
     * these three matixes into one final which holds the required effect
     * <p>
     * we could provide another method for relative rotation staring by current angles
     */

    public void rotateVehicle(Vector3d angles) {
        this.angles = angles;

        rotXMat.rotX(angles.x);
        rotYMat.rotY(angles.y);
        rotZMat.rotZ(angles.z);

        //multiply matrixes
        rotMat.mul(rotXMat, rotYMat);
        rotMat.mul(rotZMat);

        transform.setRotation(rotMat);
        vehicleTG.setTransform(transform);
        //update camera if binded
        Camera.updatePos(pos, angles);
         updateCorners();
    }

    /**
     * this method enables setting the rotation matrix of the vehicle transformgroup
     * directly without setting angles of rotation
     *
     * this generates a problem that we wont be able to change the camera angle proberly
     * unless we were able to get the angles from the rotation matrix.
     *
     * by try and error (low graphics experience) when we get the Quat3d (q)for the rotation
     * matrix, we got sqr(q.y)+sqr(q,w) = 1 so it's defentaely angles and by taking the formula
     * arcsin(q.y)*2 we got the same angle we used in rotation (we do rotation here arround
     * Y only this is why it probably worked)
     *
     * as we said, we need this angle to update the camera proberly
     *
     * @param mat Matrix3d
     *
     */
    public void rotateVehicle(Matrix3d mat) {
        transform.setRotation(mat);
        vehicleTG.setTransform(transform);

        //get angle
        Quat4d q = new Quat4d();
        transform.get(q);
        angles.y = Math.asin(q.y) * 2; //try and error
        Camera.updatePos(pos, angles);
        updateCorners();
    }

    /**
     * passing a quaternion representation of the rotation matrix we issue a rotate command
     * @param q Quat4d
     */
    public void rotateVehicle(Quat4d q) {
        transform.set(q);
        vehicleTG.setTransform(transform);

        angles.y = Math.asin(q.y) * 2; //try and error
        Camera.updatePos(pos, angles);
         updateCorners();
    }


    /**
     * get vehicle body front right corner coords relative to the world coords
     * we do it in this fucking way:
     * first we consider the point at 0,0,0 then translate it to it's corner then apply
     * the body transformation then vehicle transformation to this point so we get it
     * @return Vector3d
     */
    public Point3d getFRCornerBodyCoords() {
        Point3d corner = new Point3d(0, 0, 0);

        Transform3D trans = new Transform3D();
        Vector3d transVec = new Vector3d( -this.length / 2, -body.bodyHeight/2, -this.width / 2);

        trans.setTranslation(transVec);
        trans.mul(body.transform);
        trans.mul(transform);

        trans.transform(corner);
        FRCornerCoords = corner;
        return corner;
    }

    /**
     * get vehicle body front right corner coords relative to the world coords
     * we do it in this fucking way:
     * first we consider the point at 0,0,0 then translate it to it's corner then apply
     * the body transformation then vehicle transformation to this point so we get it
     * @return Vector3d
     */
    public Point3d getFLCornerBodyCoords() {
        Point3d corner = new Point3d(0, 0, 0);

        Transform3D trans = new Transform3D();
        Vector3d transVec = new Vector3d( -this.length / 2, -body.bodyHeight/2, this.width / 2);

        trans.setTranslation(transVec);
        trans.mul(body.transform);
        trans.mul(transform);

        trans.transform(corner);
        FLCornerCoords = corner;
        return corner;
    }

    /**
     * get vehicle body back left corner coords relative to the world coords
     * we do it in this fucking way:
     * first we consider the point at 0,0,0 then translate it to it's corner then apply
     * the body transformation then vehicle transformation to this point so we get it
     * @return Vector3d
     */
    public Point3d getBLCornerBodyCoords() {
        Point3d corner = new Point3d(0, 0, 0);

        Transform3D trans = new Transform3D();
        Vector3d transVec = new Vector3d( this.length / 2,-body.bodyHeight/2, this.width / 2);

        trans.setTranslation(transVec);
        trans.mul(body.transform);
        trans.mul(transform);

        trans.transform(corner);
        BLCornerCoords = corner;
        return corner;
    }


    /**
     * get vehicle body back right corner coords relative to the world coords
     * we do it in this fucking way:
     * first we consider the point at 0,0,0 then translate it to it's corner then apply
     * the body transformation then vehicle transformation to this point so we get it
     * @return Vector3d
     */
    public Point3d getBRCornerBodyCoords() {
        Point3d corner = new Point3d(0, 0, 0);

        Transform3D trans = new Transform3D();
        Vector3d transVec = new Vector3d( this.length / 2, -body.bodyHeight/2, -this.width / 2);

        trans.setTranslation(transVec);
        body.geom.getTransform(body.transform);
        trans.mul(body.transform);
        trans.mul(transform);

        trans.transform(corner);
        BRCornerCoords = corner;
        return corner;
    }

    /**
     * just call to update the 4 corners coords also other vehicle specific point positions
     * reltaive to the world
     *
     * first we assume the wheels always stick to the ground so to achieve this we'll do the following
     * after current vehicle update we check the position of each wheel contact point(the supposed one)
     * if it was above the ground we tranlate the wheel with the same x-z values of center and y value
     * of the subtraction from 0
     */
    public void updateCorners(){

        FRWheel.getCenterRelative2World();
        FLWheel.getCenterRelative2World();
        BRWheel.getCenterRelative2World();
        BLWheel.getCenterRelative2World();
        //**********
        double FRh = FRWheel.getHock().y;
        double FLh = FLWheel.getHock().y;
        double BRh = BRWheel.getHock().y;
        double BLh = BLWheel.getHock().y;

        double FRv = FRWheel.getVetricalVec().y;
        double FLv = FLWheel.getVetricalVec().y;
        double BRv = BRWheel.getVetricalVec().y;
        double BLv = BLWheel.getVetricalVec().y;

        FRWheel.fixPos(BLh,BLv);
        FLWheel.fixPos(BRh,BRv);
        BRWheel.fixPos(FLh,FLv);
        BLWheel.fixPos(FRh,FRv);
        //*********

//        FRWheel.fixPos();
//        FLWheel.fixPos();
//        BRWheel.fixPos();
//        BLWheel.fixPos();


     }


    /**
     * update the vehicle position, also the binded camera
     * @param pos Vector3d
     */
    public void setPos(Vector3d pos) {
        this.pos = pos;
        transform.setTranslation(pos);
        vehicleTG.setTransform(transform);
        //update camera if binded
        Camera.updatePos(pos, angles);
        updateCorners();
    }

    /**
     * update position, when the position value is set alone
     */
    public void updatePos() {
        this.setPos(pos);
    }

    /**
     * update the angle of vehicle when angle is changed directly
     */
    public void updateAngle() {
        this.rotateVehicle(angles);
    }

    /**
     * update both poistion and angle
     */
    public void updateCoords() {
        this.setCoords(pos, angles);
    }

    /**
     * spin all wheels arround their axon with the same angle(unify spining)
     * we call the 4 wheels relating methods
     * @param angle double
     */
    public void spinWheels(double angle) {
        FLWheel.spinWheel(angle);
        BLWheel.spinWheel(angle);
        FRWheel.spinWheel(angle);
        BRWheel.spinWheel(angle);
        updateCorners();
    }

    /**
     * merge all changes in one transform and apply it once instead of applying in many
     * we set the transform to the poition and rotation matrix then apply it to the vehicle
     *
     * @param pos Vector3d
     * @param ang double
     */
    public void setCoords(Vector3d pos, Vector3d ang) {
        angles = ang;

        rotXMat.rotX(angles.x);
        rotYMat.rotY(angles.y);
        rotZMat.rotZ(angles.z);

        //multiply matrixes
        rotMat.mul(rotXMat, rotYMat);
        rotMat.mul(rotZMat);

        transform.setRotation(rotMat);

        this.pos = pos;
        transform.setTranslation(pos);
        vehicleTG.setTransform(transform);
        //update camera if binded
        Camera.updatePos(pos, angles);
        updateCorners();

    }

    /**
     *
     * @param pos Vector3d
     * @param ang Vector3d
     */
    public void setCoords(Vector3d pos, Quat4d ang) {
        transform.setRotation(ang);
        this.pos = pos;
        transform.setTranslation(pos);
        vehicleTG.setTransform(transform);
        //update camera if binded
        Camera.updatePos(pos, angles);
        updateCorners();
    }

    /**
     * bind the camera to this vehicle, and update it's poition-look at and up vectors
     * the default camera view is TVPos1: behind and looking at the vehicle
     * @param bind boolean
     */
    public void bindCamera(boolean bind) {
        Camera.isCameraBinded = bind;
        Camera.setTVPos1CameraType();
        Camera.updatePos(pos, angles);
    }

    /**
     * reset the vehicle position and angle to 0s
     */

    public void reset() {
        this.setPos(new Vector3d(0, 0, 0));
        this.rotateVehicle(new Vector3d(0, 0, 0));

    }


}


