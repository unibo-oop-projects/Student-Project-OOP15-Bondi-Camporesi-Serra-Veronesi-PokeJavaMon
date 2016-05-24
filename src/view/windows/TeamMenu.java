package view.windows;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

import controller.MainController;
import controller.parameters.State;
import exceptions.OnlyOnePokemonInSquadException;
import exceptions.PokemonIsExhaustedException;
import exceptions.PokemonIsFightingException;
import exceptions.PokemonNotFoundException;
import model.pokemon.Pokemon;
import model.pokemon.Stat;
import view.View;
import view.fight.FightScreen;
/**
 * TeamMenuClass
 */
public class TeamMenu extends JWindow implements MyFrame {
	/**
	 * serialVersionUID
	 */   
    private static final long serialVersionUID = 4848482754813638374L;
    /**
     * panel
     */
    private JPanel panel;
    /**
     * names
     */
    private final ArrayList<String>names;
    /**
     * lvl
     */
    private final ArrayList<String>lvl;
    /**
     * cHP
     */
    private final ArrayList<String>cHP;
    /**
     * mHP
     */
    private final ArrayList<String>mHP;
    /**
     * pk
     */
    private final ArrayList<Pokemon> pk;
    /**
     * cols
     */
    private int cols = 1;
    /**
     * info
     */
    private JButton info;
    /**
     * set
     */
    private JButton set;
    /**
     * deposit
     */
    private JButton deposit;
    /**
     * select
     */
    private JButton select;
    /**
     * exit
     */
    private JButton exit;
    /**
     * canExit, isChangingPoke
     */
    private boolean canExit, isChangingPoke;
	/**
	 * TeamMenu
	 */
    public TeamMenu(final boolean b1, final boolean b2) {
        this.canExit = b1;
        this.isChangingPoke = b2;
        this.names = new ArrayList<String>();
        this.lvl = new ArrayList<String>();
        this.cHP = new ArrayList<String>();
        this.mHP = new ArrayList<String>();
        this.pk = new ArrayList<Pokemon>();
    }
    
