package im.crossim.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.crossim.common.vo.ApiResult;
import im.crossim.common.vo.EmptyVo;
import im.crossim.user.entity.UserDeviceEntity;
import im.crossim.user.mapper.UserDeviceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class UserDeviceService extends ServiceImpl<UserDeviceMapper, UserDeviceEntity> implements IService<UserDeviceEntity> {

    private void removeUserDevice(String userOsnId) {
        LambdaQueryWrapper<UserDeviceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDeviceEntity::getUserOsnId, userOsnId);
        this.remove(queryWrapper);
    }

    public void setUserDevice(String userOsnId, Integer vendor, String deviceId) {
        removeUserDevice(userOsnId);

        UserDeviceEntity userDeviceEntity = new UserDeviceEntity();
        userDeviceEntity.setUserOsnId(userOsnId);
        userDeviceEntity.setVendor(vendor);
        userDeviceEntity.setDeviceId(deviceId);
        this.save(userDeviceEntity);
    }

    public UserDeviceEntity getByUserOsnId(String userOsnId) {
        LambdaQueryWrapper<UserDeviceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDeviceEntity::getUserOsnId, userOsnId);
        return this.getOne(queryWrapper);
    }


}
