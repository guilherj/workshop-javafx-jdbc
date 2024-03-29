package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	// INJETANDO DEPENDENCIA DA CLASSE DEPARTMENT
	private Department entity;
	
	// Injetando Depend�ncia da classe DepartmentService
	private DepartmentService service;
	
	// Lista criada para que os objetos possam se inscrever e possam ouvir quando um evento de seu interesse for feito nesse objeto.
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	// M�TODOS PARA INJETAR AS DEPEND�NCIAS DAS CLASSES EM QUALQUER LUGAR DO PROGRAMA.
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	
	/*  M�todo para que outros objetos possam se inscrever na lista.
	 *  
	 *  Assim outros objetos,  desde que implementem a interface DataChangeListener,
	 *  conseguem se increver na lista para receber o evento desta classe
	 */
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
		
	/* Necess�rio passar um ActionEvent como par�metro para pegar a a��o de apertar o bot�o save e passar essa a��o ao
	 * m�todo currentStage usado mais abaixo na hora de fechar a janela do formul�rio.
	 */
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		/* Programa��o defensiva necess�ria para que, caso tenha esquecido de injetar a depend�ncia 
		 * da entidade e do servi�o seja lan�ada uma exce��o.
		 */
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		/*
		 * PROGRAMA��O DEFENSIVA (Necess�rio de se fazer)
		 * Como estamos fazendo uma opera��o com banco de dados essa opera��o pode lan�ar uma exce��o ent�o 
		 * coloco toda a opera��o dentro de um try/catch e lan�o um alert mostrando a mensagem do erro
		 */
		try {
			
		// Pegando os dados do formu�rio e inserindo no objeto entity
		entity = getFormData();
		
		// Chamando o m�todo para salvar o objeto com os dados do formul�rio dentro do BD
		service.saveOrUpdate(entity);
		
		
		/* Chamada do m�todo para que quando for feita a opera��o de salvar o objeto no banco de dados
		 * os objetos inscritos na lista dataChangeListeners possam ser notificados dessa a��o.
		 */
		notifyDataChangeListeners(); 
		
		/* Pegando a refer�ncia do palco que fez a chamada do formul�rio e usando a fun��o close para fechar a janela
		 * quando concluir a opera��o de salvar o objeto.
		 */
		Utils.currentStage(event).close();
		
		/*
		 * Agora o m�todo getFormData() pode lan�ar uma ValidationException ent�o agora temos que
		 * tratar dessa exception por um novo catch. E nesse tratamento vamos lan�ar o m�todo
		 * setErrorMessage que foi implementado mais abaixo na classe.
		 */		
		}catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		
		}catch (DbException e) {
			Alerts.showAlert("Error Saving Object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	/*
	 * M�todo respons�vel por notificar os objetos da lista dataChangeListeners que uma a��o de seu interesse
	 * foi executada. E como os objetos s�o notificados? Executando o m�todo onDataChanged() da interface DataChangeListener
	 * 
	 * Por isso essa classe DepartmentFormController � chamada de subject, por que ela � uma classe que emite o evento e apenas implementa
	 * o m�todo da interface mais n�o assina o contrato da mesma.
	 */
	private void notifyDataChangeListeners() {
		
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDataChange();
		}
		
	}

	// M�todo para pegar as informa��es preenchidas nos TextFileds do formul�rio
	private Department getFormData() {
		Department obj = new Department();
		
		//Instanciando a classe de ValidationException dentro do m�todo.
		ValidationException exception = new ValidationException("Validation Error");
		
		// Como o txtId recebe um integer faz se a convers�o para String usando o m�todo auxiliar criado anteriormente no pacote Utils
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		/*
		 * Fazendo a verifica��o no textField txtName, nessa verifica��o o textFields n�o pode ficar vazio.
		 *
		 * trim() -> Est� eliminando todo espa�o vazio no inicio e no final do TextField 
		 * equals("") -> O equals com as aspas dentro dos par�nteses verifica se o text Fileds est� em branco.
		 * 
		 *** Lendo o If: Se o txtname estiver valendo nulo ou se estiver vazio adiciona o erro na cole��o de erros da classe
		 *  ValidationException.
		 * 
		 */
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText()); // Setando o nome, mesmo vazio.
		
		/*
		 * testando se foi adicionado algum erro na cole��o de erros, se tiver sido adicionado algum
		 * e o tamanho for maior que 0 lan�a a exce��o.
		 */
		if(exception.getErrors().size() > 0) {
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
	
	public void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	// M�todo para passar os valores do objeto da classe para os Text Fields
	public void updateFormData() {
		
		// programa��o defensiva para caso a depend�ncia da classe n�o tenha sido injetada.
		if(entity == null) {
			throw new IllegalStateException("Entity was null!");
		}
		
		// Como o textFied trabalha com texto � necess�rio converter o Id que � integer para String.
		txtId.setText(String.valueOf(entity.getId()));  
		txtName.setText(entity.getName());
	}
	
	// M�todo para setar os erros nas label's de erro no formul�rio, se houver
	private void setErrorMessages(Map<String, String> errors) {
		
		/* Percorrendo o Map e pegando todas as chaves que tiverem na cole��o e passando para a v�riavel fields
		 * que � do tipo Set, Set � uma outra cole��o. 
		 */
		Set<String> fields = errors.keySet();
		
		/*
		 * Verificando se em fields cont�m uma chave com nome "name" , se tiver 
		 * setar na labelErrorName a mensagem que estiver relacionada a chave "name" da cole��o Map.
		 */
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
		
		
	}
	
	
	
	

}
