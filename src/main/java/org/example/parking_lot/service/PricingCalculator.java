package org.example.parking_lot.service;

import org.example.parking_lot.enums.VehicleType;

import java.math.BigDecimal;
import java.time.Duration;

public interface PricingCalculator {
    BigDecimal calculateParkingFee(VehicleType vehicleType, Duration duration);
}
