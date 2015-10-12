package unimelb.edu.instamelb.instamelb_swipe;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by pheo on 10/12/15.
 */
// SwipeSenderClient used to broadcast need to transmit, then interact with server
public class SwipeSenderClient implements Runnable {

    private DatagramSocket c;
    private final int port; // Port to send to
    private final String address = "255.255.255.255"; // Broadcast to Address

    // Constructor
    public SwipeSenderClient(int port_to_send) {
        this.port = port_to_send;
    }


    // Run thread with .start()
    @Override
    public void run() {

        try {
            c = new DatagramSocket();
            c.setBroadcast(true);

            byte[] sendData = "SwipeSenderClient_REQUEST".getBytes();

            // Try this.address (255.255.255.255)
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(this.address), this.port);
                c.send(sendPacket);
                Log.w(getClass().getName(), "[SwipeSenderClient] Request packet sent to: " + this.address);
            } catch (Exception e) {
                Log.w(getClass().getName(), "[SwipeSenderClient] ERROR (1): " + e.getMessage());
            }

            // Broadcast message over all network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast =  interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, this.port);
                        c.send(sendPacket);
                    } catch (Exception e) {
                        Log.w(getClass().getName(), "[SwipeSenderClient] ERROR (2): " + e.getMessage());
                    }

                    Log.w(getClass().getName(), "[SwipeSenderClient] Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }

                Log.w(getClass().getName(), "[SwipeSenderClient] Done looping over all network interfaces. Now waiting for a reply!");

                // Wait for a response
                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                c.receive(receivePacket);

                // We have a response
                Log.w(getClass().getName(), "[SwipeSenderClient] Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

                // Check if the message is correct
                String message = new String(receivePacket.getData()).trim();
                if (message.equals("SwipeListenerServer_RESPONSE")) {
                    // Do something with server's ip
                    Log.w(getClass().getName(), "[SwipeSenderClient] Start Thread to send image to server! (Does nothing atm)");
                }

                // Close the port!
                c.close();
            }
        } catch (IOException ex) {
            Log.w(SwipeSenderClient.class.getName(), "[SwipeSenderClient] ERROR (3): " + ex.getMessage());
        }

    }
}


/*
    // Call once to broadcast image
    // Starts an image server for 10 seconds.
    public Boolean broadcastImage(String base64_image_string) {
        // Emit Single Broadcast
        return Boolean.TRUE;
    }

    public Boolean sendBroadcast() {
        // Broadcast to
        return Boolean.TRUE;
    }
*/