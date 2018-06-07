package tk.ubublik.spotifydownloader.util;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static File getWritableFile(String filename) throws IOException {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, filename);
        path.mkdirs();
        file.createNewFile();
        return file;
    }

    public static FileOutputStream getOutputStream(File file) throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    public static boolean removeFile(File file){
        try{
            return file.delete();
        } catch (Exception e){
            return false;
        }
    }
}
