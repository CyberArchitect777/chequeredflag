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
 * TrackSegments.java
 *
 * Created on 16. Februar 2005, 22:26
 */

package chequeredflag.data.track;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

import chequeredflag.f1gp.*;

/**
 *
 * @author Klaus
 */
public class TrackSegments extends Vector {
    final int const2PI_mul_4k = 25736; // 2 * PI * 4k

    /** Creates a new instance of TrackSegments */
    public TrackSegments() {
        // initialise segment array
        m_segs = new Seg[ 2000 ]; // In-game limit is 1420 Segs.
                                  // Provide some more to allow track modifications
                                  // that exceed the limit temporarily.
        // Also initialise the segments.
        for ( int i = 0; i < m_segs.length; i++ )
            m_segs[ i ] = new Seg();
    }

    public void load( FileInputStream fis )
    {
        // Read track segments and store them in the vector,
        // until a track segment with type == FF is read in.
        // This marks the end of the list.
        TrackSegment ts;
        do
        {
            ts = new TrackSegment();
            ts.load( fis );
            add(ts);
        } while (ts.getType() != 0xFF);
    }

    public int save( FileOutputStream fos ) throws IOException
    {
        int nWritten = 0;
        for ( Enumeration e = elements(); e.hasMoreElements(); )
            nWritten += ((TrackSegment) e.nextElement()).save(fos);
        try
        {
            fos.write( 0xFF );  // write end-of-track-segments indication
            fos.write( 0xFF );
            nWritten += 2;
        }
        catch( IOException ioe )
        {
        }

        return nWritten;
    }

    // currently not needed methods
    protected void InitFences() {}
    protected void TCClearBuffers() {}
    protected void TCLoadKerbColours(int nKerbColours) {}

    // Process track sectors and convert data into Segs.
    // Called twice when processing a track. For the second pass, the second
    // parameter is true.
    protected int ProcessTrackSegments( int nSegStart, boolean bTrackCompilePass2 )
    {
        int nSegsProcessed = 0;
        for ( Enumeration e = elements(); e.hasMoreElements(); )
        {
            // get the next element
            TrackSegment ts = (TrackSegment) e.nextElement();
            // Process commands
            ProcessTrackCommands( nSegStart, bTrackCompilePass2 );
            // Process layout
            nSegsProcessed = ProcessTrackLayout( ts, nSegStart, bTrackCompilePass2 );
            nSegStart = nSegStart + nSegsProcessed;
        }
        
        // return total number of processed Segs
        return nSegStart;
    };

    protected void ProcessTrackCommands( int nSegStart, boolean bTrackCompilePass2 )
    {
        // not needed for layout processing?
    };

    protected int ProcessTrackLayout( TrackSegment ts, int nSegStart, boolean bTrackCompilePass2 )
    {
        int nSegsProcessed;
        if ( bTrackCompilePass2 )
            nSegsProcessed = ProcessTrackLayoutPass2( ts, nSegStart );
        else
        {
            // Pass 1
            int nTCSectorArg_Flags;
            nTCSectorArg_Flags = ts.m_nFlags;
            nTCSectorArg_Flags |= 0xC300; // set unk flags.
            nTCSectorArg_Flags ^= 0x3000; // toggle left/right wall remove bits
            nTCSectorArg_Flags &= 0xFF37; // clear road signs
            // save modified flags of previous Seg
            int nTCSectorModifiedFlags_Save = nTCSectorModifiedFlags;
            // store new modified flags
            nTCSectorModifiedFlags = nTCSectorArg_Flags;
            // combine with Flags from previous seg
            nTCSectorArg_Flags &= nTCSectorModifiedFlags_Save;
            nTCSectorArg_Flags &= 0x0C00; // mask l/r kerb bits
            nTCSectorArgFlagsKerbsContinued = 0;
            if ( nTCSectorArg_Flags != 0 )
            {
                // Some kerbs on both Segs: kerbtype changed?
                if ( (( nTCSectorModifiedFlags_Save ^ nTCSectorModifiedFlags) & 0x0004 ) == 0 )
                {
                    // same kerbs on both Segs: store bits
                    nTCSectorArgFlagsKerbsContinued = nTCSectorArg_Flags;
                }           
            }
            // Bridged wall continued?
            bTCSectorArgFlagsBridgedWallCntd = nTCSectorModifiedFlags & nTCSectorModifiedFlags_Save & 0x0030;

            nSegsProcessed = TCProcessTrackSectorPass1( ts, nSegStart );
        }
        return nSegsProcessed;
    };

