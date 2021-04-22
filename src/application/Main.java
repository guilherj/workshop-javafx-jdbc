package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {
		
	// Expondo uma refer�ncia (como atributo) da cena da janela principal.
	private static Scene mainScene;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load();
			
			/*
			 * Macete para ajustar via c�digo o scroll pane na janela.
			 * Assim pode redimensionar a janela o quanto quiser que o scroll pane
			 * Vai ocupar as bordas da forma correta.
			 */
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			
			// CENA PRINCIPAL
			mainScene = new Scene(scrollPane);
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Sample JavaFX application");
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/*
	 *  M�todo get do atributo da cena principal pois o atributo est� como private
	 *  Por Boas Pr�ticas da programa��o onde todo atributo deve ser private.
	 */	
	public static Scene getMainScene() {
		return mainScene;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
