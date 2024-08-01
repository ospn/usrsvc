package im.crossim.code.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.crossim.code.dto.UseDto;
import im.crossim.code.entity.ActivationCodeEntity;
import im.crossim.code.mapper.ActivationCodeMapper;
import im.crossim.code.service.handler.ActivationCodeHandler;
import im.crossim.code.vo.UseVo;
import im.crossim.common.enums.ResultCodeEnum;
import im.crossim.common.exception.BusinessException;
import im.crossim.common.exception.SystemException;
import im.crossim.common.utils.UserUtil;
import im.crossim.common.vo.ApiResult;
import im.crossim.common.vo.EmptyVo;
import im.crossim.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class ActivationCodeService extends ServiceImpl<ActivationCodeMapper, ActivationCodeEntity> implements IService<ActivationCodeEntity>, ApplicationRunner {

    @Autowired
    private ActivationCodeMapper activationCodeMapper;

    @Autowired
    private ApplicationContext applicationContext;

    private final Map<Integer, ActivationCodeHandler> HANDLER_MAP = new ConcurrentHashMap<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (ActivationCodeHandler handler : applicationContext.getBeansOfType(ActivationCodeHandler.class).values()) {
            HANDLER_MAP.put(handler.getType(), handler);
        }
    }

    private ActivationCodeHandler getHandler(int type) {
        return HANDLER_MAP.get(type);
    }

    private ActivationCodeEntity getActivationCode(String code, Integer type) {
        Date now = new Date();

        LambdaQueryWrapper<ActivationCodeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivationCodeEntity::getCode, code)
                .eq(ActivationCodeEntity::getDeleted, false)
                .eq(ActivationCodeEntity::getEnabled, true)
                .le(ActivationCodeEntity::getEffectiveTimeStart, now)
                .ge(ActivationCodeEntity::getEffectiveTimeEnd, now);
        if (type != null) {
            queryWrapper.eq(ActivationCodeEntity::getType, type);
        }

        return this.getOne(queryWrapper);
    }

    private void useActivationCode(int activationCodeId, int userId) {
        Date now = new Date();

        LambdaUpdateWrapper<ActivationCodeEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(ActivationCodeEntity::getUsed, true)
                .set(ActivationCodeEntity::getUsedBy, userId)
                .set(ActivationCodeEntity::getUsedTime, now)
                .set(ActivationCodeEntity::getUpdateTime, now)
                .eq(ActivationCodeEntity::getId, activationCodeId);
        this.update(updateWrapper);
    }

    public ApiResult<UseVo> use(UseDto dto) {
        String code = StringUtils.trim(dto.getCode());
        Integer type = dto.getType();
        JSONObject param = dto.getParam();

        // 获取当前用户。
        UserEntity currentUser = UserUtil.getCurrentUser();

        // 获取激活码。
        ActivationCodeEntity activationCode = getActivationCode(code, type);
        if (activationCode == null) {
            // 错误的激活码。
            throw new BusinessException(ResultCodeEnum.INVALID_ACTIVATION_CODE);
        }
        if (activationCode.getUsed()) {
            // 激活码已被使用。
            throw new BusinessException(ResultCodeEnum.USED_ACTIVATION_CODE);
        }

        // 处理激活码的使用行为。
        ActivationCodeHandler handler = getHandler(activationCode.getType());
        if (handler == null) {
            // 无该类型的激活码的处理器。
            throw new SystemException(ResultCodeEnum.ACTIVATION_CODE_HANDLER_NOT_EXISTS);
        }
        JSONObject result = handler.handle(activationCode, currentUser, param);

        // 标记激活码为已使用。
        useActivationCode(activationCode.getId(), currentUser.getId());

        return ApiResult.success(
                UseVo.builder()
                        .result(result)
                        .build()
        );
    }

}
