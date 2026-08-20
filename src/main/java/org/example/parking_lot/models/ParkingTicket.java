package org.example.parking_lot.models;

import org.example.parking_lot.enums.SpotSize;
import org.example.parking_lot.enums.VehicleType;

import java.time.Duration;
import java.time.LocalDateTime;

public class ParkingTicket {
    private final String ticketId;
    private final ParkingSpot parkingSpot;
    private final VehicleType vehicleType;
    private final String registrationNumber;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public ParkingTicket(String ticketId, ParkingSpot parkingSpot, VehicleType vehicleType,
                         String registrationNumber, LocalDateTime entryTime){
        this.ticketId = ticketId;
        this.parkingSpot = parkingSpot;
        this.vehicleType = vehicleType;
        this.registrationNumber = registrationNumber;
        this.entryTime = entryTime;
    }

    public void close(LocalDateTime exitTime){
        if(exitTime == null || exitTime.isBefore(entryTime)){
            throw new RuntimeException("Invalid Exit time");
        }
        this.exitTime = exitTime;
    }

    public String getTicketId() {
        return ticketId;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public Duration calculateDuration(){
        return Duration.between(entryTime, exitTime);
    }
}
