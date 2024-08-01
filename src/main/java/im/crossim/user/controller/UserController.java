package im.crossim.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.ospn.command.CmdReDappLogin;
import im.crossim.common.vo.ApiResult;
import im.crossim.common.vo.EmptyVo;
import im.crossim.project.entity.ProjectEntity;
import im.crossim.project.service.ProjectService;
import im.crossim.user.dto.*;
import im.crossim.user.service.UserService;
import im.crossim.user.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Api(
        tags = {"用户模块"}
)
@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @ApiOperation(
            value = "注册用户",
            notes = "可通过用户名、手机号码、邮箱地址注册用户。",
            httpMethod = "POST"
    )
    @RequestMapping(
            value = "/register",
            method = {RequestMethod.POST}
    )
    public ApiResult<RegisterVo> register(@Validated @RequestBody RegisterDto dto) {
        return userService.register(dto);
    }

    @ApiOperation(
            value = "登录",
            notes = "可通过用户名、手机号码、邮箱地址进行登录。",
            httpMethod = "POST"
    )
    @RequestMapping(
            value = "/login",
            method = {RequestMethod.POST}
    )
    public ApiResult<LoginVo> login(@Validated @RequestBody LoginDto dto) {
        return userService.login(dto);
    }

    @ApiOperation(
            value = "获取DAPP INFO",
            notes = "获取DAPP INFO。",
            httpMethod = "GET"
    )
    @RequestMapping(
            value = "/get-dapp-info",
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    public String getDappInfo() {
        return userService.getDappInfo();
    }

    @ApiOperation(
            value = "DAPP登录",
            notes = "DAPP登录。",
            httpMethod = "POST"
    )
    @RequestMapping(
            value = "/dapp-login",
            method = {RequestMethod.POST}
    )
    public JSONObject dappLogin(@Validated @RequestBody DappRequestDto dto) {
        return userService.dappLogin(dto);
    }

    @ApiOperation(
            value = "获取登录信息",
            notes = "获取登录信息。",
            httpMethod = "POST"
    )
    @RequestMapping(
            value = "/get-login-info",
            method = {RequestMethod.POST}
    )
    public ApiResult<GetLoginInfoVo> getLoginInfo(@Validated @RequestBody GetLoginInfoDto dto) {
        return userService.getLoginInfo(dto);
    }

    @ApiOperation(
            value = "双密钥登录",
            notes = "双密钥登录。",
            httpMethod = "POST"
    )
    @RequestMapping(
            value = "/login-with-double-key",
            method = {RequestMethod.POST}
    )
    public ApiResult<LoginVo> loginWithDoubleKey(@Validated @RequestBody LoginWithDoubleKeyDto dto) {
        ProjectEntity projectEntity = projectService.getByProject(dto.getUsername());
        if (projectEntity == null) {
            // project不存在，虚构一个用户返回回去。
            // 只需要虚构token和refreshToken。
            String fakeToken = UUID.randomUUID().toString().replace("-", "");
            String fakeRefreshToken = UUID.randomUUID().toString().replace("-", "");

            // 返回虚构的登录成功响应。
            return ApiResult.success(
                    LoginVo.builder()
                            .token(fakeToken)
                            .refreshToken(fakeRefreshToken)
                            .build()
            );
        }

        return userService.loginWithDoubleKey(dto);
    }





    @ApiOperation(
            value = "修改密码",
            notes = "修改当前用户的密码。",
            httpMethod = "POST"
    )
    @RequestMapping(
            value = "/change-password",
            method = {RequestMethod.POST}
    )
    public ApiResult<EmptyVo> changePassword(@Validated @RequestBody ChangePasswordDto dto) {
        return userService.changePassword(dto);
    }

    @ApiOperation(
            value = "拉取配置",
            notes = "拉取配置。",
            httpMethod = "GET"
    )
    @RequestMapping(
            value = "/fetch-config",
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    public ApiResult<FetchConfigVo> fetchConfig(
            @ApiParam("项目") @RequestParam(required = false) String project
    ) {
        return userService.fetchConfig(project);
    }

    @ApiOperation(
            value = "找回密码",
            notes = "通过手机号码或邮箱地址找回密码。",
            httpMethod = "POST"
    )
    @RequestMapping(
            value = "/change-password-by-code",
            method = {RequestMethod.POST}
    )
    public ApiResult<EmptyVo> changePasswordByCode(@Validated @RequestBody ChangePasswordByCodeDto dto) {
        return userService.changePasswordByCode(dto);
    }

    @ApiOperation(
            value = "刷新Token",
            notes = "通过现有的刷新Token获取新的登录Token与刷新Token，同时原有的登录Token与刷新Token失效。",
            httpMethod = "POST"
    )
    @RequestMapping(
            value = "/refresh-token",
            method = {RequestMethod.POST}
    )
    public ApiResult<RefreshTokenVo> refreshToken(@Validated @RequestBody RefreshTokenDto dto) {
        return userService.refreshToken(dto);
    }

    @ApiOperation(
            value = "发送验证码",
            notes = "发送验证码，用于：注册、登录、找回密码。",
            httpMethod = "POST"
    )
    @RequestMapping(
            value = "/send-code",
            method = {RequestMethod.POST}
    )
    public ApiResult<EmptyVo> sendCode(@Validated @RequestBody SendCodeDto dto) {
        return userService.sendCode(dto);
    }

    @ApiOperation(
            value = "注销账号",
            notes = "注销账号。",
            httpMethod = "DELETE"
    )
    @RequestMapping(
            value = "/delete-self",
            method = {RequestMethod.DELETE, RequestMethod.POST}
    )
    public ApiResult<EmptyVo> deleteSelf() {
        return userService.deleteSelf();
    }

    @ApiOperation(
            value = "记录用户的DAPP使用信息",
            notes = "记录用户的DAPP使用信息。",
            httpMethod = "POST"
    )
    @RequestMapping(
            value = "/log-dapp-use-info",
            method = {RequestMethod.POST}
    )
    public ApiResult<EmptyVo> logDappUseInfo(@RequestBody LogDappUseInfoDto dto) {
        return userService.logDappUseInfo(dto);
    }

    @ApiOperation(
            value = "确认用户是否已存在",
            notes = "确认用户是否已存在。",
            httpMethod = "GET"
    )
    @RequestMapping(
            value = "/has-user",
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    public ApiResult<HasUserVo> hasUser(
            @ApiParam("用户的OSN ID") @RequestParam String osnId
    ) {
        return userService.hasUser(osnId);
    }

    @ApiOperation(
            value = "申诉DAPP",
            notes = "申诉某个已被拉黑的DAPP。",
            httpMethod = "POST"
    )
    @RequestMapping(
            value = "/appeal-dapp",
            method = {RequestMethod.POST}
    )
    public ApiResult<EmptyVo> appealDapp(@RequestBody AppealDappDto dto) {
        return userService.appealDapp(dto);
    }

    @ApiOperation(
            value = "获取推荐的用户OSN ID列表",
            notes = "获取推荐的用户OSN ID列表。",
            httpMethod = "GET"
    )
    @RequestMapping(
            value = "/get-recommended-osn-ids",
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    public ApiResult<GetRecommendedOsnIdsVo> getRecommendedOsnIds() {
        return userService.getRecommendedOsnIds();
    }

    @RequestMapping(
            value = "/set-user-device",
            method = {RequestMethod.POST}
    )
    public ApiResult<EmptyVo> setUserDevice(@Validated @RequestBody SetUserDeviceDto dto) {
        return userService.setUserDevice(dto);
    }

}
