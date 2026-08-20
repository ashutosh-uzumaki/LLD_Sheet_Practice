package org.example.parking_lot.models;

import org.example.parking_lot.enums.SpotSize;
import org.example.parking_lot.enums.VehicleType;

public class Car extends Vehicle{

    public Car(String registrationNumber){
        super(registrationNumber);
    }

    @Override
    public VehicleType getVehicleType(){
        return VehicleType.CAR;
    }

    @Override
    public SpotSize getRequiredSpotSize(){
        return SpotSize.MEDIUM;
    }
}
