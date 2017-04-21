package Gui;

import Tools.FileIO;
import Tools.Utils;
import Misc.GuiComp;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * a gui item that is used to efficiently visualize in 2 dimensions
 * uses an array of pixels whose color values are individually set
 */
public class GuiVis extends JPanel implements GuiComp {
    public final int xDim;
    public final int yDim;
    public final int scale;
    public final int compX;
    public final int compY;
    BufferedImage buff;
    int[] data;
    Graphics2D g;

    /**
     * @param pixelW width of the GuiVis in pixels
     * @param pixelH height of the GuiVis in pixels
     * @param scaleFactor the width and height in screen pixels of each GuiVis pixel
     * @param compX width on the gui GridBagLayout
     * @param compY height on the gui GridBagLayout
     */
    public GuiVis(int pixelW, int pixelH, int scaleFactor,int compX,int compY){
        this.setVisible(true);
        xDim=pixelW;
        yDim=pixelH;
        this.compX=compX;
        this.compY=compY;
        buff=new BufferedImage(xDim,yDim,BufferedImage.TYPE_INT_RGB);
        data=((DataBufferInt)buff.getRaster().getDataBuffer()).getData();
        scale=scaleFactor;
        this.setPreferredSize(new Dimension((int)Math.ceil(xDim*scaleFactor), (int)Math.ceil(yDim*scaleFactor)));
        this.setMaximumSize(new Dimension((int)Math.ceil(xDim*scaleFactor), (int)Math.ceil(yDim*scaleFactor)));
        this.setMinimumSize(new Dimension((int)Math.ceil(xDim*scaleFactor), (int)Math.ceil(yDim*scaleFactor)));
    }

    /**
     * @param pixelW width of the GuiVis in pixels
     * @param pixelH height of the GuiVis in pixels
     * @param scaleFactor the width and height in screen pixels of each GuiVis pixel
     */
    public GuiVis(int pixelW, int pixelH, int scaleFactor){
        this.setVisible(true);
        xDim=pixelW;
        yDim=pixelH;
        this.compX=1;
        this.compY=1;
        buff=new BufferedImage(xDim,yDim,BufferedImage.TYPE_INT_RGB);
        data=((DataBufferInt)buff.getRaster().getDataBuffer()).getData();
        scale=scaleFactor;
        this.setPreferredSize(new Dimension((int)Math.ceil(xDim*scaleFactor), (int)Math.ceil(yDim*scaleFactor)));
        this.setMaximumSize(new Dimension((int)Math.ceil(xDim*scaleFactor), (int)Math.ceil(yDim*scaleFactor)));
        this.setMinimumSize(new Dimension((int)Math.ceil(xDim*scaleFactor), (int)Math.ceil(yDim*scaleFactor)));
    }
    /**
     * sets pixel at the specified x,y position to the rgb color value specified, bounding components between 0 and 1
     */
    public void SetColorBound(int x, int y, float r, float g, float b){
        r=Utils.BoundValF(r,0,1);
        g=Utils.BoundValF(g,0,1);
        b=Utils.BoundValF(b,0,1);
        SetColor(x,y,r,g,b);
    }

    /**
     * sets the pixel at the specified x,y position using the heat colormap, which goes from black to red to yellow to white
     */
    public void SetColorHeat(int x, int y, double val) {
        float r = (float) Math.min(1, val * 3);
        float g = 0;
        float b = 0;
        if (val > 0.333) {
            g = (float) Math.min(1, (val - 0.333) * 3);
        }
        if (val > 0.666) {
            b = (float) Math.min(1, (val - 0.666) * 3);
        }
        SetColor(x, y, r, g, b);
    }

    /**
     * sets the pixel at the specified x,y position using the heat colormap, which goes from black to red to yellow to white
     * @param colorOrder the order in which to fill the colors, default is rgb, but any permutation of these three characters is valid
     */
    public void SetColorHeat(int x, int y, double val,String colorOrder) {
        float c1 = (float) Math.min(1, val * 3);
        float c2 = 0;
        float c3 = 0;
        if (val > 0.333) {
            c2 = (float) Math.min(1, (val - 0.333) * 3);
        }
        if (val > 0.666) {
            c3 = (float) Math.min(1, (val - 0.666) * 3);
        }
        switch (colorOrder) {
            case "rgb":
                SetColor(x, y, c1, c2, c3);
                break;
            case "rbg":
                SetColor(x, y, c1, c3, c2);
                break;
            case "grb":
                SetColor(x, y, c2, c1, c3);
                break;
            case "gbr":
                SetColor(x, y, c3, c1, c2);
                break;
            case "brg":
                SetColor(x, y, c2, c3, c1);
                break;
            case "bgr":
                SetColor(x, y, c3, c2, c1);
                break;
            default:
                SetColor(x, y, c1, c2, c3);
                System.out.println("Invalid colorOrder string passed to SetColorHeat:" + colorOrder + "\ncolorOrder String must be some permutation of the characters 'r','g','b'");
                break;
        }
    }
    public void SetColorHeatBound(int x,int y,double val){
        val=Utils.BoundVal(val,0,1);
        SetColorHeat(x,y,val);
    }
    public void SetColorHeatBound(int x,int y,double val,String colorOrder){
        val=Utils.BoundVal(val,0,1);
        SetColorHeat(x,y,val,colorOrder);
    }

    /**
     * gets the x component of the vis window
     */
    @Override
    public int compX(){return compX;}
    /**
     * gets the y component of the vis window
     */
    @Override
    public int compY(){return compY;}

    /**
     * called by the GuiWindow class to place the vis window
     */
    @Override
    public void GetComps(ArrayList<Component> putHere, ArrayList<Integer> coordsHere, ArrayList<Integer> compSizesHere) {
        putHere.add(this);
        coordsHere.add(0);
        coordsHere.add(0);
        compSizesHere.add(compX);
        compSizesHere.add(compY);
    }
    public void SetColor(int x, int y, float r, float g, float b){
        Color c=new Color(r,g,b);
        //buff.setRGB(x,compY-1-y,c.getRGB());
        data[(yDim-y-1)*xDim+x]=c.getRGB();
    }

    /**
     * sets all pixels to the rgb color specified, bounding components between 0 and 1
     */
    public void ClearColor(float r, float g, float b){
        r=Utils.BoundValF(r,0,1);
        g=Utils.BoundValF(g,0,1);
        b=Utils.BoundValF(b,0,1);
        Color c=new Color(r,g,b);
        Arrays.fill(data,c.getRGB());
    }

    /**
     * called by the GuiWindow to draw the vis
     */
    @Override
    public void paint(Graphics g){
        ((Graphics2D)g).drawImage(buff.getScaledInstance(scale*xDim,scale*yDim,Image.SCALE_FAST),null,null);
        repaint();
    }
    void WriteOut(String path,String type){
        try {
            FileOutputStream out=new FileOutputStream(path);
            out.write(12);
            try {
                if(!ImageIO.write(buff, type, out)){
                    System.err.println("unable to write image to path"+path);
                }
                out.close();
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void ToPNG(String path){
        WriteOut(path,"png");
    }
    public void ToJPG(String path){
        WriteOut(path,"jpg");
    }
    public void ToGIF(String path){
        WriteOut(path,"gif");
    }
}
