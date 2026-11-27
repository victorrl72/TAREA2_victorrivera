package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import conexion.ConexionBD;
import entidades.Cirujano;
import entidades.Empleado;
import entidades.Enfermeria;
import entidades.Especialidad;
import entidades.Perfil;
import entidades.Secretariado;

public class EmpleadoDAO {

	private EspecialidadDAO especialidadDAO = new EspecialidadDAO();

	public Empleado buscarPorCredenciales(String usuario, String password) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT id_empleado, nombre_apellidos, telefono, email, nif_nie, direccion, usuario, password, perfil "
				+ "FROM empleado WHERE usuario = ? AND password = ?";

		try {
			ps = ConexionBD.getConexion().prepareStatement(sql);
			ps.setString(1, usuario);
			ps.setString(2, password);
			rs = ps.executeQuery();

			if (rs.next()) {
				Empleado emp = new Empleado();
				emp.setIdEmpleado(rs.getLong("id_empleado"));
				emp.setNombre(rs.getString("nombre_apellidos"));
				emp.setTelefono(rs.getString("telefono"));
				emp.setEmail(rs.getString("email"));
				emp.setNifNie(rs.getString("nif_nie"));
				emp.setDireccion(rs.getString("direccion"));
				emp.setUsuario(rs.getString("usuario"));
				emp.setPassword(rs.getString("password"));
				emp.setPerfil(Perfil.valueOf(rs.getString("perfil").toLowerCase()));
				return emp;
			}
		} catch (SQLException e) {
			System.out.println("Error en login contra BD: " + e.getMessage());
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
			} catch (SQLException e) {
				System.out.println("Error al cerrar recursos: " + e.getMessage());
			}
		}
		return null;
	}

	public boolean existeUsuario(String usuario) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT id_empleado FROM empleado WHERE usuario = ?";

		try {
			ps = ConexionBD.getConexion().prepareStatement(sql);
			ps.setString(1, usuario);
			rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			System.out.println("Error al comprobar usuario: " + e.getMessage());
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
			} catch (SQLException e) {
				System.out.println("Error al cerrar recursos: " + e.getMessage());
			}
		}
		return false;
	}

	public boolean existeNif(String nifNie) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT id_empleado FROM empleado WHERE nif_nie = ?";

		try {
			ps = ConexionBD.getConexion().prepareStatement(sql);
			ps.setString(1, nifNie);
			rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			System.out.println("Error al comprobar NIF: " + e.getMessage());
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
			} catch (SQLException e) {
				System.out.println("Error al cerrar recursos: " + e.getMessage());
			}
		}
		return false;
	}

	public boolean insertarEmpleado(Empleado emp) {
		Connection con = ConexionBD.getConexion();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sqlEmp = "INSERT INTO empleado (nombre_apellidos, telefono, email, nif_nie, direccion, usuario, password, perfil) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			con.setAutoCommit(false);

			ps = con.prepareStatement(sqlEmp, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, emp.getNombre());
			ps.setString(2, emp.getTelefono());
			ps.setString(3, emp.getEmail());
			ps.setString(4, emp.getNifNie());
			ps.setString(5, emp.getDireccion());
			ps.setString(6, emp.getUsuario());
			ps.setString(7, emp.getPassword());
			ps.setString(8, emp.getPerfil().name().toUpperCase());
			ps.executeUpdate();

			rs = ps.getGeneratedKeys();
			if (!rs.next()) {
				con.rollback();
				return false;
			}
			long idGenerado = rs.getLong(1);
			emp.setIdEmpleado(idGenerado);
			rs.close();
			ps.close();

			if (emp instanceof Secretariado) {
				Secretariado sec = (Secretariado) emp;
				ps = con.prepareStatement("INSERT INTO secretariado (id_empleado, anios_experiencia) VALUES (?, ?)");
				ps.setLong(1, idGenerado);
				ps.setInt(2, sec.gettExperiencia());
				ps.executeUpdate();
				ps.close();
			} else if (emp instanceof Enfermeria) {
				Enfermeria enf = (Enfermeria) emp;
				ps = con.prepareStatement("INSERT INTO enfermeria (id_empleado, categoria) VALUES (?, ?)");
				ps.setLong(1, idGenerado);
				ps.setString(2, String.valueOf(enf.getCategoria()));
				ps.executeUpdate();
				ps.close();
			} else if (emp instanceof Cirujano) {
				Cirujano cir = (Cirujano) emp;
				ps = con.prepareStatement("INSERT INTO cirujano (id_empleado) VALUES (?)");
				ps.setLong(1, idGenerado);
				ps.executeUpdate();
				ps.close();

				ps = con.prepareStatement("INSERT INTO cirujano_especialidad (id_cirujano, id_especialidad) VALUES (?, ?)");
				for (Especialidad esp : cir.getEspecialidades()) {
					ps.setLong(1, idGenerado);
					ps.setLong(2, esp.getIdEspecialidad());
					ps.executeUpdate();
				}
				ps.close();
			}

			con.commit();
			return true;
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException ex) {
				System.out.println("Error en rollback: " + ex.getMessage());
			}
			System.out.println("Error al insertar empleado: " + e.getMessage());
			return false;
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				con.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("Error al cerrar recursos: " + e.getMessage());
			}
		}
	}

	public Cirujano obtenerCirujanoPorId(long idEmpleado) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT id_empleado, nombre_apellidos, telefono, email, nif_nie, direccion, usuario "
				+ "FROM empleado WHERE id_empleado = ? AND perfil = 'CIRUGIA'";

		try {
			ps = ConexionBD.getConexion().prepareStatement(sql);
			ps.setLong(1, idEmpleado);
			rs = ps.executeQuery();

			if (rs.next()) {
				Cirujano cir = new Cirujano();
				cir.setIdEmpleado(rs.getLong("id_empleado"));
				cir.setNombre(rs.getString("nombre_apellidos"));
				cir.setTelefono(rs.getString("telefono"));
				cir.setEmail(rs.getString("email"));
				cir.setNifNie(rs.getString("nif_nie"));
				cir.setDireccion(rs.getString("direccion"));
				cir.setUsuario(rs.getString("usuario"));
				cir.setEspecialidades(especialidadDAO.obtenerPorCirujano(idEmpleado));
				return cir;
			}
		} catch (SQLException e) {
			System.out.println("Error al obtener ficha del cirujano: " + e.getMessage());
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
			} catch (SQLException e) {
				System.out.println("Error al cerrar recursos: " + e.getMessage());
			}
		}
		return null;
	}
}