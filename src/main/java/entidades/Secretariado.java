package entidades;

public class Secretariado extends Empleado {

	private int tExperiencia;

	public Secretariado() {
		setPerfil(Perfil.secretariado);
	}

	public int gettExperiencia() {
		return tExperiencia;
	}

	public void settExperiencia(int tExperiencia) {
		this.tExperiencia = tExperiencia;
	}
}