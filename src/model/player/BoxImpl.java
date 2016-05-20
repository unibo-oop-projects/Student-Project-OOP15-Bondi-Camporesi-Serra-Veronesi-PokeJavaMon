package model.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exceptions.OnlyOnePokemonInSquadException;
import exceptions.PokemonNotFoundException;
import exceptions.SquadFullException;
import model.pokemon.Pokemon;
import model.pokemon.Pokedex;
import model.pokemon.PokemonInBattle;
import model.squad.Squad;

public class BoxImpl implements Box {

    private List<Pokemon> pokemonInBox;
    private boolean isSet;
    private static Box SINGLETON;
    
    private BoxImpl() {
        pokemonInBox = new ArrayList<>();
    }
    
    public static Box getBox() {
        if (SINGLETON == null) {
            synchronized (BoxImpl.class) {
                if (SINGLETON == null) {
                    SINGLETON = new BoxImpl();
                }
            }
        }
        return SINGLETON;
    }

    @Override
    /**
     * Deposits a pokemon from the current squad,
     * @throws PokemonNotFoundException if the pokemon is not in the squad
     * @throws OnlyOnePokemonInSquad if you try to deposit last pokemon in your squad
     * 
     */
    public void depositPokemon(final Pokemon pkmn, final Squad squad) throws PokemonNotFoundException, OnlyOnePokemonInSquadException {
        if (squad.getSquadSize() == 1) {
          throw new OnlyOnePokemonInSquadException();  
        }
        
        if(!squad.getPokemonList().contains(pkmn)) {
            throw new PokemonNotFoundException();
        }
        //pkmn.heal(pkmn.getStat(Stat.HP) - pkmn.getCurrentHP());
        pokemonInBox.add(pkmn);
        squad.remove(pkmn);
    }

    @Override
    public void withdrawPokemon(final Pokemon pkmn, final Squad squad) throws PokemonNotFoundException, SquadFullException {
        if (squad.getSquadSize() == 6/*SquadImpl.SQUAD_SIZE_MAX*/) {
            throw new SquadFullException();
        }
        
        if (!this.pokemonInBox.contains(pkmn)) {
            throw new PokemonNotFoundException();
        }
        
        if (pkmn.getPokedexEntry() == Pokedex.MISSINGNO) {
            throw new IllegalArgumentException();
        }
        
        this.pokemonInBox.remove(pkmn);
        squad.add(pkmn);
    }

    public void putCapturedPokemon(final Pokemon pkmn) {
        
        if (pkmn.getPokedexEntry() == Pokedex.MISSINGNO) {
            throw new IllegalArgumentException();
        }
        
        this.pokemonInBox.add(pkmn);
    }
    
    @Override
    public int getBoxSize() {
        return this.pokemonInBox.size();
    }
    
    @Override
    public List<Pokemon> getPokemonList() {
        return Collections.unmodifiableList(this.pokemonInBox);
    }

    @Override
    public boolean contains(final Pokemon pkmn) {
        for (final Pokemon p : pokemonInBox) {
            if ( ((PokemonInBattle) p).equals((PokemonInBattle)pkmn)) {
                return true;
            }
        }
        return false;
    }
    
    public void setPokemons(List<Pokemon> pokemons) {
        if (!this.isSet) {
        	this.pokemonInBox = pokemons;
        	this.isSet = true;
        } else {
        	throw new IllegalStateException("Cannot set box more than once");
        }
        
    }
    
    @Override
    public String toString() {
        String retString = "";
        for (final Pokemon p : pokemonInBox) {
          retString = retString + " " + p.getPokedexEntry().getName();  
        }
        return retString;
    }
}