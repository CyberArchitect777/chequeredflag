/*
 * Main.java
 *
 * Created on 22 February 2005, 22:23
 */

package chequeredflag;

import chequeredflag.gui.*;

import javax.swing.*;
import java.util.prefs.Preferences;

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
        Preferences userData = Preferences.userRoot();
        Preferences cheqFlagData = userData.node("chequeredflag");
        String interfaceType = cheqFlagData.get("CFUIMode", "System Default");
        mainScreen = new MainGUI();
        ThemeManager userInterfaceTheme = new ThemeManager();
        userInterfaceTheme.setUserInterfaceType(interfaceType, mainScreen);        
    }
       
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        Main CheqFlag = new Main();
    }
    
}
