package im.crossim.code.service.handler;

import com.alibaba.fastjson.JSONObject;
import im.crossim.code.entity.ActivationCodeEntity;
import im.crossim.code.enums.ActivationCodeTypeEnum;
import im.crossim.common.enums.ResultCodeEnum;
import im.crossim.common.exception.BusinessException;
import im.crossim.common.exception.SystemException;
import im.crossim.group.entity.GroupEntity;
import im.crossim.group.service.GroupService;
import im.crossim.user.entity.UserEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupMaxMemberCountUpActivationCodeHandler implements ActivationCodeHandler {

    private static final int GROUP_MAX_MEMBER_COUNT = 1000;

    @Autowired
    private GroupService groupService;

    @Override
    public int getType() {
        return ActivationCodeTypeEnum.GROUP_MAX_MEMBER_COUNT_UP.getValue();
    }

    @Override
    public JSONObject handle(ActivationCodeEntity activationCode, UserEntity user, JSONObject param) {
        if (param == null) {
            throw new SystemException(ResultCodeEnum.INVALID_PARAM);
        }

        String groupOsnId = param.getString("groupOsnId");
        if (StringUtils.isEmpty(groupOsnId)) {
            throw new SystemException(ResultCodeEnum.INVALID_PARAM);
        }

        // 获取群组对象。
        GroupEntity groupEntity = groupService.getByGroupOsnId(groupOsnId);
        if (groupEntity == null) {
            throw new BusinessException(ResultCodeEnum.GROUP_NOT_EXISTS);
        }

        // 检测当前用户是否为该群群主。
        if (!user.getId().equals(groupEntity.getUserId())) {
            throw new BusinessException(ResultCodeEnum.NOT_GROUP_MASTER);
        }

        // 检测是否已经升级过。
        if (groupEntity.getMaxMemberCount() >= GROUP_MAX_MEMBER_COUNT) {
            throw new BusinessException(ResultCodeEnum.GROUP_MAX_MEMBER_COUNT_ALREADY);
        }

        // 升级群组最大人数。
        JSONObject result = groupService.setGroupMax(groupOsnId, GROUP_MAX_MEMBER_COUNT);
        if (result == null) {
            throw new SystemException(ResultCodeEnum.FAILED_TO_SET_GROUP_MAX_MEMBER_COUNT);
        }

        // 修改本地群组最大人数。
        groupService.updateMaxMemberCount(groupEntity.getId(), GROUP_MAX_MEMBER_COUNT);

        return result;
    }

}
