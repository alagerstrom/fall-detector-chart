package se.andreaslagerstrom.falldetectorchart;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class App extends Application {

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
            for (Double d : data){
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

        menuBox.getChildren().add(refreshButton);
        menuBox.getChildren().add(sortByDateButton);
        menuBox.getChildren().add(sortByTypeButton);
        menuBox.getChildren().add(sortByOsButton);
        menuBox.getChildren().add(showAverageFall);
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
                vBox.setAlignment(Pos.TOP_CENTER);
                vBox.getChildren().add(new Label(fall.getOperatingSystem()));
                vBox.getChildren().add(new Label(fall.getDevice()));
                if (fall.getDate() != null)
                    vBox.getChildren().add(new Label(fall.getDate().getIso()));
                vBox.getChildren().add(new Label("Type: " + fall.getClassificationType()));
                vBox.getChildren().add(new Label("Impact start: " + fall.getImpactStart()));
                vBox.getChildren().add(new Label("Impact end: " + fall.getImpactEnd()));
                vBox.getChildren().add(new Label("Impact duration: " + fall.getImpactDuration()));
                vBox.getChildren().add(new Label("Average acceleration: " + fall.getAverageAcceleration()));

                LineChart lineChart = buildGraph(fall);
                lineChart.setPrefHeight(1000);
                vBox.getChildren().add(lineChart);
                tab.setContent(vBox);
                tabPane.getTabs().add(tab);
            }
        }
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

        XYChart.Series impactThreshold = new XYChart.Series<>();
        XYChart.Series upperMotionlessThreshold = new XYChart.Series<>();
        XYChart.Series lowerMotionlessThreshold = new XYChart.Series<>();

        impactThreshold.setName("Impact threshold");
        upperMotionlessThreshold.setName("Upper motionless threshold");
        lowerMotionlessThreshold.setName("Lower motionless threshold");

        impactThreshold.getData().add(new XYChart.Data<>(0, 3));
        impactThreshold.getData().add(new XYChart.Data<>(8500, 3));
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
        lineChart.getData().add(impactThreshold);
        lineChart.getData().add(upperMotionlessThreshold);
        lineChart.getData().add(lowerMotionlessThreshold);
        return lineChart;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
