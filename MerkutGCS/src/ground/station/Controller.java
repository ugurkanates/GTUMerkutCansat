package ground.station;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ground.station.CsvFileWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.exit;
import static java.lang.Thread.sleep;


public class Controller extends VBox implements Initializable {

    @FXML
    LineChart<Number, Number> chart1;

    @FXML
    LineChart<Number, Number> chart2;

    @FXML
    LineChart<Number, Number> chart3;

    @FXML
    LineChart<Number, Number> chart4;

    @FXML
    LineChart<Number, Number> chart5;

    @FXML
    NumberAxis time1,time2,time3,time4,time5;
    @FXML
    NumberAxis magnitude1,magnitude2,magnitude3,magnitude4,magnitude5;

    @FXML
    Button btnStartCamera;

    @FXML
    Button btnStopCamera;

    @FXML
    Button vidStart;

    @FXML
    Button vidStop;


    @FXML
    ComboBox<WebCamInfo> cbCameraOptions;

    @FXML
    ImageView imgWebCamCapturedImage;

    @FXML
    Text paket;

    @FXML
    Text batarya;

    @FXML
    Text basinc;

    @FXML
    Text yuksek;

    @FXML
    Text speed;

    @FXML
    Text temp;

    @FXML
    Text time;

    @FXML
    Text gpsalt;

    @FXML
    Text gpslat;

    @FXML
    Text gpslong;

    @FXML
    Text durum;




