package edu.neu.madcourse.skilift.models;

public class RideInfo {
    private String rideID;
    private String username;
    private String departureLocation;
    private double departureLatitude;
    private double departureLongitude;
    private long pickupUnixTimestamp;
    private long pickupDate;
    private String pickupTime;
    private long returnUnixTimestamp;
    private long returnDate;
    private String returnTime;
    private String destination;
    private int passengers;
    private String carModel;
    private String licensePlate;
    private String skiRack;
    private String specialRequests;

    public RideInfo(String rideID, String username, String departureLocation,
                    double departureLatitude, double departureLongitude,
                    long pickupUnixTimestamp, long pickupDate,
                    String pickupTime, long returnUnixTimestamp, long returnDate, String returnTime, String destination, int passengers, String carModel,
                    String licensePlate, String skiRack, String specialRequests) {
        this.rideID = rideID;
        this.username = username;
        this.departureLocation = departureLocation;
        this.departureLatitude = departureLatitude;
        this.departureLongitude = departureLongitude;
        this.pickupUnixTimestamp = pickupUnixTimestamp;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.returnUnixTimestamp = returnUnixTimestamp;
        this.returnDate = returnDate;
        this.returnTime = returnTime;
        this.destination = destination;
        this.passengers = passengers;
        this.carModel = carModel;
        this.licensePlate = licensePlate;
        this.skiRack = skiRack;
        this.specialRequests = specialRequests;
    }

    // Used by Firebase to reconstruct RideInfo
    @SuppressWarnings("unused")
    public RideInfo() {}

    public long getPickupUnixTimestamp() {
        return pickupUnixTimestamp;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public long getReturnUnixTimestamp() {
        return returnUnixTimestamp;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public String getRideID() {
        return rideID;
    }

    public String getUsername() {
        return username;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public double getDepartureLatitude() {
        return departureLatitude;
    }

    public double getDepartureLongitude() {
        return departureLongitude;
    }

    public long getPickupDate() {
        return pickupDate;
    }

    public long getReturnDate() {
        return returnDate;
    }

    public String getDestination() {
        return destination;
    }

    public int getPassengers() {
        return passengers;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String isSkiRack() {
        return skiRack;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }
}
