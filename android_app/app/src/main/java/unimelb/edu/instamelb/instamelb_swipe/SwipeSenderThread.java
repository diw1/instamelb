package unimelb.edu.instamelb.instamelb_swipe;

/**
 * Created by pheo on 10/12/15.
 */
// SwipeSender contains functions
public class SwipeSenderThread implements Runnable {

    @Override
    public void run() {

    }

    public static SwipeSenderThread getInstance() {
        return SwipeSenderThreadHolder.INSTANCE;
    }

    private static class SwipeSenderThreadHolder {

        private static final SwipeSenderThread INSTANCE = new SwipeSenderThread();
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

}
