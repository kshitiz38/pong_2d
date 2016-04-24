//import org.json.*;

import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class UDP implements Runnable, WindowListener, ActionListener {
    JSONObject key_event;
    protected InetAddress group;
    protected int port;
    protected ArrayList<Machine> playerlist;
    static UDP udp;

    public UDP(InetAddress group, int port) {
        this.group = group;
        this.port = port;
//        initAWT ();
        createAndShowGUI();
    }

    protected Frame frame;
    protected TextArea output;
    protected TextField input;
    protected JButton CreateRoom;
    protected JButton JoinRoom;
    protected LobbyServer lobbyServer;

    protected void initAWT() {
        frame = new Frame
                ("UDP [" + group.getHostAddress() + ":" + port + "]");
        frame.addWindowListener(this);
        output = new TextArea();
        output.setEditable(false);
        input = new TextField();
        input.addActionListener(this);
        frame.setLayout(new BorderLayout());
        frame.add(output, "Center");
        frame.add(input, "South");
        frame.pack();


        //Pranav
        CreateRoom = new JButton();
        JoinRoom = new JButton();

        CreateRoom.setText("Create Room");
        CreateRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lobbyServer = new LobbyServer();
            }
        });

        JoinRoom.setText("Join Room");
        JoinRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkRoomMsg();
            }
        });

        frame.add(CreateRoom, "North");
        frame.add(JoinRoom, "North");
        //
    }

    protected Thread listener;

    public synchronized void start() throws IOException {
        if (listener == null) {
            initNet();
            listener = new Thread(this);
            listener.start();
//            frame.setVisible (true);
        }
    }

    //  protected DatagramSocket socket;
    protected DatagramSocket socket;
    protected DatagramPacket outgoing, incoming;

    protected void initNet() throws IOException {
        //socket = new MulticastSocket (port);
        socket = new DatagramSocket(port);
        //socket.setTimeToLive (5);
        //socket.joinGroup (group);
        outgoing = new DatagramPacket(new byte[1], 1, group, 1234);
        incoming = new DatagramPacket(new byte[65508], 65508);
    }

    public synchronized void stop() throws IOException {
        frame.setVisible(false);
        if (listener != null) {
            listener.interrupt();
            listener = null;
            //try {
//        socket.leaveGroup (group);
            //  } finally {
            //      socket.close ();
//      }
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

//        connectMsg();
    }

    //send key event
    public void sendKeyEvent(int event_code, String type){
        JSONObject jsonObject= new JSONObject();

        jsonObject.put("key_event_code", event_code);
        jsonObject.put("MessageType", "Key_Event");
        jsonObject.put("event_type", type);
        String jsonString = jsonObject.toString();
        byte[] bytes = jsonString.getBytes();
        outgoing.setData(bytes);
        outgoing.setLength(bytes.length);
        try {
            socket.send(outgoing);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    //retreiving keyEvent
    public JSONObject getKeyEvent(){
        return key_event;
    }

    // Send Message Stuff
    public void sendBallInfo(double ball_x, double ball_y, double vel_x, double vel_y, int ball_id) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType", "Ball_Moving");
        jsonObject.put("ball_x", ball_x);
        jsonObject.put("ball_y", ball_y);
        jsonObject.put("vel_x", vel_x);
        jsonObject.put("vel_y", vel_y);
        jsonObject.put("ball_id", ball_id);

        String jsonString = jsonObject.toString();
        byte[] bytes = jsonString.getBytes();
        outgoing.setData(bytes);
        outgoing.setLength(bytes.length);
        try {
            socket.send(outgoing);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    public void sendPaddleInfo(double paddle_x, double paddle_y, double vel_x, double vel_y, int paddle_id) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType", "Paddle_Moving");
        jsonObject.put("paddle_x", paddle_x);
        jsonObject.put("paddle_y", paddle_y);
        jsonObject.put("vel_x", vel_x);
        jsonObject.put("vel_y", vel_y);
        jsonObject.put("paddle_id", paddle_id);

        String jsonString = jsonObject.toString();
        byte[] bytes = jsonString.getBytes();
        outgoing.setData(bytes);
        outgoing.setLength(bytes.length);
        try {
            socket.send(outgoing);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    public void WallHit(int player_id, int num_hits) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType", "Wall_Hit");
        jsonObject.put("player_id", player_id);
        jsonObject.put("num_hits", num_hits);

        String jsonString = jsonObject.toString();
        byte[] bytes = jsonString.getBytes();
        outgoing.setData(bytes);
        outgoing.setLength(bytes.length);
        try {
            socket.send(outgoing);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    public void PaddleHit(int player_id, double paddle_x, double paddle_y, double vel_paddle_x, double vel_paddle_y, int paddle_id, double ball_x, double ball_y, double vel_ball_x, double vel_ball_y, int ball_id) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType", "Paddle_Hit");
        jsonObject.put("playerId", player_id);

        jsonObject.put("ball_x", ball_x);
        jsonObject.put("ball_y", ball_y);
        jsonObject.put("vel_x", vel_ball_x);
        jsonObject.put("vel_y", vel_ball_y);
        jsonObject.put("ball_id", ball_id);

        jsonObject.put("paddle_x", paddle_x);
        jsonObject.put("paddle_y", paddle_y);
        jsonObject.put("vel_x", vel_paddle_x);
        jsonObject.put("vel_y", vel_paddle_y);
        jsonObject.put("paddle_id", paddle_id);

        String jsonString = jsonObject.toString();
        byte[] bytes = jsonString.getBytes();
        outgoing.setData(bytes);
        outgoing.setLength(bytes.length);
        try {
            socket.send(outgoing);
        } catch (IOException e) {
            handleIOException(e);
        }

    }

    public void PaddleRemove(int player_id) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType", "Paddle_Remove");
        jsonObject.put("playerId", player_id);

        String jsonString = jsonObject.toString();
        byte[] bytes = jsonString.getBytes();
        outgoing.setData(bytes);
        outgoing.setLength(bytes.length);
        try {
            socket.send(outgoing);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    public void AcknowledgementMsg(String Messagetype, int player_id) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType", "Ack");
        jsonObject.put("playerId", player_id);
        jsonObject.put("Messagetype", Messagetype);

        String jsonString = jsonObject.toString();
        byte[] bytes = jsonString.getBytes();
        outgoing.setData(bytes);
        outgoing.setLength(bytes.length);
        try {
            socket.send(outgoing);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    public void playMsg() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType","Start");

        String jsonString = jsonObject.toString(); //only for string data ?? put(String,bool)??
        byte[] bytes = jsonString.getBytes();
        for (Machine machine : playerlist) {
            InetAddress broadcast = null;
            try {
                broadcast = InetAddress.getByName(machine.getIp());
//            broadcast = InetAddress.getByName("127.0.0.1");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            DatagramPacket startGame = new DatagramPacket(bytes, bytes.length, broadcast, machine.getPort());
//        outgoing.setData(bytes);
//        outgoing.setLength(bytes.length);
            try {
                socket.send(startGame);
            } catch (IOException e) {
                handleIOException(e);
            }
        }
    }

    public void checkRoomMsg() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType", "checkRoom");
        jsonObject.put("connect", true);
//        jsonObject.put("Messagetype",Messagetype);

        String jsonString = jsonObject.toString(); //only for string data ?? put(String,bool)??
        byte[] bytes = jsonString.getBytes();
        InetAddress broadcast = null;
        try {
            broadcast = InetAddress.getByName("255.255.255.255");
//            broadcast = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DatagramPacket lobbyServer = new DatagramPacket(bytes, bytes.length, broadcast, 1235);
//        outgoing.setData(bytes);
//        outgoing.setLength(bytes.length);
        try {
            socket.send(lobbyServer);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    public void connectMsg() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType", "connectToGame");
        String jsonString = jsonObject.toString(); //only for string data ?? put(String,bool)??
        byte[] bytes = jsonString.getBytes();
        InetAddress broadcast = null;
        try {
            broadcast = InetAddress.getByName("255.255.255.255");
//            broadcast = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        DatagramPacket lobbyServer = new DatagramPacket(bytes, bytes.length, broadcast, 1235);
        try {
            socket.send(lobbyServer);
        } catch (IOException e) {
            handleIOException(e);
        }
    }
    //time message

    public void WinMsg(int player_id) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageType", "Win");
        jsonObject.put("playerId", player_id);

        String jsonString = jsonObject.toString();
        byte[] bytes = jsonString.getBytes();
        outgoing.setData(bytes);
        outgoing.setLength(bytes.length);
        try {
            socket.send(outgoing);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    //
    protected synchronized void handleIOException(IOException ex) {
        if (listener != null) {
            output.append(ex + "\n");
            input.setVisible(false);
            frame.validate();
            if (listener != Thread.currentThread())
                listener.interrupt();
            listener = null;
            //try {
            //socket.leaveGroup (group);
            //} catch (IOException ignored) {
            //}
            socket.close();
        }
    }

    TextArea textArea;

    //run method for listener thread
    public void run() {
        try {
            while (!Thread.interrupted()) {
                incoming.setLength(incoming.getData().length);
                socket.receive(incoming);
                String message = new String(incoming.getData(), 0, incoming.getLength());
//                System.out.println(message);
                JSONObject jsonObject = new JSONObject(message);

                String msg_type = jsonObject.getString("MessageType");

                switch (msg_type) {
                    case "Ball_Moving":
                        output.append(msg_type + "\n");
                        break;
                    case "Paddle_Moving":
                        break;
                    case "Wall_Hit":
                        break;
                    case "Paddle_Hit":
                        break;
                    case "Paddle_Remove":
                        break;
                    case "Ack":
                        break;
                    case "Win":
                        break;
                    case "Key_Event":
                        key_event=jsonObject;
                        break;
                    case "Start" :
                        System.out.println("Start");
                        new Pong(udp);
                        break;
                    case "lobbyResp":

                        textArea.setText("");
                        playerlist = new ArrayList<>();
                        System.out.println(jsonObject.getInt("numPlayers"));
                        for (int i = 0; i < jsonObject.getInt("numPlayers"); i++) {
//                            output.append(jsonObject.getString("Player"+i)+"\n");
                            String ip_port = jsonObject.getString("Player" + i);
                            textArea.append( ip_port + "\n");
                            String[] ip_ports = ip_port.split(":");
                            Machine machine = new Machine(ip_ports[0],Integer.parseInt(ip_ports[1]));
                            playerlist.add(machine);
                        }
//                        if(jsonObject.getInt("numPlayers")>=2){
//                            //will go for play button
//                        }
                        break;
                    default:
                        System.out.println("Unknown Message Type");
                }

            }
        } catch (IOException ex) {
            handleIOException(ex);
        }
    }

    ///
    final static boolean shouldFill = true;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;

    public void addComponentsToPane(Container pane) {
        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        JButton button;
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        if (shouldFill) {
            //natural height, maximum width
            c.fill = GridBagConstraints.HORIZONTAL;
        }

        button = new JButton("Create Room");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                lobbyServer = new LobbyServer();
                Thread threadLobby = new Thread(){
                    @Override
                    public void run() {
                        lobbyServer = new LobbyServer();
                    }
                };
                threadLobby.start();
                connectMsg();
            }
        });
        if (shouldWeightX) {
            c.weightx = 0.5;
        }
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(button, c);

        button = new JButton("Join Room");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectMsg();
            }
        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        pane.add(button, c);

        button = new JButton("CheckPlayers");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkRoomMsg();
            }
        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 0;
        pane.add(button, c);

        textArea = new TextArea();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 40;      //make this component tall
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        pane.add(textArea, c);

        button = new JButton("Play");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playMsg();
//                new Pong(udp);
            }
        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 0;       //reset to default
        c.weighty = 1.0;   //request any extra vertical space
        c.anchor = GridBagConstraints.PAGE_END; //bottom of space
        c.insets = new Insets(10, 0, 0, 0);  //top padding
        c.gridx = 1;       //aligned with button 2
        c.gridwidth = 2;   //2 columns wide
        c.gridy = 2;       //third row

        pane.add(button, c);
    }


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("GridBagLayoutDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }



    //main method
    public static void main(String[] args) throws IOException {
        if ((args.length != 1) || (!args[0].contains(":")))
            throw new IllegalArgumentException
                    ("Syntax: UDP <group>:<port>");

        int idx = args[0].indexOf(":");
        InetAddress group = InetAddress.getByName(args[0].substring(0, idx));
        int port = Integer.parseInt(args[0].substring(idx + 1));

        UDP pong = new UDP(group, port);
        udp = pong;
        pong.start();
    }


}