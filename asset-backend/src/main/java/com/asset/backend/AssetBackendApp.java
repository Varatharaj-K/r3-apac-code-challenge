package com.asset.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@EnableSwagger2
@SpringBootApplication
@EnableAutoConfiguration
public class AssetBackendApp extends SpringBootServletInitializer {

    private static final String TITLE = "";
    private static final String DESCRIPTION="";
    private static final String DEVELOPER_NAME="";
    private static final String COMPANY_URL="";
    private static final String VERSION="1.0";

    public static void main(String[] args) {
        SpringApplication.run(AssetBackendApp.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return super.configure(builder);
    }

    @Bean
    public Docket customDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.asset.backend"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(TITLE, DESCRIPTION, VERSION, "",
                new Contact(DEVELOPER_NAME, COMPANY_URL,"" ), " ",
                "", new ArrayList<VendorExtension>());
    }

}
