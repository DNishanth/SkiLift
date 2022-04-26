package edu.neu.madcourse.skilift.models;

public class UserProfile {
    private long memberDate;
    private int ridesCompleted;
    private String skiType;
    private String favoriteMountains;
    private String funFact;
    private String profilePictureSrc;
    private double rating;
    private int numRatings;

    public UserProfile(long memberDate, int ridesCompleted, String skiType,
                       String favoriteMountains, String funFact, String profilePictureSrc,
                       Double rating, int numRatings) {
        this.memberDate = memberDate;
        this.ridesCompleted = ridesCompleted;
        this.skiType = skiType;
        this.favoriteMountains = favoriteMountains;
        this.funFact = funFact;
        this.profilePictureSrc = profilePictureSrc;
        this.rating = rating;
        this.numRatings = numRatings;
    }

    // Used by Firebase to reconstruct UserProfile
    @SuppressWarnings("unused")
    public UserProfile() {}

    public long getMemberDate() {
        return memberDate;
    }

    public int getRidesCompleted() {
        return ridesCompleted;
    }

    public String getSkiType() {
        return skiType;
    }

    public String getFavoriteMountains() {
        return favoriteMountains;
    }

    public String getFunFact() {
        return funFact;
    }

    public String getProfilePictureSrc() {
        return profilePictureSrc;
    }

    public double getRating() {
        return rating;
    }

    public int getNumRatings() {
        return numRatings;
    }
}
