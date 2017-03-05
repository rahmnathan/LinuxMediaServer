package nr.localmovies.exception;

public class EmptyDirectoryException extends LocalMovieException {

    @Override
    public TitleRequestError getErrorEnum(){
        return TitleRequestError.EMPTY_DIRECTORY;
    }
}
