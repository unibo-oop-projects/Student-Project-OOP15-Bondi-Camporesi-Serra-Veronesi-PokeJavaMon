package model.fight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.Controller;
import exceptions.CannotCaughtTrainerPkmException;
import exceptions.CannotEscapeFromTrainerException;
import model.items.Item;
import model.pokemon.Move;
import model.pokemon.PokemonInBattle;
import model.pokemon.Stat;
import model.trainer.GymLeader;
import model.trainer.Trainer;


public class FightVsTrainer extends AbstractFight {

    private static final int STANDARD_EFFECTIVENESS_VALUE = 1;
    private static final int SUPER_EFFECTIVE_MAX_VALUE = 4;
    private final Trainer trainer;
    private final Map<PokemonInBattle, Map<Stat, Double>> enemyPkmsBoosts = new HashMap<>();
    private static final String TRAINER_DEFEAT_MESS = "You defeated a trainer, you earn money: ";
    private static final String GYM_LEADER_DEFEAT_MESS = "You defeated a gym leader, you gain a badge!";

    public FightVsTrainer(final Trainer trainer) {
        super();
        this.trainer = trainer;
        enemyPkm = this.trainer.getSquad().getPokemonList().get(FIRST_ELEM);
        for (final PokemonInBattle pkm : trainer.getSquad().getPokemonList()) {
            enemyPkmsBoosts.put(pkm, createBoostsMap());
        }
    }

    @Override
    public boolean applyRun() throws CannotEscapeFromTrainerException{
        throw new CannotEscapeFromTrainerException();
    }

    @Override
    protected boolean useBall(final Item itemToUse) throws CannotCaughtTrainerPkmException {
        throw new CannotCaughtTrainerPkmException();
    }

    @Override
    protected double getEnemyBoost(final Stat stat) {
        return enemyPkmsBoosts.get(enemyPkm).get(stat);
    }

    @Override
    public void setEnemyBoost(final Stat stat, final Double d) {
        enemyPkmsBoosts.get(enemyPkm).replace(stat, d);
    }

    @Override
    protected Move calculationEnemyMove() {
        Move move = enemyPkm.getCurrentMoves().get(FIRST_ELEM);
        boolean superEffective = false;
        //cerca di ritornare prima una mossa superefficace, 
        //se non la trovo, ritorna la mossa che fa più danno
        //se non ha mosse che fanno danno, usa la prima mossa
        final List<Move> moves = new ArrayList<>();
        for (final Move mov : enemyPkm.getCurrentMoves()) {
            if (mov != null) {
                moves.add(mov);
            }
        }
        for (final Move m : moves) {
            if (m.getStat() == Stat.HP) {
                if (SUPER_EFFECTIVE_MAX_VALUE == table.getMultiplierAttack(m.getType(), allyPkm.getPokemon().getFirstType(), allyPkm.getPokemon().getSecondType())) {
                    move = m;
                    break;
                } else if (SUPER_EFFECTIVE == table.getMultiplierAttack(m.getType(), allyPkm.getPokemon().getFirstType(), allyPkm.getPokemon().getSecondType())) {
                    superEffective = true;
                    move = m;
                } else if (move.getValue() < m.getValue() && !superEffective) {
                    move = m;
                }
            }
        }
        return move;
    }

