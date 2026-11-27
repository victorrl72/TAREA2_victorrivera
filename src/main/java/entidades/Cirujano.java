package entidades;

import java.util.HashSet;
import java.util.Set;

public class Cirujano extends Empleado {

	private Set<Especialidad> especialidades = new HashSet<>();

	public Cirujano() {
		setPerfil(Perfil.cirugia);
	}

	public Set<Especialidad> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(Set<Especialidad> especialidades) {
		this.especialidades = especialidades;
	}
}