package Gui;

import Misc.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * a menu item that takes string input
 */
public class MenuString extends JTextField implements Misc.MenuItem,GuiComp {
    String labelText;
    public JLabel label;
    int compX;
    int compY;
    int nCols;

    /**
     * @param mySet the MenuSet that can be queried to get the value of this menuString
     * @param label the label of the menuString
     * @param initVal the starting value of the menuString
     */
    public MenuString(MenuSet mySet, String label, String initVal){
        super(10);
        nCols=10;
        this.compX=1;
        this.compY=2;
        this.labelText=label;
        this.label=new JLabel(labelText);
        mySet.AddGuiMenuItem(this,initVal);
    }

    /**
     * @param mySet the MenuSet that can be queried to get the value of this menuString
     * @param label the label of the menuString
     * @param initVal the starting value of the menuString
     * @param nCols the number of characters that will fit on the display
     * @param compX the width on the gui GridBagLayout
     * @param compY the height on the gui GridBagLayout
     */
    public MenuString(MenuSet mySet, String label, String initVal, int nCols, int compX, int compY){
        super(nCols);
        this.nCols=nCols;
        this.compX=compX;
        this.compY=compY;
        this.labelText=label;
        this.label=new JLabel(labelText);
        mySet.AddGuiMenuItem(this,initVal);
    }

    /**
     * sets the foreground and background of the MenuString
     * @param foregroundColor color of the text if null the GuiWin color will be used
     * @param backgroundColor color of the background, if null the GuiWin color will be used
     */
    public MenuString SetColor(Color foregroundColor, Color backgroundColor){
        if(backgroundColor!=null){
            setOpaque(true);
            setBackground(backgroundColor);
            label.setOpaque(true);
            label.setBackground(backgroundColor);
        }
        if(foregroundColor !=null) {
            setForeground(foregroundColor);
            label.setForeground(foregroundColor);
            setCaretColor(foregroundColor);
        }
        return this;
    }

    /**
     * ignore
     */
    @Override
    public int TypeID() {
        return 3;
    }

    /**
     * ignore
     */
    @Override
    public void Set(String val) {
        if(val.length()>nCols){ val=val.substring(0,nCols); }
        this.setText(val);
    }

    /**
     * ignore
     */
    @Override
    public String Get() {
        return this.getText();
    }

    /**
     * ignore
     */
    @Override
    public String GetLabel() {
        return labelText;
    }

    /**
     * ignore
     */
    @Override
    public int NEntries() {
        return 2;
    }

    /**
     * ignore
     */
    @Override
    public <T extends Component> T GetEntry(int iEntry) {
        switch(iEntry){
            case 0: return (T)label;
            case 1: return (T)this;
            default: throw new IllegalArgumentException(iEntry+" does not match to an item!");
        }
    }

    /**
     * ignore
     */
    @Override
    public int compX() {
        return compX;
    }

    /**
     * ignore
     */
    @Override
    public int compY() {
        return compY;
    }

    /**
     * ignore
     */
    @Override
    public void GetComps(ArrayList<Component> putHere, ArrayList<Integer> coordsHere, ArrayList<Integer> compSizesHere) {
        int labelEnd=compY/2;
        putHere.add(this.label);
        coordsHere.add(0);
        coordsHere.add(0);
        compSizesHere.add(compX);
        compSizesHere.add(labelEnd);
        putHere.add(this);
        coordsHere.add(0);
        coordsHere.add(labelEnd);
        compSizesHere.add(compX);
        compSizesHere.add(compY-labelEnd);
    }
}
