package model.fight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import exceptions.CannotCaughtTrainerPkmException;
import exceptions.CannotEscapeFromTrainerException;
import exceptions.CannotUseItemInBattleException;
import exceptions.PokemonIsExhaustedException;
import exceptions.PokemonIsFightingException;
import exceptions.PokemonNotFoundException;
import model.items.Boost;
import model.items.Item;
import model.items.Item.ItemType;
import model.items.Item.whenToUse;
import model.items.Pokeball;
import model.items.Potion;
import model.player.Player;
import model.player.PlayerImpl;
import model.pokemon.Move;
import model.pokemon.Pokemon;
import model.pokemon.PokemonInBattle;
import model.pokemon.Stat;
import model.pokemon.WeaknessTable;
import model.squad.Squad;



public class FightVsWildPkm implements Fight {
	final private static int SUCCESS_PROBABILTY = 255;
	protected final int ATTACKS_TO_DO = 2;
	protected final double STAB_ACTIVE = 1.5;
	protected final double MIN_BOOST_VALUE = 0.25;
	protected final double MAX_BOOST_VALUE = 2.0;
	protected double stab;
	protected double effectiveValue = 1;
	
	protected Player player;
	protected PokemonInBattle allyPkm;
	protected PokemonInBattle enemyPkm;
	protected Move moveUsed;
	protected Map<PokemonInBattle, Map<Stat, Double>> allyPkmsBoosts = new HashMap<>();
	private Map<Stat, Double> enemyPkmBoosts;
	
	protected final WeaknessTable table = WeaknessTable.getWeaknessTable();
	
	protected final Map<Stat, Double> createBoostsMap(){
		final Map<Stat, Double> boosts = new HashMap<>();
		boosts.put(Stat.ATK, 1.0);
		boosts.put(Stat.DEF, 1.0);
		boosts.put(Stat.SPD, 1.0);
		return boosts;
	}
	
	public FightVsWildPkm(final Pokemon wildPokemon){
		this.player = PlayerImpl.getPlayer();
		this.allyPkm = checkPkmSquad(player.getSquad());
		this.enemyPkm = (PokemonInBattle) wildPokemon;
		for(PokemonInBattle pkm : player.getSquad().getPokemonList()){
			this.allyPkmsBoosts.put(pkm, createBoostsMap());
		}
		this.enemyPkmBoosts = new HashMap<>(createBoostsMap());
	}
	
	@Override
	public PokemonInBattle checkPkmSquad(Squad pkmSquad){
		final List<PokemonInBattle> pkmSquadList = pkmSquad.getPokemonList();
		for(PokemonInBattle pkm : pkmSquadList){
			if(pkm.getCurrentHP() > 0){
				return pkm;
			}
		}
		return null;
	}
	
	@Override
	public boolean run() throws CannotEscapeFromTrainerException{
		final Random escapeRoll = new Random();
		final int escapeChance = (32 * allyPkm.getStat(Stat.SPD)) / 
				(enemyPkm.getStat(Stat.SPD) / 4) + 30;
		return (escapeChance > escapeRoll.nextInt(SUCCESS_PROBABILTY));
	}
	
	@Override
	public void change(final int pkmPos) throws PokemonIsExhaustedException, PokemonIsFightingException {
		if(player.getSquad().getPokemonList().get(pkmPos).getCurrentHP() == 0){
			throw new PokemonIsExhaustedException();
		}
		else if(player.getSquad().getPokemonList().get(pkmPos) == allyPkm){
			throw new PokemonIsFightingException();
		}
		allyPkm = player.getSquad().getPokemonList().get(pkmPos);
		player.getSquad().switchPokemon(0, pkmPos);
	}
	
	@Override
	public ItemType identifyItem(final Item itemToUse) throws CannotUseItemInBattleException{
		if(itemToUse.whenToUse() == whenToUse.OUT_OF_BATTLE){
			throw new CannotUseItemInBattleException();
		}
			return itemToUse.getType();
	}
	
