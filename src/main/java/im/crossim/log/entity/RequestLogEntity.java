package im.crossim.log.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import im.crossim.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel("请求配置实体类")
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_request_log", autoResultMap = true)
public class RequestLogEntity extends BaseEntity {

    @ApiModelProperty("请求ID")
    private String requestId;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("客户端IP")
    private String clientIp;

    @ApiModelProperty("URL地址")
    private String url;

    @ApiModelProperty("URL地址的PATH部分")
    private String path;

    @ApiModelProperty("请求方式")
    private String method;

    @ApiModelProperty("请求头")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject headers;

    @ApiModelProperty("请求体")
    private String requestBody;

    @ApiModelProperty("响应体")
    private String responseBody;

}
