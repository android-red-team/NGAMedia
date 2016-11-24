package nga.ngamedia;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jose on 10/6/15.
 */
public class Movie implements Parcelable {
    private String title; // used by movies
    private String name; // used by TVShows
    @SerializedName("poster_path")
    private String poster;
    @SerializedName("overview")
    private String description;
    @SerializedName("backdrop_path")
    private String backdrop;
    @SerializedName("vote_average")
    private String voteAverage;
    private String release_date;
    private String first_air_date;
    private String vote_count;
    private String popularity;

    public Movie() {}

    protected Movie(Parcel in) {
        title = in.readString();
        name = in.readString();
        poster = in.readString();
        description = in.readString();
        backdrop = in.readString();
        voteAverage = in.readString();
        vote_count = in.readString();
        popularity = in.readString();
        release_date = in.readString();
        first_air_date = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBackdrop() {
        return "http://image.tmdb.org/t/p/w500"  + backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getVoteAverage() { return voteAverage; }

    public void setVoteAverage(String voteAverage) { this.voteAverage = voteAverage; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(title);
            parcel.writeString(name);
            parcel.writeString(poster);
            parcel.writeString(description);
            parcel.writeString(backdrop);
        if (voteAverage != null) {
            parcel.writeString(voteAverage);
        }
        if (vote_count == null) {
            parcel.writeString("0");
        } else {
            parcel.writeString(vote_count);
        }
            parcel.writeString(popularity);
            parcel.writeString(release_date);
            parcel.writeString(first_air_date);
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getVote_count() {
        return vote_count;
    }

    public void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getFirst_air_date() {
        return first_air_date;
    }

    public void setFirst_air_date(String first_air_date) {
        this.first_air_date = first_air_date;
    }

    public static class MovieResult {
        private List<Movie> results;

        public List<Movie> getResults() {
            return results;
        }
    }
}
