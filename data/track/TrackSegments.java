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
    public void calculateTrackLayout(int nStartWidth, int nStartAngle, double dPosX, double dPosY) {
        int nWidthLength, nWidthEnd;
        nWidthLength = 0;
        nWidthEnd = 0;
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
                           * ((tsPitlaneEntry.getWidthStart() - nPITWIDTH/2) * Track.s_dWIDTHSCALE);
	    dPitStartY = tsPitlaneEntry.getPosYStart()
                         - Math.sin(tsPitlaneEntry.getAngleStart())
                           * ((tsPitlaneEntry.getWidthStart() - nPITWIDTH/2) * Track.s_dWIDTHSCALE);
        }
	else
	{
            /*pits on the right*/
            dPitStartX = tsPitlaneEntry.getPosXStart()
                         + Math.cos(tsPitlaneEntry.getAngleStart())
                           * ((tsPitlaneEntry.getWidthStart() - nPITWIDTH/2) * Track.s_dWIDTHSCALE);
            dPitStartY = tsPitlaneEntry.getPosYStart()
                         + Math.sin(tsPitlaneEntry.getAngleStart())
                           * ((tsPitlaneEntry.getWidthStart() - nPITWIDTH/2) * Track.s_dWIDTHSCALE);
	}
        // Calculate pit lane
        calculateTrackLayout(nPITWIDTH,
                             tsPitlaneEntry.getAngleStart(),
                             dPitStartX,
                             dPitStartY);
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
    
}
