package view.frames;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.LineBorder;

import controller.MainController;
import model.pokemon.Move;
import model.pokemon.Pokemon;
import view.View;

public class LearnMoveFrame extends JWindow implements MyFrame {        
        
        private static final long serialVersionUID = -4826245459412294421L;
        private JPanel panel;
        private JButton move1;
        private JButton move2;
        private JButton move3;
        private JButton move4;
        private JButton move5;
        private JLabel tooltip;
        private JLabel forget1;
        private JLabel forget2;
        private JLabel forget3;
        private JLabel forget4;
        private JLabel forget5;
        private Move newMove;
        private Pokemon pk;
        
        public LearnMoveFrame(final Move mv) {
            this.newMove = mv;
            this.pk = MainController.getController().getPlayer().getSquad().getPokemonList().get(0);
        }
        
        @Override
        public void showFrame() {
                this.setAlwaysOnTop(true); 
                this.setBounds(100, 100, 220, 300);
                panel = new JPanel();
                this.setContentPane(panel);   
                panel.setBorder(new LineBorder(Color.GRAY, 4));
                panel.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));   
                this.getContentPane().add(Box.createVerticalGlue());
                tooltip = new JLabel("Pokemon already knows 4 moves!");
                tooltip.setAlignmentX(Component.CENTER_ALIGNMENT);
                this.getContentPane().add(tooltip);
                this.getContentPane().add(Box.createVerticalGlue());
                forget5 = new JLabel("Do not learn:");
                forget5.setAlignmentX(Component.CENTER_ALIGNMENT);
                this.getContentPane().add(forget5);
                move5 = new JButton(newMove.name());
                move5.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        View.getView().disposeCurrent();
                        View.getView().removeCurrent();
                        View.getView().resumeCurrent();
                    } 
                });
                move5.setAlignmentX(Component.CENTER_ALIGNMENT);
                this.getContentPane().add(move5);
                this.getContentPane().add(Box.createVerticalGlue());
                this.setButton(forget1, move1, 0);
                this.setButton(forget2, move2, 1);
                this.setButton(forget3, move3, 2);
                this.setButton(forget4, move4, 3);
                this.setVisible(true);
        }
        
        private void setButton(JLabel l, JButton b, int x) {
            l = new JLabel("Forget:");
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.getContentPane().add(l);
            b = new JButton(pk.getCurrentMoves().get(x).name());
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainController.getController().learnMove(pk, pk.getCurrentMoves().get(x), newMove);
                    View.getView().disposeCurrent();
                    View.getView().removeCurrent();
                    View.getView().resumeCurrent();
                } 
            });
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.getContentPane().add(b);
            this.getContentPane().add(Box.createVerticalGlue());
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