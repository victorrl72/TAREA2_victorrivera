package entidades;

public class Alergia implements java.io.Serializable{

	private Long idAlergia;
	private String nombre;
	
	//asocio a medicamento
	private Medicamento medicamento; 
	
	public Alergia() {
		
	}

	public Long getIdAlergia() {
		return idAlergia;
	}

	public void setIdAlergia(Long idAlergia) {
		this.idAlergia = idAlergia;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	
	public Medicamento getMedicamento() {
		return medicamento;
	}
	
	public void setMedicamento(Medicamento medicamento) {
		this.medicamento = medicamento;
	}
	
}
