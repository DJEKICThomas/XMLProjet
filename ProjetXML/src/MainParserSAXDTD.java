import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainParserSAXDTD {

    public static void main(String[] args) {

        String xmlPath = "src/resources/images.xml";

        try {
            // Mesure mémoire avant
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long memAvant = runtime.totalMemory() - runtime.freeMemory();

            // Mesure temps avant
            long startTime = System.currentTimeMillis();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);  // Active la validation DTD
            factory.setNamespaceAware(false);

            SAXParser parser = factory.newSAXParser();
            ImageHandlerDTD handler = new ImageHandlerDTD();

            System.out.println("Validation + parsing SAX en cours...");
            parser.parse(xmlPath, handler);

            // Mesure temps après parsing
            long endTime = System.currentTimeMillis();
            long memApres = runtime.totalMemory() - runtime.freeMemory();

            // Résultats
            System.out.println("--------------------------------------------------");
            System.out.println("Nombre d’images contenant la localisation 'loc right' : "
                    + handler.getCountLocRight());

            System.out.println("\nTop 10 des labels les plus fréquents :");
            handler.getTop10Labels()
                    .forEach((label, count) ->
                            System.out.println(label + " → " + count + " occurrences")
                    );

            System.out.println("\nTemps d’exécution : " + (endTime - startTime) + " ms");
            System.out.println("Mémoire utilisée : " + (memApres - memAvant) / 1024 + " KB");

        } catch (Exception e) {
            System.err.println("Erreur SAX/DTD :");
            e.printStackTrace();
        }
    }
}
