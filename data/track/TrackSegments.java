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
    public void calculateTrackLayout(int nStartWidth, int nStartAngle) {
        double dPosX, dPosY;
        int nWidthLength, nWidthEnd;
        dPosX = 0.0;
        dPosY = 0.0;
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

    /** gets the track segment at position i in the vector (1-based) */
    public TrackSegment getAt(int i)
    {
        if ( ( i > elementCount ) || ( i < 1 ) )
            return null;
        else
            return (TrackSegment) elementAt( i - 1 );
    }
    
}
