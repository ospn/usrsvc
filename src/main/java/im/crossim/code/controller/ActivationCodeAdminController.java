package im.crossim.code.controller;

import im.crossim.code.dto.admin.GenerateDto;
import im.crossim.code.service.ActivationCodeAdminService;
import im.crossim.code.vo.admin.GenerateVo;
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
        tags = {"激活码（管理）模块"}
)
@RestController
@RequestMapping("/api/admin/activation-code")
@Validated
public class ActivationCodeAdminController {

    @Autowired
    private ActivationCodeAdminService activationCodeAdminService;

    @ApiOperation(
            value = "批量生成激活码",
            notes = "批量生成激活码。",
            httpMethod = "POST"
    )
    @RequestMapping(
            value = "/generate",
            method = {RequestMethod.POST}
    )
    public ApiResult<GenerateVo> generate(@Validated @RequestBody GenerateDto dto) {
        return activationCodeAdminService.generate(dto);
    }

}
