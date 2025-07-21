package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfigs implements WebMvcConfigurer {
    public void addViewControllers(ViewControllerRegistry controllerRegistration) {
        controllerRegistration.addViewController("/user").
                setViewName("forward:/user.html");
        controllerRegistration.addViewController("/admin").
                setViewName("forward:/admin.html");
    }
}
