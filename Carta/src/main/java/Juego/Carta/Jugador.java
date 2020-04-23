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
public class Jugador implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idjugador;
	
	@Column
	private String nombre;
	
	@Column
	private String pass;
	
	@OneToMany(cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = "jugadoridjugador")
	private List<Partidajugador> partidajugador;
	
	@OneToMany(cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = "jugadoridjugador")
	private List<Cartasjugador> cartasjugador;

	public int getIdjugador() {
		return idjugador;
	}

	public void setIdjugador(int idjugador) {
		this.idjugador = idjugador;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public List<Partidajugador> getPartidajugador() {
		return partidajugador;
	}

	public void setPartidajugador(List<Partidajugador> partidajugador) {
		this.partidajugador = partidajugador;
	}

	public List<Cartasjugador> getCartasjugador() {
		return cartasjugador;
	}

	public void setCartasjugador(List<Cartasjugador> cartasjugador) {
		this.cartasjugador = cartasjugador;
	}
	
}
