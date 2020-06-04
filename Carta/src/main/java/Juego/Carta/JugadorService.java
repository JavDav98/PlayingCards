package Juego.Carta;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

@CrossOrigin
@RestController
@RequestMapping("/jugador")
@Slf4j
public class JugadorService {

	/**
	 * Permite utilizar los metodos de la interfaz jugador heredados de JPARepository
	 */
	@Autowired
	JugadorRepository jugadorRepository;
	
    /**
     * Notification emitter (emisor de notificaciones)
     */
    private EmitterProcessor<Jugador> notificationProcessor;

     @PostConstruct
    private void createProcessor() {
        notificationProcessor = EmitterProcessor.<Jugador>create();
    }

     /**
      * Creacion de un nuevo jugador
      * @param j
      * @return
      */
    @PostMapping("/new")
    public ResponseEntity<?> create(
            @RequestBody Jugador j) {
        jugadorRepository.save(j);

        notificationProcessor.onNext(j);

        return new ResponseEntity<>(j, HttpStatus.OK);
    }

    @GetMapping("/all")
    public List<Jugador> getAll() {

        return jugadorRepository.findAll();
    }
    
    /**
     * verifica el el jugador segun Password y nombre para otorgar acceso al juego
     * @param nombre
     * @param pass
     * @return
     */
    @GetMapping("find/by/nombre/{nombre}/pass/{pass}")
    public ResponseEntity<?> findByNombreAndPass(@PathVariable("nombre") String nombre, @PathVariable("pass") String pass) {
    	Jugador j = jugadorRepository.findByNombreAndPass(nombre, pass);
    	
    	notificationProcessor.onNext(j);
    	
    	return new ResponseEntity<>(j, HttpStatus.OK);
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/find/by/id/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") int id){
        Jugador j = jugadorRepository.findById(id).get();

        return  new ResponseEntity<>(j, HttpStatus.OK);
    }

    /**
     * Flujo reactivo que contiene los datos del Jugador que recibe modificaciones
     *
     * @return
     */
    private Flux<ServerSentEvent<Jugador>> getJugadorSSE() {

        // notification processor retorna un FLUX en el cual podemos estar "suscritos" cuando este tenga otro valor ...
        return notificationProcessor
                .log().map(
                        (jugador) -> {
                            return ServerSentEvent.<Jugador>builder()
                                    .id(UUID.randomUUID().toString())
                                    .event("jugador-result")
                                    .data(jugador)
                                    .build();
                        }).concatWith(Flux.never());
    }

    /**
     * Flujo reactivo que posee un "heartbeat" para que la conexión del cliente se mantenga
     *
     * @return
     */
    private Flux<ServerSentEvent<Jugador>> getNotificationHeartbeat() {
        return Flux.interval(Duration.ofSeconds(15))
                .map(i -> {
                    System.out.println(String.format("JugadorService sending heartbeat [%s] ...", i.toString()));
                    return ServerSentEvent.<Jugador>builder()
                            .id(String.valueOf(i))
                            .event("heartbeat-result")
                            .data(null)
                            .build();
                });
    }

    /**
     * Servicio reactivo que retorna la combinación de los dos flujos antes declarados
     * Simplificacion de declaracion GET por "GetMapping"
     *
     * @return
     */

    @GetMapping("/notification/sse")
    public Flux<ServerSentEvent<Jugador>>
    getJobResultNotification() {

        return Flux.merge(
                getNotificationHeartbeat(),
                getJugadorSSE()
        );

    }
}
