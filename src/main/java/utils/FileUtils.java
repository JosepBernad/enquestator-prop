package utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Aquesta classe s'encarrega de la persistència dels fitxers
 */
public class FileUtils {
    /**
     * Aquest mètode permet guardar un objecte qualsevol a un fitxer amb nom filename
     * @param o l'objecte
     * @param filename el fitxer
     */
    public static void saveObjectInFile(Object o, String filename) {
        FileWriter fileWriter = null;
        try {
            File file = new File(filename);
            fileWriter = new FileWriter(file);
            if (!file.exists()) file.createNewFile();
            ObjectMapper objectMapper = new ObjectMapper().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
            fileWriter.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o));
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
