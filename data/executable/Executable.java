/*
 * Chequered Flag: An editor for Formula One Grand Prix/World Circuit
 * Copyright (C) 2005-2006  The Chequered Flag Development Team
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

/*
 * Executable.java
 *
 * Created on 30 December 2006, 20:25
 */

package chequeredflag.data.executable;

import java.io.*;

/**
 *
 * @author barrie
 */
public class Executable 
{
    
    private int gameVersion;
    private File gameFile;
    private GameMods gameTweaks;
    
    /** Creates a new instance of Executable */
    public Executable(File loadedFile)
    {
        gameFile = loadedFile;
        int executableSize = (int)gameFile.length();     
                
        switch(executableSize)
        {
            case 321878: gameVersion = 1; System.out.println("1.05 European version detected"); break;
            case 321748: gameVersion = 2; System.out.println("1.05 Italian version detected"); break;
            case 321716: gameVersion = 3; System.out.println("1.05 US version detected"); break;
            default: gameVersion = 4; break;
        }    
        
        gameTweaks = new GameMods();
    }
    
    public int returnGameVersionID()
    {
        return gameVersion;
    }
    
    public String returnGameVersionString()
    {
        switch (gameVersion)
        {
            case 1: return "1.05 European version";
            case 2: return "1.05 Italian version";
            case 3: return "1.05 US version";
            default: return "Unknown version";
        }
    }   
    
    public GameMods getGameTweaks()
    {
        return gameTweaks;
    }
    
    public boolean saveData()
    {
        // Saves data back into a F1GP/WC executable file. Returns a code to indicate the completed saving status
        // Code True - Success, False - Failure
        
        String filePath = gameFile.getPath();
        try
        {
            RandomAccessFile binaryAccess = new RandomAccessFile(filePath, "rw");
            boolean gameTweaksSuccess = gameTweaks.saveData(binaryAccess, gameVersion);
            binaryAccess.close();
            if (gameTweaksSuccess == true)
            {
                return true;
            }
            else
            {
                return false;
            }            
        }
        catch (Exception exceptionError)
        {
            exceptionError.printStackTrace();
            return false;
        }            
        
    }
    
    public int loadData()
    {
        // Loads an F1GP/WC executable file. Returns a code to indicate the completed loading status
        // Code: 0 - Success, 1 - Invalid File, 2 - Unknown Error
        
        if (gameVersion > 3)
        {
            return 1;
        }
        else
        {
            String filePath = gameFile.getPath();
            try
            {
                RandomAccessFile binaryAccess = new RandomAccessFile(filePath, "r");
                boolean gameTweaksSuccess = gameTweaks.loadData(binaryAccess, gameVersion);
                if (gameTweaksSuccess == true)
                {
                    return 0;
                }
                else
                {
                    return 2;
                }
            }
            catch (Exception exceptionError)
            {
                return 2;
            }            
        }
    }    
}