    public Controller(){

//fiiltestleri yukari aldim grafik duzelir  belki
        try {FXMLLoader loader = new FXMLLoader(Controller.class.getResource("sample.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        fiilTest(chart1,"blue",1);
        fiilTest(chart2,"red",2);
        fiilTest(chart3,"green",3);
        fiilTest(chart4,"yellow",4);
        fiilTest(chart5,"black",5);

    }
    private class WebCamInfo {

        private String webCamName;
        private int webCamIndex;

        public String getWebCamName() {
            return webCamName;
        }

        public void setWebCamName(String webCamName) {
            this.webCamName = webCamName;
        }

        public int getWebCamIndex() {
            return webCamIndex;
        }

        public void setWebCamIndex(int webCamIndex) {
            this.webCamIndex = webCamIndex;
        }

        @Override
        public String toString() {
            return webCamName;
        }
    }


    XYChart.Series<Double, Double> testSeries;
    private BufferedImage grabbedImage;
    private Webcam selWebCam = null;
    private boolean stopCamera = false;
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();

    XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
    XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
    XYChart.Series<Number, Number> series3 = new XYChart.Series<>();
    XYChart.Series<Number, Number> series4 = new XYChart.Series<>();
    XYChart.Series<Number, Number> series5 = new XYChart.Series<>();


    private String cameraListPromptText = "Choose Camera";

    public  void processSensorData(SerialDevice.SensorData sensorData) {
        if (sensorData == null) {
            return;
        }
        paket.setText("Paket No:"+sensorData.paket);
        batarya.setText("Batarya:"+sensorData.battv+" V");
        basinc.setText("Basınç(PA):"+sensorData.airpress+" Pa");
        yuksek.setText("Görev Yükselik"+sensorData.airalt+" M"); // gps air var bide?
        speed.setText("İniş Hızı:"+ sensorData.desrate+ " m/s");
        temp.setText("Sıcaklık:"+sensorData.airtemp+" Celcius");
        time.setText("Zaman:"+sensorData.tarih);
        gpsalt.setText("GPSALT:"+sensorData.gpsalt);
        gpslat.setText("GPSLAT:"+sensorData.gpslat);
        gpslong.setText("GPSLON:"+sensorData.gpslon);

        // DATA SIRASI
        //ALTITUDE SERIES 1
        series1.getData().add(new XYChart.Data<Number, Number>(Double.parseDouble(sensorData.airalt), Integer.parseInt(sensorData.paket)));
        //TEMP SERIES 2
        series2.getData().add(new XYChart.Data<Number, Number>(Double.parseDouble(sensorData.airtemp), Integer.parseInt(sensorData.paket)));
        // fALL sPEED
        series3.getData().add(new XYChart.Data<Number, Number>(Double.parseDouble(sensorData.desrate), Integer.parseInt(sensorData.paket)));
        // Press Speed
        series4.getData().add(new XYChart.Data<Number, Number>(Double.parseDouble(sensorData.airpress), Integer.parseInt(sensorData.paket)));
        //BATTERY
        // Press Speed
        series5.getData().add(new XYChart.Data<Number, Number>(Double.parseDouble(sensorData.battv), Integer.parseInt(sensorData.paket)));

        CsvFileWriter.writeCsvFile("rapor.csv",sensorData);
        double diff = Integer.parseInt(sensorData.paket) - time1.getUpperBound();
        if (diff > 0) {
            series1.getData().remove(0);
            series2.getData().remove(0);
            series3.getData().remove(0);
            series4.getData().remove(0);
            series5.getData().remove(0);
        }


            //GPS SSAT diye bisi var?
        //DURUM icin fonksiyon? bir seyden if





       /* double pitch = Math.atan2(sensorData.filaX, Math.sqrt(sensorData.filaY * sensorData.filaY + sensorData.filaZ * sensorData.filaZ));
        double roll = -Math.atan2(sensorData.filaY, Math.sqrt(sensorData.filaX * sensorData.filaX + sensorData.filaZ * sensorData.filaZ));
        xAngle1.setAngle(Math.toDegrees(roll));
        yAngle1.setAngle(Math.toDegrees(pitch));
        xAngle.setAngle(sensorData.absX);
        yAngle.setAngle(sensorData.absY);
        zAngle.setAngle(sensorData.absZ);
        deltaTime.setValue(sensorData.delta);

        xGyro.getData().add(new XYChart.Data<>(sensorData.frame, sensorData.gX));
        yGyro.getData().add(new XYChart.Data<>(sensorData.frame, sensorData.gY));
        zGyro.getData().add(new XYChart.Data<>(sensorData.frame, sensorData.gZ));

        xAcc.getData().add(new XYChart.Data<>(sensorData.frame, sensorData.aX));
        yAcc.getData().add(new XYChart.Data<>(sensorData.frame, sensorData.aY));
        zAcc.getData().add(new XYChart.Data<>(sensorData.frame, sensorData.aZ));

        xAccFil.getData().add(new XYChart.Data<>(sensorData.frame, sensorData.filaX));
        yAccFil.getData().add(new XYChart.Data<>(sensorData.frame, sensorData.filaY));
        zAccFil.getData().add(new XYChart.Data<>(sensorData.frame, sensorData.filaZ));
        double diff = sensorData.frame - time.getUpperBound();

        if (diff > 0) {
            time.setLowerBound(diff + time.getLowerBound());
            time.setUpperBound(diff + time.getUpperBound());
            xGyro.getData().remove(0);
            yGyro.getData().remove(0);
            zGyro.getData().remove(0);
            xAcc.getData().remove(0);
            yAcc.getData().remove(0);
            zAcc.getData().remove(0);
            xAccFil.getData().remove(0);
            yAccFil.getData().remove(0);
            zAccFil.getData().remove(0);
        }
        */
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        time1.setAutoRanging(true);
        magnitude1.setAutoRanging(true);
        time2.setAutoRanging(true);
        magnitude2.setAutoRanging(true);
        time3.setAutoRanging(true);
        magnitude3.setAutoRanging(true);
        time4.setAutoRanging(true);
        magnitude4.setAutoRanging(true);
        time5.setAutoRanging(true);
        magnitude5.setAutoRanging(true);
/*
        fiilTest(chart1,"blue",1);
        fiilTest(chart2,"red",2);
        fiilTest(chart3,"green",3);
        fiilTest(chart4,"yellow",4);
        fiilTest(chart5,"black",5);
        */
        ObservableList<WebCamInfo> options = FXCollections.observableArrayList();
        int webCamCounter = 0;
        for (Webcam webcam : Webcam.getWebcams()) {
            WebCamInfo webCamInfo = new WebCamInfo();
            webCamInfo.setWebCamIndex(webCamCounter);
            webCamInfo.setWebCamName(webcam.getName());
            options.add(webCamInfo);
            webCamCounter++;
        }
        cbCameraOptions.setItems(options);
        cbCameraOptions.setPromptText(cameraListPromptText);
        cbCameraOptions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<WebCamInfo>() {

            @Override
            public void changed(ObservableValue<? extends WebCamInfo> arg0, WebCamInfo arg1, WebCamInfo arg2) {
                if (arg2 != null) {
                    System.out.println("WebCam Index: " + arg2.getWebCamIndex() + ": WebCam Name:" + arg2.getWebCamName());
                    initializeWebCam(arg2.getWebCamIndex());
                }
            }
        });
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                setImageViewSize();
            }
        });


