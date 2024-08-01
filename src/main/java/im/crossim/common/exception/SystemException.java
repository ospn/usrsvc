package im.crossim.common.exception;

import im.crossim.common.enums.ResultCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SystemException extends RuntimeException {

    private Integer code = 1000;

    public SystemException() {
    }

    public SystemException(String message) {
        super(message);
    }

    public SystemException(int code, String message) {
        super(message);
        this.code = code;
    }

    public SystemException(Exception ex) {
        super(ex);
    }

    public SystemException(ResultCodeEnum resultCodeEnum) {
        this(
                resultCodeEnum.getCode(),
                resultCodeEnum.getMsg()
        );
    }

}
