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
        } while (ts.getSign() != 4);
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
        int nPosX, nPosY;
        int nWidthLength, nWidthEnd;
        nPosX = 0;
        nPosY = 0;
        nWidthLength = 0;
        nWidthEnd = 0;
        for ( Enumeration e = elements(); e.hasMoreElements(); )
        {
            // get the next element
            TrackSegment ts = (TrackSegment) e.nextElement();
            // calculate its layout
            ts.calculateLayout(
                nPosX, nPosY,
                nStartWidth, nStartAngle,
                nWidthLength,   // remaining length for change of track width
                nWidthEnd       // track width to be reached
                );
            // get starting data for next segment
            // Changed by Barrie due to compile error. Original code shown below tried to pipe a double into an integer value.
            // Just a warning under C++, but unfortunately an error under Java :)
            
            // Original code
            //nPosX = ts.getPosXEnd();
            //nPosY = ts.getPosYEnd();
            
            // New code
            
            nPosX = new Double(ts.getPosXEnd()).intValue();
            nPosY = new Double(ts.getPosYEnd()).intValue();
            
            // End of changes
            
            nStartWidth = ts.getWidthEnd();
            nStartAngle = ts.getAngleEnd();
            nWidthLength = ts.getWidthChangeLength();
            nWidthEnd = ts.getWidthChangeEnd();
        }
    }
}
