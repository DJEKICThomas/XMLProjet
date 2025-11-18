import org.w3c.dom.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.util.*;

public class MainParserDOMXSD {

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

            // Charger le schéma XSD
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new File(xsdPath));

            // Configurer DOM + validation XSD
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setSchema(schema);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(xmlPath));

            // Mesure temps après parsing
            long endTime = System.currentTimeMillis();
            long memApres = runtime.totalMemory() - runtime.freeMemory();

            System.out.println("Validation XSD + parsing DOM réussi !");
            System.out.println("--------------------------------------------------");

            // Comptage
            int countLocRight = 0;
            Map<String, Integer> labelCount = new HashMap<>();

            NodeList images = doc.getElementsByTagName("image");

            for (int i = 0; i < images.getLength(); i++) {
                Element image = (Element) images.item(i);

                boolean hasLocRight = false;

                NodeList labels = image.getElementsByTagName("label");
                for (int j = 0; j < labels.getLength(); j++) {
                    String labelText = labels.item(j).getTextContent().trim();
                    labelCount.merge(labelText, 1, Integer::sum);
                }

                NodeList locs = image.getElementsByTagName("localization");
                for (int j = 0; j < locs.getLength(); j++) {
                    String locText = locs.item(j).getTextContent().trim();
                    if (locText.equals("loc right")) hasLocRight = true;
                }

                if (hasLocRight) countLocRight++;
            }

            // Affichage
            System.out.println("Nombre d’images contenant 'loc right' : " + countLocRight);
            System.out.println("\nTop 10 des labels les plus fréquents :");
            getTop10(labelCount).forEach((label, count) -> System.out.println(label + " → " + count + " occurrences"));

            System.out.println("\nTemps d’exécution : " + (endTime - startTime) + " ms");
            System.out.println("Mémoire utilisée : " + (memApres - memAvant) / 1024 + " KB");

        } catch (Exception e) {
            System.err.println("Erreur DOM/XSD :");
            e.printStackTrace();
        }
    }

    private static LinkedHashMap<String, Integer> getTop10(Map<String, Integer> map) {
        return map.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(10)
                .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);
    }
}
