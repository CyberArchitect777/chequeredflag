/*
 * TrackFileHeader.java
 *
 * Created on 9. Februar 2005, 23:21
 */

package chequeredflag.data.track;

import java.io.*;
import chequeredflag.data.*;

/**
 *
 * @author Klaus
 */
public class TrackFileHeader extends CFDataObject {

    /** Creates a new instance of TrackFileHeader */
    public TrackFileHeader() {
    }

    public void load( FileInputStream fis )
    {
        try {
            // read seven values, 2 bytes each
            m_nSizeBase         = loadInt( fis );
            m_nUnk1             = loadInt( fis );
            m_nUnk2             = loadInt( fis );
            m_nUnk3             = loadInt( fis );
            m_nChecksumOffset   = loadInt( fis );
            m_nObjDataOffset    = loadInt( fis );
            m_nTrackDataOffset  = loadInt( fis );
        }
        catch(IOException ioe ) {}
    }

    public int save( FileOutputStream fos ) throws IOException
    {
        // write 7 2-byte values
        write( fos, m_nSizeBase );
        write( fos, m_nUnk1 );
        write( fos, m_nUnk2 );
        write( fos, m_nUnk3 );
        write( fos, m_nChecksumOffset );
        write( fos, m_nObjDataOffset );
        write( fos, m_nTrackDataOffset );
        return 14;  // 2 x 7 = 14 bytes written
    }

    public void setChecksumOffset( long nChecksumOffset )
    {
        // Offsets are stored in track file based on "SizeBase".
        // Caller must supply real byte offset in file.
        m_nChecksumOffset = (int) (nChecksumOffset - m_nSizeBase);
    }

    public int getChecksumOffset()
    {
        // Give out the value to be stored in track data file.
        // Note: this is NOT the real offset in the track file, but
        // the offset counted from the end of the static header
        // (which is m_nSizeBase bytes long).
        return m_nChecksumOffset;
    }

    public int saveChecksumOffset( FileOutputStream fos )
    {
        // Output stream is on correct position already.
        int nBytesWritten = 0;
        try
        {
            nBytesWritten = write( fos, m_nChecksumOffset );
        }
        catch( IOException ioe )
        {
        }

        return nBytesWritten;
    }

    /** Data members */
    int m_nSizeBase;
    int m_nUnk1;
    int m_nUnk2;
    int m_nUnk3;
    int m_nChecksumOffset;	// to be added to m_nSizeBase for offset in data file
    int m_nObjDataOffset;	// maybe, maybe not? see GP3 track editor
    int m_nTrackDataOffset;	// see ChecksumOffset
}
