package im.crossim.crypto.service;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import im.crossim.common.exception.SystemException;
import im.crossim.config.web.UserServiceConfig;
import im.crossim.crypto.enums.CryptoModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class CryptoService {

    @Autowired
    private UserServiceConfig userServiceConfig;

    /**
     * 生成用于MySQL的AES加密函数的秘钥。
     *
     * @param key 秘钥。
     * @param charset 秘钥字符串编码。
     * @return 秘钥。
     */
    private static SecretKeySpec generateMySQLAESKey(final String key, final Charset charset) {
        final byte[] finalKey = new byte[16];
        int i = 0;
        for (byte b : key.getBytes(charset)) {
            finalKey[i++ % 16] ^= b;
        }
        return new SecretKeySpec(finalKey, "AES");
    }

    /**
     * AES加密。
     *
     * @param plaintext 明文。
     * @param secretKeySpec 秘钥。
     * @return 密文（BASE64编码）。
     * @throws Exception 加密异常
     */
    private static String encryptAES(String plaintext, SecretKeySpec secretKeySpec) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
        return Base64Encoder.encode(encryptedBytes);
    }

    /**
     * AES解密。
     *
     * @param ciphertext 密文（BASE64编码）。
     * @param secretKeySpec 秘钥。
     * @return 明文。
     * @throws Exception 解密异常
     */
    private static String decryptAES(String ciphertext, SecretKeySpec secretKeySpec) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decryptedBytes = cipher.doFinal(Base64Decoder.decode(ciphertext));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 加密明文。
     *
     * @param content 明文。
     * @return 密文。
     */
    public String encrypt(String content) {
        if (content == null) {
            return null;
        }

        Integer mode = userServiceConfig.getCryptoConfig().getMode();
        String key = userServiceConfig.getCryptoConfig().getKey();

        if (mode == null || mode == CryptoModeEnum.NONE.getValue()) {
            // 不加密。
            return content;
        } else if (mode == CryptoModeEnum.BASE64.getValue()) {
            // Base64编码。
            return Base64Encoder.encode(content, StandardCharsets.UTF_8);
        } else if (mode == CryptoModeEnum.AES.getValue()) {
            // AES加密。
            if (key == null) {
                key = "";
            }
            SecretKeySpec secretKeySpec = generateMySQLAESKey(key, StandardCharsets.UTF_8);

            try {
                return encryptAES(content, secretKeySpec);
            } catch (Exception ex) {
                throw new SystemException(ex);
            }
        } else {
            throw new SystemException("错误的加密模式：" + mode);
        }
    }

    /**
     * 解密。
     *
     * @param content 密文。
     * @return 明文。
     */
    public String decrypt(String content) {
        if (content == null) {
            return null;
        }

        Integer mode = userServiceConfig.getCryptoConfig().getMode();
        String key = userServiceConfig.getCryptoConfig().getKey();

        if (mode == null || mode == CryptoModeEnum.NONE.getValue()) {
            // 不加密。
            return content;
        } else if (mode == CryptoModeEnum.BASE64.getValue()) {
            // Base64编码。
            return Base64Decoder.decodeStr(content, StandardCharsets.UTF_8);
        } else if (mode == CryptoModeEnum.AES.getValue()) {
            // AES加密。
            if (key == null) {
                key = "";
            }
            SecretKeySpec secretKeySpec = generateMySQLAESKey(key, StandardCharsets.UTF_8);

            try {
                return decryptAES(content, secretKeySpec);
            } catch (Exception ex) {
                throw new SystemException(ex);
            }
        } else {
            throw new SystemException("错误的加密模式：" + mode);
        }
    }

}
