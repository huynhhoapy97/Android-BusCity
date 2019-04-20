package truonghuynhhoa.ptit.model;

public class StationOfRoute {
    private BusRoute route;
    private BusStation station;
    private BusTurn turn;
    private Integer numericalOrder;
    private Integer nextStationTime;
    public StationOfRoute() {
    }
    public StationOfRoute(BusRoute route, BusStation station, BusTurn turn, Integer numericalOrder,
                          Integer nextStationTime) {
        this.route = route;
        this.station = station;
        this.turn = turn;
        this.numericalOrder = numericalOrder;
        this.nextStationTime = nextStationTime;
    }

    public BusRoute getRoute() {
        return route;
    }
    public void setRoute(BusRoute route) {
        this.route = route;
    }
    public BusStation getStation() {
        return station;
    }
    public void setStation(BusStation station) {
        this.station = station;
    }
    public BusTurn getTurn() {
        return turn;
    }
    public void setTurn(BusTurn turn) {
        this.turn = turn;
    }
    public Integer getNumericalOrder() {
        return numericalOrder;
    }
    public void setNumericalOrder(Integer numericalOrder) {
        this.numericalOrder = numericalOrder;
    }
    public Integer getNextStationTime() {
        return nextStationTime;
    }
    public void setNextStationTime(Integer nextStationTime) {
        this.nextStationTime = nextStationTime;
    }
}
