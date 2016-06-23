package sk.eea.arttag.helpers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilesHelper {

    private static final Logger LOG = LoggerFactory.getLogger(FilesHelper.class);

    public static Boolean download(String imagePath, Path targetFileName) {
        try {
            URL url = new URL(imagePath);
            if(!url.getProtocol().startsWith("http")){
                LOG.info("Only HTTP(s) protocols are accepted");
            }
            targetFileName.toFile().createNewFile();
            try(InputStream is = url.openStream();FileOutputStream os = new FileOutputStream(targetFileName.toFile())){
                byte[] buf = new byte[1024];
                while(is.read(buf)>0){
                    os.write(buf);
                }
            }catch(IOException e){
                LOG.info("Could not downlad URL");
                return false;
            }
        } catch (Exception e ) {
            LOG.info("Invalid URL for download file");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}
