enum VehicleType {
    MOTORCYCLE,
    CAR,
    TRUCK
}

enum ParkingSpotSize {
    SMALL = 1,
    MEDIUM = 2,
    LARGE = 3
}

class Vehicle {
    private string plate;
    private VehicleType type;

    constructor(string plate, VehicleType type) {
        this.plate = plate;
        this.type = type;
    }

    getPlate(): string {
        return this.plate;
    }

    getVehicleType(): VehicleType {
        return this.type;
    }
}

class ParkingSpot {
    private ParkingSpotSize size;
    private Vehicle | null parkedCar = null;

    constructor(ParkingSpotSize size) {
        this.size = size;
    }

    getSize(): ParkingSpotSize {
        return this.size;
    }

    getParkedVehicle(): Vehicle | null {
        return this.parkedVehicle;
    }

    isAvailable(): boolean {
        return this.parkedCar == null;
    }

    canVehicleFit(Vehicle vehicle): boolean {
        switch(vehicle.getVehicleType()) {
            case VehicleType.MOTORCYCLE:
                return true;
            case VehicleType.CAR:
                return this.size == ParkingSpotSize.MEDIUM || this.size == ParkingSpotSize.LARGE;
            case VehicleType.TRUCK:
                return this.size == ParkingSpotSize.LARGE;
        }
    }

    parkVehicle(Vehicle vehicle): boolean {
        if(this.canVehicleFit(vehicle) && this.isAvailable()) {
            this.parkedVehicle = vehicle;

            return true;
        }

        return false;
    }

    leave(): void {
        this.parkedVehicle = null;
    }
}

class ParkingSpotRepository {
    private List<ParkingSpot> parkingSpots;

    add(ParkingSpot parkingSpot): void {
        this.parkingSpots.add(parkingSpot);
    }

    getAvailableSpot(Vehicle vehicle): ParkingSpot | null {
        ParkingSpot bestSpot = null;
        
        for(ParkingSpot spot : this.parkingSpots) {
            if(!spot.isAvailable() || !spot.canVehicleFit(vehicle)) {
                continue;
            }

            if(!besSpot || spot.getSize() < bestSpot.getSize()) {
                bestSpot = spot;
            }
        }

        return bestSpot;
    }    
}

class Floor {
    private int floorNumber;
    private ParkingSpotRepository parkingSpotRepository;

    constructor(int floorNumber, ParkingSpotRepository parkingSpotRepository) {
        this.floorNumber = floorNumber;
        this.parkingSpotRepository = parkingSpotRepository;
    }

    getFloorNumber(): int {
        return this.floorNumber;
    }

    getParkingSpotRepository(): ParkingSpotRepository {
        return this.parkingSpotRepository;
    }
}

class FloorRepository {
    private List<Floor> floors;

    add(Floor floor): void {
        this.floors.add(floor);
    }

    getFloors(): List<Floor> {
        return this.floors;
    }
}

class ParkingLot {
    private FloorRepository floorRepository;
    private Map<string, ParkingSpot> vehicleToParkingSpotMap;

    constructor(FloorRepository floorRepository) {
        this.floorRepository = floorRepository;
        this.vehicleToParkingSpotMap = new HashMap<string, ParkingSpot>();
    }

    addVehicleToMap(Vehicle vehicle, ParkingSpot parkingSpot): void {
        this.vehicleToParkingSpotMap.put(vehicle.getPlate(), parkingSpot);
    }

    removeVehicleFromMap(Vehicle vehicle): void {
        this.vehicleToParkingSpotMap.remove(vehicle.getPlate());
    }

    findParkedVehicle(Vehicle vehicle): ParkingSpot | null {
        return this.vehicleToParkingSpotMap.get(vehicle.getPlate()) || null;
    }

    parkVehicle(Vehicle vehicle): boolean {
        for(Floor floor : this.floorRepository.getFloors()) {
            ParkingSpot spot = floor.getParkingSpotRepository().getAvailableSpot(vehicle);

            if(spot) {
                spot.parkVehicle(vehicle);
                this.addVehicleToMap(vehicle, spot);
                
                return true;
            }

        }

        return false;
    }
}