package im.crossim.code.enums;

public enum ActivationCodeTypeEnum {
    LEVEL_UP_TO_2(1000, "升级VIP等级2"),

    LEVEL_DOWN_TO_1(1100, "降级VIP等级1"),

    GROUP_MAX_MEMBER_COUNT_UP(2000, "群最大人数提升"),

    ;

    private final int value;
    private final String name;

    ActivationCodeTypeEnum(int value, String name) {
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
