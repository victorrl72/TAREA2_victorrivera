package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import conexion.ConexionBD;
import entidades.Historial;
import entidades.Paciente;

public class PacienteDAO {

	public boolean existeNif(String nifNie, Long idExcluir) {
		String sql = idExcluir == null
				? "SELECT 1 FROM paciente WHERE nif_nie = ?"
				: "SELECT 1 FROM paciente WHERE nif_nie = ? AND id_paciente <> ?";
		try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
			ps.setString(1, nifNie);
			if (idExcluir != null) {
				ps.setLong(2, idExcluir);
			}
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			System.err.println("Error al comprobar NIF del paciente: " + e.getMessage());
		}
		return false;
	}

	public boolean insertarPaciente(Paciente p, List<Long> idsAlergias) {
		Connection con = ConexionBD.getConexion();
		String sqlPac = "INSERT INTO paciente (nombre_completo, fecha_nacimiento, telefono, email, direccion, nif_nie) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		String sqlHist = "INSERT INTO historial (descripcion, id_paciente) VALUES (?, ?)";
		String sqlAler = "INSERT INTO historial_alergia (num_historia, id_alergia) VALUES (?, ?)";

		try {
			con.setAutoCommit(false);

			long idPaciente;
			try (PreparedStatement ps = con.prepareStatement(sqlPac, Statement.RETURN_GENERATED_KEYS)) {
				ps.setString(1, p.getNombre());
				ps.setDate(2, Date.valueOf(p.getFechaNacimiento()));
				ps.setString(3, p.getTelefono());
				ps.setString(4, p.getEmail());
				ps.setString(5, p.getDireccion());
				ps.setString(6, p.getNifNie());
				ps.executeUpdate();
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (!rs.next()) {
						con.rollback();
						return false;
					}
					idPaciente = rs.getLong(1);
					p.setIdPaciente(idPaciente);
				}
			}

			long numHistoria;
			String desc = (p.getHistorial() != null && p.getHistorial().getDescripcion() != null)
					? p.getHistorial().getDescripcion()
					: "";
			try (PreparedStatement ps = con.prepareStatement(sqlHist, Statement.RETURN_GENERATED_KEYS)) {
				ps.setString(1, desc);
				ps.setLong(2, idPaciente);
				ps.executeUpdate();
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (!rs.next()) {
						con.rollback();
						return false;
					}
					numHistoria = rs.getLong(1);
					if (p.getHistorial() == null) {
						p.setHistorial(new Historial());
					}
					p.getHistorial().setIdHistorial(numHistoria);
					p.getHistorial().setIdPaciente(idPaciente);
				}
			}

			if (idsAlergias != null && !idsAlergias.isEmpty()) {
				try (PreparedStatement ps = con.prepareStatement(sqlAler)) {
					for (Long idAlergia : idsAlergias) {
						ps.setLong(1, numHistoria);
						ps.setLong(2, idAlergia);
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
			System.err.println("Error al insertar paciente: " + e.getMessage());
			return false;
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				System.err.println("Error al restaurar autoCommit: " + e.getMessage());
			}
		}
	}

	public boolean actualizarPaciente(Paciente p, List<Long> idsAlergias) {
		Connection con = ConexionBD.getConexion();
		String sqlPac = "UPDATE paciente SET nombre_completo = ?, fecha_nacimiento = ?, telefono = ?, "
				+ "email = ?, direccion = ?, nif_nie = ? WHERE id_paciente = ?";
		String sqlHist = "UPDATE historial SET descripcion = ? WHERE num_historia = ?";
		String sqlDelAler = "DELETE FROM historial_alergia WHERE num_historia = ?";
		String sqlInsAler = "INSERT INTO historial_alergia (num_historia, id_alergia) VALUES (?, ?)";

		try {
			con.setAutoCommit(false);

			try (PreparedStatement ps = con.prepareStatement(sqlPac)) {
				ps.setString(1, p.getNombre());
				ps.setDate(2, Date.valueOf(p.getFechaNacimiento()));
				ps.setString(3, p.getTelefono());
				ps.setString(4, p.getEmail());
				ps.setString(5, p.getDireccion());
				ps.setString(6, p.getNifNie());
				ps.setLong(7, p.getIdPaciente());
				ps.executeUpdate();
			}

			if (p.getHistorial() != null && p.getHistorial().getIdHistorial() != null) {
				long numHistoria = p.getHistorial().getIdHistorial();
				try (PreparedStatement ps = con.prepareStatement(sqlHist)) {
					ps.setString(1, p.getHistorial().getDescripcion());
					ps.setLong(2, numHistoria);
					ps.executeUpdate();
				}

				if (idsAlergias != null) {
					try (PreparedStatement ps = con.prepareStatement(sqlDelAler)) {
						ps.setLong(1, numHistoria);
						ps.executeUpdate();
					}
					try (PreparedStatement ps = con.prepareStatement(sqlInsAler)) {
						for (Long idAlergia : idsAlergias) {
							ps.setLong(1, numHistoria);
							ps.setLong(2, idAlergia);
							ps.executeUpdate();
						}
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
			System.err.println("Error al actualizar paciente: " + e.getMessage());
			return false;
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				System.err.println("Error al restaurar autoCommit: " + e.getMessage());
			}
		}
	}

	public Paciente buscarPorId(long idPaciente) {
		String sql = "SELECT p.id_paciente, p.nombre_completo, p.fecha_nacimiento, p.telefono, p.email, p.direccion, p.nif_nie, "
				+ "h.num_historia, h.descripcion "
				+ "FROM paciente p LEFT JOIN historial h ON p.id_paciente = h.id_paciente "
				+ "WHERE p.id_paciente = ?";
		try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
			ps.setLong(1, idPaciente);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapearPaciente(rs);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al buscar paciente por ID: " + e.getMessage());
		}
		return null;
	}

	public List<Paciente> buscarPorFiltros(String nombre, String telefono, String email, String nifNie) {
		List<Paciente> lista = new ArrayList<>();
		StringBuilder sql = new StringBuilder(
				"SELECT p.id_paciente, p.nombre_completo, p.fecha_nacimiento, p.telefono, p.email, p.direccion, p.nif_nie, "
						+ "h.num_historia, h.descripcion "
						+ "FROM paciente p LEFT JOIN historial h ON p.id_paciente = h.id_paciente WHERE 1=1");
		List<String> parametros = new ArrayList<>();

		if (nombre != null && !nombre.isBlank()) {
			sql.append(" AND p.nombre_completo LIKE ?");
			parametros.add("%" + nombre.trim() + "%");
		}
		if (telefono != null && !telefono.isBlank()) {
			sql.append(" AND p.telefono LIKE ?");
			parametros.add("%" + telefono.trim() + "%");
		}
		if (email != null && !email.isBlank()) {
			sql.append(" AND p.email LIKE ?");
			parametros.add("%" + email.trim() + "%");
		}
		if (nifNie != null && !nifNie.isBlank()) {
			sql.append(" AND p.nif_nie LIKE ?");
			parametros.add("%" + nifNie.trim() + "%");
		}
		sql.append(" ORDER BY p.id_paciente");

		try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql.toString())) {
			for (int i = 0; i < parametros.size(); i++) {
				ps.setString(i + 1, parametros.get(i));
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(mapearPaciente(rs));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al buscar pacientes: " + e.getMessage());
		}
		return lista;
	}

	public List<Long> obtenerIdsAlergias(long numHistoria) {
		List<Long> ids = new ArrayList<>();
		String sql = "SELECT id_alergia FROM historial_alergia WHERE num_historia = ? ORDER BY id_alergia";
		try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
			ps.setLong(1, numHistoria);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					ids.add(rs.getLong("id_alergia"));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener alergias del historial: " + e.getMessage());
		}
		return ids;
	}

	private Paciente mapearPaciente(ResultSet rs) throws SQLException {
		Paciente p = new Paciente();
		p.setIdPaciente(rs.getLong("id_paciente"));
		p.setNombre(rs.getString("nombre_completo"));
		Date fn = rs.getDate("fecha_nacimiento");
		if (fn != null) {
			p.setFechaNacimiento(fn.toLocalDate());
		}
		p.setTelefono(rs.getString("telefono"));
		p.setEmail(rs.getString("email"));
		p.setDireccion(rs.getString("direccion"));
		p.setNifNie(rs.getString("nif_nie"));

		long numHist = rs.getLong("num_historia");
		if (!rs.wasNull()) {
			Historial h = new Historial();
			h.setIdHistorial(numHist);
			h.setDescripcion(rs.getString("descripcion"));
			h.setIdPaciente(p.getIdPaciente());
			p.setHistorial(h);
		}
		return p;
	}
}