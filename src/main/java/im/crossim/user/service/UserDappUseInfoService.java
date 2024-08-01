package im.crossim.user.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.crossim.common.enums.ResultCodeEnum;
import im.crossim.common.exception.BusinessException;
import im.crossim.common.exception.SystemException;
import im.crossim.common.utils.UrlUtil;
import im.crossim.user.entity.UserDappUseInfoEntity;
import im.crossim.user.entity.UserEntity;
import im.crossim.user.mapper.UserDappUseInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.util.DomainNameUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.net.util.IPAddressUtil;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class UserDappUseInfoService extends ServiceImpl<UserDappUseInfoMapper, UserDappUseInfoEntity> implements IService<UserDappUseInfoEntity> {

    public void log(UserEntity user, String dappInfoStr) {
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

        UserDappUseInfoEntity userDappUseInfoEntity = new UserDappUseInfoEntity();
        userDappUseInfoEntity.setUserId(user.getId());
        userDappUseInfoEntity.setDappInfo(dappInfo);
        userDappUseInfoEntity.setDappInfoName(dappInfoName);
        userDappUseInfoEntity.setDappInfoPortrait(dappInfoPortrait);
        userDappUseInfoEntity.setDappInfoUrl(dappInfoUrl);
        userDappUseInfoEntity.setDappInfoUrlHost(dappInfoUrlHost);
        userDappUseInfoEntity.setDappInfoTarget(dappInfoTarget);
        userDappUseInfoEntity.setBan(false);
        if (!this.save(userDappUseInfoEntity)) {
            throw new SystemException("failed to save user dapp use info to database");
        }
    }

}
