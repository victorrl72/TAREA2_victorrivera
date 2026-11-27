package entidades;

public class Revision extends Cita{

	private Long idRevision;
	private String anotaciones;
	
	public Revision() {
		
	}

	public Long getIdRevision() {
		return idRevision;
	}

	public void setIdRevision(Long idRevision) {
		this.idRevision = idRevision;
	}

	public String getAnotaciones() {
		return anotaciones;
	}

	public void setAnotaciones(String anotaciones) {
		this.anotaciones = anotaciones;
	}
	
}
