package im.crossim.user.service;

import com.alibaba.fastjson.JSONObject;
import com.ospn.common.OsnServer;
import com.ospn.common.OsnUtils;
import com.ospn.core.ILTPEvent;
import com.ospn.core.LTPData;
import im.crossim.push.service.HuaweiPushService;
import im.crossim.push.service.MiPushService;
import im.crossim.push.service.PushService;
import im.crossim.user.entity.UserDeviceEntity;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

import static com.ospn.utils.CryptUtils.makeMessage;
import static com.ospn.utils.CryptUtils.takeMessage;

@Slf4j
@Service
public class LTPServer extends OsnServer implements ILTPEvent {

    private static LTPData ltpData;

    // 自定义密码。
    private static String properPassword = "123456789";

    @Autowired
    private UserDeviceService userDeviceService;

    @Autowired
    private HuaweiPushService huaweiPushService;

    @Autowired
    private MiPushService miPushService;

    @Autowired
    private PushService pushService;

    public LTPServer() {
        init();
    }

    /**
     * 初始化LTPServer
     */
    private void init() {
        // 创建LTPData对象。
        if (ltpData != null) {
            return;
        }
        ltpData = new LTPData();

        try {
            // 加载ospn.properties配置文件。
            Properties prop;
            try (FileInputStream in = new FileInputStream("ospn.properties")) {
                prop = new Properties();
                prop.load(in);

                // 根据ospn.properties配置文件内的配置初始化LTPData对象。
                ltpData.init(prop, properPassword, this);
            }

            // 往配置内写入appId。
            prop.put("appID", ltpData.apps.osnID);

            // 生成私钥，并往配置内写入appKey。
            String privateKey = OsnUtils.aesEncrypt(ltpData.apps.osnKey, properPassword);
            prop.put("appKey", privateKey);

            // 将最新的配置写入ospn.properties配置文件。
            try (FileOutputStream out = new FileOutputStream("ospn.properties", false)) {
                prop.store(out, "Comments");
            }
        } catch (Exception ex) {
            log.error("LTPServer初始化失败", ex);
        }
    }

    /**
     * 用于获取DappInfo
     */
    public String getDappInfoNoParam() {
        JSONObject content = ltpData.dappInfo.toClientJsonNoParam(ltpData.apps.osnKey);
        if (content == null) {
            return "";
        }
        return content.toString();
    }

    @Override
    public void handleMessage(ChannelHandlerContext channelHandlerContext, JSONObject message) {

        //System.out.println("[LtpServer::handleMessage] begin. " + jsonObject);

        System.out.println("----------------handleMessage---------------------:"+message);
        // 处理收到的消息
        if (message == null){
            return;
        }


        String command = message.getString("command");
        String to = message.getString("to");
        if (command == null || to == null){
            return;
        }


        if (!to.equalsIgnoreCase(ltpData.apps.osnID)){
            System.out.println("[LtpServer::handleMessage] error, to is " + to);
            return;
        }


        //System.out.println("[handleMessage] begin!!!!!!");

        log.info("获取回调指令--------------------->>>>{}",message);
        if("UserInfo".equalsIgnoreCase(command)){

//            handleUserInfo(jsonObject);

        } else if("Message".equalsIgnoreCase(command)){

            JSONObject data = takeMessage(message);
            if(data == null) {
                return;
            }
            //解密完拿到token
            String command2 = data.getString("command");

            log.info("解密拿到了回调指令--------------------->>>>{}",command2);
            if (command2 != null) {
                if ("PushInfo".equalsIgnoreCase(command2)) {
                    //接收推送消息
                    String user = data.getString("user");
                    if (StringUtils.isEmpty(user)) {
                        log.error("【消息推送】准备推送消息，但是用户OSN ID为空");
                        return;
                    }

                    log.info(
                            "【消息推送】准备推送消息，用户OSN ID={}",
                            user
                    );

                    pushService.cacheMessageReceivedNotification(user);

                    return;
                }
            }


            String commandID = data.getString("commandID");
            if (commandID == null){
                return;
            }
            System.out.println("commandID : " + commandID);
            String result = data.getString("result");
            if (result == null){
                return;
            }



        } else if ("GetServiceInfo".equalsIgnoreCase(command)){

            String from = message.getString("from");
            if (from == null){
                return;
            }

            JSONObject content = ltpData.dappInfo.toClientJson(ltpData.apps.osnKey);

            System.out.println("[LtpServer::HandleMessage] dapp content : " + content);

            JSONObject original = new JSONObject();
            original.put("id", message.getString("id"));

            System.out.println("[LtpServer::HandleMessage] original id : " + original);

            JSONObject returnData = makeMessage(
                    "ServiceInfo",
                    ltpData.apps.osnID,
                    from,
                    content,
                    ltpData.apps.osnKey,
                    original);

            ltpData.sendJson(returnData);

        }
    }

    public static LTPData getLtpData() {
        return ltpData;
    }
}
