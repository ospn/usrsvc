package im.crossim.user.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("登录配置")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginConfigVo {

    @ApiModelProperty("临时目录路径")
    private String tempDirectory;

    @ApiModelProperty("用户头像路径")
    private String userPortraitDirectory;

    @ApiModelProperty("群组头像路径")
    private String groupPortraitDirectory;

    @ApiModelProperty("IM服务器主机地址")
    private String hostIp;

    @ApiModelProperty("用户的OSN ID")
    private String userId;

    @ApiModelProperty("用户的VIP等级")
    private Integer userVipLevel;

    @ApiModelProperty("注册服务的OSN ID")
    private String userServiceId;

    @ApiModelProperty("文件上传接口的URL")
    private String apiUploadUrl;

    @ApiModelProperty("通过文件上传接口上传的文件的URL前缀")
    private String fileUrlPrefix;

    @ApiModelProperty("应用的主DAPP")
    @JsonProperty("MainDapp")
    private String mainDapp;

    @ApiModelProperty("应用的主页URL")
    private String mainPageUrl;

}
