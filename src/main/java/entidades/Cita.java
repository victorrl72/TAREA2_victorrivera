package entidades;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Cita {
	
	private Long idCita;
	private LocalDate fecha;
	private LocalDateTime hora;
	private String anotacion;
	
	//tratamiento de cada cita
	private Tratamiento tratamiento;

	public Cita() {
		
	}


	public Long getIdCita() {
		return idCita;
	}


	public void setIdCita(Long idCita) {
		this.idCita = idCita;
	}


	public LocalDate getFecha() {
		return fecha;
	}


	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}


	public LocalDateTime getHora() {
		return hora;
	}


	public void setHora(LocalDateTime hora) {
		this.hora = hora;
	}


	public String getAnotacion() {
		return anotacion;
	}


	public void setAnotacion(String anotacion) {
		this.anotacion = anotacion;
	}
	
	
	public Tratamiento getTratamiento() {
        return tratamiento;
    }

	
    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }
	
} 
