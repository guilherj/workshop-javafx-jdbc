package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	private Connection conn;

	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("INSERT INTO Department (Name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getName());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {

				ResultSet rs = st.getGeneratedKeys();

				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Unexpected error! No rows Affected!");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Department obj) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("UPDATE Department SET Name = ? WHERE Id = ?");

			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected == 0) {
				throw new DbException("Unexpected error! No rows Affected!");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		
		try {
			
			st = conn.prepareStatement("DELETE FROM department WHERE Id = ?");
			
			st.setInt(1, id);
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected == 0) {
				throw new DbException("Unexpected error! No rows Affected!");
			}
			
		}catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		}finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {

			st = conn.prepareStatement("SELECT * FROM Department WHERE id = ?");

			st.setInt(1, id);
			rs = st.executeQuery();

			if (rs.next()) {
				Department dep = instantiateDepartment(rs);
				return dep;
			}
			return null;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	
	/*
	 * COMO A CLASSE Department N�O TEM RELA��O COM A CLASSE Seller ENT�O O C�DIGO DO M�TODO findAll()
	 * DE DepartmentDaoJDBC SE TORNA SIMPLES POIS N�O SE PRENDE A CONFERIR REPETI��O DE INSTANCIA��O
	 * DE OBJETOS DE OUTRA CLASSE QUE N�O SEJA A Department.
	 * 
	 * EM SellerDaoJDBC TIVEMOS QUE FAZER ESSA VERIFICA��O POIS A CLASSE Seller TEM RELA��O COM A CLASSE Department
	 * E POR ISSO AO INSTANCIAR MAIS DE UM OBJETO DO TIPO Seller, OS OBJETOS TIPO Seller DEVERIAM EST�R LIGADOS AO MESMO
	 * Department, NO CASO DE TER MAIS DE UM Seller NO MESMO Department DENTRO DO BANCO.
	 * 
	 *  POIS SE N�O TIVER ESSE CONTROLE, CADA VEZ QUE INSTANCIAR UM Seller IR� INSTANCIAR UM NOVO Department DENTRO DO BANCO
	 *  MESMO QUE TENHA MAIS DE UM Seller NO MESMO Department.
	 * 
	 */
	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT * FROM Department ORDER BY Name");

			rs = st.executeQuery();

			List<Department> list = new ArrayList<>();
			while (rs.next()) {
				Department dep = instantiateDepartment(rs);
				list.add(dep);
			}
			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	/*
	 * M�TODO AUXILIAR PARA INSTANCIA��O DO OBJETO DEPARTMENT DENTRO DA CLASSE
	 * DepartmentDaoJDBC, A EXPLICA��O DO PORQUE EST� NA CLASSE SellerDaoJDBC.
	 */

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("Id"));
		dep.setName(rs.getString("Name"));
		return dep;

	}

}
