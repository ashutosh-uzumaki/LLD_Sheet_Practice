# Parking Lot LLD - Phase 1 Revision Notes

## 1. Scope

Phase 1 is the core Parking Lot implementation.

### Included

-   Park a vehicle
-   Unpark a vehicle
-   Find the best parking spot
-   Generate and manage parking tickets
-   Calculate parking fees
-   Support Bike, Car, and Truck
-   Validate important inputs
-   Keep parking spots reusable after unpark

### Deliberately deferred

These are interview extensions, not Phase 1: - Multiple entry/exit
gates - Concurrency - Display board / Observer - Payment methods -
Reservations - EV charging - Notifications - Persistence/database -
Other advanced variations

------------------------------------------------------------------------

# 2. Core Domain Model

``` text
ParkingLot
    |
    | owns
    v
  Floor
    |
    | owns
    v
ParkingSpot


ParkingLot
    |
    | manages
    v
ParkingTicket


Vehicle
   ^
   |
 +---+---+
 |   |   |
Bike Car Truck


PricingCalculator
       ^
       |
 +-----+-------------+
 |                   |
FlatRatePricing   ProRataPricing
```

## Responsibilities

### ParkingLot

Owns/orchestrates: - `List<Floor>` - active tickets - pricing
calculator - `park()` - `unpark()` - global best-spot selection across
floors

It is the main domain orchestrator.

### Floor

Owns: - `List<ParkingSpot>`

Responsible for: - creating its parking spots - finding the best spot on
that floor

### ParkingSpot

Knows: - `spotId` - `SpotSize` - occupying registration number

Responsible for: - determining whether a vehicle fits - knowing whether
it is occupied - occupying - releasing

### Vehicle

Base class containing: - registration number

Abstract behavior: - `getVehicleType()` - `getRequiredSpotSize()`

Implementations: - Bike → SMALL - Car → MEDIUM - Truck → LARGE

### ParkingTicket

Represents one parking session.

Contains: - `ticketId` - `registrationNumber` - `vehicleType` -
`entryTime` - `exitTime` - `ParkingSpot`

Responsible for: - closing the ticket - calculating parking duration

### PricingCalculator

Interface responsible for calculating the fee.

``` java
BigDecimal calculateParkingFee(
    VehicleType vehicleType,
    Duration duration
);
```

Implementations can use different pricing policies.

------------------------------------------------------------------------

# 3. Enums

## SpotSize

``` java
public enum SpotSize {
    SMALL(1),
    MEDIUM(2),
    LARGE(3);

    private final int rank;
}
```

The rank lets us compare sizes.

``` text
SMALL  = 1
MEDIUM = 2
LARGE  = 3
```

A larger spot can fit a smaller vehicle.

For example:

``` text
Car requires MEDIUM
MEDIUM.rank >= Car.required.rank
2 >= 2 → yes

LARGE.rank >= Car.required.rank
3 >= 2 → yes
```

## VehicleType

``` java
public enum VehicleType {
    BIKE,
    CAR,
    TRUCK
}
```

------------------------------------------------------------------------

# 4. Vehicle Design

``` text
Vehicle
├── registrationNumber
├── getVehicleType()
└── getRequiredSpotSize()

Bike
├── BIKE
└── SMALL

Car
├── CAR
└── MEDIUM

Truck
├── TRUCK
└── LARGE
```

Registration number is validated when the vehicle is constructed.

Invalid examples: - `null` - empty string - blank string

------------------------------------------------------------------------

# 5. ParkingSpot Design

State:

``` java
private final int spotId;
private final SpotSize spotSize;
private String registrationNumber;
```

## Occupancy invariant

``` text
registrationNumber == null
        ↓
     available

registrationNumber != null
        ↓
     occupied
```

## Important methods

### `isAvailable()`

Returns whether the spot is free.

### `canFit(Vehicle vehicle)`

Only answers:

> Can this vehicle fit in this spot?

It does not perform allocation.

### `occupy(registrationNumber)`

If already occupied: - throw an exception

Otherwise: - store registration number

### `release(registrationNumber)`

Only the vehicle currently occupying the spot can release it.

------------------------------------------------------------------------

# 6. Spot ID Generation

`Floor` creates the spots.

Spot IDs are: - integers - unique within a floor - sequential across all
spot sizes

Example:

