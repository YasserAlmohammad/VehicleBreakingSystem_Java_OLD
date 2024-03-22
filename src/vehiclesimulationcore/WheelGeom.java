package vehiclesimulationcore;

import javax.vecmath.Matrix3d;
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;
import javax.vecmath.Point3d;
import javax.media.j3d.TransformGroup;

/**
 *
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *
 * WheelGeom class:
 * it holds the wheel geometry transform group and the parant transform group which
 * represensts the coordinate system of the wheel located at it's center
 * it also has the dimension and some related data
 * <p>
 * so two Transform groups: enables rotaion arroud it's axon not arround the vehicle center
 */

public class WheelGeom {
    VehicleGeom parent = null;

    /**
     * geom is the transform group that holds the geometry data, so when we need to
     * transform the wheel relative to it's coordinate system we use it
     */
    TransformGroup geom = null;
    /**
     * coordSys is the transform group that represensts the coordinate system of
     * the wheel
     */
    TransformGroup coordinateSys = new TransformGroup();
    Transform3D transform = new Transform3D(); //needed one way or the other

    /**
     * this vector holds the three values of the rotation component arround the three axis
     * <p>the z value is the rotaional step arround it's axon
     * <p>the y value is used to steer left or right
     * <p>the x value is used in the camper angle, load overload
     */
    public Vector3d rotate = new Vector3d(0, 0, 0);
    //to the vehicle geom body center, better for each wheel geom to his own transformation
    private Matrix3d rotMat = new Matrix3d();

    /**
     * we use three matrixes to calculate the rotational matrix, instead of calculating it
     * manually by sine-cos...<p>
     * so when multiple rotating we multiply the right ones and set to the final rotMat
     */
    private Matrix3d rotXMat = new Matrix3d();
    private Matrix3d rotYMat = new Matrix3d();
    private Matrix3d rotZMat = new Matrix3d();


    /**
     * geometry quantities, the geometrical dimensions of the wheel
     */

    public double thickness = 0;
    public double radius = 0;
    /**
     * metalPartRadius, radius of the internal metal part of the wheel, the
     * rubber thivkness is calculated by subtraction, this is used for later use
     * when deformation is considerd
     */
    public double metalPartRadius = 0; //for deformation mesures
    public double rubberPartThicknesss = 0;
    /**
     * distance between the vehicle body and the spring hock attached to it
     * the other side is attached to the wheel center(we assume this)
     */
    public double springHockDistance = 0;
    /**
     * the initial position of the wheel center of geom relative to the vehicle body  center
     */
    Point3d initPos = new Point3d(0, 0, 0);

    /**
     * the initial position of the wheel relative to the vehile centered at 0,0,0
     */
    Point3d initPos2=new Point3d(0,0,0);
    /**
     * center of wheel geom relative to the world
     */
    public Point3d center = new Point3d(0, 0, 0);
    public Point3d hock=new Point3d(0,0,0);
    /**
     * the steering X axis vector relative to the world
     */

    public Vector3d steerAxisVec=new Vector3d(1,0,0);
    public Vector3d verticalVec=new Vector3d(0,-1,0);
    Vector3d extContactPoint=new Vector3d(0,0,0);
    /**
     * the wheel-grund contact point relative to the world
     * you must call updatePoints before getting this member for performance issues
     */
    public Point3d groundContactPoint = new Point3d(0, 0, 0);


    /**
     * the wheel head normalized vector relative to the world
     * you must call updatePoints before getting this member for performance issues
     */

    public Vector3d wheelHead=new Vector3d(-1,0,0);

    /**
     * the lateral vector that is perpendecular to the wheel Head and relative to the world
     */
    public Vector3d wheelHeadVetrical=new Vector3d(0,0,-1);
    /**
     * temp
     */
    private Transform3D trans = new Transform3D();


    /**
     * constructs using a transform group coming from a loader or anything else
     * that generate the geometry
     *
     * @param model TransformGroup holding the geometry data
     */

    public WheelGeom(TransformGroup model) {
        geom = model;
        coordinateSys.addChild(geom);
        geom.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        geom.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        coordinateSys.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        rotXMat.rotX(rotate.x);
        rotYMat.rotY(rotate.y);
        rotZMat.rotZ(rotate.z);
//  houn
        coordinateSys.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    }

    /**
     * wheel geom dimension information
     *
     * @param thick double, thickness
     * @param r double, radius
     * @param metalR double, metal part radius
     */
    public void setDims(double thick, double r, double metalR) {
        thickness = thick;
        radius = r;
        metalPartRadius = metalR;
        rubberPartThicknesss = radius - metalPartRadius;
    }

    /**
     * setTranform, update wheel coordinate system transformgroup, and set the transform local variable
     * this method is better called only when we load the wheel and gather the vehicle parts to
     * set it's coordinate system relative to the vehicle body geom center
     *
     * @param trans Transform3D
     */
    public void setTranform(Transform3D trans) {
        coordinateSys.setTransform(trans); //update translation
    }

