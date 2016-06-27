package sk.eea.arttag;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("application")
public class ApplicationProperties {

    private String version;

    private String hostname;

    private Integer maxDescriptionSize;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getMaxDescriptionSize() {
        return maxDescriptionSize;
    }

    public void setMaxDescriptionSize(Integer maxDescriptionSize) {
        this.maxDescriptionSize = maxDescriptionSize;
    }
}
