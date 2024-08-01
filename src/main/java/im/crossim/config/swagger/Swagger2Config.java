package im.crossim.config.swagger;

import org.springframework.beans.factory.annotation.Value;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@EnableSwagger2
@Configuration
public class Swagger2Config {

    @Value("${swagger.enabled:true}")
    private Boolean enabled;

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.any())
                .paths(PathSelectors.regex("/.*/error").negate())
                .paths(PathSelectors.regex("/.*/actuator/health.*").negate())
                .paths(PathSelectors.regex("/.*/actuator").negate())
                .build()
                .enable(enabled)
                .globalRequestParameters(getGlobalRequestParameters());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("注册服务接口文挡")
                .description("本文档描述注册服务的接口定义")
                .version("1.0")
                .build();
    }

    private List<RequestParameter> getGlobalRequestParameters() {
        List<RequestParameter> globalRequestParameters = new ArrayList<>();

        RequestParameterBuilder tokenBuilder = new RequestParameterBuilder();
        tokenBuilder
                .name("Authorization")
                .description("认证")
                .required(false)
                .in(ParameterType.HEADER);
        globalRequestParameters.add(tokenBuilder.build());

        return globalRequestParameters;
    }

}
