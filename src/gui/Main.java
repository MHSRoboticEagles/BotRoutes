package gui;

import io.BotConnector;
import io.FileLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static final double WIDTH  = 900;
    private static final double HEIGHT = 720;

    @Override
    public void start(Stage primaryStage) throws Exception{
        processCommandLine();

        Parent root = FXMLLoader.load(getClass().getResource("botroutes.fxml"));
        primaryStage.setTitle("Bot Routes");
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("Shutting down");
        try {
            BotConnector.runDisconnect();
        }
        catch (Exception ex){
            System.err.println(ex.getMessage());
        }
    }

    private void processCommandLine() {
        try {
            String homePath = getParameters().getNamed().get("homeFolder");
            if (homePath != null) {
                System.out.format("HomePath='%s'.\n", homePath);
                FileLoader.setHomeFolder(homePath);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
