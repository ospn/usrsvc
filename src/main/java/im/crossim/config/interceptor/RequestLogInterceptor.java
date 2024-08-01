package im.crossim.config.interceptor;

import im.crossim.config.wrapper.RequestWrapper;
import im.crossim.log.service.RequestLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Configuration
@Component
public class RequestLogInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RequestLogService requestLogService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String requestBody = StringUtils.EMPTY;
            if (request instanceof RequestWrapper) {
                requestBody = ((RequestWrapper) request).getBody();
            }

            String requestId = requestLogService.logRequest(request, requestBody);

            HttpSession httpSession = request.getSession(true);
            httpSession.setAttribute("__REQUEST_ID__", requestId);
        } catch (Exception ex) {
            log.error("记录请求日志时发生异常", ex);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            HttpSession httpSession = request.getSession();

            // __RESPONSE_BODY__是在响应的时候写入到Session的key，可以看responseWrapper。
            String responseBody = (String) httpSession.getAttribute("__RESPONSE_BODY__");

            String requestId = (String) httpSession.getAttribute("__REQUEST_ID__");

            if (StringUtils.isNotEmpty(requestId)) {
                requestLogService.logResponse(requestId, responseBody);
            }
        } catch (Exception e) {
            log.error("记录响应日志时发生异常", e);
        }
    }
}
