package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import  org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.OneToMany;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private int turno;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_ID")
    private GamePlayer gamePlayer;


    @ElementCollection
    @Column (name = "salvoLocation")
    private Set<String> salvoLocation;

    //Constructor

    public Salvo(){
    }

    public Salvo(int turno, GamePlayer gamePlayer, Set<String> salvoLocation) {
        this.turno = turno;
        this.gamePlayer = gamePlayer;
        this.salvoLocation = salvoLocation;
    }

    //get and set


    public long getId() {
        return id;
    }


    public int getTurno() {
        return turno;
    }

    public void setTurno(int turno) {
        this.turno = turno;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Set<String> getSalvoLocation() {
        return salvoLocation;
    }

    public void setSalvoLocation(Set<String> salvoLocation) {
        this.salvoLocation = salvoLocation;
    }

    public Object makeSalvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", this.getTurno());
        dto.put("salvoLocation", this.getSalvoLocation());
        dto.put("player", this.getGamePlayer().getPlayer().getId());
        return dto;
    }
}

