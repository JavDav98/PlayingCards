package Juego.Carta;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PartidaRepository extends JpaRepository<Partida, Serializable>{

	Partida findByAlias(String alias);
	
	List<Partida> findAllByGanador(int ganador);

}
