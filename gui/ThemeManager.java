/*
 * ThemeManager.java
 *
 * Created on 10 December 2006, 23:43
 */

package chequeredflag.gui;

import javax.swing.*;
import java.awt.Component;

/**
 *
 * @author barrie
 */
public class ThemeManager 
{
    
    /** Creates a new instance of ThemeManager */
    public ThemeManager() 
    {
        
    }
    
    public void setUserInterfaceType(String interfaceMode, MainGUI visualDisplay)
    {
        if (interfaceMode.compareTo("Metal") == 0)
        {
            try
            {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                SwingUtilities.updateComponentTreeUI(visualDisplay);
            }
            catch (Exception exceptionError)
            {
                exceptionError.printStackTrace();
            }
        }
        else
        {
            if (interfaceMode.compareTo("Motif") == 0)
            {
                try
                {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(visualDisplay);
                }
                catch (Exception exceptionError)
                {
                    exceptionError.printStackTrace();
                }
            }
            else
            {
                if (interfaceMode.compareTo("System Default") == 0)
                {
                    try
                    {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        SwingUtilities.updateComponentTreeUI(visualDisplay);
                    }
                    catch (Exception exceptionError)
                    {
                        exceptionError.printStackTrace();
                    }
                }
                else
                {
                    if (interfaceMode.compareTo("Windows") == 0)
                    {
                        try
                        {
                            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                            SwingUtilities.updateComponentTreeUI(visualDisplay);                        
                        }
                        catch (Exception exceptionError)
                        {
                            exceptionError.printStackTrace();
                        }
                    }
                    else
                    {
                        if (interfaceMode.compareTo("GTK") == 0)
                        {
                            try
                            {
                                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                                SwingUtilities.updateComponentTreeUI(visualDisplay);                        
                            }
                            catch (Exception exceptionError)
                            {
                                exceptionError.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
    
}
