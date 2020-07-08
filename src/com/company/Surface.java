package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class Surface extends JPanel implements ActionListener
{

    private Timer timer;
    private static Control ctrl;

    public static final int WIDTH = 1020;
    public static final int HEIGHT = 960;

    public static final int ROAD_WIDTH = 100;

    public static final int DELAY = 100;

    public Surface()
    {
        init();
    }

    private void init()
    {
        ctrl = new Control();

        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        timer = new Timer(DELAY, this);
        timer.start();
    }

    public static Control getCtrl()
    {
        return ctrl;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        render(g);
    }

    private void render(Graphics g)
    {
        g.setColor(Color.WHITE);
        g.fillRect((WIDTH / 2) - ROAD_WIDTH / 2, 0, ROAD_WIDTH, HEIGHT);
        g.fillRect(0, (HEIGHT / 2) - ROAD_WIDTH / 2, WIDTH, ROAD_WIDTH);

        for (Lane lane : new ArrayList<Lane>(Arrays.asList(Lane.UP, Lane.DOWN, Lane.RIGHT, Lane.LEFT)))
        {
            TrafficLight light = ctrl.getTrafficLight(lane);
            g.setColor(light.getState());
            g.fillRect(light.getxPos(), light.getyPos(), Surface.ROAD_WIDTH / 2, Surface.ROAD_WIDTH / 2);
        }

        for (Lane l : ctrl.getAllCars().keySet())
        {
            for (Car car : ctrl.getAllCars().get(l))
            {
                g.setColor(car.color);
                g.fillRect(car.getxPos(), car.getyPos(), Surface.ROAD_WIDTH / 2, Surface.ROAD_WIDTH / 2);
            }
        }

        for (Car car : ctrl.getPassedCars())
        {
            g.setColor(car.color);
            g.fillRect(car.getxPos(), car.getyPos(), Surface.ROAD_WIDTH / 2, Surface.ROAD_WIDTH / 2);
        }

        g.setColor(Color.WHITE);
        g.drawString("North: " + ctrl.getAllCars().get(Lane.UP).size(), 0, 10);
        g.drawString("South: " + ctrl.getAllCars().get(Lane.DOWN).size(), 0, 20);
        g.drawString("West: " + ctrl.getAllCars().get(Lane.LEFT).size(), 0, 30);
        g.drawString("East: " + ctrl.getAllCars().get(Lane.RIGHT).size(), 0, 40);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        ctrl.update();
        repaint();
    }
}
