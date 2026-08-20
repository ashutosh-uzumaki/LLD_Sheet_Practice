package org.example.parking_lot.models;

import org.example.parking_lot.enums.SpotSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Floor {
    private final Integer floorNumber;
    private final int smallCount;
    private final int mediumCount;
    private final int largeCount;
    private final List<ParkingSpot> parkingSpots;

    public Floor(Integer floorNumber, int smallCount, int mediumCount, int largeCount){
        if(smallCount < 0 || mediumCount < 0 || largeCount < 0){
            throw new IllegalArgumentException("Spots cannot be in negative numbers");
        }
        this.floorNumber = floorNumber;
        this.smallCount = smallCount;
        this.mediumCount = mediumCount;
        this.largeCount = largeCount;
        parkingSpots = new ArrayList<>();
        addSpots(smallCount, SpotSize.SMALL);
        addSpots(mediumCount, SpotSize.MEDIUM);
        addSpots(largeCount, SpotSize.LARGE);
    }

    public void addSpots(int count, SpotSize spotSize){
        for(int i=1; i<=count; i++){
            parkingSpots.add(new ParkingSpot(parkingSpots.size()+1, spotSize));
        }
    }

    public Optional<ParkingSpot> findBestSpot(Vehicle vehicle){
        ParkingSpot bestSpot = null;
        for(ParkingSpot spot: parkingSpots){
            if(spot.canFit(vehicle) && spot.isAvailable()){
                bestSpot = chooseBetterSpot(spot, bestSpot);
            }
        }
        return Optional.ofNullable(bestSpot);
    }

    private ParkingSpot chooseBetterSpot(ParkingSpot spot, ParkingSpot bestSpot){
        if(bestSpot == null){
            return spot;
        }

        if(spot.getSpotSize().getRank() < bestSpot.getSpotSize().getRank()){
            return spot;
        }else if(spot.getSpotSize().getRank() == bestSpot.getSpotSize().getRank()){
            if(spot.getSpotId() < bestSpot.getSpotId()){
                return spot;
            }
        }
        return bestSpot;
    }
}
