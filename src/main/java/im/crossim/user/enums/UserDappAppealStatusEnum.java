package im.crossim.user.enums;

public enum UserDappAppealStatusEnum {
    NOT_PROCESSED(0, "未处理"),
    PROCESSED(1, "已处理"),
    ;

    private final int value;
    private final String name;

    UserDappAppealStatusEnum(int value, String name) {
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
