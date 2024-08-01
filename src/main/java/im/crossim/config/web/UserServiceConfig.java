package im.crossim.config.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "user-service")
public class UserServiceConfig {

    @Data
    public static class ImConfig {

        private String groupServerUrl;

        private String groupServerPassword;

    }

    @Data
    public static class SmsConfig {

        private Boolean enabled;

        private Integer smsCodeSize;

        private Integer smsCodeDurationSeconds;

        private Integer smsCodePeriodSeconds;

    }

    @Data
    public static class LoginConfig {

        private String tempDirectory;

        private String userPortraitDirectory;

        private String groupPortraitDirectory;

        private String webRtcBase;

        private String webRtcHost;

        private String apiUploadUrl;

        private String fileUrlPrefix;

        private String mainDapp;

        private String mainPageUrl;

    }

    @Data
    public static class SensitiveKeywordConfig {

        private String fileUrl;

        private Integer version;

    }

    @Data
    public static class DappBlackListConfig {

        private String fileUrl;

        private Integer version;

    }

    @Data
    public static class CryptoConfig {

        /**
         * 0 = 不加密
         * 1 = Base64
         * 2 = AES
         */
        private Integer mode;

        /**
         * 当mode=2时，代表AES的KEY。
         */
        private String key;

    }

    @Data
    public static class AppConfig {

        private String downloadPageUrl;

        private Integer version;

        private Boolean force;

    }

    private Boolean imNodeEnabled;

    private ImConfig imConfig;

    private SmsConfig sms;

    private LoginConfig loginConfig;

    private SensitiveKeywordConfig sensitiveKeywordConfig;

    private DappBlackListConfig dappBlackListConfig;

    private CryptoConfig cryptoConfig;

    private AppConfig appConfig;

    private List<String> recommendedOsnIds;

}
