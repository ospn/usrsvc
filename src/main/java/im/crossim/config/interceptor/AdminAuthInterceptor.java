package im.crossim.config.interceptor;

import im.crossim.common.enums.ResultCodeEnum;
import im.crossim.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Configuration
@Component
public class AdminAuthInterceptor extends HandlerInterceptorAdapter {

    private static final String KEY = "1234567890";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authHeader)) {
            throw new BusinessException(ResultCodeEnum.AUTHORIZATION_FAILED);
        }

        if (!authHeader.startsWith("Key")) {
            throw new BusinessException(ResultCodeEnum.AUTHORIZATION_FAILED);
        }

        String[] arr = authHeader.split("\\s");
        if (arr.length != 2) {
            throw new BusinessException(ResultCodeEnum.AUTHORIZATION_FAILED);
        }
        String key = arr[1];
        if (!KEY.equals(key)) {
            throw new BusinessException(ResultCodeEnum.AUTHORIZATION_FAILED);
        }

        return true;
    }

}
