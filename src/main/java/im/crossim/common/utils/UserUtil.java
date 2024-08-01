package im.crossim.common.utils;

import im.crossim.user.entity.UserEntity;

public class UserUtil {

    private static final ThreadLocal<UserEntity> THREAD_LOCAL = new ThreadLocal<>();

    public static void setCurrentUser(UserEntity userEntity) {
        THREAD_LOCAL.set(userEntity);
    }

    public static UserEntity getCurrentUser() {
        return THREAD_LOCAL.get();
    }

    public static void clearCurrentUser() {
        THREAD_LOCAL.remove();
    }

}
