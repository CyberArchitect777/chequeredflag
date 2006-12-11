/*
 * RecentFiles.java
 *
 * Created on 10 December 2006, 15:04
 */

package chequeredflag.data.gui;

import java.util.ArrayList;
import java.io.*;
import java.util.prefs.Preferences;

/**
 *
 * @author barrie
 */
public class RecentFiles 
{
    
    private String[] recentFileList;
    private int numberFiles;
    
    /** Creates a new instance of RecentFiles */
    
    public RecentFiles() 
    {
        recentFileList = new String[4];
        int numberFiles = 0;
        loadRecentFiles();
    }
    
    public boolean addNewFile(String fileName)
    {
        
        // Adds a new recent file to the list if it is not blank or a duplicate of one already available.
        
        boolean itemAdded = false;
        if ((fileName.compareTo("") != 0) && (fileName.compareTo(recentFileList[0]) != 0) && (fileName.compareTo(recentFileList[1]) != 0) && (fileName.compareTo(recentFileList[2]) != 0) && (fileName.compareTo(recentFileList[3]) != 0))
        {
            recentFileList[3] = recentFileList[2];
            recentFileList[2] = recentFileList[1];
            recentFileList[1] = recentFileList[0];
            recentFileList[0] = fileName;
            itemAdded = true;
            if (numberFiles < 4)
            {
                numberFiles++;
            }
            saveRecentFiles();
        }
        return itemAdded;
    }
    
    public String getRecentFilePath(int fileIndex)
    {
        // Returns the specified recent file path
        
        return recentFileList[fileIndex];
    }
    
    public String getRecentFileName(int fileIndex)
    {
        // Returns the specified recent filename
        
        int intDirIndex = recentFileList[fileIndex].lastIndexOf(File.separator);
        return recentFileList[fileIndex].substring(intDirIndex+1,recentFileList[fileIndex].length());
    }
    
    public void saveRecentFiles()
    {
        Preferences userData = Preferences.userRoot();
        Preferences cheqFlagData = userData.node("chequeredflag");
        for (int x=0;x<4;x++)
        {
            cheqFlagData.put("CFRecentFile" + (x+1), recentFileList[x]);
        }
    }
    
    public void loadRecentFiles()
    {
        Preferences userData = Preferences.userRoot();
        Preferences cheqFlagData = userData.node("chequeredflag");
        for (int x=0;x<4;x++)
        {
            recentFileList[x] = cheqFlagData.get("CFRecentFile" + (x+1), "");
            if (recentFileList[x].compareTo("") != 0)
            {
                numberFiles++;
            }
        }
    }
    
    public int getNumberFiles()
    {
        return numberFiles;
    }
    
}