    protected void TCCalcVergeWidth() {};
    protected void TCSelectiveClearBufW1E920() {};

    // In-game calculations use 19 bits for X and Y coordinates.
    // Original function stores lower 3 bits in field bFineOffset.
    // We try to use 32 bits so this function is quite simple.
    
    protected void TCInitSegPos( Seg seg )
    {
        seg.setPos( dTCAbsPosX, dTCAbsPosY, dTCAbsPosZ );
    }

    protected void TCInitSeg( int nSegIndex )
    {
        Seg seg = m_segs[ nSegIndex ];
        seg.wAngleZ = (short) wTCAbsAngleZ_2;
        seg.wAngleXChase = (short) wTCAbsAngleX;

        TCInitSegPos( seg );

        seg.wCCLine = 0;
        seg.wCCLineRAngle = 0;
    };

    /**
        Store angle change to Seg nSeg, multiplied with PI/2.
        Segment number and angle of following segment provided by parameters.
    */

    protected void TCWriteAngleZChangeMulHalfPI(int nSeg, int nAngleZ)
    {
        if ( nSeg < 0 )
            return;

        Seg seg = m_segs[ nSeg ];
        int nAngleChange = (short)((short) nAngleZ - seg.wAngleZ);
        // shift right by 14 bits = division by 16k
        int nAngleChangeMulHalfPi = (nAngleChange * const2PI_mul_4k) >> 14;
        seg.wAngleZChangeMulHalfPI = nAngleChangeMulHalfPi;
    }

    // returns number of Segs belonging to this TrackSegment.
    protected int TCProcessTrackSectorPass1( TrackSegment ts, int nSegStart )
    {
        TCCalcVergeWidth();
        TCSelectiveClearBufW1E920();
        short wTCOldAbsAngleZ = 0;
        boolean fFirstLoop = true;
        for ( int i = nSegStart; i < nSegStart + ts.m_nTlu; i++ )
        {
            // Calculate Angles
            if ( fFirstLoop )
            {
                if ( fTC0xa5 )
                {
                    // Command A5 is present on this sector
                    wTCAbsAngleZ_2 = (short) (wTCAbsAngleZ_2 - ts.m_nCurvature / 2);
                    wTCAbsAngleX = (short) (wTCAbsAngleX - ts.m_nHeightChange / 2);
                    fTC0xa5 = false;
                }
                else
                {
                    // Shift AngleZ values: AngleZ_2 -> AngleZ -> OldAngleZ
                    wTCOldAbsAngleZ = wTCAbsAngleZ;
                    wTCAbsAngleZ = wTCAbsAngleZ_2;
                    // modify angles
                    wTCAbsAngleZ_2 = (short) (wTCAbsAngleZ_2 + ts.m_nCurvature / 2);
                    wTCAbsAngleX = (short) (wTCAbsAngleX + ts.m_nHeightChange / 2);
                }
                fFirstLoop = false;
            }
            else
            {
                wTCAbsAngleZ_2 += ts.m_nCurvature;
                wTCOldAbsAngleZ = wTCAbsAngleZ;
                wTCAbsAngleZ += ts.m_nCurvature;
                wTCAbsAngleX += ts.m_nHeightChange;
            }
       
            // TCPrepareSegFlags_91034();
            // TCCalcOffsetsByTrk_Width();
            TCInitSeg(i);
            TCWriteAngleZChangeMulHalfPI(i - 1, wTCAbsAngleZ);

            // -----------------------------------------------------------
            // calculate positions
            int nPosChangeX, nPosChangeY, nPosChangeZ;
            nPosChangeX = F1GPMath.LookupSin( wTCAbsAngleZ_2 );
            nPosChangeX = (nPosChangeX * 1024) / 0x4000; // convert to 1/1024 tlu

            nPosChangeY = F1GPMath.LookupCos( wTCAbsAngleZ_2 );
            nPosChangeY = (nPosChangeY * 1024) / 0x4000;

            nPosChangeZ = F1GPMath.LookupSin( wTCAbsAngleX );
            nPosChangeZ = (nPosChangeZ * 1024) / 0x4000;
            
            // update absolute positions
            dTCAbsPosX = dTCAbsPosX + nPosChangeX;
            dTCAbsPosY = dTCAbsPosY + nPosChangeY;
            dTCAbsPosZ = dTCAbsPosZ + nPosChangeZ;

            // TCIncrCountersCalcVergeTrackWidth();
        }

        // Calculate angles at end of sector
        wTCAbsAngleZ_2 += ts.m_nCurvature / 2;
        wTCAbsAngleX += ts.m_nHeightChange / 2;
        wTCOldVergeWidth = (short) ts.m_nFenceDistL; // DistR ??? @@@

        return ts.m_nTlu;
    }

