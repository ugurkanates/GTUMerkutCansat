package ground.station;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Section;
import ground.station.SerialDevice.SensorData;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.CheckListView;

/**
 *
 * @author Siddhesh Rane
 */
public class SensorPlotter extends VBox implements Initializable {

    //Sensor DATA
    XYChart.Series<Double, Double> xGyro;
    XYChart.Series<Double, Double> yGyro;
    XYChart.Series<Double, Double> zGyro;
    XYChart.Series<Double, Double> xAcc;
    XYChart.Series<Double, Double> yAcc;
    XYChart.Series<Double, Double> zAcc;
    XYChart.Series<Double, Double> xAccFil;
    XYChart.Series<Double, Double> yAccFil;
    XYChart.Series<Double, Double> zAccFil;
    ObservableList<XYChart.Series<Double, Double>> data;

    @FXML
    LineChart<Double, Double> chart;
    @FXML
    NumberAxis time;
    @FXML
    NumberAxis magnitude;
    @FXML
    CheckBox includeZeroOnChart;
    @FXML
    private OrientationController xAngle;
    @FXML
    private OrientationController yAngle;
    @FXML
    private OrientationController zAngle;
    @FXML
    private OrientationController xAngle1;
    @FXML
    private OrientationController yAngle1;
    @FXML
    private OrientationController zAngle1;
    @FXML
    private Gauge deltaTime;
    @FXML
    private CheckListView<XYChart.Series<Double, Double>> filterList;
    private ObservableList<XYChart.Series<Double, Double>> filteredSeries;

    //Computed Sensor data
    private double xGyInt, yGyInt, zGyInt;

    public SensorPlotter() {
        xGyro = new XYChart.Series<>();
        xGyro.setName("Gyro X");
        yGyro = new XYChart.Series<>();
        yGyro.setName("Gyro Y");
        zGyro = new XYChart.Series<>();
        zGyro.setName("Gyro Z");
        xAcc = new XYChart.Series<>();
        xAcc.setName("Accelerometer X");
        yAcc = new XYChart.Series<>();
        yAcc.setName("Accelerometer Y");
        zAcc = new XYChart.Series<>();
        zAcc.setName("Accelerometer Z");
        xAccFil = new XYChart.Series<>();
        xAccFil.setName("Accelerometer X Filtered");
        yAccFil = new XYChart.Series<>();
        yAccFil.setName("Accelerometer Y Filtered");
        zAccFil = new XYChart.Series<>();
        zAccFil.setName("Accelerometer Z Filtered");
        data = FXCollections.observableArrayList(xGyro, yGyro, zGyro, xAcc, yAcc, zAcc, xAccFil, yAccFil, zAccFil);
        FXMLLoader loader = new FXMLLoader(SensorPlotter.class.getResource("Sensor.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(SensorPlotter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterList.setItems(data);
        filterList.getCheckModel().checkAll();
//        filteredSeries = data.filtered(filterList.getCheckModel().getSelectedItems()::contains);
        filteredSeries = FXCollections.observableArrayList(data);
        filterList.getCheckModel().getCheckedItems().addListener((Observable ob) -> {
            filteredSeries.setAll(filterList.getCheckModel().getCheckedItems());
        });
        chart.setData(filteredSeries);
        magnitude.forceZeroInRangeProperty().bind(includeZeroOnChart.selectedProperty());
        xAngle.setAngle(0);
        yAngle.setAngle(0);
        zAngle.setAngle(0);
        deltaTime.setSections(
                new Section(0, 5, Color.LIGHTGREEN),
                new Section(5, 10, Color.GREENYELLOW),
                new Section(10, 15, Color.ORANGE),
                new Section(15, 20, Color.RED));
        deltaTime.setSectionsVisible(true);
    }

}
