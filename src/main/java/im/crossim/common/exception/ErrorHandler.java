package im.crossim.common.exception;

import im.crossim.common.vo.ApiResult;
import im.crossim.common.vo.EmptyVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public ApiResult<EmptyVo> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException全局异常拦截", ex);
        return ApiResult.error();
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ApiResult<EmptyVo> handleException(Exception ex) {
        log.error("Exception全局异常拦截", ex);
        return ApiResult.error();
    }

    @ResponseBody
    @ExceptionHandler(BusinessException.class)
    public ApiResult<EmptyVo> handleBusinessException(BusinessException ex) {
        log.error("BusinessException全局异常拦截", ex);
        return ApiResult.create(ex.getCode(), ex.getMessage(), null);
    }

    @ResponseBody
    @ExceptionHandler(SystemException.class)
    public ApiResult<EmptyVo> handleSystemException(SystemException ex) {
        log.error("SystemException全局异常拦截", ex);
        return ApiResult.create(ex.getCode(), ex.getMessage(), null);
    }

    @ResponseBody
    @ExceptionHandler(value = {BindException.class})
    public ApiResult<EmptyVo> handleBindException(BindException ex) {
        log.error("BindException全局异常拦截", ex);
        return ApiResult.error(ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ApiResult<EmptyVo> handleConstraintViolationException(ConstraintViolationException ex) {
        // TODO: 美化错误信息。
        log.error("ConstraintViolationException全局异常拦截", ex);
        return ApiResult.error(ex.getMessage());
    }

}
