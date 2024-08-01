package im.crossim.config.interceptor;

import com.alibaba.fastjson.JSONObject;
import im.crossim.common.enums.ResultCodeEnum;
import im.crossim.common.exception.BusinessException;
import im.crossim.common.utils.UserUtil;
import im.crossim.log.service.RequestLogService;
import im.crossim.user.entity.UserEntity;
import im.crossim.user.pojo.TokenData;
import im.crossim.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Configuration
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestLogService requestLogService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserUtil.clearCurrentUser();

        String authHeader = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authHeader)) {
            throw new BusinessException(ResultCodeEnum.AUTHORIZATION_FAILED);
        }

        if (!authHeader.startsWith("Token")) {
            throw new BusinessException(ResultCodeEnum.AUTHORIZATION_FAILED);
        }

        String[] arr = authHeader.split("\\s");
        if (arr.length != 2) {
            throw new BusinessException(ResultCodeEnum.AUTHORIZATION_FAILED);
        }
        String token = arr[1];

        String redisKey = userService.generateLoginTokenRedisKey(token);
        String tokenDataJson = stringRedisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isEmpty(tokenDataJson)) {
            throw new BusinessException(ResultCodeEnum.AUTHORIZATION_FAILED);
        }

        TokenData tokenData = null;
        try {
            tokenData = JSONObject.parseObject(tokenDataJson, TokenData.class);
        } catch (Exception ex) {
            log.error("解析TokenData的JSON时发生错误：" + tokenDataJson, ex);
        }
        if (tokenData == null) {
            throw new BusinessException(ResultCodeEnum.AUTHORIZATION_FAILED);
        }

        UserEntity userEntity = userService.getById(tokenData.getUserId());
        if (userEntity == null) {
            throw new BusinessException(ResultCodeEnum.AUTHORIZATION_FAILED);
        }
        UserUtil.setCurrentUser(userEntity);

        try {
            HttpSession httpSession = request.getSession();
            String requestId = (String) httpSession.getAttribute("__REQUEST_ID__");
            if (StringUtils.isNotEmpty(requestId)) {
                requestLogService.logUser(requestId, userEntity.getId());
            }
        } catch (Exception ex) {
            log.error("记录请求日志所属用户时发生异常", ex);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserUtil.clearCurrentUser();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserUtil.clearCurrentUser();
    }
}
