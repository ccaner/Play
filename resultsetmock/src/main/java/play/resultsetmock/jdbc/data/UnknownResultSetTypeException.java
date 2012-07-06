package play.resultsetmock.jdbc.data;

public class UnknownResultSetTypeException extends IllegalArgumentException {

    public UnknownResultSetTypeException(String message) {
        super(message);
    }

}
