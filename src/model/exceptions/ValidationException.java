/*
 * Classe Respons�vel por carregar os erros que podem acontecer e lan�a-los caso necess�rio.
 */

package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	/* 
	 * Declara��o do atributo errors como uma cole��o Map para carregar a cole��o de erros que podem ter em cada campo do
	 * formul�rio. O Map trabalha com par�metros chave , valor como j� visto em outras aulas do curso.
	 */
	private Map<String, String> errors = new HashMap<>();
	
	public ValidationException(String msg) {
		super(msg);
	}
	
	public Map<String, String> getErrors(){
		return errors;
	}
	
	public void addError(String fieldName, String errorMessage) {
		errors.put(fieldName, errorMessage);
		
	}
	
	

}
