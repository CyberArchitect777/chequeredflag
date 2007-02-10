/*
 * Chequered Flag: An editor for Formula One Grand Prix/World Circuit
 * Copyright (C) 2005-2007  The Chequered Flag Development Team
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
 * GameTweaks.java
 *
 * Created on 01 January 2007, 03:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package chequeredflag.data.executable;

import java.io.*;

/**
 *
 * @author barrie
 */
public class GameTweaks 
{
    
    private boolean qualifyingTyres;
    private boolean fastFade;
    private boolean languageSelection;
    
    /**
     * Creates a new instance of GameTweaks
     */
    public GameTweaks() 
    {
        qualifyingTyres = false;
        fastFade = false;
        languageSelection = false;
    }
    
    public boolean getQualifyingTyres()
    {
        return qualifyingTyres;
    }
    
    public boolean getFastFade()
    {
        return fastFade;
    }
    
    public boolean getLanguageSelection()
    {
        return languageSelection;
    }
    
    public void setQualifyingTyres(boolean newValue)
    {
        qualifyingTyres = newValue;
    }
    
    public void setFastFade(boolean newValue)
    {
        fastFade = newValue;
    }
    
    public void setLanguageSelection(boolean newValue)
    {
        languageSelection = newValue;
    }
    
    public boolean saveData(RandomAccessFile binaryAccess, int gameVersion)
    {
        byte[] fastFadeData = new byte[10];
        if (fastFade == true)
        {
            for (int x=0;x<10;x++)
            {
                fastFadeData[x] = (byte)0x90;
            }
        }
        else
        {
            fastFadeData[0] = (byte)0xEC;
            fastFadeData[1] = (byte)0xA8;
            fastFadeData[2] = 0x08;
            fastFadeData[3] = 0x75;
            fastFadeData[4] = (byte)0xFB;
            fastFadeData[5] = (byte)0xEC;
            fastFadeData[6] = (byte)0xA8;
            fastFadeData[7] = 0x08;
            fastFadeData[8] = 0x74;
            fastFadeData[9] = (byte)0xFB;           
        }
        BinaryManager.setDataBytes(gameVersion, binaryAccess, 304646, 304507, 304480, 578506, 578362, 578346, fastFadeData, fastFadeData);
        byte[] qualifyingTyresData1 = new byte[2];
        byte[] qualifyingTyresData2 = new byte[1];
        if (qualifyingTyres == true)
        {
            qualifyingTyresData1[0] = (byte)0x80;
            qualifyingTyresData1[1] = 0x75;
            qualifyingTyresData2[0] = 0x40;
        }
        else
        {
            qualifyingTyresData1[0] = 0x40;
            qualifyingTyresData1[1] = 0x74;
            qualifyingTyresData2[0] = 0x04;
        }
        BinaryManager.setDataBytes(gameVersion, binaryAccess, 44056, 44056, 44056, 48808, 48808, 48808, qualifyingTyresData1, qualifyingTyresData1);
        BinaryManager.setDataBytes(gameVersion, binaryAccess, 44083, 44083, 44083, 48835, 48835, 48835, qualifyingTyresData2, qualifyingTyresData2);
        byte[] languageSelectionData1 = new byte[1];
        byte[] languageSelectionData2 = new byte[1];
        byte[] languageSelectionData3 = new byte[1];
        if (languageSelection == true)
        {
            languageSelectionData1[0] = 0x00;
            languageSelectionData2[0] = 0x00;
            languageSelectionData3[0] = 0x00;
        }
        else
        {
            if ((gameVersion == 2) || (gameVersion == 5))
            {
                languageSelectionData1[0] = 0x7D;
            }
            else
            {
                languageSelectionData1[0] = (byte)0xA8;
            }
            languageSelectionData2[0] = 0x2C;
            languageSelectionData3[0] = 0x1F;                    
        }
        BinaryManager.setDataBytes(gameVersion, binaryAccess, 203440, 203338, 203396, 1, 1, 1, languageSelectionData1, languageSelectionData1);
        BinaryManager.setDataBytes(gameVersion, binaryAccess, 203450, 203398, 203406, 1, 1, 1, languageSelectionData2, languageSelectionData2);
        BinaryManager.setDataBytes(gameVersion, binaryAccess, 203467, 203415, 203423, 1, 1, 1, languageSelectionData3, languageSelectionData3);
        return true;
    }
    
    public boolean loadData(RandomAccessFile binaryAccess, int gameVersion)
    {
        //byte[] dataValue = new byte[3];
        byte[] dataValues;
        dataValues = BinaryManager.getDataBytes(gameVersion, binaryAccess, 304646, 304507, 304480, 578506, 578362, 578346, 1);
        System.out.println(dataValues);
        if (dataValues[0] == (byte)0x90)
        {
            fastFade = true;
        }
        else
        {
            fastFade = false;
        }
        qualifyingTyres = false;
        dataValues = BinaryManager.getDataBytes(gameVersion, binaryAccess, 44056, 44056, 44056, 48808, 48808, 48808, 1);
        if (dataValues[0] == (byte)0x80)
        {
            qualifyingTyres = true;
        }
        else
        {
            qualifyingTyres = false;
        }
        dataValues = BinaryManager.getDataBytes(gameVersion, binaryAccess, 203450, 203398, 203406, 433538, 433474, 433490, 1);
        if (dataValues[0] == 0x00)
        {
            languageSelection = true;
        }
        else
        {
            languageSelection = false;
        }
        return true;
    }       
}
