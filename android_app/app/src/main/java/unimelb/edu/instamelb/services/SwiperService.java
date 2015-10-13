package unimelb.edu.instamelb.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import unimelb.edu.instamelb.instamelb_swipe.SwipeListenerServer;
import unimelb.edu.instamelb.instamelb_swipe.SwipeSenderImageServer;

/**
 * Created by pheo on 10/14/15.
 */
public class SwiperService extends Service {

    public SwiperService() {
        Log.w(getClass().getName(), "[SwiperService] Starting Service!");

        // Start SwiperServiceThread
        Thread swiper_service_thread = new Thread(new SwiperServiceThread(this));
        swiper_service_thread.start();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class SwiperServiceThread implements Runnable {

        protected Context mContext;

        // Constructor
        public SwiperServiceThread(Context context) {
            Log.w(getClass().getName(), "[SwiperServiceThread] Starting Thread!");
            this.mContext = context;
        }

        @Override
        public void run() {

            // SwipeListenerServer (UDP)
            Thread swipe_listener_server = new Thread(new SwipeListenerServer(13475, 13476));
            swipe_listener_server.start(); // When to .stop()?

            // SwipeSenderImageServer (TCP)
            Thread swipe_sender_image_server = new Thread(new SwipeSenderImageServer(13476));
            swipe_sender_image_server.start(); // When to .stop()?

            // TODO: Need some way to kill servers (use .stop())

        }

    }

}
