enum RequestType {
    UP,
    DOWN,
    DESTINATION
}

public class ElevatorRequest {
    private int floor;
    private RequestType type;

    public ElevatorRequest(int floor, RequestType type) {
        this.floor = floor;
        this.type = type;
    }

    RequestType getType() {
        return this.type;
    }

    int getFloor() {
        return this.floor;
    }
}


import java.util.HashSet;
import java.util.Set;

enum ElevatorDirection {
    UP,
    DOWN,
    IDLE
}

public class Elevator {
    private int passengersLimit = 10;
    private int weightLimit = 1000;
    private ElevatorDirection direction = ElevatorDirection.IDLE;
    private int currentFloor = 0;
    private int currentPassengers = 0;
    private int currentWeight = 0;
    private Set<ElevatorRequest> requests = new HashSet<>();
    private Map<Integer, Integer> passengersWeightByFloor = new HashMap<>();
    private Map<Integer, Integer> passengersCountByFloor = new HashMap<>();

    ElevatorDirection getDirection() {
        return this.direction;
    }

    int getCurrentFloor() {
        return this.currentFloor;
    }

    boolean isFull() {
        return this.currentPassengers == this.passengersLimit || this.currentWeight >= this.weightLimit;
    }

    boolean canPickUp(ElevatorRequest request) {
        if(this.isFull()) return false;
        if (this.direction == ElevatorDirection.IDLE) return true;
        if(request.getType() == RequestType.UP && this.direction == ElevatorDirection.DOWN) return false;
        if(request.getType() == RequestType.UP &&
            this.direction == ElevatorDirection.UP &&
            request.getFloor() < this.currentFloor
        ) return false;
        if(request.getType() == RequestType.DOWN && this.direction == ElevatorDirection.UP) return false;
        if(request.getType() == RequestType.DOWN &&
                this.direction == ElevatorDirection.DOWN &&
                request.getFloor() > this.currentFloor
        ) return false;

        return true;
    }

    boolean addRequest(ElevatorRequest request) {
        if(this.isFull() || !this.canPickUp(request)) return false;

        this.requests.add(request);
        return true;
    }

    boolean boardPassenger(int destinationFloor, int passengerWeight) {
        if(this.isFull()) return false;

        this.currentPassengers++;
        this.passengersCountByFloor.put(destinationFloor,
                this.passengersCountByFloor.getOrDefault(destinationFloor, 0) + 1
        );
        this.currentWeight += passengerWeight;
        this.passengersWeightByFloor.put(destinationFloor,
                this.passengersWeightByFloor.getOrDefault(destinationFloor, 0) + passengerWeight
        );

        ElevatorRequest destination = new ElevatorRequest(destinationFloor, RequestType.DESTINATION);
        this.requests.add(destination);

        return true;
    }

    void move() {
        if(this.direction == ElevatorDirection.IDLE && !this.requests.isEmpty()) {
            ElevatorRequest next = this.requests.iterator().next();
            this.direction = next.getFloor() > this.currentFloor
                    ? ElevatorDirection.UP
                    : ElevatorDirection.DOWN;
        }

        if(this.direction == ElevatorDirection.UP) this.currentFloor++;
        else if(this.direction == ElevatorDirection.DOWN) this.currentFloor--;

        final int arriveFloor = this.currentFloor;
        this.requests.removeIf(request -> {
            if(request.getFloor() != arriveFloor) return false;

            if(request.getType() == RequestType.DESTINATION) {
                int passengersByFloor = this.passengersCountByFloor.get(arriveFloor);
                this.currentPassengers -= passengersByFloor;

                int passengersWeight = this.passengersWeightByFloor.get(arriveFloor);
                this.passengersWeightByFloor.remove(arriveFloor);
                this.currentWeight -= passengersWeight;

                return true;
            }

            if(request.getType() == RequestType.UP || request.getType() == RequestType.DOWN) {
                // Simulate request destination
                return true;
            }

            return false;
        });

        if(this.requests.isEmpty()) {
            this.direction = ElevatorDirection.IDLE;
        }

    }
}

import java.util.List;

public class ElevatorRepository {
    private List<Elevator> elevators = new ArrayList<>();

    void add(Elevator elevator) {
        this.elevators.add(elevator);
    }

    List<Elevator> getElevators() {
        return this.elevators;
    }
}

import java.util.List;

public interface RequestStrategy {
    Elevator assign(ElevatorRequest request, List<Elevator> elevators);
}

public class NearestElevatorStrategy implements RequestStrategy{
    @Override
    public Elevator assign(ElevatorRequest request, List<Elevator> elevators) {
        int nearestDistance = 1000;
        Elevator nearestElevator = null;

        for(Elevator elevator : elevators) {
            if (!elevator.canPickUp(request)) continue;

            int distance = Math.abs(elevator.getCurrentFloor() - request.getFloor());
            if(distance < nearestDistance) {
                nearestDistance = distance;
                nearestElevator = elevator;
            }
        }

        return nearestElevator;
    }
}

public class ElevatorController {
    private int floors;
    private RequestStrategy requestStrategy;
    private ElevatorRepository elevatorRepository;

    public ElevatorController(int floors, RequestStrategy requestStrategy, ElevatorRepository elevatorRepository) {
        this.floors = floors;
        this.requestStrategy = requestStrategy;
        this.elevatorRepository = elevatorRepository;
    }

    boolean requestElevator(ElevatorRequest request) {
        Elevator elevator = this.requestStrategy.assign(request, this.elevatorRepository.getElevators());

        if(elevator == null) throw new Error("No elevators available...");

        if(!elevator.addRequest(request)) {
            throw new Error("Request cannot be added");
        }

        System.out.println("Request added successfully");
        return true;
    }

    void step() {
        for(Elevator elevator : this.elevatorRepository.getElevators()) {
            elevator.move();
        }
    }
}