    /**
     * reference parent vehicle to get vehicle specific data
     *
     * @param v VehicleGeom
     */
    public void setParentVehicle(VehicleGeom v) {
        parent = v;
    }

    /**
     * rotateWheel, rotate the wheel geometry arround it's parant transform
     * group(coordinateSys) of it's own, rotate arround Y axis
     *
     * @param step double, one step at a time
     */
    public void rotateWheel(double xVal, double yVal, double zVal) { //arround it's vertical plane
        rotate.set(xVal, yVal, zVal);

        rotXMat.rotX(rotate.x);
        rotYMat.rotY(rotate.y);
        rotZMat.rotZ(rotate.z);

        //multiply three
        rotMat.mul(rotXMat, rotYMat);
        rotMat.mul(rotZMat);

        //set rotate part of the transform
        transform.setRotation(rotMat);
        geom.setTransform(transform);
    }

    /**
     * same as above, but passing values inside a vector
     * @param vec Vector3d
     */
    public void rotateWheel(Vector3d vec) {
        rotateWheel(vec.x, vec.y, vec.z);
    }

    /**
     *  just spin the wheel, rotate arround it's axon, without affecting the other components
     * so only the z rotational component is affected
     *
     * @param step double, one step at a time
     */
    public void spinWheel(double zAngle) {
        rotate.z = zAngle;
        rotZMat.rotZ(rotate.z);

        //multiply three
        rotMat.mul(rotXMat, rotYMat);
        rotMat.mul(rotZMat);

        transform.setRotation(rotMat);
        geom.setTransform(transform);

    }

    /**
     *  rotate arround it's vertical plane, without affecting the other components
     * so only the y rotational component is affected
     *
     * @param step double, one step at a time
     */
    public void steerWheel(double yAngle) { //arround it's vertical plane
        rotate.y = yAngle;
        rotYMat.rotY(rotate.y);

        //multiply three
        rotMat.mul(rotXMat, rotYMat);
        rotMat.mul(rotZMat);

        transform.setRotation(rotMat);
        geom.setTransform(transform);
    }


    /**
     * for suspension of the vehicle, the wheel might change it's relative
     * position with it's axel
     *
     * @param step Vector3d, translattion steps
     */
    public void translateWheel(Vector3d step) {
        transform.setTranslation(step);
        geom.setTransform(transform);
    }

    /**
     * because wheel might change it's rotation or translation values due
     * rotation or suspension, we use this to reset it's position back to it's
     * coordinate system origin
     */
    public void resetWheelPos() {
        transform.setIdentity();
        geom.setTransform(transform);
    }


    private Point3d wheelCenter = new Point3d(0, 0, 0);
    private Transform3D wheelCoordSys = new Transform3D();
    private Transform3D hockTrans = new Transform3D();

    /**
     * the distance between wheel geomerty center and the spring hock attatched to
     * the vehicle that represents the suspension mechanisim
     *
     * this is how we do it:
     * we apply the wheel transformations to the 0,0,0 point to get the center
     * we apply the body transformation and a suitable translation to the 0,0,0 to get the
     * hock point relative to the veihcle
     *
     * then we take the sqrt of the subtracted sum of squares for the single components to get
     * the distance between the two points
     * @return double
     */
    public double getCenter_SpringHockDistance() {
        double distance = 0;
        wheelCenter.set(0,0,0);

        coordinateSys.getTransform(wheelCoordSys);
        geom.getTransform(trans);

        wheelCoordSys.mul(trans);
        wheelCoordSys.transform(wheelCenter);
        //so we got the wheel center relative to the vehicle

        //lets get the spring hock relative to the car

        hock.set(0, 0, 0);

        parent.body.geom.getTransform(trans);
        hockTrans.setTranslation(new Vector3d(initPos.x,
                                              -(parent.body.bodyHeight / 2 -
                                                springHockDistance), initPos.z));
        trans.mul(hockTrans);

        trans.transform(hock);
// biardak

        distance = Math.sqrt(Math.abs((hock.x * hock.x + hock.y * hock.y +
                                       hock.z * hock.z) -
                                      (wheelCenter.x * wheelCenter.x +
                                       wheelCenter.y * wheelCenter.y +
                                       wheelCenter.z * wheelCenter.z)));
        return distance;
    }

    private Transform3D coordsTrans = new Transform3D();
    /**
     * returns and sets the center of the wheel geometry
     * we compose transforms to get the center point
     * @return Point3d
     */
    public Point3d getCenterRelative2World() {
        center.set(0, 0, 0);
        parent.vehicleTG.getTransform(trans);
        coordinateSys.getTransform(coordsTrans);
        trans.mul(coordsTrans);
        trans.mul(transform);
        trans.transform(center);
     //   UpdateWheelGroundContactPointInfo(0,0);
    //    System.out.println("center="+center.x+","+center.y+","+center.z);
        return center;
    }


