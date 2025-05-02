package com.example.proyecto1raentrega.dto;

import java.util.List;

public class DetallePeliculaDTO {

    private String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private String overview;
    private String release_date;
    private double popularity;
    private String poster_path;
    private Credits credits;

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public double getPopularity() {
        return popularity;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public Credits getCredits() {
        return credits;
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
