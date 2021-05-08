/*
 * Classe Responsável por carregar os erros que podem acontecer e lança-los caso necessário.
 */

package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	/* 
	 * Declaração do atributo errors como uma coleção Map para carregar a coleção de erros que podem ter em cada campo do
	 * formulário. O Map trabalha com parâmetros chave , valor como já visto em outras aulas do curso.
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
