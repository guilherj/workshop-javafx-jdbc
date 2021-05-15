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
 * Essa classe implementa a interface DataChangeListener pois ela sera classe que ouvirá (Observer) e recebrá a notificação
 * da ação feita pela classe subject que nesse caso é o SellerFormController.
 */
public class SellerListController implements Initializable, DataChangeListener {

	/*
	 * declarando a dependência da classe controller com a classe service de
	 * Seller mais sem injetar a depenência dentro da classe, abaixo é criado a
	 * função setSellerService para poder injetar essa dependência em outro
	 * lugar no programa.
	 * 
	 * Isso é um principio de acoplamento forte, muito recomendado de ser feito no
	 * java, caso tenha dúvidas sobre esse assunto assistir a aula do curso Java
	 * Completo do Prof Nélio na Udemy no módulo de interfaces que é explicado esse
	 * conceito lá.
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
		 * Por ser um botão para cadastrar um novo Seller instancia-se um objeto
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

	// Criado o setSellerSet para injetar a depenência do serviço em outro lugar
	// no programa.
	public void setSellerService(SellerService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	/*
	 * método auxiliar que serve como macete para inicializar apropriadamente as
	 * colunas da table view pois sem isso as colunas não irão funcionar como se
	 * deve, após criar o método deve-se chama-lo no método initialize()
	 */
	private void initializeNodes() {
		
		// Passando a variável da classe para as propriedades das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		
		/*
		 * Após determinar as propriedades da tablecolumn e associando com a variável da classe
		 * chamar o método da classe Utils para formatar a coluna com data passando nos parâmetros
		 * o nome da tablecolumn e a máscara de date que deseja usar. 
		 */
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		
		/*
		 * Após determinar as propriedades da tablecolumn e associando com a variável da classe
		 * chamar o método da classe Utils para formatar a coluna com valor tipo double passando nos parâmetros
		 * o nome da tablecolumn e quantas casas decimais deseja que saia na coluna. 
		 */
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		

		/*
		 * Macete para que a tableView que nesse caso é a tableViewSeller, acompanhe
		 * o tamanho da janela que nesse caso é a janela Main
		 */
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());

	}

	/*
	 * Método que servirá para acessar o service, pegar a lista e carregar a mesma
	 * na ObservableList declarada mais acima.
	 * 
	 * o objetivo é carregar a lista no observableList e depois esse observableList
	 * carregar na tableView, para que os dados possam ser mostrados na view do
	 * SellerList
	 */
	public void updateTableView() {

		/*
		 * Essa exceção é para proteger o método caso o programador esqueça de declarar
		 * a injeção de dependência do servico que nesse caso é o setSellerService,
		 * a exceção será lançada de propósito para que o programador veja que esqueçeu.
		 */
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		// acessou a lista do service, carregou no obsList e setou os itens no
		// tableViewSeller
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsList);
		initEditButtons(); // Chamando o método responsável por criar um botão edit em cada linha da tabela Seller.
		initRemoveButtons(); // Chamando o método responsável por criar um botão remove em cada linha da tabela Seller.

		// Agora tem que chamar esse método, isso será feito lá na MainViewController
	}

	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
//
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//			Pane pane = loader.load();
//
//			// Pegando o controller da tela que carregou na variável loader
//			SellerFormController controller = loader.getController();
//
//			// Injetando a dependência da classe entidade no controller da view
//			controller.setSeller(obj);
//
//			// Injetando a dependência do serviço no controller da view
//			controller.setSellerService(new SellerService());
//
//			/*
//			 * Inscrevendo a própria classe (DeparmentListController) na lista de
//			 * notificações de evento implementada na classe SellerFormController, ao
//			 * receber a notificação é executado o método da interface onDataChange()
//			 * implementado mais abaixo
//			 */
//			controller.subscribeDataChangeListener(this);
//
//			// Carregando os dados do objeto para o formulário
//			controller.updateFormData();
//
//			/*
//			 * Quando for instanciar uma janela de dialog modal na frente de outra janela
//			 * deve-se instanciar um novo Stage, será um palco na frente do outro.
//			 */
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Enter Seller data");
//
//			dialogStage.setScene(new Scene(pane)); // Carrega uma nova cena com a view que foi carregada na variavel
//													// pane
//
//			dialogStage.setResizable(false); // A Janela não pode ser redimensionada.
//
//			dialogStage.initOwner(parentStage); // Indica quem é a cena pai desse dialog.
//
//			dialogStage.initModality(Modality.WINDOW_MODAL); // Indica se a janela é modal ou não, ou seja,
//			// Se for modal não é possivel clica na janela de baixo enquanto a janela modal
//			// estiver aberta.
//			dialogStage.showAndWait();
//
//		} catch (IOException e) {
//			Alerts.showAlert("IO Exception", "Error load View", e.getMessage(), AlertType.ERROR);
//		}
	}

	/*
	 * Método da interface que servirá para executar a ação necessária após o evento
	 * ser executado pela classe subject e essa classe que é a observer ser
	 * notificada
	 */
	@Override
	public void onDataChange() {

		// Ação necessária quando a notificação do evento executado é disparada.
		updateTableView();

	}

	/*
	 * Método que serve para criar um botão Edit em cada linha da tabela de
	 * Seller Ao clicar no botão ele vai carregar o formulário de Seller
	 * para edita-lo usando o método criado anteriormente createDialogForm.
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
	 * Método para criar um button com nome "remove" , ao clicar no botão ele vai chamar o método
	 * removeEntity para fazer a removação do objeto.
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
				button.setOnAction(event -> removeEntity(obj)); // Chamada do método removeEntity
			}
		});
	}

	// Método para remover uma entidade.
	public void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		/*
		 *  Se o usuário clicar no botão OK do alert confirmation, ou seja
		 *  confirmar a deleção do objeto, deve-se chamar o service.remove
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
