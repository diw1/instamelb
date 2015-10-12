package unimelb.edu.instamelb.instamelb_swipe;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by pheo on 10/12/15.
 */

public class SwipeListenerServer implements Runnable  {

    private DatagramSocket socket;
    private final int port = 8888;                     // Port to listen on
    private final String address = "0.0.0.0";   // Listen to rest of the network


    @Override
    public void run() {

        try {
            // Keep socket open to listen to all UDP Traffic destined for port
            socket = new DatagramSocket(this.port, InetAddress.getByName(this.address));
            socket.setBroadcast(true);

            while (true) {
                Log.w(getClass().getName(), "[SwipeListenerServer] Ready to receive broadcast ");

                // Receive packet
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                // Packet received
                Log.w(getClass().getName(), "[SwipeListenerServer] Discovery packet received from: " + packet.getAddress().getHostAddress());
                Log.w(getClass().getName(), "[SwipeListenerServer] Packet received; data: " + new String(packet.getData()));

                // See if packet holds the right command (message)
                String message = new String(packet.getData()).trim();
                if (message.equals("SwipeSenderClient_REQUEST")) {
                    byte[] sendData = "SwipeListenerServer_RESPONSE".getBytes();

                    // Send a response
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);
                }
            }
        } catch (IOException ex) {
            Log.w(SwipeListenerServer.class.getName(), "[SwipeListenerServer] ERROR: " + ex.getMessage());
        }

    }

    public static SwipeListenerServer getInstance() {
        return SwipeListenerServerHolder.INSTANCE;
    }

    private static class SwipeListenerServerHolder {
        private static final SwipeListenerServer INSTANCE = new SwipeListenerServer();
    }

}
