package Juego.Carta;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table
public class Partidajugador implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idpartidajugador;
	
	@Column
	private int partidaidpartida;
	
	@Column
	private int jugadoridjugador;

	public int getIdpartidajugador() {
		return idpartidajugador;
	}

	public void setIdpartidajugador(int idpartidajugador) {
		this.idpartidajugador = idpartidajugador;
	}

	public int getPartidaidpartida() {
		return partidaidpartida;
	}

	public void setPartidaidpartida(int partidaidpartida) {
		this.partidaidpartida = partidaidpartida;
	}

	public int getJugadoridjugador() {
		return jugadoridjugador;
	}

	public void setJugadoridjugador(int jugadoridjugador) {
		this.jugadoridjugador = jugadoridjugador;
	}
	
	
	
}
