package unimelb.edu.instamelb.callbacks;

import java.util.ArrayList;

import unimelb.edu.instamelb.pojo.Movie;

/**
 * Created by Windows on 02-03-2015.
 */
public interface BoxOfficeMoviesLoadedListener {
    public void onBoxOfficeMoviesLoaded(ArrayList<Movie> listMovies);
}
