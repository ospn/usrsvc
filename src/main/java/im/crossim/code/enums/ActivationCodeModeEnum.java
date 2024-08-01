package im.crossim.code.enums;

public enum ActivationCodeModeEnum {
    ALPHABET(0, "字母表模式"),
    UUID(1, "UUID模式"),
    ;

    private final int value;
    private final String name;

    ActivationCodeModeEnum(int value, String name) {
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
