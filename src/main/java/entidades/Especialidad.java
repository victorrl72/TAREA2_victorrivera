package entidades;

public class Especialidad {

	private Long idEspecialidad;
	private String nombreEspecialidad;

	public Especialidad() {
	}

	public Especialidad(Long idEspecialidad, String nombreEspecialidad) {
		this.idEspecialidad = idEspecialidad;
		this.nombreEspecialidad = nombreEspecialidad;
	}

	public Long getIdEspecialidad() {
		return idEspecialidad;
	}

	public void setIdEspecialidad(Long idEspecialidad) {
		this.idEspecialidad = idEspecialidad;
	}

	public String getNombreUnico() {
		return nombreEspecialidad;
	}

	public void setNombreUnico(String nombreUnico) {
		this.nombreEspecialidad = nombreUnico;
	}

	@Override
	public String toString() {
		return idEspecialidad + ". " + nombreEspecialidad;
	}
}