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
@RequestMapping("/partida")
@Slf4j
public class PartidaService {
	
	/**
	 * Permite utilizar los metodos de la interfaz Partida heredados de JPARepository
	 */
	@Autowired
	PartidaRepository partidaRepository;

	@Autowired
	PartidajugadorRepository partidajRepository;
	
    /**
     * Notification emitter (emisor de notificaciones)
     */
    private EmitterProcessor<Partida> notificationProcessor;

     @PostConstruct
    private void createProcessor() {
        notificationProcessor = EmitterProcessor.<Partida>create();
    }
     
     @PostMapping("/new")
     public ResponseEntity<?> create(
             @RequestBody Partida p) {
         partidaRepository.save(p);
         

         notificationProcessor.onNext(p);

         return new ResponseEntity<>(p, HttpStatus.OK);
     }

     @GetMapping("/all")
     public List<Partida> getAll() {
         return partidaRepository.findAll();
     }
     
     @GetMapping("/ganador/{idg}")
     public List<Partida> getPartidas(@PathVariable("idg") int idg) {
         return partidaRepository.findAllByGanador(idg);
     }
     
     
     @GetMapping("/find/by/alias/{alias}")
     public ResponseEntity<?> finByAlias(@PathVariable("alias") String alias){
    	 Partida p = partidaRepository.findByAlias(alias);
    	 return new ResponseEntity<>(p, HttpStatus.OK);
     }
     
     @PostMapping("/joingame")
     public ResponseEntity<?> joinGame(@RequestBody Partidajugador pj) {
     	partidajRepository.save(pj);
     	Partida p = partidaRepository.findById(pj.getPartidaidpartida()).get();

         notificationProcessor.onNext(p);
     	
     	return new ResponseEntity<>(p, HttpStatus.OK);
     	    	
     }   
     

     /**
      * Flujo reactivo que contiene los datos de persona
      *
      * @return
      */
     private Flux<ServerSentEvent<Partida>> getPartidaSSE() {

         // notification processor retorna un FLUX en el cual podemos estar "suscritos" cuando este tenga otro valor ...
         return notificationProcessor
                 .log().map(
                         (partida) -> {
                             return ServerSentEvent.<Partida>builder()
                                     .id(UUID.randomUUID().toString())
                                     .event("partida-result")
                                     .data(partida)
                                     .build();
                         }).concatWith(Flux.never());
     }
     
     /**
      * Flujo reactivo que posee un "heartbeat" para que la conexión del cliente se mantenga
      *
      * @return
      */
     private Flux<ServerSentEvent<Partida>> getNotificationHeartbeat() {
         return Flux.interval(Duration.ofSeconds(15))
                 .map(i -> {
                     System.out.println(String.format("PartidaService sending heartbeat [%s] ...", i.toString()));
                     return ServerSentEvent.<Partida>builder()
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
     public Flux<ServerSentEvent<Partida>>
             getJobResultNotification() {

         return Flux.merge(
                 getNotificationHeartbeat(),
                 getPartidaSSE()
         );

     }
}
