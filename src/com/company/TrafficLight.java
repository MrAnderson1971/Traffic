package com.company;

import java.awt.*;

public class TrafficLight
{

    public static final int YELLOW_LIGHT_TIME = 30000;

    private Lane lane;
    private Color state;

    private int xPos;
    private int yPos;

    private long timeTillLightChange;
    private long timeOfLastChange;

    public TrafficLight(Lane lane)
    {
        this.lane = lane;
        this.state = Color.RED;

        this.timeOfLastChange = System.currentTimeMillis();

        switch (this.lane)
        {
            case UP:
                this.xPos = Surface.WIDTH / 2;// + Surface.ROAD_WIDTH / 2;
                this.yPos = Surface.HEIGHT / 2;// + Surface.ROAD_WIDTH / 2;
                break;
            case DOWN:
                this.xPos = Surface.WIDTH / 2 - Surface.ROAD_WIDTH / 2;
                this.yPos = Surface.HEIGHT / 2 - Surface.ROAD_WIDTH / 2;
                break;
            case RIGHT:
                this.xPos = Surface.WIDTH / 2 - Surface.ROAD_WIDTH / 2;
                this.yPos = Surface.HEIGHT / 2;// + Surface.ROAD_WIDTH / 2;
                break;
            case LEFT:
                this.xPos = Surface.WIDTH / 2;// + Surface.ROAD_WIDTH / 2;
                this.yPos = Surface.HEIGHT / 2 - Surface.ROAD_WIDTH / 2;
        }
    }

    public Color getState()
    {
        return state;
    }

    public int getxPos()
    {
        return xPos;
    }

    public int getyPos()
    {
        return yPos;
    }

    public long getTimeOfLastChange()
    {
        return timeOfLastChange;
    }

    public void setTimeOfLastChange()
    {
        timeOfLastChange = System.currentTimeMillis();
    }

    public void go()
    {
        setTimeOfLastChange();
        state = Color.GREEN;
    }

    public void stop()
    {
        setTimeOfLastChange();
        timeTillLightChange = YELLOW_LIGHT_TIME + 10 * System.currentTimeMillis();
        state = Color.YELLOW;
    }

    public void update()
    {
        if (state.equals(Color.YELLOW))
        {
            timeTillLightChange -= System.currentTimeMillis();
        }
        if (state.equals(Color.YELLOW) && timeTillLightChange < 0)
        {
            timeTillLightChange = 0;
            state = Color.RED;

            if (this.equals(Surface.getCtrl().getTrafficLight(Lane.DOWN)))
            {
                Surface.getCtrl().getTrafficLight(Lane.LEFT).go();
                Surface.getCtrl().getTrafficLight(Lane.RIGHT).go();
            } else if (this.equals(Surface.getCtrl().getTrafficLight(Lane.LEFT)))
            {
                Surface.getCtrl().getTrafficLight(Lane.UP).go();
                Surface.getCtrl().getTrafficLight(Lane.DOWN).go();
            }
        }
    }
}
