package com.modernjava.moviebookingapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.LocalTime;
import java.util.*;

/**
 * Loads the movie catalog for the Movie Booking App from a classpath
 * properties file (src/main/resources/movies.properties) instead of
 * hardcoding movies in {@link MovieBookingApp}.
 *
 * <p>Expected format (1-based index per movie):
 * <pre>
 * movie.count=20
 * movie.1.title=Inception
 * movie.1.rating=8.8
 * movie.1.duration=148
 * movie.1.genres=Action,Sci-Fi
 * movie.1.year=2010
 * movie.1.certification=PG-13
 * movie.1.description=...
 * movie.1.director=Christopher Nolan
 * movie.1.writers=Christopher Nolan
 * movie.1.studio=Warner Bros. Pictures
 * movie.1.language=English
 * movie.1.budget=160000000
 * movie.1.cast=Leonardo D.:Dom Cobb;Elliot P.:Ariadne
 * movie.1.keywords=Dreams,Sci-Fi,Heist
 * movie.1.showtimes=10:00,13:30,16:45,19:30,22:15
 * movie.1.theatre=Screen 1
 * movie.1.price=12.50
 * movie.1.rows=5
 * movie.1.seatsPerRow=8
 * </pre>
 *
 * <p>A top-level {@code booking.seats} key (e.g. {@code booking.seats=C1,C4,C3})
 * supplies the default seat selection for the console demo.
 */
public final class MovieCatalogLoader {

    private static final String DEFAULT_RESOURCE = "/movies.properties";

    private MovieCatalogLoader() {
    }

    public static List<Movie> loadFromProperties() {
        return loadFromProperties(DEFAULT_RESOURCE);
    }

    public static List<Movie> loadFromProperties(String classpathResource) {
        Properties properties = loadProperties(classpathResource);
        int count = Integer.parseInt(properties.getProperty("movie.count", "0"));
        List<Movie> movies = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            movies.add(toMovie(properties, i));
        }
        return movies;
    }

    /**
     * Loads each movie's showtime configuration (timings, theatre, price and
     * seat-grid size) from movies.properties, keyed by the {@link Movie}
     * instances returned from {@link #loadFromProperties()}. The given
     * {@code catalog} must have been produced by that same properties file
     * and in the same order, since both are read by 1-based index.
     */
    public static Map<Movie, ShowtimeInfo> loadShowtimeInfo(List<Movie> catalog) {
        return loadShowtimeInfo(catalog, DEFAULT_RESOURCE);
    }

    public static Map<Movie, ShowtimeInfo> loadShowtimeInfo(List<Movie> catalog, String classpathResource) {
        Properties properties = loadProperties(classpathResource);
        Map<Movie, ShowtimeInfo> result = new LinkedHashMap<>();
        for (int i = 0; i < catalog.size(); i++) {
            result.put(catalog.get(i), toShowtimeInfo(properties, i + 1));
        }
        return result;
    }

    /**
     * Reads the default/demo seat selection (key: booking.seats, e.g. "C1,C4,C3").
     */
    public static List<String> loadDefaultSeats() {
        return loadDefaultSeats(DEFAULT_RESOURCE);
    }

    public static List<String> loadDefaultSeats(String classpathResource) {
        Properties properties = loadProperties(classpathResource);
        return splitCsv(properties.getProperty("booking.seats", "C1,C4,C3"));
    }

    private static Properties loadProperties(String classpathResource) {
        Properties properties = new Properties();
        try (InputStream in = MovieCatalogLoader.class.getResourceAsStream(classpathResource)) {
            if (in == null) {
                throw new IllegalStateException("Could not find " + classpathResource + " on the classpath.");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + classpathResource, e);
        }
        return properties;
    }

    private static Movie toMovie(Properties p, int index) {
        String prefix = "movie." + index + ".";
        return new Movie(
                p.getProperty(prefix + "title"),
                Double.parseDouble(p.getProperty(prefix + "rating")),
                Integer.parseInt(p.getProperty(prefix + "duration")),
                splitCsv(p.getProperty(prefix + "genres")),
                Integer.parseInt(p.getProperty(prefix + "year")),
                p.getProperty(prefix + "certification"),
                p.getProperty(prefix + "description"),
                p.getProperty(prefix + "director"),
                splitCsv(p.getProperty(prefix + "writers")),
                p.getProperty(prefix + "studio"),
                p.getProperty(prefix + "language"),
                Double.parseDouble(p.getProperty(prefix + "budget")),
                parseCast(p.getProperty(prefix + "cast")),
                splitCsv(p.getProperty(prefix + "keywords"))
        );
    }

    private static ShowtimeInfo toShowtimeInfo(Properties p, int index) {
        String prefix = "movie." + index + ".";
        List<LocalTime> times = splitCsv(p.getProperty(prefix + "showtimes")).stream()
                .map(LocalTime::parse)
                .toList();
        return new ShowtimeInfo(
                times,
                p.getProperty(prefix + "theatre"),
                Double.parseDouble(p.getProperty(prefix + "price")),
                Integer.parseInt(p.getProperty(prefix + "rows")),
                Integer.parseInt(p.getProperty(prefix + "seatsPerRow"))
        );
    }

    private static List<String> splitCsv(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /**
     * Parses "Leonardo D.:Dom Cobb;Elliot P.:Ariadne" into CastMember entries.
     */
    private static List<CastMember> parseCast(String value) {
        List<CastMember> cast = new ArrayList<>();
        for (String entry : value.split(";")) {
            String[] parts = entry.split(":", 2);
            if (parts.length == 2) {
                cast.add(new CastMember(parts[0].trim(), parts[1].trim()));
            }
        }
        return cast;
    }
}
