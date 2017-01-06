package com.example.sjsingh.popularmovies.Items;

/**
 * Created by Sarabjeet Singh on 07-09-2016.
 */
public class GridItem {
    private String image;
    private String title;
    private String synopsis;
    private String rating;
    private String releaseDate;
    private String backdrop;
    private String trailer;
    private String review;
    private String id;

    public GridItem() {
        super();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlot() {
        return synopsis;
    }

    public void setPlot(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}