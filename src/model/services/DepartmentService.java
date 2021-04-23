package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Department;

public class DepartmentService {
	
	public List<Department> findAll(){
		
		/* 
		 * Criando um list com dados "MOCKADOS"
		 * 
		 * o termo MOCK � usado no meio da programa��o para dizer que est� mockando os dados manualmente
		 * normalmente usado para testes.
		 */
		
		List<Department> list = new ArrayList<>();
		list.add(new Department(1, "Books"));
		list.add(new Department(2, "Computers"));
		list.add(new Department(3, "Electronics"));
		return list;
	}

}
