package im.crossim.group.controller;

import im.crossim.common.vo.ApiResult;
import im.crossim.common.vo.EmptyVo;
import im.crossim.group.dto.CreateGroupDto;
import im.crossim.group.service.GroupService;
import im.crossim.group.vo.CreateGroupVo;
import im.crossim.group.vo.ListGroupVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(
        tags = {"群组模块"}
)
@RestController
@RequestMapping("/api/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @ApiOperation(
            value = "创建群组",
            notes = "创建群组。",
            httpMethod = "POST"
    )
    @RequestMapping(
            value = "/create-group",
            method = {RequestMethod.POST}
    )
    public ApiResult<CreateGroupVo> createGroup(@Validated @RequestBody CreateGroupDto dto) {
        return groupService.createGroup(dto);
    }

    @ApiOperation(
            value = "删除群组",
            notes = "删除群组。",
            httpMethod = "DELETE"
    )
    @RequestMapping(
            value = "/delete-group",
            method = {RequestMethod.DELETE, RequestMethod.POST}
    )
    public ApiResult<EmptyVo> deleteGroup(
            @ApiParam("群组的OSN ID") @RequestParam String osnId
    ) {
        return groupService.deleteGroup(osnId);
    }

    @ApiOperation(
            value = "列出群组",
            notes = "列出群组。",
            httpMethod = "GET"
    )
    @RequestMapping(
            value = "/list-group",
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    public ApiResult<ListGroupVo> listGroup(
            @ApiParam("页码，从1开始") @RequestParam int current,
            @ApiParam("分页大小") @RequestParam int size
    ) {
        return groupService.listGroup(current, size);
    }

}
