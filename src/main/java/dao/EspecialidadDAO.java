package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import conexion.ConexionBD;
import entidades.Especialidad;

public class EspecialidadDAO {

	public List<Especialidad> listarTodas() {
		List<Especialidad> lista = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT id_especialidad, nombre FROM especialidad ORDER BY id_especialidad";

		try {
			ps = ConexionBD.getConexion().prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				Especialidad esp = new Especialidad();
				esp.setIdEspecialidad(rs.getLong("id_especialidad"));
				esp.setNombreUnico(rs.getString("nombre"));
				lista.add(esp);
			}
		} catch (SQLException e) {
			System.out.println("Error al listar especialidades: " + e.getMessage());
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
			} catch (SQLException e) {
				System.out.println("Error al cerrar recursos: " + e.getMessage());
			}
		}
		return lista;
	}

	public Set<Especialidad> obtenerPorCirujano(long idCirujano) {
		Set<Especialidad> especialidades = new HashSet<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT e.id_especialidad, e.nombre "
				+ "FROM especialidad e "
				+ "INNER JOIN cirujano_especialidad ce ON e.id_especialidad = ce.id_especialidad "
				+ "WHERE ce.id_cirujano = ? ORDER BY e.id_especialidad";

		try {
			ps = ConexionBD.getConexion().prepareStatement(sql);
			ps.setLong(1, idCirujano);
			rs = ps.executeQuery();
			while (rs.next()) {
				Especialidad esp = new Especialidad();
				esp.setIdEspecialidad(rs.getLong("id_especialidad"));
				esp.setNombreUnico(rs.getString("nombre"));
				especialidades.add(esp);
			}
		} catch (SQLException e) {
			System.out.println("Error al obtener especialidades del cirujano: " + e.getMessage());
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
			} catch (SQLException e) {
				System.out.println("Error al cerrar recursos: " + e.getMessage());
			}
		}
		return especialidades;
	}
}