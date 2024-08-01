package im.crossim.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import sun.net.util.IPAddressUtil;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class UrlUtil {

    /**
     * 提取URL中的主机，并且如果主机为域名，则只保留一级域名。
     *
     * @param urlStr URL字符串
     * @return 主机，如果解析失败则返回null
     */
    public static String extractUrlHost(String urlStr) {
        if (StringUtils.isEmpty(urlStr)) {
            return null;
        }

        try {
            URL url = new URL(urlStr);

            String host = url.getHost();
            if (StringUtils.isEmpty(host)) {
                return null;
            }

            if (IPAddressUtil.isIPv4LiteralAddress(host)
                    || IPAddressUtil.isIPv6LiteralAddress(host)) {
                return host;
            } else {
                Pattern pattern = Pattern.compile("[^.]+\\.[^.]+$");
                Matcher matcher = pattern.matcher(host);
                if (matcher.find()) {
                    return matcher.group(0);
                } else {
                    return host;
                }
            }
        } catch (Exception ex) {
            log.error(
                    String.format(
                            "解析URL失败：%s",
                            urlStr
                    ),
                    ex
            );
            return null;
        }
    }

}
