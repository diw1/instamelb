package unimelb.edu.instamelb.callbacks;

import java.util.ArrayList;

import unimelb.edu.instamelb.pojo.Movie;

/**
 * Created by Windows on 13-04-2015.
 */
public interface UpcomingMoviesLoadedListener {
    public void onUpcomingMoviesLoaded(ArrayList<Movie> listMovies);
}
