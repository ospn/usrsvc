package im.crossim.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import im.crossim.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel("用户设备实体类")
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_user_device", autoResultMap = true)
public class UserDeviceEntity extends BaseEntity {

    @ApiModelProperty("用户的OSN ID")
    private String userOsnId;

    @ApiModelProperty("厂商，1=小米；2=Vivo；3=华为")
    private Integer vendor;

    @ApiModelProperty("设备ID")
    private String deviceId;

}