    // returns number of Segs belonging to this TrackSegment.
    protected int ProcessTrackLayoutPass2( TrackSegment ts, int nSegStart )
    {
        // @@@@ missing
        return ts.m_nTlu;
    };

    // Create Segs objects like in-game calculations
    protected int TCCreateSegments()
    {
        // Process segments, starting at first Seg, first pass
        nTrackSegs = ProcessTrackSegments( 0, false );
        // second pass
        ProcessTrackSegments( 0, true );

        return nTrackSegs;
    };

    /*
        Calculate difference of angle and position between first and last segment.
        Angle differences are stored in wTCAbsAngleZ_2 and wTCAbsAngleX.
        Position differences are stored in dTCAbsPosX/Y/Z.
    */
    protected void TCCalcPosAngleDifference(int nIndexLastSeg)
    {
        wTCAbsAngleZ_2 = (short) (m_segs[ 0 ].wAngleZ - m_segs[ nIndexLastSeg ].wAngleZ);
        wTCAbsAngleX = (short) (m_segs[ 0 ].wAngleXChase - m_segs[ nIndexLastSeg ].wAngleXChase);
        dTCAbsPosX = m_segs[ 0 ].getPosX();
        dTCAbsPosY = m_segs[ 0 ].getPosY();
        dTCAbsPosZ = m_segs[ 0 ].getPosZ();
        if ( bCreatingPitlaneSegments )
        {
            // Add difference between pit entry and exit
            dTCAbsPosX += dTCOffsetPitLanePosX;
            dTCAbsPosY += dTCOffsetPitLanePosY;
            dTCAbsPosZ += dTCOffsetPitLanePosZ;
        }
        dTCAbsPosX = dTCAbsPosX - m_segs[ nIndexLastSeg ].getPosX();
        dTCAbsPosY = dTCAbsPosY - m_segs[ nIndexLastSeg ].getPosY();
        dTCAbsPosZ = dTCAbsPosZ - m_segs[ nIndexLastSeg ].getPosZ();
    };
    

