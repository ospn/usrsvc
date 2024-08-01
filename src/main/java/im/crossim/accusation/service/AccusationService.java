package im.crossim.accusation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.crossim.accusation.dto.AccuseDto;
import im.crossim.accusation.entity.AccusationEntity;
import im.crossim.accusation.mapper.AccusationMapper;
import im.crossim.common.utils.UserUtil;
import im.crossim.common.vo.ApiResult;
import im.crossim.common.vo.EmptyVo;
import im.crossim.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class AccusationService extends ServiceImpl<AccusationMapper, AccusationEntity> implements IService<AccusationEntity> {

    @Autowired
    private AccusationMapper accusationMapper;

    public ApiResult<EmptyVo> accuse(AccuseDto dto) {
        UserEntity currentUser = UserUtil.getCurrentUser();

        AccusationEntity accusationEntity = new AccusationEntity();
        accusationEntity.setSourceOsnId(currentUser.getOsnId());
        accusationEntity.setTargetOsnId(dto.getTargetOsnId());
        accusationEntity.setContent(dto.getContent());
        this.save(accusationEntity);

        return ApiResult.success();
    }

}
