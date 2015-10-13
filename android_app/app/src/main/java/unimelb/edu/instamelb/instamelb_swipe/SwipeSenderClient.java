package unimelb.edu.instamelb.instamelb_swipe;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.DhcpInfo;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by pheo on 10/12/15.
 */
// SwipeSenderClient does a udp broadcast offering image download service to recipients
public class SwipeSenderClient implements Runnable {

    private final int port; // Port to send to
    private String unique_string;
    private int broadcast_count;
    private Context mContext;


    // Constructor
    public SwipeSenderClient(int port_to_send, String uniq_str, int spam_count, Context app_context) {
        this.port = port_to_send;
        this.unique_string = uniq_str;

        if (spam_count < 0) {
            this.broadcast_count = 1;
        } else {
            this.broadcast_count = spam_count;
        }

        this.mContext = app_context;

    }

    // Run thread with .start()
    @Override
    public void run() {

        try {
            String data = "SwipeSenderClient_REQUEST=" + this.unique_string;
            Log.w(getClass().getName(), "[SwipeSenderClient] Data: " + data);

            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(),
                    getBroadcastAddress(), this.port);
            Log.w(getClass().getName(), "[SwipeSenderClient] Broadcasting to: " + packet.getAddress().getHostAddress());

            // Spam with broadcast_count
            for (int i = 0; i < this.broadcast_count; i++) {
                socket.send(packet);
            }

        } catch (IOException ex) {
            Log.w(getClass().getName(), "[SwipeSenderClient] ERROR: " + ex.getMessage());
        }

    }

    private InetAddress getBroadcastAddress() throws IOException {

        WifiManager wifi = (WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }
}
