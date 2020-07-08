package com.company;

import java.awt.*;
import java.util.*;

public class Car
{

    /**
     * Pixels per tick.
     */
    public static final int SPEED = 100; //(50 * Surface.DELAY) / 1000;

    private int xPos;
    private int yPos;
    private int[] direction;

    private boolean moving;

    private Lane lane;

    private double timeTillGo;
    public Color color;
    public double distancedTraveled;

    public Car(Lane lane, Color color)
    {
        this.lane = lane;
        this.timeTillGo = 0;
        this.moving = true;
        this.color = color;
        this.distancedTraveled = 0;

        int speed = (SPEED * Surface.DELAY) / 1000;
        //int speed = SPEED;
        switch (this.lane)
        {
            case UP:
                this.xPos = Surface.WIDTH / 2;// + Surface.ROAD_WIDTH / 2;
                this.yPos = Surface.HEIGHT;
                this.direction = new int[]{0, -speed};
                break;
            case DOWN:
                this.xPos = Surface.WIDTH / 2 - Surface.ROAD_WIDTH / 2;
                this.yPos = 0;
                this.direction = new int[]{0, speed};
                break;
            case LEFT:
                this.xPos = Surface.WIDTH;
                this.yPos = Surface.HEIGHT / 2;// + Surface.ROAD_WIDTH / 2;
                this.direction = new int[]{-speed, 0};
                break;
            case RIGHT:
                this.xPos = 0;
                this.yPos = Surface.HEIGHT / 2 - Surface.ROAD_WIDTH / 2;
                this.direction = new int[]{speed, 0};
                break;
        }
    }

    public Car(Lane lane)
    {
        this(lane, new Color((int) Main.randomUniform(0, 255), (int) Main.randomUniform(0, 255), (int) Main.randomUniform(0, 255)));
    }

    public boolean getMoving()
    {
        return moving;
    }

    public int getxPos()
    {
        return xPos;
    }

    public int getyPos()
    {
        return yPos;
    }

    public Lane getLane()
    {
        return lane;
    }

    /**
     * Decide amount of time until car will start moving again.
     * In milliseconds.
     */
    public void go()
    {
        timeTillGo = new Random().nextDouble() * 5000;
    }

    public void setMoving(boolean moving)
    {
        this.moving = moving;
    }

    public boolean updateMoving()
    {
        return updateMoving(true);
    }

    public boolean updateMoving(boolean stopAtLight)
    {
        /*
        Traffic lights.
         */
        if (!Surface.getCtrl().getTrafficLight(lane).getState().equals(Color.GREEN) && stopAtLight)
        {
            switch (lane)
            {
                case UP:
                    if (distancedTraveled > Surface.HEIGHT / 2.0 - Surface.ROAD_WIDTH)
                    {
                        return false;
                    }
                    break;
                case DOWN:
                    if (distancedTraveled > Surface.HEIGHT / 2.0 - 1.5 * Surface.ROAD_WIDTH)
                    {
                        return false;
                    }
                    break;
                case RIGHT:
                    if (distancedTraveled > Surface.WIDTH / 2.0 - 1.5 * Surface.ROAD_WIDTH)
                    {
                        return false;
                    }
                    break;
                case LEFT:
                    if (distancedTraveled > Surface.WIDTH / 2.0 - Surface.ROAD_WIDTH)
                    {
                        return false;
                    }
            }
        }

        for (Car car : Surface.getCtrl().getAllCars().get(lane))
        {
            // If currently moving, decide when to stop.
            if (0 <= car.distancedTraveled - distancedTraveled && car.distancedTraveled - distancedTraveled < Surface.ROAD_WIDTH - 10 && car != this && moving)
            {
                return false;
            } else if (0 <= car.distancedTraveled - distancedTraveled && car.distancedTraveled - distancedTraveled < Surface.ROAD_WIDTH - 20 && car != this && !moving)
            {
                // If not currently moving, decide if should stay not moving.
                return false;
            }
        }
        return true;
    }

    public void stop()
    {
        moving = false;
    }

    public void update()
    {
        // Only stop at traffic light if hasn't passed yet.
        moving = updateMoving(!Surface.getCtrl().getPassedCars().contains(this));

        if (moving)
        {
            xPos += direction[0];
            yPos += direction[1];
            distancedTraveled += Math.abs(direction[0] + direction[1]);
        } else if (timeTillGo <= 0 && updateMoving())
        {
            timeTillGo = 0;
        } else if (!Surface.getCtrl().getTrafficLight(lane).getState().equals(Color.RED))
        {
            timeTillGo -= System.currentTimeMillis();
        }
    }
}
