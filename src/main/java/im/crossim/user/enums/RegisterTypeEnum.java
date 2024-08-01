package im.crossim.user.enums;

public enum RegisterTypeEnum {
    USERNAME(1, "用户名注册"),
    MOBILE(2, "手机号码注册"),
    EMAIL(3, "电子邮箱注册"),

    AUTO_REGISTER(10, "自动注册"),
    ;

    private final int value;
    private final String name;

    RegisterTypeEnum(int value, String name) {
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
        for (RegisterTypeEnum registerTypeEnum : RegisterTypeEnum.values()) {
            if (value == registerTypeEnum.getValue()) {
                return true;
            }
        }
        return false;
    }
}
