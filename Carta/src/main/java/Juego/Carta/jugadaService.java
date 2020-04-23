package Juego.Carta;

import java.time.Duration;
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
	
	@Autowired
	CartasjugadorRepository cjRepository;
	
	@Autowired
	CartasRepository cartasRepository;
	
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
    			System.out.println("PLAYER1      "+cj.getCartasidcartas()+ "<----------->" +cj.getJugadoridjugador());
    			cartas.remove(num);
    		}else {
    			cj.setJugadoridjugador(p2);
    			cjRepository.save(cj);
    			System.out.println("PLAYER2      "+cj.getCartasidcartas()+ "<----------->" +cj.getJugadoridjugador());
    			cartas.remove(num);
    		}
    	}

    	Jugador player1 = jugadorRepository.findById(p1).get();
    	Jugador player2 = jugadorRepository.findById(p2).get();

        notificationProcessor.onNext(player1);
        notificationProcessor.onNext(player2);

        return new ResponseEntity<>(player1, HttpStatus.OK);
    }
    
    @GetMapping("/winer/{player1}/{player2}")
    public ResponseEntity<?> gameOver(@PathVariable("player1")int p1, @PathVariable("player2")int p2){
    	
    	Jugador player1 = jugadorRepository.findById(p1).get();
    	Jugador player2 = jugadorRepository.findById(p2).get();
    	
    	if	(player1.getCartasjugador().size()>player2.getCartasjugador().size()) {
    		notificationProcessor.onNext(player1);
    		return new ResponseEntity<>(player1, HttpStatus.OK);
    	}else if (player1.getCartasjugador().size()<player2.getCartasjugador().size()){
    		notificationProcessor.onNext(player2);
    		return new ResponseEntity<>(player2, HttpStatus.OK);
    	}else {
    		return new ResponseEntity<>(null, HttpStatus.OK);
    	}
    }
    
    @PostMapping("/remove/cards")
    public ResponseEntity<?> removeCards(@RequestBody Cartasjugador cj){
    	
    	Cartasjugador cjtemp = cjRepository.findByCartasidcartasAndJugadoridjugador(cj.getCartasidcartas(), cj.getJugadoridjugador());
    	cjRepository.delete(cjtemp);
    	
    	Jugador j = jugadorRepository.findById(cj.getJugadoridjugador()).get();

        notificationProcessor.onNext(j);
    	return new ResponseEntity<>(j, HttpStatus.OK);
    }
    
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
    
    @PostMapping("/move/cards")
    public ResponseEntity<?> moveCards(@RequestBody Cartasjugador cj){
    	
    	cjRepository.save(cj);
    	
    	Jugador j = jugadorRepository.findById(cj.getJugadoridjugador()).get();

        notificationProcessor.onNext(j);
    	return new ResponseEntity<>(j, HttpStatus.OK);
    }
    
    /**
     * Flujo reactivo que contiene los datos de persona
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