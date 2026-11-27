package vista;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import conexion.ConexionBD;
import entidades.Alergia;
import entidades.Cirujano;
import entidades.Empleado;
import entidades.Enfermeria;
import entidades.Especialidad;
import entidades.Historial;
import entidades.Medicamento;
import entidades.Paciente;
import entidades.Perfil;
import entidades.Secretariado;
import entidades.Sesion;
import servicios.ServiciosAlergia;
import servicios.ServiciosEmpleado;
import servicios.ServiciosPaciente;
import servicios.ServiciosSesion;

public class Clinica_Principal {

	private static final Scanner sc = new Scanner(System.in);
	private static final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private static ServiciosSesion serviciosSesion = new ServiciosSesion();
	private static ServiciosEmpleado serviciosEmpleado = new ServiciosEmpleado();
	private static ServiciosAlergia serviciosAlergia = new ServiciosAlergia();
	private static ServiciosPaciente serviciosPaciente = new ServiciosPaciente();

	public static void main(String[] args) {
		boolean salir = false;
		while (!salir) {
			Sesion sesion = serviciosSesion.getSesionActual();
			if (sesion.getPerfil() == Perfil.invitado) {
				salir = menuInvitado();
			} else if (sesion.getPerfil() == Perfil.secretariado) {
				menuSecretariado();
			} else if (sesion.getPerfil() == Perfil.enfermeria) {
				menuEnfermeria();
			} else if (sesion.getPerfil() == Perfil.cirugia) {
				menuCirugia();
			} else if (sesion.getPerfil() == Perfil.admin) {
				menuAdmin();
			}
		}
		ConexionBD.cerrarConexion();
		sc.close();
		System.out.println("Saliendo...");
	}

	private static boolean menuInvitado() {
		System.out.println("\nCLÍNICA - Invitado");
		System.out.println("1- Iniciar Sesión");
		System.out.println("0- Salir");
		System.out.print("Elija una opción: ");

		String opcion = sc.nextLine();

		switch (opcion) {
		case "1":
			iniciarSesion();
			break;
		case "0":
			return true;
		default:
			System.out.println("Opción no válida, inténtalo de nuevo.");
		}
		return false;
	}

	private static void iniciarSesion() {
		System.out.print("Introduce el nombre de usuario: ");
		String usuario = sc.nextLine().trim();

		System.out.print("Introduce la contraseña: ");
		String password = sc.nextLine().trim();

		String error = serviciosSesion.login(usuario, password);
		if (error == null) {
			Sesion sesion = serviciosSesion.getSesionActual();
			System.out.println("Bienvenido, " + sesion.getNombre());
		} else {
			System.out.println("Error: " + error);
		}
	}

	private static void menuSecretariado() {
		Sesion sesion = serviciosSesion.getSesionActual();
		System.out.println("CLÍNICA - Menú Secretariado (" + sesion.getNombre() + ")");
		System.out.println("1- Dar de alta nuevo empleado");
		System.out.println("2- Gestionar pacientes");
		System.out.println("0- Cerrar Sesión (Logout)");
		System.out.print("Elija una opción: ");

		String opcion = sc.nextLine();

		switch (opcion) {
		case "1":
			altaEmpleado();
			break;
		case "2":
			menuGestionPacientes();
			break;
		case "0":
			cerrarSesion();
			break;
		default:
			System.out.println("Opción no válida.");
		}
	}

	private static void menuEnfermeria() {
		Sesion sesion = serviciosSesion.getSesionActual();
		System.out.println("CLÍNICA - Menú Enfermería (" + sesion.getNombre() + ")");
		System.out.println("1- Gestionar Alergias y Medicamentos");
		System.out.println("0- Cerrar Sesión");
		System.out.print("Elija una opción: ");

		String opcion = sc.nextLine();

		switch (opcion) {
		case "1":
			menuGestionAlergias();
			break;
		case "0":
			cerrarSesion();
			break;
		default:
			System.out.println("Opción no válida.");
		}
	}

	private static void menuCirugia() {
		Sesion sesion = serviciosSesion.getSesionActual();
		System.out.println("CLÍNICA - Menú Cirugía (" + sesion.getNombre() + ")");
		System.out.println("1- Ver mi ficha (CU6)");
		System.out.println("0- Cerrar Sesión");
		System.out.print("Elija una opción: ");

		String opcion = sc.nextLine();

		switch (opcion) {
		case "1":
			verFichaCirujano();
			break;
		case "0":
			cerrarSesion();
			break;
		default:
			System.out.println("Opción no válida.");
		}
	}

