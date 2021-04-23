/*
 * TODAS AS EXPLICA��ES DE MACETES, M�TODOS E COMO USA-LOS EST� ABAIXO
 * 
 */

package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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

public class MainViewController implements Initializable {

	@FXML
	private MenuItem MenuItemSeller;

	@FXML
	private MenuItem MenuItemDepartment;

	@FXML
	private MenuItem MenuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("TESTE MENU ITEM SELLER, FUNCIONANDO");
	}

	@FXML
	public void onMenuItemDepartmentAction() {
		loadView2("/gui/DepartmentList.fxml"); 
		/*
		 * Criado o metodo loadView2 somente para poder carregar os dados
		 * da list em DepartmentService na tableView enquanto n�o � feito a integra��o com o banco de dados.
		 * a implementa��o desse novo m�todo tempor�rio est� mais l� em baixo.
		 */
	}

	@FXML
	public void onMenuItemAboutAction() {
		/*
		 * Chamada do m�todo loadView() passando como par�metro o caminho da View que deve-se carregar
		 * que nesse caso � o About.fxml
		 */
		loadView("/gui/About.fxml");
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
	 */
	public synchronized void loadView(String absoluteName) {

		try {
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
			 */
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			/*
			 * ^
			 * ^
			 * ^ COMENT�RIO DO C�DIGO ACIMA
			 * O c�digo "getChildren().add()" adiciona as refer�ncias que guardamos anteriormente passando eles como
			 * par�metros dentro dos par�nteses, e o c�digo "getChildren().addAll()" adiciona todo um
			 * elemento que � passado como par�metro dentro dos par�nteses, que nesse caso � o newVBox instanciado l� em
			 * cima no inicio desse m�todo. J� a newVBox recebe o cont�udo que est� dentro de loader, esse
			 * loader recebe o recurso  que � passado pelo par�metro do m�todo.
			 */

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);

		}
	}
	
	public synchronized void loadView2(String absoluteName) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			/*
			 * Foi pego a refer�ncia do controller da view, injetado a depen�ncia do DepartmentService
			 * e feito a chamada do m�todo updateTableView que mostrar� os dados da List na tableView da 
			 * view DepartmentList
			 */ 
			DepartmentListController controller = loader.getController();
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);

		}
	}

}
