package entidades;

import java.time.LocalDate;
import java.util.Set;

public class Tratamiento {

	
	private Long idTratamiento;
	private String nombre;
	private LocalDate fechaInicio= LocalDate.now();
	private Boolean consentimiento = false;
	//informe global de cada tratamiento
	private String informeGlobal;
	
	private Long idPaciente; //carga ligera 
	private Paciente paciente; //carga pesada
	
	//cada tratamiento se compone de citas
	private Set<Cita> citas;
	
	
	public Tratamiento () {
		
	}
	
	public Long getIdTratamiento() {
		return idTratamiento;
	}




	public void setIdTratamiento(Long idTratamiento) {
		this.idTratamiento = idTratamiento;
	}




	public String getNombre() {
		return nombre;
	}




	public void setNombre(String nombre) {
		this.nombre = nombre;
	}




	public LocalDate getFechaInicio() {
		return fechaInicio;
	}




	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}




	public Boolean getConsentimiento() {
		return consentimiento;
	}



	public void setConsentimiento(Boolean consentimiento) {
		this.consentimiento = consentimiento;
	}




	public Long getIdPaciente() {
		return idPaciente;
	}




	public void setIdPaciente(Long idPaciente) {
		this.idPaciente = idPaciente;
	}




	public Paciente getPaciente() {
		return paciente;
	}
	


	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}
	

	public Set<Cita> getCitas() {
		return citas;
	}

	
	
	public void setCitas(Set<Cita> citas) {
		this.citas = citas;
	}


	
}
