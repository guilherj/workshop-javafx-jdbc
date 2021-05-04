package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	// INJETANDO DEPENDENCIA DA CLASSE DEPARTMENT
	private Department entity;
	
	// Injetando Depend�ncia da classe DepartmentService
	private DepartmentService service;
	
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
		
		/* Pegando a refer�ncia do palco que fez a chamada do formul�rio e usando a fun��o close para fechar a janela
		 * quando concluir a opera��o de salvar o objeto.
		 */
		Utils.currentStage(event).close();
		
		}catch (DbException e) {
			Alerts.showAlert("Error Saving Object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	// M�todo para pegar as informa��es preenchidas nos TextFileds do formul�rio
	private Department getFormData() {
		Department obj = new Department();
		
		// Como o txtId recebe um integer faz se a convers�o para String usando o m�todo auxiliar criado anteriormente no pacote Utils
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setName(txtName.getText());
		
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
	
	
	
	

}
