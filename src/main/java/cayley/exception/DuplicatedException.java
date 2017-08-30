package cayley.exception;

/**
 * @author wangoo
 * @since 2017-08-30 14:25
 */
public class DuplicatedException extends CayleyException {
    public DuplicatedException() {
    }

    public DuplicatedException(String message) {
        super(message);
    }

    public DuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedException(Throwable cause) {
        super(cause);
    }

    public DuplicatedException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
