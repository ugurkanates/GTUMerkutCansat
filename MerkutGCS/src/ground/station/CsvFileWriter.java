package ground.station;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ashraf
 *
 */
public class CsvFileWriter {

    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    public static int howmany = 0 ;
    public static boolean fileOpen=false;
    public static FileWriter fileWriter = null;

    //CSV file header
    //         public SensorData(String paket,String tarih, String airpress, String airalt, String desrate, String airtemp, String battv, String gpslat, String gpslon, String gpsalt, String gpssat) {

    private static final String FILE_HEADER = "TAKIM,PAKET,TARIH,BASINC,YUKSEK,DUSHIZ,SICAK,BATVOLT,GPSLAT,GPSLON,GPSALT,GPSSAT";

    public static void writeCsvFile(String fileName, SerialDevice.SensorData cihaz) {

        //Create new students objects



        try {
            fileWriter = new FileWriter(fileName);
            fileOpen = true;

            if(howmany == 0){
                //Write the CSV file header
                fileWriter.append(FILE_HEADER.toString());

                //Add a new line separator after the header
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

                howmany++;
                fileWriter.append("7893");
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(cihaz.paket);
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(cihaz.tarih);
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(cihaz.airpress);
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(cihaz.airalt);
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(cihaz.desrate);
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(cihaz.airtemp);
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(cihaz.battv);
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(cihaz.gpslat);
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(cihaz.gpslon);
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(cihaz.gpsalt);
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(cihaz.gpssat);
                fileWriter.append(NEW_LINE_SEPARATOR);


            System.out.println("CSV file was created successfully !!!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {

            try {
                fileWriter.flush();
                fileWriter.close();
                fileWriter = null;
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }

        }
    }
}