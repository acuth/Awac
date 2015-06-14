package acuth.awac;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by adrian on 14/06/15.
 */
public class ConsoleLogWriter {
    private BufferedWriter mWriter = null;
    private SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    private void writeLog(String msg) throws IOException {
        mWriter.write(TIME_FORMAT.format(new Date()) + " - " + msg);
        mWriter.newLine();
        mWriter.flush();
    }

    ConsoleLogWriter(String filename) {
        System.out.println("new ConsoleLogWriter()");
        File logFile = null;
        if (filename != null && filename.length() > 0) {
            File logDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Awac");
            if (logDir.exists() || logDir.mkdirs()) {
                try {
                    logFile = new File(logDir, filename);
                    if (!logFile.exists()) {
                        logFile.createNewFile();
                    }
                    FileWriter fw = new FileWriter(logFile);
                    mWriter = new BufferedWriter(fw);
                    writeLog("AWAC: web-app start");
                    //bwriter.close();
                    //fw.close();
                } catch (IOException ex) {
                    System.out.println("Problem setting up log file");
                    ex.printStackTrace();
                    logFile = null;
                }
            }
        }
        System.out.println("ConsoleLogWriter - log file " + logFile);
    }

    public synchronized void log(String msg) {
        System.out.println(msg);
        if (mWriter != null) {
            try {
                //FileWriter fw = new FileWriter(mFile, true);
                //BufferedWriter bwriter = new BufferedWriter(fw);
                writeLog(msg);
                //bwriter.close();
                //fw.close();
            } catch (IOException e) {
                System.out.println("Problem writing to log file");
                e.printStackTrace();
            }
        }
    }
}