    @Override
    public void showFrame() {
        this.setAlwaysOnTop(true);
        this.panel = new JPanel();
        this.setContentPane(panel);
        this.panel.setBorder(new LineBorder(Color.GRAY, 4));
        this.names.add("NAME");
        this.lvl.add("LEVEL");
        this.cHP.add("HEALTH POINTS");
        this.mHP.add("");
        this.pk.add(null);
        for (Pokemon p : MainController.getController().getSquad().get().getPokemonList()) {
        	this.names.add(p.getPokedexEntry().name()); // Nome Pkmn
        	this.lvl.add("" + p.getStat(Stat.LVL)); // Livello
        	this.mHP.add("" + p.getStat(Stat.MAX_HP));
        	this.cHP.add("" + p.getCurrentHP());
            this.pk.add(p);
        }
        for(int i = 0; i<names.size();i++) {
            if (i == 0) {
            	this.panel.add(new JLabel(this.names.get(i)));
            	this.panel.add(new JLabel(this.lvl.get(i)));
            	this.panel.add(new JLabel(this.cHP.get(i)));
            	this.panel.add(new JLabel(""));
            	this.panel.add(new JLabel(""));
            	this.panel.add(new JLabel(""));
            	this.exit = new JButton("EXIT");
            	this.exit.setBorderPainted(false);
            	this.exit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        View.getView().disposeCurrent();
                        View.getView().removeCurrent();
                        View.getView().resumeCurrent();
                    }
                });
                if (!canExit) {
                	this.exit.setEnabled(false);
                }
                this.panel.add(this.exit);
                i++;
            }
            final int index = i - 1;
            final Pokemon pkmn = this.pk.get(i);
            this.panel.add(new JLabel(this.names.get(i)));
            this.panel.add(new JLabel(this.lvl.get(i)));
            this.panel.add(new JLabel(this.cHP.get(i) + " / " + this.mHP.get(i)));
            this.info = new JButton("INFO");
            this.info.setBorderPainted(false);
            this.info.addActionListener(new ActionListener() {
                private final Pokemon ID = pkmn;
                @Override
                public void actionPerformed(ActionEvent e) {
                    Statistics st = new Statistics(ID);
                    View.getView().addNew(st);
                    View.getView().hideParent();
                    View.getView().showCurrent();
                }
            });
            this.panel.add(this.info);
            this.set = new JButton("SET FIRST");
            this.set.setBorderPainted(false);
            this.set.addActionListener(new ActionListener() {
                private final int ID = index;
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (MainController.getController().getStatusController().getState() == State.MENU) {
                        if (MainController.getController().getSquad().get().getPokemonList().get(index).getCurrentHP() > 0) {
                            MainController.getController().switchPokemon(0, ID);
                            View.getView().disposeCurrent();
                            View.getView().removeCurrent();
                            TeamMenu sc = new TeamMenu(true, false);
                            View.getView().addNew(sc);
                            View.getView().showCurrent();
                        } else {
                            View.getView().hideCurrent();
                            View.getView().addNew(new MessageFrame(null, "CANNOT SELECT THAT POKEMON"));
                            View.getView().showCurrent();
                        }
                    } else {
                        if (MainController.getController().getSquad().get().getPokemonList().get(0).getCurrentHP() == 0) {
                            try {
                                View.getView().disposeCurrent();
                                View.getView().removeCurrent();
                                MainController.getController().getFightController().selectPokemon(MainController.getController().getSquad().get().getPokemonList().get(index));
                                MyFrame fr = View.getView().getCurrent();
                                ((FightScreen) fr).repaintFrame();
                                View.getView().resumeCurrent();
                            } catch (PokemonIsExhaustedException e1) {
                                TeamMenu tm = new TeamMenu(false, false);
                                View.getView().addNew(tm);
                                View.getView().showCurrent();
                                View.getView().hideCurrent();
                                View.getView().addNew(new MessageFrame(null, "CANNOT SELECT THAT POKEMON"));
                                View.getView().showCurrent();
                            } catch (PokemonIsFightingException e1) {
                                TeamMenu tm = new TeamMenu(false, false);
                                View.getView().addNew(tm);
                                View.getView().showCurrent();
                                View.getView().hideCurrent();
                                View.getView().addNew(new MessageFrame(null, "CANNOT SELECT THAT POKEMON"));
                                View.getView().showCurrent();
                            }
                        } else {
                            try {
                                View.getView().disposeCurrent();
                                View.getView().removeCurrent();
                                MainController.getController().getFightController().changePokemon(MainController.getController().getSquad().get().getPokemonList().get(index));
                                View.getView().resumeCurrent();
                            } catch (PokemonIsExhaustedException e1) {
                                View.getView().addNew(new MessageFrame(null, "CANNOT SELECT THAT POKEMON"));
                                View.getView().showCurrent();
                            } catch (PokemonIsFightingException e1) {
                                View.getView().addNew(new MessageFrame(null, "CANNOT SELECT THAT POKEMON"));
                                View.getView().showCurrent();
                            }
                        }
                    }
                }
            });
            if (isChangingPoke) {
            	this.set.setEnabled(false);
            }
            if (index == 0) {
                this.set.setEnabled(false);
            }
            this.panel.add(this.set);
            this.deposit = new JButton("DEPOSIT");
            this.deposit.setBorderPainted(false);
            this.deposit.addActionListener(new ActionListener() {
                final Pokemon p = pkmn;
                public void actionPerformed(ActionEvent e) {
                    for (final Pokemon pok : MainController.getController().getSquad().get().getPokemonList()) {
                        if (pok != p && pok.getCurrentHP() > 0) {
                            try {
                                MainController.getController().depositPokemon(p);
                                if (MainController.getController().getSquad().get().getPokemonList().get(0).getCurrentHP() == 0) {
                                    MainController.getController().getSquad().get().switchPokemon(0, MainController.getController().getSquad().get().getPokemonList().indexOf(pok));
                                }
                                View.getView().disposeCurrent();
                                View.getView().removeCurrent();
                                TeamMenu sc = new TeamMenu(canExit, isChangingPoke);
                                View.getView().addNew(sc);
                                View.getView().showCurrent();
                                return;
                            } catch (PokemonNotFoundException e1) {
                                View.getView().hideCurrent();
                                View.getView().addNew(new MessageFrame(null, "POKEMON NOT FOUND"));
                                View.getView().showCurrent();
                                return;
                            } catch (OnlyOnePokemonInSquadException e1) {
                                View.getView().hideCurrent();
                                View.getView().addNew(new MessageFrame(null, "CANNOT DEPOSIT LAST POKEMON"));
                                View.getView().showCurrent();
                                return;
                            }
                        }
                    }
                    View.getView().hideCurrent();
                    View.getView().addNew(new MessageFrame(null, "CANNOT DEPOSIT LAST ALIVE POKEMON"));
                    View.getView().showCurrent();
                }
            });
            if (MainController.getController().getStatusController().getState() != State.MENU || isChangingPoke) {
            	deposit.setEnabled(false);
            }
            this.panel.add(this.deposit);
            this.select = new JButton("SELECT");
            this.select.setBorderPainted(false);
            this.select.addActionListener(new ActionListener() {
                final Pokemon p = pkmn;
                public void actionPerformed(ActionEvent e) {
                    View.getView().disposeCurrent();
                    View.getView().removeCurrent();
                    MyFrame fr = View.getView().getCurrent();
                    ((BagMenu) fr).useItem(p);
                    if (MainController.getController().getStatusController().getState() == State.MENU) {
                        View.getView().removeCurrent();
                        BagMenu z = new BagMenu();
                        View.getView().addNew(z);
                        View.getView().showCurrent();
                    } 
                }
            });
            if (!isChangingPoke) {
            	this.select.setEnabled(false);
            }
            this.panel.add(this.select);
        }      
        this.panel.setLayout(new GridLayout(this.names.size(), this.cols));
        this.setSize(900,100 * names.size());
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void disposeFrame() {
        this.dispose();
    }

    @Override
    public void hideFrame() {
        this.setVisible(false);
    }
    
    @Override
    public void resumeFrame() {
        this.setVisible(true);
    }
}      