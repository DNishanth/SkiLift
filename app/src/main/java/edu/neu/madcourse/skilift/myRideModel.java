package edu.neu.madcourse.skilift;

public class myRideModel {

  String origin, destination, pickup, returnTime, license;

  public myRideModel(String origin, String destination, String pickup, String returnTime, String license) {
    this.origin = origin;
    this.destination = destination;
    this.pickup = pickup;
    this.returnTime = returnTime;
    this.license = license;
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
}
