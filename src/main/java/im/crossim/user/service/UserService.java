package im.crossim.user.service;

import cn.hutool.core.codec.Base64Encoder;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ospn.command.CmdDappLogin;
import com.ospn.command.CmdReDappLogin;
import com.ospn.common.ECUtils;
import im.crossim.common.enums.ResultCodeEnum;
import im.crossim.common.exception.BusinessException;
import im.crossim.common.exception.SystemException;
import im.crossim.common.utils.PasswordUtil;
import im.crossim.common.utils.UserUtil;
import im.crossim.common.vo.ApiResult;
import im.crossim.common.vo.EmptyVo;
import im.crossim.config.web.UserServiceConfig;
import im.crossim.crypto.service.CryptoService;
import im.crossim.im.entity.ImNodeConfigEntity;
import im.crossim.im.service.ImNodeConfigService;
import im.crossim.im.service.ImService;
import im.crossim.message.enums.MessageTypeEnum;
import im.crossim.message.service.SmsCodeService;
import im.crossim.project.entity.ProjectEntity;
import im.crossim.project.pojo.Project;
import im.crossim.project.service.ProjectService;
import im.crossim.user.dto.*;
import im.crossim.user.entity.UserEntity;
import im.crossim.user.enums.*;
import im.crossim.user.mapper.UserMapper;
import im.crossim.user.pojo.RefreshTokenData;
import im.crossim.user.pojo.TokenData;
import im.crossim.user.pojo.UserVipInfo;
import im.crossim.user.vo.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class UserService extends ServiceImpl<UserMapper, UserEntity> implements IService<UserEntity> {

    @Autowired
    private UserServiceConfig userServiceConfig;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SmsCodeService smsCodeService;

    @Autowired
    private ImNodeConfigService imNodeConfigService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ImService imService;

    @Autowired
    private UserDappUseInfoService userDappUseInfoService;

    @Autowired
    private UserDappAppealService userDappAppealService;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserDeviceService userDeviceService;

    @Autowired
    private LTPServer ltpServer;

    private boolean existsUsername(String username) {
        String encryptedUsername = cryptoService.encrypt(username);

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserEntity::getUsername, encryptedUsername);
        return this.count(queryWrapper) > 0;
    }

    private boolean existsMobile(String mobile) {
        String encryptedMobile = cryptoService.encrypt(mobile);

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserEntity::getMobile, encryptedMobile);
        return this.count(queryWrapper) > 0;
    }

    private boolean existsEmail(String email) {
        String encryptedEmail = cryptoService.encrypt(email);

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserEntity::getEmail, encryptedEmail);
        return this.count(queryWrapper) > 0;
    }

    private void checkRegisterUserInfo(String username, String mobile, String email) {
        if (StringUtils.isNotEmpty(username)) {
            if (existsUsername(username)) {
                throw new BusinessException(ResultCodeEnum.USERNAME_EXISTS);
            }
        }
        if (StringUtils.isNotEmpty(mobile)) {
            if (existsMobile(mobile)) {
                throw new BusinessException(ResultCodeEnum.MOBILE_EXISTS);
            }
        }
        if (StringUtils.isNotEmpty(email)) {
            if (existsEmail(email)) {
                throw new BusinessException(ResultCodeEnum.EMAIL_EXISTS);
            }
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class RegisterParam {

        private Integer type;
        private String username;
        private String mobile;
        private String email;
        private String password;
        private String nickname;
        private String project;
        private String portrait;
        private String code;

        private String userOsnId;

    }

    private UserEntity register(RegisterParam param, boolean autoRegister) {
        Integer type = param.getType();
        String username = StringUtils.trim(param.getUsername());
        String mobile = StringUtils.trim(param.getMobile());
        String email = StringUtils.trim(param.getEmail());
        String password = StringUtils.trim(param.getPassword());
        String nickname = StringUtils.trim(param.getNickname());
        String project = param.getProject();
        String portrait = param.getPortrait();
        String code = StringUtils.trim(param.getCode());

        String userOsnId = param.getUserOsnId();

        // 校验注册类型，并且根据不同注册类型进行校验。
        String realUsername;
        if (!RegisterTypeEnum.validateValue(type)) {
            throw new SystemException("param type contains invalid value");
        }
        if (RegisterTypeEnum.USERNAME.getValue() == type) {
            // 用户名注册。

            realUsername = username;

            throw new SystemException("not supported");
        } else if (RegisterTypeEnum.MOBILE.getValue() == type) {
            // 手机号码注册。

            if (StringUtils.isEmpty(mobile)) {
                throw new SystemException("param mobile cannot be empty");
            }

            if (StringUtils.isEmpty(code)) {
                throw new SystemException("param code cannot be empty");
            }

            if (!smsCodeService.verifySmsCode(mobile, MessageTypeEnum.REGISTER.getValue(), code)) {
                throw new BusinessException(ResultCodeEnum.INVALID_VERIFICATION_CODE);
            }

            realUsername = mobile;
        } else if (RegisterTypeEnum.EMAIL.getValue() == type) {
            // 电子邮箱注册。

            realUsername = email;

            throw new SystemException("not supported");
        } else if (RegisterTypeEnum.AUTO_REGISTER.getValue() == type) {
            // 自动注册。

            if (!autoRegister) {
                throw new SystemException("not supported type: " + type);
            }

            realUsername = username;
        } else {
            throw new SystemException("not supported type: " + type);
        }

        // 验证用户名、手机号码、电子邮箱是否已存在。
        checkRegisterUserInfo(realUsername, mobile, email);

        // 获取一个可用的IM节点配置。
        ImNodeConfigEntity imNodeConfigEntity = imNodeConfigService.getAvailableConfig();
        if (imNodeConfigEntity == null) {
            throw new SystemException("no available im node config");
        }

        // 创建OSN ID，并将OSN ID注册到IM节点。
        String[] keys;
        String osnId;
        if (userServiceConfig.getImNodeEnabled()) {
            // IM节点被启用。

            if (RegisterTypeEnum.AUTO_REGISTER.getValue() == type) {
                keys = new String[] {userOsnId, "", ""};
                osnId = userOsnId;
            } else {
                // 创建OSN ID。
                keys = imService.createOsnID("user", "");
                if (keys == null) {
                    throw new SystemException("failed to generate account");
                }
                osnId = keys[0];
                if (osnId.length() < 20) {
                    throw new SystemException("failed to generate account");
                }
            }

            // 将OSN ID注册到IM节点。
            if (imService.register2im(
                    imNodeConfigEntity.getImServerIp(), imNodeConfigEntity.getImServerPort(), imNodeConfigEntity.getImServerPassword(),
                    osnId
            ) == null) {
                throw new SystemException("failed to register to im error");
            }
        } else {
            // IM节点未被启用，单纯就是为了方便本地调试不与IM节点相关功能。

            keys = new String[] {"A", "B", "C"};
            osnId = keys[0];
        }

        // 再次加密前端传过来的加密密码。
        String realPassword;
        if (RegisterTypeEnum.AUTO_REGISTER.getValue() != type) {
            realPassword = PasswordUtil.encrypt(password);
            if (StringUtils.isEmpty(realPassword)) {
                throw new BusinessException(ResultCodeEnum.INVALID_PASSWORD_FORMAT);
            }
        } else {
            // 自动注册没密码。
            realPassword = "";
        }

        // 新增用户。
        UserEntity userEntity = new UserEntity();
        userEntity.setRegisterType(type);
        userEntity.setUsername(realUsername);
        userEntity.setMobile(mobile);
        userEntity.setEmail(email);
        userEntity.setPassword(realPassword);
        userEntity.setImId(imNodeConfigEntity.getId());
        userEntity.setPrivateKey("VER2-" +  keys[1] + "-" + keys[2]);
        userEntity.setOsnId(osnId);
        userEntity.setNickname(nickname);
        userEntity.setType(UserTypeEnum.NORMAL_USER.getValue());
        userEntity.setStatus(UserStatusEnum.ENABLED.getValue());
        userEntity.setProject(project);
        userEntity.setPortrait(portrait);
        userEntity.setVipLevel(UserVipLevelEnum.LEVEL_1.getValue());

        if (!this.save(userEntity)) {
            throw new SystemException("failed to save user");
        }

        // 递增IM节点配置的使用次数。
        imNodeConfigService.incVolume(imNodeConfigEntity.getId());

        return userEntity;
    }

    public ApiResult<RegisterVo> register(RegisterDto dto) {
        String project = dto.getProject();

        RegisterParam registerParam = new RegisterParam();
        BeanUtils.copyProperties(dto, registerParam);
        UserEntity userEntity = register(registerParam, false);

        LoginVo loginVo = login(userEntity, project);

        RegisterVo vo = RegisterVo.builder()
                .loginInfo(loginVo)
                .build();

        return ApiResult.success(vo);
    }

    private UserEntity getUserByUsernameAndPassword(String username, String password) {
        String encryptedUsername = cryptoService.encrypt(username);

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserEntity::getUsername, encryptedUsername)
                .eq(UserEntity::getPassword, password);
        return this.getOne(queryWrapper);
    }

    private UserEntity getUserByMobile(String mobile) {
        String encryptedMobile = cryptoService.encrypt(mobile);

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserEntity::getMobile, encryptedMobile);
        return this.getOne(queryWrapper);
    }

    public String generateLoginTokenRedisKey(String token) {
        return String.format(
                "USRSVC:USER_SERVICE:TOKEN:%s",
                token
        );
    }

    private String generateRefreshTokenRedisKey(String refreshToken) {
        return String.format(
                "USRSVC:USER_SERVICE:REFRESH_TOKEN:%s",
                refreshToken
        );
    }

    private static final long LOGIN_TOKEN_DURATION_SECOND = 3 * 24 * 60 * 60;
    private static final long REFRESH_TOKEN_DURATION_SECOND = 12 * 30 * 24 * 60 * 60;

    private LoginVo login(UserEntity userEntity, String project) {
        return login(
                userEntity,
                project,
                true
        );
    }

    private LoginVo login(
            UserEntity userEntity,
            String project,
            boolean needUserInfo
    ) {
        // 用户状态检查。
        int userStatus = userEntity.getStatus();
        if (userStatus == UserStatusEnum.NOT_ENABLED.getValue()) {
            // 用户未启用。
            throw new BusinessException(ResultCodeEnum.USER_NOT_ENABLED);
        }
        if (userStatus == UserStatusEnum.DISABLED.getValue()) {
            // 用户被禁用。
            throw new BusinessException(ResultCodeEnum.USER_DISABLED);
        }

        // 生成登录Token，并存储到Redis。
        String token = UUID.randomUUID().toString().replace("-", "");
        TokenData tokenData = TokenData.builder()
                .userId(userEntity.getId())
                .build();
        String tokenRedisKey = generateLoginTokenRedisKey(token);
        stringRedisTemplate.opsForValue().set(
                tokenRedisKey,
                JSONObject.toJSONString(tokenData),
                Duration.ofSeconds(LOGIN_TOKEN_DURATION_SECOND)
        );

        // 生成刷新Token，并存储到Redis。
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        RefreshTokenData refreshTokenData = RefreshTokenData.builder()
                .token(token)
                .tokenData(tokenData)
                .build();
        String refreshTokenRedisKey = generateRefreshTokenRedisKey(refreshToken);
        stringRedisTemplate.opsForValue().set(
                refreshTokenRedisKey,
                JSONObject.toJSONString(refreshTokenData),
                Duration.ofSeconds(REFRESH_TOKEN_DURATION_SECOND)
        );

        UserVo userVo = null;
        if (needUserInfo) {
            userVo = new UserVo();
            BeanUtils.copyProperties(userEntity, userVo);
        }

        LoginConfigVo loginConfigVo = getLoginConfig(userEntity, project);

        SensitiveKeywordConfigVo sensitiveKeywordConfigVo = getSensitiveKeywordConfig();

        DappBlackListConfigVo dappBlackListConfigVo = getDappBlackListConfig();

        return LoginVo.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userInfo(userVo)
                .loginConfig(loginConfigVo)
                .sensitiveKeywordConfig(sensitiveKeywordConfigVo)
                .dappBlackListConfig(dappBlackListConfigVo)
                .build();
    }

    public ApiResult<LoginVo> login(LoginDto dto) {
        Integer type = dto.getType();
        String username = dto.getUsername();
        String mobile = dto.getMobile();
        String email = dto.getEmail();
        String password = dto.getPassword();
        String code = dto.getCode();
        String project = dto.getProject();

        UserEntity userEntity;

        if (LoginTypeEnum.PASSWORD.getValue() == type) {
            // 密码登录。

            // 再次加密前端传过来的加密密码。
            String realPassword = PasswordUtil.encrypt(password);
            if (StringUtils.isEmpty(realPassword)) {
                throw new BusinessException(ResultCodeEnum.INVALID_PASSWORD_FORMAT);
            }

            userEntity = getUserByUsernameAndPassword(
                    username,
                    realPassword
            );

        } else if (LoginTypeEnum.MOBILE.getValue() == type) {
            // 手机登录。

            if (smsCodeService.verifySmsCode(mobile, LoginTypeEnum.MOBILE.getValue(), code)) {
                userEntity = getUserByMobile(mobile);
            } else {
                userEntity = null;
            }

        } else if (LoginTypeEnum.EMAIL.getValue() == type) {
            // 邮箱登录。

            userEntity = null;
        } else {
            userEntity = null;
        }

        if (userEntity == null) {
            if (LoginTypeEnum.PASSWORD.getValue() == type) {
                throw new BusinessException(ResultCodeEnum.FAILED_TO_LOGIN_USERNAME);
            } else if (LoginTypeEnum.MOBILE.getValue() == type) {
                throw new BusinessException(ResultCodeEnum.FAILED_TO_LOGIN_MOBILE);
            } else if (LoginTypeEnum.EMAIL.getValue() == type) {
                throw new BusinessException(ResultCodeEnum.FAILED_TO_LOGIN_EMAIL);
            } else {
                throw new BusinessException(ResultCodeEnum.FAILED_TO_LOGIN);
            }
        }

        LoginVo vo = login(userEntity, project);

        return ApiResult.success(vo);
    }

    public String getDappInfo() {
        return ltpServer.getDappInfoNoParam();
    }

    private String generateDappLoginSessionRedisKey(String osnId) {
        return String.format(
                "USRSVC:USER_SERVICE:DAPP_LOGIN:SESSION:%s",
                osnId
        );
    }

    private static final long DAPP_LOGIN_SESSION_DURATION_SECOND = 2 * 60;

    private void setDappLoginSession(String osnId, String session) {
        String redisKey = generateDappLoginSessionRedisKey(osnId);

        stringRedisTemplate.opsForValue().set(
                redisKey,
                session,
                Duration.ofSeconds(DAPP_LOGIN_SESSION_DURATION_SECOND)
        );
    }

    private String getDappLoginSession(String osnId) {
        String redisKey = generateDappLoginSessionRedisKey(osnId);
        
        return stringRedisTemplate.opsForValue().get(redisKey);
    }

    private void removeDappLoginSession(String osnId) {
        String redisKey = generateDappLoginSessionRedisKey(osnId);

        stringRedisTemplate.delete(redisKey);
    }

    private UserEntity getUserByOsnId(String osnId) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getOsnId, osnId);
        return this.getOne(queryWrapper);
    }

    public JSONObject dappLogin(DappRequestDto dto) {
        JSONObject ret = null;
        if ("GetServerInfo".equalsIgnoreCase(dto.command)) {
            ret = LTPServer.getLtpData().getServerInfo(dto.getCmdGetServerInfo());
            String session = ret.getString("session");

            setDappLoginSession(dto.user, session);
        } else if ("Login".equalsIgnoreCase(dto.command)) {
            String username = dto.getUsername();
            String project = username;

            String session = getDappLoginSession(dto.user);
            removeDappLoginSession(dto.user);

            // 确认project是否存在。
            ProjectEntity projectEntity = projectService.getByProject(project);
            if (projectEntity == null) {
                // project不存在，虚构一个用户返回回去。
                // 只需要虚构token和refreshToken。
                String fakeToken = UUID.randomUUID().toString().replace("-", "");
                String fakeRefreshToken = UUID.randomUUID().toString().replace("-", "");

                // 构造虚构的登录成功响应。
                ApiResult<LoginVo> fakeResult = ApiResult.success(
                        LoginVo.builder()
                                .token(fakeToken)
                                .refreshToken(fakeRefreshToken)
                                .build()
                );

                // 直接返回虚构的成功。
                CmdReDappLogin fakeCmdRe = new CmdReDappLogin();
                fakeCmdRe.setError("0:success");
                fakeCmdRe.addSessionKey(
                        dto.user,
                        JSONObject.toJSONString(fakeResult).getBytes(StandardCharsets.UTF_8)
                );
                return fakeCmdRe.toJson();
            }

            CmdDappLogin cmdDappLogin = dto.getCmdDappLogin();
            cmdDappLogin.session = session;
            CmdReDappLogin cmdRe = LTPServer.getLtpData().login(cmdDappLogin);

            if (cmdRe.errCode.equalsIgnoreCase("0:success")) {
                UserEntity userEntity = getUserByOsnId(dto.user);

                if (userEntity == null) {
                    RegisterParam registerParam = RegisterParam.builder()
                            .type(RegisterTypeEnum.AUTO_REGISTER.getValue())
                            .username(dto.user)
                            .nickname("User")
                            .project(project)
                            .portrait("")
                            .userOsnId(dto.user)
                            .build();

                    userEntity = register(registerParam, true);
                }

                ApiResult<LoginVo> result;
                try {
                    LoginVo loginVo = login(userEntity, project, false);
                    result = ApiResult.success(loginVo);
                } catch (Exception ex) {
                    log.error("DAPP LOGIN失败", ex);
                    result = ApiResult.error();
                }

                cmdRe.addSessionKey(
                        dto.user,
                        JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8)
                );
                ret =  cmdRe.toJson();
            } else {
                ret = cmdRe.toJson();
            }
        }
        return ret;
    }

    private static final int LOGIN_WITH_DOUBLE_KEY_SESSION_SECOND = 2 * 60;

    private String generateLoginWithDoubleKeySessionRedisKey(String userOsnId) {
        return String.format(
                "USRSVC:USER_SERVICE:LOGIN_WITH_DOUBLE_KEY:SESSION:%s",
                userOsnId
        );
    }

    public ApiResult<GetLoginInfoVo> getLoginInfo(GetLoginInfoDto dto) {
        String userOsnId = dto.getUserOsnId();
        String random = dto.getRandom();

        String challenge = UUID.randomUUID().toString().replace("-", "");

        String session = random + ":" + challenge;
        String calcData = session + ":" + userOsnId;
        String calcHash = ECUtils.osnHash(calcData.getBytes(StandardCharsets.UTF_8));
        String calcSign = ECUtils.osnSign(
                LTPServer.getLtpData().apps.osnKey,
                calcHash.getBytes(StandardCharsets.UTF_8)
        );

        String loginWithDoubleKeyRedisKey = generateLoginWithDoubleKeySessionRedisKey(userOsnId);
        stringRedisTemplate.opsForValue().set(
                loginWithDoubleKeyRedisKey,
                session,
                Duration.ofSeconds(LOGIN_WITH_DOUBLE_KEY_SESSION_SECOND)
        );

        return ApiResult.success(
                GetLoginInfoVo.builder()
                        .serviceOsnId(LTPServer.getLtpData().apps.osnID)
                        .random(random)
                        .challenge(challenge)
                        .sign(calcSign)
                        .build()
        );


        // 生成一个随机数 字符串 challenge


        // 在redis里保存2分钟
        // 格式
        /**
         * key osnid
         * value: random +:+ challenge
         * */

        //返回值
        /**
         * serverId
         * random
         * challenge
         * sign: random +:+ challenge;
         * */

    }

    private boolean checkOsnIdAndShadowId(String osnId, String shadowId) {

        // OSNID 转pub 字节流
        byte[] pub = ECUtils.toPublicKey(shadowId);
        if (pub == null) {
            //log.info("[checkV2Right] ECUtils.toPublicKey(pubKey) error.");
            return false;

        }
        // 转了字节流以后转rip160
        //log.info("[checkV2Right] befor ripemd160 pub : " + toHexString(pub));
        byte[] shadow = ECUtils.genRipeMD160(pub);
        if (shadow == null) {
            //log.info("[checkV2Right] ripemd160 null.");
            return false;
        }
        //log.info("[checkV2Right] end ripemd160 data : " + toHexString(shadow));
        //log.info("[checkV2Right] ripemd160 pubkey:" + Base64.getEncoder().encodeToString(shadow));
        //log.info("[checkV2Right] ripemd160 pub:" + str1);
        //
        byte[] groupShadow = ECUtils.getRipeMD160(osnId);
        if (groupShadow == null) {
            //log.info("[checkV2Right] ECUtils.getRipeMD160 null.");
            return false;
        }
        // 比较  shadow 和 group shadow
        String str1 = Base64Encoder.encode(shadow);
        String str2 = Base64Encoder.encode(groupShadow);
        //log.info("[checkV2Right] ripemd160 1:" + str1);
        //log.info("[checkV2Right] ripemd160 2:" + str2);
        return str1.equalsIgnoreCase(str2);
    }

    public ApiResult<LoginVo> loginWithDoubleKey(LoginWithDoubleKeyDto dto) {
        String username = dto.getUsername();
        String userOsnId = dto.getUserOsnId();
        String shadowId = dto.getShadowId();
        String timestamp = dto.getTimestamp();
        String signOsnId = dto.getSignOsnId();
        String signShadowId = dto.getSignShadowId();

        // 从redis里面取数据，取不到返回超时
        String loginWithDoubleKeyRedisKey = generateLoginWithDoubleKeySessionRedisKey(userOsnId);
        String session = stringRedisTemplate.opsForValue().get(loginWithDoubleKeyRedisKey);
        if (StringUtils.isEmpty(session)) {
            return ApiResult.error("SESSION不存在");
        }

        // 验证osnId和shadowId的关联性
        if (!checkOsnIdAndShadowId(userOsnId, shadowId)) {
            return ApiResult.error("userOsnId与shadowId无关联");
        }

        // 生成hash      random +:+ challenge+ ":" +timestamp
        String calcData = session + ":" + timestamp + ":" + LTPServer.getLtpData().apps.osnID;
        String calcHash = ECUtils.osnHash(calcData.getBytes(StandardCharsets.UTF_8));

        // 用osnId验证sign1
        if (!ECUtils.osnVerify(userOsnId, calcHash.getBytes(StandardCharsets.UTF_8), signOsnId)) {
            return ApiResult.error("使用userOsnId验证signOsnId失败");
        }

        // 用shadowId验证sign2
        if (!ECUtils.osnVerify(shadowId, calcHash.getBytes(StandardCharsets.UTF_8), signShadowId)) {
            return ApiResult.error("使用shadowId验证signShadowId失败");
        }

        UserEntity userEntity = getUserByOsnId(userOsnId);

        if (userEntity == null) {
            RegisterParam registerParam = RegisterParam.builder()
                    .type(RegisterTypeEnum.AUTO_REGISTER.getValue())
                    .username(userOsnId)
                    .nickname("User")
                    .project(username)
                    .portrait("")
                    .userOsnId(userOsnId)
                    .build();

            userEntity = register(registerParam, true);
        }

        ApiResult<LoginVo> result;
        try {
            LoginVo loginVo = login(userEntity, username, false);
            result = ApiResult.success(loginVo);
        } catch (Exception ex) {
            log.error("DAPP LOGIN失败", ex);
            result = ApiResult.error();
        }

        return result;
    }




    private boolean updateUserPasswordByUserId(int userId, String newPassword) {
        UpdateWrapper<UserEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(UserEntity::getPassword, newPassword)
                .eq(UserEntity::getId, userId);
        return this.update(updateWrapper);
    }

    private boolean updateUserPasswordByMobile(String mobile, String newPassword) {
        String encryptedMobile = cryptoService.encrypt(mobile);

        UpdateWrapper<UserEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(UserEntity::getPassword, newPassword)
                .eq(UserEntity::getMobile, encryptedMobile);
        return this.update(updateWrapper);
    }

    public ApiResult<EmptyVo> changePassword(ChangePasswordDto dto) {
        String currentPassword = StringUtils.trim(dto.getCurrentPassword());
        String newPassword = StringUtils.trim(dto.getNewPassword());

        // 再次加密前端传过来的加密密码。
        String realCurrentPassword = PasswordUtil.encrypt(currentPassword);
        if (StringUtils.isEmpty(realCurrentPassword)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PASSWORD_FORMAT);
        }

        UserEntity currentUser = UserUtil.getCurrentUser();
        if (!StringUtils.equals(currentUser.getPassword(), realCurrentPassword)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PASSWORD);
        }

        // 再次加密前端传过来的加密密码。
        String realNewPassword = PasswordUtil.encrypt(newPassword);
        if (StringUtils.isEmpty(realNewPassword)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PASSWORD_FORMAT);
        }

        if (!updateUserPasswordByUserId(currentUser.getId(), realNewPassword)) {
            throw new SystemException("failed to update user password");
        }

        return ApiResult.success();
    }

    private LoginConfigVo getLoginConfig(UserEntity user, String project) {
        if (user == null) {
            throw new SystemException("no user");
        }

        Integer imId = user.getImId();
        if (imId == null) {
            throw new SystemException("user does not have im id");
        }

        ImNodeConfigEntity imNodeConfigEntity = imNodeConfigService.getById(imId);
        if (imNodeConfigEntity == null) {
            throw new SystemException("im node config does not exists");
        }

        Project p = projectService.get(project);

        return LoginConfigVo.builder()
                .tempDirectory(userServiceConfig.getLoginConfig().getTempDirectory())
                .userPortraitDirectory(userServiceConfig.getLoginConfig().getUserPortraitDirectory())
                .groupPortraitDirectory(userServiceConfig.getLoginConfig().getGroupPortraitDirectory())
                .hostIp(imNodeConfigEntity.getImServerIp())
                .userId(user.getOsnId())
                .userVipLevel(user.getVipLevel())
                .userServiceId(LTPServer.getLtpData().apps.osnID)
                .apiUploadUrl(userServiceConfig.getLoginConfig().getApiUploadUrl())
                .fileUrlPrefix(userServiceConfig.getLoginConfig().getFileUrlPrefix())
                .mainDapp(p.getMainDapp())
                .mainPageUrl(userServiceConfig.getLoginConfig().getMainPageUrl())
                .build();
    }

    public SensitiveKeywordConfigVo getSensitiveKeywordConfig() {
        return SensitiveKeywordConfigVo.builder()
                .fileUrl(userServiceConfig.getSensitiveKeywordConfig().getFileUrl())
                .version(userServiceConfig.getSensitiveKeywordConfig().getVersion())
                .build();
    }

    public DappBlackListConfigVo getDappBlackListConfig() {
        return DappBlackListConfigVo.builder()
                .fileUrl(userServiceConfig.getDappBlackListConfig().getFileUrl())
                .version(userServiceConfig.getDappBlackListConfig().getVersion())
                .build();
    }

    public ApiResult<FetchConfigVo> fetchConfig(String project) {
        UserEntity currentUser = UserUtil.getCurrentUser();

        LoginConfigVo loginConfigVo = getLoginConfig(currentUser, project);

        SensitiveKeywordConfigVo sensitiveKeywordConfigVo = getSensitiveKeywordConfig();

        DappBlackListConfigVo dappBlackListConfigVo = getDappBlackListConfig();

        return ApiResult.success(
                FetchConfigVo.builder()
                        .loginConfig(loginConfigVo)
                        .sensitiveKeywordConfig(sensitiveKeywordConfigVo)
                        .dappBlackListConfig(dappBlackListConfigVo)
                        .build()
        );
    }

    public ApiResult<EmptyVo> changePasswordByCode(ChangePasswordByCodeDto dto) {
        String target = StringUtils.trim(dto.getTarget());
        String newPassword = StringUtils.trim(dto.getNewPassword());
        String code = StringUtils.trim(dto.getCode());

        if (!smsCodeService.verifySmsCode(target, MessageTypeEnum.CHANGE_PASSWORD_BY_CODE.getValue(), code)) {
            throw new BusinessException(ResultCodeEnum.INVALID_VERIFICATION_CODE);
        }

        // 再次加密前端传过来的加密密码。
        String realNewPassword = PasswordUtil.encrypt(newPassword);
        if (StringUtils.isEmpty(realNewPassword)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PASSWORD_FORMAT);
        }

        updateUserPasswordByMobile(target, realNewPassword);

        return ApiResult.success();
    }

    public ApiResult<RefreshTokenVo> refreshToken(RefreshTokenDto dto) {
        String refreshToken = StringUtils.trim(dto.getRefreshToken());

        String refreshTokenRedisKey = generateRefreshTokenRedisKey(refreshToken);
        String refreshTokenDataJson = stringRedisTemplate.opsForValue().get(refreshTokenRedisKey);
        if (StringUtils.isEmpty(refreshTokenDataJson)) {
            throw new BusinessException(ResultCodeEnum.INVALID_REFRESH_TOKEN);
        }

        RefreshTokenData refreshTokenData = null;
        try {
            refreshTokenData = JSONObject.parseObject(refreshTokenDataJson, RefreshTokenData.class);
        } catch (Exception ex) {
            log.error("解析TokenData的JSON时发生错误：" + refreshTokenDataJson, ex);
        }
        if (refreshTokenData == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_REFRESH_TOKEN);
        }

        String token = refreshTokenData.getToken();
        TokenData tokenData = refreshTokenData.getTokenData();

        // 让原来的Token失效。
        String oldTokenRedisKey = generateLoginTokenRedisKey(token);
        stringRedisTemplate.delete(oldTokenRedisKey);

        // 让原来的刷新Token失效。
        String oldRefreshTokenRedisKey = generateRefreshTokenRedisKey(refreshToken);
        stringRedisTemplate.delete(oldRefreshTokenRedisKey);

        // 生成登录Token，并存储到Redis。
        String newToken = UUID.randomUUID().toString().replace("-", "");
        String newTokenRedisKey = generateLoginTokenRedisKey(newToken);
        stringRedisTemplate.opsForValue().set(
                newTokenRedisKey,
                JSONObject.toJSONString(tokenData),
                Duration.ofSeconds(LOGIN_TOKEN_DURATION_SECOND)
        );

        // 生成刷新Token，并存储到Redis。
        String newRefreshToken = UUID.randomUUID().toString().replace("-", "");
        RefreshTokenData newRefreshTokenData = RefreshTokenData.builder()
                .token(newToken)
                .tokenData(tokenData)
                .build();
        String newRefreshTokenRedisKey = generateRefreshTokenRedisKey(newRefreshToken);
        stringRedisTemplate.opsForValue().set(
                newRefreshTokenRedisKey,
                JSONObject.toJSONString(newRefreshTokenData),
                Duration.ofSeconds(REFRESH_TOKEN_DURATION_SECOND)
        );

        return ApiResult.success(
                RefreshTokenVo.builder()
                        .token(newToken)
                        .refreshToken(newRefreshToken)
                        .build()
        );
    }

    /**
     * 发送验证码。
     *
     * @param dto 请求参数。
     * @return 响应参数。
     */
    public ApiResult<EmptyVo> sendCode(SendCodeDto dto) {
        String target = StringUtils.trim(dto.getTarget());
        Integer type = dto.getType();

        if (!MessageTypeEnum.validateValue(type)) {
            throw new SystemException("param type contains invalid value");
        }

        // 发送验证码。
        if (smsCodeService.sendSmsCode(target, type)) {
            return ApiResult.success();
        } else {
            return ApiResult.create(ResultCodeEnum.FAILED_TO_SEND_VERIFICATION_CODE);
        }
    }

    /**
     * 删除自身账户。
     *
     * @return 响应参数。
     */
    public ApiResult<EmptyVo> deleteSelf() {
        UserEntity currentUser = UserUtil.getCurrentUser();

        this.removeById(currentUser.getId());

        return ApiResult.success();
    }

    /**
     * 激励用户使用的DAPP信息。
     *
     * @param dto 请求体。
     * @return 响应参数。
     */
    public ApiResult<EmptyVo> logDappUseInfo(LogDappUseInfoDto dto) {
        UserEntity currentUser = UserUtil.getCurrentUser();
        String dappInfoStr = dto.getDappInfoStr();

        userDappUseInfoService.log(currentUser, dappInfoStr);

        return ApiResult.success();
    }

    /**
     * 检测用户是否存在。
     *
     * @param osnId 用户OSN ID。
     * @return 响应参数。
     */
    public ApiResult<HasUserVo> hasUser(String osnId) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getOsnId, osnId);

        boolean has = this.count(queryWrapper) > 0;

        return ApiResult.success(
                HasUserVo.builder()
                        .has(has)
                        .build()
        );
    }

    /**
     * 申诉DAPP。
     *
     * @param dto 请求体。
     * @return 响应体。
     */
    public ApiResult<EmptyVo> appealDapp(AppealDappDto dto) {
        UserEntity currentUser = UserUtil.getCurrentUser();
        String dappInfoStr = dto.getDappInfoStr();

        userDappAppealService.appeal(currentUser, dappInfoStr);

        return ApiResult.success();
    }

    /**
     * 升级VIP等级。
     *
     * @param user 用户实例。
     * @param level 目标等级。
     */
    public void levelUp(UserEntity user, int level) {
        // 检测用户是否已处于该VIP等级或者更高的VIP等级。
        if (user.getVipLevel() >= level) {
            throw new BusinessException(ResultCodeEnum.FAILED_TO_LEVEL_UP_ALREADY);
        }

        // 升级。
        LambdaUpdateWrapper<UserEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserEntity::getVipLevel, level)
                .eq(UserEntity::getId, user.getId());
        this.update(updateWrapper);
    }

    /**
     * 降级VIP等级。
     *
     * @param user 目标实例。
     * @param level 目标等级。
     */
    public void levelDown(UserEntity user, int level) {
        // 检测用户是否已处于该VIP等级或者更低的VIP等级。
        if (user.getVipLevel() <= level) {
            throw new BusinessException(ResultCodeEnum.FAILED_TO_LEVEL_DOWN_ALREADY);
        }

        // 降级。
        LambdaUpdateWrapper<UserEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserEntity::getVipLevel, level)
                .eq(UserEntity::getId, user.getId());
        this.update(updateWrapper);
    }

    /**
     * 获取VIP信息。
     *
     * @param level VIP等级。
     * @return VIP信息。
     */
    public UserVipInfo getUserVipInfo(int level) {
        UserVipLevelEnum userVipLevelEnum = UserVipLevelEnum.getByValue(level);
        if (userVipLevelEnum == null) {
            throw new SystemException(ResultCodeEnum.VIP_LEVEL_NOT_EXISTS);
        }

        return UserVipInfo.builder()
                .maxGroupCount(userVipLevelEnum.getMaxGroupCount())
                .maxGroupUserCount(userVipLevelEnum.getMaxGroupUserCount())
                .build();
    }

    /**
     * 获取VIP信息。
     *
     * @param user 用户实例。
     * @return VIP信息。
     */
    public UserVipInfo getUserVipInfo(UserEntity user) {
        return getUserVipInfo(user.getVipLevel());
    }

    /**
     * 获取推荐OSN ID列表。
     *
     * @return 响应体。
     */
    public ApiResult<GetRecommendedOsnIdsVo> getRecommendedOsnIds() {
        return ApiResult.success(
                GetRecommendedOsnIdsVo.builder()
                        .recommendedOsnIds(
                                userServiceConfig.getRecommendedOsnIds()
                        )
                        .build()
        );
    }

    public ApiResult<EmptyVo> setUserDevice(SetUserDeviceDto dto) {
        UserEntity currentUser = UserUtil.getCurrentUser();

        userDeviceService.setUserDevice(
                currentUser.getOsnId(),
                dto.getVendor(),
                dto.getDeviceId()
        );

        return ApiResult.success();
    }

}
