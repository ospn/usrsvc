package im.crossim.push.service;

import com.alibaba.fastjson.JSONObject;
import com.xiaomi.push.sdk.ErrorCode;
import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;
import im.crossim.user.enums.UserDeviceVendorEnum;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class MiPushService implements VendorPushService {

    @Override
    public int getVendor() {
        return UserDeviceVendorEnum.XIAOMI.getValue();
    }

    @Override
    public boolean pushMessage(String title, String body, List<String> deviceIds) {
        try {
            Constants.useOfficial();
            Sender sender = new Sender("【小米推送的Secret】");
            Message msg = new Message.Builder()
                    .title(title)
                    .description(body)
                    .payload(body)
                    .restrictedPackageName("【Android APP的包名】")
                    .notifyType(1)     // 使用默认提示音提示
                    .extra("channel_id", "【渠道ID】")
                    .extra(Constants.EXTRA_PARAM_NOTIFY_EFFECT, Constants.NOTIFY_LAUNCHER_ACTIVITY)
                    .build();
            Result result = sender.send(msg, deviceIds, 3);
            log.info(
                    "【消息推送】发送小米消息结果，用户regIds={}，结果={}",
                    JSONArray.toJSONString(deviceIds),
                    JSONObject.toJSONString(result)
            );
            return result != null
                    && result.getErrorCode().getValue() == ErrorCode.Success.getValue();
        } catch (Exception ex) {
            log.error(
                    String.format(
                            "【消息推送】发送小米消息时发生异常，用户regIds=%s",
                            JSONArray.toJSONString(deviceIds)
                    ),
                    ex
            );
            return false;
        }
    }
}
