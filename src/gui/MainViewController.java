/*
 * TODAS AS EXPLICA��ES DE MACETES, M�TODOS E COMO USA-LOS EST� ABAIXO
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
	 * Chamada do m�todo loadView() passando como par�metro o caminho da View que deve-se carregar
	 * que nesse caso � o DepartmentList.fxml
	 * 
	 * E passando uma express�o lambda onde passa uma fun��o que recebe como par�metro um objeto do tipo 
	 * DepartmentListController com nome controller, esse objeto chama o m�todo setDepartmentService, esse m�todo 
	 * instancia um new DepartmentService e depois chama o m�todo updateTableView. toda essa fun��o � passada como 
	 * par�metro para o m�todo loadView.
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
		 * Chamada do m�todo loadView() passando como par�metro o caminho da View que deve-se carregar
		 * que nesse caso � o About.fxml
		 * 
		 * E passando uma express�o lambda onde passa uma fun��o x que no abrir e fechar as aspas n�o executa nada.
		 */
		loadView("/gui/About.fxml", x ->{});
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO Auto-generated method stub

	}

	/*
	 *  M�TODO PARA CARREGAR UMA TELA NA MAIN VIEW.
	 *
	 *  ADICIONAR A PALAVRA "synchronized" PARA EVITAR QUE O PROCESSAMENTO DO M�TODO N�O SEJA INTERROMPIDO
	 *  DURANTO O PROCESSO.
	 *  
	 *  o m�todo recebe o caminho da view que deve carregar (String absoluteName), e recebe um objeto consumer do tipo T 
	 *  qualquer que ser� uma fun��o para executar qualquer l�gica necess�ria para carregar os elementos de uma view.
	 */
	public synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {

		try {
			//C�digo para carregar a view que recebeu no par�mentro
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			// PEGANDO A REFER�NCIA DA CENA PRINCIPAL
			Scene mainScene = Main.getMainScene();
			
			/*
			 * "MainsVBox" - Criando uma refer�ncia do VBox da MainView
			 * "mainsScene.getRoot()" - Pega o primeiro elemento da view, que nesse caso � o scroll Pane da Main View
			 * (Se olhar o MainView.fxml poder� ver isso);
			 * 
			 * "(ScrollPane) mainScene.getRoot()" - � um cast para que o compilador saiba que esta sendo pego
			 * um Scroll Pane pelo get.Root();
			 * 
			 * getContent j� pega toda a refer�ncia de tudo que vem dentro do ScrollPane, incluindo os childerns da VBox
			 * da MainView, ent�o coloca-se tudo isso dentro de um par�nteses e faz um getContent() para pegar toda essa
			 * refer�ncia "((ScrollPane) mainScene.getRoot()).getContent()" e faz um cast para que o compilador saiba que
			 * est� recebendo uma VBox "(VBox) ((ScrollPane) mainScene.getRoot()).getContent()"
			 * 
			 */
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			/*
			 * "getChildren().get(0)" - Pega o primeiro filho da VBox mainVBox que est� na MainView, que nesse caso � o 
			 * Menu Bar e guarda na vari�vel mainMenu do tipo Node.
			 * 
			 */
			Node mainMenu = mainVBox.getChildren().get(0);
			
			/*
			 * Agora para conseguir colocar os childerns de uma tela e conseguir preservar o elemento da Main View 
			 * principal tem que apagar os childrens com o comando "getChildern().clean()" mais como guardamos toda
			 * a informa��o do conte�do da VBox nas refer�ncias anteriores podemos apagar e adicionar posteriormente.
			 * 
			 * O c�digo "getChildren().add()" adiciona as refer�ncias que guardamos anteriormente passando eles como
			 * par�metros dentro dos par�nteses, e o c�digo "getChildren().addAll()" adiciona todo um
			 * elemento que � passado como par�metro dentro dos par�nteses, que nesse caso � o newVBox instanciado l� em
			 * cima no inicio desse m�todo. J� a newVBox recebe o cont�udo que est� dentro de loader, esse
			 * loader recebe o recurso  que � passado pelo par�metro do m�todo.
			 */
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
									
			/*
			 * o loader.getController() pega o controller do tipo T qualquer, passado pelo par�metro da fun��o
			 * loadView e carrega no objeto controller do tipo T qualquer. 
			 * 
			 * O objeto initializingAction.accept() executar� a fun��o que ser� passada no argumento da chamada deste m�todo
			 * e mandar� o controller recebido na linha de cima como argumento.
			 * 
			 */
			T controller = loader.getController();
			initializingAction.accept(controller);

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);

		}
	}
	
	

}
