package im.crossim.code.service.handler;

import com.alibaba.fastjson.JSONObject;
import im.crossim.code.entity.ActivationCodeEntity;
import im.crossim.user.entity.UserEntity;

public interface ActivationCodeHandler {

    int getType();

    JSONObject handle(ActivationCodeEntity activationCode, UserEntity user, JSONObject param);

}
