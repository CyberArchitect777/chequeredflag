/*
 * Footer.java
 *
 * Created on 17. Februar 2005, 23:47
 */

package chequeredflag.data.track;

import java.io.*;

/**
 *
 * @author Klaus
 *
 * This is a simple unstructured storage of the bytes following the pit lane data.
 * Since this part is never larger than 256 bytes, a 256 byte array is used.
 */

public class Footer {

    /** Creates a new instance of Footer */
    public Footer() {
        m_baData = new byte[ 256 ];
        m_nDataSize = 0;
    }

    public int load( FileInputStream fis)
    {
        int nIndex = 0;
        try
        {
            int nNext = fis.read();
            while( nNext != -1 )
            {
                // A byte was successfully read from the file
                m_baData[ nIndex++ ] = (byte) nNext;
                // try to read next byte.
                nNext = fis.read();
            }
        }
        catch( IOException ioe )
        {
        }
        
        m_nDataSize = nIndex;
        return nIndex; //  number of read bytes
    }

    public int save( FileOutputStream fos) throws IOException
    {
        fos.write(m_baData, 0, m_nDataSize );
        return m_nDataSize;
    }

    public void setChecksum( long lChecksum )
    {
        // Checksum is stored in the last 4 bytes
        m_baData[m_nDataSize - 4] = (byte) ((lChecksum >> 24) & 0xFF);
        m_baData[m_nDataSize - 3] = (byte) ((lChecksum >> 16) & 0xFF);
        m_baData[m_nDataSize - 2] = (byte) ((lChecksum >> 8) & 0xFF);
        m_baData[m_nDataSize - 1] = (byte) (lChecksum & 0xFF);
    }

    // instance data members
    byte m_baData[];
    int m_nDataSize;
}
