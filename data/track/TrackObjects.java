/*
 * TrackObjects.java
 *
 * Created on 14. Februar 2005, 23:14
 */

package chequeredflag.data.track;

import java.io.*;
import chequeredflag.data.*;

/**
 *
 * @author Klaus
 */
public class TrackObjects extends CFDataObject {

    /** Creates a new instance of TrackObjects */
    public TrackObjects() {
    }

    public void load(FileInputStream fis, int nSize)
    {
        try {
            // read number of objects
            m_nNumObj = loadInt( fis );
            // create object storage
            m_baData = new byte[ nSize ];
            // read from file
            fis.read(m_baData);
        }
        catch( IOException ioe)
        {
        }
    }

    public int save( FileOutputStream fos ) throws IOException
    {
        // write number of objects
        write( fos, m_nNumObj );
        // write objects
        fos.write( m_baData );
        // return number of written bytes
        return 2 + m_baData.length;
    }

    // data members
    protected byte m_baData[];
    protected int m_nNumObj;
}
