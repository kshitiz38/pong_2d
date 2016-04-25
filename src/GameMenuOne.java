/**
 * Created by Kshitiz Sharma on 25-Apr-16.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kshitiz Sharma
 */
public class GameMenuOne extends JFrame{
    public GameMenuOne(){

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.setBackground(Color.BLACK);

        JPanel options = new JPanel();
        options.setLayout(new GridBagLayout());

//        options.setBorder(new LineBorder(Color.yellow));
        options.setBackground(Color.BLACK);

        JLabel title = new JLabel("Select an Option", JLabel.CENTER);
        title.setFont(new Font("Verdana", Font.PLAIN, 18));
        title.setForeground(Color.WHITE);

        JLabel label = new JLabel("Classic Game", JLabel.CENTER);
        label.setFont(new Font("Verdana", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.RELATIVE;
        gbc.gridy = 0;
        gbc.ipady = 5;

        JLabel label1 = new JLabel("Multiplayer Game", JLabel.CENTER);
        label1.setFont(new Font("Verdana", Font.BOLD, 16));
        label1.setForeground(Color.WHITE);
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.fill = GridBagConstraints.RELATIVE;
        gbc1.gridy = 1;
        gbc1.ipady = 5;

        JLabel label2 = new JLabel("Achievements", JLabel.CENTER);
        label2.setFont(new Font("Verdana", Font.BOLD, 16));
        label2.setForeground(Color.WHITE);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.fill = GridBagConstraints.RELATIVE;
        gbc2.gridy = 2;
        gbc2.ipady = 5;

        JLabel label3 = new JLabel("Options", JLabel.CENTER);
        label3.setFont(new Font("Verdana", Font.BOLD, 16));
        label3.setForeground(Color.WHITE);
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.fill = GridBagConstraints.RELATIVE;
        gbc3.gridy = 3;
        gbc3.ipady = 5;

        options.add(label,gbc);
        options.add(label1,gbc1);
        options.add(label2,gbc2);
        options.add(label3,gbc3);


        JLabel labelDeveloper = new JLabel("Developed by BETABUGGERS", JLabel.CENTER);
        labelDeveloper.setFont(new Font("Verdana", Font.PLAIN, 14));
        labelDeveloper.setForeground(Color.WHITE);

        container.add(labelDeveloper, BorderLayout.SOUTH);
        container.add(options, BorderLayout.CENTER);
        container.add(title,BorderLayout.NORTH);

        setSize(500, 300);
        setLocationRelativeTo(null);
        setVisible(true);

        label.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                // TODO
            }
        });

        label1.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                // TODO
                InetAddress group = null;
                try {
                    group = InetAddress.getByName("127.0.0.1");
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
                int port = 9685;

                UDP pong = new UDP(group, port);
                pong.setUDP(pong);

                try {
                    pong.start();
                    //setVisible(false);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        label2.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                // TODO
            }
        });

        label3.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                // TODO
            }
        });



    }

}

