package dynamics;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

abstract public class Solver {
  public static double solve(double x, double xDot, double t){
    return x+xDot*t;
  }

  public Solver() {
  }

}
