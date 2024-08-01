package im.crossim.log.service;

import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.crossim.log.entity.RequestLogEntity;
import im.crossim.log.mapper.RequestLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class RequestLogService extends ServiceImpl<RequestLogMapper, RequestLogEntity> implements IService<RequestLogEntity> {

    public String logRequest(HttpServletRequest request, String requestBody) {
        String requestId = UUID.randomUUID().toString().replace("-", "");

        String clientIp = ServletUtil.getClientIP(request);
        String url = request.getRequestURL().toString();
        String path = URLUtil.getPath(url);
        String method = request.getMethod();

        JSONObject headers = new JSONObject();
        Enumeration<String> headerNamesEnum =  request.getHeaderNames();
        while (headerNamesEnum.hasMoreElements()) {
            String headerName = headerNamesEnum.nextElement();

            JSONArray headerValues = new JSONArray();
            Enumeration<String> headersEnum = request.getHeaders(headerName);
            while (headersEnum.hasMoreElements()) {
                headerValues.add(headersEnum.nextElement());
            }

            headers.put(headerName, headerValues);
        }

        RequestLogEntity requestLogEntity = new RequestLogEntity();
        requestLogEntity.setRequestId(requestId);
        requestLogEntity.setUserId(null);
        requestLogEntity.setClientIp(clientIp);
        requestLogEntity.setUrl(url);
        requestLogEntity.setPath(path);
        requestLogEntity.setMethod(method);
        requestLogEntity.setHeaders(headers);
        requestLogEntity.setRequestBody(requestBody);
        requestLogEntity.setResponseBody(null);
        this.save(requestLogEntity);

        log.info("请求日志【{}】请求信息：{}", requestId, JSONObject.toJSONString(requestLogEntity));

        return requestId;
    }

    public void logUser(String requestId, int userId) {
        UpdateWrapper<RequestLogEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(RequestLogEntity::getUserId, userId)
                .set(RequestLogEntity::getUpdateTime, new Date())
                .eq(RequestLogEntity::getRequestId, requestId);
        this.update(updateWrapper);

        log.info("请求日志【{}】用户信息：{}", requestId, userId);
    }

    public void logResponse(String requestId, String responseBody) {
        UpdateWrapper<RequestLogEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(RequestLogEntity::getResponseBody, responseBody)
                .set(RequestLogEntity::getUpdateTime, new Date())
                .eq(RequestLogEntity::getRequestId, requestId);
        this.update(updateWrapper);

        log.info("请求日志【{}】响应体：{}", requestId, responseBody);
    }

}
