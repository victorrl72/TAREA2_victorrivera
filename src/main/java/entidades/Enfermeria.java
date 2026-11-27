package entidades;

public class Enfermeria extends Empleado {

	private char categoria;

	public Enfermeria() {
		setPerfil(Perfil.enfermeria);
	}

	public char getCategoria() {
		return categoria;
	}

	public void setCategoria(char categoria) {
		this.categoria = categoria;
	}
}