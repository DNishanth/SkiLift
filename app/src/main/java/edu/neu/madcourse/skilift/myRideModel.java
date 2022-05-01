package edu.neu.madcourse.skilift;

public class myRideModel {

  String origin, destination, pickup, returnTime, license, driver, rideID;

  public myRideModel(String origin, String destination, String pickup, String returnTime, String license, String driver, String rideID) {
    this.origin = origin;
    this.destination = destination;
    this.pickup = pickup;
    this.returnTime = returnTime;
    this.license = license;
    this.driver = driver;
    this.rideID = rideID;
  }

  public String getOrigin() {
    return origin;
  }

  public String getDestination() {
    return destination;
  }

  public String getPickup() {
    return pickup;
  }

  public String getReturnTime() {
    return returnTime;
  }

  public String getLicense() {
    return license;
  }

  public String getDriver() {
    return driver;
  }
  public String getRideID() {
    return rideID;
  }
}
