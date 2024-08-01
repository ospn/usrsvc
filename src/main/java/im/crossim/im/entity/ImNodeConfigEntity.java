package im.crossim.im.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import im.crossim.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel("IM节点配置实体类")
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_im_node_config", autoResultMap = true)
public class ImNodeConfigEntity extends BaseEntity {

    @ApiModelProperty("聊天服务器主机IP地址")
    private String imServerIp;

    @ApiModelProperty("聊天服务器主机端口")
    private Integer imServerPort;

    @ApiModelProperty("聊天服务器密码")
    private String imServerPassword;

    @ApiModelProperty("当前用户量")
    private Integer volume;

    @ApiModelProperty("最大用户量")
    private Integer maxVolume;

    @ApiModelProperty("是否启用")
    private Boolean enabled;

    @ApiModelProperty("排序号，越小越靠前")
    private Integer orderNo;

}
