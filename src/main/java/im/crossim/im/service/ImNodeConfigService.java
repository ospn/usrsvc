package im.crossim.im.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.crossim.im.entity.ImNodeConfigEntity;
import im.crossim.im.mapper.ImNodeConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ImNodeConfigService extends ServiceImpl<ImNodeConfigMapper, ImNodeConfigEntity> implements IService<ImNodeConfigEntity> {

    @Autowired
    private ImNodeConfigMapper imNodeConfigMapper;

    /**
     * 获取可用的IM节点配置。
     *
     * @return IM节点配置。
     */
    public ImNodeConfigEntity getAvailableConfig() {
        // 先尝试获取没有超过最大使用量的IM节点配置，如果完全没有，则挑个用户量最少的。
        ImNodeConfigEntity configEntity = imNodeConfigMapper.getAvailableConfig();
        if (configEntity != null) {
            // 存在没有超过最大使用量的IM节点配置。
            return configEntity;
        } else {

            return imNodeConfigMapper.getLowestVolumeConfig();
        }
    }

    /**
     * 递增IM节点配置使用量。
     *
     * @param id IM节点配置ID
     */
    public void incVolume(int id) {
        imNodeConfigMapper.incVolume(id);
    }

}
