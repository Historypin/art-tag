package sk.eea.arttag.helpers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class FilesHelperTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Ignore
    public void testDownload() throws Exception {
        //String imagePath = "http://gradcontent.com/lib/600x350/marmalade5.jpg";
        String imagePath = "http://gradcontent.com/lib/600x350/drink2.jpg";
        
        String fileName = new File(imagePath).getName();
        Path targetPath = Paths.get(System.getProperty("java.io.tmpdir"), fileName);

        FilesHelper.download(imagePath, targetPath);
    }

}
