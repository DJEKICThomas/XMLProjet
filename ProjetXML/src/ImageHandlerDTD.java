import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;

public class ImageHandlerDTD extends DefaultHandler {

    private boolean inLabel = false;
    private boolean inLocalization = false;

    private int countLocRight = 0;
    private boolean imageHasLocRight = false;

    private Map<String, Integer> labelCount = new HashMap<>();

    private StringBuilder buffer = new StringBuilder();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        if (qName.equals("image")) {
            imageHasLocRight = false;
        }
        if (qName.equals("label")) {
            inLabel = true;
        }
        if (qName.equals("localization")) {
            inLocalization = true;
        }

        buffer.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        buffer.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {

        String content = buffer.toString().trim();

        if (inLabel && qName.equals("label")) {
            labelCount.merge(content, 1, Integer::sum);
            inLabel = false;
        }

        if (inLocalization && qName.equals("localization")) {
            if (content.equals("loc right")) {
                imageHasLocRight = true;
            }
            inLocalization = false;
        }

        if (qName.equals("image")) {
            if (imageHasLocRight) {
                countLocRight++;
            }
        }
    }

    // Getters des résultats
    public int getCountLocRight() {
        return countLocRight;
    }

    public LinkedHashMap<String, Integer> getTop10Labels() {
        return labelCount.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(10)
                .collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()),
                        LinkedHashMap::putAll);
    }
}
