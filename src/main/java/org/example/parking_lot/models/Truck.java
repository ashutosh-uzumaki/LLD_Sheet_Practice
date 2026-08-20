package org.example.parking_lot.models;

import org.example.parking_lot.enums.SpotSize;
import org.example.parking_lot.enums.VehicleType;

public class Truck extends Vehicle{

    public Truck(String registrationNumber){
        super(registrationNumber);
    }

    @Override
    public VehicleType getVehicleType(){
        return VehicleType.TRUCK;
    }

    @Override
    public SpotSize getRequiredSpotSize(){
        return SpotSize.LARGE;
    }
}
