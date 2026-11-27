package servicios;

import java.util.List;

import dao.AlergiaFicheroDAO;
import entidades.Alergia;
import entidades.Medicamento;

public class ServiciosAlergia {

	private AlergiaFicheroDAO alergiaDAO = new AlergiaFicheroDAO();

	public List<Alergia> listarAlergias() {
		return alergiaDAO.listarTodas();
	}

	public Alergia buscarPorId(long id) {
		return alergiaDAO.buscarPorId(id);
	}

	public String registrarAlergia(String nombreAlergia, String nombreMed, String principioActivo, int dosisMax) {
		if (estaVacio(nombreAlergia)) {
			return "El nombre de la alergia es obligatorio.";
		}
		if (estaVacio(nombreMed) || estaVacio(principioActivo)) {
			return "El nombre y el principio activo del medicamento son obligatorios.";
		}
		if (dosisMax <= 0) {
			return "La dosis maxima diaria debe ser mayor que 0.";
		}

		long nuevoId = alergiaDAO.calcularSiguienteId();

		Medicamento med = new Medicamento();
		med.setIdMedicamento(nuevoId);
		med.setNombre(nombreMed.trim());
		med.setPrincipio(principioActivo.trim());
		med.setDosis(dosisMax);

		Alergia alergia = new Alergia();
		alergia.setIdAlergia(nuevoId);
		alergia.setNombre(nombreAlergia.trim());
		alergia.setMedicamento(med);

		if (!alergiaDAO.guardar(alergia)) {
			return "Error al guardar la alergia en el fichero.";
		}
		return null;
	}

	private boolean estaVacio(String texto) {
		return texto == null || texto.trim().isEmpty();
	}
}