        /*
        <TAKIM NO>,<PAKET NUMARASI>,<GÖNDERME SAATİ>,<BASINÇ>,<YÜKSEKLİK>,<İNİŞ HIZI>,
<SICAKLIK>,<PİL GERİLİMİ>,<GPS LATITUDE>,<GPS LONGITUDE>,<GPS ALTITUDE>,<UYDU
STATÜSÜ>
           <2018>,<1>,<12021997>,<12>,<13.32>,<14.42>,<15>,<550>,<13.047>,<17.450>,<42.400>,<0>
         */

/*
        String feed = "<7893,"+str_packageno+","+","+year+"."+str_month+"."+str_day+"/"+str_hour+"."+
 */



    }

    protected void setImageViewSize() {


        imgWebCamCapturedImage.setFitHeight(190);
        imgWebCamCapturedImage.setFitWidth(290);
        imgWebCamCapturedImage.prefHeight(190);
        imgWebCamCapturedImage.prefWidth(290);
        imgWebCamCapturedImage.setPreserveRatio(true);

    }

    protected void initializeWebCam(final int webCamIndex) {

        Task<Void> webCamIntilizer = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                if (selWebCam == null) {
                    selWebCam = Webcam.getWebcams().get(webCamIndex);
                    selWebCam.open();
                } else {
                    closeCamera();
                    selWebCam = Webcam.getWebcams().get(webCamIndex);
                    selWebCam.open();
                }
                startWebCamStream();
                return null;
            }

        };

        new Thread(webCamIntilizer).start();
        btnStartCamera.setDisable(true);
    }

    protected void startWebCamStream() {
        File file = new File("output.mp4");
        // -> -> -> -> -> -> -> ->  > -> -> -> >


        if(file == null) {
            System.out.println("error");
            exit(2);
        }

       IMediaWriter writer = ToolFactory.makeWriter(file.getName());
        writer.setMaskLateStreamExceptions(true); // KAPATMIYOSA -->

       /* vidStop.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                writer.close();
            }
        });
        */

       Dimension size = WebcamResolution.QQVGA.getSize(); // SUNA BAKMAM LAZIM KESIN YANLIS

        //Dimension size = new Dimension(190,290);

       writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);




        stopCamera = false;
        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                //while(!stopcam)
                long start = System.currentTimeMillis();

                for(int i=0;!stopCamera;i++) {
                    try {
                            if ((grabbedImage = selWebCam.getImage()) != null) {

                            int finalI = i;

                            Platform.runLater(new Runnable() {

                                @Override
                                public void run() {

                                    BufferedImage image = ConverterFactory.convertToType(selWebCam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
                                    IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);

                                    IVideoPicture frame = converter.toPicture(image,(System.currentTimeMillis() - start) * 1000);
                                    frame.setKeyFrame(finalI == 0);
                                    frame.setQuality(0);

                                    writer.encodeVideo(finalI, frame);


                                    final Image mainiamge = SwingFXUtils
                                            .toFXImage(grabbedImage, null);
                                    imageProperty.set(mainiamge);
                                    // BURAYI SILMEK GEREKIR MI BİLMIYOM
                                    try {
                                        sleep(30);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                            grabbedImage.flush();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                writer.setForceInterleave(false);
//                writer.setForceInterleave(false);



                return null;
            }

        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        imgWebCamCapturedImage.imageProperty().bind(imageProperty);

    }

    private void closeCamera() {
        if (selWebCam != null) {
            selWebCam.close();
        }
    }

    public void stopCamera(ActionEvent event) {
        stopCamera = true;
        btnStartCamera.setDisable(false);
        btnStopCamera.setDisable(true);
    }

    public void startCamera(ActionEvent event) {
        stopCamera = false;
        startWebCamStream();
        btnStartCamera.setDisable(true);
        btnStopCamera.setDisable(false);
    }

    public void disposeCamera(ActionEvent event) {
        stopCamera = true;
        closeCamera();
        btnStopCamera.setDisable(true);
        btnStartCamera.setDisable(true);
    }





    void fiilTest(LineChart<Number,Number> item,String color,int i){
        if(i==1){
            series1 = new XYChart.Series<>();
            item.getData().add(series1);
            series1.getNode().setStyle("-fx-stroke: " +color + ";");
            item.setLegendVisible(false);


        }
        if(i==2){
            series2 = new XYChart.Series<>();
            item.getData().add(series2);
            series2.getNode().setStyle("-fx-stroke: " +color + ";");
            item.setLegendVisible(false);
        }
        if(i==3){
            series3 = new XYChart.Series<>();
            item.getData().add(series3);
            series3.getNode().setStyle("-fx-stroke: " +color + ";");
            item.setLegendVisible(false);


        }
        if(i==4){
            series4 = new XYChart.Series<>();
            item.getData().add(series4);
            series4.getNode().setStyle("-fx-stroke: " +color + ";");
            item.setLegendVisible(false);
        }
        if(i==5){
            series5 = new XYChart.Series<>();
            item.getData().add(series5);
            series5.getNode().setStyle("-fx-stroke: " +color + ";");
            item.setLegendVisible(false);
        }

        // series1.setName("Series 1");

       /* series1.getData().add(new XYChart.Data<>(1, 2));
        series1.getData().add(new XYChart.Data<>(2, 3));
       */


    }

}