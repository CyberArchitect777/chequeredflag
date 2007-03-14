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

package chequeredflag.data.track;

/*
 * User: Rene
 * Date: 18-okt-2005
 * Time: 22:11:29
 * $Id: Seg.java,v 1.4 2007/03/14 21:44:48 ksix Exp $
 */

public class Seg {
	short wAngleZ;
	short wAngleXChase;
	int nPosX;
	int nPosZ;
	int nPosY;
	int wCCLineRAngle;
	int wCCLine;
	int wAngleZChangeMulHalfPI;
	short bPosFine;	// 3 bits for x in low nibble, 3 bits for y in high nibble NOT USED HERE!

        public int getPosX() { return nPosX; };    // for hiding details of x/y pos storage (bPosFine)
        public int getPosY() { return nPosY; };
        public int getPosZ() { return nPosZ; };

        /**
            Unlike F1GP internal storage, X and Y position are directly stored as 32 bit int.
            bPosFine is not used as long as it is not definitely needed (KS 13.03.07)
        */
        public void setPos( int nNewPosX, int nNewPosY, int nNewPosZ )
        {
            nPosX = nNewPosX;
            nPosY = nNewPosY;
            nPosZ = nNewPosZ;
        };

        public int getTrackWidth()
        { return 512; } // @@@ not set yet

        public int getAngleZ()
        { return wAngleZ; };

        public int getAngleXChase()
        { return wAngleXChase; }

        public int getCCLine()
        { return wCCLine; };

        public int getCCLineRAngle()
        { return wCCLineRAngle; }
}
