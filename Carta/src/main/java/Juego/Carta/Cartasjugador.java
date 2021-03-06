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
public class Cartasjugador implements Serializable{

	/**
	 * 
	 * Representa la asignación de cartas a un jugador, conforme avance la partida algunas cartas seran desasignadas del jugador 1 y asignadas al jugador 2
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idcartasj;
	
	@Column
	private int jugadoridjugador;
	
	@Column
	private int cartasidcartas;

	public int getIdcartasj() {
		return idcartasj;
	}

	public void setIdcartasj(int idcartasj) {
		this.idcartasj = idcartasj;
	}

	public int getCartasidcartas() {
		return cartasidcartas;
	}

	public void setCartasidcartas(int cartasidcartas) {
		this.cartasidcartas = cartasidcartas;
	}

	public int getJugadoridjugador() {
		return jugadoridjugador;
	}

	public void setJugadoridjugador(int partidajugadoridpartidajugador) {
		this.jugadoridjugador = partidajugadoridpartidajugador;
	}
}
