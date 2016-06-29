package sk.eea.arttag.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import sk.eea.arttag.ApplicationProperties;

@Configuration
public class StaticResourceConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(String.format("/%s/**", applicationProperties.getCulturalObjectsPublicPath()))
            .addResourceLocations(String.format("file:%s", applicationProperties.getCulturalObjectsFileSystemPath()));
    }
}
