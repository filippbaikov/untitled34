package ClientPack;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
       FXMLLoader fxmlLoader=new FXMLLoader();
       Parent root=fxmlLoader.load(getClass().getResource("/chat.fxml").openStream());
       Controller controller=(Controller) fxmlLoader.getController();
        primaryStage.setTitle("ГОВОРУШКА");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setOnCloseRequest(event -> controller.sendCloseRequest());
        primaryStage.show();
    }
// окно чата( размер, название и т.д)

    public static void main(String[] args) {
        launch(args);
    }
}
