package com.codeoftheweb.salvo;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameplayerRepository gameplayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @RequestMapping("/games")
    public List<Map<String, Object>> getGames() {
        return gameRepository.findAll()
                .stream()
                .map(Game -> makeGameDTO(Game))
                .collect(Collectors.toList());
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
