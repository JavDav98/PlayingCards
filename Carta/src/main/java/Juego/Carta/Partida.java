package Juego.Carta;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table
public class Partida implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idpartida;
	
	@Column
	public String nombre;
	
	@Column
	public String alias;
	
	@Column
	private int ganador;
	
	@OneToMany(cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = "partidaidpartida")
	private List<Partidajugador> partidajugador;

	public int getIdpartida() {
		return idpartida;
	}

	public void setIdpartida(int idpartida) {
		this.idpartida = idpartida;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getGanador() {
		return ganador;
	}

	public void setGanador(int ganador) {
		this.ganador = ganador;
	}

	public List<Partidajugador> getPartidajugador() {
		return partidajugador;
	}

	public void setPartidajugador(List<Partidajugador> partidajugador) {
		this.partidajugador = partidajugador;
	}
	
	
}
