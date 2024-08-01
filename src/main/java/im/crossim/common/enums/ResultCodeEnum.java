package im.crossim.common.enums;

public enum ResultCodeEnum {
    SUCCESS(0, "操作成功"),
    ERROR(500, "业务错误，请联系服务提供商"),
    SYSTEM_ERROR(100000, "系统错误，请联系服务提供商"),

    AUTHORIZATION_FAILED(403, "登录信息失效，请重新登录"),

    /* ========================================
        业务异常
     ======================================== */
    // 通用（500~599）。
    IM_NODE_FAILED(501, "连接聊天通信节点失败"),
    MAX_VOLUME_EXCEEDED(502, "已达到创建上限"),
    NO_PRIVILEGE(503, "无权限"),

    // 用户相关（600~699）。
    INVALID_DAPP_INFO(600, "错误的小程序信息"),
    USERNAME_EXISTS(601, "用户名已存在"),
    MOBILE_EXISTS(602, "手机号码已存在"),
    EMAIL_EXISTS(603, "电子邮箱地址已存在"),
    INVALID_VERIFICATION_CODE(604, "错误的验证码"),
    INVALID_PASSWORD_FORMAT(605, "错误的密码格式"),
    USER_NOT_ENABLED(606, "用户未被启用"),
    USER_DISABLED(607, "用户已被禁用"),
    FAILED_TO_LOGIN(608, "登录失败"),
    FAILED_TO_LOGIN_USERNAME(609, "登录失败，账号或密码错误"),
    FAILED_TO_LOGIN_MOBILE(610, "登录失败，手机号码或验证码"),
    FAILED_TO_LOGIN_EMAIL(611, "登录失败，电子邮箱地址或验证码"),
    INVALID_PASSWORD(612, "密码错误"),
    INVALID_REFRESH_TOKEN(613, "错误的刷新令牌"),
    FAILED_TO_SEND_VERIFICATION_CODE(614, "发送验证码失败"),
    FAILED_TO_SEND_VERIFICATION_CODE_GATEWAY(615, "发送验证码失败，短信服务商错误，请稍后再试"),
    FAILED_TO_SEND_VERIFICATION_CODE_PERIOD(616, "发送验证码失败，操作过于频繁，请稍后再试"),
    FAILED_TO_LEVEL_UP_ALREADY(617, "升级失败，已处于该VIP等级或者更高的VIP等级"),
    FAILED_TO_LEVEL_DOWN_ALREADY(618, "降级失败，已处于该VIP等级或者更低的VIP等级"),

    // 激活码（700~799）。
    INVALID_ACTIVATION_CODE(700, "错误的激活码"),
    USED_ACTIVATION_CODE(701, "激活码已被使用"),

    // 群组相关（800~899）。
    GROUP_NOT_EXISTS(800, "群组不存在"),
    NOT_GROUP_MASTER(801, "非群主"),
    GROUP_MAX_MEMBER_COUNT_ALREADY(802, "群组最大人数已升级"),

    /* ========================================
        系统异常
     ======================================== */
    // 通用（100500~100599）
    INVALID_PARAM(100500, "参数错误"),

    // 用户相关（100600~100699）。
    VIP_LEVEL_NOT_EXISTS(100600, "不存在该VIP等级"),

    // 激活码（100700~100799）。
    ACTIVATION_CODE_HANDLER_NOT_EXISTS(100700, "无该类型的激活码的处理器"),

    // 群组相关（100800~100899）。
    FAILED_TO_SET_GROUP_MAX_MEMBER_COUNT(100800, "设置群组最大成员数失败"),

    ;

    private final int code;
    private final String msg;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
