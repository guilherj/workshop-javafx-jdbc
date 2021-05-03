package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable {
	
	// INJETANDO DEPENDENCIA DA CLASSE DEPARTMENT
	private Department entity;
	
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
	
	// M�TODO PARA INJETAR A DEPEND�NCIA DA CLASSE EM QUALQUER LUGAR DO PROGRAMA.
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	@FXML
	public void onBtSaveAction() {
		System.out.println("BtSave Funcionando!");
	}
	
	@FXML
	public void onBtCancelAction() {
		System.out.println("BtCancel Funcionando!");
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
