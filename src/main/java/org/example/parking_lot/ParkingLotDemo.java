package org.example.parking_lot;

import org.example.parking_lot.models.*;
import org.example.parking_lot.service.PricingCalculator;
import org.example.parking_lot.service.impl.FlatRatePricing;

import java.math.BigDecimal;
import java.util.List;

public class ParkingLotDemo {
    public static void main(String[] args) {
        List<Floor> floors = List.of(
                new Floor(1, 2, 1, 0),
                new Floor(2, 0, 1, 0)
        );
        PricingCalculator pricingCalculator = new FlatRatePricing();
        ParkingLot parkingLot = new ParkingLot(floors, pricingCalculator);
        Vehicle bike = new Bike("KA 01 1111");
        Vehicle bike1 = new Bike("KA 02 1112");
        Vehicle bike2 = new Bike("KA 03 1114");
        Vehicle car = new Car("KA 03 1113");
        ParkingTicket carTicket = parkingLot.park(car);
        Vehicle car1 = new Car("KA 04 1114");
        ParkingTicket secondCarTicket = parkingLot.park(car1);
        ParkingTicket ticket = parkingLot.park(bike);
        ParkingTicket ticket1 = parkingLot.park(bike1);
        ParkingTicket ticket2 = null;
        try{
            parkingLot.park(bike2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        BigDecimal fee = parkingLot.unpark(ticket.getRegistrationNumber());
        System.out.println("Fee Paid "+fee);
    }
}
