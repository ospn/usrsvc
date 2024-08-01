package im.crossim.message.enums;

public enum MessageTypeEnum{
    REGISTER(1, "注册"),
    LOGIN(2, "登录"),
    CHANGE_PASSWORD_BY_CODE(3, "找回密码"),
    ;

    private final int value;
    private final String name;

    MessageTypeEnum(int value, String name) {
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
