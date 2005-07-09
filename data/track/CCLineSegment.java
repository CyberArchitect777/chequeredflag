/*
 * CCLineSegment.java
 *
 * Created on 17. Februar 2005, 23:51
 */

package chequeredflag.data.track;

import java.io.*;
import chequeredflag.data.*;

/**
 *
 * @author Klaus
 */
public class CCLineSegment extends CFDataObject {

    /** Creates a new instance of CCLineSegment */
    public CCLineSegment(int nType) {
        m_nType = nType;
        m_nTlu = 0;
        // create always full length of parameter array (3)
        // BARRIE: Changed 3 to 4 to account for dimensioning error
        m_nParam = new int[ 4 ];
    }

    public void load( FileInputStream fis)
    {
        try
        {
            // All CCLine segments have at least two parameters
            m_nParam[ 0 ] = fis.read() + fis.read() * 256;
            m_nParam[ 1 ] = fis.read() + fis.read() * 256;
            // Rest depends on CCLine segment type
            if ( m_nType != 0 )
            {
                // Should be a type 0x80 (first segment) or 0x40 (32bit radius) segment
                m_nParam[ 2 ] = fis.read() + fis.read() * 256;
            }
        }
        catch( IOException ioe )
        {
        }
    }

    // accessor methods for internal data
    public int getTlu()
    { return m_nTlu; }
    public void setTlu( int nTlu )
    { m_nTlu = nTlu; }

    public int getType()
    { return m_nType; }
    public void setType( int nType )
    { m_nType = nType; }

    public int getParam( int nIndex )
    { return m_nParam[ nIndex + 1 ]; }
    public void setParam( int nIndex, int nValue )
    { m_nParam[ nIndex + 1 ] = nValue; }

    // save object to disk
    public int save(FileOutputStream fos) throws IOException
    {
        int nBytesWritten = 0;
        // Write length and type
        fos.write( m_nTlu );
        fos.write( m_nType );
        nBytesWritten += 2;
        // All CCLine segments have at least two parameters
        write( fos, m_nParam[ 0 ] );
        write( fos, m_nParam[ 1 ] );
        nBytesWritten += 4;
        // Rest depends on CCLine segment type
        if ( m_nType != 0 )
        {
            // Should be a type 0x80 (first segment) or 0x40 (32bit radius) segment
            write( fos, m_nParam[ 2 ] );
            nBytesWritten += 2;
        }
        return nBytesWritten;
    }

    // instance data members
    protected int m_nType, m_nTlu;
    protected int m_nParam[];
}
