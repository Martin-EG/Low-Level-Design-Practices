Design an **elevator system** that controls elevators in a building

Requirements
- The building has multiple elevators and multiple floors.
- A person can call an elevator from any floor (and indicates if we want to go up or down).
- Inside the elevator, a person can select the destination floor.
- Each elevator has three states: **Stand_by**, **Up**, **Down**.
- The system should assign the best elevator when someone call.
- Elevator has a limit of 10 persons or 1000kg of weight. If it is full, it cannot accept more people.

**Entities**

enum ElevatorDirection {
  IDLE,
  UP,
  DOWN
}

enum RequestType {
  UP,
  DOWN,
  DESTINATION
}

ElevatorRequest
- floor: int;
- type: RequestType
- weight: int
+ Request(floor: int, type: RequestType)
+ getFloor(): int
+ getType(): RequestType

Elevator
- id: string
- passengersLimit: int
- weightLimit: int
- direction: ElevatorDirection = ElevatorState.IDLE
- currentFloor: int = 0
- currentPassengers: int
- currentWeight: int
- requests: Set<ElevatorRequest>
+ isFull(): boolean
+ canPickUp(request: ElevatorRequest): boolean
+ addRequest(request: ElevatorRequest): void
+ getDirection(): ElevatorDirection
+ getCurrentFloor(): int
+ move(): void

ElevatorRepository
  - elevators: List<Elevator>
  + add(): void
  + getElevators(): List<Elevator>

interface RequestStrategy
 + assign(request, elevators): Elevator

SameDirectionStrategy implements RequestStrategy

ElevatorController
  - floors: int
  - requestStrategy: RequestStrategy
  - elevatorRepository: ElevatorRepository
  + requestElevator(floor: int, type: RequestType): boolean
  + step(): void //Simulate the move of the elevators