    /**
        Moves all Segs so that the difference between start and end of track is
        distributed evenly among the Segs.
    */
    protected void TCRecalcPosToFit(int nLastSeg, int nDiffX, int nDiffY, int nDiffZ )
    {
        int nAbsPosX, nAbsPosY, nAbsPosZ;   // absolute positions
        int nRestX, nRestY, nRestZ;         // difference transferred to next Seg
        int nOffsetX, nOffsetY, nOffsetZ;   // cumulated movement
        int nNumSegs;

        nRestX = 0;
        nRestY = 0;
        nRestZ = 0;
        nOffsetX = 0;
        nOffsetY = 0;
        nOffsetZ = 0;

        // Number of segments (0..nLastSeg)
        nNumSegs = nLastSeg + 1;

        for ( int i = 0; i <= nLastSeg; i++ )
        {
            // Calculate new offset values
            nRestX = nRestX + nDiffX;
            nOffsetX = nOffsetX + nRestX / nNumSegs;
            nRestX = nRestX % nNumSegs;

            nRestY = nRestY + nDiffY;
            nOffsetY = nOffsetY + nRestY / nNumSegs;
            nRestY = nRestY % nNumSegs;

            nRestZ = nRestZ + nDiffZ;
            nOffsetZ = nOffsetZ + nRestZ / nNumSegs;
            nRestZ = nRestZ % nNumSegs;
            
            // Move segment
            Seg seg = m_segs[ i ];
            nAbsPosX = seg.getPosX();
            nAbsPosY = seg.getPosY();
            nAbsPosZ = seg.getPosZ();
            seg.setPos( nAbsPosX + nOffsetX, nAbsPosY + nOffsetY, nAbsPosZ + nOffsetZ );
        }
    };

    protected void TCInitData(int nStartWidth, int nStartAngle, int nPosX, int nPosY)
    {
        nSegNumber = 0; // for m_segs array access
        wTCSectorArgModifiedFlags = 0;
        bTC0x9a = 0x5A; // pitlane viewing distance?
        wKerbsLeftBegin = 1;
        wKerbsLeftLength = 16;
        wKerbsRightBegin = 1;
        wKerbsRightLength = 16;
        wTrk_CCCoachingLeft = 16;
        wTrk_CCCoachingRight = 16;
        word_1ECE4 = 16;  // up to now unknown purpose...
        word_1ECEC = 16;
        word_1ED10 = 16;
        word_1ED18 = 16;

        InitFences();
        
        fTC0xa5 = false;
        wTC0xaa_arg1 = 0x17;
        wTC0xaa_arg2 = 0x7;
        wTC0xaa_arg3 = 0x1A00;
        nSeg_1E77E = -1;
        wTCAbsAngleZ_2 = (short) nStartAngle;
        wTCAbsAngleX = 0; // @@@ X-Angle from header
        dTCAbsPosX = nPosX;
        dTCAbsPosZ = 0; // @@@ get from header
        dTCAbsPosY = nPosY;
        wTrk_Width = (short) nStartWidth;
        wTrk_WidthPlus0x50 = (short) (wTrk_Width + 0x50);

        TCClearBuffers();
        
        wPoleWidth = 100; // @@@ get from header
        wTCTrackDataFlags = 0; //  @@@ get from header
        wTCOldVergeWidth = 0; //  @@@ get from header
        wTCNumKerbColours = 0; //  @@@ get from header

        TCLoadKerbColours(wTCNumKerbColours);

        bCreatingPitlaneSegments = false;
        bDefaultTextureFlagsPlus1 = 3;
        byte_1E8AF = (byte) 0xaa;
        byte_1E8AE = 0x3d;
        bDefaultKerbColourIndex = (byte) 0x95;
        byte_1E8AC = (byte) 0x88;
        wTCAbsAngleZ = wTCAbsAngleZ_2;

        nTCSectorModifiedFlags = 0;
        nTCSectorArgFlagsKerbsContinued = 0;
        bTCSectorArgFlagsBridgedWallCntd = 0;
    }

    protected void TCCompileTrack(int nStartWidth, int nStartAngle, int nPosX, int nPosY)
    {
        TCInitData(nStartWidth, nStartAngle, nPosX, nPosY);

        nSegNumber = TCCreateSegments();
        int wMaxTrackSegIndex = nSegNumber - 1;
        int wMaxTrackSegIndexDiv32 = wMaxTrackSegIndex / 32;
        if ( wMaxTrackSegIndexDiv32 > 50 )
        {
          // Error: too many segments in track @@@
        }
        // store "pointers" to last segment
        int nLastTrackSeg = wMaxTrackSegIndex;
        int nSegTmp2 = wMaxTrackSegIndex;
        int nLastSegTrackOrPitLane = wMaxTrackSegIndex;
        int nFirstSegTrackOrPitLane = 0;
        // one more "pointer" to second but last
        int nSecondButLastSeg = wMaxTrackSegIndex - 1;

        int nSegTmp1 = 0;

        // Calculate difference of angle and position between first and last seg
        TCCalcPosAngleDifference( nLastTrackSeg );
        // Adjust positions
        TCRecalcPosToFit( nLastTrackSeg, dTCAbsPosX, dTCAbsPosY, dTCAbsPosZ );
        // Calculate difference once again (for now adjusted track)
        TCCalcPosAngleDifference( nLastTrackSeg );
    }

