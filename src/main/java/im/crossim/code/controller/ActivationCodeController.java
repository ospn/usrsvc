package im.crossim.code.controller;

import im.crossim.code.dto.UseDto;
import im.crossim.code.service.ActivationCodeService;
import im.crossim.code.vo.UseVo;
import im.crossim.common.vo.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(
        tags = {"激活码模块"}
)
@RestController
@RequestMapping("/api/activation-code")
@Validated
public class ActivationCodeController {

    @Autowired
    private ActivationCodeService activationCodeService;

    @ApiOperation(
            value = "使用激活码",
            notes = "使用激活码。",
            httpMethod = "POST"
    )
    @RequestMapping(
            value = "/use",
            method = {RequestMethod.POST}
    )
    public ApiResult<UseVo> use(@Validated @RequestBody UseDto dto) {
        return activationCodeService.use(dto);
    }

}
