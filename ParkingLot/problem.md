Design a parking lot system that can support different types of vehicles and parking spots.

Qs before start:
- Our parking lot support different vehicles? **There are three vehicles we support; Motorcycle, Car, Truck**
- Our parking lot has spots of different sizes? **Spots can be; Small, Medium, Large**
- Our parking lot system should be scalable to multiple floors or it is always a single floor? **Yes, we allow multiple floor**
- Following the last question, how many spots should have each floor? **Each floor has multiple spots**

More rules we have:
- **Motorcycle** can park in any spot.
- **Car** can park in medium and large spots.
- **Truck** can park only in large spots.


**Entities using repositories**
enum VehicleType {
  MOTORCYCLE,
  CAR,
  TRUCK
}

enum ParkingSpotSize {
  SMALL,
  MEDIUM,
  LARGE
}

Vehicle
 - plate: string
 - type: VehicleType
 + getVehicleType(): VehicleType

ParkingSpot
 - id: string
 - parkedVehicle: Vehicle | null
 - size: ParkingSpotSize
 + canVehicleFit(vehicle: Vehicle): boolean
 + isAvailable(): boolean
 + park(): void
 + leave(): void

 ParkingSpotRepository
 - parkingSpots: ParkingSpot[]
 + add(): boolean
 + findParkingSpotBySlotNumber(slotNumber: string): ParkingSpot

Floor
 - id: string
 - parkingSpotRepository: ParkingSpotRepository
 - isFull: boolean
 + addParkingSpot(ParkingSpot): boolean

FloorsRepository
 - floors: Floor[]
 + add(floor: Floor): boolean
 + findFloorByFloorNumber(floorNumber: number): Floor

ParkingLot
- floorRepository: FloorRepository
+ parkCar(vehicle: Vehicle): boolean;
+ leave(vehicle: Vehicle): boolean

**Entities without repositories**
enum VehicleType {
  MOTORCYCLE,
  CAR,
  TRUCK
}

enum ParkingSpotSize {
  SMALL,
  MEDIUM,
  LARGE
}

Vehicle
 - plate: string
 - type: VehicleType
 + getVehicleType(): VehicleType

ParkingSpot
 - id: string
 - parkedVehicle: Vehicle | null
 - size: ParkingSpotSize
 + canVehicleFit(vehicle: Vehicle): boolean
 + isAvailable(): boolean
 + park(): void
 + leave(): void

Floor
 - level: number
 - parkingSpots: ParkingSpot[]
 - isFull: boolean
 + addParkingSpot(ParkingSpot): boolean

ParkingLot
- floors: Floor[]
+ addFloor(Floor): boolean
+ parkVehicle(vehicle: Vehicle): boolean;
+ leave(vehicle: Vehicle): boolean