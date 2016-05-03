package controller.fight;

import java.util.List;

import exceptions.CannotCaughtTrainerPkmException;
import exceptions.CannotEscapeFromTrainerException;
import exceptions.PokemonIsExhaustedException;
import exceptions.PokemonIsFightingException;
import exceptions.PokemonNotFoundException;
import model.fight.Effectiveness;
import model.fight.Fight;
import model.fight.FightVsTrainer;
import model.fight.FightVsWildPkm;
import model.items.Item;
import model.player.PlayerImpl;
import model.pokemon.Move;
import model.pokemon.Pokemon;
import model.pokemon.Pokedex;
import model.pokemon.PokemonInBattle;
import model.pokemon.Stat;
import model.trainer.Trainer;
import view.methods.MethodsImplemented;
import view.methods.MethodsToImplement;

/**
 * The Controller that controls the fight.
 * This class implements the singleton programmation pattern 
 */
public final class FightController implements FightControllerInterface {

    private static FightControllerInterface singleton;
    private Fight fight;
    private final MethodsToImplement view = new MethodsImplemented();
    
    /**
     * Private constructor, used by the method getController
     */
    private FightController() {}
    
    /** 
     * @return the curent {@link FightController}, or a new {@link FightController}
     * if this is the first time this method is invoked
     */
    public static FightControllerInterface getController() {    
        if (singleton == null) {
            synchronized (FightController.class) {
                if (singleton == null) {
                    singleton = new FightController();
                }
            }
        }
        return singleton;
    }
    
    @Override
    public void newFightWithTrainer(final Trainer trainer) {
        fight = new FightVsTrainer(trainer);
        System.out.println("Fight with: " + trainer.getID());
    }
    
    @Override
    public void newFightWithPokemon(final Pokemon pokemon) {
        fight = new FightVsWildPkm(pokemon);
        System.out.println("Fight with: " + pokemon.getPokemon().name() + " LVL: " + pokemon.getStat(Stat.LVL));
        System.out.println("My Pkmn HP: " + PlayerImpl.getPlayer().getSquad().getPokemonList().get(0).getCurrentHP());
    }
    
    @Override
    public Fight getFight() {
        return this.fight;
    }
    
    // Metodi che chiama il MODEL
    
    @Override
    public void resolveAttack(final Move myMove, final Effectiveness myMoveEffectiveness, final Move enemyMove, final Effectiveness enemyMoveEffectiveness, final boolean myMoveFirst, final boolean lastPokemonKills, final Pokemon nextEnemyPokemon, final String optionalMessage) {
        view.resolveMove(myMove, myMoveEffectiveness, enemyMove, enemyMoveEffectiveness, myMoveFirst, lastPokemonKills, nextEnemyPokemon, optionalMessage);
    }
    
    @Override
    public void resolveRun(final boolean success, final Move enemyMove, final boolean isMyPokemonDead) {
        view.resolveRun(success, enemyMove, isMyPokemonDead);
    }
    
    @Override
    public void resolveItem(final Item item, final Pokemon pokemon, final Move enemyMove, final boolean isMyPokemonDead) {
        view.resolveUseItem(item, pokemon, enemyMove, isMyPokemonDead);
    }
    
    @Override
    public void resolvePokemon(final Pokemon myPokemon, final Move enemyMove, final boolean isMyPokemonDead) {
        view.resolveChangePokemon(myPokemon, enemyMove, isMyPokemonDead);
    }
    
    // Metodi che chiama la VIEW
    
    @Override
    public void attack(final Move move) {
        fight.moveTurn(move);
    }
    
    @Override
    public void run() throws CannotEscapeFromTrainerException {
        fight.runTurn();
    }
    
    @Override
    public void changePokemon(final Pokemon pokemon) throws PokemonIsExhaustedException, PokemonIsFightingException {
        fight.changeTurn((PokemonInBattle) pokemon);
    }
    
    @Override
    public void useItem(final Item item, final Pokemon pokemon) throws PokemonIsExhaustedException, PokemonNotFoundException, CannotCaughtTrainerPkmException, IllegalStateException {
        fight.itemTurn(item, (PokemonInBattle) pokemon);
    }
    
    @Override
    public List<Pokedex> resolveEvolution() {
        return fight.getPkmsThatMustEvolve();
    }
    
    @Override
    public void selectPokemon(final Pokemon pokemon) throws PokemonIsExhaustedException, PokemonIsFightingException {
        fight.applyChange((PokemonInBattle) pokemon);
    }

    @Override
    public Pokemon getEnemyPokemon() {
        return fight.getCurrentEnemyPokemon();
    }
}