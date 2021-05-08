/*
 * Esta interface servirá para que um objeto escute o evento de outro objeto. 
 * 
 *  E o que esse método faz? ele permitirá trabalharmos com o padrão de projetos chamado observer que trabalha com a idéia de eventos, ou seja, 
 * quando um objeto fizer alguma operação que nesse caso será alterações no banco de dados, outros objetos que serão os observers
 * saberão dessa alteração e farão suas operações necessárias após essa ação executada pelo objeto subject.
 * 
 * Termos usados:
 * 
 * ** Subject - são os objetos que emitem o evento  ser escutado pelos Observers.
 * ** Observer - São os objetos que receberão uma notificação quando o evento for executado para que possa executar suas operações necessárias.
 *
 * Mais explicações desse assunto nas classes DepartmentFormController e DepartmentListController
 * 
 * Esse padrão de projetos observer é uma forma de comunicar 2 objetos de forma altamente desaclopada, o objeto que emite o evento
 * não conhece o objeto que está escutando o evento dele.
 * 
 * Padrão muito importante que é seguido em qualquer aplicação que lida com eventos como: Eventos de telas, eventos de servidores e assim em diante. 
 */

package gui.listeners;

public interface DataChangeListener {
	
	// Evento para ser disparado quando os dados mudarem.
	void onDataChange();
}
