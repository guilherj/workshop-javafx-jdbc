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
	
	// Injetando Dependência da classe DepartmentService
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
	
	// MÉTODOS PARA INJETAR AS DEPENDÊNCIAS DAS CLASSES EM QUALQUER LUGAR DO PROGRAMA.
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
		
	/* Necessário passar um ActionEvent como parâmetro para pegar a ação de apertar o botão save e passar essa ação ao
	 * método currentStage usado mais abaixo na hora de fechar a janela do formulário.
	 */
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		/* Programação defensiva necessária para que, caso tenha esquecido de injetar a dependência 
		 * da entidade e do serviço seja lançada uma exceção.
		 */
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		/*
		 * PROGRAMAÇÃO DEFENSIVA (Necessário de se fazer)
		 * Como estamos fazendo uma operação com banco de dados essa operação pode lançar uma exceção então 
		 * coloco toda a operação dentro de um try/catch e lanço um alert mostrando a mensagem do erro
		 */
		try {
			
		// Pegando os dados do formuário e inserindo no objeto entity
		entity = getFormData();
		
		// Chamando o método para salvar o objeto com os dados do formulário dentro do BD
		service.saveOrUpdate(entity);
		
		/* Pegando a referência do palco que fez a chamada do formulário e usando a função close para fechar a janela
		 * quando concluir a operação de salvar o objeto.
		 */
		Utils.currentStage(event).close();
		
		}catch (DbException e) {
			Alerts.showAlert("Error Saving Object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	// Método para pegar as informações preenchidas nos TextFileds do formulário
	private Department getFormData() {
		Department obj = new Department();
		
		// Como o txtId recebe um integer faz se a conversão para String usando o método auxiliar criado anteriormente no pacote Utils
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
	
	// Método para passar os valores do objeto da classe para os Text Fields
	public void updateFormData() {
		
		// programação defensiva para caso a dependência da classe não tenha sido injetada.
		if(entity == null) {
			throw new IllegalStateException("Entity was null!");
		}
		
		// Como o textFied trabalha com texto é necessário converter o Id que é integer para String.
		txtId.setText(String.valueOf(entity.getId()));  
		txtName.setText(entity.getName());
	}
	
	
	
	

}
