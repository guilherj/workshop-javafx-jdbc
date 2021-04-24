package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	private Connection conn;

	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement(
					"INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId) " + "VALUES (?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				/*
				 * USADO if POR QUE A INSERÇÃO É DE APENAS UM VALOR, CASO TIVESSE INSERINDO MAIS
				 * DE UM VALOR TERIA QUE USAR O WHILE
				 */
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
	public void update(Seller obj) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? WHERE Id = ?");

			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());

			st.executeUpdate();

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
			st = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");

			st.setInt(1, id);

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
	public Seller findById(Integer id) {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			// COMANDO SQL PARA BUSCA DE SELLER POR ID
			st = conn.prepareStatement("SELECT seller.*,department.Name as DepName FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id " + "WHERE seller.Id = ?");

			st.setInt(1, id); // <-- SETANDO O ID DA SENTENÇA SQL COMO O ID PASSADO POR ARGUMENTO NO MÉTODO
			rs = st.executeQuery();

			if (rs.next()) {

				Department dep = instantiateDepartment(rs); // USANDO O MÉTODO AUXILIAR DECLARADO ABAIXO
				Seller obj = instantiateSeller(rs, dep); // USANDO O MÉTODO AUXILIAR DECLARADO ABAIXO
				return obj;
			}
			return null;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
			/*
			 * NÃO É PRECISO FECHAR A CONEXÃO COM O BANCO, POIS ESSE DAO PODE SER USADO PARA
			 * OUTRAS OPERAÇÕES, ENTÃO PODE DEIXAR PARA FECHAR A CONEXÃO NA CLASSE PROGRAM.
			 */
		}

	}

	@Override
	public List<Seller> findAll() {
		/*
		 * O CÓDIGO DO MÉTODO findAll() É O MESMO USADO NO MÉTODO findByDepartmentId(),
		 * PORTANTO AS EXPLICAÇÕES DESSA ESTRUTURA JÁ ESTÃO DESCRITAS NESSE MÉTODO, COM
		 * DIFERENÇA APENAS NA QUERY SQL QUE NÃO TEM A LINHA WHERE ... E A LINHA
		 * "st.setInt(1, department.getId()) POR NÃO TER NECESSIDADE DE ESPECIFICAR UM
		 * ID PARA ESSA BUSCA POIS findAll() BUSCA TODOS OS VENDEDORES CADASTRADOS NO
		 * BANCO.
		 */

		PreparedStatement st = null;
		ResultSet rs = null;

		try {

			st = conn.prepareStatement("SELECT seller.*,department.Name as DepName FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id  ORDER BY Name");

			rs = st.executeQuery();

			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();

			while (rs.next()) {
				Department dep = map.get(rs.getInt("DepartmentId"));

				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}

				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);
			}

			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);

		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {

			st = conn.prepareStatement("SELECT seller.*,department.Name as DepName FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id WHERE DepartmentId = ? ORDER BY Name");

			st.setInt(1, department.getId());
			rs = st.executeQuery();

			// >>> INICIO ESTRUTURA DE CONTROLE DE REPETIÇÕES DE OBJETOS <<<

			List<Seller> list = new ArrayList<>();
			// Criando um list de seller para guardar cada seller que faça parte do
			// DepartmentId especificado.

			Map<Integer, Department> map = new HashMap<>();
			/*
			 * Criando um Map para guardar o Department encontrado e instanciado dentro do
			 * while, assim fazemos uma estrutura de controle para evitar de se instanciar
			 * repetidamente o mesmo objeto do tipo Department, o correto é todos os
			 * seller's encontrados na busca apontarem para o mesmo objeto do tipo
			 * Department.
			 */

			while (rs.next()) {

				Department dep = map.get(rs.getInt("DepartmentId"));
				/*
				 * Fazendo uma busca dentro do map passando como chave o departmentId recebeido
				 * pelo ResultSet, Caso ainda não exista nenhum Department dentro de map com a
				 * chave informada o mesmo retornará null e passará esse null para a variável
				 * auxiliar dep Caso já exista um department dentro do map com a chave recebida
				 * a variável dep receberá esse objeto.
				 */

				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
					/*
					 * Teste com if para o caso de o objeto Department ainda não ter sido
					 * instanciado pelo while caso a varável dep tenha valor null é executado o
					 * método já criado instantiateDepartment(rs) passando o ResultSet como
					 * argumento. Department já é instanciado dentro desse método.
					 * 
					 * Após instanciar o objeto através do método, é adicionado o mesmo dentro do
					 * map e assim na próxima vez que se repertir o while o map não retornará mais
					 * um valor null para a variável dep no código acima do if *
					 */
				}

				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);
			}
			// >>> FIM DA ESTRUTURA DE CONTROLE DE REPETIÇÃO DE OBJETOS <<<

			/*
			 * Toda essa estrutura de códigos acima pode ser usada em outras situações
			 * quando for necessário fazer uma busca no banco de dados por um objeto e caso
			 * tenha mais de um resultado dessa busca não repetir a instanciação do mesmo
			 * objeto resultado da busca.
			 */

			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);

		}
	}

	/*
	 * OS MÉTODOS ABAIXO (instantiateSeller e instantiateDepartment) SERVEM COMO
	 * AUXILIARES PARA QUALQUER MÉTODO DA CLASSE QUE PRECISE INSTANCIAR UM OBJETO
	 * SELLER OU DEPARTMENT ASSIM ELE NÃO FICA MUITO GRANDE E O MÉTODO QUE O CHAMAR
	 * FICA MAIS ORGANIZADO.
	 * 
	 * AS EXCESSÕES QUE PODEM DAR NESSES MÉTODOS ESTÃO SENDO PROPAGADAS PARA QUE
	 * SEJAM TRATADAS PELO CATCH QUANDO FOREM CHAMADOS NO MÉTODO FindById.
	 * 
	 * O CÓDIGO DESSES MÉTODOS ESTÃO INSTANCIANDO OS OBJETOS COM OS DADOS RECEBIDOS
	 * PELO RESULTSET "rs" É NECESSÁRIO FAZER ESSA OPERAÇÃO POIS ESTAMOS PROGRAMANDO
	 * NUMA LINGUAGEM ORIENTADA A OBJETO ENTÃO DEVE-SE INSTANCIAR OS OBJETOS COM
	 * SUAS ASSOCIAÇÕES NA MEMÓRIA.
	 */

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep);
		return obj;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

}
