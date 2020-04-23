package Juego.Carta;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JugadorRepository extends JpaRepository<Jugador, Serializable>{
	
	Jugador findByNombreAndPass(String nombre, String pass);

}
