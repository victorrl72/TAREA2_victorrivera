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

	private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();

	public Empleado buscarPorCredenciales(String usuario, String password) {
		String sql = "SELECT id_empleado, nombre_apellidos, telefono, email, nif_nie, direccion, usuario, password, perfil "
				+ "FROM empleado WHERE usuario = ? AND password = ?";
		try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
			ps.setString(1, usuario);
			ps.setString(2, password);
			try (ResultSet rs = ps.executeQuery()) {
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
			}
		} catch (SQLException e) {
			System.err.println("Error en login contra BD: " + e.getMessage());
		}
		return null;
	}

	public boolean existeUsuario(String usuario) {
		String sql = "SELECT 1 FROM empleado WHERE usuario = ?";
		try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
			ps.setString(1, usuario);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			System.err.println("Error al comprobar usuario: " + e.getMessage());
		}
		return false;
	}

	public boolean existeNif(String nifNie) {
		String sql = "SELECT 1 FROM empleado WHERE nif_nie = ?";
		try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
			ps.setString(1, nifNie);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			System.err.println("Error al comprobar NIF: " + e.getMessage());
		}
		return false;
	}

	public boolean insertarEmpleado(Empleado emp) {
		Connection con = ConexionBD.getConexion();
		String sqlEmp = "INSERT INTO empleado (nombre_apellidos, telefono, email, nif_nie, direccion, usuario, password, perfil) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			con.setAutoCommit(false);

			long idGenerado;
			try (PreparedStatement ps = con.prepareStatement(sqlEmp, Statement.RETURN_GENERATED_KEYS)) {
				ps.setString(1, emp.getNombre());
				ps.setString(2, emp.getTelefono());
				ps.setString(3, emp.getEmail());
				ps.setString(4, emp.getNifNie());
				ps.setString(5, emp.getDireccion());
				ps.setString(6, emp.getUsuario());
				ps.setString(7, emp.getPassword());
				ps.setString(8, emp.getPerfil().name().toUpperCase());
				ps.executeUpdate();

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (!rs.next()) {
						con.rollback();
						return false;
					}
					idGenerado = rs.getLong(1);
					emp.setIdEmpleado(idGenerado);
				}
			}

			if (emp instanceof Secretariado sec) {
				String sqlSec = "INSERT INTO secretariado (id_empleado, anios_experiencia) VALUES (?, ?)";
				try (PreparedStatement ps = con.prepareStatement(sqlSec)) {
					ps.setLong(1, idGenerado);
					ps.setInt(2, sec.gettExperiencia());
					ps.executeUpdate();
				}
			} else if (emp instanceof Enfermeria enf) {
				String sqlEnf = "INSERT INTO enfermeria (id_empleado, categoria) VALUES (?, ?)";
				try (PreparedStatement ps = con.prepareStatement(sqlEnf)) {
					ps.setLong(1, idGenerado);
					ps.setString(2, String.valueOf(enf.getCategoria()));
					ps.executeUpdate();
				}
			} else if (emp instanceof Cirujano cir) {
				String sqlCir = "INSERT INTO cirujano (id_empleado) VALUES (?)";
				try (PreparedStatement ps = con.prepareStatement(sqlCir)) {
					ps.setLong(1, idGenerado);
					ps.executeUpdate();
				}
				String sqlEsp = "INSERT INTO cirujano_especialidad (id_cirujano, id_especialidad) VALUES (?, ?)";
				try (PreparedStatement ps = con.prepareStatement(sqlEsp)) {
					for (Especialidad esp : cir.getEspecialidades()) {
						ps.setLong(1, idGenerado);
						ps.setLong(2, esp.getIdEspecialidad());
						ps.executeUpdate();
					}
				}
			}

			con.commit();
			return true;
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException ex) {
				System.err.println("Error en rollback: " + ex.getMessage());
			}
			System.err.println("Error al insertar empleado: " + e.getMessage());
			return false;
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				System.err.println("Error al restaurar autoCommit: " + e.getMessage());
			}
		}
	}

	public Cirujano obtenerCirujanoPorId(long idEmpleado) {
		String sql = "SELECT id_empleado, nombre_apellidos, telefono, email, nif_nie, direccion, usuario "
				+ "FROM empleado WHERE id_empleado = ? AND perfil = 'CIRUGIA'";
		try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
			ps.setLong(1, idEmpleado);
			try (ResultSet rs = ps.executeQuery()) {
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
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener ficha del cirujano: " + e.getMessage());
		}
		return null;
	}
}