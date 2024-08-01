package im.crossim.config.web;

import im.crossim.config.interceptor.AdminAuthInterceptor;
import im.crossim.config.interceptor.AuthInterceptor;
import im.crossim.config.interceptor.RequestLogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private AdminAuthInterceptor adminAuthInterceptor;

    @Autowired
    private RequestLogInterceptor requestLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 用户侧接口日志拦截器。
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/admin/**")
                .order(10);

        // 用户侧接口认证拦截器。
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/admin/**",

                        "/api/user/register",
                        "/api/user/login",
                        "/api/user/get-dapp-info",
                        "/api/user/dapp-login",
                        "/api/user/change-password-by-code",
                        "/api/user/refresh-token",
                        "/api/user/send-code",
                        "/api/test/**",
                        "/api/app/get-app-config",
                        "/api/user/get-recommended-osn-ids",

                        "/api/user/get-login-info",
                        "/api/user/login-with-double-key"
                )
                .order(20);

        // 管理侧接口认证拦截器。
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/admin/**")
                .order(21);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
    }

}
