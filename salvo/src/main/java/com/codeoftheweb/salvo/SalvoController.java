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


    @RequestMapping("/games")

    Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new HashMap<>();
        if (isGuest(authentication)){
            dto.put("player", "Guest");
        }else{
            Player player = playerRepository.findByUserName(authentication.getName());
            dto.put("player", player.makePlayerDTO());
        }
        dto.put("games", gameRepository.findAll()
                .stream()
                .map(Game -> makeGameDTO(Game))
                .collect(Collectors.toList()));
        return dto;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (playerRepository.findByUserName(email) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }
        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping("/game_view/{nn}")
    public Map<String, Object> getGameViewByGamePlayerID(@PathVariable Long nn) {
        GamePlayer gameplayer = gameplayerRepository.findById(nn).get();

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

        dto.put ("salvoEs", gameplayer.getGame().getGamePlayers()
                .stream()
                .flatMap(gamePlayer1 -> gameplayer.getSalvoEs()
                        .stream()
                        .map(salvo -> salvo.makeSalvoDTO()))
                .collect(Collectors.toList()));

        return dto;

    }


    @RequestMapping("/leader")
    public List<Map<String,Object>> makeLeaderBoard(){
        return playerRepository
                .findAll()
                .stream()
                .map(player -> player.makeLeaderBoardDTO())
                .collect(Collectors.toList());
    }


    public Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getGameDate().getTime());
        dto.put("gamePlayers", getAllGamePlayers(game.getGamePlayers()));
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
}
