import org.junit.jupiter.api.Test;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import com.thesis.diagramplugin.diagram.kopenogram.KopenogramXmlViewBuilder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class KopenogramRendererTest {

    @Test
    public void testBreakElementRendering() throws Exception {
        // Load test XML
        File xmlFile = new File("src/test/resources/exceptionTest.xml");
        // Create the Kopenogram view builder
        KopenogramXmlViewBuilder builder = new KopenogramXmlViewBuilder(new String(Files.readAllBytes(Paths.get(xmlFile.getPath()))));
    }
}
