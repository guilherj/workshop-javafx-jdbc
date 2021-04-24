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
				 * USADO if POR QUE A INSER��O � DE APENAS UM VALOR, CASO TIVESSE INSERINDO MAIS
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

			st.setInt(1, id); // <-- SETANDO O ID DA SENTEN�A SQL COMO O ID PASSADO POR ARGUMENTO NO M�TODO
			rs = st.executeQuery();

			if (rs.next()) {

				Department dep = instantiateDepartment(rs); // USANDO O M�TODO AUXILIAR DECLARADO ABAIXO
				Seller obj = instantiateSeller(rs, dep); // USANDO O M�TODO AUXILIAR DECLARADO ABAIXO
				return obj;
			}
			return null;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
			/*
			 * N�O � PRECISO FECHAR A CONEX�O COM O BANCO, POIS ESSE DAO PODE SER USADO PARA
			 * OUTRAS OPERA��ES, ENT�O PODE DEIXAR PARA FECHAR A CONEX�O NA CLASSE PROGRAM.
			 */
		}

	}

	@Override
	public List<Seller> findAll() {
		/*
		 * O C�DIGO DO M�TODO findAll() � O MESMO USADO NO M�TODO findByDepartmentId(),
		 * PORTANTO AS EXPLICA��ES DESSA ESTRUTURA J� EST�O DESCRITAS NESSE M�TODO, COM
		 * DIFEREN�A APENAS NA QUERY SQL QUE N�O TEM A LINHA WHERE ... E A LINHA
		 * "st.setInt(1, department.getId()) POR N�O TER NECESSIDADE DE ESPECIFICAR UM
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

			// >>> INICIO ESTRUTURA DE CONTROLE DE REPETI��ES DE OBJETOS <<<

			List<Seller> list = new ArrayList<>();
			// Criando um list de seller para guardar cada seller que fa�a parte do
			// DepartmentId especificado.

			Map<Integer, Department> map = new HashMap<>();
			/*
			 * Criando um Map para guardar o Department encontrado e instanciado dentro do
			 * while, assim fazemos uma estrutura de controle para evitar de se instanciar
			 * repetidamente o mesmo objeto do tipo Department, o correto � todos os
			 * seller's encontrados na busca apontarem para o mesmo objeto do tipo
			 * Department.
			 */

			while (rs.next()) {

				Department dep = map.get(rs.getInt("DepartmentId"));
				/*
				 * Fazendo uma busca dentro do map passando como chave o departmentId recebeido
				 * pelo ResultSet, Caso ainda n�o exista nenhum Department dentro de map com a
				 * chave informada o mesmo retornar� null e passar� esse null para a vari�vel
				 * auxiliar dep Caso j� exista um department dentro do map com a chave recebida
				 * a vari�vel dep receber� esse objeto.
				 */

				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
					/*
					 * Teste com if para o caso de o objeto Department ainda n�o ter sido
					 * instanciado pelo while caso a var�vel dep tenha valor null � executado o
					 * m�todo j� criado instantiateDepartment(rs) passando o ResultSet como
					 * argumento. Department j� � instanciado dentro desse m�todo.
					 * 
					 * Ap�s instanciar o objeto atrav�s do m�todo, � adicionado o mesmo dentro do
					 * map e assim na pr�xima vez que se repertir o while o map n�o retornar� mais
					 * um valor null para a vari�vel dep no c�digo acima do if *
					 */
				}

				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);
			}
			// >>> FIM DA ESTRUTURA DE CONTROLE DE REPETI��O DE OBJETOS <<<

			/*
			 * Toda essa estrutura de c�digos acima pode ser usada em outras situa��es
			 * quando for necess�rio fazer uma busca no banco de dados por um objeto e caso
			 * tenha mais de um resultado dessa busca n�o repetir a instancia��o do mesmo
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
	 * OS M�TODOS ABAIXO (instantiateSeller e instantiateDepartment) SERVEM COMO
	 * AUXILIARES PARA QUALQUER M�TODO DA CLASSE QUE PRECISE INSTANCIAR UM OBJETO
	 * SELLER OU DEPARTMENT ASSIM ELE N�O FICA MUITO GRANDE E O M�TODO QUE O CHAMAR
	 * FICA MAIS ORGANIZADO.
	 * 
	 * AS EXCESS�ES QUE PODEM DAR NESSES M�TODOS EST�O SENDO PROPAGADAS PARA QUE
	 * SEJAM TRATADAS PELO CATCH QUANDO FOREM CHAMADOS NO M�TODO FindById.
	 * 
	 * O C�DIGO DESSES M�TODOS EST�O INSTANCIANDO OS OBJETOS COM OS DADOS RECEBIDOS
	 * PELO RESULTSET "rs" � NECESS�RIO FAZER ESSA OPERA��O POIS ESTAMOS PROGRAMANDO
	 * NUMA LINGUAGEM ORIENTADA A OBJETO ENT�O DEVE-SE INSTANCIAR OS OBJETOS COM
	 * SUAS ASSOCIA��ES NA MEM�RIA.
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
