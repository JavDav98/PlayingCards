package Juego.Carta;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
@RequestMapping("/move")
@Slf4j
public class jugadaService {
	
	/**
	 * "@Autowired" Permite utilizar los metodos de la interfaz Jugador, Cartasjugador y Cartas heredados de JPARepository
	 */
	@Autowired
	CartasjugadorRepository cjRepository;
	
	@Autowired
	CartasRepository cartasRepository;
	
	@Autowired
	JugadorRepository jugadorRepository;
	
	@Autowired
	PartidaRepository partidaRepository;
	
    /**
     * Notification emitter (emisor de notificaciones)
     */
    private EmitterProcessor<Jugador> notificationProcessor;

     @PostConstruct
    private void createProcessor() {
        notificationProcessor = EmitterProcessor.<Jugador>create();
    }

     /**
      * Reparte las 52 cartas entre los dos jugadores asignados a la partida
      * @param p1
      * @param p2
      * @return
      */
    @GetMapping("/deal/cards/{player1}/{player2}")
    public ResponseEntity<?> dealCards(
            @PathVariable("player1") int p1, @PathVariable("player2")int p2) {
        
    	List<Cartas> cartas = cartasRepository.findAll();
    	Random n = new Random();
    	
    	for	(int i = 0; i<52; i++) {
        	Cartasjugador cj = new Cartasjugador();
        	Cartas cartatemp = new Cartas();
    		int num = n.nextInt(cartas.size());
    		cartatemp = cartas.get(num);
			cj.setCartasidcartas(cartatemp.getIdcartas());
    		if	(i<26) {    			
    			cj.setJugadoridjugador(p1);
    			cjRepository.save(cj);
    			cartas.remove(num);
    		}else {
    			cj.setJugadoridjugador(p2);
    			cjRepository.save(cj);
    			cartas.remove(num);
    		}
    	}

    	Jugador player1 = jugadorRepository.findById(p1).get();
    	Jugador player2 = jugadorRepository.findById(p2).get();

        notificationProcessor.onNext(player1);
        notificationProcessor.onNext(player2);

        return new ResponseEntity<>(player2, HttpStatus.OK);
    }  
    
    /**
     * Notifica con un jugador id 0, que uno de los jugadores ya entrego su carta yncluyendo la carta entregada
     * @param cj
     * @return
     */
    @PostMapping("/notify/delivery")
    public ResponseEntity<?> notifyDelivery(@RequestBody Cartasjugador cj){
    	Cartasjugador cjtemp = cjRepository.findByCartasidcartasAndJugadoridjugador(cj.getCartasidcartas(), cj.getJugadoridjugador());
    	Jugador j = new Jugador();
    	j.setIdjugador(0);
    	List<Cartasjugador> lcj = new ArrayList<>();
    	lcj.add(cjtemp);
    	j.setCartasjugador(lcj);
    	
    	notificationProcessor.onNext(j);
    	
    	return new ResponseEntity<>(j, HttpStatus.OK);
    }
    
    /**
     * Le quita la carta al que entregue la carta menor
     * @param cj
     * @return
     */
    @PostMapping("/remove/cards")
    public ResponseEntity<?> removeCards(@RequestBody Cartasjugador cj){
    	
    	Cartasjugador cjtemp = cjRepository.findByCartasidcartasAndJugadoridjugador(cj.getCartasidcartas(), cj.getJugadoridjugador());
    	cjRepository.delete(cjtemp);
    	
    	Jugador j = jugadorRepository.findById(cj.getJugadoridjugador()).get();

        notificationProcessor.onNext(j);
    	return new ResponseEntity<>(j, HttpStatus.OK);
    }
    
    /**
     * Le asigna la carta del jugador que entrego la carta menor al jugador que entrego la carta mayor
     * @param cj
     * @return
     */
    
    @PostMapping("/move/cards")
    public ResponseEntity<?> moveCards(@RequestBody Cartasjugador cj){
    	
    	cjRepository.save(cj);
    	
    	Jugador j = jugadorRepository.findById(cj.getJugadoridjugador()).get();

        notificationProcessor.onNext(j);
    	return new ResponseEntity<>(j, HttpStatus.OK);
    }
    
    /**
     * Devuelve el ganador de la partida
     * @param p1
     * @param p2
     * @return
     */
    @GetMapping("/winer/{idpartida}")
    public ResponseEntity<?> gameOver(@PathVariable("idpartida")int id){
    	
    	Partida p = partidaRepository.findById(id).get();
    	
    	Jugador player1 = jugadorRepository.findById(p.getPartidajugador().get(0).getJugadoridjugador()).get();
    	Jugador player2 = jugadorRepository.findById(p.getPartidajugador().get(1).getJugadoridjugador()).get();;
    	
    	if	(player1.getCartasjugador().size()>player2.getCartasjugador().size()) {
    		notificationProcessor.onNext(player1);
    		p.setGanador(player1.getIdjugador());
    		partidaRepository.save(p);
    		return new ResponseEntity<>(player1, HttpStatus.OK);
    	}else if (player1.getCartasjugador().size()<player2.getCartasjugador().size()){
    		notificationProcessor.onNext(player2);
    		p.setGanador(player2.getIdjugador());
    		partidaRepository.save(p);
    		return new ResponseEntity<>(player2, HttpStatus.OK);
    	}else {
    		return new ResponseEntity<>(null, HttpStatus.OK);
    	}
    }
    
    /**
     * Limpia los datos de los movimientos en la partida una vez esta finaliza
     * @param p1
     * @param p2
     */
    @GetMapping("/clean/game/{player1}/{player2}")
    public void cleanGame(@PathVariable("player1")int p1, @PathVariable("player2")int p2){
    	List<Cartasjugador> cj = cjRepository.findByJugadoridjugador(p1);
    	List<Cartasjugador> cjj = cjRepository.findByJugadoridjugador(p2);
    	
    	for	(Cartasjugador cj1 :cj) {
        	cjRepository.delete(cj1);
    	}
    	
    	for	(Cartasjugador cjj1 :cjj) {
        	cjRepository.delete(cjj1);
    	}
    	
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
                    System.out.println(String.format("sending heartbeat [%s] ...", i.toString()));
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