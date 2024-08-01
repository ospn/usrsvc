package im.crossim.user.enums;

public enum UserTypeEnum {
    NORMAL_USER(3, "普通用户"),
    ;

    private final int value;
    private final String name;

    UserTypeEnum(int value, String name) {
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