	private static void menuAdmin() {
		Sesion sesion = serviciosSesion.getSesionActual();
		System.out.println("CLÍNICA - Menú Administrador (" + sesion.getNombre() + ")");
		System.out.println("1- Dar de alta nuevo empleado");
		System.out.println("2- Gestionar Alergias y Medicamentos");
		System.out.println("3- Gestionar pacientes");
		System.out.println("0- Cerrar Sesión (Logout)");
		System.out.print("Elija una opción: ");

		String opcion = sc.nextLine();

		switch (opcion) {
		case "1":
			altaEmpleado();
			break;
		case "2":
			menuGestionAlergias();
			break;
		case "3":
			menuGestionPacientes();
			break;
		case "0":
			cerrarSesion();
			break;
		default:
			System.out.println("Opción no válida.");
		}
	}

	private static void cerrarSesion() {
		serviciosSesion.logout();
		System.out.println("Sesión cerrada correctamente. Volviendo al menú de Invitado.");
	}

	//alta de empleado

	private static void altaEmpleado() {
		System.out.println("\nALTA DE NUEVO EMPLEADO");
		System.out.print("Introduce el perfil (secretariado / enfermeria / cirugia): ");
		String perfil = sc.nextLine().trim().toLowerCase();

		if (!perfil.equals("secretariado") && !perfil.equals("enfermeria") && !perfil.equals("cirugia")) {
			System.out.println("Error: Perfil no válido. Debe ser 'secretariado', 'enfermeria' o 'cirugia'.");
			return;
		}

		System.out.print("Introduce el nombre de usuario: ");
		String usuario = sc.nextLine().trim();
		System.out.print("Introduce la contraseña: ");
		String password = sc.nextLine().trim();
		System.out.print("Introduce el nombre y apellidos: ");
		String nombrePersona = sc.nextLine().trim();
		System.out.print("Introduce el teléfono: ");
		String telefono = sc.nextLine().trim();
		System.out.print("Introduce el email: ");
		String email = sc.nextLine().trim();
		System.out.print("Introduce el NIF/NIE: ");
		String nif = sc.nextLine().trim();
		System.out.print("Introduce la dirección: ");
		String direccion = sc.nextLine().trim();

		Empleado nuevo = null;

		if (perfil.equals("secretariado")) {
			Secretariado sec = new Secretariado();
			int anios = leerEntero("Introduce los años de experiencia: ");
			sec.settExperiencia(anios);
			nuevo = sec;
		} else if (perfil.equals("enfermeria")) {
			Enfermeria enf = new Enfermeria();
			System.out.print("Introduce la categoría (A, B o C): ");
			String cat = sc.nextLine().trim().toUpperCase();
			enf.setCategoria(cat.isEmpty() ? ' ' : cat.charAt(0));
			nuevo = enf;
		} else {
			Cirujano cir = new Cirujano();
			List<Especialidad> todas = serviciosEmpleado.listarEspecialidades();
			System.out.println("Especialidades disponibles:");
			for (Especialidad e : todas) {
				System.out.println("  " + e);
			}
			System.out.print("Introduce los IDs de las especialidades separados por comas (ej: 1,4): ");
			String linea = sc.nextLine().trim();
			Set<Especialidad> elegidas = new HashSet<>();
			if (!linea.isEmpty()) {
				String[] partes = linea.split(",");
				for (String parte : partes) {
					try {
						long idEsp = Long.parseLong(parte.trim());
						for (Especialidad e : todas) {
							if (e.getIdEspecialidad() == idEsp) {
								elegidas.add(e);
							}
						}
					} catch (NumberFormatException e) {
						System.out.println("ID no válido ignorado: " + parte.trim());
					}
				}
			}
			cir.setEspecialidades(elegidas);
			nuevo = cir;
		}

		nuevo.setUsuario(usuario);
		nuevo.setPassword(password);
		nuevo.setNombre(nombrePersona);
		nuevo.setTelefono(telefono);
		nuevo.setEmail(email);
		nuevo.setNifNie(nif);
		nuevo.setDireccion(direccion);

		String error = serviciosEmpleado.registrarEmpleado(nuevo);
		if (error == null) {
			System.out.println("El empleado ha sido registrado correctamente con ID: " + nuevo.getIdEmpleado());
		} else {
			System.out.println("Error: " + error);
		}
	}

	// gestion alergias y medicamentos  fichero s binarios

