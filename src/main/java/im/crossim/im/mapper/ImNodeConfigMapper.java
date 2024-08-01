package im.crossim.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import im.crossim.im.entity.ImNodeConfigEntity;
import org.apache.ibatis.annotations.Param;

public interface ImNodeConfigMapper extends BaseMapper<ImNodeConfigEntity> {

    ImNodeConfigEntity getAvailableConfig();

    ImNodeConfigEntity getLowestVolumeConfig();

    void incVolume(@Param("id") int id);

}
