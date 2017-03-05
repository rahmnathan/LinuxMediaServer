package nr.localmovies.exception;

public class UnauthorizedFolderException extends LocalMovieException {

    @Override
    public TitleRequestError getErrorEnum(){
        return TitleRequestError.UNAUTHORIZED_FOLDER;
    }
}
