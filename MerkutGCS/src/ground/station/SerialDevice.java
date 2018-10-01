package ground.station;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringPropertyBase;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * SerialDevice represents a microcontroller or Arduino device on the embedded
 * system that is connected via a serial port and continuously supplying
 * telemetry data.
 *
 * @author Siddhesh Rane
 */
public class SerialDevice implements SerialPortEventListener {

    private final SerialPort port;
    private final StringBuilder buffer;
    private final PipedWriter writer = new PipedWriter();
    private final PipedReader reader = new PipedReader();
    private final ReadOnlyStringPropertyBaseImpl output = new ReadOnlyStringPropertyBaseImpl();
    public final ConcurrentLinkedQueue<SensorData> q = new ConcurrentLinkedQueue<>();

    private Thread parserThread;
    Runnable parser = new Runnable() {
        /*
       <TAKIM NO>,<PAKET NUMARASI>,<GÖNDERME SAATİ>,<BASINÇ>,<YÜKSEKLİK>,<İNİŞ HIZI>,
<SICAKLIK>,<PİL GERİLİMİ>,<GPS LATITUDE>,<GPS LONGITUDE>,<GPS ALTITUDE>,<UYDU
STATÜSÜ>
          {<2018>,<1>,<12021997>,<12>,<13.32>,<14.42>,<15>,<550>,<13.047>,<17.450>,<42.400>,<0>}
          {<a2018>,<b1>,<c12021997>,<d12>,<e13.32>,<f14.42>,<e
        */

        public String removeLastChar(String s) {
            if (s == null || s.length() == 0) {
                return s;
            }
            return s.substring(0, s.length()-1);
        }
        @Override
        public void run() {
            Scanner scanner = new Scanner(reader);
            //uzun bi string geliyo tum datalar
            double frame = 0;
           String token = scanner.nextLine();
            /*String token2[] = new String[]{"" +
                    "<7893,00001,2018.09.17/16:50:07,100919.67,432.1,07.2,29.3,7.4,43.12345,21.12345,430.9,08>",
                    "<7895,00002,2018.09.17/16:50:07,10091.67,431.1,07.2,25.4,5.4,43.12345,21.12345,434.9,08>",
                    "<7895,00003,2018.09.17/16:50:07,10091.67,431.1,07.2,25.4,5.4,43.12345,21.12345,434.9,08>",
                    "<7895,00004,2018.09.17/16:50:07,10091.67,431.1,07.2,25.4,5.4,43.12345,21.12345,434.9,08>",
                    "<7895,00005,2018.09.17/16:50:07,10091.67,431.1,07.2,25.4,5.4,43.12345,21.12345,434.9,08>",
                    "<7895,00006,2018.09.17/16:50:07,10091.67,431.1,07.2,25.4,5.4,43.12345,21.12345,434.9,08>",-++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++-----------------------------------------------------------------------------------+-++++++++++++++++++++++++++++++++++++++++++++++++++++++-+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    "<7895,00007,2018.09.17/16:50:07,10091.67,431.1,07.2,25.4,5.4,43.12345,21.12345,434.9,08>",
                    "<7895,00008,2018.09.17/16:50:07,10091.67,431.1,07.2,25.4,5.4,43.12345,21.12345,434.9,08>"++++++++"<7895,00010,2018.09.17/16:50:07,10091.67,431.1,07.2,25.4,5.4,43.12345,21.12345,434.9,08>",
                    "<7895,00011,2018.09.17/16:50:07,10091.67,431.1,07.2,25.4,5.4,43.12345,21.12345,434.9,08>",
                    "<7895,00012,2018.09.17/16:50:07,10091.67,431.1,07.2,25.4,5.4,43.12345,21.12345,434.9,08>",+++++++++++++++++++++++++
                    "<7895,00013,2018.09.17/16:50:07,10091.67,431.1,07.2,25.4,5.4,43.12345,21.12345,434.9,08>"};
            String token = token2[new Random().nextInt(10)]; // negatif geliyomu
            */
            System.out.println(token);
            String list[] = token.split(",");

            // ilk <7299 degiscek <
            // sonuncda > silinecek
            // onun disi atima yap
            //hatta ilki olan <7893 gerek yok bide sabit takid
            //
             //        public SensorData(String tarih, String airpress, String airalt, String desrate, String airtemp, String battv, String gpslat, String gpslon, String gpsalt, String gpssat) {
                        String gpssat = removeLastChar(list[11]);
                        SensorData sensorData = new SensorData(list[1],list[2],list[3],list[4],list[5],list[6],list[7],list[8],list[9],list[10],gpssat);
                        q.add(sensorData);
                        //frame++;


        }
    };
    
    /**
     * Creates a SerialDevice class for a device connected to the given
     * SerialPort. {@code port} has to be non-null and opened.
     * NullPointerException is thrown if port is null.
     *
     * @param port the SerialPort to which the device is connected
     */
    public SerialDevice(SerialPort port) {

        if (port == null) {
            throw new NullPointerException("Serial port cannot be null");
        }
        this.port = port;
        buffer = new StringBuilder(1024);
        try {
            port.addEventListener(this);
            reader.connect(writer);
        } catch (SerialPortException | IOException ex) {
            Logger.getLogger(SerialDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ReadOnlyStringProperty outputProperty() {
        return output;
    }

    public String getOutput() {
        return output.get();
    }

    public void sendInput(String msg) {
        try {
            port.writeString(msg);
        } catch (SerialPortException ex) {
            Logger.getLogger(SerialDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventValue() > 0) {
            try {
                if (parserThread == null) {
                    parserThread = new Thread(parser, "Serial Data Parser");
                    parserThread.start();
                }
                final String msg = port.readString();
                writer.write(msg);
                writer.flush();
                buffer.append(msg);
                if (buffer.length() > 64 * 1024) {
                    buffer.delete(0, msg.length());
                }
                Platform.runLater(output::fireValueChangedEvent);

            } catch (SerialPortException | IOException ex) {
                Logger.getLogger(SerialDevice.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("port  < 0");
        }
    }

    public SerialPort getSerialPort() {
        return port;
    }

    private class ReadOnlyStringPropertyBaseImpl extends ReadOnlyStringPropertyBase {

        public ReadOnlyStringPropertyBaseImpl() {
        }

        @Override
        public String get() {
            return buffer.toString();
        }

        @Override
        public Object getBean() {
            return SerialDevice.this;
        }

        @Override
        public String getName() {
            return "output";
        }

        @Override
        public void fireValueChangedEvent() {
            super.fireValueChangedEvent(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public static final class SensorData {
        public SensorData(String paket,String tarih, String airpress, String airalt, String desrate, String airtemp, String battv, String gpslat, String gpslon, String gpsalt, String gpssat) {
            this.paket = paket;
            this.tarih = tarih;
            this.airpress = airpress;
            this.airalt = airalt;
            this.desrate = desrate;
            this.airtemp = airtemp;
            this.battv = battv;
            this.gpslat = gpslat;
            this.gpslon = gpslon;
            this.gpsalt = gpsalt;
            this.gpssat = gpssat;
        }

        //gercek datalar 2018
        public String paket;
        public String tarih;
        public String airpress;
        public String airalt;
        public String desrate;
        public String airtemp;
        public String battv;
        public String gpslat; //lattitude
        public String gpslon;
        public String gpsalt; //altitude probably
        public String gpssat;
        //DURUM YOK suan HANGI HALDE OLDUGU



        private SensorData() {
        }

    }
}
