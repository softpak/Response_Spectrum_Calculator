/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

/**
 *
 * @author you
 */
public class Point {
    private final Double x;
    private final Double y;
    private final String station;

    public Point(String station, Double x, Double y) {
        this.x = x;
        this.y = y;
        this.station = station;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public String getStation() {
        return station;
    }

    public String toString() {
        return "(" + x + " " + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            if (x.equals(((Point) obj).getX()) && y.equals(((Point) obj).getY())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // http://stackoverflow.com/questions/22826326/good-hashcode-function-for-2d-coordinates
        // http://www.cs.upc.edu/~alvarez/calculabilitat/enumerabilitat.pdf
        int tmp = (int) (y + ((x + 1) / 2));
        return Math.abs((int) (x + (tmp * tmp)));
    }
}
