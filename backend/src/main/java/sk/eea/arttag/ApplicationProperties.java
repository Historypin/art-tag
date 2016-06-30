package sk.eea.arttag;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("application")
public class ApplicationProperties {

    private String version;

    private String hostname;

    private String hostnamePrefix;

    private Integer maxDescriptionSize;

    private String culturalObjectsPublicPath;

    private String culturalObjectsFileSystemPath;

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

    public String getHostnamePrefix() {
        return hostnamePrefix;
    }

    public void setHostnamePrefix(String hostnamePrefix) {
        this.hostnamePrefix = hostnamePrefix;
    }

    public String getCulturalObjectsPublicPath() {
        return culturalObjectsPublicPath;
    }

    public void setCulturalObjectsPublicPath(String culturalObjectsPublicPath) {
        this.culturalObjectsPublicPath = culturalObjectsPublicPath;
    }

    public String getCulturalObjectsFileSystemPath() {
        return culturalObjectsFileSystemPath;
    }

    public void setCulturalObjectsFileSystemPath(String culturalObjectsFileSystemPath) {
        this.culturalObjectsFileSystemPath = culturalObjectsFileSystemPath;
    }
}
