package dao;

import java.sql.Connection;
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
		String sql = "SELECT id_especialidad, nombre FROM especialidad ORDER BY id_especialidad";
		try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				lista.add(new Especialidad(rs.getLong("id_especialidad"), rs.getString("nombre")));
			}
		} catch (SQLException e) {
			System.err.println("Error al listar especialidades: " + e.getMessage());
		}
		return lista;
	}

	public Set<Especialidad> obtenerPorCirujano(long idCirujano) {
		Set<Especialidad> especialidades = new HashSet<>();
		String sql = "SELECT e.id_especialidad, e.nombre "
				+ "FROM especialidad e "
				+ "INNER JOIN cirujano_especialidad ce ON e.id_especialidad = ce.id_especialidad "
				+ "WHERE ce.id_cirujano = ? ORDER BY e.id_especialidad";
		try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
			ps.setLong(1, idCirujano);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					especialidades.add(new Especialidad(rs.getLong("id_especialidad"), rs.getString("nombre")));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener especialidades del cirujano: " + e.getMessage());
		}
		return especialidades;
	}
}