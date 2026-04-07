// VehicleType and ParkingSpotSize enums should use the same numeric values to allow for easy comparison in canVehicleFit method.
enum VehicleType {
  MOTORCYCLE,
  CAR,
  TRUCK,
}

enum ParkingSpotSize {
  SMALL,
  MEDIUM,
  LARGE,
}

class Vehicle {
  private plate: string;
  private type: VehicleType;

  constructor(plate: string, type: VehicleType, 
  ) {
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
  private id: string;
  private size: ParkingSpotSize;
  private parkedVehicle: Vehicle | null = null;

  constructor(id: string, size: ParkingSpotSize) {
    this.id = id;
    this.size = size;
  }

  getSize(): ParkingSpotSize {
    return this.size;
  }

  getParkedVehicle(): Vehicle | null {
    return this.parkedVehicle;
  }

  canVehicleFit(vehicle: Vehicle): boolean {
    switch(vehicle.getVehicleType()) {
      case VehicleType.MOTORCYCLE:
        return true;
      case VehicleType.CAR:
        return this.size === ParkingSpotSize.MEDIUM || this.size === ParkingSpotSize.LARGE;
      case VehicleType.TRUCK:
        return this.size === ParkingSpotSize.LARGE;
    }
  }

  isAvailable(): boolean {
    return this.parkedVehicle === null;
  }

  parkVehicle(vehicle: Vehicle): boolean {
    if (this.canVehicleFit(vehicle) && this.isAvailable()) {
      this.parkedVehicle = vehicle;
      return true;
    }

    return false;
  }

  leave(): void {
    this.parkedVehicle = null;
  }
}

class Floor {
  private level: number;
  private parkingSpots: ParkingSpot[];

  constructor(level: number) {
    this.level = level;
    this.parkingSpots = [];
  }

  addParkingSpot(parkingSpot: ParkingSpot): void {
    this.parkingSpots.push(parkingSpot);
  }


  findAvailableParkingSpotForVehicle(vehicle: Vehicle): ParkingSpot | null {
    let bestSpot: ParkingSpot | null = null;

    for(const spot of this.parkingSpots) {
      if(!spot.isAvailable() || !spot.canVehicleFit(vehicle)) continue;

      if(!bestSpot || spot.getSize() < bestSpot.getSize()) {
        bestSpot = spot;
      }
    }

    return bestSpot;
  }
}

class ParkingLot {
  private floors: Floor[] = [];
  private vehicleToParkingSpotMap: Map<string, ParkingSpot> = new Map();

  addFloor(floor: Floor): void {
    this.floors.push(floor);
  }

  addVehicleToMap(vehicle: Vehicle, parkingSpot: ParkingSpot): void {
    this.vehicleToParkingSpotMap.set(vehicle.getPlate(), parkingSpot);
  }

  removeVehicleFromMap(vehicle: Vehicle): void {
    this.vehicleToParkingSpotMap.delete(vehicle.getPlate());
  }

  findParkedVehicle(vehicle: Vehicle): ParkingSpot | null {
    return this.vehicleToParkingSpotMap.get(vehicle.getPlate()) || null;
  }

  parkVehicle(vehicle: Vehicle): boolean {
    for(const floor of this.floors) {
      const availableParkingSpotOnFloor = floor.findAvailableParkingSpotForVehicle(vehicle);

      if(!availableParkingSpotOnFloor) continue;

      if(availableParkingSpotOnFloor.parkVehicle(vehicle)) {
        this.addVehicleToMap(vehicle, availableParkingSpotOnFloor);
        return true;
      }
      else throw new Error('Cannot park the vehicle...');
    }

    return false;
  }

  leave(vehicle: Vehicle): boolean {
    const parkedSpot = this.findParkedVehicle(vehicle);
    
    if(parkedSpot) {
      parkedSpot.leave();
      this.removeVehicleFromMap(vehicle);
      return true;
    }

    return false;
  }
}

// Usage
const parkingLot = new ParkingLot();
const floor1 = new Floor(1);
floor1.addParkingSpot(new ParkingSpot('A1', ParkingSpotSize.SMALL));
floor1.addParkingSpot(new ParkingSpot('A2', ParkingSpotSize.SMALL));
floor1.addParkingSpot(new ParkingSpot('A3', ParkingSpotSize.MEDIUM));

const floor2 = new Floor(2);
floor2.addParkingSpot(new ParkingSpot('B1', ParkingSpotSize.MEDIUM));
floor2.addParkingSpot(new ParkingSpot('B2', ParkingSpotSize.LARGE));
floor2.addParkingSpot(new ParkingSpot('B3', ParkingSpotSize.MEDIUM));

parkingLot.addFloor(floor1);
parkingLot.addFloor(floor2);

const car1 = new Vehicle('AAA', VehicleType.CAR);
const car2 = new Vehicle('AAB', VehicleType.CAR);
const motorcycle1 = new Vehicle('A1A', VehicleType.MOTORCYCLE);
const motorcycle2 = new Vehicle('A2A', VehicleType.MOTORCYCLE);
const motorcycle3 = new Vehicle('A3A', VehicleType.MOTORCYCLE);
const truck1 = new Vehicle('ZZZ', VehicleType.TRUCK);
const truck2 = new Vehicle('ZYZ', VehicleType.TRUCK);

parkingLot.parkVehicle(car1); //return true
parkingLot.parkVehicle(car2); //return true
parkingLot.parkVehicle(motorcycle1); //return true
parkingLot.parkVehicle(motorcycle2); //return true
parkingLot.parkVehicle(motorcycle3); //return true
parkingLot.parkVehicle(truck1); //return true
parkingLot.parkVehicle(truck2); //return false, no more large spots