package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

/*
 * Essa classe implementa a interface DataChangeListener pois ela sera classe que ouvir� (Observer) e recebr� a notifica��o
 * da a��o feita pela classe subject que nesse caso � o SellerFormController.
 */
public class SellerListController implements Initializable, DataChangeListener {

	/*
	 * declarando a depend�ncia da classe controller com a classe service de
	 * Seller mais sem injetar a depen�ncia dentro da classe, abaixo � criado a
	 * fun��o setSellerService para poder injetar essa depend�ncia em outro
	 * lugar no programa.
	 * 
	 * Isso � um principio de acoplamento forte, muito recomendado de ser feito no
	 * java, caso tenha d�vidas sobre esse assunto assistir a aula do curso Java
	 * Completo do Prof N�lio na Udemy no m�dulo de interfaces que � explicado esse
	 * conceito l�.
	 */
	private SellerService service;

	@FXML
	private TableView<Seller> tableViewSeller;

	@FXML
	private TableColumn<Seller, Integer> tableColumnId;

	@FXML
	private TableColumn<Seller, String> tableColumnName;
	
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;

	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;

	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;

	@FXML
	private Button btNew;

	@FXML
	public void onbtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event); // Captando o palco que fez a chamada.

		/*
		 * Por ser um bot�o para cadastrar um novo Seller instancia-se um objeto
		 * vazio e passa ele como argumento na chamada da tela.
		 */
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);

	}

	/*
	 * List do Java FX, pode ser usado para receber os dados de um list comum e
	 * depois carregar numa tableview.
	 */
	private ObservableList<Seller> obsList;

	// Criado o setSellerSet para injetar a depen�ncia do servi�o em outro lugar
	// no programa.
	public void setSellerService(SellerService service) {
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
		
		// Passando a vari�vel da classe para as propriedades das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		
		/*
		 * Ap�s determinar as propriedades da tablecolumn e associando com a vari�vel da classe
		 * chamar o m�todo da classe Utils para formatar a coluna com data passando nos par�metros
		 * o nome da tablecolumn e a m�scara de date que deseja usar. 
		 */
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		
		/*
		 * Ap�s determinar as propriedades da tablecolumn e associando com a vari�vel da classe
		 * chamar o m�todo da classe Utils para formatar a coluna com valor tipo double passando nos par�metros
		 * o nome da tablecolumn e quantas casas decimais deseja que saia na coluna. 
		 */
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		

		/*
		 * Macete para que a tableView que nesse caso � a tableViewSeller, acompanhe
		 * o tamanho da janela que nesse caso � a janela Main
		 */
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());

	}

	/*
	 * M�todo que servir� para acessar o service, pegar a lista e carregar a mesma
	 * na ObservableList declarada mais acima.
	 * 
	 * o objetivo � carregar a lista no observableList e depois esse observableList
	 * carregar na tableView, para que os dados possam ser mostrados na view do
	 * SellerList
	 */
	public void updateTableView() {

		/*
		 * Essa exce��o � para proteger o m�todo caso o programador esque�a de declarar
		 * a inje��o de depend�ncia do servico que nesse caso � o setSellerService,
		 * a exce��o ser� lan�ada de prop�sito para que o programador veja que esque�eu.
		 */
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		// acessou a lista do service, carregou no obsList e setou os itens no
		// tableViewSeller
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsList);
		initEditButtons(); // Chamando o m�todo respons�vel por criar um bot�o edit em cada linha da tabela Seller.
		initRemoveButtons(); // Chamando o m�todo respons�vel por criar um bot�o remove em cada linha da tabela Seller.

		// Agora tem que chamar esse m�todo, isso ser� feito l� na MainViewController
	}

	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
//
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//			Pane pane = loader.load();
//
//			// Pegando o controller da tela que carregou na vari�vel loader
//			SellerFormController controller = loader.getController();
//
//			// Injetando a depend�ncia da classe entidade no controller da view
//			controller.setSeller(obj);
//
//			// Injetando a depend�ncia do servi�o no controller da view
//			controller.setSellerService(new SellerService());
//
//			/*
//			 * Inscrevendo a pr�pria classe (DeparmentListController) na lista de
//			 * notifica��es de evento implementada na classe SellerFormController, ao
//			 * receber a notifica��o � executado o m�todo da interface onDataChange()
//			 * implementado mais abaixo
//			 */
//			controller.subscribeDataChangeListener(this);
//
//			// Carregando os dados do objeto para o formul�rio
//			controller.updateFormData();
//
//			/*
//			 * Quando for instanciar uma janela de dialog modal na frente de outra janela
//			 * deve-se instanciar um novo Stage, ser� um palco na frente do outro.
//			 */
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Enter Seller data");
//
//			dialogStage.setScene(new Scene(pane)); // Carrega uma nova cena com a view que foi carregada na variavel
//													// pane
//
//			dialogStage.setResizable(false); // A Janela n�o pode ser redimensionada.
//
//			dialogStage.initOwner(parentStage); // Indica quem � a cena pai desse dialog.
//
//			dialogStage.initModality(Modality.WINDOW_MODAL); // Indica se a janela � modal ou n�o, ou seja,
//			// Se for modal n�o � possivel clica na janela de baixo enquanto a janela modal
//			// estiver aberta.
//			dialogStage.showAndWait();
//
//		} catch (IOException e) {
//			Alerts.showAlert("IO Exception", "Error load View", e.getMessage(), AlertType.ERROR);
//		}
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
	 * M�todo que serve para criar um bot�o Edit em cada linha da tabela de
	 * Seller Ao clicar no bot�o ele vai carregar o formul�rio de Seller
	 * para edita-lo usando o m�todo criado anteriormente createDialogForm.
	 * 
	 */
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	/*
	 * M�todo para criar um button com nome "remove" , ao clicar no bot�o ele vai chamar o m�todo
	 * removeEntity para fazer a remova��o do objeto.
	 */
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj)); // Chamada do m�todo removeEntity
			}
		});
	}

	// M�todo para remover uma entidade.
	public void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		/*
		 *  Se o usu�rio clicar no bot�o OK do alert confirmation, ou seja
		 *  confirmar a dele��o do objeto, deve-se chamar o service.remove
		 *  depois de deletar chamar o updateTableView para atualizar a tableview
		 */		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Service was null");
			}
			
			try {
			service.remove(obj);
			updateTableView();
			
			}catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