    /**
      Calculate all coordinates and angles of segments
    */
    public void calculateTrackLayout(int nStartWidth, int nStartAngle, int nPosX, int nPosY) {
        int nWidthLength, nWidthEnd;
        double dANGLE_SCALE;
        nWidthLength = 0;
        nWidthEnd = 0;
        dANGLE_SCALE = Math.PI * 2.0 / 65535.0;
 
        // convert pos to double for "old" calculations
        double dPosX, dPosY;
        dPosX = nPosX;
        dPosY = nPosY;

        for ( Enumeration e = elements(); e.hasMoreElements(); )
        {
            // get the next element
            TrackSegment ts = (TrackSegment) e.nextElement();
            // calculate its layout
            ts.calculateLayout(
                dPosX, dPosY,
                nStartWidth, nStartAngle,
                nWidthLength,   // remaining length for change of track width
                nWidthEnd       // track width to be reached
                );
            // get starting data for next segment
            dPosX = ts.getPosXEnd();
            dPosY = ts.getPosYEnd();
            nStartWidth = ts.getWidthEnd();
            nStartAngle = ts.getAngleEnd();
            nWidthLength = ts.getWidthChangeLength();
            nWidthEnd = ts.getWidthChangeEnd();
        }

        // ----------------------------------------------------------
        // Calculations following in-game calculations
        TCCompileTrack(nStartWidth, nStartAngle, nPosX, nPosY);
    }

    /**
      Find position of pit lane entry in trackSegments list.
      Then, calculate other position data based on that.
      fPitSide is true if pits are on the left side of the track,
      and false if on right side.
    */
    public void calculatePitlaneLayout(TrackSegments trackSegments, boolean fPitSide)
    {
        // Find track segment that contains pit lane entry.
        TrackSegment tsPitlaneEntry = trackSegments.findPitlaneEntry();
        if ( tsPitlaneEntry == null )
            return;
        // Pit lane width in width scales (defined by F1GP)
        int nPITWIDTH=1344;
        double dPitStartX, dPitStartY;
        if ( fPitSide )
        {
            /*pits on the left*/
	    dPitStartX = tsPitlaneEntry.getPosXStart()
                         - Math.cos(tsPitlaneEntry.getAngleStart())
                           * ((double)((tsPitlaneEntry.getWidthStart() - nPITWIDTH/2)) / 1024.0);
	    dPitStartY = tsPitlaneEntry.getPosYStart()
                         - Math.sin(tsPitlaneEntry.getAngleStart())
                           * ((double)((tsPitlaneEntry.getWidthStart() - nPITWIDTH/2)) / 1024.0);
        }
	else
	{
            /*pits on the right*/
            dPitStartX = tsPitlaneEntry.getPosXStart()
                         + Math.cos(tsPitlaneEntry.getAngleStart())
                           * ((double)((tsPitlaneEntry.getWidthStart() - nPITWIDTH/2)) / 1024.0);
            dPitStartY = tsPitlaneEntry.getPosYStart()
                         + Math.sin(tsPitlaneEntry.getAngleStart())
                           * ((double)((tsPitlaneEntry.getWidthStart() - nPITWIDTH/2)) / 1024.0);
	}
        // Calculate pit lane
        // convert position to int
        int nPitStartX = new Double( dPitStartX ).intValue();
        int nPitStartY = new Double( dPitStartY ).intValue();
        calculateTrackLayout(nPITWIDTH,
                             tsPitlaneEntry.getAngleStart(),
                             nPitStartX,
                             nPitStartY);
    }

