package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	// INJETANDO DEPENDENCIA DA CLASSE SELLER
	private Seller entity;

	// Injetando Dependência da classe SellerService
	private SellerService service;

	// Injetando Dependência da classe DepartmentService
	private DepartmentService departmentService;

	// Lista criada para que os objetos possam se inscrever e possam ouvir quando um
	// evento de seu interesse for feito nesse objeto.
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtBaseSalary;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	// Lista do JavaFX para carregar na comboBox
	private ObservableList<Department> obsList;

	// MÉTODOS PARA INJETAR AS DEPENDÊNCIAS DAS CLASSES EM QUALQUER LUGAR DO
	// PROGRAMA.
	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	/*
	 * Método para que outros objetos possam se inscrever na lista.
	 * 
	 * Assim outros objetos, desde que implementem a interface DataChangeListener,
	 * conseguem se increver na lista para receber o evento desta classe
	 */
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	/*
	 * Necessário passar um ActionEvent como parâmetro para pegar a ação de apertar
	 * o botão save e passar essa ação ao método currentStage usado mais abaixo na
	 * hora de fechar a janela do formulário.
	 */
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		/*
		 * Programação defensiva necessária para que, caso tenha esquecido de injetar a
		 * dependência da entidade e do serviço seja lançada uma exceção.
		 */
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}

		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		/*
		 * PROGRAMAÇÃO DEFENSIVA (Necessário de se fazer) Como estamos fazendo uma
		 * operação com banco de dados essa operação pode lançar uma exceção então
		 * coloco toda a operação dentro de um try/catch e lanço um alert mostrando a
		 * mensagem do erro
		 */
		try {

			// Pegando os dados do formuário e inserindo no objeto entity
			entity = getFormData();

			// Chamando o método para salvar o objeto com os dados do formulário dentro do
			// BD
			service.saveOrUpdate(entity);

			/*
			 * Chamada do método para que quando for feita a operação de salvar o objeto no
			 * banco de dados os objetos inscritos na lista dataChangeListeners possam ser
			 * notificados dessa ação.
			 */
			notifyDataChangeListeners();

			/*
			 * Pegando a referência do palco que fez a chamada do formulário e usando a
			 * função close para fechar a janela quando concluir a operação de salvar o
			 * objeto.
			 */
			Utils.currentStage(event).close();

			/*
			 * Agora o método getFormData() pode lançar uma ValidationException então agora
			 * temos que tratar dessa exception por um novo catch. E nesse tratamento vamos
			 * lançar o método setErrorMessage que foi implementado mais abaixo na classe.
			 */
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());

		} catch (DbException e) {
			Alerts.showAlert("Error Saving Object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	/*
	 * Método responsável por notificar os objetos da lista dataChangeListeners que
	 * uma ação de seu interesse foi executada. E como os objetos são notificados?
	 * Executando o método onDataChanged() da interface DataChangeListener
	 * 
	 * Por isso essa classe SellerFormController é chamada de subject, por que ela é
	 * uma classe que emite o evento e apenas implementa o método da interface mais
	 * não assina o contrato da mesma.
	 */
	private void notifyDataChangeListeners() {

		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChange();
		}

	}

	// Método para pegar as informações preenchidas no formulário e setar no objeto
	private Seller getFormData() {
		Seller obj = new Seller();

		// Instanciando a classe de ValidationException dentro do método.
		ValidationException exception = new ValidationException("Validation Error");

		// Como o txtId recebe um String faz se a conversão para Integer usando o método
		// auxiliar criado anteriormente no pacote Utils
		obj.setId(Utils.tryParseToInt(txtId.getText()));

		/*
		 * Fazendo a verificação no textField txtName, nessa verificação o textFields
		 * não pode ficar vazio.
		 *
		 * trim() -> Está eliminando todo espaço vazio no inicio e no final do TextField
		 * equals("") -> O equals com as aspas dentro dos parênteses verifica se o text
		 * Fileds está em branco.
		 * 
		 *** Lendo o If: Se o txtname estiver valendo nulo ou se estiver vazio adiciona o
		 * erro na coleção de erros da classe ValidationException.
		 * 
		 */
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText()); // Setando o nome, mesmo vazio.

		// Vale a mesma explicação que está no txtName
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addError("email", "Field can't be empty");
		}
		obj.setEmail(txtEmail.getText());

		/*
		 * Pegando a data do DatePicker que está no formato default do computador do
		 * usuário e convertendo para um Instant que é o formato independente de
		 * formato.
		 * 
		 * Deve-se testar também se ao clicar no botão save e for feita a instanciação
		 * do objeto se o DatePicker não for null, ou seja se foi selecionada uma data,
		 * se for null adicionar o erro na coleção de erros da classe
		 * ValidationException.
		 */
		if (dpBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field can't be empty");
		} else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant)); // Convertendo um objeto do tipo instant para Date
		}

		// Vale a mesma explicação que está no txtName
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			exception.addError("baseSalary", "Field can't be empty");
		}

		/*
		 * Como o txtId recebe um String faz se a conversão para Double usando o método
		 * auxiliar criado anteriormente no pacote Utils
		 */
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		
		obj.setDepartment(comboBoxDepartment.getValue());

		/*
		 * testando se foi adicionado algum erro na coleção de erros, se tiver sido
		 * adicionado algum e o tamanho for maior que 0 lança a exceção.
		 */
		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	// Esse método é chamado pelo método initialize para sempre que a view for
	// carregada já iniciar com a
	// Codificação especificada nele.
	public void initializeNodes() {

		/*
		 * Chamada das funções para parametrizar os campos a serem preenchidos pelos
		 * usuarios como quantos caractéres determinado textfield pode receber ao
		 * máximo, o txtBaseSalary só receber números do tipo double e qual máscara
		 * aceitar receber o DatePicker dpBirthDate
		 * 
		 */
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");

		initializeComboBoxDepartment(); // Chamando o método que inicializa a comboBoxDepartment
	}

	// Método para passar os valores do objeto da classe para os Text Fields
	public void updateFormData() {

		// programação defensiva para caso a dependência da classe não tenha sido
		// injetada.
		if (entity == null) {
			throw new IllegalStateException("Entity was null!");
		}

		// Como o textFied trabalha com texto é necessário converter o Id que é integer
		// para String.
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));

		/*
		 * Forma de mostrar na tela a data no formato da maquina do usuário, pois assim
		 * independente de localidade e como a data é mostrada nessa localidade, o
		 * sistema sempre vai mostrar como é mostrado pelo computador do usuário.
		 * 
		 * Fazendo uma programação defensiva com o if para não estourar uma excessão
		 * NullPointerException Só fará a conversão para data local do usuário quando o
		 * dpBirthDate não estiver vazio, foi necessário fazer esse if pq quando
		 * apertava no botão new estourava a excessão.
		 */
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}

		// Caso esteja criando um novo Seller a comboBox irá exibir o primeiro elemento
		// da lista de Department.
		if (entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartment.setValue(entity.getDepartment());
		}

	}

	// Método para carregar a lista de Department na comboBoxDepartment, esse método
	// deve ser chamado quando a tela for carregada
	// Então deve-se chama-lo no controller que chamar a view desse controller, que
	// nesse caso é SellerListController no método
	// createdDialogForm()
	public void loadAssociatedObjects() {

		// Programação defensiva caso tenha esquecido de injetar a dependência do
		// service
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService was null");
		}

		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	// Método para setar os erros nas label's de erro no formulário, se houver
	private void setErrorMessages(Map<String, String> errors) {

		/*
		 * Percorrendo o Map e pegando todas as chaves que tiverem na coleção e passando
		 * para a váriavel fields que é do tipo Set, Set é uma outra coleção.
		 */
		Set<String> fields = errors.keySet();

		/*
		 * Verificando se em fields contém um erro com uma chave com nome especificado em errors.get() , se tiver setar na
		 * labelError correspondente a mensagem que estiver relacionada a chave da coleção Map, se não tiver chave nenhuma
		 * setar para o labelError "" que quer dizer vazio.
		 * 
		 *  Foi utilizado o operador ternário "?" que faz o mesmo teste de if em apenas uma linha, porém esse operador
		 *  só pode ser usado quando o if tiver apenas uma condição.
		 */
		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));
		labelErrorEmail.setText((fields.contains("email") ? errors.get("email") : ""));
		labelErrorBirthDate.setText((fields.contains("birthDate") ? errors.get("birthDate") : ""));
		labelErrorBaseSalary.setText((fields.contains("baseSalary") ? errors.get("baseSalary") : ""));

	}

	// Método para inicializar a comboBoxDepartment
	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

}
