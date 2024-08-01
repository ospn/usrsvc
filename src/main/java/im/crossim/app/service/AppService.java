package im.crossim.app.service;

import im.crossim.app.vo.GetAppConfigVo;
import im.crossim.common.vo.ApiResult;
import im.crossim.config.web.UserServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class AppService {

    @Autowired
    private UserServiceConfig userServiceConfig;

    public ApiResult<GetAppConfigVo> getAppConfig() {
        GetAppConfigVo vo = GetAppConfigVo.builder()
                .downloadPageUrl(userServiceConfig.getAppConfig().getDownloadPageUrl())
                .version(userServiceConfig.getAppConfig().getVersion())
                .force(userServiceConfig.getAppConfig().getForce())
                .build();

        return ApiResult.success(vo);
    }

}