	@Override
	public void useBoost(Item itemToUse) throws PokemonNotFoundException{
		final Boost boost = (Boost) itemToUse;
		allyPkmsBoosts.get(allyPkm).put(boost.getStat(), 
				(allyPkmsBoosts.get(allyPkm).get(boost.getStat()) + boost.getCoeff()));
		player.getInventory().consumeItem(itemToUse);
	}
	
	@Override
	public boolean usePokeball(Item itemToUse) throws CannotCaughtTrainerPkmException{
		final Pokeball ball = (Pokeball) itemToUse;
		player.getInventory().consumeItem(itemToUse);
		return ball.isCaptured(enemyPkm);
	}
	
	@Override
	public void usePotion(Item itemToUse, PokemonInBattle pkmTarget) throws PokemonIsExhaustedException, PokemonNotFoundException{
		if(pkmTarget.getCurrentHP() == 0){
			throw new PokemonIsExhaustedException();
		}
		final Potion potion = (Potion) itemToUse;
		potion.effect(player, pkmTarget);
		player.getInventory().consumeItem(itemToUse);
	}
	
	@Override
	public boolean isAllyFastest(){
		return ((allyPkm.getStat(Stat.SPD) * allyPkmsBoosts.get(allyPkm).get(Stat.SPD)) 
				> (enemyPkm.getStat(Stat.SPD) * enemyPkmBoosts.get(Stat.SPD)));
	}
	
	@Override
	public int getAttacksToDo(){
		return ATTACKS_TO_DO;
	}
	
	@Override
	public boolean isExhausted(PokemonInBattle pkm){
		return (pkm.getCurrentHP() == 0);
	}
	
	@Override
	public void setMoveUsed(final Move moveUsed){
		this.moveUsed = moveUsed;
	}
	
	@Override
	public Move getMoveUsed(){
		return moveUsed;
	}
	
	@Override
	public PokemonInBattle getAllyPkm(){
		return allyPkm;
	}
	
	@Override
	public PokemonInBattle getEnemyPkm(){
		return enemyPkm;
	}
	
	@Override
	public boolean isAllyPkm(final PokemonInBattle pkm){
		return (pkm.equals(allyPkm));
	}
	
	@Override
	public double isEffective(final PokemonInBattle stricker, final PokemonInBattle stricked){
		return effectiveValue = table.getMultiplierAttack(moveUsed.getType(), stricked.getPokemon().getFirstType(),
				stricked.getPokemon().getSecondType());
	}
	
	@Override
	public void applyDamage(PokemonInBattle stricker, PokemonInBattle stricked){
		stab = stabCalculation(stricker);
		final double atkBoost;
		final double defBoost;
		if(stricker.equals(allyPkm)){
			atkBoost = allyPkmsBoosts.get(stricker).get(Stat.ATK);
			defBoost = getEnemyBoost(Stat.DEF);
		}
		else{
			atkBoost = getEnemyBoost(Stat.ATK);
			defBoost = allyPkmsBoosts.get(stricked).get(Stat.ATK);
		}
		final int damageDone = damageCalculation(stricker, stricked, atkBoost, defBoost);
		stricked.damage(damageDone);
	}
	
	protected double stabCalculation(PokemonInBattle stricker){
		if(stricker.getPokemon().getFirstType() == moveUsed.getType() ||
				stricker.getPokemon().getSecondType() == moveUsed.getType()){
			return STAB_ACTIVE;
		}
			return 1;
	}
	
	protected int damageCalculation(PokemonInBattle stricker, PokemonInBattle stricked, 
			double atkBoost, double defBoost){
		return (int)((((2 * stricker.getStat(Stat.LVL) + 10) 
				* (stricker.getStat(Stat.ATK) * atkBoost * moveUsed.getValue())) / 
				((stricked.getStat(Stat.DEF) * defBoost) * 250 + 2)) 
				* stab * effectiveValue);
	}
	
