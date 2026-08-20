package org.example.parking_lot.service.impl;

import org.example.parking_lot.enums.VehicleType;
import org.example.parking_lot.service.PricingCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Map;

public class ProRataPricing implements PricingCalculator {
    private static final Map<VehicleType, BigDecimal> rates = Map.of(
            VehicleType.BIKE, new BigDecimal("5"),
            VehicleType.CAR, new BigDecimal("10"),
            VehicleType.TRUCK, new BigDecimal("20")
    );

    @Override
    public BigDecimal calculateParkingFee(VehicleType vehicleType, Duration duration){
        long inMinutes = duration.toMinutes();
        BigDecimal perMinuteCost = rates.get(vehicleType).divide(new BigDecimal("60"), 2, RoundingMode.HALF_UP);
        return perMinuteCost.multiply(BigDecimal.valueOf(inMinutes));
    }
}
