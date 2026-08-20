package org.example.parking_lot.service.impl;

import org.example.parking_lot.enums.VehicleType;
import org.example.parking_lot.service.PricingCalculator;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

public class FlatRatePricing implements PricingCalculator {
    private static final Map<VehicleType, BigDecimal> rates = Map.of(
            VehicleType.BIKE, new BigDecimal("10"),
            VehicleType.CAR, new BigDecimal("20"),
            VehicleType.TRUCK, new BigDecimal("30")
    );

    @Override
    public BigDecimal calculateParkingFee(VehicleType vehicleType, Duration duration){
        BigDecimal rate = rates.get(vehicleType);
        long hours = duration.toHours();
        return rate.multiply(BigDecimal.valueOf(hours));
    }
}
