package im.crossim.group.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import im.crossim.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@ApiModel("群组实体类")
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_group", autoResultMap = true)
public class GroupEntity extends BaseEntity {

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("头像")
    private String portrait;

    @ApiModelProperty("用户列表")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> userList;

    @ApiModelProperty("群组OSN ID")
    private String groupOsnId;

    @ApiModelProperty("群组信息")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject groupInfo;

    @ApiModelProperty("最大人数")
    private Integer maxMemberCount;

    @ApiModelProperty("是否删除")
    private Boolean deleted;

}
