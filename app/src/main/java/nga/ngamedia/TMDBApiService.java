package nga.ngamedia;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by gerard on 10/6/16.
 */
public interface TMDBApiService {
    @GET("/discover/movie?language=en-US&sort_by=popularity.desc")
    void getPopularMovies(Callback<Movie.MovieResult> cb);

    @GET("/movie/upcoming?language=en-US&sort_by=popularity.desc")
    void getUpcomingMovies(Callback<Movie.MovieResult> cb);

    @GET("/discover/movie?language=en-US&sort_by=vote_average.desc")
    void getBestMovies(Callback<Movie.MovieResult> cb);

    @GET("/search/multi?language=en-US&sort_by=popularity.desc")
    void getSearchResults(@Query("query") String query, Callback<Movie.MovieResult> cb);

    @GET("/discover/tv?language=en-US&sort_by=popularity.desc")
    void getPopularTV(Callback<Movie.MovieResult> cb);

    @GET("/discover/tv?language=en-US&sort_by=release_date.desc")
    void getRecentTV(Callback<Movie.MovieResult> cb);

    @GET("/discover/tv?language=en-US&sort_by=vote_average.desc")
    void getBestTV(Callback<Movie.MovieResult> cb);


}
