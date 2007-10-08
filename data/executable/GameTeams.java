
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
 * GameTeams.java
 *
 * Created on 24 February 2007, 00:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package chequeredflag.data.executable;

import java.io.*;
import java.util.Vector;
import java.nio.ByteBuffer;

/**
 *
 * @author barrie
 */
public class GameTeams 
{
    
    private Vector teamNames;
    private Vector engineNames;
    private int[] bhpSetting;
        
    /** Creates a new instance of GameTeams */
    public GameTeams() 
    {
        teamNames = new Vector();
        engineNames = new Vector();
        bhpSetting = new int[20];
        for (int x=0;x<20;x++)
        {
            bhpSetting[x] = 0;
        }
        
    }
    
    public String getTeamName(int nameIndex)
    {
        return (String)teamNames.get(nameIndex);
    }
    
    public String getEngineName(int nameIndex)
    {
        return (String)engineNames.get(nameIndex);
    }
    
    public int getBHPSetting(int bhpIndex)
    {
        return bhpSetting[bhpIndex];
    }
    
    public void setBHPSetting(int bhpIndex, int bhpValue)
    {
        bhpSetting[bhpIndex] = bhpValue;
    }
    
    public Vector getAllTeamNames()
    {
        return teamNames;
    }
    
    public void setTeamName(int nameIndex, String nameString)
    {
        teamNames.set(nameIndex, nameString);
    }
    
    public void setEngineName(int nameIndex, String nameString)
    {
        engineNames.set(nameIndex, nameString);
    }   
    
    public int saveData(RandomAccessFile binaryAccess, int gameVersion)
    {
        /*byte[] driverQualifyingData = new byte[40];
        byte[] driverRaceData = new byte[40];
        for (int x=0;x<40;x++)
        {
            driverQualifyingData[x] = (byte)qualifyingGrip[x];
            driverRaceData[x] = (byte)raceGrip[x];
        }
        BinaryManager.setDataBytes(gameVersion, binaryAccess, 184036, 183972, 183988, driverQualifyingData);
        BinaryManager.setDataBytes(gameVersion, binaryAccess, 184076, 184012, 184028, driverRaceData);
        byte[] driverByteName = new byte[23];
        int currentOffset = 0;
        for (int x=0;x<40;x++)
        {
            StringBuffer currentString = new StringBuffer((String)driverNames.get(x));
            for (int y=0;y<23;y++)
            {
                if (y < currentString.length())
                {
                    driverByteName[y] = (byte)currentString.charAt(y);
                }
                else
                {
                    driverByteName[y] = (byte)0x00;
                }
            }
            BinaryManager.setDataBytes(gameVersion, binaryAccess, 180250+currentOffset, 180186+currentOffset, 180202+currentOffset, driverByteName);
            currentOffset = currentOffset + 24;            
        }*/
        return 0;
    }
    
    public int loadData(RandomAccessFile binaryAccess, int gameVersion)
    {
        
        // Name Offsets
        
        // European - 181210
        // Italian - 181146
        // US - 181162
        
        // BHP Offsets
        
        // European - 183996
        // Italian - 183932
        // US - 183948
        
        byte[] dataValues;
        int currentOffset = 0;
        for (int x=0;x<40;x++)
        {
            if (x == 9)
            {
                System.out.println("x" + x);
            }
            int currentByteCount = 0;
            boolean readEnd = false;
            StringBuffer currentString = new StringBuffer();
            dataValues = BinaryManager.getDataBytes(gameVersion, binaryAccess, 181210+currentOffset, 181146+currentOffset, 181162+currentOffset, 13);
            while (readEnd == false)
            {
                if ((dataValues[currentByteCount] != 0x00) && (currentByteCount < 12))
                {
                    char currentCharacter = (char)dataValues[currentByteCount];
                    currentString.append(currentCharacter);
                }
                else
                {
                    readEnd = true;
                    if (x < 20)
                    {
                        teamNames.add(currentString.substring(0,currentString.length()));
                    }
                    else
                    {
                        engineNames.add(currentString.substring(0,currentString.length()));
                    }
                }
                currentByteCount++;
            }
            currentOffset = currentOffset + 13;
        }
        dataValues = BinaryManager.getDataBytes(gameVersion, binaryAccess, 183996, 183932, 183948, 40);
        int currentCounter = 0;
        for (int x=0;x<40;x=x+2)
        {
            try
            {
                ByteBuffer collectiveBytes = ByteBuffer.allocate(2);
                collectiveBytes.put(dataValues[x+1]);
                collectiveBytes.put(dataValues[x]);
                short combinedValue = collectiveBytes.getShort(0);
                bhpSetting[currentCounter] = (int)combinedValue;
                currentCounter++;
            }
            catch (Exception exceptionError)
            {
                exceptionError.printStackTrace();
            }
        }
        return 0;
    }
}
