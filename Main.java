/*
 * Main.java
 *
 * Created on 22 February 2005, 22:23
 */

package chequeredflag;

import chequeredflag.gui.*;

/**
 *
 * @author barrie
 */
public class Main 
{
    
    private MainGUI mainScreen;
    
    /** Creates a new instance of Main */
    public Main() 
    {
           mainScreen = new MainGUI();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        Main CheqFlag = new Main();
    }
    
}
