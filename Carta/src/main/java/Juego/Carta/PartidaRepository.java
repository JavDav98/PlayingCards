package Juego.Carta;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PartidaRepository extends JpaRepository<Partida, Serializable>{

	Partida findByAlias(String alias);

}
