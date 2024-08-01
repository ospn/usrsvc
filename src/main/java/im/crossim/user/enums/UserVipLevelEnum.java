package im.crossim.user.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum UserVipLevelEnum {
    LEVEL_1(
            1,
            "等级1",
            20,
            200
    ),
    LEVEL_2(
            2,
            "等级2",
            20,
            1000
    ),
    ;

    private final int value;
    private final String name;

    private final int maxGroupCount;
    private final int maxGroupUserCount;

    private static final Map<Integer, UserVipLevelEnum> MAP = new ConcurrentHashMap<>();
    static {
        for (UserVipLevelEnum item : values()) {
            MAP.put(item.getValue(), item);
        }
    }

    public static UserVipLevelEnum getByValue(int value) {
        return MAP.get(value);
    }

    UserVipLevelEnum(
            int value,
            String name,
            int maxGroupCount,
            int maxGroupUserCount
    ) {
        this.value = value;
        this.name = name;

        this.maxGroupCount = maxGroupCount;
        this.maxGroupUserCount = maxGroupUserCount;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public int getMaxGroupCount() {
        return maxGroupCount;
    }

    public int getMaxGroupUserCount() {
        return maxGroupUserCount;
    }

}
