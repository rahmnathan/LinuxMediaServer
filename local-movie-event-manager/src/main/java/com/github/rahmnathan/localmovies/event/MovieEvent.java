package com.github.rahmnathan.localmovies.event;

public enum MovieEvent {
    ENTRY_CREATE("CREATE"),
    DELETE("DELETE");

    private final String movieEventString;

    MovieEvent(String movieEventString) {
        this.movieEventString = movieEventString;
    }

    public String getMovieEventString() {
        return movieEventString;
    }

    public String getAsString(){
        return movieEventString;
    }
}
