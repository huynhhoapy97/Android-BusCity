package truonghuynhhoa.ptit.model;

import java.io.Serializable;

public class Instruction implements Serializable {
    private int numericalOrder;
    private int busRoute;
    private int walkDistance;
    private int busDistance;

    public Instruction() {
    }

    public Instruction(int numericalOrder, int busRoute, int walkDistance, int busDistance) {
        this.numericalOrder = numericalOrder;
        this.busRoute = busRoute;
        this.walkDistance = walkDistance;
        this.busDistance = busDistance;
    }

    public int getNumericalOrder() {
        return numericalOrder;
    }

    public void setNumericalOrder(int numericalOrder) {
        this.numericalOrder = numericalOrder;
    }

    public int getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(int busRoute) {
        this.busRoute = busRoute;
    }

    public int getWalkDistance() {
        return walkDistance;
    }

    public void setWalkDistance(int walkDistance) {
        this.walkDistance = walkDistance;
    }

    public int getBusDistance() {
        return busDistance;
    }

    public void setBusDistance(int busDistance) {
        this.busDistance = busDistance;
    }
}
