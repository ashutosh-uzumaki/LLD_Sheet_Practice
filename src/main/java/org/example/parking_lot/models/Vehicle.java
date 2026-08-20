package org.example.parking_lot.models;

import org.example.parking_lot.enums.SpotSize;
import org.example.parking_lot.enums.VehicleType;

public abstract class Vehicle {
    private final String registrationNumber;

    public Vehicle(String registrationNumber){
        if(registrationNumber == null || registrationNumber.isBlank()){
            throw new RuntimeException("Registration Number Cannot Be Null or Blank!!");
        }
        this.registrationNumber = registrationNumber;
    }

    public String getRegistrationNumber(){
        return this.registrationNumber;
    }

    public abstract VehicleType getVehicleType();
    public abstract SpotSize getRequiredSpotSize();
}
