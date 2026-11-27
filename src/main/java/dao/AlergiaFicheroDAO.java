package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import entidades.Alergia;

public class AlergiaFicheroDAO {

	private static final String CARPETA_ALERGIAS = "alergias";

	public AlergiaFicheroDAO() {
		File dir = new File(CARPETA_ALERGIAS);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public List<Alergia> listarTodas() {
		List<Alergia> lista = new ArrayList<>();
		File dir = new File(CARPETA_ALERGIAS);
		File[] ficheros = dir.listFiles((d, name) -> name.endsWith(".dat"));

		if (ficheros != null) {
			for (File f : ficheros) {
				try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
					Object obj = ois.readObject();
					if (obj instanceof Alergia alergia) {
						lista.add(alergia);
					}
				} catch (IOException | ClassNotFoundException e) {
					System.err.println("Error al leer fichero " + f.getName() + ": " + e.getMessage());
				}
			}
		}

		lista.sort(Comparator.comparingLong(Alergia::getIdAlergia));
		return lista;
	}

	public Alergia buscarPorId(long idAlergia) {
		File f = new File(CARPETA_ALERGIAS, "alergia_" + idAlergia + ".dat");
		if (!f.exists()) {
			return null;
		}
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
			Object obj = ois.readObject();
			if (obj instanceof Alergia alergia) {
				return alergia;
			}
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Error al leer alergia " + idAlergia + ": " + e.getMessage());
		}
		return null;
	}

	public long calcularSiguienteId() {
		List<Alergia> existentes = listarTodas();
		long maxId = 0;
		for (Alergia a : existentes) {
			if (a.getIdAlergia() != null && a.getIdAlergia() > maxId) {
				maxId = a.getIdAlergia();
			}
		}
		return maxId + 1;
	}

	public boolean guardar(Alergia alergia) {
		File dir = new File(CARPETA_ALERGIAS);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File f = new File(dir, "alergia_" + alergia.getIdAlergia() + ".dat");
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
			oos.writeObject(alergia);
			return true;
		} catch (IOException e) {
			System.err.println("Error al guardar alergia: " + e.getMessage());
			return false;
		}
	}
}