    Transform3D edgeTrans = new Transform3D();
    Transform3D rotTrans = new Transform3D();

    /**
     * updates wheel ground contact point relative to the world coord sys also it updates the
     * wheel head vector
     * you need to pass the camber and steering angle
     * this is how we do it:
     *
     * initially the wheel head vector has the value (-1,0,0)
     * we get the wheel center then translate it to the bottom edge then we apply two rotations
     * the camber and steering to the point
     *
     * @param camperAngle double
     * @param steerAngle double
     * @return Point3d
     */
    public void UpdateWheelGroundContactPointInfo(double camberAngle,
                                              double steerAngle) {
    wheelHeadVetrical.set(0,0,-1);
    wheelHead.set(-1,0,0);

    groundContactPoint.set(0, 0, 0);
        parent.vehicleTG.getTransform(trans);

        coordinateSys.getTransform(coordsTrans);
        trans.mul(coordsTrans);

        edgeTrans.setTranslation(new Vector3d(0, -radius, 0)); //go to edge
        trans.mul(edgeTrans); //go to edge

        //now apply rotation to the edge in two axises only

        rotXMat.rotX(camberAngle);
        rotYMat.rotY(steerAngle);
        rotZMat.rotZ(0);
        rotMat.mul(rotXMat,rotYMat);
        rotMat.mul(rotZMat);

        rotTrans.setRotation(rotMat);
        trans.mul(rotTrans); //rotate
        trans.transform(wheelHead); //transform the wheel head vector
        trans.transform(wheelHeadVetrical);
        trans.transform(groundContactPoint); //transform the wheel-ground contact point

    //    System.out.println("edge="+groundContactPoint.x+","+groundContactPoint.y+","+groundContactPoint.z);
     //   System.out.println("vec="+wheelHead.x+","+wheelHead.y+","+wheelHead.z);
    }

    private Transform3D temp=new Transform3D();
    private Vector3d wheelSticking=new Vector3d(0,0,0);
    /**
     * to make wheel stick with the ground after a transformation is applied to the vehicle
     */
    public void fixPos(){
        extContactPoint.set(0,0,0);

        getCenterRelative2World();
        this.getHock();
        this.getVetricalVec();

        double distance=0;
        if(this.verticalVec.y!=0)
            distance=hock.y/this.verticalVec.y;
        else
            distance=hock.y;
        wheelSticking.set(initPos.x,distance+initPos2.y*2+radius/2.5,initPos.z);
        temp.setTranslation(wheelSticking);



        setTranform(temp);
//        Transform3D coco = new Transform3D();
//        Vector3d v = new Vector3d();
//        v.set(0.0,-1,0.0);
//        temp.m
//        coco.setTranslation();
    }
    public void fixPos(double hock,double verVec){
        extContactPoint.set(0,0,0);

        getCenterRelative2World();
        double distance=0;
        if(verVec!=0)
            distance=hock/verVec;
        else
            distance=hock;
        wheelSticking.set(initPos.x,distance+initPos2.y*2+radius/2.5,initPos.z);
        temp.setTranslation(wheelSticking);
        setTranform(temp);

    }
    /**
     * get the steering X axis relative to the world
     * @return Vector3d
     */
    public Vector3d getSteerAxisVec(){
        steerAxisVec.set(-1,0,0);
       parent.vehicleTG.getTransform(trans);
       coordinateSys.getTransform(coordsTrans);
       trans.mul(coordsTrans);

       trans.transform(steerAxisVec);
       return steerAxisVec;
    }

    /**
     * get the hock point relative to the world
     * @return Point3d
     */
    private Transform3D bodyTrans=new Transform3D();
    private Vector3d hockTranslation=new Vector3d(0,0,0);

    /**
     * returns and updates the hock point that the spring is attached to
     * @return Point3d
     */
    public Point3d getHock(){
        hock.set(0,0,0);
        parent.vehicleTG.getTransform(trans);
        parent.body.geom.getTransform(bodyTrans);
        trans.mul(bodyTrans);
        hockTranslation.set(initPos.x,-(parent.body.bodyHeight / 2 -springHockDistance), initPos.z);
        hockTrans.setTranslation(hockTranslation);
        trans.mul(hockTrans);
        trans.transform(hock);

        return hock;
    }

    /**
     * returns the vertical Y vector of the wheel to get it's intersection with ground
     * @return Vector3d
     */
    public Vector3d getVetricalVec(){
       verticalVec.set(0,-1,0);
       parent.vehicleTG.getTransform(trans);
       coordinateSys.getTransform(coordsTrans);
       trans.mul(coordsTrans);
       trans.transform(verticalVec);

       return verticalVec;
    }


}
