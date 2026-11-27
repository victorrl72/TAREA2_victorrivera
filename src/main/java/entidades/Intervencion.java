package entidades;

import java.util.Set;

public class Intervencion extends Cita{

	private Long idIntervencion;
	private int duracion;
	private String materialUsado;
	private String desarrollo;
	private String pruebasHechas;
	
	//empleadois participantes
	private Set<Enfermeria> enfermerosParticipantes;
	private Set<Cirujano> cirujanosParticipantes;
	
	public Intervencion() {
		
	}

	public Long getIdIntervencion() {
		return idIntervencion;
	}

	public void setIdIntervencion(Long idIntervencion) {
		this.idIntervencion = idIntervencion;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public String getMaterialUsado() {
		return materialUsado;
	}

	public void setMaterialUsado(String materialUsado) {
		this.materialUsado = materialUsado;
	}

	public String getDesarrollo() {
		return desarrollo;
	}

	public void setDesarrollo(String desarrollo) {
		this.desarrollo = desarrollo;
	}

	public String getPruebasHechas() {
		return pruebasHechas;
	}

	public void setPruebasHechas(String pruebasHechas) {
		this.pruebasHechas = pruebasHechas;
	}

	public Set<Enfermeria> getEnfermerosParticipantes() {
		return enfermerosParticipantes;
	}

	public void setEnfermerosParticipantes(Set<Enfermeria> enfermerosParticipantes) {
		this.enfermerosParticipantes = enfermerosParticipantes;
	}

	public Set<Cirujano> getCirujanosParticipantes() {
		return cirujanosParticipantes;
	}

	public void setCirujanosParticipantes(Set<Cirujano> cirujanosParticipantes) {
		this.cirujanosParticipantes = cirujanosParticipantes;
	}
	
	
	
}
