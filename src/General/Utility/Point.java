package General.Utility;

public final class Point { 
    private final double x;    // x-coordinate
    private final double y;    // y-coordinate
   
    // random point
    public Point() {
        x = Math.random();
        y = Math.random();
    }

    // point initialized from parameters
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // accessor methods
    public double x() { return x; }
    public double y() { return y; }
    public double r() { return Math.sqrt(x*x + y*y); }
    public double theta() { return Math.atan2(y, x); }

    // Euclidean distance between this point and that point
    public double distanceTo(Point that) {
        double dx = this.x - that.x;
        double dy = this.y - that.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    // return a string representation of this point
    public String toString() {
        return "(" + x + ", " + y + ")";
    } 
}

