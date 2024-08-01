package im.crossim.user.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetLoginInfoVo {

    private String serviceOsnId;
    private String random;
    private String challenge;
    private String sign;

}
