package im.crossim.message.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SmsService {

    @Value("${sms.gateway:}")
    private String gateway;

    @Value("${sms.username:}")
    private String username;

    @Value("${sms.password:}")
    private String password;

    @Value("${sms.sign:}")
    private String sign;

    public boolean sendInternationalMessage(String content, String mobile) {
        content = String.format("【%s】您的验证码为：%s，请按页面提示填写，切勿泄露于他人。", sign, content);
        return sendMessage(getSendUrl(false), mobile, content);
    }

    private String getSendUrl(boolean international) {
        if (!international) {
            // 国内短信。
            return String.format("%s/sms", gateway);
        }
        return String.format("%s/wsms", gateway);
    }

    private boolean sendMessage(String url, String mobile, String content) {
        Map<String, Object> params = new HashMap<>();
        params.put("u", username);
        params.put("p", password);
        params.put("m", mobile);
        params.put("c", content);

        String resp = HttpUtil.post(url, params);

        if ("0".equals(resp)) {
            return true;
        } else {
            log.error(
                    "请求短信网关发送短信失败，URL={}，请求参数={}，响应参数={}",
                    url,
                    JSONObject.toJSONString(params),
                    resp
            );
            return false;
        }
    }

}
