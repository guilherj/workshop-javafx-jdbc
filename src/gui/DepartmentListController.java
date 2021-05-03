package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {
	
	/*
	 * declarando a dependência da classe controller com a classe service de department
	 * mais sem injetar a depenência dentro da classe, abaixo é criado a função setDepartmentService
	 * para poder injetar essa dependência em outro lugar no programa.
	 * 
	 * Isso é um principio de acoplamento forte, muito recomendado de ser feito no java,
	 * caso tenha dúvidas sobre esse assunto assistir a aula do curso Java Completo do Prof Nélio na Udemy
	 * no módulo de interfaces que é explicado esse conceito lá.
	 */
	private DepartmentService service;
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private Button btNew;
	
	@FXML
	public void onbtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event); // Captando o palco que fez a chamada.
		
		/* Por ser um botão para cadastrar um novo department instancia-se um objeto vazio e passa ele como argumento
		 * na chamada da tela.
		 */
		Department obj = new Department();
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
		
	}
	
	/*
	 * List do Java FX, pode ser usado para receber os dados de um list comum e depois
	 * carregar numa tableview. 
	 */
	private ObservableList<Department> obsList;
	
	// Criado o setDepartmentSet para injetar a depenência do serviço em outro lugar no programa.
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}

	/*
	 * método auxiliar que serve como macete para inicializar apropriadamente as colunas da table view
	 * pois sem isso as colunas não irão funcionar como se deve, após criar o método deve-se chama-lo no método 
	 * initialize()
	 */
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		
		/*
		 * Macete para que a tableView que nesse caso é a tableViewDepartment, acompanhe o tamanho da janela
		 * que nesse caso é a janela Main
		 */
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	/*
	 * Método que servirá para acessar o service, pegar a lista e carregar a mesma na ObservableList
	 * declarada mais acima.
	 * 
	 * o objetivo é carregar a lista no observableList e depois esse observableList carregar na tableView,
	 * para que os dados possam ser mostrados na view do DepartmentList
	 */
	public void updateTableView() {
		
		/*
		 * Essa exceção é para proteger o método caso o programador esqueça de declarar a injeção de dependência do
		 * servico que nesse caso é o setDepartmentService, a exceção será lançada de propósito para
		 * que o programador veja que esqueçeu.
		 */
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		// acessou a lista do service, carregou no obsList e setou os itens no tableViewDepartment
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
		
		// Agora tem que chamar esse método, isso será feito lá na MainViewController
	}
	
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			// Pegando o controller da tela que carregou na variável loader
			DepartmentFormController controller = loader.getController(); 
			
			// Injetando o objeto no controller da view
			controller.setDepartment(obj);
			
			// Carregando os dados do objeto para o formulário
			controller.updateFormData();
			
			/*
			 * Quando for instanciar uma janela de dialog modal na frente de outra janela
			 * deve-se instanciar um novo Stage, será um palco na frente do outro.
			 */
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			
			dialogStage.setScene(new Scene(pane)); //Carrega uma nova cena com a view que foi carregada na variavel pane
			
			dialogStage.setResizable(false); // A Janela não pode ser redimensionada.
			
			dialogStage.initOwner(parentStage); // Indica quem é a cena pai desse dialog.
			
			dialogStage.initModality(Modality.WINDOW_MODAL); // Indica se a janela é modal ou não, ou seja,
			// Se for modal não é possivel clica na janela de baixo enquanto a janela modal estiver aberta.
			dialogStage.showAndWait();
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error load View", e.getMessage(), AlertType.ERROR);
		}
	}

}
