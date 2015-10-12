package unimelb.edu.instamelb.instamelb_swipe;

/**
 * Created by pheo on 10/12/15.
 */
// SwipeListener listens for SwipeSender requests, starts new thread when receiving a request, and
public class SwipeListener {

    int port; // Port that swipe listener will listen to broadcasts from

    public SwipeListener(int listen_on_port) {
        this.port = listen_on_port;
    }

}
