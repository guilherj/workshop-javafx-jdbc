/*
 * TODAS AS EXPLICAÇÕES DE MACETES, MÉTODOS E COMO USA-LOS ESTÁ ABAIXO
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
		 * da list em DepartmentService na tableView enquanto não é feito a integração com o banco de dados.
		 * a implementação desse novo método temporário está mais lá em baixo.
		 */
	}

	@FXML
	public void onMenuItemAboutAction() {
		/*
		 * Chamada do método loadView() passando como parâmetro o caminho da View que deve-se carregar
		 * que nesse caso é o About.fxml
		 */
		loadView("/gui/About.fxml");
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
	 */
	public synchronized void loadView(String absoluteName) {

		try {
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
			 */
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			/*
			 * ^
			 * ^
			 * ^ COMENTÁRIO DO CÓDIGO ACIMA
			 * O código "getChildren().add()" adiciona as referências que guardamos anteriormente passando eles como
			 * parâmetros dentro dos parênteses, e o código "getChildren().addAll()" adiciona todo um
			 * elemento que é passado como parâmetro dentro dos parênteses, que nesse caso é o newVBox instanciado lá em
			 * cima no inicio desse método. Já a newVBox recebe o contéudo que está dentro de loader, esse
			 * loader recebe o recurso  que é passado pelo parâmetro do método.
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
			 * Foi pego a referência do controller da view, injetado a depenência do DepartmentService
			 * e feito a chamada do método updateTableView que mostrará os dados da List na tableView da 
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
