package pub.zjh.mall.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

public class MallException extends RuntimeException {

    public MallException() {
    }

    public MallException(String message) {
        super(message);
    }

    public MallException(Throwable cause) {
        super(cause);
    }

}
