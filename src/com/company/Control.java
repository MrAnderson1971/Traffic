package com.company;

import java.awt.*;
import java.util.*;

import static com.company.Lane.LEFT;
import static com.company.Lane.RIGHT;

public class Control
{

    public static final int MIN_CAR_TIME = 100000;
    public static final int MAX_CAR_TIME = 300000;
    public static final long MIN_WAIT_TIME = 10000;
    public static final int MAX_CAR_THRESHOLD = 5;

    private LinkedList<Car> carRight;
    private LinkedList<Car> carLeft;
    private LinkedList<Car> carDown;
    private LinkedList<Car> carUp;
    private LinkedList<Car> passedCars;
    private HashMap<Lane, LinkedList<Car>> allCars;

    /*
    Left/right and up/down should always be in sync.
     */
    private TrafficLight rightTrafficLight;
    private TrafficLight leftTrafficLight;
    private TrafficLight downTrafficLight;
    private TrafficLight upTrafficLight;

    private long timeTillNextCar;
    private int horizontalCars;
    private int verticalCars;

    public Control()
    {
        this.carDown = new LinkedList<>();
        this.carLeft = new LinkedList<>();
        this.carRight = new LinkedList<>();
        this.carUp = new LinkedList<>();
        this.passedCars = new LinkedList<>();

        this.allCars = new HashMap<>();
        this.allCars.put(Lane.UP, this.carUp);
        this.allCars.put(Lane.DOWN, this.carDown);
        this.allCars.put(RIGHT, this.carRight);
        this.allCars.put(LEFT, this.carLeft);

        this.upTrafficLight = new TrafficLight(Lane.UP);
        this.downTrafficLight = new TrafficLight(Lane.DOWN);
        this.leftTrafficLight = new TrafficLight(LEFT);
        this.rightTrafficLight = new TrafficLight(RIGHT);

        this.timeTillNextCar = (long) Main.randomUniform(MIN_CAR_TIME, MAX_CAR_TIME);
        this.upTrafficLight.go();
        this.downTrafficLight.go();

        horizontalCars = verticalCars = 0;
    }

    public HashMap<Lane, LinkedList<Car>> getAllCars()
    {
        return allCars;
    }

    public LinkedList<Car> getPassedCars()
    {
        return passedCars;
    }

    public TrafficLight getTrafficLight(Lane lane)
    {
        switch (lane)
        {
            case UP:
                return upTrafficLight;
            case DOWN:
                return downTrafficLight;
            case RIGHT:
                return rightTrafficLight;
            case LEFT:
                return leftTrafficLight;
            default:
                throw new IllegalArgumentException("This statement should be unreachable.");
        }
    }

    private void addCar()
    {
        if (timeTillNextCar <= 0)
        {
            timeTillNextCar = (long) Main.randomUniform(MIN_CAR_TIME, MAX_CAR_TIME) + 10 * System.currentTimeMillis();
            Lane randomLane = (Lane) Main.randomChoice(allCars.keySet());
            Car newCar = new Car(randomLane);
            newCar.setMoving(newCar.updateMoving());

            // Make sure there is room to add a new car.
            if (newCar.getMoving())
            {
                allCars.get(randomLane).add(new Car(randomLane));
            }
        }
    }

    public void update()
    {

        horizontalCars = carRight.size() + carLeft.size();
        verticalCars = carUp.size() + carDown.size();

        downTrafficLight.update();
        upTrafficLight.update();
        leftTrafficLight.update();
        rightTrafficLight.update();

        /*
        Decide when to switch traffic lights.
         */
        if (downTrafficLight.getState().equals(Color.RED) && leftTrafficLight.getState().equals(Color.GREEN)
                && verticalCars > MAX_CAR_THRESHOLD && System.currentTimeMillis() - downTrafficLight.getTimeOfLastChange() > MIN_WAIT_TIME)
        {
            leftTrafficLight.stop();
            rightTrafficLight.stop();
        }

        if (leftTrafficLight.getState().equals(Color.RED) && downTrafficLight.getState().equals(Color.GREEN)
                && horizontalCars > MAX_CAR_THRESHOLD && System.currentTimeMillis() - leftTrafficLight.getTimeOfLastChange() > MIN_WAIT_TIME)
        {
            downTrafficLight.stop();
            upTrafficLight.stop();
        }

        /*
        Determine if car has passed traffic light.
         */
        for (Lane l : allCars.keySet())
        {
            Iterator<Car> cars = allCars.get(l).iterator();
            while (cars.hasNext())
            {
                Car car = cars.next();
                car.update();
                switch (car.getLane())
                {
                    case UP:
                    case DOWN:
                        if (car.distancedTraveled > Surface.HEIGHT / 2.0 + Surface.ROAD_WIDTH / 2.0)
                        {
                            if (!Surface.getCtrl().getTrafficLight(car.getLane()).getState().equals(Color.RED))
                            {
                                passedCars.add(car);
                                cars.remove();
                            }
                        }
                        break;
                    case RIGHT:
                    case LEFT:

                        if (car.distancedTraveled > Surface.WIDTH / 2.0 + Surface.ROAD_WIDTH / 2.0)
                        {
                            if (!Surface.getCtrl().getTrafficLight(car.getLane()).getState().equals(Color.RED))
                            {
                                passedCars.add(car);
                                cars.remove();
                            }
                        }
                }
            }
            for (Car car : allCars.get(l))
            {
                car.update();
            }
        }

        // Remove passed cars.
        Iterator<Car> cars = passedCars.iterator();
        while (cars.hasNext())
        {
            Car car = cars.next();
            //car.setMoving(car.updateMoving(false));
            car.update();
            if (0 > car.getxPos() || car.getxPos() > Surface.WIDTH || 0 > car.getyPos() || car.getyPos() > Surface.HEIGHT)
            {
                cars.remove();
            }
        }

        timeTillNextCar -= System.currentTimeMillis();
        addCar();
    }
}
