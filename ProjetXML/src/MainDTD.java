import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MainDTD {

    public static void main(String[] args) {

        String csvPath = "src/resources/PADCHEST_chest_x_ray_images_labels_160K_01.02.19.csv";
        String xmlPath = "src/resources/images.xml";

        try (
                BufferedReader br = new BufferedReader(new FileReader(csvPath));
                BufferedWriter bw = new BufferedWriter(new FileWriter(xmlPath))
        ) {
            // Écrire l’en-tête XML
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<!DOCTYPE images SYSTEM \"images.dtd\">\n");
            bw.write("<images>\n");

            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {

                if (firstLine) { // sauter la ligne d'en-têtes du CSV
                    firstLine = false;
                    continue;
                }

                List<String> fields = parseCSVLine(line);

                if (fields.size() < 36) continue;

                // Raccourcis
                String imageID = fields.get(1);
                String imageDir = fields.get(2);
                String studyDate = fields.get(3);
                String studyID = fields.get(4);
                String patientID = fields.get(5);
                String patientBirth = fields.get(6);
                String patientSex = fields.get(7);
                String viewPosition = fields.get(8);
                String projection = fields.get(9);
                String methodProjection = fields.get(10);
                String pediatric = fields.get(11);
                String modality = fields.get(12);
                String manufacturer = fields.get(13);

                String photometric = fields.get(14);
                String pixelRep = fields.get(15);
                String pixelAspect = fields.get(16);
                String spatialRes = fields.get(17);
                String bitsStored = fields.get(18);
                String windowCenter = fields.get(19);
                String windowWidth = fields.get(20);
                String rows = fields.get(21);
                String columns = fields.get(22);
                String tubeCurrent = fields.get(23);
                String exposure = fields.get(24);
                String exposureInuAs = fields.get(25);
                String exposureTime = fields.get(26);
                String relativeExposure = fields.get(27);

                String reportID = fields.get(28);
                String reportText = escapeXML(fields.get(29));
                String methodLabel = fields.get(30);

                List<String> labels = parseArray(fields.get(31));
                List<String> localizations = parseArray(fields.get(32));
                List<List<String>> labelsBySentence = parseArrayOfArrays(fields.get(33));
                List<String> cuisLabels = parseArray(fields.get(34));
                List<String> cuisLocs = parseArray(fields.get(35));

                // ---- Génération XML ----
                bw.write("  <image id=\"" + escapeXML(imageID) + "\">\n");

                bw.write("    <ImageDir>" + escapeXML(imageDir) + "</ImageDir>\n");
                bw.write("    <StudyDate>" + escapeXML(studyDate) + "</StudyDate>\n");
                bw.write("    <StudyID>" + escapeXML(studyID) + "</StudyID>\n");

                bw.write("    <patient>\n");
                bw.write("      <PatientID>" + escapeXML(patientID) + "</PatientID>\n");
                bw.write("      <PatientBirth>" + escapeXML(patientBirth) + "</PatientBirth>\n");
                bw.write("      <PatientSex>" + escapeXML(patientSex) + "</PatientSex>\n");
                bw.write("    </patient>\n");

                bw.write("    <Acquisition>\n");
                bw.write("      <ViewPosition>" + escapeXML(viewPosition) + "</ViewPosition>\n");
                bw.write("      <Projection>" + escapeXML(projection) + "</Projection>\n");
                bw.write("      <MethodProjection>" + escapeXML(methodProjection) + "</MethodProjection>\n");
                bw.write("      <Pediatric>" + escapeXML(pediatric) + "</Pediatric>\n");
                bw.write("      <Modality>" + escapeXML(modality) + "</Modality>\n");
                bw.write("      <Manufacturer>" + escapeXML(manufacturer) + "</Manufacturer>\n");
                bw.write("    </Acquisition>\n");

                bw.write("    <ImageMetadata>\n");
                bw.write("      <PhotometricInterpretation>" + escapeXML(photometric) + "</PhotometricInterpretation>\n");
                bw.write("      <PixelRepresentation>" + escapeXML(pixelRep) + "</PixelRepresentation>\n");
                bw.write("      <PixelAspectRatio>" + escapeXML(pixelAspect) + "</PixelAspectRatio>\n");
                bw.write("      <SpatialResolution>" + escapeXML(spatialRes) + "</SpatialResolution>\n");
                bw.write("      <BitsStored>" + escapeXML(bitsStored) + "</BitsStored>\n");
                bw.write("      <WindowCenter>" + escapeXML(windowCenter) + "</WindowCenter>\n");
                bw.write("      <WindowWidth>" + escapeXML(windowWidth) + "</WindowWidth>\n");
                bw.write("      <Rows>" + escapeXML(rows) + "</Rows>\n");
                bw.write("      <Columns>" + escapeXML(columns) + "</Columns>\n");
                bw.write("      <XRayTubeCurrent>" + escapeXML(tubeCurrent) + "</XRayTubeCurrent>\n");
                bw.write("      <Exposure>" + escapeXML(exposure) + "</Exposure>\n");
                bw.write("      <ExposureInuAs>" + escapeXML(exposureInuAs) + "</ExposureInuAs>\n");
                bw.write("      <ExposureTime>" + escapeXML(exposureTime) + "</ExposureTime>\n");
                bw.write("      <RelativeXRayExposure>" + escapeXML(relativeExposure) + "</RelativeXRayExposure>\n");
                bw.write("    </ImageMetadata>\n");

                bw.write("    <Report>\n");
                bw.write("      <ReportID>" + escapeXML(reportID) + "</ReportID>\n");
                bw.write("      <Text>" + reportText + "</Text>\n");
                bw.write("      <MethodLabel>" + escapeXML(methodLabel) + "</MethodLabel>\n");
                bw.write("    </Report>\n");

                // Labels
                if (!labels.isEmpty()) {
                    bw.write("    <Labels>\n");
                    for (String l : labels)
                        bw.write("      <label>" + escapeXML(l) + "</label>\n");
                    bw.write("    </Labels>\n");
                }

                // Localisations
                if (!localizations.isEmpty()) {
                    bw.write("    <Localizations>\n");
                    for (String l : localizations)
                        bw.write("      <localization>" + escapeXML(l) + "</localization>\n");
                    bw.write("    </Localizations>\n");
                }

                // Array of arrays
                if (!labelsBySentence.isEmpty()) {
                    bw.write("    <LabelsLocalizationsBySentence>\n");
                    for (List<String> sentence : labelsBySentence) {
                        bw.write("      <sentence>\n");
                        for (String s : sentence)
                            bw.write("        <item>" + escapeXML(s) + "</item>\n");
                        bw.write("      </sentence>\n");
                    }
                    bw.write("    </LabelsLocalizationsBySentence>\n");
                }

                // CUIs labels
                if (!cuisLabels.isEmpty()) {
                    bw.write("    <labelCUIS>\n");
                    for (String c : cuisLabels)
                        bw.write("      <cui>" + escapeXML(c) + "</cui>\n");
                    bw.write("    </labelCUIS>\n");
                }

                // CUIs localizations
                if (!cuisLocs.isEmpty()) {
                    bw.write("    <LocalizationsCUIS>\n");
                    for (String c : cuisLocs)
                        bw.write("      <cui>" + escapeXML(c) + "</cui>\n");
                    bw.write("    </LocalizationsCUIS>\n");
                }

                bw.write("  </image>\n");
            }

            bw.write("</images>");
            System.out.println("XML généré avec succès → " + xmlPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '\"') inQuotes = !inQuotes;
            else if (c == ',' && !inQuotes) {
                result.add(sb.toString().trim());
                sb.setLength(0);
            } else sb.append(c);
        }
        result.add(sb.toString().trim());
        return result;
    }

    private static List<String> parseArray(String field) {
        List<String> list = new ArrayList<>();
        field = field.replace("[", "").replace("]", "").replace("'", "").trim();
        if (field.isEmpty()) return list;
        for (String item : field.split(",")) list.add(item.trim());
        return list;
    }

    private static List<List<String>> parseArrayOfArrays(String field) {
        List<List<String>> res = new ArrayList<>();
        if (!field.contains("[")) return res;
        field = field.trim();
        field = field.substring(1, field.length() - 1); // enlève [[ ]]
        String[] sentences = field.split("\\],\\s*\\[");
        for (String s : sentences) {
            s = s.replace("[", "").replace("]", "").replace("'", "");
            List<String> sub = new ArrayList<>();
            for (String item : s.split(",")) {
                if (!item.isBlank()) sub.add(item.trim());
            }
            res.add(sub);
        }
        return res;
    }

    private static String escapeXML(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
