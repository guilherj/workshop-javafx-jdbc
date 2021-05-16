/*
 * Classe utilitária com métodos para auxiliar operações no sistema.
 */

package gui.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class Utils {

	/*
	 * metodo utilitário para pegar o Palco atual (Stage) quando for apertar um
	 * botão.
	 * 
	 * Exemplo: Ao clicar no botão (ActionEvent), se for necessário abrir uma janela
	 * de dialog por cima da janela onde foi apertado o botão, essa classe guarda a
	 * refeência dessa janela onde foi apertado o botão.
	 * 
	 */
	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}

	/*
	 * Método utilitário para converter valores string que vierem das text fields
	 * para integer
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
	
	/*
	 * Método utilitário para converter valores string que vierem das text fields
	 * para Double
	 * 
	 * Caso o valor passado não for um double válido retornar null.
	 */
	public static Double tryParseToDouble(String str) {
		try {
			return Double.parseDouble(str);

		} catch (NumberFormatException e) {
			return null;
		}
	}

	/*
	 * Método auxiliar para formatar a data de uma tableview.
	 */
	public static <T> void formatTableColumnDate(TableColumn<T, Date> tableColumn, String format) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Date> cell = new TableCell<T, Date>() {
				private SimpleDateFormat sdf = new SimpleDateFormat(format);

				@Override
				protected void updateItem(Date item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						setText(sdf.format(item));
					}
				}
			};
			return cell;
		});
	}

	/*
	 * Método auxiliar para formatar valores do tipo double para sairem com duas
	 * casas decimais numa tableview
	 */
	public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColumn, int decimalPlaces) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Double> cell = new TableCell<T, Double>() {
				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						Locale.setDefault(Locale.US);
						setText(String.format("%." + decimalPlaces + "f", item));
					}
				}
			};
			return cell;
		});
	}

	/*
	 * Método auxiliar para Formatar o DatePicker para que a data dentro dele apareceça como eu determinar.
	 */
	public static void formatDatePicker(DatePicker datePicker, String format) {
		datePicker.setConverter(new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format);
			{
				datePicker.setPromptText(format.toLowerCase());
			}

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		});
	}

}
