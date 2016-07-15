package sk.eea.arttag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@PropertySource({"classpath:default.properties", "file:${working.directory}/conf/arttag.properties"})
@EnableConfigurationProperties(value = {ApplicationProperties.class, GameProperties.class})
public class ArttagApp extends SpringBootServletInitializer {


    public static void main(String[] args) {
        SpringApplication.run(ArttagApp.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ArttagApp.class);
    }
}
