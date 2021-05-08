package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

/*
 * Essa classe implementa a interface DataChangeListener pois ela sera classe que ouvir� (Observer) e recebr� a notifica��o
 * da a��o feita pela classe subject que nesse caso � o DepartmentFormController.
 */
public class DepartmentListController implements Initializable, DataChangeListener {

	/*
	 * declarando a depend�ncia da classe controller com a classe service de
	 * department mais sem injetar a depen�ncia dentro da classe, abaixo � criado a
	 * fun��o setDepartmentService para poder injetar essa depend�ncia em outro
	 * lugar no programa.
	 * 
	 * Isso � um principio de acoplamento forte, muito recomendado de ser feito no
	 * java, caso tenha d�vidas sobre esse assunto assistir a aula do curso Java
	 * Completo do Prof N�lio na Udemy no m�dulo de interfaces que � explicado esse
	 * conceito l�.
	 */
	private DepartmentService service;

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;

	@FXML
	private Button btNew;

	@FXML
	public void onbtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event); // Captando o palco que fez a chamada.

		/*
		 * Por ser um bot�o para cadastrar um novo department instancia-se um objeto
		 * vazio e passa ele como argumento na chamada da tela.
		 */
		Department obj = new Department();
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);

	}

	/*
	 * List do Java FX, pode ser usado para receber os dados de um list comum e
	 * depois carregar numa tableview.
	 */
	private ObservableList<Department> obsList;

	// Criado o setDepartmentSet para injetar a depen�ncia do servi�o em outro lugar
	// no programa.
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	/*
	 * m�todo auxiliar que serve como macete para inicializar apropriadamente as
	 * colunas da table view pois sem isso as colunas n�o ir�o funcionar como se
	 * deve, ap�s criar o m�todo deve-se chama-lo no m�todo initialize()
	 */
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		/*
		 * Macete para que a tableView que nesse caso � a tableViewDepartment, acompanhe
		 * o tamanho da janela que nesse caso � a janela Main
		 */
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());

	}

	/*
	 * M�todo que servir� para acessar o service, pegar a lista e carregar a mesma
	 * na ObservableList declarada mais acima.
	 * 
	 * o objetivo � carregar a lista no observableList e depois esse observableList
	 * carregar na tableView, para que os dados possam ser mostrados na view do
	 * DepartmentList
	 */
	public void updateTableView() {

		/*
		 * Essa exce��o � para proteger o m�todo caso o programador esque�a de declarar
		 * a inje��o de depend�ncia do servico que nesse caso � o setDepartmentService,
		 * a exce��o ser� lan�ada de prop�sito para que o programador veja que esque�eu.
		 */
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		// acessou a lista do service, carregou no obsList e setou os itens no
		// tableViewDepartment
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
		initEditButtons(); // Chamando o m�todo respons�vel por criar um bot�o edit em cada linha da tabela Department.

		// Agora tem que chamar esse m�todo, isso ser� feito l� na MainViewController
	}

	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			// Pegando o controller da tela que carregou na vari�vel loader
			DepartmentFormController controller = loader.getController();

			// Injetando a depend�ncia da classe entidade no controller da view
			controller.setDepartment(obj);

			// Injetando a depend�ncia do servi�o no controller da view
			controller.setDepartmentService(new DepartmentService());

			/*
			 * Inscrevendo a pr�pria classe (DeparmentListController) na lista de
			 * notifica��es de evento implementada na classe DepartmentFormController, ao
			 * receber a notifica��o � executado o m�todo da interface onDataChange()
			 * implementado mais abaixo
			 */
			controller.subscribeDataChangeListener(this);

			// Carregando os dados do objeto para o formul�rio
			controller.updateFormData();

			/*
			 * Quando for instanciar uma janela de dialog modal na frente de outra janela
			 * deve-se instanciar um novo Stage, ser� um palco na frente do outro.
			 */
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");

			dialogStage.setScene(new Scene(pane)); // Carrega uma nova cena com a view que foi carregada na variavel
													// pane

			dialogStage.setResizable(false); // A Janela n�o pode ser redimensionada.

			dialogStage.initOwner(parentStage); // Indica quem � a cena pai desse dialog.

			dialogStage.initModality(Modality.WINDOW_MODAL); // Indica se a janela � modal ou n�o, ou seja,
			// Se for modal n�o � possivel clica na janela de baixo enquanto a janela modal
			// estiver aberta.
			dialogStage.showAndWait();

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error load View", e.getMessage(), AlertType.ERROR);
		}
	}

	/*
	 * M�todo da interface que servir� para executar a a��o necess�ria ap�s o evento
	 * ser executado pela classe subject e essa classe que � a observer ser
	 * notificada
	 */
	@Override
	public void onDataChange() {

		// A��o necess�ria quando a notifica��o do evento executado � disparada.
		updateTableView();

	}

	/*
	 *  M�todo que serve para criar um bot�o Edit em cada linha da tabela de Department
	 *  Ao clicar no bot�o ele vai carregar o formul�rio de Department para edita-lo usando o m�todo 
	 *  criado anteriormente createDialogForm.
	 *  
	 */	
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}

}
