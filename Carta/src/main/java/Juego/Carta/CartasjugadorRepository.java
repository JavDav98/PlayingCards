package Juego.Carta;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartasjugadorRepository extends JpaRepository<Cartasjugador, Serializable>{

	Cartasjugador findByCartasidcartasAndJugadoridjugador(int cartasidcartas, int jugadoridjugador);
	
	List<Cartasjugador> findByJugadoridjugador (int jugadoridjugador);
}
