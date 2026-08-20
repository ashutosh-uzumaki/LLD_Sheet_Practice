package org.example.parking_lot.models;

import org.example.parking_lot.enums.SpotSize;

import java.util.Objects;

public class ParkingSpot {
    private final Integer spotId;
    private final SpotSize spotSize;
    private String registrationNumber;

    public ParkingSpot(Integer spotId, SpotSize spotSize){
        this.spotId = spotId;
        this.spotSize = spotSize;
    }

    public void occupy(String registrationNumber){
        if(isOccupied()){
            throw new RuntimeException("Already occupied");
        }
        this.registrationNumber = Objects.requireNonNull(registrationNumber);
    }

    public boolean canFit(Vehicle vehicle){
        return this.spotSize.getRank() >= vehicle.getRequiredSpotSize().getRank();
    }

    public void release(String registrationNumber){
        if(this.registrationNumber.equals(Objects.requireNonNull(registrationNumber))){
            this.registrationNumber = null;
        }else{
            throw new RuntimeException("Spot is occupied by another vehicle.");
        }
    }

    public boolean isOccupied(){
        return this.registrationNumber != null;
    }

    public boolean isAvailable(){
        return registrationNumber == null;
    }

    public Integer getSpotId(){
        return this.spotId;
    }

    public SpotSize getSpotSize(){
        return this.spotSize;
    }
}
