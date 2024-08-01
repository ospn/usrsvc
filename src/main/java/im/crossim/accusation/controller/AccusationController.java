package im.crossim.accusation.controller;

import im.crossim.accusation.dto.AccuseDto;
import im.crossim.accusation.service.AccusationService;
import im.crossim.common.vo.ApiResult;
import im.crossim.common.vo.EmptyVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(
        tags = {"举报模块"}
)
@RestController
@RequestMapping("/api/accusation")
@Validated
public class AccusationController {

    @Autowired
    private AccusationService accusationService;

    @ApiOperation(
            value = "举报用户与群组",
            notes = "举报用户与群组。",
            httpMethod = "POST"
    )
    @RequestMapping(
            value = "/accuse",
            method = {RequestMethod.POST}
    )
    public ApiResult<EmptyVo> accuse(@RequestBody AccuseDto dto) {
        return accusationService.accuse(dto);
    }

}
