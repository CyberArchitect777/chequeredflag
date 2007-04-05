
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
 * GameDrivers.java
 *
 * Created on 24 February 2007, 00:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package chequeredflag.data.executable;

import java.io.*;
import java.util.Vector;

/**
 *
 * @author barrie
 */
public class GameDrivers 
{
    
    private Vector driverNames;
    private int[] qualifyingGrip;
    private int[] raceGrip;
        
    /** Creates a new instance of GameDrivers */
    public GameDrivers() 
    {
        driverNames = new Vector();
        qualifyingGrip = new int[40];
        raceGrip = new int[40];
        for (int x=0;x<40;x++)
        {
            qualifyingGrip[x] = 0;
            raceGrip[x] = 0;
        }
        
    }
    
    public String getDriverName(int nameIndex)
    {
        return (String)driverNames.get(nameIndex);
    }
    
    public int getQualifyingGrip(int gripIndex)
    {
        return qualifyingGrip[gripIndex];
    }
    
    public int getRaceGrip(int gripIndex)
    {
        return raceGrip[gripIndex];
    }
    
    public void setQualifyingGrip(int gripIndex, int gripValue)
    {
        qualifyingGrip[gripIndex] = gripValue;
    }
    
    public void setRaceGrip(int gripIndex, int gripValue)
    {
        raceGrip[gripIndex] = gripValue;
    }
    
    public Vector getAllDriverNames()
    {
        return driverNames;
    }
    
    public void setDriverName(int nameIndex, String nameString)
    {
        driverNames.set(nameIndex, nameString);
    }
    
    public int saveData(RandomAccessFile binaryAccess, int gameVersion)
    {
        byte[] driverQualifyingData = new byte[40];
        byte[] driverRaceData = new byte[40];
        for (int x=0;x<40;x++)
        {
            driverQualifyingData[x] = (byte)qualifyingGrip[x];
            driverRaceData[x] = (byte)raceGrip[x];
        }
        BinaryManager.setDataBytes(gameVersion, binaryAccess, 158420, 158368, 158376, 184036, 183972, 183988, driverQualifyingData, driverQualifyingData);
        BinaryManager.setDataBytes(gameVersion, binaryAccess, 158460, 158408, 158416, 184076, 184012, 184028, driverRaceData, driverRaceData);
        return 0;
    }
    
    public int loadData(RandomAccessFile binaryAccess, int gameVersion)
    {
        
        // Detected grip data European packed = offset 158420, offset 158460
        // Detected grip data Italian packed = offset 158368, offset 158408
        // Detected grip data US packed = offset 158376, offset 158416
        // Detected grip data European unpacked = offset 184036, offset 184076
        // Detected grip data Italian unpacked = offset 183972, offset 184012
        // Detected grip data US unpacked = offset 183988, offset 184028 
        
        byte[] dataValues;
        int[] startingValue = new int[6];
        startingValue[0] = 154976; // Packed European version
        startingValue[1] = 154924; // Packed Italian version
        startingValue[2] = 154932; // Packed US version
        startingValue[3] = 180250; // Unpacked European version
        startingValue[4] = 180160; // Unpacked Italian version
        startingValue[5] = 180202; // Unpacked US version
        if (gameVersion < 4)
        {
            int currentOffset = 0;
            for (int x=0;x<40;x++)
            {
                dataValues = BinaryManager.getDataBytes(gameVersion, binaryAccess, startingValue[0]+currentOffset, startingValue[1]+currentOffset, startingValue[2]+currentOffset, startingValue[3]+currentOffset, startingValue[4]+currentOffset, startingValue[5]+currentOffset, 23);
                boolean zeroDetected = false;
                int endString = -1;
                int endData = -1;
                for (int y=0;y<dataValues.length;y++)
                {
                    if (zeroDetected == true)
                    {
                        if (dataValues[y] == (byte)0xB2)
                        {
                            if (endString == -1) // If endString hasn't already been detected
                            {
                                endString = y-2;
                            }
                        }
                        else
                        {
                            if (dataValues[y] == (byte)0xB0)
                            {
                                if (endData == -1) // If endData hasn't already been detected'
                                {
                                    endData = y+1;
                                }
                            }
                            else
                            {
                                zeroDetected = false;
                            }
                        }
                    }
                    if (dataValues[y] == 0x00)
                    {
                        zeroDetected = true;
                    }
                }
                StringBuffer currentString = new StringBuffer();
                if (endString == -1)
                {
                    endString = 23;
                }
                for (int y=0;y<endString;y++)
                {
                    if (dataValues[y] != 0x00)
                    {
                        char currentCharacter = (char)dataValues[y];
                        currentString.append(currentCharacter);
                    }
                }
                driverNames.add(currentString.toString());
                if (endData == -1)
                {
                    currentOffset = currentOffset + 24;
                }
                else
                {
                    currentOffset = currentOffset + endData;
                }
            }
        }
        else
        {
            int currentOffset = 0;
            for (int x=0;x<40;x++)
            {
                int currentByteCount = 0;
                System.out.println(x);
                boolean readEnd = false;
                StringBuffer currentString = new StringBuffer();
                dataValues = BinaryManager.getDataBytes(gameVersion, binaryAccess, startingValue[0]+currentOffset, startingValue[1]+currentOffset, startingValue[2]+currentOffset, startingValue[3]+currentOffset, startingValue[4]+currentOffset, startingValue[5]+currentOffset, 23);
                while (readEnd == false)
                {
                    if ((dataValues[currentByteCount] != 0x00) && (currentByteCount < 23))
                    {
                        char currentCharacter = (char)dataValues[currentByteCount];
                        currentString.append(currentCharacter);
                    }
                    else
                    {
                        readEnd = true;
                        driverNames.add(currentString.substring(0,currentString.length()));
                    }
                    currentByteCount++;
                }
                currentOffset = currentOffset + 24;
            }
        }
        dataValues = BinaryManager.getDataBytes(gameVersion, binaryAccess, 158420, 158368, 158376, 184036, 183972, 183988, 40);
        for (int x=0;x<40;x++)
        {
            qualifyingGrip[x] = (int)dataValues[x];
        }
        dataValues = BinaryManager.getDataBytes(gameVersion, binaryAccess, 158460, 158408, 158416, 184076, 184012, 184028, 40);
        for (int x=0;x<40;x++)
        {
            raceGrip[x] = (int)dataValues[x];
        }        
        return 0;
    }      
    
}
