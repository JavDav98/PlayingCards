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
public class Cartas implements Serializable{

	/**
	 * 
	 * Clase Carta reprensenta las cartas utilizadas dentro del juego
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idcartas;
	
	@Column
	private String nombre;
	
	@Column
	private int valor;
	
	/**
	 * Define el vinculo con la clase Cartasjugador
	 **/
	@OneToMany(cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = "cartasidcartas")
	private List<Cartasjugador> cartasjugador;

	public int getIdcartas() {
		return idcartas;
	}

	public void setIdcartas(int idcastas) {
		this.idcartas = idcastas;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

	public List<Cartasjugador> getCartasjugador() {
		return cartasjugador;
	}

	public void setCartasjugador(List<Cartasjugador> cartasjugador) {
		this.cartasjugador = cartasjugador;
	}
	
}