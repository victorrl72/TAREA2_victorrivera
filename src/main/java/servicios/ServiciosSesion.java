package servicios;

import conexion.ConexionBD;
import dao.EmpleadoDAO;
import entidades.Empleado;
import entidades.Perfil;
import entidades.Sesion;

public class ServiciosSesion {

	private EmpleadoDAO empleadoDAO = new EmpleadoDAO();
	private Sesion sesionActual = new Sesion();

	public Sesion getSesionActual() {
		return sesionActual;
	}

	public boolean estaAutenticado() {
		return sesionActual.getPerfil() != Perfil.invitado;
	}

	public String login(String usuario, String password) {
		if (estaAutenticado()) {
			return "Ya tienes una sesion iniciada. Debes cerrar sesion antes de volver a entrar.";
		}
		if (usuario == null || usuario.trim().isEmpty() || password == null || password.trim().isEmpty()) {
			return "El usuario y la contrasena no pueden estar vacios.";
		}

		String adminUser = ConexionBD.getPropiedad("admin.user");
		String adminPass = ConexionBD.getPropiedad("admin.password");

		if (usuario.equals(adminUser) && password.equals(adminPass)) {
			sesionActual = new Sesion(0L, adminUser, "Administrador General", Perfil.admin);
			return null;
		}

		Empleado emp = empleadoDAO.buscarPorCredenciales(usuario, password);
		if (emp != null) {
			sesionActual = new Sesion(emp.getIdEmpleado(), emp.getUsuario(), emp.getNombre(), emp.getPerfil());
			return null;
		}

		return "Credenciales incorrectas.";
	}

	public void logout() {
		sesionActual = new Sesion();
	}
}