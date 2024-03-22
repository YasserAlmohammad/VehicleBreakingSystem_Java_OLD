package dynamics;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public abstract class MatrixAlgebra {
  /*multiplies two two-dimentional arrays A,B respectively and returns the result in result*/
  /* checked OK */
  public static void mul(double[][] result,double[][] A, double[][] B){
      for(int i=0; i<A.length; i++){
        for(int k=0; k<A.length; k++){
          result[k][i]=0;
          for(int j=0; j<A.length; j++){
            result[k][i]+=A[k][j]*B[j][i];
          }
        }
      }
  }
  /*multiplies a two-dimentional array with A with a vector B and returns the result in result */
  /* checked OK */
  public static void mul(double[] result,double[][] A,double[]B){
        for(int k=0; k<A.length; k++){
          result[k]=0;
          for(int j=0; j<A.length; j++){
            result[k]+=A[k][j]*B[j];
          }
        }
   }
   /*scales a two-dimentional array A by a factor of scl and returns the result in result*/
   /* checked OK */
  public static void mul(double[][] result,double[][] A, double scl){
    for(int i=0;i<A.length;i++)
      for(int j=0; j<A.length; j++)
        result[i][j]=A[i][j]*scl;
  }
  /*scales a vector A by a factor of scl and returns the result in result*/
   /* checked OK */
  public static void mul(double[] result,double[] A,double scl){
    for(int i=0; i<A.length;i++)
      result[i]=A[i]*scl;
  }
  /*scales a vector A by a factor of 1/scl and returns the result in result*/
  /* checked OK */
  public static void div(double[] result,double[] A,double scl){
    for(int i=0; i<A.length;i++)
      result[i]=A[i]/scl;
  }
  /*calculates the cross product of vectors A, B and returns the result in result*/
  public static void crossProd(double[] result, double[] A, double[] B){
    result[0] =  A[1]*B[2]-B[1]*A[2];
    result[1] = -A[0]*B[2]+B[0]*A[2];
    result[2] =  A[0]*B[1]-B[0]*A[1];
  }
  /*adds two vectors A, B and returns the result in result*/
  /* checked OK */
  public static void add(double[] result, double[] A, double[] B){
    for(int i=0; i<A.length;i++)
      result[i]=A[i]+B[i];
  }
  public static void sub(double[] result, double[] A, double[] B){
    for(int i=0; i<A.length;i++)
      result[i]=A[i]-B[i];
  }
  public static void quatToRot(double[][] result, double[] quat){
    result[0][0] = 1 - 2*Math.pow(quat[2],2) - 2*Math.pow(quat[3],2);
    result[0][1] = 2*quat[1]*quat[2] - 2*quat[0]*quat[3];
    result[0][2] = 2*quat[1]*quat[3] + 2*quat[0]*quat[2];
    //************
    result[1][0] = 2*quat[1]*quat[2] + 2*quat[0]*quat[3];;
    result[1][1] = 1- 2*Math.pow(quat[1],2) - 2*Math.pow(quat[3],2);
    result[1][2] = 2*quat[2]*quat[3] - 2*quat[0]*quat[1];
    //************
    result[2][0] = 2*quat[1]*quat[3] - 2*quat[0]*quat[2];
    result[2][1] = 2*quat[2]*quat[3] + 2*quat[0]*quat[1];
    result[2][2] = 1 - 2*Math.pow(quat[1],2) - 2*Math.pow(quat[2],2);
  }
  public static void rotToQuat(double[] result, double[][] matrix){
    double tr, s;
    tr = matrix[0][0] + matrix[1][1] + matrix[2][2];
    if (tr>=0){
      s = Math.sqrt(tr + 1);
      result[0] = .5 * s;
      s = .5 / s;
      result[1] = (matrix[2][1] - matrix[1][2]) * s;
      result[2] = (matrix[0][2] - matrix[2][0]) * s;
      result[3] = (matrix[1][0] - matrix[0][1]) * s;
    }
    else{
      int i = 0;

      if(matrix[1][1] > matrix[0][0])
        i = 1;
      if(matrix[2][2] > matrix[i][i])
        i = 2;
      switch (i){
        case 0:
          s = Math.sqrt((matrix[0][0] - (matrix[1][1] + matrix[2][2])) + 1);
          result[1] = .5 * s;
          s = .5 / s;
          result[2] = (matrix[0][1] + matrix[1][0]) * s;
          result[3] = (matrix[2][0] + matrix[0][2]) * s;
          result[0] = (matrix[2][1] - matrix[1][2]) * s;
          break;
        case 1:
          s = Math.sqrt((matrix[1][1] - (matrix[2][2] + matrix[0][0])) + 1);
          result[2] = .5 * s;
          s = .5 / s;
          result[3] = (matrix[1][2] + matrix[2][1]) * s;
          result[1] = (matrix[0][1] + matrix[1][0]) * s;
          result[0] = (matrix[0][2] - matrix[2][0]) * s;
          break;
        case 2:
          s = Math.sqrt((matrix[2][2] - (matrix[0][0] + matrix[1][1])) + 1);
          result[3] = .5 * s;
          s = .5 / s;
          result[1] = (matrix[2][0] + matrix[0][2]) * s;
          result[2] = (matrix[1][2] + matrix[2][1]) * s;
          result[0] = (matrix[1][0] - matrix[0][1]) * s;
      }
    }
  }
  public static void quatMul(double[] result, double[] qOne, double[] qTwo){
    /*
    Let Q1 and Q2 be two quaternions, which are defined, respectively, as
    (w1, x1, y1, z1) and (w2, x2, y2, z2)
    (Q1 * Q2).w = (w1w2 - x1x2 - y1y2 - z1z2)
    (Q1 * Q2).x = (w1x2 + x1w2 + y1z2 - z1y2)
    (Q1 * Q2).y = (w1y2 - x1z2 + y1w2 + z1x2)

    (Q1 * Q2).z = (w1z2 + x1y2 - y1x2 + z1w2
    */
    result[0] = qOne[0]*qTwo[0] - qOne[1]*qTwo[1] - qOne[2]*qTwo[2] - qOne[3]*qTwo[3];
    result[1] = qOne[0]*qTwo[1] + qOne[1]*qTwo[0] + qOne[2]*qTwo[3] - qOne[3]*qTwo[2];
    result[2] = qOne[0]*qTwo[2] - qOne[1]*qTwo[3] + qOne[2]*qTwo[0] + qOne[3]*qTwo[1];
    result[3] = qOne[0]*qTwo[3] + qOne[1]*qTwo[2] - qOne[2]*qTwo[1] + qOne[3]*qTwo[0];
  }
  // returns the magnitude of a vector
  /* checked OK */
  public static double getMagnitude(double[] vctr){
    double sum = 0;
    for (int i=0; i<vctr.length;i++)
      sum += Math.pow(vctr[i],2);
    return Math.sqrt(sum);
  }


  // returnes the vector with its members raised to the power (power)
  /* checked OK */
  public static void power(double[] result,double[] vctr,double pwr){
    for(int i=0; i<vctr.length; i++){
      result[i] = Math.pow(vctr[i],pwr);
    }
  }
  // multiplies two vectors in terms of one to one multiplication
  /* checked OK*/
  public static void mul(double[] result, double[] vctr1, double[] vctr2){
    for(int i = 0; i< vctr1.length;i++)
      result[i] = vctr1[i]*vctr2[i];
  }
  public static void transpose(double[][] result, double[][] matrix){
    for(int i=0;i<matrix.length;i++)
      for(int j=0;j<matrix.length;j++)
        result[i][j]=matrix[j][i];
  }
  public static void display(double[][] m){
    for(int i=0; i<m.length;i++){
      for(int j=0;j<m.length;j++)
        System.out.print(m[i][j]+"   ");
      System.out.println();
    }
   System.out.println("****************");
  }
  public static void display(double[] v){
    for(int i=0; i<v.length;i++)
      System.out.print(v[i]+"  ");
    System.out.println();
  }
  public static void normalize(double[] result,double[] v){
    double vNorm = 0;
    for(int i = 0; i<v.length; i++)
      vNorm += v[i]*v[i];
    vNorm = Math.sqrt(vNorm);
    double scl = 1/vNorm;
    //System.out.println(scl);
    mul(result,v,scl);
  }
  public static void set(double[] result, double[] input){
      for(int i =0; i<input.length; i++)
          result[i] = input[i];
  }
  public static void main(String[] args) {


    double[][] R = { {1, 0, 0, }
                   , {0, 1, 0, }
                   , {0, 0, 1, }
    };
    double[][] RR = new double[3][3];
    R[0][0] = Math.cos(Math.PI/40);
    R[1][0] = -Math.sin(Math.PI/40);
    R[2][0] = 0;

    R[0][1] = Math.sin(Math.PI/40);
    R[1][1] = Math.cos(Math.PI/40);
    R[2][1] = 0;

    R[0][2] = 0;
    R[1][2] = 0;
    R[2][2] = 1;
    System.out.println("***********");
    display(R);

    double[] q = new double[4];
    double[] res = new double[4];
    for(int i=0; i<1; i++){
      rotToQuat(q,R);
      quatMul(res,q,q);
      quatToRot(R,res);
//      System.out.println("***********");
//      display(R);
    }
    System.out.println("***********");
    mul(RR,R,R);
    display(RR);
  }

}
