/*
 * Classe utilit�ria para pegar o Palco atual (Stage) quando for apertar um bot�o.
 * 
 * Exemplo: Ao clicar no bot�o (ActionEvent), se for necess�rio abrir uma janela de dialog por cima da janela onde foi
 * apertado o bot�o, essa classe guarda a refe�ncia dessa janela onde foi apertado o bot�o.
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
	
	/* M�todo utilit�rio para converter valores string que vierem das text fields para integer
	 * 
	 * Caso o valor passado n�o for um integer v�lido retornar null.
	 */
	public static Integer tryParseToInt(String str) {
		try {
		return Integer.parseInt(str);
		
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