``` text
Floor 1

Spot 1 → SMALL
Spot 2 → SMALL
Spot 3 → MEDIUM
Spot 4 → MEDIUM
Spot 5 → LARGE
```

The ID is generated using:

``` java
parkingSpots.size() + 1
```

This avoids resetting the ID counter when `addSpots()` is called for
another size.

------------------------------------------------------------------------

# 7. Floor Spot Selection

`Floor.findBestSpot(vehicle)` traverses all spots.

For each spot:

``` text
available?
   ↓ yes
can fit?
   ↓ yes
candidate
```

Among candidates, choose the best using:

### Rule 1

Prefer the smallest suitable `SpotSize`.

``` text
SMALL < MEDIUM < LARGE
```

### Rule 2

If size is equal, prefer the lower `spotId`.

Example:

``` text
Spot 3 → MEDIUM
Spot 4 → MEDIUM

Spot 3 wins
```

The helper was conceptually:

``` java
chooseBetterSpot(current, best)
```

and should remain an internal Floor implementation detail.

`findBestSpot()` returns:

``` java
Optional<ParkingSpot>
```

If no suitable spot exists:

``` java
Optional.empty()
```

Use:

``` java
Optional.ofNullable(bestSpot)
```

not:

``` java
Optional.of(bestSpot)
```

because `bestSpot` can be null.

------------------------------------------------------------------------

# 8. ParkingLot Selection Across Floors

Floors are assumed to be ordered by `floorNumber`.

Each floor returns its own best spot.

ParkingLot then compares those candidates.

Rules:

1.  Prefer the smaller suitable spot size.
2.  If sizes are equal, keep the candidate from the earlier floor.

Because floors are already ordered:

``` text
Floor 1
Floor 2
Floor 3
```

we do not need to compare floor numbers explicitly if we preserve the
first candidate when sizes are equal.

### Important design insight

Selection is decomposed:

``` text
Floor
→ best spot on this floor

ParkingLot
→ best spot across floors
```

Each class handles selection at its own level.

------------------------------------------------------------------------

# 9. Parking Flow

The final core flow is:

``` text
park(vehicle)
    |
    +--> validate vehicle
    |
    +--> check duplicate registration
    |
    +--> find best spot
    |
    +--> if none → NoAvailableSpotException
    |
    +--> occupy spot
    |
    +--> create ParkingTicket
    |
    +--> add ticket to activeTickets
    |
    +--> return ticket
```

Important ordering:

``` text
find spot
    ↓
occupy spot
    ↓
create ticket
```

We intentionally occupy the spot before creating the ticket so that the
domain state does not temporarily say:

``` text
ticket exists
but
spot is still free
```

------------------------------------------------------------------------

# 10. Active Tickets

ParkingLot maintains:

``` java
Map<String, ParkingTicket> activeTickets;
```

Key:

`text registrationNumber`

Reason: - a registration number identifies the currently parked
vehicle - it makes duplicate-parking checks easy - it makes unpark
lookup easy

Example:

``` text
"KA01AB1234" → ParkingTicket
"KA02CD5678" → ParkingTicket
```

------------------------------------------------------------------------

# 11. Unpark Flow

Core sequence:

``` text
unpark(registrationNumber)
        |
        +--> find active ticket
        |
        +--> if absent → VehicleNotParkedException
        |
        +--> close ticket
        |
        +--> calculate duration
        |
        +--> calculate fee
        |
        +--> release parking spot
        |
        +--> remove ticket from activeTickets
        |
        +--> return fee
```

The ticket owns duration calculation.

ParkingLot orchestrates.

PricingCalculator owns pricing.

ParkingSpot owns release.

This keeps responsibilities separated.

------------------------------------------------------------------------

# 12. ParkingTicket Lifecycle

A newly created ticket has:

``` text
entryTime = set
exitTime = null
```

Lifecycle:

``` text
OPEN
 |
 | close(exitTime)
 v
CLOSED
```

`close()` validates: - exit time is not null - exit time is not before
entry time - ticket is not already closed

`LocalDateTime` comparison uses methods such as:

``` java
exitTime.isBefore(entryTime)
exitTime.isAfter(entryTime)
```

Do not use:

``` java
exitTime < entryTime
```

because `LocalDateTime` is an object.

Duration:

``` java
Duration.between(entryTime, exitTime)
```

and the ticket exposes:

