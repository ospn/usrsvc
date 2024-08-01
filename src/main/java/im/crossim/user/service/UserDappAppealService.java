package im.crossim.user.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.crossim.common.enums.ResultCodeEnum;
import im.crossim.common.exception.BusinessException;
import im.crossim.common.exception.SystemException;
import im.crossim.common.utils.UrlUtil;
import im.crossim.user.entity.UserDappAppealEntity;
import im.crossim.user.entity.UserEntity;
import im.crossim.user.enums.UserDappAppealStatusEnum;
import im.crossim.user.mapper.UserDappAppealMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class UserDappAppealService extends ServiceImpl<UserDappAppealMapper, UserDappAppealEntity> implements IService<UserDappAppealEntity> {

    public void appeal(UserEntity user, String dappInfoStr) {
        JSONObject dappInfo;
        try {
            dappInfo = JSONObject.parseObject(dappInfoStr);
        } catch (Exception ex) {
            log.error(
                    String.format(
                            "解析DAPP INFO失败：%s",
                            dappInfoStr
                    ),
                    ex
            );
            throw new BusinessException(ResultCodeEnum.INVALID_DAPP_INFO);
        }

        String dappInfoName = StringUtils.defaultIfEmpty(
                dappInfo.getString("name"),
                ""
        );
        String dappInfoPortrait = StringUtils.defaultIfEmpty(
                dappInfo.getString("portrait"),
                ""
        );
        String dappInfoUrl = StringUtils.defaultIfEmpty(
                dappInfo.getString("url"),
                ""
        );
        String dappInfoTarget = StringUtils.defaultIfEmpty(
                dappInfo.getString("target"),
                ""
        );

        String dappInfoUrlHost = StringUtils.defaultString(
                UrlUtil.extractUrlHost(dappInfoUrl),
                ""
        );

        UserDappAppealEntity userDappAppealEntity = new UserDappAppealEntity();
        userDappAppealEntity.setUserId(user.getId());
        userDappAppealEntity.setDappInfo(dappInfo);
        userDappAppealEntity.setDappInfoName(dappInfoName);
        userDappAppealEntity.setDappInfoPortrait(dappInfoPortrait);
        userDappAppealEntity.setDappInfoUrl(dappInfoUrl);
        userDappAppealEntity.setDappInfoUrlHost(dappInfoUrlHost);
        userDappAppealEntity.setDappInfoTarget(dappInfoTarget);
        userDappAppealEntity.setStatus(UserDappAppealStatusEnum.NOT_PROCESSED.getValue());
        if (!this.save(userDappAppealEntity)) {
            throw new SystemException("failed to save user dapp appeal to database");
        }
    }

}
