package com.company;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class Main extends JFrame
{

    public Main()
    {
        init();
    }

    private void init()
    {
        add(new Surface());

        setResizable(false);
        pack();

        setTitle("Traffic");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args)
    {
        EventQueue.invokeLater(() ->
                {
                    JFrame ex = new Main();
                    ex.setVisible(true);
                }
        );
    }

    /**
     * Returns a random double between min and max.
     * @param min Inclusive
     * @param max Exclusive
     * @return A pseudorandom double between min and max.
     */
    public static double randomUniform(double min, double max)
    {
        return min + (max - min) * new Random().nextDouble();
    }

    public static Object randomChoice(Collection<?> collection)
    {
        int rand = new Random().nextInt(collection.size());
        Iterator<?> iter = collection.iterator();
        for (int i = 0; i < rand; i++)
        {
            iter.next();
        }
        return iter.next();
    }
}
