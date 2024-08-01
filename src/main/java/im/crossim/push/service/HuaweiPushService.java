package im.crossim.push.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import im.crossim.user.enums.UserDeviceVendorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class HuaweiPushService implements VendorPushService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String CLIENT_ID = "【华为推送的Client ID】";
    private static final String CLIENT_SECRET = "【华为推送的Client Secret】";

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class AccessTokenData {

        private String accessToken;
        private String tokenType;
        private Integer expiresIn;

    }

    private AccessTokenData getAccessTokenFromRemote() {
        String url = String.format(
                "https://oauth-login.cloud.huawei.com/oauth2/v3/token?grant_type=client_credentials&client_id=%s&client_secret=%s",
                CLIENT_ID,
                CLIENT_SECRET
        );
        String resp = HttpUtil.post(url, "");
        if (StringUtils.isEmpty(resp)) {
            log.error("【华为推送服务】获取AccessToken时，对方接口无响应");
            return null;
        }

        JSONObject respObj = JSONObject.parseObject(resp);
        if (respObj.containsKey("error")) {
            log.error(
                    "【华为推送服务】获取AccessToken时，对方接口返回错误，响应内容：{}",
                    resp
            );
            return null;
        }

        log.info(
                "【华为推送服务】获取AccessToken成功，信息：{}",
                respObj
        );

        return AccessTokenData.builder()
                .accessToken(respObj.getString("access_token"))
                .tokenType(respObj.getString("token_type"))
                .expiresIn(respObj.getIntValue("expires_in"))
                .build();
    }

    private static final String ACCESS_TOKEN_REDIS_KEY = "USRSVC:USER_SERVICE:HUAWEI_PUSH:ACCESS_TOKEN";

    private AccessTokenData getAccessTokenFromLocal() {
        String accessTokenDataJson = stringRedisTemplate.opsForValue().get(ACCESS_TOKEN_REDIS_KEY);
        if (StringUtils.isEmpty(accessTokenDataJson)) {
            AccessTokenData accessTokenData = getAccessTokenFromRemote();
            if (accessTokenData == null) {
                log.error("【华为推送服务】从远程获取AccessToken失败");
                return null;
            }

            stringRedisTemplate.opsForValue().set(
                    ACCESS_TOKEN_REDIS_KEY,
                    JSONObject.toJSONString(accessTokenData),
                    Duration.ofSeconds(accessTokenData.getExpiresIn() - 5 * 60) // 提前5分钟过期
            );

            return accessTokenData;
        } else {
            return JSONObject.toJavaObject(
                    JSONObject.parseObject(accessTokenDataJson),
                    AccessTokenData.class
            );
        }
    }

    @Override
    public int getVendor() {
        return UserDeviceVendorEnum.HUAWEI.getValue();
    }

    public boolean pushMessage(String title, String body, List<String> deviceIds) {
        String url = String.format(
                "https://push-api.cloud.huawei.com/v1/%s/messages:send",
                CLIENT_ID
        );

        AccessTokenData accessTokenData = getAccessTokenFromLocal();
        if (accessTokenData == null) {
            log.error("【华为推送服务】推送消息失败，无法获取Access Token");
            return false;
        }

        JSONObject req = new JSONObject()
                .fluentPut("validate_only", false)
                .fluentPut(
                        "message",
                        new JSONObject()
                                .fluentPut(
                                        "android",
                                        new JSONObject()
                                                .fluentPut(
                                                        "category",
                                                        "HEALTH"
                                                )
                                                .fluentPut(
                                                        "notification",
                                                        new JSONObject()
                                                                .fluentPut("title", title)
                                                                .fluentPut("body", body)
                                                                .fluentPut(
                                                                        "click_action",
                                                                        new JSONObject()
                                                                                .fluentPut("type", 3)
                                                                )
                                                )
                                )
                                .fluentPut(
                                        "token",
                                        deviceIds
                                )
                );
        String reqStr = req.toJSONString();

        HttpRequest request = HttpUtil.createPost(url);
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.header("Authorization", accessTokenData.getTokenType() + " " + accessTokenData.getAccessToken());
        request.body(reqStr);
        try (HttpResponse httpResponse = request.execute()) {
            if (!httpResponse.isOk()) {
                log.error(
                        "【华为推送服务】推送消息失败，发送接口HTTP CODE错误，HTTP CODE：{}",
                        httpResponse.getStatus()
                );
                return false;
            }

            String respStr = httpResponse.body();
            if (StringUtils.isEmpty(respStr)) {
                log.error("【华为推送服务】推送消息失败，对方接口无响应");
                return false;
            }

            JSONObject resp = JSONObject.parseObject(respStr);
            if ("80000000".equals(resp.getString("code"))) {
                log.info("【华为推送服务】推送消息成功，响应：{}", respStr);
                return true;
            } else {
                log.error(
                        "【华为推送服务】推送消息失败，对方接口响应错误，响应：{}",
                        respStr
                );
                return false;
            }
        }
    }

}
