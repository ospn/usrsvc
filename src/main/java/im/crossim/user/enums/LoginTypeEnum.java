package im.crossim.user.enums;

import im.crossim.message.enums.MessageTypeEnum;

public enum LoginTypeEnum {
    PASSWORD(1, "密码登录"),
    MOBILE(2, "手机登录"),
    EMAIL(3, "邮箱登录"),
    ;

    private final int value;
    private final String name;

    LoginTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static boolean validateValue(int value) {
        for (MessageTypeEnum messageTypeEnum : MessageTypeEnum.values()) {
            if (value == messageTypeEnum.getValue()) {
                return true;
            }
        }
        return false;
    }

}
