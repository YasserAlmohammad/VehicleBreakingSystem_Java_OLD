package vehiclesimulationcore;

public class ABSalgorithms{
    static double minSlip=-0.05;
    static double maxSlip=-0.15;
    static double minVehicleSpeed=11; //11 m == 40Km/Hour
    static double minBrakeTorque=0; //11 m == 40Km/Hour
    static double slipMargin=0.05;
    static boolean status=true;
    static double multiplier= 1;
    static double releaseDelay=0.05;
    static double applyDelay=0.05;
    static double time=0;
    static double releaseRate=0.05;
    static double applyRateSec=0.05;
    static double applyRatePri=0.2;
    private static double prevBTorque=0;
    static double timeStep=0.01;
    ABSalgorithms(){
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void setSlipP(double slipP){
        minSlip=slipP+slipMargin; //negative slip
        maxSlip=slipP-slipMargin;
    }
    public static void setTimeStep(double ts){
        releaseDelay=applyDelay=ts*multiplier;
    }

    public static double tireSlipAlgorithm(double bTorque, double vehicleSpeed, double slip){
        //check for validy
        time+=timeStep;
        if(!((vehicleSpeed>minVehicleSpeed)&&(bTorque>minBrakeTorque))){
            time =0;
            return 0;
        }
        //slip is negative for braking
        if(slip<=maxSlip){   //absolute value is begger  -0.5<=-0.2
            System.out.println("case3");
            status=true;
            if(time<releaseDelay){
                return 0;
            }
            else
                return -releaseRate*bTorque;

        }
        else{
            if(slip<minSlip){ //between  //brake
            System.out.println("case2");
                status = true;
                if (bTorque < prevBTorque) {
                    time = 0;
                    status = false;
                    return 0;
                }
                if (time < applyDelay)
                    return 0;
                else
                    return applyRateSec * bTorque;
            }
            else{ //////////slip<slipMax
                System.out.println("case1");
                if(status){
                    time=0;
                    return 0;
                }
                else{
                    if(bTorque<prevBTorque){
                        time=0;
                        status=false;
                        return 0;
                    }
                    else{
                        if(time<applyDelay)
                            return 0;
                        else
                            return applyRatePri*bTorque;
                    }
                }

            }

        }
    }

    private void jbInit() throws Exception {
    }

}
