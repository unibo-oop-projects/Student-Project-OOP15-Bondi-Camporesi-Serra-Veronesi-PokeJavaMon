package model.items;

import exceptions.PokemonNotFoundException;
import model.player.Player;
import model.pokemon.PokemonInBattle;

public class Potion extends AbstractItem {

    public static enum PotionType {
        POTION(20, 300), SUPERPOTION(50, 700), HYPERPOTION(200, 1200);
        
        private PotionType(final int heal, final int cost) {
            this.heal = heal;
            this.cost = cost;
        }
        
        private final int heal;
        private final int cost;
        
        public int getHeal() {
            return this.heal;
        }
        
        public int getCost() {
            return this.cost;
        }
    }
    
    private final PotionType quality;
    
    public Potion(final Potion.PotionType quality) {
        super(quality.cost, Item.ItemType.POTION, false);
        this.quality = quality;
    }

    @Override
    public void effect(final Player p, final PokemonInBattle pkmn) throws PokemonNotFoundException {
        if (!p.getSquad().getPokemonList().contains(pkmn)) {
            throw new PokemonNotFoundException();
        }
        
        pkmn.heal(this.quality.heal);
        
    }
    
    public PotionType getQuality() {
        return this.quality;
    }
    
    public whenToUse whenToUse() {
        return Item.whenToUse.EVERYWHERE;
    }
    
    @Override
    public boolean equals(Object object) {
        return this.hashCode() == ((Potion) object).hashCode();
    }
    
    @Override
    public int hashCode() {
        switch (this.quality) {
        case HYPERPOTION :
            return 9999970;
        case  SUPERPOTION :
            return 9999971;
        case  POTION :
            return 9999972;
        }
        return 0;
    }

    @Override
    public String toString() {
        return quality.toString();
    }
}
