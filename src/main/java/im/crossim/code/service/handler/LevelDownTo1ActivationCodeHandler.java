package im.crossim.code.service.handler;

import com.alibaba.fastjson.JSONObject;
import im.crossim.code.entity.ActivationCodeEntity;
import im.crossim.code.enums.ActivationCodeTypeEnum;
import im.crossim.user.entity.UserEntity;
import im.crossim.user.enums.UserVipLevelEnum;
import im.crossim.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LevelDownTo1ActivationCodeHandler implements ActivationCodeHandler {

    @Autowired
    private UserService userService;

    @Override
    public int getType() {
        return ActivationCodeTypeEnum.LEVEL_DOWN_TO_1.getValue();
    }

    @Override
    public JSONObject handle(ActivationCodeEntity activationCode, UserEntity user, JSONObject param) {
        userService.levelDown(user, UserVipLevelEnum.LEVEL_1.getValue());

        return null;
    }

}
