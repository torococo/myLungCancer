package AgentGridMin;

import Model.CONST_AND_FUNCTIONS;
import Model.CONST_AND_FUNCTIONS.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * Created by bravorr on 10/21/16.
 */
public class Visualizer extends JPanel{
    public final int xDim;
    public final int yDim;
    public final int scale;
    BufferedImage buff;
    int[] data;
    Graphics2D g;
    public Visualizer(int x, int y, int scaleFactor){
        this.setVisible(true);
        xDim=x;
        yDim=y;
        buff=new BufferedImage(xDim,yDim,BufferedImage.TYPE_INT_RGB);
        data=((DataBufferInt)buff.getRaster().getDataBuffer()).getData();
        scale=scaleFactor;
        this.setPreferredSize(new Dimension((int)Math.ceil(xDim*scaleFactor), (int)Math.ceil(yDim*scaleFactor)));
        this.setMaximumSize(new Dimension((int)Math.ceil(xDim*scaleFactor), (int)Math.ceil(yDim*scaleFactor)));
        this.setMinimumSize(new Dimension((int)Math.ceil(xDim*scaleFactor), (int)Math.ceil(yDim*scaleFactor)));
    }
    public void SetBound(int x,int y,float r,float g,float b){
        r=Utils.BoundVal(r,0,1);
        g=Utils.BoundVal(g,0,1);
        b=Utils.BoundVal(b,0,1);
        Set(x,y,r,g,b);
    }
    public void SetHeat(int x,int y,double val){
        if(val>0) {
            float r = (float) Math.min(1, val * 3);
            float g = 0;
            float b = 0;
            if (val > 0.333) {
                g = (float) Math.min(1, (val - 0.333) * 3);
            }
            if (val > 0.666) {
                b = (float) Math.min(1, (val - 0.666) * 3);
            }
            Set(x, y, r, g, b);
        }
    }

    public void SetHeatCold(int x,int y,double val){
        if(val>0) {
            float b = (float) Math.min(1, val * 3);
            float g = 0;
            float r = 0;
            if (val > 0.333) {
                g = (float) Math.min(1, (val - 0.333) * 3);
            }
            if (val > 0.666) {
                r = (float) Math.min(1, (val - 0.666) * 3);
            }
            Set(x, y, r, g, b);
        }
    }


    public void MultipleDensitiesSet(int x,int y, double d1, double d2, double d3){
        double val = 0;
        float b = 1;
        float r = 1;
        float g = 1;
        if (d3 > 0) {
            r = (float) (d3 / CONST_AND_FUNCTIONS.MAX_POP);
            g = 0;
        } else if (d2 > 0)
        {
            g = (float) (d2 / CONST_AND_FUNCTIONS.MAX_POP);
        }
        else if (d1 > 0)
        {
            b = (float) (d1 / CONST_AND_FUNCTIONS.MAX_POP);
            g = 0;
        }
        Set(x,y,r,g,b);
    }

    public void Set(int x,int y,float r,float g,float b){
        Color c=new Color(r,g,b);
        //buff.setRGB(x,yDim-1-y,c.getRGB());
        data[(yDim-y-1)*xDim+x]=c.getRGB();
    }


    public void Clear(float r,float g,float b){
        Color c=new Color(r,g,b);
        Arrays.fill(data,c.getRGB());
    }
    @Override
    public void paint(Graphics g){
        ((Graphics2D)g).drawImage(buff.getScaledInstance(scale*xDim,scale*yDim,Image.SCALE_FAST),null,null);
        repaint();
    }
    public Graphics2D paint2(Graphics g){
        ((Graphics2D)g).drawImage(buff.getScaledInstance(scale*xDim,scale*yDim,Image.SCALE_FAST),null,null);
        return (Graphics2D)g;
    }
    //public void Draw(){
        //BufferStrategy bs=this.getBufferStrategy();
        //Graphics g=bs.getDrawGraphics();
        //g.drawImage(buff,0,0,(int)(xDim*scale),(int)(yDim*scale),null);
        //g.drawImage(buff,0,0,xDim,yDim,null);
        //g.dispose();
        //bs.show();
    //}


    void WriteOut(String path,String type){
        try {
            FileOutputStream out=new FileOutputStream(path);
            out.write(12);
            try {
                Graphics2D gOut=buff.createGraphics();
                paint2(gOut);
                if(!ImageIO.write(buff, "gif", out)){
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
        WriteOut(path,"PNG");
    }
    public void ToJPG(String path){
        WriteOut(path,"JPG");
    }
    public void ToGIF(String path){
        WriteOut(path,"GIF");
    }
}
