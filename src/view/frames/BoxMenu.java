package view.frames;

import java.awt.*;  
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.LineBorder;

import controller.MainController;
import exceptions.PokemonNotFoundException;
import exceptions.SquadFullException;
import model.pokemon.Pokemon;
import model.pokemon.Stat;
import view.View;  
  
public class BoxMenu extends JWindow implements MyFrame {

    private static final long serialVersionUID = 6312860320430410019L;
    private JPanel panel;
    private JScrollPane pn;
    private final ArrayList<String> names;
    private final ArrayList<String> lvl;
    private final ArrayList<String> cHP;
    private final ArrayList<String> mHP;
    private final ArrayList<Pokemon> pk;
    private int cols = 1;
    private JButton info;
    private JButton withdraw;
    private JButton exit;
    
    public BoxMenu() {
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
        this.pn = new JScrollPane(this.panel);
        this.setContentPane(this.pn);     
        this.panel.setBorder(new LineBorder(Color.GRAY, 4));
        this.panel.setLayout(new GridLayout(1,1));      
        this.names.add("NAME");
        this.lvl.add("LEVEL");
        this.cHP.add("HEALTH POINTS");
        this.mHP.add("");
        this.pk.add(null);     
        for (Pokemon p : MainController.getController().getBox().getPokemonList()) {
            this.names.add(p.getPokemon().name()); // Nome Pkmn
            this.lvl.add("" + p.getStat(Stat.LVL)); // Livello
            this.mHP.add("" + p.getCurrentHP() + "/" + p.getStat(Stat.HP));
            this.cHP.add("" + p.getCurrentHP());
            this.pk.add(p);
        }  
        for(int i = 0; i<this.names.size();i++) {
            if (i == 0) {
                this.panel.add(new JLabel(this.names.get(i)));
            	this.panel.add(new JLabel(this.lvl.get(i)));
            	this.panel.add(new JLabel(this.cHP.get(i)));
            	this.panel.add(new JLabel(""));
            	this.exit = new JButton("EXIT");
            	this.exit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        View.getView().disposeCurrent();
                        View.getView().removeCurrent();
                        View.getView().resumeCurrent();
                    }
                });
            	this.panel.add(this.exit);
            } else {
                final Pokemon pokmn = this.pk.get(i);
                this.panel.add(new JLabel(this.names.get(i)));
                this.panel.add(new JLabel(this.lvl.get(i)));
                this.panel.add(new JLabel(this.mHP.get(i)));
                this.info = new JButton("INFO");
                this.info.addActionListener(new ActionListener() {
                private final Pokemon ID = pokmn;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Statistics st = new Statistics(ID);
                        View.getView().addNew(st);
                        View.getView().hideParent();
                        View.getView().showCurrent();
                    }
                });
                this.panel.add(info);
                this.withdraw = new JButton("WITHDRAW");
                this.withdraw.addActionListener(new ActionListener() {
                    final Pokemon selected = pokmn;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            MainController.getController().withdrawPokemon(selected);
                            View.getView().disposeCurrent();
                            View.getView().removeCurrent();
                            BoxMenu bx = new BoxMenu();
                            View.getView().addNew(bx);
                            View.getView().showCurrent();
                        } catch (PokemonNotFoundException e1) {
                            View.getView().hideCurrent();
                            View.getView().addNew(new MessageFrame(null, "POKEMON NOT FOUND"));
                            View.getView().showCurrent();
                        } catch (SquadFullException e1) {
                            View.getView().hideCurrent();
                            View.getView().addNew(new MessageFrame(null, "SQUAD IS FULL"));
                            View.getView().showCurrent();
                        }
                    }
                });
                this.panel.add(this.withdraw);
            }               
        }
        this.panel.setLayout(new GridLayout(this.names.size(), this.cols));
        this.setSize(900,100 * this.names.size());
        if (this.names.size() > 6) {
            this.setSize(800,600);
        } else {
            this.setSize(800,100 * this.names.size());
        }
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