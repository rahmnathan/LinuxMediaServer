package com.github.rahmnathan.localmovie.domain;

public enum MovieEvent {
    ENTRY_CREATE("CREATE"),
    ENTRY_DELETE("DELETE");

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
