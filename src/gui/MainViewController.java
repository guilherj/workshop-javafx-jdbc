/*
 * TODAS AS EXPLICAÇÕES DE MACETES, MÉTODOS E COMO USA-LOS ESTÁ ABAIXO
 * 
 */

package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem MenuItemSeller;

	@FXML
	private MenuItem MenuItemDepartment;

	@FXML
	private MenuItem MenuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {
		loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
			controller.setSellerService(new SellerService());
			controller.updateTableView();
		}); 
	}

	
	/*
	 * Chamada do método loadView() passando como parâmetro o caminho da View que deve-se carregar
	 * que nesse caso é o DepartmentList.fxml
	 * 
	 * E passando uma expressão lambda onde passa uma função que recebe como parâmetro um objeto do tipo 
	 * DepartmentListController com nome controller, esse objeto chama o método setDepartmentService, esse método 
	 * instancia um new DepartmentService e depois chama o método updateTableView. toda essa função é passada como 
	 * parâmetro para o método loadView.
	 */
	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		}); 
		
	}

	@FXML
	public void onMenuItemAboutAction() {
		/*
		 * Chamada do método loadView() passando como parâmetro o caminho da View que deve-se carregar
		 * que nesse caso é o About.fxml
		 * 
		 * E passando uma expressão lambda onde passa uma função x que no abrir e fechar as aspas não executa nada.
		 */
		loadView("/gui/About.fxml", x ->{});
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO Auto-generated method stub

	}

	/*
	 *  MÉTODO PARA CARREGAR UMA TELA NA MAIN VIEW.
	 *
	 *  ADICIONAR A PALAVRA "synchronized" PARA EVITAR QUE O PROCESSAMENTO DO MÉTODO NÃO SEJA INTERROMPIDO
	 *  DURANTO O PROCESSO.
	 *  
	 *  o método recebe o caminho da view que deve carregar (String absoluteName), e recebe um objeto consumer do tipo T 
	 *  qualquer que será uma função para executar qualquer lógica necessária para carregar os elementos de uma view.
	 */
	public synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {

		try {
			//Código para carregar a view que recebeu no parâmentro
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			// PEGANDO A REFERÊNCIA DA CENA PRINCIPAL
			Scene mainScene = Main.getMainScene();
			
			/*
			 * "MainsVBox" - Criando uma referência do VBox da MainView
			 * "mainsScene.getRoot()" - Pega o primeiro elemento da view, que nesse caso é o scroll Pane da Main View
			 * (Se olhar o MainView.fxml poderá ver isso);
			 * 
			 * "(ScrollPane) mainScene.getRoot()" - É um cast para que o compilador saiba que esta sendo pego
			 * um Scroll Pane pelo get.Root();
			 * 
			 * getContent já pega toda a referência de tudo que vem dentro do ScrollPane, incluindo os childerns da VBox
			 * da MainView, então coloca-se tudo isso dentro de um parênteses e faz um getContent() para pegar toda essa
			 * referência "((ScrollPane) mainScene.getRoot()).getContent()" e faz um cast para que o compilador saiba que
			 * está recebendo uma VBox "(VBox) ((ScrollPane) mainScene.getRoot()).getContent()"
			 * 
			 */
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			/*
			 * "getChildren().get(0)" - Pega o primeiro filho da VBox mainVBox que está na MainView, que nesse caso é o 
			 * Menu Bar e guarda na variável mainMenu do tipo Node.
			 * 
			 */
			Node mainMenu = mainVBox.getChildren().get(0);
			
			/*
			 * Agora para conseguir colocar os childerns de uma tela e conseguir preservar o elemento da Main View 
			 * principal tem que apagar os childrens com o comando "getChildern().clean()" mais como guardamos toda
			 * a informação do conteúdo da VBox nas referências anteriores podemos apagar e adicionar posteriormente.
			 * 
			 * O código "getChildren().add()" adiciona as referências que guardamos anteriormente passando eles como
			 * parâmetros dentro dos parênteses, e o código "getChildren().addAll()" adiciona todo um
			 * elemento que é passado como parâmetro dentro dos parênteses, que nesse caso é o newVBox instanciado lá em
			 * cima no inicio desse método. Já a newVBox recebe o contéudo que está dentro de loader, esse
			 * loader recebe o recurso  que é passado pelo parâmetro do método.
			 */
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
									
			/*
			 * o loader.getController() pega o controller do tipo T qualquer, passado pelo parâmetro da função
			 * loadView e carrega no objeto controller do tipo T qualquer. 
			 * 
			 * O objeto initializingAction.accept() executará a função que será passada no argumento da chamada deste método
			 * e mandará o controller recebido na linha de cima como argumento.
			 * 
			 */
			T controller = loader.getController();
			initializingAction.accept(controller);

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);

		}
	}
	
	

}
