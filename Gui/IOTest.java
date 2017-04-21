package Gui;
import java.io.FileOutputStream;

/**
 * Created by bravorr on 4/21/17.
 */
public class IOTest {

        public static void main(String args[]){
            try{
                FileOutputStream fout=new FileOutputStream("testout.txt");
                fout.write(65);
                fout.close();
                System.out.println("success...");
            }catch(Exception e){System.out.println(e);}
        }

}
