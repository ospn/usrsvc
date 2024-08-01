package im.crossim.push.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import im.crossim.user.entity.UserDeviceEntity;
import im.crossim.user.service.UserDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class PushService implements ApplicationRunner {

    @Autowired
    private UserDeviceService userDeviceService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    private static final String OSN_ID_CACHE_REDIS_KEY = "USRSVC:PUSH_SERVICE:MESSAGE_RECEIVED_NOTIFICATION:OSN_ID_CACHE";

    private static final Object LOCK = new Object();

    private final Map<Integer, VendorPushService> VENDOR_PUSH_SERVICE_MAP = new ConcurrentHashMap<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (VendorPushService vendorPushService : applicationContext.getBeansOfType(VendorPushService.class).values()) {
            VENDOR_PUSH_SERVICE_MAP.put(
                    vendorPushService.getVendor(),
                    vendorPushService
            );
        }
    }

    public void cacheMessageReceivedNotification(String osnId) {
        log.info("【批量消息推送】缓存消息接收通知类型的通知的OSN ID：{}", osnId);
        synchronized (LOCK) {
            stringRedisTemplate.opsForSet().add(
                    OSN_ID_CACHE_REDIS_KEY,
                    osnId
            );
        }
    }

    private static final int BATCH_COUNT = 1000;

    private void pushMessageForAllDevices(Map<Integer, List<UserDeviceEntity>> userDeviceEntityMap) {
        for (Map.Entry<Integer, List<UserDeviceEntity>> entry : userDeviceEntityMap.entrySet()) {
            int vendor = entry.getKey();
            List<UserDeviceEntity> userDeviceEntityList = entry.getValue();

            VendorPushService vendorPushService = VENDOR_PUSH_SERVICE_MAP.get(vendor);
            if (vendorPushService != null) {
                List<String> osnIds = new ArrayList<>();
                List<String> deviceIds = new ArrayList<>();
                for (UserDeviceEntity userDeviceEntity : userDeviceEntityList) {
                    osnIds.add(userDeviceEntity.getUserOsnId());
                    deviceIds.add(userDeviceEntity.getDeviceId());
                }

                try {
                    if (vendorPushService.pushMessage(
                            "消息通知",
                            "您收到了消息，请查看！",
                            deviceIds
                    )) {
                        log.info(
                                "【批量推送通知】推送成功，厂商类型：{}，推送的OSN ID：{}",
                                vendor,
                                JSONArray.toJSONString(osnIds)
                        );
                    } else {
                        log.error(
                                "【批量推送通知】推送失败，厂商类型：{}，未推送的OSN ID：{}",
                                vendor,
                                JSONArray.toJSONString(osnIds)
                        );
                    }
                } catch (Exception ex) {
                    log.error(
                            String.format(
                                    "【批量推送通知】推送时发生异常，厂商类型：%s，未推送的OSN ID：%s",
                                    vendor,
                                    JSONArray.toJSONString(osnIds)
                            ),
                            ex
                    );
                }
            } else {
                log.error("【批量推送通知】没有该厂商的推送服务：{}", vendor);
            }
        }
    }

    public void pushMessageForAllDevices() {
        Set<String> osnIdSet;
        synchronized (LOCK) {
            osnIdSet = stringRedisTemplate.opsForSet().members(OSN_ID_CACHE_REDIS_KEY);
            stringRedisTemplate.delete(OSN_ID_CACHE_REDIS_KEY);
        }
        if (CollectionUtil.isEmpty(osnIdSet)) {
            log.info("【批量通知推送】没有需要推送消息的OSN ID");
            return;
        }
        List<String> osnIds = new ArrayList<>(osnIdSet);
        log.info("【批量通知推送】需要推送消息的OSN ID的个数：{}", osnIds.size());

        int osnIdsSize = osnIds.size();
        int batchCount = (osnIdsSize / BATCH_COUNT) + (osnIdsSize % BATCH_COUNT != 0 ? 1 : 0);
        for (int i = 0; i < batchCount; i++) {
            int fromIndex = i * BATCH_COUNT;
            int toIndex = osnIdsSize - (i * BATCH_COUNT) > BATCH_COUNT
                    ? ((i + 1) * BATCH_COUNT)
                    : osnIdsSize;
            List<String> subOsnIds = osnIds.subList(fromIndex, toIndex);

            LambdaQueryWrapper<UserDeviceEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(UserDeviceEntity::getUserOsnId, subOsnIds);
            List<UserDeviceEntity> userDeviceEntityList = userDeviceService.list(queryWrapper);

            // Map<VENDOR, ENTITY>
            Map<Integer, List<UserDeviceEntity>> userDeviceEntityMap = new HashMap<>();
            for (UserDeviceEntity userDeviceEntity : userDeviceEntityList) {
                int vendor = userDeviceEntity.getVendor();
                if (!userDeviceEntityMap.containsKey(vendor)) {
                    userDeviceEntityMap.put(vendor, new ArrayList<>());
                }
                userDeviceEntityMap.get(vendor).add(userDeviceEntity);
            }

            pushMessageForAllDevices(userDeviceEntityMap);
        }
    }

}
