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
 * $Id: Seg.java,v 1.3 2007/01/10 15:59:10 b-za Exp $
 */

public class Seg {
	short wAngleZ;
	short wAngleXChase;
	short wPosX;
	short wPosZ;
	short wPosY;
	int wCCLineRAngle;
	int wCCLine;
	int wAngleZChangeMulHalfPI;
	short bPosFine;	// 3 bits for x in low nibble, 3 bits for y in high nibble
}
