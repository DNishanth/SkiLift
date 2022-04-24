package edu.neu.madcourse.skilift.models;

public class RideInfo {
    private String username;
    private long pickupDate;
//    private int pickupTime;
    private long returnDate;
//    private int returnTime;
    private String destination;
    private int passengers;
    private String carModel;
    private String licensePlate;
    private String skiRack;
    private String specialRequests;

    public RideInfo(String username, long pickupDate, long returnDate, String destination,
                    int passengers, String carModel, String licensePlate, String skiRack,
                    String specialRequests) {
        this.username = username;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
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

    public String getUsername() {
        return username;
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
