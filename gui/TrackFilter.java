/*
 * TrackFilter.java
 *
 * Created on 18 March 2005, 23:36
 */

package chequeredflag.gui;

import javax.swing.filechooser.FileFilter;
import java.io.*;

/**
 *
 * @author barrie
 */
public class TrackFilter extends FileFilter
{
    
    /** Creates a new instance of TrackFilter */
    public TrackFilter() 
    {
        
    }
    
    public boolean accept(File trackFilter)
    {
        return (trackFilter.getName().toLowerCase().startsWith("f1ct") && trackFilter.getName().toLowerCase().endsWith(".dat")) || trackFilter.isDirectory();
    }
    
    public String getDescription()
    {
        return "F1GP/WC Track File";
    }
    
}