``` java
Duration calculateDuration()
```

------------------------------------------------------------------------

# 13. Pricing

Pricing is abstracted:

``` java
interface PricingCalculator
```

This prevents ParkingLot from depending on one pricing algorithm.

## FlatRatePricing

Example:

``` text
Bike  → ₹10/hour
Car   → ₹20/hour
Truck → ₹30/hour
```

Whole-hour pricing can use:

``` java
duration.toHours()
```

This intentionally ignores fractional hours.

Example:

``` text
90 minutes
→ 1 hour
→ charge 1 hour
```

## ProRataPricing

Uses exact minutes.

Formula:

``` text
fee = hourlyRate × minutes / 60
```

Example:

``` text
Car = ₹20/hour
90 minutes

20 × 90 / 60
= ₹30
```

For monetary calculation, use `BigDecimal`.

Prefer calculating the full expression and rounding the final result
rather than rounding the per-minute rate first.

Example:

``` java
return rate
    .multiply(BigDecimal.valueOf(minutes))
    .divide(
        BigDecimal.valueOf(60),
        2,
        RoundingMode.HALF_UP
    );
```

Important Java point:

`BigDecimal` does not use `/` or `*`.

Use:

``` java
divide()
multiply()
```

------------------------------------------------------------------------

# 14. Why `Duration`?

`Duration` represents an elapsed amount of time.

Useful operations:

``` java
duration.toMinutes()
duration.toHours()
```

For prorated billing, use:

``` java
duration.toMinutes()
```

because `toHours()` truncates fractional hours.

Example:

``` text
90 minutes
toHours()   → 1
toMinutes() → 90
```

Therefore `toMinutes()` is required for exact prorated pricing.

------------------------------------------------------------------------

# 15. Validation / Domain Rules

Important invariants identified during Phase 1:

### Vehicle

Registration number cannot be: - null - empty - blank

### Floor

Spot counts cannot be negative.

Valid:

``` text
10, 0, 0
0, 10, 0
0, 0, 10
2, 3, 4
```

Invalid:

``` text
-1, 2, 3
```

A floor containing only one spot size is valid.

### ParkingSpot

Cannot occupy an already occupied spot.

Only the correct registration can release the spot.

### ParkingTicket

Cannot: - close with null exit time - close before entry time - close an
already closed ticket

### ParkingLot

Cannot: - park a null vehicle - park a vehicle whose registration is
already active - park when no suitable spot exists - unpark a vehicle
that has no active ticket

------------------------------------------------------------------------

# 16. Demo

The Demo class is for manually exercising the system.

It can: - create floors - create a pricing implementation - create the
ParkingLot - park vehicles - unpark vehicles - print fees - simulate
payment

Payment itself is outside Phase 1.

Important distinction:

``` text
PricingCalculator
→ tells us how much is owed

Payment system
→ would actually collect the money
```

For Phase 1, the Demo can simply simulate:

``` text
Fee Paid: ₹20
Payment successful
```

------------------------------------------------------------------------

# 17. Phase 1 Design Decisions

### Why does Floor own ParkingSpot creation?

Because Floor owns the spots.

``` text
Floor
  └── List<ParkingSpot>
```

It is reasonable for the owner to construct its members.

### Why does ParkingLot own park/unpark?

Because parking/unparking is a workflow involving multiple domain
objects.

``` text
ParkingLot
  ├── Vehicle
  ├── Floor
  ├── ParkingSpot
  ├── ParkingTicket
  └── PricingCalculator
```

ParkingLot coordinates them.

### Why does ParkingTicket calculate duration?

Because the ticket represents the parking session and contains:

``` text
entryTime
exitTime
```

PricingCalculator should not need to know how ticket state is
represented.

### Why does ParkingSpot only know occupancy?

Because it shouldn't know: - pricing - tickets - parking-lot policies -
vehicle allocation strategy

It answers simple domain questions:

``` text
Can I fit this vehicle?
Am I occupied?
Can this vehicle release me?
```

------------------------------------------------------------------------

# 18. Phase 1 Class Responsibility Cheat Sheet

  Class               Main Responsibility
  ------------------- ----------------------------------------
  ParkingLot          Orchestrate park/unpark
  Floor               Own spots and find best spot on floor
  ParkingSpot         Occupancy and fit
  ParkingTicket       Parking-session state and duration
  Vehicle             Registration + vehicle characteristics
  Bike                Bike characteristics
  Car                 Car characteristics
  Truck               Truck characteristics
  PricingCalculator   Pricing abstraction
  FlatRatePricing     Whole-hour pricing
  ProRataPricing      Minute-based prorated pricing
  SpotSize            Spot size + rank
  VehicleType         Vehicle category

