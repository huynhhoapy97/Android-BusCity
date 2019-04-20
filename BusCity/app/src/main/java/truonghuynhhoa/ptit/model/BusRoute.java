package truonghuynhhoa.ptit.model;

public class BusRoute {
    private Integer id;
    private String name;
    private String startTime;
    private String endTime;
    private Integer routeLength;
    private Integer runTime;
    private Integer spacingTime;

    public BusRoute() {
    }

    public BusRoute(Integer id, String name){
        this.id = id;
        this.name = name;
    }

    public BusRoute(Integer id, String name, String startTime, String endTime, Integer routeLength, Integer runTime, Integer spacingTime) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.routeLength = routeLength;
        this.runTime = runTime;
        this.spacingTime = spacingTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getRouteLength() {
        return routeLength;
    }

    public void setRouteLength(Integer routeLength) {
        this.routeLength = routeLength;
    }

    public Integer getRunTime() {
        return runTime;
    }

    public void setRunTime(Integer runTime) {
        this.runTime = runTime;
    }

    public Integer getSpacingTime() {
        return spacingTime;
    }

    public void setSpacingTime(Integer spacingTime) {
        this.spacingTime = spacingTime;
    }
}
