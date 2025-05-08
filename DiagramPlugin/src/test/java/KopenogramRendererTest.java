import com.thesis.diagramplugin.diagram.kopenogram.KopenogramView;
import com.thesis.diagramplugin.parser.JavaDiagramParser;
import com.thesis.diagramplugin.parser.PythonDiagramParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class KopenogramRendererTest {

    @Test
    public void testBreakElementRendering() throws Exception {
        // Load test XML
        File xmlFile = new File("C:\\Users\\peska\\OneDrive\\Dokumenty\\001BP\\2025-03-25_75_INP\\75_INP\\util\\divide_method.xml");
        //Create the Kopenogram view builder
        KopenogramView builder = new KopenogramView(new String(Files.readAllBytes(Paths.get(xmlFile.getPath()))));
    }
    @Test
    public void testUML() throws Exception{
        JavaDiagramParser parser = new JavaDiagramParser();
        PythonDiagramParser parser1 = new PythonDiagramParser();
        File xmlFile = parser.parseDirectory("C:\\Users\\peska\\OneDrive\\Dokumenty\\GitHub\\UMLPlugin\\DiagramPlugin\\src\\main\\java\\com\\thesis\\diagramplugin\\testClasses");
//        File xmlFIle = parser1.parseDirectory("C:\\Users\\peska\\OneDrive\\Dokumenty\\001BP\\2025-03-25_75_INP\\75_INP\\modules")
        if (xmlFile != null) {
            System.out.println("Generated XML: " + xmlFile.getAbsolutePath());
        } else {
            System.out.println("Error: XML file not created.");
        }


    }

    public void tatoMetodaMaNazevDelsiJakTricetPetZnaku()
    {
        int promennaKteraMaNazevDelsiJakTriceetPetZnaku = 35;
    }
}
