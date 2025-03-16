import com.thesis.diagramplugin.diagram.classdiagram.ClassDiagramView;
import com.thesis.diagramplugin.diagram.classdiagram.model.ClassDiagramModelPackage;
import com.thesis.diagramplugin.parser.JavaDiagramParser;
import org.dom4j.DocumentHelper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class KopenogramRendererTest {

    @Test
    public void testBreakElementRendering() throws Exception {
        // Load test XML
        File xmlFile = new File("src/test/resources/umlTest.xml");
        // Create the Kopenogram view builder
        ClassDiagramView builder = new ClassDiagramView(new ClassDiagramModelPackage(DocumentHelper.parseText(new String(Files.readAllBytes(Paths.get(xmlFile.getPath())))).getRootElement()));
    }
    @Test
    public void testUML() throws Exception{
        JavaDiagramParser parser = new JavaDiagramParser();
        File xmlFile = parser.parseDirectory("C:\\Users\\peska\\OneDrive\\Dokumenty\\GitHub\\UMLPlugin\\DiagramPlugin\\src\\main\\java\\com\\thesis\\diagramplugin\\testClasses");
        if (xmlFile != null) {
            System.out.println("Generated XML: " + xmlFile.getAbsolutePath());
        } else {
            System.out.println("Error: XML file not created.");
        }


    }
}