	@Override
	public boolean applyMoveOnBoost(PokemonInBattle stricker, PokemonInBattle stricked){
		Double newBoostValue;
		boolean changeApplied = true;
		if(isAllyPkm(stricker)){
			if(moveUsed.isOnEnemy()){
				newBoostValue = getEnemyBoost(moveUsed.getStat()) - moveUsed.getValue();
				if(newBoostValue < MIN_BOOST_VALUE){
					newBoostValue = MIN_BOOST_VALUE;
					changeApplied = false;
				}
				setEnemyBoost(moveUsed.getStat(), newBoostValue);
			}
			else{
				newBoostValue = allyPkmsBoosts.get(stricker).get(moveUsed.getStat()) 
					+ moveUsed.getValue();
				if(newBoostValue > MAX_BOOST_VALUE){
					newBoostValue = MAX_BOOST_VALUE;
					changeApplied = false;
				}
				allyPkmsBoosts.get(stricked).replace(moveUsed.getStat(), newBoostValue);
			}
		}
		else{
			if(moveUsed.isOnEnemy()){
				newBoostValue = allyPkmsBoosts.get(stricked).get(moveUsed.getStat()) 
						- moveUsed.getValue();
				if(newBoostValue < MIN_BOOST_VALUE){
					newBoostValue = MIN_BOOST_VALUE;
					changeApplied = false;
				}
				allyPkmsBoosts.get(stricked).replace(moveUsed.getStat(), newBoostValue);
			}
			else{
				newBoostValue = getEnemyBoost(moveUsed.getStat()) + moveUsed.getValue();
				if(newBoostValue > MAX_BOOST_VALUE){
					newBoostValue = MAX_BOOST_VALUE;
					changeApplied = false;
				}
				setEnemyBoost(moveUsed.getStat(), newBoostValue);
			}
		}
		return changeApplied;
	}
	
	protected double getEnemyBoost(Stat stat){
		return enemyPkmBoosts.get(stat);
	}
	
	protected void setEnemyBoost(Stat stat, Double d){
		enemyPkmBoosts.replace(stat, d);
	}
	
	@Override
	public Move enemyMove(){
		Random numberMove = new Random();
		final int movesNumber = enemyPkm.getCurrentMoves().size();
		return enemyPkm.getCurrentMoves().get(numberMove.nextInt(movesNumber));
	}
	
	@Override
	public int getExp(){
		return (int) (expBaseCalculation() / 7);
	}
	
	protected double expBaseCalculation(){
		//TODO testare se è bilanciata la quantità di baseExp
		double baseExp;
		switch(enemyPkm.getPokemon().getRarity()){
			case COMMON:
				baseExp = 60;
			case UNCOMMON:
				baseExp = 90;
				break;
			case RARE:
				baseExp = 120;
				break;
			case STARTER:
				baseExp = 150;
				break;
			case LEGENDARY:
				baseExp = 300;
				break;
			default:
				baseExp = 1;
		}
		baseExp = baseExp * enemyPkm.getStat(Stat.LVL);
		return baseExp;
	}
	
	@Override
	public boolean giveExpAndCheckLvlUp(final int exp){
		if(allyPkm.getNecessaryExp() <= exp){
			allyPkm.getAllStats().replace(Stat.EXP, (exp - allyPkm.getNecessaryExp()));
			allyPkm.levelUp();
			return true;
		}
		allyPkm.getAllStats().replace(Stat.EXP, (allyPkm.getStat(Stat.EXP) + exp));
		return false;
	}
	
	@Override
	public List<PokemonInBattle> getPkmsThatMustEvolve(){
		List<PokemonInBattle> pmksThatMustEvolve = new ArrayList<>();
		for(PokemonInBattle pkm : player.getSquad().getPokemonList()){
			if(pkm.checkIfEvolves()){
				pmksThatMustEvolve.add(pkm);
			}
		}
		if(pmksThatMustEvolve.isEmpty()){
			return null;
		}
		return pmksThatMustEvolve;
	}
	
}