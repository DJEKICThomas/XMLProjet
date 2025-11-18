import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

public class MainParserSAXXSD {

    public static void main(String[] args) {

        String xmlPath = "src/resources/images_xsd.xml";
        String xsdPath = "src/resources/images.xsd";

        try {
            // Mesure mémoire avant
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long memAvant = runtime.totalMemory() - runtime.freeMemory();

            // Mesure temps avant
            long startTime = System.currentTimeMillis();

            // Charger le XSD
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new File(xsdPath));

            // Configurer SAX avec validation XSD
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setSchema(schema);

            SAXParser parser = factory.newSAXParser();
            ImageHandlerXSD handler = new ImageHandlerXSD();

            System.out.println("Validation XSD + parsing SAX en cours...");
            parser.parse(new File(xmlPath), handler);

            // Mesure temps après
            long endTime = System.currentTimeMillis();
            long memApres = runtime.totalMemory() - runtime.freeMemory();

            // Résultats
            System.out.println("--------------------------------------------------");
            System.out.println("Nombre d’images contenant 'loc right' : " + handler.getCountLocRight());

            System.out.println("\nTop 10 des labels les plus fréquents :");
            handler.getTop10Labels()
                    .forEach((label, count) -> System.out.println(label + " → " + count + " occurrences"));

            System.out.println("\nTemps d’exécution : " + (endTime - startTime) + " ms");
            System.out.println("Mémoire utilisée : " + (memApres - memAvant) / 1024 + " KB");

        } catch (Exception e) {
            System.err.println("Erreur SAX/XSD :");
            e.printStackTrace();
        }
    }
}
