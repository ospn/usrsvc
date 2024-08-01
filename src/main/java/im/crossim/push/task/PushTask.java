package im.crossim.push.task;

import im.crossim.push.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class PushTask {

    @Autowired
    private PushService pushService;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void handle() {
        log.info("====================【批量通知推送】通知推送任务开始====================");
        pushService.pushMessageForAllDevices();
    }

}
