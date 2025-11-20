import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

public class MainXSLT {
    public static void main(String[] args) {

        String xml = "src/resources/images.xml";
        String xsl = "src/resources/images.xsl";
        String output = "src/resources/images.html";

        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(new File(xsl));

            Transformer transformer = factory.newTransformer(xslt);

            transformer.transform(
                    new StreamSource(new File(xml)),
                    new StreamResult(new File(output))
            );

            System.out.println("HTML généré : " + output);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
