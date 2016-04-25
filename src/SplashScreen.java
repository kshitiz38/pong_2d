/**
 * Created by Kshitiz Sharma on 25-Apr-16.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kshitiz Sharma
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.Border;

public class SplashScreen extends JWindow {


    private static JProgressBar progressBar = new JProgressBar();

    private static SplashScreen execute;
    private static GameMenuOne gm;

    private static int count;
    private static Timer timer1;

    public SplashScreen() {

        Container container = getContentPane();
        container.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(10, 10, 480, 260);
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.BLACK);
        container.add(panel);

        JLabel labelTitle = new JLabel("PONG GAME 2D", JLabel.CENTER);
        JLabel labelDeveloper = new JLabel("Developed by BETABUGGERS", JLabel.CENTER);

        labelTitle.setFont(new Font("Verdana", Font.BOLD, 25));
        labelTitle.setForeground(Color.WHITE);
        labelTitle.setBounds(0, 0, 280, 30);

        labelDeveloper.setFont(new Font("Verdana", Font.BOLD, 14));
        labelDeveloper.setForeground(Color.WHITE);
        labelDeveloper.setBounds(0, 0, 280, 30);

        panel.add(labelTitle, BorderLayout.CENTER);
        panel.add(labelDeveloper, BorderLayout.SOUTH);

        progressBar.setBounds(55, 180, 390, 15);
        //progressBar.setIndeterminate(true);
        progressBar.setBackground(Color.WHITE);
        progressBar.setStringPainted(true);
        container.add(progressBar);
        loadProgressBar();

        setSize(500, 300);
        setLocationRelativeTo(null);

        container.setBackground(Color.BLACK);

        final int pause = 3000;
        final Runnable closerRunner = new Runnable()
        {
            public void run()
            {
                setVisible(false);
                gm = new GameMenuOne();
                dispose();
            }
        };

        final Runnable waitRunner = new Runnable()
        {
            public void run()
            {
                try
                {
                    Thread.sleep(pause);
                    timer1.stop();
                    SwingUtilities.invokeAndWait(closerRunner);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    // can catch InvocationTargetException
                    // can catch InterruptedException
                }
            }
        };
        setVisible(true);
        Thread splashThread = new Thread(waitRunner, "SplashThread");
        splashThread.start();

    }

    public void loadProgressBar() {
        ActionListener al = new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                count++;
                progressBar.setValue(count);
            }
        };
        timer1 = new Timer(20, al);
        timer1.start();
    }

    public static void main(String[] args) {

        execute = new SplashScreen();

    }

}

