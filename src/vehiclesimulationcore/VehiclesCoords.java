package vehiclesimulationcore;

import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;
import java.lang.String;
import javax.vecmath.Quat4d;
import javax.vecmath.Matrix3d;

/**
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *<p>
 * model specific coords currenlty they are taken fron 3DSMAX
 * assuming model head towards negative X, perpendecular with z, and y is up
 *<p>
 * data specific information, path info for model files...
 * the same data could be written in outside file and load new models
 * <p>
 * this class takes care of loading the different vehicle models using the appropriate
 * coords
 *
 * center of the geomerty
 *
 * models initially are centered in the world they are loaded from from so we need to make few translations
 * to position them in their right structures
 *
 */
public class VehiclesCoords {
    public VehiclesCoords() {
    }

    /**
     *
     * <p>Title: Vehicle Dynamics Simulation Program</p>
     *
     * <p>Description: vehicle dynmaics, braking system simulation</p>
     *
     * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
     * Engineering at Damascus University</p>
     * this internal class is the case to load the first model SPORTVW model
     */
    public static class SportTVW {
        SportTVW() {
        }

        static int code = 0; //model code

        static double BHeight=1.36;

        //wheel geom coordinates in meters
        static double radius = 0.30;
        static double thickness = 0.3;
        static double metalRadius = 0;


        static double FLWheelX = 1.378;
        static double FRWheelX = FLWheelX;
        static double BLWheelX = -1.189;
        static double BRWheelX = BLWheelX;

        static double FLWheelY =radius;
        static double FRWheelY = radius;
        static double BLWheelY = radius;
        static double BRWheelY = radius;

        static double FLWheelZ = -0.788;
        static double FRWheelZ = -FLWheelZ;
        static double BLWheelZ = FLWheelZ;
        static double BRWheelZ = FRWheelZ;


        //distance between initial wheel axis and body base
        static double axisBaseDistance=radius/2.5; //initial
        static double springBaseDistance=axisBaseDistance*2;


        //vehicle geom coords
        static double VLength = 4;
        static double VWidth = 1.6;
        static double VHeight =1.36+radius-axisBaseDistance; //initial height body and wheels considered


        //path info
        static String dir = "models";
        static String bodyFileName = "sporttvw";
        static String wheelFileName = "sporttvw_wheel";
        static String fileName = "models\\sporttvw.3ds";

    }


