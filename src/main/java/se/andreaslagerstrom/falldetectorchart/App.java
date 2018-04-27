package se.andreaslagerstrom.falldetectorchart;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class App extends Application {

    private static final double IOS_IMPACT_THRESHOLD = 3.5;
    private static final double ANDROID_IMPACT_THRESHOLD = 2.5;


    private TabPane tabPane = new TabPane();
    private List<Fall> falls = new RequestHandler().makeRequest().getBody().getResults();

    public void start(Stage stage) {

        stage.setTitle("Fall detection chart");
        recreateTabs();

        VBox topBox = new VBox();
        HBox menuBox = new HBox();

        Button refreshButton = new Button("Refresh");

        Button sortByDateButton = new Button("Sort by date");
        Button sortByTypeButton = new Button("Sort by classification type");
        Button sortByOsButton = new Button("Sort by OS");
        Button showAverageFall = new Button("Show average fall");
        Button generateCsvButton = new Button("Generate CSV file");

        showAverageFall.setOnAction(event -> {

            List<Fall> fallFalls = new ArrayList<>();

            for (Fall fall : falls)
                if (fall.getClassificationType().equals(Fall.ClassificationType.FALL))
                    fallFalls.add(fall);

            Fall averageFall = new Fall();

            double[] data = new double[400];

            for (Fall fall : fallFalls) {
                for (int i = 0; i < fall.getData().size(); i++) {
                    data[i] += fall.getData().get(i) / fallFalls.size();
                }
            }

            List<Double> dataList = new ArrayList<>();
            for (Double d : data) {
                dataList.add(d);
            }

            averageFall.setData(dataList);

            List<Fall> fallList = new ArrayList<>();
            fallList.add(averageFall);

            falls = fallList;
            recreateTabs();
        });

        sortByOsButton.setOnAction(event -> {
            falls.sort(Comparator.comparing(Fall::getOperatingSystem));
            recreateTabs();
        });

        sortByDateButton.setOnAction(event -> {
            falls.sort(Comparator.comparing(o -> o.getDate().getIso()));
            recreateTabs();
        });

        sortByTypeButton.setOnAction(event -> {
            falls.sort(Comparator.comparing(Fall::getClassificationType));
            recreateTabs();
        });

        generateCsvButton.setOnAction(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            File dir = chooser.showDialog(null);
            if (dir != null)
                CSVGenerator.generateCsv(falls, dir);
        });

        menuBox.getChildren().add(refreshButton);
        menuBox.getChildren().add(sortByDateButton);
        menuBox.getChildren().add(sortByTypeButton);
        menuBox.getChildren().add(sortByOsButton);
        menuBox.getChildren().add(showAverageFall);
        menuBox.getChildren().add(generateCsvButton);
        topBox.getChildren().add(menuBox);

        refreshButton.setOnAction(event -> {
            falls = new RequestHandler().makeRequest().getBody().getResults();
            recreateTabs();
        });

        topBox.getChildren().add(tabPane);
        Scene scene = new Scene(topBox, 800, 600);

        stage.setScene(scene);
        stage.show();
    }

    private void recreateTabs() {
        tabPane.getTabs().clear();
        int index = 1;
        for (Fall fall : falls) {
            if (fall.getData() != null) {
                Tab tab = new Tab("Fall " + index++);
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);
                VBox leftBox = new VBox();
                VBox rightBox = new VBox();
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.TOP_CENTER);
                vBox.getChildren().add(hBox);
                hBox.getChildren().add(leftBox);
                hBox.getChildren().add(rightBox);
                addLabel(leftBox, rightBox, "Device: ", fall.getDevice());
                addLabel(leftBox, rightBox, "Operating system: ", fall.getOperatingSystem());
                if (fall.getDate() != null)
                    addLabel(leftBox, rightBox, "Date: ", fall.getDate().getIso());
                addLabel(leftBox, rightBox, "Impact start: ", fall.getImpactStart());
                addLabel(leftBox, rightBox, "Impact end: ", fall.getImpactEnd());
                addLabel(leftBox, rightBox, "Average acceleration: ", fall.getAverageAccelerationVariation());
                addLabel(leftBox, rightBox, "Impact duration: ", fall.getImpactDuration());
                addLabel(leftBox, rightBox, "Impact peak value: ", fall.getImpactPeakValue());
                addLabel(leftBox, rightBox, "Impact peak duration: ", fall.getImpactPeakDuration());
                addLabel(leftBox, rightBox, "Longest valley value: ", fall.getLongestValleyValue());
                addLabel(leftBox, rightBox, "Longest valley duration: ", fall.getLongestValleyDuration());
                addLabel(leftBox, rightBox, "Number of peaks prior to impact: ", fall.getNumberOfPeaksPriorToImpact());
                addLabel(leftBox, rightBox, "Number of valleys prior to impact: ", fall.getNumberOfValleysPriorToImpact());
                addLabel(leftBox, rightBox, "Classification type: ", fall.getClassificationType().name());
                addLabel(leftBox, rightBox, "Classification number: ", "" + fall.getClassificationType().getNumValue());

                LineChart lineChart = buildGraph(fall);
                lineChart.setPrefHeight(1000);
                vBox.getChildren().add(lineChart);
                tab.setContent(vBox);
                tabPane.getTabs().add(tab);
            }
        }
    }

    private void addLabel(VBox leftBox, VBox rightBox, String property, String value) {
        leftBox.getChildren().add(new Label(property));
        rightBox.getChildren().add(new Label(value));
    }

    @SuppressWarnings("unchecked")
    private static LineChart buildGraph(Fall fall) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("g");
        xAxis.setLabel("Index");
        final LineChart<Number, Number> lineChart =
                new LineChart<Number, Number>(xAxis, yAxis);

        //defining a series

        XYChart.Series androidImpactThreshold = new XYChart.Series<>();
        XYChart.Series iosImpactThreshold = new XYChart.Series<>();

        XYChart.Series upperMotionlessThreshold = new XYChart.Series<>();
        XYChart.Series lowerMotionlessThreshold = new XYChart.Series<>();

        androidImpactThreshold.setName("Android impact threshold");
        iosImpactThreshold.setName("iOS impact threshold");

        upperMotionlessThreshold.setName("Upper motionless threshold");
        lowerMotionlessThreshold.setName("Lower motionless threshold");

        androidImpactThreshold.getData().add(new XYChart.Data<>(0, ANDROID_IMPACT_THRESHOLD));
        androidImpactThreshold.getData().add(new XYChart.Data<>(8500, ANDROID_IMPACT_THRESHOLD));
        iosImpactThreshold.getData().add(new XYChart.Data<>(0, IOS_IMPACT_THRESHOLD));
        iosImpactThreshold.getData().add(new XYChart.Data<>(8500, IOS_IMPACT_THRESHOLD));
        upperMotionlessThreshold.getData().add(new XYChart.Data<>(0, 1.8));
        upperMotionlessThreshold.getData().add(new XYChart.Data<>(8500, 1.8));
        lowerMotionlessThreshold.getData().add(new XYChart.Data<>(0, 0.3));
        lowerMotionlessThreshold.getData().add(new XYChart.Data<>(8500, 0.3));

        XYChart.Series series = new XYChart.Series();
        series.setName("Fall");

        int i = 0;
        for (Double d : fall.getData()) {
            series.getData().add(new XYChart.Data<Number, Number>(i * 20, d));
            i++;
        }
        lineChart.getData().add(series);
        lineChart.getData().add(androidImpactThreshold);
        lineChart.getData().add(iosImpactThreshold);

        lineChart.getData().add(upperMotionlessThreshold);
        lineChart.getData().add(lowerMotionlessThreshold);
        return lineChart;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
