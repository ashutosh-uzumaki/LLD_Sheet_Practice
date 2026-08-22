package org.example.parking_lot;

import org.example.parking_lot.models.*;
import org.example.parking_lot.service.PricingCalculator;
import org.example.parking_lot.service.impl.FlatRatePricing;

import java.util.List;

import java.util.concurrent.CountDownLatch;

public class ParkingLotDemo {

    public static void main(String[] args)
            throws InterruptedException {

        List<Floor> floors = List.of(
                new Floor(1, 2, 0, 0)
        );

        PricingCalculator pricingCalculator =
                new FlatRatePricing();

        ParkingLot parkingLot =
                new ParkingLot(floors, pricingCalculator);

        int numberOfEntryGates = 5;

        CountDownLatch startLatch =
                new CountDownLatch(1);

        CountDownLatch doneLatch =
                new CountDownLatch(numberOfEntryGates);

        for (int i = 1; i <= numberOfEntryGates; i++) {

            String registrationNumber =
                    "KA-01-" + (1000 + i);

            Thread entryGate = new Thread(() -> {

                try {
                    // Wait until all gates are ready
                    startLatch.await();

                    Vehicle bike =
                            new Bike(registrationNumber);

                    ParkingTicket ticket =
                            parkingLot.park(bike);

                    System.out.println(
                            Thread.currentThread().getName()
                                    + " parked "
                                    + registrationNumber
                                    + " at spot "
                                    + ticket.getParkingSpot().getSpotId()
                    );

                } catch (Exception e) {

                    System.out.println(
                            Thread.currentThread().getName()
                                    + " failed: "
                                    + e.getMessage()
                    );

                } finally {
                    doneLatch.countDown();
                }

            }, "Entry-Gate-" + i);

            entryGate.start();
        }

        // Release all gates at approximately the same time
        startLatch.countDown();

        // Wait for every gate to finish
        doneLatch.await();

        System.out.println("All entry gates completed.");
    }
}