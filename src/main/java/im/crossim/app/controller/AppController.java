package im.crossim.app.controller;

import im.crossim.app.service.AppService;
import im.crossim.app.vo.GetAppConfigVo;
import im.crossim.common.vo.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(
        tags = {"APP模块"}
)
@RestController
@RequestMapping("/api/app")
@Validated
public class AppController {

    @Autowired
    private AppService appService;

    @ApiOperation(
            value = "获取APP配置",
            notes = "获取APP配置。",
            httpMethod = "GET"
    )
    @RequestMapping(
            value = "/get-app-config",
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    public ApiResult<GetAppConfigVo> getAppConfig() {
        return appService.getAppConfig();
    }

}