	private static void menuGestionAlergias() {
		boolean volver = false;
		while (!volver) {
			System.out.println("GESTIÓN DE ALERGIAS Y MEDICAMENTOS");
			System.out.println("1- Ver alergias (CU3A)");
			System.out.println("2- Nueva alergia a medicamento");
			System.out.println("3- Editar alergia");
			System.out.println("0- Volver al menú principal");
			System.out.print("Elija una opción: ");

			String opcion = sc.nextLine();
			switch (opcion) {
			case "1":
				verAlergias();
				break;
			case "2":
				nuevaAlergia();
				break;
			case "3":
				editarAlergia();
				break;
			case "0":
				volver = true;
				break;
			default:
				System.out.println("Opción no válida.");
			}
		}
	}

	private static void verAlergias() {
		List<Alergia> listaAlergias = serviciosAlergia.listarAlergias();
		if (listaAlergias.isEmpty()) {
			System.out.println("No hay alergias para mostrar.");
			return;
		}

		System.out.println("LISTADO DE ALERGIAS");
		for (Alergia a : listaAlergias) {
			Medicamento m = a.getMedicamento();
			String nombreMed = (m != null) ? m.getNombre() : "Sin medicamento";
			String principioMed = (m != null) ? m.getPrincipio() : "Ninguno";
			int dosisMed = (m != null) ? m.getDosis() : 0;

			System.out.println("ID: " + a.getIdAlergia()
					+ " | Alergia: " + a.getNombre()
					+ " | Medicamento: " + nombreMed
					+ " | Principio: " + principioMed
					+ " | Dosis: " + dosisMed);
		}
	}

	private static void nuevaAlergia() {
		System.out.println("NUEVA ALERGIA");
		System.out.print("Introduce el nombre de la alergia: ");
		String nombreAlergia = sc.nextLine().trim();
		System.out.print("Introduce el nombre del medicamento: ");
		String nombreMed = sc.nextLine().trim();
		System.out.print("Introduce el principio activo: ");
		String principio = sc.nextLine().trim();
		int dosisNum = leerEntero("Introduce la dosis máxima en número: ");

		String error = serviciosAlergia.registrarAlergia(nombreAlergia, nombreMed, principio, dosisNum);
		if (error == null) {
			System.out.println("Alergia guardada con éxito.");
		} else {
			System.out.println("Error: " + error);
		}
	}

	private static void editarAlergia() {
		System.out.println("EDITAR ALERGIA");
		long idBuscado = leerLong("Introduce el ID de la alergia a editar: ");
		Alergia alergia = serviciosAlergia.buscarPorId(idBuscado);

		if (alergia == null) {
			System.out.println("Error: No existe ninguna alergia con ese ID.");
			return;
		}

		System.out.println("Editando alergia: " + alergia.getNombre());
		System.out.print("Nuevo nombre de la alergia (dejar en blanco para mantener): ");
		String nuevoNombre = sc.nextLine().trim();
		if (!nuevoNombre.isEmpty()) {
			alergia.setNombre(nuevoNombre);
		}

		Medicamento med = alergia.getMedicamento();
		if (med != null) {
			System.out.print("Nuevo nombre del medicamento: ");
			String nuevoNomMed = sc.nextLine().trim();
			if (!nuevoNomMed.isEmpty()) {
				med.setNombre(nuevoNomMed);
			}

			System.out.print("Nuevo principio activo: ");
			String nuevoPrin = sc.nextLine().trim();
			if (!nuevoPrin.isEmpty()) {
				med.setPrincipio(nuevoPrin);
			}

			System.out.print("Nueva dosis máxima: ");
			String nuevaDosis = sc.nextLine().trim();
			if (!nuevaDosis.isEmpty()) {
				try {
					med.setDosis(Integer.parseInt(nuevaDosis));
				} catch (NumberFormatException e) {
					System.out.println("Dosis no válida, se mantiene la anterior.");
				}
			}
		}

		String error = serviciosAlergia.editarAlergia(alergia);
		if (error == null) {
			System.out.println("Alergia actualizada correctamente.");
		} else {
			System.out.println("Error: " + error);
		}
	}

	//gestionar pacientes

	private static void menuGestionPacientes() {
		boolean volver = false;
		while (!volver) {
			System.out.println("GESTIÓN DE PACIENTES");
			System.out.println("1- Crear paciente (CU4A)");
			System.out.println("2- Modificar paciente (CU4A)");
			System.out.println("3- Buscar pacientes (CU4B)");
			System.out.println("0- Volver al menú principal");
			System.out.print("Elija una opción: ");

			String opcion = sc.nextLine();
			switch (opcion) {
			case "1":
				crearPaciente();
				break;
			case "2":
				modificarPaciente();
				break;
			case "3":
				buscarPacientes();
				break;
			case "0":
				volver = true;
				break;
			default:
				System.out.println("Opción no válida.");
			}
		}
	}

