package vehiclesimulationcore;

import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Quat4d;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.TransformGroup;

/**
 *
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 * body geomerty holds a branch group of it's body components and additional dim data
 * in general the body is not changed, because we change it's parent transform group
 * how ever when enabling the suspension mechanics, we'll change it relatively to the whole vehicle
 *
 * rotation of the body is not allowed yet from inside this class
 */

class BodyGeom {
    TransformGroup geom=null;
    //quantities
    double bodyLength=0;
    double bodyWidth=0;
    double bodyHeight=0;

    /**
     * position is changed in suspension
     */
    Vector3d pos=new Vector3d(0,0,0);
    Transform3D transform=new Transform3D();

    /**
     * we usually don't call this method
     */
    public BodyGeom(){
        geom=new TransformGroup();
        bodyHeight=0;
        bodyLength=0;
        bodyWidth=0;
    }

    /**
     * passing the body as a transform group(which is most likely)
     *
     * @param model TransformGroup
     * @param width double
     * @param length double
     * @param height double
     */
    public BodyGeom(TransformGroup model, double length,double width, double height) {
        geom=model;
        bodyHeight=height;
        bodyLength=length;
        bodyWidth=width;

        geom.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        geom.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    }

    /**
     * passing a body as a BranchGroup we create this object under the body transform group
     *
     * @param model BranchGroup
     * @param width double
     * @param length double
     * @param height double
     */
    public BodyGeom(BranchGroup model,double width, double length, double height) {
        geom=new TransformGroup();
        geom.addChild(model);
        bodyHeight=height;
        bodyLength=length;
        bodyWidth=width;
        geom.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        geom.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    }
    //main attributes of the body
    public void setDims(double width,double length,double height, double frontWheel){
        bodyHeight=height;
        bodyLength=length;
        bodyWidth=width;
    }
    public void setGeom(TransformGroup model){
        geom=model;
    }

    /**
     * change the position of the body relatively to the car: called when suspension is enabled
     * <p>
     * in general how ever we don't rotate the body alone so we wont make a rotational component
     * for the body alone
     *
     * @param pos Vector3d
     */
    public void setPos(Vector3d pos){
        this.pos=pos;
        transform.setTranslation(pos);
        geom.setTransform(transform);
    }

    /**
     * rotation matrix of the body relativge to the vehicle
     * @param mat Matrix3d
     */
    public void rotate(Matrix3d mat){
        transform.setRotation(mat);
        geom.setTransform(transform);
    }

    /**
     * set rotation quaternion for the body relative to the vehicle
     * @param q Quate4d
     */
    public void rotate(Quat4d q){
        transform.setRotation(q);
        geom.setTransform(transform);
    }

}
