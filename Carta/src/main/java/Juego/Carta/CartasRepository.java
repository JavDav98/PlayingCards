package Juego.Carta;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartasRepository extends JpaRepository <Cartas, Serializable>{

}