    @Override
    public void moveTurn(final Move move) {
        reset();
        int exp;
        int attacksDone = 0;
        boolean isEnd = false;
        boolean turnOrder = setIsAllyFastest();
        while (attacksDone < ATTACKS_TO_DO && !isEnd) {
            if (turnOrder) {
                allyTurn(move);
                if (isEnemyExhausted) {
                    final PokemonInBattle allyPkmNotUpdated = allyPkm;
                    final Map<Stat, Double> allyPkmBoost = allyPkmsBoosts.remove(allyPkmNotUpdated);
                    exp = getExp();
                    giveExpAndCheckLvlUp(exp);
                    isEnd = true;
                    allyPkmsBoosts.put(allyPkm, allyPkmBoost);
                }
            } else {
                enemyTurn();
                if (isAllyExhausted) {
                    isEnd = true;
                }
            }
            turnOrder = !turnOrder;
            attacksDone += 1;
        }
        if (attacksDone == ATTACKS_TO_DO) {
            if (isAllyFastest) {
                if (isAllyExhausted) {
                    //alleato attacca, nemico attacca, pokemon alleato esausto
                    Controller.getController().getFightController().resolveAttack(move, allyEff, enemyMove, enemyEff, isAllyFastest, true, null, null);
                } else {
                    //alleato attacca, nemico attacca, pokemon alleato sopravvive
                    Controller.getController().getFightController().resolveAttack(move, allyEff, enemyMove, enemyEff, isAllyFastest, false, null, null);
                }
            } else {
                if (isEnemyExhausted) {
                    //nemico attacca, alleato attacca, pokemon nemico esausto
                    if (checkLose(trainer.getSquad())) {
                        player.beatTrainer(trainer);
                        if (trainer instanceof GymLeader) {
                            Controller.getController().getFightController().resolveAttack(move, allyEff, enemyMove, enemyEff, isAllyFastest, true, null, 
                                    EXP_MESSAGE + getExp() + TRAINER_DEFEAT_MESS + trainer.getMoney() + GYM_LEADER_DEFEAT_MESS);
                        } else {
                            Controller.getController().getFightController().resolveAttack(move, allyEff, enemyMove, enemyEff, isAllyFastest, true, null, 
                                    EXP_MESSAGE + getExp() + TRAINER_DEFEAT_MESS + trainer.getMoney());
                        }
                    } else {
                        trainerChange();
                        Controller.getController().getFightController().resolveAttack(move, allyEff, enemyMove, enemyEff, isAllyFastest, true, enemyPkm, 
                                EXP_MESSAGE + getExp());
                    }
                } else {
                    //nemico attacca, alleato attacca, pokemon nemico sopravvive
                    Controller.getController().getFightController().resolveAttack(move, allyEff, enemyMove, enemyEff, isAllyFastest, false, null, null);
                }
            }
        } else {
            if (isAllyFastest) {
                //alleato attacca per primo, pkm nemico esausto
                if (checkLose(trainer.getSquad())) {
                    player.beatTrainer(trainer);
                    if (trainer instanceof GymLeader) {
                        Controller.getController().getFightController().resolveAttack(move, allyEff, enemyMove, enemyEff, isAllyFastest, true, null, 
                                EXP_MESSAGE + getExp() + TRAINER_DEFEAT_MESS + trainer.getMoney() + GYM_LEADER_DEFEAT_MESS);
                    } else {
                        Controller.getController().getFightController().resolveAttack(move, allyEff, enemyMove, enemyEff, isAllyFastest, true, null, 
                                EXP_MESSAGE + getExp() + TRAINER_DEFEAT_MESS + trainer.getMoney());
                    }
                } else {
                    trainerChange();
                    Controller.getController().getFightController().resolveAttack(move, allyEff, enemyMove, enemyEff, isAllyFastest, false, enemyPkm, EXP_MESSAGE + getExp());
                }
            } else {
                //nemico attacca per primo, pkm alleato esausto
                Controller.getController().getFightController().resolveAttack(null, null, enemyMove, enemyEff, isAllyFastest, false, null, null);
            }
        }
        reset();
    }

    protected boolean setIsAllyFastest() {
        return isAllyFastest = (allyPkm.getStat(Stat.SPD) * allyPkmsBoosts.get(allyPkm).get(Stat.SPD))
                >= (enemyPkm.getStat(Stat.SPD) * enemyPkmsBoosts.get(enemyPkm).get(Stat.SPD));
    }

    public int getExp() {
        return (int) (expBaseCalculation() * 1.5) / EXP_COEFFICIENT; 
    }

    protected void trainerChange() {
        //manda il primo pkm che trova e che ha un tipo superefficace contro l'allyPkm
        for (final PokemonInBattle pkm : this.trainer.getSquad().getPokemonList()) {
            if (STANDARD_EFFECTIVENESS_VALUE < table.getMultiplierAttack(pkm.getPokemon().getFirstType(), 
                    allyPkm.getPokemon().getFirstType(), allyPkm.getPokemon().getSecondType())
                    || STANDARD_EFFECTIVENESS_VALUE < table.getMultiplierAttack(pkm.getPokemon().getSecondType(), 
                            allyPkm.getPokemon().getFirstType(), allyPkm.getPokemon().getSecondType())) {
                enemyPkm = pkm;
                break;
            }
        }
        //se non ne trova nessuno manda il primo pokemon che trova
        if (enemyPkm.getCurrentHP() == 0) {
            for (final PokemonInBattle pkm : this.trainer.getSquad().getPokemonList()) {
                if (pkm.getCurrentHP() > 0) {
                    enemyPkm = pkm;
                    break;
                }
            }
        }
    }

}