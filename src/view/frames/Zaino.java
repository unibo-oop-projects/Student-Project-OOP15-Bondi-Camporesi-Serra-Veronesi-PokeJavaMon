package view.frames;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

import controller.Controller;
import controller.parameters.State;
import exceptions.CannotCaughtTrainerPkmException;
import exceptions.PokemonIsExhaustedException;
import exceptions.PokemonNotFoundException;
import model.items.Item;
import model.items.Item.ItemType;
import model.items.Potion;
import model.player.PlayerImpl;
import model.pokemon.Pokemon;
import model.pokemon.PokemonInBattle;
import view.View;
import view.resources.MessageFrame;

public class Zaino extends JWindow implements MyFrame {

    private static final long serialVersionUID = 4403659276705962715L;
    private Item itemToUse;
    private JPanel contiene;
    private final ArrayList<String>Name1 = new ArrayList<String>();
    private final ArrayList<String>Name2 = new ArrayList<String>();
    private final ArrayList<String>Qnt = new ArrayList<String>();
    private final ArrayList<Item> it = new ArrayList<Item>();
    private int cols = 1;
    private JButton esci2;
    private JButton usa;

    public void selectItem(Item it) {
        itemToUse = it;
    }

    public void useItem(Pokemon p) {
        if (itemToUse != null) {
            if (Controller.getController().getStatusController().getState() == State.FIGHTING) {
                try {
                    Controller.getController().getFightController().useItem(itemToUse, p);
                    selectItem(null);
                    disposeFrame();
                } catch (PokemonIsExhaustedException e1) {
                    new MessageFrame(null, "POKEMON IS EXAUSTED");
                    selectItem(null);
                    disposeFrame();
                } catch (PokemonNotFoundException e1) {
                    new MessageFrame(null, "POKEMON NOT FOUND");
                    selectItem(null);
                    disposeFrame();
                } catch (CannotCaughtTrainerPkmException e1) {
                    new MessageFrame(null, "CANNOT CATCH TRAINER POKEMON");
                    selectItem(null);
                    disposeFrame();
                } catch (IllegalStateException e1) {
                    new MessageFrame(null, "YOU HAVE NO MORE THIS ITEM");
                    selectItem(null);
                    disposeFrame();
                }
            } else {
                if (itemToUse instanceof Potion) {
                    try {
                        ((Potion) itemToUse).effect(PlayerImpl.getPlayer(), (PokemonInBattle) p);
                        PlayerImpl.getPlayer().getInventory().consumeItem(itemToUse);
                        disposeFrame();
                    } catch (PokemonNotFoundException e) {
                        new MessageFrame(null, "POKEMON NOT FOUND");
                    } catch (IllegalStateException ex) {
                        new MessageFrame(null, "YOU HAVE NO MORE THIS ITEM");
                        selectItem(null);
                        disposeFrame();
                    }
                }
            }
        }
    }

    @Override
    public void showFrame() {
        this.setAlwaysOnTop(true);
        contiene = new JPanel();
        this.setContentPane(contiene);   
        contiene.setLayout(new GridLayout(1,1));
        Name1.add("TYPE");
        Name2.add("NAME");
        Qnt.add("QUANTITY");
        it.add(null);   
        for (Item i : PlayerImpl.getPlayer().getInventory().getSubInventory(ItemType.POTION).keySet()) { 
            if (PlayerImpl.getPlayer().getInventory().getSubInventory(ItemType.POTION).get(i) != 0) {
                Name1.add(i.getType().name()); 
                Name2.add(i.toString()); 
                Qnt.add("" + PlayerImpl.getPlayer().getInventory().getSubInventory(ItemType.POTION).get(i));
                it.add(i);
                System.out.println(""+ PlayerImpl.getPlayer().getInventory().getSubInventory(ItemType.POTION).get(i));
            }
        }  
        for (Item i : PlayerImpl.getPlayer().getInventory().getSubInventory(ItemType.POKEBALL).keySet()) { 
            if (PlayerImpl.getPlayer().getInventory().getSubInventory(ItemType.POKEBALL).get(i) != 0) {
                Name1.add(i.getType().name());
                Name2.add(i.toString()); 
                Qnt.add("" + PlayerImpl.getPlayer().getInventory().getSubInventory(ItemType.POKEBALL).get(i));
                it.add(i);
            }
        }  
        for (Item i : PlayerImpl.getPlayer().getInventory().getSubInventory(ItemType.BOOST).keySet()) { 
            if (PlayerImpl.getPlayer().getInventory().getSubInventory(ItemType.BOOST).get(i) != 0) {
                Name1.add(i.getType().name()); 
                Name2.add(i.toString()); 
                Qnt.add("" + PlayerImpl.getPlayer().getInventory().getSubInventory(ItemType.BOOST).get(i));
                it.add(i);
            }
        } 
        for(int j = 0; j<Name1.size();j++) {   
            if (j==0) {
                contiene.add(new JLabel(Name1.get(j)));
                contiene.add(new JLabel(Name2.get(j)));
                contiene.add(new JLabel(Qnt.get(j)));
                esci2 = new JButton("Exit");
                esci2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        View.getView().disposeCurrent();
                        View.getView().removeCurrent();
                        View.getView().resumeCurrent();
                    }
                });
                contiene.add(esci2);
                j++;
            }
            final Item itm = it.get(j);
            contiene.add(new JLabel(Name1.get(j)));
            contiene.add(new JLabel(Name2.get(j)));
            contiene.add(new JLabel(Qnt.get(j)));
            usa = new JButton("Use");
            usa.addActionListener(new ActionListener() {     
                Item i = itm;
                @Override
                public void actionPerformed(ActionEvent e) {            
                    if (i.getType() == ItemType.POTION) {
                        selectItem(i);
                        Squadra sq = new Squadra(true, true);
                        View.getView().hideCurrent();
                        View.getView().addNew(sq);
                        View.getView().showCurrent();
                    } else {
                        selectItem(i);
                        if (Controller.getController().getStatusController().getState() == State.FIGHTING) {
                            useItem(Controller.getController().getEnemyPokemonInFight());
                        } else {
                            new MessageFrame(null, "NON PUOI USARE QUESTO STRUMENTO FUORI DALLA BATTAGLIA");
                            useItem(null);
                        }
                    }
//                    disposeFrame();
                }
            });
//            if (itm.getType() != ItemType.POTION && Controller.getController().getStatusController().getState() != State.FIGHTING) {
//                usa.setEnabled(false);
//            }
            contiene.add(usa);
        }
        
        contiene.setLayout(new GridLayout(Name1.size(), cols));
        this.setSize(600,60 * Name1.size());
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
    public void resume() {
        this.setVisible(true);
    }
}