	private static void crearPaciente() {
		System.out.println("NUEVO PACIENTE");
		System.out.print("Nombre completo: ");
		String nombre = sc.nextLine().trim();
		LocalDate fechaNac = leerFecha("Fecha de nacimiento (dd/MM/yyyy): ");
		System.out.print("Teléfono: ");
		String telefono = sc.nextLine().trim();
		System.out.print("Email: ");
		String email = sc.nextLine().trim();
		System.out.print("Dirección: ");
		String direccion = sc.nextLine().trim();
		System.out.print("NIF/NIE: ");
		String nif = sc.nextLine().trim();
		System.out.print("Descripción inicial del historial: ");
		String descHistorial = sc.nextLine().trim();

		List<Long> idsAlergias = pedirAlergiasPaciente();

		Paciente p = new Paciente();
		p.setNombre(nombre);
		p.setFechaNacimiento(fechaNac);
		p.setTelefono(telefono);
		p.setEmail(email);
		p.setDireccion(direccion);
		p.setNifNie(nif);

		Historial h = new Historial();
		h.setDescripcion(descHistorial);
		p.setHistorial(h);

		String error = serviciosPaciente.crearPaciente(p, idsAlergias);
		if (error == null) {
			System.out.println("Paciente creado correctamente con ID: " + p.getIdPaciente()
					+ " y Nº Historia: " + p.getHistorial().getIdHistorial());
		} else {
			System.out.println("Error: " + error);
		}
	}

	private static void modificarPaciente() {
		System.out.println("MODIFICAR PACIENTE");
		long id = leerLong("Introduce el ID del paciente a modificar: ");
		Paciente p = serviciosPaciente.buscarPorId(id);

		if (p == null) {
			System.out.println("No existe ningún paciente con ID " + id);
			return;
		}

		System.out.println("Deja en blanco para mantener el valor actual.");

		System.out.print("Nombre [" + p.getNombre() + "]: ");
		String nombre = sc.nextLine().trim();
		if (!nombre.isEmpty()) p.setNombre(nombre);

		System.out.print("Fecha nacimiento [" + p.getFechaNacimiento().format(formatoFecha) + "] (dd/MM/yyyy): ");
		String fechaStr = sc.nextLine().trim();
		if (!fechaStr.isEmpty()) {
			try {
				p.setFechaNacimiento(LocalDate.parse(fechaStr, formatoFecha));
			} catch (DateTimeParseException e) {
				System.out.println("Fecha no válida, se mantiene la anterior.");
			}
		}

		System.out.print("Teléfono [" + p.getTelefono() + "]: ");
		String telefono = sc.nextLine().trim();
		if (!telefono.isEmpty()) p.setTelefono(telefono);

		System.out.print("Email [" + p.getEmail() + "]: ");
		String email = sc.nextLine().trim();
		if (!email.isEmpty()) p.setEmail(email);

		System.out.print("Dirección [" + p.getDireccion() + "]: ");
		String direccion = sc.nextLine().trim();
		if (!direccion.isEmpty()) p.setDireccion(direccion);

		System.out.print("NIF/NIE [" + p.getNifNie() + "]: ");
		String nif = sc.nextLine().trim();
		if (!nif.isEmpty()) p.setNifNie(nif);

		if (p.getHistorial() != null) {
			System.out.print("Descripción historial [" + p.getHistorial().getDescripcion() + "]: ");
			String desc = sc.nextLine().trim();
			if (!desc.isEmpty()) p.getHistorial().setDescripcion(desc);
		}

		List<Long> idsAlergias = null;
		System.out.print("¿Quieres actualizar sus alergias? (S/N): ");
		if (sc.nextLine().trim().equalsIgnoreCase("S")) {
			idsAlergias = pedirAlergiasPaciente();
		}

		String error = serviciosPaciente.modificarPaciente(p, idsAlergias);
		if (error == null) {
			System.out.println("Paciente modificado correctamente.");
		} else {
			System.out.println("Error: " + error);
		}
	}

	private static List<Long> pedirAlergiasPaciente() {
		List<Long> idsSeleccionados = new ArrayList<>();
		List<Alergia> disponibles = serviciosAlergia.listarAlergias();

		if (disponibles.isEmpty()) {
			System.out.println("No hay alergias registradas en el sistema.");
			return idsSeleccionados;
		}

		System.out.print("¿Tiene alguna alergia? (S/N): ");
		if (!sc.nextLine().trim().equalsIgnoreCase("S")) {
			return idsSeleccionados;
		}

		verAlergias();
		System.out.print("Introduce los IDs de las alergias separados por comas (ej: 1,2): ");
		String linea = sc.nextLine().trim();
		if (!linea.isEmpty()) {
			String[] partes = linea.split(",");
			for (String parte : partes) {
				try {
					long idAlergia = Long.parseLong(parte.trim());
					if (serviciosAlergia.buscarPorId(idAlergia) != null && !idsSeleccionados.contains(idAlergia)) {
						idsSeleccionados.add(idAlergia);
					} else {
						System.out.println("Alergia con ID " + idAlergia + " no encontrada.");
					}
				} catch (NumberFormatException e) {
					System.out.println("ID no válido ignorado: " + parte.trim());
				}
			}
		}
		return idsSeleccionados;
	}

