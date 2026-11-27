package servicios;

import java.util.List;

import dao.EmpleadoDAO;
import dao.EspecialidadDAO;
import entidades.Cirujano;
import entidades.Empleado;
import entidades.Enfermeria;
import entidades.Especialidad;
import entidades.Secretariado;

public class ServiciosEmpleado {

	private EmpleadoDAO empleadoDAO = new EmpleadoDAO();
	private EspecialidadDAO especialidadDAO = new EspecialidadDAO();

	public List<Especialidad> listarEspecialidades() {
		return especialidadDAO.listarTodas();
	}

	public String registrarEmpleado(Empleado emp) {
		if (emp == null) {
			return "Datos del empleado vacios.";
		}
		if (estaVacio(emp.getNombre()) || estaVacio(emp.getTelefono()) || estaVacio(emp.getEmail())
				|| estaVacio(emp.getNifNie()) || estaVacio(emp.getDireccion())) {
			return "Todos los datos personales son obligatorios.";
		}
		if (estaVacio(emp.getUsuario()) || emp.getUsuario().contains(" ")) {
			return "El nombre de usuario no puede estar vacio ni contener espacios.";
		}
		if (estaVacio(emp.getPassword()) || emp.getPassword().contains(" ")) {
			return "La contrasena no puede estar vacia ni contener espacios.";
		}
		if (empleadoDAO.existeUsuario(emp.getUsuario())) {
			return "Ya existe un empleado con ese nombre de usuario.";
		}
		if (empleadoDAO.existeNif(emp.getNifNie())) {
			return "Ya existe un empleado con ese NIF/NIE.";
		}

		if (emp instanceof Secretariado) {
			Secretariado sec = (Secretariado) emp;
			if (sec.gettExperiencia() < 0) {
				return "Los anios de experiencia no pueden ser negativos.";
			}
		} else if (emp instanceof Enfermeria) {
			Enfermeria enf = (Enfermeria) emp;
			char cat = Character.toUpperCase(enf.getCategoria());
			if (cat != 'A' && cat != 'B' && cat != 'C') {
				return "La categoria de enfermeria debe ser A, B o C.";
			}
			enf.setCategoria(cat);
		} else if (emp instanceof Cirujano) {
			Cirujano cir = (Cirujano) emp;
			if (cir.getEspecialidades() == null || cir.getEspecialidades().isEmpty()) {
				return "Un cirujano debe tener al menos una especialidad.";
			}
		}

		boolean guardado = empleadoDAO.insertarEmpleado(emp);
		if (!guardado) {
			return "Error al guardar el empleado en la base de datos.";
		}
		return null;
	}

	public Cirujano obtenerFichaCirujano(long idEmpleado) {
		return empleadoDAO.obtenerCirujanoPorId(idEmpleado);
	}

	private boolean estaVacio(String texto) {
		return texto == null || texto.trim().isEmpty();
	}
}