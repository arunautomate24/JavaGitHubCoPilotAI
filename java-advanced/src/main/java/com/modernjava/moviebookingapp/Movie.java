package com.modernjava.moviebookingapp;

import java.util.List;
import java.util.Objects;

/**
 * Movie model backing the "Hero Backdrop" / "Overview" tab shown in
 * MovieBooking.md: rating, runtime, genres, description, production
 * details, cast and keywords.
 */
public class Movie {

    private final String title;
    private final double rating; // e.g. 8.4
    private final int durationMinutes; // e.g. 148 -> "2h 28m"
    private final List<String> genres;
    private final int releaseYear;
    private final String certification; // e.g. PG-13
    private final String description;
    private final String director;
    private final List<String> writers;
    private final String studio;
    private final String language;
    private final double budget;
    private final List<CastMember> cast;
    private final List<String> keywords;

    public Movie(String title, double rating, int durationMinutes, List<String> genres,
                 int releaseYear, String certification, String description, String director,
                 List<String> writers, String studio, String language, double budget,
                 List<CastMember> cast, List<String> keywords) {
        this.title = title;
        this.rating = rating;
        this.durationMinutes = durationMinutes;
        this.genres = genres;
        this.releaseYear = releaseYear;
        this.certification = certification;
        this.description = description;
        this.director = director;
        this.writers = writers;
        this.studio = studio;
        this.language = language;
        this.budget = budget;
        this.cast = cast;
        this.keywords = keywords;
    }

    public String getTitle() {
        return title;
    }

    public double getRating() {
        return rating;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public List<String> getGenres() {
        return genres;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getCertification() {
        return certification;
    }

    public String getDescription() {
        return description;
    }

    public String getDirector() {
        return director;
    }

    public List<String> getWriters() {
        return writers;
    }

    public String getStudio() {
        return studio;
    }

    public String getLanguage() {
        return language;
    }

    public double getBudget() {
        return budget;
    }

    public List<CastMember> getCast() {
        return cast;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * Mirrors MovieV1#formattedDuration(): turns 148 minutes into "2h 28m".
     */
    public String formattedDuration() {
        int hours = durationMinutes / 60;
        int mins = durationMinutes % 60;
        return hours > 0 ? hours + "h " + mins + "m" : mins + "m";
    }

    @Override
    public String toString() {
        return "Movie{title='" + title + "', rating=" + rating +
                ", duration=" + formattedDuration() +
                ", genres=" + genres + ", year=" + releaseYear + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Movie m)) return false;
        return releaseYear == m.releaseYear && Objects.equals(title, m.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, releaseYear);
    }
}
