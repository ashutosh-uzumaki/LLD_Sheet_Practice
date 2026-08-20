package org.example.parking_lot.models;

import org.example.parking_lot.enums.VehicleType;
import org.example.parking_lot.service.PricingCalculator;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class ParkingLot {
    private final List<Floor> floors;
    private final Map<String, ParkingTicket> activeTickets;
    private final PricingCalculator pricingCalculator;

    public ParkingLot(List<Floor> floors, PricingCalculator pricingCalculator){
        this.floors = floors;
        activeTickets = new HashMap<>();
        this.pricingCalculator = pricingCalculator;
    }

    public void addFloor(Floor floor){
        floors.add(floor);
    }

    public ParkingTicket park(Vehicle vehicle){
        if(vehicle == null){
            throw new IllegalArgumentException("Cannot park no vehicle");
        }
        if(activeTickets.containsKey(vehicle.getRegistrationNumber())){
            throw new RuntimeException("Duplicate Vehicle. It is already parked. Check Registration Number");
        }
        Optional<ParkingSpot> spot = findParkingSpot(vehicle);
        if(spot.isEmpty()){
            throw new RuntimeException("No spots available for parking");
        }
        ParkingSpot parkingSpot = spot.get();
        parkingSpot.occupy(vehicle.getRegistrationNumber());
        ParkingTicket parkingTicket = new ParkingTicket(
                UUID.randomUUID().toString(),
                parkingSpot,
                vehicle.getVehicleType(),
                vehicle.getRegistrationNumber(),
                LocalDateTime.now()
        );
        activeTickets.put(vehicle.getRegistrationNumber(), parkingTicket);
        return parkingTicket;
    }

    private Optional<ParkingSpot> findParkingSpot(Vehicle vehicle){
        ParkingSpot bestSpot = null;
        for(Floor floor: floors){
            Optional<ParkingSpot> currSpot = floor.findBestSpot(vehicle);
            if(currSpot.isPresent()){
                bestSpot = chooseBetterSpot(currSpot.get(), bestSpot);
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

    public BigDecimal unpark(String registrationNumber){
        ParkingTicket ticket = activeTickets.get(registrationNumber);
        if(ticket == null){
            throw new RuntimeException("Ticket Not Found!!!");
        }

        ParkingSpot spot = ticket.getParkingSpot();
        ticket.close(LocalDateTime.now().plusHours(2));
        VehicleType vehicleType = ticket.getVehicleType();
        Duration duration = ticket.calculateDuration();
        BigDecimal fee = pricingCalculator.calculateParkingFee(vehicleType, duration);
        spot.release(registrationNumber);
        activeTickets.remove(registrationNumber);
        return fee;
    }
}
