/*
 * Classe utilitária para pegar o Palco atual (Stage) quando for apertar um botão.
 * 
 * Exemplo: Ao clicar no botão (ActionEvent), se for necessário abrir uma janela de dialog por cima da janela onde foi
 * apertado o botão, essa classe guarda a refeência dessa janela onde foi apertado o botão.
 * 
 */

package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	
	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}
	
	/* Método utilitário para converter valores string que vierem das text fields para integer
	 * 
	 * Caso o valor passado não for um integer válido retornar null.
	 */
	public static Integer tryParseToInt(String str) {
		try {
		return Integer.parseInt(str);
		
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
