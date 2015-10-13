package unimelb.edu.instamelb.instamelb_swipe;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pheo on 10/12/15.
 */

public class SwipeListenerServer implements Runnable  {

    private DatagramSocket socket;              // UDP Socket to listen for broadcasts
    private final int listen_port;              // Port to listen on
    private final int download_port;            // Port to download from
    private List<String> unique_strings;        // Unique strings list
    private Boolean isStopped = Boolean.FALSE;

    // Constructor
    public SwipeListenerServer(int server_port, int download_port) {
        this.listen_port = server_port;
        this.download_port = download_port;
        //this.unique_strings = Collections.synchronizedList(new ArrayList<String>());
        this.unique_strings = new ArrayList<>();
    }

    @Override
    public void run() {

        try {
            // Create socket on port
            openServerSocket();

            // Run forever
            while(!isStopped()) {

                Log.w(getClass().getName(), "[SwipeListenerServer] Ready to receive broadcast from SwipeSenderClient");
                byte[] buf = new byte[8 * 1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                // See if packet holds the right command (message)
                String message = new String(packet.getData()).trim();
                if (message.startsWith("SwipeSenderClient_REQUEST")) {
                    String uniq_str = message.split("=", 2)[1]; // Unique string to not request multiple times
                    Log.w(getClass().getName(), "[SwipeListenerServer] Received `" + uniq_str + "` FROM (" + packet.getAddress().getHostAddress() + ")");

                    // Appropriate message found, check if in global list
                    if (unique_strings.indexOf(uniq_str) < 0) {
                        // Not in global list, now add
                        unique_strings.add(uniq_str);

                        Log.w(getClass().getName(), "[SwipeListenerServer] Spawning SwipeListenerImageClient to download image off SwipeSenderImageServer");

                        // start SwipeListenerImageClient thread
                        Thread swipe_listener_image_client = new Thread(new SwipeListenerImageClient(packet.getAddress().getHostAddress(), this.download_port, uniq_str));
                        swipe_listener_image_client.start();

                    } else {
                        // Already in global list, do nothing.
                        Log.w(getClass().getName(), "[SwipeListenerServer] RECEIVED EXISTING UNIQUE STRING!");
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Check if stopped
    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    // Stop Server
    public synchronized void stop() {
        this.isStopped = Boolean.TRUE;
        this.socket.close();
    }

    // Open Server Socket
    private void openServerSocket() {
        try {
            this.socket = new DatagramSocket(this.listen_port);
        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException("[SwipeListenerServer] EXCEPTION ----- ", e);
        }
    }

    // SwipeListenerImageClient retrieves image from SwipeSenderImageServer
    class SwipeListenerImageClient implements Runnable {

        String image_server_address;
        int image_server_port;
        String unique_string;   // So that Server knows which image to serve

        public SwipeListenerImageClient (String image_server_address, int image_server_port, String uniq_str) {
            this.image_server_address = image_server_address;
            this.image_server_port = image_server_port;
            this.unique_string = uniq_str;
        }

        @Override
        public void run() {

            try {
                InetAddress host = InetAddress.getByName(this.image_server_address);
                Socket socket = new Socket(host, this.image_server_port);

                // Input/Output for communications
                OutputStream toServer = socket.getOutputStream();
                InputStream fromServer = socket.getInputStream();

                Log.w(getClass().getName(), "[SwipeListenerImageClient] Sending UNIQUE_STRING to SwipeSender (" + socket.getInetAddress().getHostAddress() + ")");

                // toServer, submit uniq_str
                String request_string = "SwipeListenerImageClient=" + this.unique_string + "\n";
                byte[] request_bytes =  request_string.getBytes();
                toServer.write(request_bytes);
                toServer.flush(); // Flush end

                Log.w(getClass().getName(), "[SwipeListenerImageClient] Waiting for byte[] image from SwipeSender.");

                // fromServer, get byte[] image
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(fromServer));
                String test_string = inFromServer.readLine();

                Log.w(getClass().getName(), "[SwipeListenerImageClient] Received byte[] image from SwipeSender: " + test_string);

                // TODO: If certain length, fuck off

                // TODO: Call some method to update Activity Feed

                // TODO: Call some method to write to Library

                Log.w(getClass().getName(), "[SwipeListenerImageClient] END END END END REACHED REACHED REACHED REACHED (Image sucessfully downloaded)");

                // Close streams
                toServer.close();
                fromServer.close();

                // Close socket?
                socket.close();

            } catch (UnknownHostException e) {
                Log.w(getClass().getName(), "[SwipeListenerImageClient] ERROR: UnknownHostException");
                //e.printStackTrace();
            } catch (IOException e) {
                Log.w(getClass().getName(), "[SwipeListenerImageClient] ERROR: IOException");
                //e.printStackTrace();
            }
        }
    }

}
