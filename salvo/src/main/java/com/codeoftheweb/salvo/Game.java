package com.codeoftheweb.salvo;

import  org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import javax.persistence.OneToMany;


@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private Date GameDate;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;


    public Game() {
        this.GameDate = new Date();
    }

    public Game(Date GameDate) {
        this.GameDate = GameDate;
    }

    public long getId() {
        return id;
    }

    public Date getGameDate() {
        return GameDate;
    }

    public void setGameDate(Date gameDate) {
        this.GameDate = gameDate;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

}

