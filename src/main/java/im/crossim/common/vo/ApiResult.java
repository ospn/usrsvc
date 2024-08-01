package im.crossim.common.vo;

import im.crossim.common.enums.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("响应结果")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {

    public static final int SUCCESS_CODE = ResultCodeEnum.SUCCESS.getCode();
    public static final int ERROR_CODE = ResultCodeEnum.ERROR.getCode();

    public static final String SUCCESS_MSG = ResultCodeEnum.SUCCESS.getMsg();
    public static final String ERROR_MSG = ResultCodeEnum.ERROR.getMsg();

    @ApiModelProperty("响应码，除了0均表示失败")
    private Integer code;

    @ApiModelProperty("响应消息")
    private String msg;

    @ApiModelProperty("响应数据")
    private T data;

    public static <T> ApiResult<T> create(int code, String msg, T data) {
        return ApiResult.<T>builder()
                .code(code)
                .msg(msg)
                .data(data)
                .build();
    }

    public static <T> ApiResult<T> create(ResultCodeEnum resultCodeEnum, T data) {
        return create(
                resultCodeEnum.getCode(),
                resultCodeEnum.getMsg(),
                data
        );
    }

    public static <T> ApiResult<T> create(ResultCodeEnum resultCodeEnum) {
        return create(resultCodeEnum, null);
    }

    public static <T> ApiResult<T> success(String msg, T data) {
        return create(SUCCESS_CODE, msg, data);
    }

    public static <T> ApiResult<T> success(String msg) {
        return success(msg, null);
    }

    public static <T> ApiResult<T> success(T data) {
        return success(SUCCESS_MSG, data);
    }

    public static <T> ApiResult<T> success() {
        return success(SUCCESS_MSG, null);
    }

    public static <T> ApiResult<T> error(String msg, T data) {
        return create(ERROR_CODE, msg, data);
    }

    public static <T> ApiResult<T> error(String msg) {
        return error(msg, null);
    }

    public static <T> ApiResult<T> error(T data) {
        return error(ERROR_MSG, data);
    }

    public static <T> ApiResult<T> error() {
        return error(ERROR_MSG, null);
    }

}
