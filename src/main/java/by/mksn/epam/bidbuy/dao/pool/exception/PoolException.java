package by.mksn.epam.bidbuy.dao.pool.exception;

import by.mksn.epam.bidbuy.dao.pool.ConnectionPool;

/**
 * Exception which represents not normal behaviour of {@link ConnectionPool}, something
 * like poll timeout or other stuff
 */
public class PoolException extends Exception {

    public PoolException() {
        super();
    }

    public PoolException(String cause) {
        super(cause);
    }

    public PoolException(Throwable t) {
        super(t);
    }

    public PoolException(String cause, Throwable t) {
        super(cause, t);
    }

}