package chequeredflag.data.track;

/*
 * User: Rene
 * Date: 18-okt-2005
 * Time: 22:11:29
 * $Id: Seg.java,v 1.1 2006/09/18 22:12:32 ksix Exp $
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
