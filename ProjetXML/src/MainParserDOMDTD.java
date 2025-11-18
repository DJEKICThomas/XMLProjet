import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class MainParserDOMDTD {

    public static void main(String[] args) {

        String xmlPath = "src/resources/images.xml";

        try {
            // Mesure mémoire avant
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long memAvant = runtime.totalMemory() - runtime.freeMemory();

            // Mesure temps avant
            long startTime = System.currentTimeMillis();

            // Configurer DOM avec validation DTD
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);       // ACTIVE la validation DTD
            factory.setNamespaceAware(false);  // pas de namespaces pour DTD

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new org.xml.sax.helpers.DefaultHandler());

            Document doc = builder.parse(new File(xmlPath));

            // Mesure temps après parsing
            long endTime = System.currentTimeMillis();
            long memApres = runtime.totalMemory() - runtime.freeMemory();

            System.out.println("Validation DTD + parsing DOM réussi !");
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
            System.err.println("Erreur DOM/DTD :");
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
