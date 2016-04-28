//import org.json.*;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;


public class UDP implements Runnable, WindowListener, ActionListener {

    private boolean virtualHost;

    private JSONObject key_event;
    private JSONObject ballPosition;
    protected InetAddress inetAddress;
    private JSONObject player_score;
    private JSONObject ball_and_score;
    protected int port;
    protected ArrayList<Machine> playerlist;
    private static UDP udp;
    //  protected DatagramSocket socket;
    protected DatagramSocket socket;
    protected DatagramPacket incoming;
    protected DatagramSocket ackSocket;
    DatagramSocket datagramSocketToUnblock;
    private boolean started = false;
    public int lobby_Port;

    public UDP(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
        createAndShowGUI();
        try {
            ackSocket = new DatagramSocket(9500);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        try {
            datagramSocketToUnblock = new DatagramSocket(9501);
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
    }

    public UDP(InetAddress inetAddress, int port, int port_lobby) {
        this(inetAddress,port);
        this.lobby_Port = port_lobby;

    }

    public void setUDP(UDP udp) {
        this.udp = udp;
    }

    public ArrayList<Machine> getPlayerlist() {
        return playerlist;
    }

    public void setPlayerlist(ArrayList<Machine> playerlist) {
        this.playerlist = playerlist;
    }

    protected JFrame frameMain;

    protected TextField input;
    protected LobbyServer lobbyServer;
    protected Thread listener;

    protected void initNet() throws IOException {
        socket = new DatagramSocket(port);
        incoming = new DatagramPacket(new byte[65508], 65508);
    }

    public synchronized void start() throws IOException {
        if (listener == null) {
            initNet();
            listener = new Thread(this);
            listener.start();
        }
    }

    public synchronized void stop() throws IOException {
        if (listener != null) {
            listener.interrupt();
            listener = null;
            socket.close();
        }
    }

    public void windowOpened(WindowEvent event) {
        input.requestFocus();
    }

    public void windowClosing(WindowEvent event) {
        try {
            stop();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void windowClosed(WindowEvent event) {
    }

    public void windowIconified(WindowEvent event) {
    }

    public void windowDeiconified(WindowEvent event) {
    }

    public void windowActivated(WindowEvent event) {
    }

    public void windowDeactivated(WindowEvent event) {
    }

    public void actionPerformed(ActionEvent event) {

    }

    //
    protected synchronized void handleIOException(IOException ex) {
        if (listener != null) {
            if (listener != Thread.currentThread())
                listener.interrupt();
            listener = null;
            socket.close();
        }
        ex.printStackTrace();
    }

    TextArea textArea;

    //run method for listener thread
    public void run() {
        try {
            while (!Thread.interrupted()) {
                incoming.setLength(incoming.getData().length);
                socket.receive(incoming);
                String message = new String(incoming.getData(), 0, incoming.getLength());
                JSONObject jsonObject = new JSONObject(message);
                String msg_type = jsonObject.getString("MessageType");

                switch (msg_type) {
                    case "Ball_Moving":
                        ballPosition = jsonObject;
                        sendACK(incoming,socket);
                        break;
                    case "Paddle_Moving":
                        break;
                    case "Ball_And_Score":
                        ball_and_score = jsonObject;
                        sendACK(incoming,socket);
                        break;
                    case "Wall_Hit":
                        break;
                    case "Paddle_Hit":
                        break;
                    case "Paddle_Remove":
                        break;
                    case "Win":
                        break;
                    case "Player_Score":
                        System.out.println("Score received");
                        player_score = jsonObject;
                        sendACK(incoming,socket);
                        break;
                    case "Key_Event":
                        key_event=jsonObject;
//                        sendACK(incoming,socket);
                        break;
                    case "Start":
                        System.out.println("Start");
                        if (started) {
                            break;
                        }
                        started = true;
                        sendACK(incoming,socket);
                        checkRoomMsg();//to get latest players
                        new Pong(udp);

                        break;
                    case "lobbyResp":
                        textArea.setText("");
                        playerlist = new ArrayList<>();
                        System.out.println(jsonObject.getInt("numPlayers"));
                        for (int i = 0; i < jsonObject.getInt("numPlayers"); i++) {
                            String ip_port = jsonObject.getString("Player" + i);
                            textArea.append(ip_port + "\n");
                            String[] ip_ports = ip_port.split(":");
                            Machine machine = new Machine(ip_ports[0], Integer.parseInt(ip_ports[1]));
                            playerlist.add(machine);
                        }
                        break;
                    default:
                        System.out.println("Unknown Message Type");
                }

            }
        } catch (IOException ex) {
            handleIOException(ex);
        }
    }

    public boolean getVirtualHost() {
        return virtualHost;
    }

    //send key event
    public void sendKeyEvent(int event_code, String type, int playerIndex) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("key_event_code", event_code);
        jsonObject.put("MessageType", "Key_Event");
        jsonObject.put("event_type", type);
        jsonObject.put("playerIndex", playerIndex);
        String jsonString = jsonObject.toString();
        byte[] bytes = jsonString.getBytes();
//        sendMessageToAllExcludingMeWithAcknowledgeMsg(bytes);
        sendMessageToAllExcludingMeWithoutAcknowledgeMsg(bytes);
        //not synchronizing the ball first time it enters space
    }

    private void sendMessageToAllExcludingMeWithoutAcknowledgeMsg(byte[] bytes) {
        InetAddress myInetAddress = null;
        try {
            myInetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        assert myInetAddress != null;
        String myIp = myInetAddress.getHostAddress();
        //If resends exceed a particular number assume player lost and unblock the socket.receive by sending message to itself
        //and perform necessary actions like updating the playerlist and replacing the code with AI
        for (Machine machine : playerlist) {
            if (!machine.getIp().equals(myIp)) {//excluding me
                //DataPacket formed
                InetAddress ip_of_machine = null;
                try {
                    ip_of_machine = InetAddress.getByName(machine.getIp());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, ip_of_machine, machine.getPort());

                //Send DataPacket
                try {
                    socket.send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //send score
    public void sendPlayerScore(int player_1_score, int player_2_score, int player_3_score, int player_4_score) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType", "Player_Score");
        jsonObject.put("player_1_score", player_1_score);
        jsonObject.put("player_2_score", player_2_score);
        jsonObject.put("player_3_score", player_3_score);
        jsonObject.put("player_4_score", player_4_score);
        String jsonString = jsonObject.toString();
        byte[] bytes = jsonString.getBytes();
        sendMessageToAllExcludingMeWithAcknowledgeMsg(bytes);
    }

    //retreiving keyEvent
    public JSONObject getKeyEvent() {
        return key_event;
    }

    public JSONObject getPlayerScore() {
        return player_score;
    }

    public JSONObject getPlayerScoreAndBall() {
        return ball_and_score;
    }

    public void resetKeyEvent() {
        key_event = null;
    }

    public void resetScoreEvent() {
        player_score = null;
    }


    // Send Message Stuff


    public void resetBallPosition() {
        this.ballPosition = null;
    }

    public void resetBallAndScore() {
        this.ball_and_score = null;
    }


    public void sendBallInfo(double ball_x, double ball_y, double BALL_SPEEDX, double BALL_SPEEDY, double vel_x, double vel_y, int ball_id) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType", "Ball_Moving");
        jsonObject.put("ball_x", ball_x);
        jsonObject.put("ball_y", ball_y);
        jsonObject.put("vel_x", vel_x);
        jsonObject.put("vel_y", vel_y);
        jsonObject.put("BALL_SPEEDX", BALL_SPEEDX);
        jsonObject.put("BALL_SPEEDY", BALL_SPEEDY);
        jsonObject.put("ball_id", ball_id);

        String jsonString = jsonObject.toString();
        byte[] bytes = jsonString.getBytes();
        sendMessageToAllExcludingMeWithAcknowledgeMsg(bytes);
    }

    public void sendBallAndScore(double ball_x, double ball_y, double BALL_SPEEDX, double BALL_SPEEDY, double vel_x, double vel_y, int ball_id, int player_1_score, int player_2_score, int player_3_score, int player_4_score)
    {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType", "Ball_And_Score");
        jsonObject.put("ball_x", ball_x);
        jsonObject.put("ball_y", ball_y);
        jsonObject.put("vel_x", vel_x);
        jsonObject.put("vel_y", vel_y);
        jsonObject.put("BALL_SPEEDX", BALL_SPEEDX);
        jsonObject.put("BALL_SPEEDY", BALL_SPEEDY);
        jsonObject.put("ball_id", ball_id);
        jsonObject.put("player_1_score", player_1_score);
        jsonObject.put("player_2_score", player_2_score);
        jsonObject.put("player_3_score", player_3_score);
        jsonObject.put("player_4_score", player_4_score);

        String jsonString = jsonObject.toString();
        byte[] bytes = jsonString.getBytes();
        sendMessageToAllExcludingMeWithAcknowledgeMsg(bytes);
    }

    public JSONObject getBallPosition() {
        return ballPosition;
    }

    public void StartGame() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("MessageType", "Start");
        String jsonString = jsonObject.toString(); //only for string data ?? put(String,bool)??
        byte[] bytes = jsonString.getBytes();
        sendMessageToAllExcludingMeWithAcknowledgeMsg(bytes);
        new Pong(udp);
    }


    private int getPort(ArrayList<Machine> playerlist, String s) {
        for (Machine machine : playerlist) {
            if (machine.getIp().equals(s))
                return machine.getPort();
        }
        return 0;
    }

    public void checkRoomMsg() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType", "checkRoom");
        jsonObject.put("connect", true);

        String jsonString = jsonObject.toString(); //only for string data ?? put(String,bool)??
        byte[] bytes = jsonString.getBytes();
        InetAddress broadcast = null;
        try {
            broadcast = InetAddress.getByName("255.255.255.255");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DatagramPacket lobbyServer = new DatagramPacket(bytes, bytes.length, broadcast, lobby_Port);

        try {
            socket.send(lobbyServer);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    public void leaveRoomMsg() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType", "checkRoom");
        jsonObject.put("connect", false);

        String jsonString = jsonObject.toString(); //only for string data ?? put(String,bool)??
        byte[] bytes = jsonString.getBytes();
        InetAddress broadcast = null;
        try {
            broadcast = InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DatagramPacket lobbyServerpacket = new DatagramPacket(bytes, bytes.length, broadcast, lobby_Port);
        try {
            socket.send(lobbyServerpacket);
        } catch (IOException e) {
            handleIOException(e);
        }
    }


//    public void isConnected() {
//        try {
//            InetAddress inetAddress = InetAddress.getByName(playerlist.get(0).getIp());
//            int port = playerlist.get(0).getPort();
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("MessageType", "isConnected");
//
//            byte[] bytes = jsonObject.toString().getBytes();
//
//            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inetAddress, port);
//            socket.send(packet);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void connectMsg() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType", "connectToGame");
        String jsonString = jsonObject.toString();
        byte[] bytes = jsonString.getBytes();
        InetAddress broadcast = null;
        try {
            broadcast = InetAddress.getByName("255.255.255.255");//broadcast message
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        DatagramPacket lobbyServer = new DatagramPacket(bytes, bytes.length, broadcast, lobby_Port);
        try {
            socket.send(lobbyServer);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    public void sendAck(Machine machine) {
        JSONObject jsonobject = new JSONObject();
        jsonobject.put("MessageType", "Ack");
        byte[] bytes = jsonobject.toString().getBytes();
        InetAddress broadcast = null;
        try {
            broadcast = InetAddress.getByName(machine.getIp());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DatagramPacket startGame = new DatagramPacket(bytes, bytes.length, broadcast, machine.getPort());
        try {
            socket.send(startGame);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    public void sendToPlayers(byte[] bytes) { //method to send json object to all excluding itself
        System.out.println(playerlist.size());
        for (Machine machine : playerlist) {
            try {
                if (!(machine.getIp().equals(InetAddress.getLocalHost().getHostAddress()))) {
                    InetAddress broadcast = null;
                    try {
                        broadcast = InetAddress.getByName(machine.getIp());
                        //            broadcast = InetAddress.getByName("127.0.0.1");
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    DatagramPacket startGame = new DatagramPacket(bytes, bytes.length, broadcast, machine.getPort());
                    try {
                        socket.send(startGame);

                    } catch (IOException e) {
                        handleIOException(e);
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        }
    }


    /// GUI
    final static boolean shouldFill = true;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;

    public void addComponentsToPane(Container pane) {
        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        JButton buttonCreateRoom;
        JButton buttonJoinRoom;
        JButton buttonCheckPlayer;
        JButton buttonPlay;
        JButton buttonClose;
        JButton buttonDisconnect;

        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        if (shouldFill) {
            //natural height, maximum width
            c.fill = GridBagConstraints.HORIZONTAL;
        }


        buttonCreateRoom = new JButton("Create Room");
        if (shouldWeightX) {
            c.weightx = 0.5;
        }
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(buttonCreateRoom, c);

        buttonJoinRoom = new JButton("Join Room");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        pane.add(buttonJoinRoom, c);

        buttonCheckPlayer = new JButton("Check Players");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 0;
        pane.add(buttonCheckPlayer, c);

        textArea = new TextArea();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 40;      //make this component tall
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        pane.add(textArea, c);

        buttonPlay = new JButton("Play");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 0;       //reset to default
        c.weighty = 1.0;   //request any extra vertical space
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.PAGE_END; //bottom of space
        c.insets = new Insets(10, 0, 0, 0);  //top padding
        c.gridx = 2;       //aligned with button 2
        c.gridwidth = 1;   //2 columns wide
        c.gridy = 2;       //third row
        pane.add(buttonPlay, c);


        buttonClose = new JButton("Close");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 0;       //reset to default
        c.weighty = 1.0;   //request any extra vertical space
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.PAGE_END; //bottom of space
        c.insets = new Insets(10, 0, 0, 0);  //top padding
        c.gridx = 0;       //aligned with button 2
        c.gridwidth = 1;   //2 columns wide
        c.gridy = 2;       //third row
        pane.add(buttonClose, c);


        buttonDisconnect = new JButton("Disconnect");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 0;       //reset to default
        c.weighty = 1.0;   //request any extra vertical space
        c.weightx = 1.5;
        c.anchor = GridBagConstraints.PAGE_END; //bottom of space
        c.insets = new Insets(10, 0, 0, 0);  //top padding
        c.gridx = 1;       //aligned with button 2
        c.gridwidth = 1;   //2 columns wide
        c.gridy = 2;       //third row
        pane.add(buttonDisconnect, c);

        buttonCreateRoom.setBorderPainted(true);
        buttonCreateRoom.setFocusPainted(false);
        buttonCreateRoom.setContentAreaFilled(false);

        buttonJoinRoom.setBorderPainted(true);
        buttonJoinRoom.setFocusPainted(false);
        buttonJoinRoom.setContentAreaFilled(false);

        buttonCheckPlayer.setBorderPainted(true);
        buttonCheckPlayer.setFocusPainted(false);
        buttonCheckPlayer.setContentAreaFilled(false);

        buttonClose.setBorderPainted(true);
        buttonClose.setFocusPainted(false);
        buttonClose.setContentAreaFilled(false);

        buttonDisconnect.setBorderPainted(true);
        buttonDisconnect.setFocusPainted(false);
        buttonDisconnect.setContentAreaFilled(false);

        buttonPlay.setBorderPainted(true);
        buttonPlay.setFocusPainted(false);
        buttonPlay.setContentAreaFilled(false);


        buttonCheckPlayer.setEnabled(false);
        buttonDisconnect.setEnabled(false);
        buttonPlay.setEnabled(false);

        buttonCreateRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread threadLobby = new Thread() {
                    @Override
                    public void run() {
                        lobbyServer = new LobbyServer(lobby_Port);
                    }//currently at port lobby_Port
                };
                threadLobby.start();//thread for starting the lobby room
                connectMsg();//message to join the room as well
                checkRoomMsg();//message to check the current players
                buttonCreateRoom.setEnabled(false);
                buttonJoinRoom.setEnabled(false);
                buttonCheckPlayer.setEnabled(true);
                buttonDisconnect.setEnabled(true);
                buttonPlay.setEnabled(true);
            }
        });

        buttonJoinRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectMsg();//message to join the room as well
                checkRoomMsg();//message to check the current players
                buttonJoinRoom.setEnabled(false);
                buttonCreateRoom.setEnabled(false);
                buttonCheckPlayer.setEnabled(true);
                buttonDisconnect.setEnabled(true);
                buttonPlay.setEnabled(true);
            }
        });

        buttonDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leaveRoomMsg();
                playerlist = new ArrayList<Machine>();
                lobbyServer.closeSocket();
                buttonJoinRoom.setEnabled(true);
                buttonCreateRoom.setEnabled(true);
                buttonCheckPlayer.setEnabled(false);
                buttonDisconnect.setEnabled(false);
                buttonPlay.setEnabled(false);
            }
        });

        buttonClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leaveRoomMsg();
                frameMain.setVisible(false);
                lobbyServer.closeSocket();
                socket.disconnect();
                socket.close();
                listener.interrupt();
                listener = null;
            }
        });


        buttonPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkRoomMsg();//latset room players
                StartGame();
                virtualHost = true;
            }
        });

        buttonCheckPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkRoomMsg();
                buttonJoinRoom.setEnabled(false);
                buttonCheckPlayer.setEnabled(true);
                buttonDisconnect.setEnabled(true);
                buttonPlay.setEnabled(true);
            }
        });
    }


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Create and set up the window.
        frameMain = new JFrame("Multiplayer");
        frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set up the content pane.
        addComponentsToPane(frameMain.getContentPane());

        //Display the window.
        frameMain.pack();
        frameMain.setLocationRelativeTo(null);
        frameMain.setVisible(true);
    }

    //master method
    public synchronized void sendMessageToAllExcludingMeWithAcknowledgeMsg(byte[] bytes) {
        int resends;//to check number of resends of each message
        InetAddress myInetAddress = null;
        try {
            myInetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        assert myInetAddress != null;
        String myIp = myInetAddress.getHostAddress();
        //If resends exceed a particular number assume player lost and unblock the socket.receive by sending message to itself
        //and perform necessary actions like updating the playerlist and replacing the code with AI
        for (Machine machine : playerlist) {
            if (!machine.getIp().equals(myIp)) {//excluding me
                resends = 0;
                //DataPacket formed
                InetAddress ip_of_machine = null;
                try {
                    ip_of_machine = InetAddress.getByName(machine.getIp());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, ip_of_machine, machine.getPort());

                //Send DataPacket
                try {
                    socket.send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Wait for acknowledgement
                DatagramPacket ackPacket = new DatagramPacket(new byte[1024], 1024);

                try {
                    ackSocket.setSoTimeout(1000); //timeout of 1 second
                } catch (SocketException e) {
                    e.printStackTrace();
                }

                boolean continueSending = true;

                while (continueSending) {
                    // send to server omitted
                    try {
                        ackSocket.receive(ackPacket);
                        continueSending = false; // a packet has been received : stop sending
                    } catch (SocketTimeoutException e) {
                        // no response received after 1 second. continue sending
                        if (resends > 4) {
                            // send message back to the acksocket to unblock it


                            //unblock message
                            JSONObject unblockJson = new JSONObject();
                            unblockJson.put("MessageType", "Unblock");
                            String unblockString = unblockJson.toString();
                            byte[] unblockBytes = unblockString.getBytes();

                            //unblockPacket
                            DatagramPacket datagramPacketUnblock = new DatagramPacket(unblockBytes, unblockBytes.length, myInetAddress, 9500);

                            //sendUnblockPacket
                            try {
//                                assert datagramSocketToUnblock != null;
                                datagramSocketToUnblock.send(datagramPacketUnblock);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                        } else {
                            resends++;
                            try {
                                socket.send(datagramPacket);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                //ack message type
                String messageAck = new String(ackPacket.getData(), 0, ackPacket.getLength());
                JSONObject jsonObjectAck = new JSONObject(messageAck);
                String msg_type_ack = jsonObjectAck.getString("MessageType");


                if (ackPacket.getAddress().getHostAddress().equals(myIp)) {
                    //current machine detected
                    System.out.println(machine.getIp() + " : disconnected");
                    //perform actions such as update playerlist
                } else {
                    if (msg_type_ack.equals("ACK"))
                        System.out.println("Ack received from : " + ackPacket.getAddress().getHostAddress());
                    else
                        System.out.println("Unknown message type for Ack");
                }
            }
        }
    }

    public synchronized void sendACK(DatagramPacket incoming,DatagramSocket socket){
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("MessageType", "ACK");
        String messageack = jsonObject1.toString();
        byte[] bytes1 = messageack.getBytes();

        DatagramPacket datagramPacket = new DatagramPacket(bytes1, bytes1.length, incoming.getAddress(), 9500);
        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}