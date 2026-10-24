package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("tablero.fxml"));

        try{
            Scene scene = new Scene(fxmlLoader.load(), 500, 600);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {

        Application.launch(App.class, args);
    }

}
