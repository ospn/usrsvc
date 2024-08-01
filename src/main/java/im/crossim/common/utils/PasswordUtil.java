package im.crossim.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class PasswordUtil {

    /**
     * 前端加密密码 = BASE64_ENCODE(SHA256(明文密码)) <br />
     * 后端加密密码 = BASE64_ENCODE(SHA256(BASE64_DECODE(前端加密密码))) <br />
     * <br />
     * 将前端密码BASE64解码后，在用SHA256加密，然后再使用BASE64编码。
     *
     * @param frontendPassword 前端加密密码
     * @return 后端加密密码
     */
    public static String encrypt(String frontendPassword) {
        if (StringUtils.isEmpty(frontendPassword)) {
            return null;
        }

        try {
            // BASE64解码前端加密密码。
            byte[] frontendPasswordBytes = Base64.decode(frontendPassword);

            // 将前端加密密码做一次SHA256。
            byte[] frontendPasswordSha256Bytes = DigestUtil.sha256(frontendPasswordBytes);

            return Base64.encode(frontendPasswordSha256Bytes);
        } catch (Exception ex) {
            log.error(
                    String.format(
                            "加密前端密码失败：%s",
                            frontendPassword
                    ),
                    ex
            );
            return null;
        }
    }

}
