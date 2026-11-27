package servicios;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dao.AlergiaFicheroDAO;
import dao.PacienteDAO;
import entidades.Alergia;
import entidades.Paciente;

public class ServiciosPaciente {

	private PacienteDAO pacienteDAO = new PacienteDAO();
	private AlergiaFicheroDAO alergiaDAO = new AlergiaFicheroDAO();

	public String crearPaciente(Paciente p, List<Long> idsAlergias) {
		String error = validarPaciente(p, null);
		if (error != null) {
			return error;
		}
		if (!pacienteDAO.insertarPaciente(p, idsAlergias)) {
			return "Error al guardar el paciente en la base de datos.";
		}
		return null;
	}

	public String modificarPaciente(Paciente p, List<Long> idsAlergias) {
		if (p == null || p.getIdPaciente() == null) {
			return "Paciente no valido.";
		}
		String error = validarPaciente(p, p.getIdPaciente());
		if (error != null) {
			return error;
		}
		if (!pacienteDAO.actualizarPaciente(p, idsAlergias)) {
			return "Error al actualizar el paciente en la base de datos.";
		}
		return null;
	}

	public Paciente buscarPorId(long idPaciente) {
		Paciente p = pacienteDAO.buscarPorId(idPaciente);
		if (p != null) {
			cargarAlergiasHistorial(p);
		}
		return p;
	}

	public List<Paciente> buscarPacientes(String nombre, String telefono, String email, String nifNie) {
		List<Paciente> lista = pacienteDAO.buscarPorFiltros(nombre, telefono, email, nifNie);
		for (Paciente p : lista) {
			cargarAlergiasHistorial(p);
		}
		return lista;
	}

	private void cargarAlergiasHistorial(Paciente p) {
		if (p.getHistorial() == null || p.getHistorial().getIdHistorial() == null) {
			return;
		}
		List<Long> ids = pacienteDAO.obtenerIdsAlergias(p.getHistorial().getIdHistorial());
		Set<Alergia> alergias = new HashSet<>();
		for (Long id : ids) {
			Alergia a = alergiaDAO.buscarPorId(id);
			if (a != null) {
				alergias.add(a);
			}
		}
		p.getHistorial().setAlergias(alergias);
	}

	private String validarPaciente(Paciente p, Long idExcluir) {
		if (p == null) {
			return "Datos del paciente vacios.";
		}
		if (estaVacio(p.getNombre()) || estaVacio(p.getTelefono()) || estaVacio(p.getEmail())
				|| estaVacio(p.getDireccion()) || estaVacio(p.getNifNie())) {
			return "Todos los datos personales del paciente son obligatorios.";
		}
		if (p.getFechaNacimiento() == null || p.getFechaNacimiento().isAfter(LocalDate.now())) {
			return "La fecha de nacimiento no es valida.";
		}
		if (pacienteDAO.existeNif(p.getNifNie(), idExcluir)) {
			return "Ya existe un paciente registrado con ese NIF/NIE.";
		}
		return null;
	}

	private boolean estaVacio(String texto) {
		return texto == null || texto.trim().isEmpty();
	}
}