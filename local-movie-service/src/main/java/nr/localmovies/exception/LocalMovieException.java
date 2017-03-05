package nr.localmovies.exception;

public abstract class LocalMovieException extends Exception {
    public abstract TitleRequestError getErrorEnum();
}
