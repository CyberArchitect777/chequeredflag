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


/**
 *
 * @author Klaus
 */
public class TrackSegments extends Vector {

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
        // until a track segment with sign == 4 is read in.
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

    /**
      Calculate all coordinates and angles of segments
    */
    public void calculateTrackLayout(int nStartWidth, int nStartAngle, int nPosX, int nPosY) {
        int nWidthLength, nWidthEnd, nSegNumber;
        double dANGLE_SCALE;
        nWidthLength = 0;
        nWidthEnd = 0;
        nSegNumber = 0; // for m_segs array access
        dANGLE_SCALE = Math.PI * 2.0 / 65535.0;
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
}
