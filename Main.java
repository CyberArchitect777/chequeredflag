/*
 * Main.java
 *
 * Created on 22 February 2005, 22:23
 */

package chequeredflag;

import chequeredflag.gui.*;

import javax.swing.*;

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
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
