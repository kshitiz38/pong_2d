import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class LobbyServer {//implements Runnable {//Lobby server will be hosted on any one player and once game is started, it is not even needed
    private static final int Group_Port = 1235; //Lobby Port number
    private ArrayList<Machine> groupMembers = new ArrayList<>(); //list
//    private static final int port = 1234; //This is the port number of all the players using this game-application

    protected DatagramSocket socket;
    protected DatagramPacket outgoing, incoming;

    public LobbyServer() {
        //public constructor
        try {
            initNet();
        } catch (IOException e) {
            handleIOException(e);
        }
        receieveConnection(); //added now
    }

    protected void initNet() throws IOException {
        //socket = new MulticastSocket (port);
        socket = new DatagramSocket(Group_Port);
        //socket.setTimeToLive (5);
        //socket.joinGroup (inetAddress);
        outgoing = new DatagramPacket(new byte[1], 1);
        incoming = new DatagramPacket(new byte[65508], 65508);
    }



    public void receieveConnection() {
        while (true) {
            incoming.setLength(incoming.getData().length);
            try {
                socket.receive(incoming);
            } catch (IOException e) {
                handleIOException(e);
            }
            InetAddress ipaddress = incoming.getAddress();
            String ip_string = ipaddress.getHostAddress();
//            System.out.println(ip_string);
            int port_received = incoming.getPort();

            String message = new String(incoming.getData(), 0, incoming.getLength());
            JSONObject jsonObject = new JSONObject(message);
            //Message type
            String messageType = jsonObject.getString("MessageType");
            switch (messageType) {
                case "checkRoom":
                    Boolean isConnect = jsonObject.getBoolean("connect");//true - check players
                    if (isConnect) {
                        JSONObject Playerslist = new JSONObject();
                        Playerslist.put("MessageType", "lobbyResp");
                        Playerslist.put("numPlayers", groupMembers.size());
                        for (int i = 0; i < groupMembers.size(); i++) {
                            Playerslist.put("Player" + i, groupMembers.get(i).getIp() + ":" + groupMembers.get(i).getPort());
                        }
                        String message_out = Playerslist.toString();
                        byte[] bytes = message_out.getBytes();
                        DatagramPacket out_packet = new DatagramPacket(bytes, bytes.length, ipaddress, port_received);
                        try {
                            socket.send(out_packet);
                        } catch (IOException e) {
                            handleIOException(e);
                        }

                    } else {// false - removeplayers
                        int toBeRemoved = -1;
                        for (int i = 0; i < groupMembers.size(); i++) {
                            if (groupMembers.get(i).getIp().equals(ip_string)) {
                                toBeRemoved = i;
                            }
                        }
                        if (toBeRemoved >= 0) {
                            groupMembers.remove(toBeRemoved);
                            //update output
                        }
                    }
                    break;
                case "connectToGame":
                    Machine machine = new Machine(ip_string, port_received);
                    if (!groupMembers.contains(machine))
                        groupMembers.add(machine);
                    break;
                default:
                    System.out.println("Unknown Message for Lobby Server");
            }
        }
    }

    private void handleIOException(IOException e) {
        e.printStackTrace();
    }

    public void closeSocket() {
        socket.disconnect();
        socket.close();
    }
}

class Machine {
    String ip;
    int port;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Machine)) {
            return false;
        }
        Machine machine = (Machine) obj;
        return ( (machine.getIp().equals(ip)) && (machine.getPort() == port));
    }

    public Machine(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
