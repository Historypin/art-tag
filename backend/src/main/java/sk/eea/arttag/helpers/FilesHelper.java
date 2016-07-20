package sk.eea.arttag.helpers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilesHelper {

    private static final Logger LOG = LoggerFactory.getLogger(FilesHelper.class);

    public static boolean download(String imagePath, Path targetPath) {
        URL url;
        
        try {
            url = new URL(imagePath);
            if (!url.getProtocol().startsWith("http")) {
                LOG.info("Only HTTP(s) protocols are accepted");
            }
        } catch (Exception e) {
            LOG.info("Invalid URL for download file. URL: " + imagePath);
            return false;
        }

        final int maxAttempts = 3;
        for (int counter = 0; counter < maxAttempts; counter++) {
            if (download2(url, targetPath)) {
                return true;
            }
        }
        
        return false;
    }

    private static boolean download2(URL url, Path targetPath) {
        try (InputStream is = url.openStream(); FileOutputStream os = new FileOutputStream(targetPath.toFile())) {
            byte[] buf = new byte[1024];
            int n;
            while ((n = is.read(buf)) > 0) {
                os.write(buf, 0, n);
            }
        } catch (IOException e) {
            LOG.info("Could not downlad URL " + url);
            return false;
        }
        return true;
    }

}
