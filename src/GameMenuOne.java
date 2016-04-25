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
public class GameMenuOne extends JFrame {

    JPanel current;

    public GameMenuOne() {

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.setBackground(Color.BLACK);

        optionsMain panel = new optionsMain();
        current = panel.getPanel();
        container.add(current, BorderLayout.CENTER);

        setSize(500, 300);
        setLocationRelativeTo(null);
        setVisible(true);


    }


    class optionsMain extends JPanel {

        private JPanel optionsMain;

        public optionsMain() {

            optionsMain = new JPanel();
            optionsMain.setLayout(new GridBagLayout());

//                optionsMain.setBorder(new LineBorder(Color.yellow));
            optionsMain.setBackground(Color.BLACK);

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
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // TODO
                    optionsClassic panel = new optionsClassic();
                    Container container = getContentPane();
                    container.remove(current);
                    current = panel.getPanel();
                    container.add(current, BorderLayout.CENTER);
                    container.revalidate();
                }
            });

            JLabel label1 = new JLabel("Multiplayer Game", JLabel.CENTER);
            label1.setFont(new Font("Verdana", Font.BOLD, 16));
            label1.setForeground(Color.WHITE);
            GridBagConstraints gbc1 = new GridBagConstraints();
            gbc1.fill = GridBagConstraints.RELATIVE;
            gbc1.gridy = 1;
            gbc1.ipady = 5;
            label1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
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

            JLabel label2 = new JLabel("Achievements", JLabel.CENTER);
            label2.setFont(new Font("Verdana", Font.BOLD, 16));
            label2.setForeground(Color.WHITE);
            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.fill = GridBagConstraints.RELATIVE;
            gbc2.gridy = 2;
            gbc2.ipady = 5;
            label2.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // TODO
                }
            });

            JLabel label3 = new JLabel("Options", JLabel.CENTER);
            label3.setFont(new Font("Verdana", Font.BOLD, 16));
            label3.setForeground(Color.WHITE);
            GridBagConstraints gbc3 = new GridBagConstraints();
            gbc3.fill = GridBagConstraints.RELATIVE;
            gbc3.gridy = 3;
            gbc3.ipady = 5;
            label3.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // TODO
                }
            });

            optionsMain.add(label, gbc);
            optionsMain.add(label1, gbc1);
            optionsMain.add(label2, gbc2);
            optionsMain.add(label3, gbc3);


            JLabel labelDeveloper = new JLabel("Developed by BETABUGGERS", JLabel.CENTER);
            labelDeveloper.setFont(new Font("Verdana", Font.PLAIN, 14));
            labelDeveloper.setForeground(Color.WHITE);

        }

        public JPanel getPanel() {
            return optionsMain;
        }
    }


    class optionsClassic extends JPanel {

        private JPanel optionsClassic;

        public optionsClassic() {

            optionsClassic = new JPanel();
            optionsClassic.setLayout(new GridBagLayout());
            optionsClassic.setBackground(Color.BLACK);

            JLabel label = new JLabel("Single Player", JLabel.CENTER);
            label.setFont(new Font("Verdana", Font.BOLD, 16));
            label.setForeground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.RELATIVE;
            gbc.gridy = 0;
            gbc.ipady = 5;
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new Pong("Single");
                }
            });

            JLabel label1 = new JLabel("Two Player", JLabel.CENTER);
            label1.setFont(new Font("Verdana", Font.BOLD, 16));
            label1.setForeground(Color.WHITE);
            GridBagConstraints gbc1 = new GridBagConstraints();
            gbc1.fill = GridBagConstraints.RELATIVE;
            gbc1.gridy = 1;
            gbc1.ipady = 5;
            label1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new Pong("2Player");
                }
            });

            JLabel label2 = new JLabel("Back", JLabel.CENTER);
            label2.setFont(new Font("Verdana", Font.BOLD, 16));
            label2.setForeground(Color.WHITE);
            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.fill = GridBagConstraints.RELATIVE;
            gbc2.gridy = 2;
            gbc2.ipady = 5;
            label2.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }
            });

            optionsClassic.add(label, gbc);
            optionsClassic.add(label1, gbc1);
            optionsClassic.add(label2, gbc2);

//                container.remove(optionsMain);
//                container.add(optionsClassic, BorderLayout.CENTER);
//                container.revalidate();
        }

        public JPanel getPanel() {
            return optionsClassic;
        }
    }
}


