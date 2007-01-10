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
 * GameMods.java
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
public class GameMods 
{
    
    private boolean qualifyingTyres;
    private boolean fastFade;
    private boolean languageSelection;
    
    /** Creates a new instance of GameMods */
    public GameMods() 
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
        byte[] byteValue = new byte[3];
        for (int x=0;x<3;x++)
        {
            byteValue[x] = 0;
        }
        if (fastFade == true)
        {
            byteValue[0] = 0x18;
            
        }
        else
        {
            byteValue[0] = (byte)0x80;
        }
        BinaryManager.setDataByte(gameVersion, binaryAccess, 125581, 125529, 125537, new Byte(byteValue[0]));
        if (qualifyingTyres == true)
        {
            byteValue[0] = 0x01;
        }
        else
        {
            byteValue[0] = 0x00;
        }
        BinaryManager.setDataByte(gameVersion, binaryAccess, 58395, 58395, 58395, new Byte(byteValue[0]));
        if (languageSelection == true)
        {
            byteValue[0] = 0x00;
            byteValue[1] = 0x00;
            byteValue[2] = 0x00;
        }
        else
        {
            byteValue[0] = (byte)0xA8;
            byteValue[1] = 0x2C;
            byteValue[2] = 0x1F;                    
        }
        BinaryManager.setDataByte(gameVersion, binaryAccess, 203440, 203338, 203396, new Byte(byteValue[0]));
        BinaryManager.setDataByte(gameVersion, binaryAccess, 203450, 203398, 203406, new Byte(byteValue[1]));
        BinaryManager.setDataByte(gameVersion, binaryAccess, 203467, 203415, 203423, new Byte(byteValue[2]));
        return true;
    }
    
    public boolean loadData(RandomAccessFile binaryAccess, int gameVersion)
    {
        Byte dataValue = BinaryManager.getDataByte(gameVersion, binaryAccess, 125581, 125529, 125537);
        System.out.println(dataValue);
        //if (dataValue == 24)
        if (dataValue == 0x18)
        {
            fastFade = true;
        }
        else
        {
            fastFade = false;
        }
        dataValue = BinaryManager.getDataByte(gameVersion, binaryAccess, 58395, 58395, 58395);
        System.out.println(dataValue);
        //if (dataValue == 1)
        if (dataValue == 0x01)
        {
            qualifyingTyres = true;
        }
        else
        {
            qualifyingTyres = false;
        }
        dataValue = BinaryManager.getDataByte(gameVersion, binaryAccess, 203440, 203338, 203396);
        System.out.println(dataValue);
        //if (dataValue == 0)
        if (dataValue == 0x00)
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
