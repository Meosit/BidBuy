package by.mksn.epam.mentalaid.service.exception;

/**
 * Thrown to indicate that something wrong with question logic
 */
public class QuestionServiceException extends ServiceException {

    /**
     * Cause of exception when invalid page index passed
     */
    public static final int INVALID_PAGE_INDEX = 0x01;
    /**
     * Cause of exception when invalid properties values passed
     */
    public static final int WRONG_INPUT = 0x11;

    private int causeCode;

    public QuestionServiceException(int causeCode) {
        super();
        this.causeCode = causeCode;
    }

    public QuestionServiceException(String cause, int causeCode) {
        super(cause);
        this.causeCode = causeCode;
    }

    public QuestionServiceException(Throwable t, int causeCode) {
        super(t);
        this.causeCode = causeCode;
    }

    public QuestionServiceException(String cause, Throwable t, int causeCode) {
        super(cause, t);
        this.causeCode = causeCode;
    }

    /**
     * Represents code of exception cause
     *
     * @return code of exception cause, one of the following:<br>
     * - {@link #WRONG_INPUT}<br>
     * - {@link #INVALID_PAGE_INDEX}
     */
    public int getCauseCode() {
        return causeCode;
    }
}
