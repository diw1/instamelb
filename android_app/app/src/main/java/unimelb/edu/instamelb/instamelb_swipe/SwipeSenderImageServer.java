package unimelb.edu.instamelb.instamelb_swipe;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by pheo on 10/13/15.
 */
// SwipeSenderImageServer starts a temporary multi-threaded server to serve images
// after the SwipeSenderClient broadcast
public class SwipeSenderImageServer implements Runnable {

    private int serverPort;
    private ServerSocket serverSocket;
    private Boolean isStopped = Boolean.FALSE;

    // TODO: Maps unique ids to uri's

    // Constructor (Server parameters)
    public SwipeSenderImageServer(int server_port) {
        this.serverPort = server_port;
    }

    @Override
    public void run() {

        // Open Server Socket
        openServerSocket();

        // Keep running unless stopped
        while (!isStopped()) {
            Log.w(getClass().getName(), "[SwipeSenderImageServer] Waiting for a SwipeListenerImageClient to connect");
            // New client socket
            Socket clientSocket;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                // Server already stopped, don't throw error
                if (isStopped()) {
                    Log.w(getClass().getName(), "[SwipeSenderImageServer] IOException but server already closed");
                    return;
                }

                throw new RuntimeException("[SwipeSenderImageServer] EXCEPTION Error accepting client connection ----- ", e);
            }

            Log.w(getClass().getName(), "[SwipeSenderImageServer] Spawning an ImageTransferThread to handle download request from SwipeListenerImageClient");

            Thread image_transfer_thread = new Thread(new ImageTransferThread(clientSocket));
            image_transfer_thread.start();
        }
        Log.w(getClass().getName(), "[SwipeSenderImageServer] SERVER STOPPED!");
    }

    // Check if stopped
    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    // Stop SwipeSenderImageServer
    public synchronized void stop() {
        this.isStopped = Boolean.TRUE;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("[SwipeSenderImageServer] EXCEPTION Error closing server ----- ", e);
        }
    }

    // Open Server Socket
    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("[SwipeSenderImageServer] EXCEPTION Cannot open port ----- ", e);
        }
    }

    // New thread spawned for each request
    class ImageTransferThread implements Runnable {

        private Socket clientSocket;    // Client Socket
        private String unique_string;   // String to identify transfer image

        // Socket?
        public ImageTransferThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {

            try {

                // input/output streams
                InputStream fromClient  = clientSocket.getInputStream();
                OutputStream toClient = clientSocket.getOutputStream();

                Log.w(getClass().getName(), "[ImageTransferThread] Listening for unique string from SwipeListenerImageClient.");

                // Listen to client, get byte[] unique string
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(fromClient));
                this.unique_string = inFromServer.readLine();
                Log.w(getClass().getName(), "[ImageTransferThread] Received unique string byte: " + this.unique_string);

                // TODO: Choose appropriate image

                // TODO: Get byte[] image to be sent


                Log.w(getClass().getName(), "[ImageTransferThread] Sending byte[] image to SwipeListenerImageClient");
                // Send to client byte[] of relevant image
                toClient.write("NOTHING\n".getBytes());
                toClient.flush(); // Flush end

                // Close streams
                fromClient.close();
                toClient.close();

                // Close socket?
                this.clientSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
                Log.w(getClass().getName(), "[ImageTransferThread] ERROR");
            }

        }
    }

}
