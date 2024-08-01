package im.crossim.message.service;

import im.crossim.common.enums.ResultCodeEnum;
import im.crossim.common.exception.BusinessException;
import im.crossim.config.web.UserServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Slf4j
@Service
public class SmsCodeService {

    @Autowired
    private UserServiceConfig userServiceConfig;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SmsService smsService;

    /**
     * 生成短信验证码。
     *
     * @param size 短信验证码长度，应该大于0。
     * @return 短信验证码。
     */
    private String generateSmsCode(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0");
        }

        int bound = 1;
        for (int i = 0; i < size; i++) {
            bound *= 10;
        }

        String smsCode = ("" + new Random().nextInt(bound));
        smsCode = StringUtils.leftPad(smsCode, size, '0');

        return smsCode;
    }

    /**
     * 生成Redis的KEY。
     *
     * @param mobile 手机号码。
     * @param type 短信验证码类型。
     * @param smsCode 短信验证码。
     * @return Redis的KEY。
     */
    private String generateRedisKey(String mobile, int type, String smsCode) {
        return String.format(
                "USRSVC:SMS_SERVICE:SMS_CODE:%s:%s:%s",
                mobile,
                type,
                smsCode
        );
    }

    /**
     * 生成用于限制发送频率的Redis的KEY。
     *
     * @param mobile 手机号码。
     * @param type 短信验证码类型。
     * @return 用于限制发送频率的Redis的KEY。
     */
    private String generatePeriodRedisKey(String mobile, int type) {
        return String.format(
                "USRSVC:SMS_SERVICE:SMS_CODE_PERIOD:%s:%s",
                mobile,
                type
        );
    }

    /**
     * 将验证码短信发送到短信网关。
     *
     * @param mobile 手机号码。
     * @param type 验证码类型。
     * @param smsCode 验证码。
     * @return 是否成功。
     */
    private boolean sendSmsCodeToGateway(String mobile, int type, String smsCode) {
        log.info(
                "发送验证码短信到短信网关，手机号码：{}；类型：{}；短信验证码：{}",
                mobile,
                type,
                smsCode
        );

        if (userServiceConfig.getSms().getEnabled()) {
            return smsService.sendInternationalMessage(smsCode, mobile);
        } else {
            return true;
        }
    }

    /**
     * 发送短信验证码。
     *
     * @param mobile 手机号码。
     * @param type 验证码类型。
     * @return 是否成功。
     */
    public boolean sendSmsCode(String mobile, int type) {
        // 限制发送频率。
        String periodRedisKey = generatePeriodRedisKey(mobile, type);
        if (StringUtils.isNotEmpty(stringRedisTemplate.opsForValue().get(periodRedisKey))) {
            throw new BusinessException(ResultCodeEnum.FAILED_TO_SEND_VERIFICATION_CODE_PERIOD);
        }
        stringRedisTemplate.opsForValue().set(
                periodRedisKey,
                mobile,
                Duration.ofSeconds(userServiceConfig.getSms().getSmsCodePeriodSeconds())
        );

        // 生成短信验证码。
        String smsCode = generateSmsCode(userServiceConfig.getSms().getSmsCodeSize());
        log.info(
                "发送短信验证码，手机号码：{}；类型：{}；短信验证码：{}",
                mobile,
                type,
                smsCode
        );

        // 将短信验证码信息存储到Redis。
        String redisKey = generateRedisKey(mobile, type, smsCode);
        stringRedisTemplate.opsForValue().set(
                redisKey,
                smsCode,
                Duration.ofSeconds(userServiceConfig.getSms().getSmsCodeDurationSeconds())
        );

        // 将短信验证码发送到短信网关。
        if (!sendSmsCodeToGateway(mobile, type, smsCode)) {
            throw new BusinessException(ResultCodeEnum.FAILED_TO_SEND_VERIFICATION_CODE_GATEWAY);
        }

        return true;
    }

    /**
     * 验证短信验证码。
     *
     * @param mobile 手机号码。
     * @param type 验证码类型。
     * @param smsCode 短信验证码。
     * @return 是否成功。
     */
    public boolean verifySmsCode(String mobile, int type, String smsCode) {
        // 确认指定KEY是否存在于Redis中，如果是的话，则认为验证通过。
        String redisKey = generateRedisKey(mobile, type, smsCode);
        Boolean result = stringRedisTemplate.hasKey(redisKey);
        if (result != null && result) {
            // 验证通过，清除KEY。
            stringRedisTemplate.delete(redisKey);
            return true;
        } else {
            // 验证不通过。
            return false;
        }
    }

}
