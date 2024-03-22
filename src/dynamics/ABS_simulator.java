package dynamics;




/**
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology Engineering at Damascus University</p>
 * <p>Company: FIT</p>
 * @author Yasser Almohammad(Graphics Design and implementation), Ahmad Zoubi, Usama Rmelawi, Mohammad Askar,Qousai Dabour (vehicle case study, core functionality)
 * @version 1.0
 */

public class ABS_simulator {

    double maxSlip=-0.05;
    double minSlip=-0.15;
    double minVehicleSpeed=11; //11 m == 40Km/Hour
    double minBrakeTorque=0; //11 m == 40Km/Hour
    boolean status=true;//true;
    double releaseDelay=0.05;
    double applyDelay=0.05;
    double time=0;
    double releaseRate=0.2;
    double applyRateSec=0.05;
    double applyRatePri=0.2;
    private static double prevBTorque=0;
    double timeStep=0.01;
    double delta_t=0;
    double minWheelSpeed=11;
    double Btinput;
     double slip;
    double radius=0.3;

  public ABS_simulator() {
  }

  double tireSlipAlgo(double Bt,double Vs,double Vw){
    double Btoutput=0;

    prevBTorque=Bt;

    if (Math.max((Vs/radius),Vw)==0){
       slip=-0.1;
    }
    else {
       slip=(Vw-(Vs/radius))/Math.max((Vs/radius),Vw);
    }
//    System.out.println("slip = "+slip);
    time=+1; /////////////////////////////////////////////

    if (!((Btinput<minBrakeTorque)&(Vs<minVehicleSpeed)&(Vw<minWheelSpeed))){//*
        if(!(slip<minSlip)&!(slip<maxSlip)){//**
           status=true;
           if(time<releaseDelay){//***
              Btoutput=prevBTorque;
           }
           else{//***
              delta_t=time-releaseDelay;
              Btoutput=prevBTorque-(releaseRate*delta_t);
           }
        }
        else{//**
           if(slip<minSlip){//****
              if(status=false){//*****
                 time=0;
                 Btoutput=Btinput;
              }
              else{//*****
                 if (Btinput<prevBTorque){//******
                    time=0;
                    status=false;
                    Btoutput=Btinput;

                 }
                 else{//******
                    if(time<applyDelay){//*******
                       Btoutput=prevBTorque;
                    }
                    else{//*******
                       delta_t=time-applyDelay;
                       Btoutput=prevBTorque+(applyRatePri*delta_t);
                    }
                 }
              }
           }
           if((minSlip<slip)&(slip<maxSlip)){//********
              status=true;
              if(Btinput<prevBTorque){//*********
                 time=0;
                 status=false;
                 Btoutput=Btinput;
              }
              else{//*********
                 if(time < applyDelay){//**********
                    Btoutput=prevBTorque;
                 }
                 else{//**********
                   delta_t=time-applyDelay;
                    Btoutput=prevBTorque+(applyRateSec*delta_t);
                 }
              }
           }
        }
    }
    else{//*
       if((Btinput<minBrakeTorque)||(Vs<minVehicleSpeed)||(Vw<minWheelSpeed)){
          time=0;
          status=false;
          Btoutput=Btinput;
       }
    }

  return Btoutput;
  }


}