    /**
      Find track segments that contains the pit lane entry, if any.
      Returns found track segment, else null.
    */
    public TrackSegment findPitlaneEntry()
    {
        for ( Enumeration e = elements(); e.hasMoreElements(); )
        {
            // get the next element
            TrackSegment ts = (TrackSegment) e.nextElement();
            // check if it contains pit lane entry
            if ( ts.findCommand(0x86) != null )
                // found
                return ts;
        }
        return null;
    }

    /** gets the Seg object at position i (0-based) */
    public Seg getSegAt( int i )
    {
        if ( i <= nSegNumber )
            return m_segs[ i ];
        return null;
    }

    public int getMaxTrackSegIndex()
    {
        return nSegNumber - 1;
    }

    /** gets the track segment at position i in the vector (1-based) */
    public TrackSegment getAt(int i)
    {
        if ( ( i > elementCount ) || ( i < 1 ) )
            return null;
        else
            return (TrackSegment) elementAt( i - 1 );
    }

    /** insert segment at given position (1-based).
        returns the newly inserted segment. */
    public TrackSegment insertAt(int i)
    {
        TrackSegment newSeg;
        newSeg = new TrackSegment();
        // initialise with length = 1 so the segment will be visible
        newSeg.m_nTlu = 1;
        if ( i > elementCount )
            add( newSeg );
        else
        {
            try {
                add( i - 1, newSeg );
            }
            catch( ArrayIndexOutOfBoundsException e )
            {
                newSeg = null;
            }
        }
        return newSeg;
    }
    
    /** delete segment at given position (1-based) */
    public void deleteAt(int i)
    {
        try {
            remove( i - 1 );
        }
        catch( ArrayIndexOutOfBoundsException e )
        {
        }
    }

    // data members
    Seg m_segs[]; // track segments as used by in-game calculations

    // Variables for in-game calculations
    int nSegNumber = 0; // for m_segs array access
    short wTCSectorArgModifiedFlags = 0;
    byte bTC0x9a = 0x5A; // pitlane viewing distance?
    short wKerbsLeftBegin = 1;
    short wKerbsLeftLength = 16;
    short wKerbsRightBegin = 1;
    short wKerbsRightLength = 16;
    short wTrk_CCCoachingLeft = 16;
    short wTrk_CCCoachingRight = 16;
    short word_1ECE4 = 16;  // up to now unknown purpose...
    short word_1ECEC = 16;
    short word_1ED10 = 16;
    short word_1ED18 = 16;
    boolean fTC0xa5 = false;
    short wTC0xaa_arg1 = 0x17;
    short wTC0xaa_arg2 = 0x7;
    short wTC0xaa_arg3 = 0x1A00;
    int nSeg_1E77E = -1;
    short wTCAbsAngleZ_2 = 0;
    short wTCAbsAngleX = 0;
    int dTCAbsPosX = 0;
    int dTCAbsPosZ = 0;
    int dTCAbsPosY = 0;
    int dTCOffsetPitLanePosX = 0;
    int dTCOffsetPitLanePosY = 0;
    int dTCOffsetPitLanePosZ = 0;
    short wTrk_Width = 0;
    short wTrk_WidthPlus0x50 = (short) (wTrk_Width + 0x50);
    short wPoleWidth = 100;
    short wTCTrackDataFlags = 0;
    short wTCOldVergeWidth = 0;
    short wTCNumKerbColours = 0;
    boolean bCreatingPitlaneSegments = false;
    byte bDefaultTextureFlagsPlus1 = 3;
    byte byte_1E8AF = (byte) 0xaa;
    byte byte_1E8AE = 0x3d;
    byte bDefaultKerbColourIndex = (byte) 0x95;
    byte byte_1E8AC = (byte) 0x88;
    short wTCAbsAngleZ = wTCAbsAngleZ_2;
    int nTCSectorModifiedFlags = 0;
    int nTCSectorArgFlagsKerbsContinued = 0;
    int bTCSectorArgFlagsBridgedWallCntd = 0;

    int nTrackSegs; // total number of track Segs in this track
}