    /**
     * providing a suitable code we generate the appropriate vehicle geometry model
     * 0 for SportTVW with full options
     * 1 for SportTVW with less details, no tectures, no transparancy, optimized wheels
     * <p>
     * more codes to come for new models other than the SportTVW
     */
    public static VehicleGeom createVehicleGeom(int modelCode) {
        VehicleGeom model = null;
        BodyGeom body=null;

        WheelGeom FLWheel=null;
        WheelGeom FRWheel =null;
        WheelGeom BLWheel=null;
        WheelGeom BRWheel=null;
        Transform3D trans=new Transform3D();

        switch (modelCode) {
        case 0:

            //   BodyGeom body=new BodyGeom(Loaders.loadASE(SportTVW.dir,SportTVW.bodyFileName),
            //                              SportTVW.VWidth,SportTVW.VHeight,SportTVW.VHeight);
            //     BodyGeom body=new BodyGeom(Loaders.loadObj("E:\\FINAL\\5thYearProect\\java3dtests\\vehicleSimulation\\models\\sporttvw.obj").getSceneGroup(),SportTVW.VWidth,SportTVW.VHeight,SportTVW.VHeight);
            body = new BodyGeom(Loaders.load3DS(SportTVW.fileName),
                                         SportTVW.VLength, SportTVW.VWidth,
                                         SportTVW.BHeight);

            //         WheelGeom FLWheel=new WheelGeom(Loaders.loadASE(SportTVW.dir,SportTVW.wheelFileName));

            //now we translate it to it's position above the wheels
            //we consider it's first positioned in 0,0,0 in the center
            trans.setTranslation(new Vector3d(0,SportTVW.BHeight/2+SportTVW.radius-SportTVW.axisBaseDistance,0));
            Quat4d q = new Quat4d();
            q.set(0.0,1.0,0.0,0.0);
            trans.setRotation(q);
            body.geom.setTransform(trans);
            body.geom.getTransform(body.transform);

//*********************
            FLWheel = new WheelGeom(Loaders.load3DS(
                            "models\\sporttvw_wheel.3ds"));
                    FLWheel.setDims(SportTVW.radius, SportTVW.thickness,
                                    SportTVW.metalRadius);
                    trans.setTranslation(new Vector3d(SportTVW.FLWheelX,
                                                      SportTVW.FLWheelY,
                                                      SportTVW.FLWheelZ));
                    FLWheel.setTranform(trans);

                    //************
                    Transform3D transholder = new Transform3D();
                    Transform3D tr = new Transform3D();
                    transholder.set(trans);
                    tr.rotY(Math.PI);
                    transholder.mul(tr);
                    FLWheel.setTranform(transholder);
                    //************
                    FLWheel.springHockDistance=SportTVW.springBaseDistance;
                    //relative to the body center of geom
                    FLWheel.initPos.set(SportTVW.FLWheelX,-SportTVW.axisBaseDistance,SportTVW.FLWheelZ);
                    FLWheel.initPos2.set(SportTVW.FLWheelX,SportTVW.FLWheelY,SportTVW.FLWheelZ);




                    FRWheel = new WheelGeom(Loaders.load3DS(
                            "models\\sporttvw_wheel.3ds"));
                    FRWheel.setDims(SportTVW.radius, SportTVW.thickness,
                                    SportTVW.metalRadius);
                    trans.setTranslation(new Vector3d(SportTVW.FRWheelX,
                                                      SportTVW.FRWheelY,
                                                      SportTVW.FRWheelZ));
                    FRWheel.setTranform(trans);

                    //************
//                    Transform3D transholder = new Transform3D();
//                    Transform3D tr = new Transform3D();
                    transholder.set(trans);
                    tr.rotY(Math.PI);
                    transholder.mul(tr);
                    FRWheel.setTranform(transholder);
                    //************

                    FRWheel.springHockDistance=SportTVW.springBaseDistance;
                    //relative to the body center of geom
                    FRWheel.initPos.set(SportTVW.FRWheelX,-SportTVW.axisBaseDistance,SportTVW.FRWheelZ);
                    FRWheel.initPos2.set(SportTVW.FRWheelX,SportTVW.FRWheelY,SportTVW.FRWheelZ);



                    BLWheel = new WheelGeom(Loaders.load3DS(
                            "models\\sporttvw_wheel.3ds"));
                    BLWheel.setDims(SportTVW.radius, SportTVW.thickness,
                                    SportTVW.metalRadius);
                    trans.setTranslation(new Vector3d(SportTVW.BLWheelX,
                                                      SportTVW.BLWheelY,
                                                      SportTVW.BLWheelZ));

                    BLWheel.setTranform(trans);
                    //************
//                    Transform3D transholder = new Transform3D();
//                    Transform3D tr = new Transform3D();
                    transholder.set(trans);
                    tr.rotY(Math.PI);
                    transholder.mul(tr);
                    BLWheel.setTranform(transholder);
                    //************
                    BLWheel.springHockDistance=SportTVW.springBaseDistance;
                    //relative to the body center of geom
                    BLWheel.initPos.set(SportTVW.BLWheelX,-SportTVW.axisBaseDistance,SportTVW.BLWheelZ);
                    BLWheel.initPos2.set(SportTVW.BLWheelX,SportTVW.BLWheelY,SportTVW.BLWheelZ);


                    BRWheel = new WheelGeom(Loaders.load3DS(
                            "models\\sporttvw_wheel.3ds"));
                    BRWheel.setDims(SportTVW.radius, SportTVW.thickness,
                                    SportTVW.metalRadius);
                    trans.setTranslation(new Vector3d(SportTVW.BRWheelX,
                                                      SportTVW.BRWheelY,
                                                      SportTVW.BRWheelZ));
                    BRWheel.setTranform(trans);
                    //************
//                    Transform3D transholder = new Transform3D();
//                    Transform3D tr = new Transform3D();
                    transholder.set(trans);
                    tr.rotY(Math.PI);
                    transholder.mul(tr);
                    BRWheel.setTranform(transholder);
                    //************
                    BRWheel.springHockDistance=SportTVW.springBaseDistance;
                    //relative to the body center of geom
                    BRWheel.initPos.set(SportTVW.BRWheelX,-SportTVW.axisBaseDistance,SportTVW.BRWheelZ);
                    BRWheel.initPos2.set(SportTVW.BRWheelX,SportTVW.BRWheelY,SportTVW.BRWheelZ);



                    model = new VehicleGeom(body, FLWheel, BLWheel, FRWheel, BRWheel);
                    model.setDims(SportTVW.VLength,SportTVW.VWidth,SportTVW.VHeight);

                    BRWheel.setParentVehicle(model);
                    FLWheel.setParentVehicle(model);
                    FRWheel.setParentVehicle(model);
                    BLWheel.setParentVehicle(model);

//                    Transform3D toto = new Transform3D();
//                    Vector3d v = new Vector3d();
//                    v.set(0,0,1);
//                    toto.setTranslation(v);
//
//                    FLWheel.geom.setTransform(toto);

            break;

        case 1: //the same as previous model but optimized
            body = new BodyGeom(Loaders.load3DS("models\\sporttvw_optimized.3ds",false),
                                         SportTVW.VLength, SportTVW.VWidth,
                                         SportTVW.BHeight);
            //now we translate it to it's position above the wheels
            //we consider it's first positioned in 0,0,0 in the center
            trans.setTranslation(new Vector3d(0,SportTVW.BHeight/2+SportTVW.radius-SportTVW.axisBaseDistance,0));
            body.geom.setTransform(trans);

            body.geom.getTransform(body.transform);

            FLWheel = new WheelGeom(Loaders.load3DS(
                    "models\\sporttvw_wheel_optimized.3ds",false));
            FLWheel.setDims(SportTVW.radius, SportTVW.thickness,
                            SportTVW.metalRadius);
            trans.setTranslation(new Vector3d(SportTVW.FLWheelX,
                                              SportTVW.FLWheelY,
                                              SportTVW.FLWheelZ));
            FLWheel.setTranform(trans);

            FLWheel.springHockDistance=SportTVW.springBaseDistance;
            //relative to the body center of geom
            FLWheel.initPos.set(SportTVW.FLWheelX,-SportTVW.axisBaseDistance,SportTVW.FLWheelZ);
            FLWheel.initPos2.set(SportTVW.FLWheelX,SportTVW.FLWheelY,SportTVW.FLWheelZ);


            FRWheel = new WheelGeom(Loaders.load3DS("models\\sporttvw_wheel_optimized.3ds",false));
            FRWheel.setDims(SportTVW.radius, SportTVW.thickness,
                            SportTVW.metalRadius);
            trans.setTranslation(new Vector3d(SportTVW.FRWheelX,
                                              SportTVW.FRWheelY,
                                              SportTVW.FRWheelZ));
            FRWheel.setTranform(trans);
            FRWheel.springHockDistance=SportTVW.springBaseDistance;
            //relative to the body center of geom
            FRWheel.initPos.set(SportTVW.FRWheelX,-SportTVW.axisBaseDistance,SportTVW.FRWheelZ);
            FRWheel.initPos2.set(SportTVW.FRWheelX,SportTVW.FRWheelY,SportTVW.FRWheelZ);



            BLWheel = new WheelGeom(Loaders.load3DS("models\\sporttvw_wheel_optimized.3ds",false));
            BLWheel.setDims(SportTVW.radius, SportTVW.thickness,
                            SportTVW.metalRadius);
            trans.setTranslation(new Vector3d(SportTVW.BLWheelX,
                                              SportTVW.BLWheelY,
                                              SportTVW.BLWheelZ));
            BLWheel.setTranform(trans);
            BLWheel.springHockDistance=SportTVW.springBaseDistance;
            //relative to the body center of geom
            BLWheel.initPos.set(SportTVW.BLWheelX,-SportTVW.axisBaseDistance,SportTVW.BLWheelZ);
            BLWheel.initPos2.set(SportTVW.BLWheelX,SportTVW.BLWheelY,SportTVW.BLWheelZ);


            BRWheel = new WheelGeom(Loaders.load3DS(
                    "models\\sporttvw_wheel_optimized.3ds",false));
            BRWheel.setDims(SportTVW.radius, SportTVW.thickness,
                            SportTVW.metalRadius);
            trans.setTranslation(new Vector3d(SportTVW.BLWheelX,
                                              SportTVW.BRWheelY,
                                              SportTVW.BRWheelZ));
            BRWheel.setTranform(trans);

            BRWheel.springHockDistance=SportTVW.springBaseDistance;
            //relative to the body center of geom
            BRWheel.initPos.set(SportTVW.BRWheelX,-SportTVW.axisBaseDistance,SportTVW.BRWheelZ);
            BRWheel.initPos2.set(SportTVW.BRWheelX,SportTVW.BRWheelY,SportTVW.BRWheelZ);


            model = new VehicleGeom(body, FLWheel, BLWheel, FRWheel, BRWheel);
            model.setDims(SportTVW.VLength,SportTVW.VWidth,SportTVW.VHeight);


            BRWheel.setParentVehicle(model);
            FLWheel.setParentVehicle(model);
            FRWheel.setParentVehicle(model);
            BLWheel.setParentVehicle(model);


            break;

        }

        return model;
    }
}
