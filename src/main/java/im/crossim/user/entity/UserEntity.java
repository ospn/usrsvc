package im.crossim.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import im.crossim.common.entity.BaseEntity;
import im.crossim.config.mybatis.handler.EncryptTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel("用户实体类")
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_user", autoResultMap = true)
public class UserEntity extends BaseEntity {

    @ApiModelProperty("注册类型")
    private Integer registerType;

    @ApiModelProperty("用户名")
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String username;

    @ApiModelProperty("手机号码")
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String mobile;

    @ApiModelProperty("电子邮箱")
    @TableField(typeHandler = EncryptTypeHandler.class)
    private String email;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("im id")
    private Integer imId;

    @ApiModelProperty("私钥")
    private String privateKey;

    @ApiModelProperty("OSN ID")
    private String osnId;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("1：管理员；2：代理商；3：普通用户")
    private Integer type;

    @ApiModelProperty("0：未启用；1：正常；2：禁用")
    private Integer status;

    @ApiModelProperty("项目")
    private String project;

    @ApiModelProperty("头像URL")
    private String portrait;

    @ApiModelProperty("VIP等级")
    private Integer vipLevel;

}
