package org.example.parking_lot.models;

import org.example.parking_lot.enums.SpotSize;
import org.example.parking_lot.enums.VehicleType;

public class Bike extends Vehicle{

    public Bike(String registrationNumber){
        super(registrationNumber);
    }

    @Override
    public VehicleType getVehicleType(){
        return VehicleType.BIKE;
    }

    @Override
    public SpotSize getRequiredSpotSize(){
        return SpotSize.SMALL;
    }
}
