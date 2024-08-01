package im.crossim.user.enums;

public enum UserStatusEnum {
    NOT_ENABLED(0, "未启用"),
    ENABLED(1, "正常"),
    DISABLED(2, "禁用"),
    ;

    private final int value;
    private final String name;

    UserStatusEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

}
