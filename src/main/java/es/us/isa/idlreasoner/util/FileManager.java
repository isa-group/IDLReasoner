package es.us.isa.idlreasoner.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    public static File recreateFile(String filePath) {
        File file = new File(filePath);
        file.delete();
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File createFileIfNotExists(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void appendLine(String filePath, String line) {
        File file = new File(filePath);
        FileWriter fw;
        try {
            fw = new FileWriter(file, true);
            BufferedWriter out = new BufferedWriter(fw);
            out.append(line + "\n");
            out.flush();
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
