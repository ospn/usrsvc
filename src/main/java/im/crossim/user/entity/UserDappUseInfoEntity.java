package im.crossim.user.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import im.crossim.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel("用户DAPP使用信息实体类")
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_user_dapp_use_info", autoResultMap = true)
public class UserDappUseInfoEntity extends BaseEntity {

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("DAPP INFO")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject dappInfo;

    @ApiModelProperty("小程序名称")
    private String dappInfoName;

    @ApiModelProperty("小程序图标URL")
    private String dappInfoPortrait;

    @ApiModelProperty("小程序URL")
    private String dappInfoUrl;

    @ApiModelProperty("小程序URL的主机，如果是域名，只保留一级域名")
    private String dappInfoUrlHost;

    @ApiModelProperty("小程序OSN ID")
    private String dappInfoTarget;

    @ApiModelProperty("是否被屏蔽")
    private Boolean ban;

}
