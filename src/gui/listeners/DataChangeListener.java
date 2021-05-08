/*
 * Esta interface servir� para que um objeto escute o evento de outro objeto. 
 * 
 *  E o que esse m�todo faz? ele permitir� trabalharmos com o padr�o de projetos chamado observer que trabalha com a id�ia de eventos, ou seja, 
 * quando um objeto fizer alguma opera��o que nesse caso ser� altera��es no banco de dados, outros objetos que ser�o os observers
 * saber�o dessa altera��o e far�o suas opera��es necess�rias ap�s essa a��o executada pelo objeto subject.
 * 
 * Termos usados:
 * 
 * ** Subject - s�o os objetos que emitem o evento  ser escutado pelos Observers.
 * ** Observer - S�o os objetos que receber�o uma notifica��o quando o evento for executado para que possa executar suas opera��es necess�rias.
 *
 * Mais explica��es desse assunto nas classes DepartmentFormController e DepartmentListController
 * 
 * Esse padr�o de projetos observer � uma forma de comunicar 2 objetos de forma altamente desaclopada, o objeto que emite o evento
 * n�o conhece o objeto que est� escutando o evento dele.
 * 
 * Padr�o muito importante que � seguido em qualquer aplica��o que lida com eventos como: Eventos de telas, eventos de servidores e assim em diante. 
 */

package gui.listeners;

public interface DataChangeListener {
	
	// Evento para ser disparado quando os dados mudarem.
	void onDataChange();
}
