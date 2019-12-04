package com.codeoftheweb.salvo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameplayerRepository gameplayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SalvoRepository salvoRepository;


    @RequestMapping("/games")
    Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new HashMap<>();
        if (isGuest(authentication)) {
            dto.put("player", "Guest");
        } else {
            Player player = playerRepository.findByUserName(authentication.getName()).get();
            dto.put("player", player.makePlayerDTO());
        }
        dto.put("games", gameRepository.findAll()
                .stream()
                .map(Game -> makeGameDTO(Game))
                .collect(Collectors.toList()));
        return dto;
    }


    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (playerRepository.findByUserName(email) != null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }
        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping("/game_view/{nn}")
    public ResponseEntity<Map<String,
            Object>> getGameViewByGamePlayerID(@PathVariable Long nn, Authentication authentication) {
        GamePlayer gameplayer = gameplayerRepository.findById(nn).get();


        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "pasoalgo"), HttpStatus.UNAUTHORIZED);

        }

        Player player = playerRepository.findByUserName(authentication.getName()).orElse(null);
        GamePlayer gamePlayer = gameplayerRepository.findById(nn).orElse(null);

        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "pasoalgo"), HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer == null) {
            return new ResponseEntity<>(makeMap("error", "pasoalgo"), HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer.getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(makeMap("error", "pasoalgo"), HttpStatus.CONFLICT);
        }

        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gameplayer.getGame().getId());
        dto.put("created", gameplayer.getGame().getGameDate());
        dto.put("gamePlayers", gameplayer.getGame().getGamePlayers().
                stream().
                map(gamePlayer1 -> gameplayer.makeGamePlayerDTO()).
                collect(Collectors.toList()));

        dto.put("ships", gameplayer.getship().
                stream().
                map(ship -> ship.makeShipDTO()).
                collect(Collectors.toList()));

        dto.put("salvoEs", gameplayer.getGame().getGamePlayers()
                .stream()
                .flatMap(gamePlayer1 -> gameplayer.getSalvoEs()
                        .stream()
                        .map(salvo -> salvo.makeSalvoDTO()))
                .collect(Collectors.toList()));

        return new ResponseEntity<>(dto, HttpStatus.OK);

    }

    private List<Map> gethits(GamePlayer  self, GamePlayer  opponent){

        List<Map> hits  = new ArrayList<>();

        Integer carrierDamage = 0;
        Integer battleshipDamage = 0;
        Integer submarineDamage = 0;
        Integer destroyerDamage = 0;
        Integer patrolboatDamage = 0;

        List <String> carrierLocation = getLocatiosByType("carrier",self);
        List <String> battleshipLocation = getLocatiosByType("battleship",self);
        List <String> submarineLocation = getLocatiosByType("submarine",self);
        List <String> destroyerLocation = getLocatiosByType("destroyer",self);
        List <String> patrolboatLocation = getLocatiosByType("patrolboat",self);

        for (Salvo  salvo : opponent.getSalvoEs()){

            long carrierHitsInTurn = 0;
            long battleshipHitsInTurn = 0;
            long submarineHitsInTurn = 0;
            long destroyerHitsInTurn = 0;
            long patrolboatHitsInTurn = 0;
            long missedShots = salvo.getSalvoLocation().size();

            Map<String, Object> hitsMapPerTurn = new LinkedHashMap<>();
            Map<String, Object> damagesPerTurn = new LinkedHashMap<>();

            List<String> salvoLocationsList = new ArrayList<>();
            List<String> hitCellsList = new ArrayList<>();

            for (String salvoShot : salvo.getSalvoLocation()) {
                if (carrierLocation.contains(salvoShot)) {
                    carrierDamage++;
                    carrierHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
                if (battleshipLocation.contains(salvoShot)) {
                    battleshipDamage++;
                    battleshipHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
                if (submarineLocation.contains(salvoShot)) {
                    submarineDamage++;
                    submarineHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
                if (destroyerLocation.contains(salvoShot)) {
                    destroyerDamage++;
                    destroyerHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
                if (patrolboatLocation.contains(salvoShot)) {
                    patrolboatDamage++;
                    patrolboatHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
            }

            damagesPerTurn.put("carrierHits", carrierHitsInTurn);
            damagesPerTurn.put("battleshipHits", battleshipHitsInTurn);
            damagesPerTurn.put("submarineHits", submarineHitsInTurn);
            damagesPerTurn.put("destroyerHits", destroyerHitsInTurn);
            damagesPerTurn.put("patrolboatHits", patrolboatHitsInTurn);
            damagesPerTurn.put("carrier", carrierDamage);
            damagesPerTurn.put("battleship", battleshipDamage);
            damagesPerTurn.put("submarine", submarineDamage);
            damagesPerTurn.put("destroyer", destroyerDamage);
            damagesPerTurn.put("patrolboat", patrolboatDamage);

            hitsMapPerTurn.put("turn", salvo.getTurn());
            hitsMapPerTurn.put("hitLocations", hitCellsList);
            hitsMapPerTurn.put("damages", damagesPerTurn);
            hitsMapPerTurn.put("missed", missedShots);
            hits.add(hitsMapPerTurn);

        };

        return hits;
    }



    @RequestMapping("/leader")
    public List<Map<String, Object>> makeLeaderBoard() {
        return playerRepository
                .findAll()
                .stream()
                .map(player -> player.makeLeaderBoardDTO())
                .collect(Collectors.toList());
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }


    public Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getGameDate().getTime());
        dto.put("gamePlayers", getAllGamePlayers(game.getGamePlayers()));
        dto.put("scores", game.getScores().stream().map(score -> score.makeScoreDTO()).collect(Collectors.toList()));
        return dto;
    }

    public List<Map<String, Object>> getAllGamePlayers(Set<GamePlayer> gamePlayers) {
        return gamePlayers
                .stream()
                .map(GamePlayer -> makeGamePlayerDTO(GamePlayer))
                .collect(Collectors.toList());
    }

    public Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", gamePlayer.getPlayer().makePlayerDTO());
        return dto;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @RequestMapping(path = "/game/{gameID}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameID, Authentication authentication) {


        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "you cant join a game if you are not logged in"), HttpStatus.UNAUTHORIZED);

        }

        Player player = playerRepository.findByUserName(authentication.getName()).orElse(null);
        Game gameToJoin = gameRepository.getOne(gameID);


        if (gameToJoin == null) {
            return new ResponseEntity<>(makeMap("error", "no such game"), HttpStatus.FORBIDDEN);
        }

        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "no such game"), HttpStatus.FORBIDDEN);
        }

        int gamePlayesCount = gameToJoin.getGamePlayers().size();

        if (gamePlayesCount == 1) {
            GamePlayer gamePlayer = gameplayerRepository.save(new GamePlayer(gameToJoin, player));
            return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(makeMap("error", "game is full!"), HttpStatus.FORBIDDEN);
        }
    }

    //---------------------------------SALVOS-----------------------------///

    @RequestMapping(path = "/games/players/{gpid}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map> addSalvo(@PathVariable long gpid,
                                        @RequestBody Salvo salvo,
                                        Authentication authentication) {


        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "you cant join a game if you are not logged in"), HttpStatus.UNAUTHORIZED);

        }

        Player player = playerRepository.findByUserName(authentication.getName()).orElse(null);
        GamePlayer gamePlayer = gameplayerRepository.findById(gpid).orElse(null);


        if (gamePlayer == null) {
            return new ResponseEntity<>(makeMap("error", "no such gamePlayer"), HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer.getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(makeMap("error", "no coinciden"), HttpStatus.FORBIDDEN);
        }

        if (gamePlayer.getship().isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "NO esta autorizado ya tengo ships"), HttpStatus.UNAUTHORIZED);
        }

        //verifica si se disparo en el turno, sino lo agrega, y sino no deja disparar.


        if (!turnHasSalvoes(salvo, gamePlayer.getSalvoEs())) {
            salvo.setTurno(gamePlayer.getSalvoEs().size() + 1);
            salvo.setGamePlayer(gamePlayer);
            salvoRepository.save(salvo);
            return new ResponseEntity<>(makeMap("ok", "Salvos agregados"), HttpStatus.CREATED);
        }
            return new ResponseEntity<>(makeMap("error", "No puedes disparar en este turno"), HttpStatus.FORBIDDEN);

    }

    // Comparo el turno que trato de crear con todos los anteriores.


    public boolean turnHasSalvoes (Salvo newSalvo, Set<Salvo> playerSalvoes) {
        boolean hasSalvoes = false;
        for (Salvo salvo: playerSalvoes) {
            if(salvo.getTurno() == newSalvo.getTurno()) {
                hasSalvoes = true;
            }
        }

        return hasSalvoes;
    }

    private List<String>  getLocatiosByType(String type, GamePlayer self){
        return  self.getship().size()  ==  0 ? new ArrayList<>() : self.getship().stream().filter(ship -> ship.getType().equals(type)).findFirst().get().getShipLocations();
    }

    private Boolean getIfAllSunk (GamePlayer self, GamePlayer opponent) {

        if(!opponent.getship().isEmpty() && !self.getSalvoEs().isEmpty()){
            return opponent.getSalvoEs().stream().flatMap(salvo -> salvo.getSalvoLocation().stream()).collect(Collectors.toList()).containsAll(self.getship().stream()
                    .flatMap(ship -> ship.getShipLocations().stream()).collect(Collectors.toList()));
        }
        return false;
    }

}