/*
 * TrackSegment.java
 *
 * Created on 16. Februar 2005, 22:56
 */

package chequeredflag.data.track;

import java.io.*;
import java.util.*;
import chequeredflag.data.*;

/**
 *
 * @author Klaus
 */
public class TrackSegment extends CFDataObject {

    /** Creates a new instance of TrackSegment */
    public TrackSegment() {
        m_nSign = 0;
        m_Commands = new Vector();
    }

    public void load( FileInputStream fis )
    {
        // Read a track segment from track file.
        // Structure in file: 0..n commands, followed by track segment layout data.
        try
        {
            // determine type of next object
            int nTlu = fis.read();
            int nType = fis.read();
            while ( ( nType != 0 ) && ( nType != 0xFF ) )
            {
                // This is a command. Read in and store into list
                Command cmd = new Command(nType, nTlu);
                cmd.load(fis);
                m_Commands.add( cmd );
                // determine type of next object
                nTlu = fis.read();
                nType = fis.read();
            }
            // All commands read. Now fill in track layout data.
            m_nType = nType;
            if ( nType == 0 )
            {
                // normal track segment
                m_nTlu          = nTlu;
                m_nCurvature    = fis.read() + fis.read() * 256;
                m_nHeightChange = fis.read() + fis.read() * 256;
                m_nFlags        = fis.read() + fis.read() * 256;
                m_nFenceDistR   = fis.read();
                m_nFenceDistL   = fis.read();
            }
            else if ( nType == 0xFF )
            {
                // End of list reached
                // There is nothing more to read from the file.
                // Nonetheless this segment may carry command information that
                // that was already gathered above.
                m_nSign = 4; // this indicates the "empty" track segment
            }
        }
        catch( IOException ioe )
        {
        }
    }

    public int getSign()
    {
        return m_nSign;
    }

    public int save( FileOutputStream fos ) throws IOException
    {
        int nWritten = 0;
        // first, save the command list.
        for ( Enumeration e = m_Commands.elements(); e.hasMoreElements(); )
            nWritten += ((Command) e.nextElement()).save(fos);
        // save own properties
        if (m_nSign != 4)
        {
            // Standard track segment
            fos.write( m_nTlu );
            fos.write( m_nType );
            if ( m_nSign < 0 )
                write( fos, 0 - m_nCurvature ); // store negative value
            else
                write( fos, m_nCurvature );
            write( fos, m_nHeightChange );
            write( fos, m_nFlags );
            // FenceDistR/L only one byte
            fos.write( m_nFenceDistR );
            fos.write( m_nFenceDistL );
        }

        return nWritten;
    }

    // instance data members
    protected int m_nType;      // segment type
    protected int m_nSign;      // derived from Curvature, also used as flag
    protected int m_nTlu;       // length in Track Length Units
    protected int m_nCurvature; // higher values make sharper curves -> 1/r
    protected int m_nHeightChange; // Curvature in Z direction
    protected int m_nFlags;     // bit coded attributes like kerbs, fences etc.
    protected int m_nFenceDistL, m_nFenceDistR; // distance between track and fence
    protected Vector m_Commands;    // List of command objects associated with track segment
}
