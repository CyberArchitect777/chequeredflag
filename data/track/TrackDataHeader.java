/*
 * TrackDataHeader.java
 *
 * Created on 16. Februar 2005, 00:12
 */

package chequeredflag.data.track;

import java.io.*;
import chequeredflag.data.*;

/**
 *
 * @author Klaus
 */
public class TrackDataHeader extends CFDataObject {

    /** Creates a new instance of TrackDataHeader */
    public TrackDataHeader() {
    }

    public void load( FileInputStream fis )
    {
        try {
            m_nStartAngle       = fis.read() + fis.read() * 256;
            m_nStartHeight      = fis.read() + fis.read() * 256;
            m_nStartY           = fis.read() + fis.read() * 256;
            m_nStartZ           = fis.read() + fis.read() * 256;
            m_nStartX           = fis.read() + fis.read() * 256;
            m_nStartWidth       = fis.read() + fis.read() * 256;
            m_nPoleWidth        = fis.read() + fis.read() * 256;
            m_nPitSide          = fis.read();
            m_nTrSurround       = fis.read();
            m_nFenceDistR       = fis.read();
            m_nFenceDistL       = fis.read();
            m_nKerbCNum         = fis.read() + fis.read() * 256;
            m_nUnk1             = fis.read() + fis.read() * 256;
            m_nKerbTopColor     = fis.read() + fis.read() * 256;
            m_nUnk2             = fis.read() + fis.read() * 256;
            m_nKerbBottomColor  = fis.read() + fis.read() * 256;
            if ( m_nKerbCNum == 4 )
            {
                // read two more colours
                m_nKerbTopColor2    = fis.read() + fis.read() * 256;
                m_nKerbBottomColor2 = fis.read() + fis.read() * 256;
            }
            else
            {
                m_nKerbTopColor2    = m_nKerbTopColor;
                m_nKerbBottomColor2 = m_nKerbBottomColor;
            };

            // other values calculated from those read from file
            m_dWidth = m_nStartWidth * 2 * Track.s_dWIDTHSCALE;
        }
        catch( IOException ioe )
        {
        }
    }

    public int save( FileOutputStream fos ) throws IOException
    {
        // double-byte values
        write( fos, m_nStartAngle );
        write( fos, m_nStartHeight );
        write( fos, m_nStartY );
        write( fos, m_nStartZ );
        write( fos, m_nStartX );
        write( fos, m_nStartWidth );
        write( fos, m_nPoleWidth );
        // single-byte values
        fos.write( m_nPitSide );
        fos.write( m_nTrSurround );
        fos.write( m_nFenceDistR );
        fos.write( m_nFenceDistL );
        // double-byte values, again
        write( fos, m_nKerbCNum );
        write( fos, m_nUnk1 );
        write( fos, m_nKerbTopColor );
        write( fos, m_nUnk2 );
        write( fos, m_nKerbBottomColor );
        // 28 Bytes written so far
        int nResult = 28;
        if ( m_nKerbCNum == 4 )
        {
            // write two more colours
            write( fos, m_nKerbTopColor2 );
            write( fos, m_nKerbBottomColor2 );
            nResult += 4; // 4 more bytes written
        }
        return nResult;
    }

    protected int m_nStartAngle;
    protected int m_nStartHeight;
    protected int m_nStartX, m_nStartY, m_nStartZ;
    protected int m_nStartWidth;
    protected int m_nPoleWidth;
    protected int m_nPitSide;
    protected int m_nTrSurround;
    protected int m_nFenceDistR, m_nFenceDistL;
    protected int m_nKerbCNum;
    protected int m_nUnk1, m_nUnk2;
    protected int m_nKerbTopColor, m_nKerbBottomColor;
    protected int m_nKerbTopColor2, m_nKerbBottomColor2;

    // calculated values
    protected double m_dWidth;

    public int getStartWidth() {
        return m_nStartWidth;
    }

    public int getStartAngle() {
        return m_nStartAngle;
    }
}
