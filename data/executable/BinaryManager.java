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
 * BinaryManager.java
 *
 * Created on 01 January 2007, 03:22
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
public class BinaryManager 
{
    
    /** Creates a new instance of BinaryManager */
    public BinaryManager() 
    {
    }
    
    public static void setDataBytes(int gameVersion, RandomAccessFile openFile, int europeanPackedVersion, int italianPackedVersion, int usPackedVersion, int europeanUnpackedVersion, int italianUnpackedVersion, int usUnpackedVersion, byte[] packedDataValues, byte[] unpackedDataValues)
    {
        try
        {
            switch (gameVersion)
            {
                case 1: openFile.seek(europeanPackedVersion); /*System.out.println("Seeking to: " + europeanPackedVersion);*/ break;
                case 2: openFile.seek(italianPackedVersion); /*System.out.println("Seeking to: " + italianPackedVersion);*/ break;
                case 3: openFile.seek(usPackedVersion); /*System.out.println("Seeking to: " + usPackedVersion);*/ break;
                case 4: openFile.seek(europeanUnpackedVersion); /*System.out.println("Seeking to: " + europeanUnpackedVersion);*/ break;
                case 5: openFile.seek(italianUnpackedVersion); /*System.out.println("Seeking to: " + italianUnpackedVersion);*/ break;
                case 6: openFile.seek(usUnpackedVersion); /*System.out.println("Seeking to: " + usUnpackedVersion);*/ break;
            }
            if (gameVersion < 4)
            {
                for (int x=0;x<packedDataValues.length;x++)
                {
                    //System.out.println("Writing value: " + packedDataValues[x]);
                    openFile.write(packedDataValues[x]);
                }
            }
            else
            {
                for (int x=0;x<unpackedDataValues.length;x++)
                {
                    //System.out.println("Writing value: " + unpackedDataValues[x]);
                    openFile.write(unpackedDataValues[x]);
                }
            }
        }
        catch (Exception exceptionError)
        {
            exceptionError.printStackTrace();
        }
    }
    
    public static byte[] getDataBytes(int gameVersion, RandomAccessFile openFile, int europeanPackedVersion, int italianPackedVersion, int usPackedVersion, int europeanUnpackedVersion, int italianUnpackedVersion, int usUnpackedVersion, int numberBytes)
    {
        byte[] dataValues = new byte[numberBytes];
        try
        {
            for (int x=0;x<numberBytes;x++)
            {
                switch (gameVersion)
                {
                    case 1: openFile.seek(europeanPackedVersion+x); /*System.out.println("Seeking to: " + europeanPackedVersion+x);*/ break;
                    case 2: openFile.seek(italianPackedVersion+x); /*System.out.println("Seeking to: " + italianPackedVersion+x);*/ break;
                    case 3: openFile.seek(usPackedVersion+x); /*System.out.println("Seeking to: " + usPackedVersion+x);*/ break;
                    case 4: openFile.seek(europeanUnpackedVersion+x); /*System.out.println("Seeking to: " + europeanUnpackedVersion+x);*/ break;
                    case 5: openFile.seek(italianUnpackedVersion+x); /*System.out.println("Seeking to: " + italianUnpackedVersion+x);*/ break;
                    case 6: openFile.seek(usUnpackedVersion+x); /*System.out.println("Seeking to: " + usUnpackedVersion+x);*/ break;
                }
                dataValues[x] = (byte)openFile.read();
                //System.out.println("Reading value: " + dataValues[x]);
                //int unsignedByte = (0x000000FF & dataValue); // Convert signed to unsigned byte (still inside integer)
                //short convertedByte = (short)unsignedByte; // Move the integer type to a short
            }
            return dataValues;
        }
        catch (Exception exceptionError)
        {
            exceptionError.printStackTrace();
            byte[] emptyValue = new byte[1];
            emptyValue[0] = 0x00;
            return emptyValue;
        }
    }    
}
