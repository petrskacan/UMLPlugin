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
        File xmlFile = new File("src/test/resources/returnNumberTwo.xml");
        SAXReader reader = new SAXReader();
        Document document = reader.read(xmlFile);
        System.out.println("Testing break element rendering...");
        // Create the Kopenogram view builder
        KopenogramXmlViewBuilder builder = new KopenogramXmlViewBuilder(new String(Files.readAllBytes(Paths.get(xmlFile.getPath()))));
        // Debug output
        System.out.println("Testing break element rendering...");
    }

    @Test
    void simpleTest() {
        System.out.println("Running a simple test...");
        assertTrue(true);
    }

    //TODO - ELSE BROKEN, WHAT ELSE XD
}
