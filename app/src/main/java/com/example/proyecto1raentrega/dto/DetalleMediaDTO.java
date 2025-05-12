package com.example.proyecto1raentrega.dto;

import java.util.List;

public class DetalleMediaDTO {
    private int id;
    private String title;           // Películas
    private String name;            // Series
    private String overview;
    private String release_date;    // Películas
    private String first_air_date;  // Series
    private double popularity;
    private String poster_path;
    private Credits credits;

    public String getDisplayTitle() {
        return title != null ? title : name;
    }

    public String getDisplayReleaseDate() {
        return release_date != null ? release_date : first_air_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getFirst_air_date() {
        return first_air_date;
    }

    public void setFirst_air_date(String first_air_date) {
        this.first_air_date = first_air_date;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public Credits getCredits() {
        return credits;
    }

    public void setCredits(Credits credits) {
        this.credits = credits;
    }

    public static class Credits {
        private List<Actor> cast;

        public List<Actor> getCast() {
            return cast;
        }
    }

    public static class Actor {
        private String name;
        private String character;

        public String getName() {
            return name;
        }

        public String getCharacter() {
            return character;
        }
    }
}
