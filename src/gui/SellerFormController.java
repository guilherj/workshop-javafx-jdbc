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

	// Injetando Depend�ncia da classe SellerService
	private SellerService service;

	// Injetando Depend�ncia da classe DepartmentService
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

	// M�TODOS PARA INJETAR AS DEPEND�NCIAS DAS CLASSES EM QUALQUER LUGAR DO
	// PROGRAMA.
	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	/*
	 * M�todo para que outros objetos possam se inscrever na lista.
	 * 
	 * Assim outros objetos, desde que implementem a interface DataChangeListener,
	 * conseguem se increver na lista para receber o evento desta classe
	 */
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	/*
	 * Necess�rio passar um ActionEvent como par�metro para pegar a a��o de apertar
	 * o bot�o save e passar essa a��o ao m�todo currentStage usado mais abaixo na
	 * hora de fechar a janela do formul�rio.
	 */
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		/*
		 * Programa��o defensiva necess�ria para que, caso tenha esquecido de injetar a
		 * depend�ncia da entidade e do servi�o seja lan�ada uma exce��o.
		 */
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}

		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		/*
		 * PROGRAMA��O DEFENSIVA (Necess�rio de se fazer) Como estamos fazendo uma
		 * opera��o com banco de dados essa opera��o pode lan�ar uma exce��o ent�o
		 * coloco toda a opera��o dentro de um try/catch e lan�o um alert mostrando a
		 * mensagem do erro
		 */
		try {

			// Pegando os dados do formu�rio e inserindo no objeto entity
			entity = getFormData();

			// Chamando o m�todo para salvar o objeto com os dados do formul�rio dentro do
			// BD
			service.saveOrUpdate(entity);

			/*
			 * Chamada do m�todo para que quando for feita a opera��o de salvar o objeto no
			 * banco de dados os objetos inscritos na lista dataChangeListeners possam ser
			 * notificados dessa a��o.
			 */
			notifyDataChangeListeners();

			/*
			 * Pegando a refer�ncia do palco que fez a chamada do formul�rio e usando a
			 * fun��o close para fechar a janela quando concluir a opera��o de salvar o
			 * objeto.
			 */
			Utils.currentStage(event).close();

			/*
			 * Agora o m�todo getFormData() pode lan�ar uma ValidationException ent�o agora
			 * temos que tratar dessa exception por um novo catch. E nesse tratamento vamos
			 * lan�ar o m�todo setErrorMessage que foi implementado mais abaixo na classe.
			 */
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());

		} catch (DbException e) {
			Alerts.showAlert("Error Saving Object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	/*
	 * M�todo respons�vel por notificar os objetos da lista dataChangeListeners que
	 * uma a��o de seu interesse foi executada. E como os objetos s�o notificados?
	 * Executando o m�todo onDataChanged() da interface DataChangeListener
	 * 
	 * Por isso essa classe SellerFormController � chamada de subject, por que ela �
	 * uma classe que emite o evento e apenas implementa o m�todo da interface mais
	 * n�o assina o contrato da mesma.
	 */
	private void notifyDataChangeListeners() {

		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChange();
		}

	}

	// M�todo para pegar as informa��es preenchidas no formul�rio e setar no objeto
	private Seller getFormData() {
		Seller obj = new Seller();

		// Instanciando a classe de ValidationException dentro do m�todo.
		ValidationException exception = new ValidationException("Validation Error");

		// Como o txtId recebe um String faz se a convers�o para Integer usando o m�todo
		// auxiliar criado anteriormente no pacote Utils
		obj.setId(Utils.tryParseToInt(txtId.getText()));

		/*
		 * Fazendo a verifica��o no textField txtName, nessa verifica��o o textFields
		 * n�o pode ficar vazio.
		 *
		 * trim() -> Est� eliminando todo espa�o vazio no inicio e no final do TextField
		 * equals("") -> O equals com as aspas dentro dos par�nteses verifica se o text
		 * Fileds est� em branco.
		 * 
		 *** Lendo o If: Se o txtname estiver valendo nulo ou se estiver vazio adiciona o
		 * erro na cole��o de erros da classe ValidationException.
		 * 
		 */
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText()); // Setando o nome, mesmo vazio.

		// Vale a mesma explica��o que est� no txtName
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addError("email", "Field can't be empty");
		}
		obj.setEmail(txtEmail.getText());

		/*
		 * Pegando a data do DatePicker que est� no formato default do computador do
		 * usu�rio e convertendo para um Instant que � o formato independente de
		 * formato.
		 * 
		 * Deve-se testar tamb�m se ao clicar no bot�o save e for feita a instancia��o
		 * do objeto se o DatePicker n�o for null, ou seja se foi selecionada uma data,
		 * se for null adicionar o erro na cole��o de erros da classe
		 * ValidationException.
		 */
		if (dpBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field can't be empty");
		} else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant)); // Convertendo um objeto do tipo instant para Date
		}

		// Vale a mesma explica��o que est� no txtName
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			exception.addError("baseSalary", "Field can't be empty");
		}

		/*
		 * Como o txtId recebe um String faz se a convers�o para Double usando o m�todo
		 * auxiliar criado anteriormente no pacote Utils
		 */
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		
		obj.setDepartment(comboBoxDepartment.getValue());

		/*
		 * testando se foi adicionado algum erro na cole��o de erros, se tiver sido
		 * adicionado algum e o tamanho for maior que 0 lan�a a exce��o.
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

	// Esse m�todo � chamado pelo m�todo initialize para sempre que a view for
	// carregada j� iniciar com a
	// Codifica��o especificada nele.
	public void initializeNodes() {

		/*
		 * Chamada das fun��es para parametrizar os campos a serem preenchidos pelos
		 * usuarios como quantos caract�res determinado textfield pode receber ao
		 * m�ximo, o txtBaseSalary s� receber n�meros do tipo double e qual m�scara
		 * aceitar receber o DatePicker dpBirthDate
		 * 
		 */
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");

		initializeComboBoxDepartment(); // Chamando o m�todo que inicializa a comboBoxDepartment
	}

	// M�todo para passar os valores do objeto da classe para os Text Fields
	public void updateFormData() {

		// programa��o defensiva para caso a depend�ncia da classe n�o tenha sido
		// injetada.
		if (entity == null) {
			throw new IllegalStateException("Entity was null!");
		}

		// Como o textFied trabalha com texto � necess�rio converter o Id que � integer
		// para String.
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));

		/*
		 * Forma de mostrar na tela a data no formato da maquina do usu�rio, pois assim
		 * independente de localidade e como a data � mostrada nessa localidade, o
		 * sistema sempre vai mostrar como � mostrado pelo computador do usu�rio.
		 * 
		 * Fazendo uma programa��o defensiva com o if para n�o estourar uma excess�o
		 * NullPointerException S� far� a convers�o para data local do usu�rio quando o
		 * dpBirthDate n�o estiver vazio, foi necess�rio fazer esse if pq quando
		 * apertava no bot�o new estourava a excess�o.
		 */
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}

		// Caso esteja criando um novo Seller a comboBox ir� exibir o primeiro elemento
		// da lista de Department.
		if (entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartment.setValue(entity.getDepartment());
		}

	}

	// M�todo para carregar a lista de Department na comboBoxDepartment, esse m�todo
	// deve ser chamado quando a tela for carregada
	// Ent�o deve-se chama-lo no controller que chamar a view desse controller, que
	// nesse caso � SellerListController no m�todo
	// createdDialogForm()
	public void loadAssociatedObjects() {

		// Programa��o defensiva caso tenha esquecido de injetar a depend�ncia do
		// service
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService was null");
		}

		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	// M�todo para setar os erros nas label's de erro no formul�rio, se houver
	private void setErrorMessages(Map<String, String> errors) {

		/*
		 * Percorrendo o Map e pegando todas as chaves que tiverem na cole��o e passando
		 * para a v�riavel fields que � do tipo Set, Set � uma outra cole��o.
		 */
		Set<String> fields = errors.keySet();

		/*
		 * Verificando se em fields cont�m um erro com uma chave com nome especificado em errors.get() , se tiver setar na
		 * labelError correspondente a mensagem que estiver relacionada a chave da cole��o Map, se n�o tiver chave nenhuma
		 * setar para o labelError "" que quer dizer vazio.
		 * 
		 *  Foi utilizado o operador tern�rio "?" que faz o mesmo teste de if em apenas uma linha, por�m esse operador
		 *  s� pode ser usado quando o if tiver apenas uma condi��o.
		 */
		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));
		labelErrorEmail.setText((fields.contains("email") ? errors.get("email") : ""));
		labelErrorBirthDate.setText((fields.contains("birthDate") ? errors.get("birthDate") : ""));
		labelErrorBaseSalary.setText((fields.contains("baseSalary") ? errors.get("baseSalary") : ""));

	}

	// M�todo para inicializar a comboBoxDepartment
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
