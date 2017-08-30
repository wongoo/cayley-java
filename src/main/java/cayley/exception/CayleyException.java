package cayley.exception;

/**
 * @author wangoo
 * @since 2017-08-30 14:29
 */
public class CayleyException extends Exception {
    public CayleyException() {
    }

    public CayleyException(String message) {
        super(message);
    }

    public CayleyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CayleyException(Throwable cause) {
        super(cause);
    }

    public CayleyException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