	private static void buscarPacientes() {
		System.out.println("BUSCAR PACIENTES (deja en blanco los campos que no quieras filtrar)");
		System.out.print("Nombre: ");
		String nombre = sc.nextLine();
		System.out.print("Teléfono: ");
		String telefono = sc.nextLine();
		System.out.print("Email: ");
		String email = sc.nextLine();
		System.out.print("NIF/NIE: ");
		String nif = sc.nextLine();

		List<Paciente> resultados = serviciosPaciente.buscarPacientes(nombre, telefono, email, nif);
		if (resultados.isEmpty()) {
			System.out.println("No se encontraron pacientes.");
			return;
		}

		System.out.println("Pacientes encontrados (" + resultados.size() + "):");
		for (Paciente p : resultados) {
			System.out.println("ID: " + p.getIdPaciente() + " | Nombre: " + p.getNombre());
			System.out.println("NIF/NIE: " + p.getNifNie() + " | Fecha Nac: "
					+ (p.getFechaNacimiento() != null ? p.getFechaNacimiento().format(formatoFecha) : "-"));
			System.out.println("Teléfono: " + p.getTelefono() + " | Email: " + p.getEmail());
			System.out.println("Dirección: " + p.getDireccion());
			if (p.getHistorial() != null) {
				System.out.println("Nº Historia: " + p.getHistorial().getIdHistorial()
						+ " | Descripción: " + p.getHistorial().getDescripcion());
				if (p.getHistorial().getAlergias().isEmpty()) {
					System.out.println("Alergias: Ninguna");
				} else {
					System.out.print("Alergias: ");
					for (Alergia a : p.getHistorial().getAlergias()) {
						System.out.print("[" + a.getNombre() + " - " + a.getMedicamento().getNombre() + "] ");
					}
					System.out.println();
				}
			}
		}
		System.out.println(" ");
	}

	//ver ficha de cirujano

	private static void verFichaCirujano() {
		Sesion s = serviciosSesion.getSesionActual();
		Cirujano cir = serviciosEmpleado.obtenerFichaCirujano(s.getIdUsuario());

		if (cir == null) {
			System.out.println("No se pudo recuperar tu ficha de cirujano.");
			return;
		}

		System.out.println("FICHA DE CIRUJANO");
		System.out.println("ID Empleado: " + cir.getIdEmpleado());
		System.out.println("Nombre:      " + cir.getNombre());
		System.out.println("NIF/NIE:     " + cir.getNifNie());
		System.out.println("Teléfono:    " + cir.getTelefono());
		System.out.println("Email:       " + cir.getEmail());
		System.out.println("Dirección:   " + cir.getDireccion());
		System.out.println("Usuario:     " + cir.getUsuario());
		System.out.println("Especialidades:");
		if (cir.getEspecialidades().isEmpty()) {
			System.out.println("  (Sin especialidades asignadas)");
		} else {
			for (Especialidad e : cir.getEspecialidades()) {
				System.out.println("  - " + e.getNombreUnico());
			}
		}
	}

	//lectura por teclado

	private static int leerEntero(String mensaje) {
		while (true) {
			System.out.print(mensaje);
			try {
				return Integer.parseInt(sc.nextLine().trim());
			} catch (NumberFormatException e) {
				System.out.println("Error: Introduce un número entero válido.");
			}
		}
	}

	private static long leerLong(String mensaje) {
		while (true) {
			System.out.print(mensaje);
			try {
				return Long.parseLong(sc.nextLine().trim());
			} catch (NumberFormatException e) {
				System.out.println("Error: Introduce un ID numérico válido.");
			}
		}
	}

	private static LocalDate leerFecha(String mensaje) {
		while (true) {
			System.out.print(mensaje);
			try {
				return LocalDate.parse(sc.nextLine().trim(), formatoFecha);
			} catch (DateTimeParseException e) {
				System.out.println("Error: Formato incorrecto. Usa dd/MM/yyyy (ej: 15/05/1998).");
			}
		}
	}
}