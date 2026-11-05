package com.modernjava.moviebookingapp;

/**
 * A single row in the "Top Cast" horizontal scroll section of the movie
 * detail page, e.g. "Leonardo D. -> Dom Cobb".
 */
public record CastMember(String actorName, String characterName) {

    @Override
    public String toString() {
        return actorName + " -> " + characterName;
    }
}
