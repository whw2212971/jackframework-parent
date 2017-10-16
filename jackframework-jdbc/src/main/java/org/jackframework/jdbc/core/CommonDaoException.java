package org.jackframework.jdbc.core;

import org.jackframework.common.exceptions.RunningException;

public class CommonDaoException extends RunningException {

    public CommonDaoException() {
    }

    public CommonDaoException(String message, Object... arguments) {
        super(message, arguments);
    }

    public CommonDaoException(Throwable cause, String message, Object... arguments) {
        super(cause, message, arguments);
    }

    public CommonDaoException(Throwable cause) {
        super(cause);
    }

}