------------------------------------------------------------------------

# 19. Interview Mental Model

When asked to design Parking Lot:

``` text
Requirements
    ↓
Identify entities
    ↓
Assign ownership
    ↓
Define responsibilities
    ↓
Define invariants
    ↓
Define allocation algorithm
    ↓
Implement
    ↓
Test
```

For our Phase 1:

``` text
ParkingLot
    owns Floors
    owns active Tickets
    uses PricingCalculator

Floor
    owns ParkingSpots

ParkingSpot
    owns occupancy state

ParkingTicket
    owns parking-session state

PricingCalculator
    owns pricing behavior
```

------------------------------------------------------------------------

# 20. Interview Explanation of `park()`

A concise interview answer:

> `ParkingLot` first validates the vehicle and checks whether its
> registration is already active. It then asks each ordered floor for
> its best available spot and chooses the globally best candidate,
> preferring the smallest suitable spot and then the earliest floor.
> Once a spot is selected, it occupies the spot before creating the
> ticket, then stores the ticket in the active-ticket map.

------------------------------------------------------------------------

# 21. Interview Explanation of `unpark()`

> `ParkingLot` retrieves the active ticket using the registration
> number. It closes the ticket, obtains the parking duration from the
> ticket, delegates fee calculation to the `PricingCalculator`, releases
> the parking spot, removes the ticket from the active-ticket map, and
> returns the calculated fee.

------------------------------------------------------------------------

# 22. Important Design Principle Learned

Do not introduce patterns just because they exist.

We introduced an abstraction for pricing because there is a genuine
variation:

``` text
Flat rate
Pro-rata
```

So:

``` text
PricingCalculator
```

has a real reason to exist.

For other future variations, we will first ask:

``` text
What changed?
        ↓
What breaks?
        ↓
Why does it break?
        ↓
What is the smallest clean design change?
```

------------------------------------------------------------------------

# 23. Future Interview Extensions

Phase 1 remains the frozen baseline.

When an interviewer gives a new requirement, we will derive the change.

For every extension, follow:

``` text
1. New requirement
2. What breaks in Phase 1?
3. Why does it break?
4. What design/code needs to change?
5. Modify UML
6. Modify code
7. Trace execution
8. What happens if we don't change it?
9. Tradeoffs
10. Interview defense
```

Examples of future extensions: - Multiple entry/exit gates - Concurrent
spot allocation - Display board - Observer pattern - Multiple payment
methods - Reservations - EV charging - Notifications - Persistence

Do not add these to the Phase 1 implementation unless the interview
question requires them.

------------------------------------------------------------------------

# 24. Current Phase 1 Status

## Core functionality

-   Parking: COMPLETE
-   Unparking: COMPLETE
-   Spot selection: COMPLETE
-   Ticket lifecycle: COMPLETE
-   Pricing abstraction: COMPLETE
-   Flat pricing: COMPLETE
-   Pro-rata pricing: COMPLETE
-   Validation: MOSTLY COMPLETE
-   End-to-end Demo: COMPLETE

## Remaining polish

-   Replace generic `RuntimeException` with domain-specific exceptions
-   Add basic JUnit tests
-   Final code cleanup
-   Final Phase 1 interview review

These are cleanup/testing tasks, not new features.

------------------------------------------------------------------------

# 25. Final Phase 1 Picture

``` text
                    ParkingLot
                  /     |      \
                 /      |       \
              Floor   Tickets   PricingCalculator
                |
                |
         List<ParkingSpot>
                |
                |
          ParkingSpot
                |
             occupied


Vehicle
  |
  +-- Bike
  +-- Car
  +-- Truck

ParkingTicket
  |
  +-- entryTime
  +-- exitTime
  +-- parkingSpot
  +-- vehicleType
  +-- registrationNumber

PricingCalculator
  |
  +-- FlatRatePricing
  +-- ProRataPricing
```

**Phase 1 goal:** a clean, understandable, interview-ready core system.

**Next work:** exceptions → basic JUnit learning/tests → final review.
