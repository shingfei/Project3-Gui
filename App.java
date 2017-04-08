package com.company;

/**
 * Created by shing on 28-3-2017.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class App
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {

            public void run()
            {
                new ReadCard();
            }
        });
    }
}
