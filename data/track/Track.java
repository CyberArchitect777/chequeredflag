/*
 * Track.java
 *
 * Created on 9. Februar 2005, 23:12
 */

package chequeredflag.data.track;

import java.io.*;
import java.nio.channels.*;

/**
 *
 * @author Klaus
 */
public class Track {

    /** Creates a new instance of Track */
    public Track() {
        m_baBackground = new byte[4096];
        m_FileHeader = new TrackFileHeader();
        m_Objects = new TrackObjects();
        m_DataHeader = new TrackDataHeader();
        m_TrackSegments = new TrackSegments();
        m_CCLine = new CCLine();
        m_CCSetup = new CCSetup();
        m_PitlaneSegments = new TrackSegments();
        m_Footer = new Footer();
    }

    public void load( File file )
    {
        long lPos; // Position in file
        FileChannel fc;
        try {
            // remember file where track was loaded from.
            m_File = file;

            // Create file input stream
            FileInputStream fis = new FileInputStream( file );
            // Get channel to be able to determine file position later,
            // mainly for debugging and reference purpose.
            fc = fis.getChannel();

            // read background picture
            fis.read(m_baBackground);

            // read file header
            m_FileHeader.load(fis);

            // objects
            m_Objects.load(fis, m_FileHeader.m_nTrackDataOffset);

            // track data header: track width, starting angle etc.
            m_DataHeader.load(fis);

            // track segment data
            m_TrackSegments.load(fis);

            // CCLine data
            m_CCLine.load(fis);

            // CCSetup data
            m_CCSetup.load(fis);

            // pitlane segment data
            m_PitlaneSegments.load(fis);

            lPos = m_PitlaneSegments.size();
            lPos = fc.position();

            // footer, which means all the rest, mainly unknown data
            int nCount = m_Footer.load(fis);

            // 10 bytes before end of file is the number of laps for full race distance
            m_nLapNumIndex = nCount - 10;

            // Close input stream
            fis.close();
        }
        catch( IOException ioe )
        {
            System.err.println("Caught exception while loading track file");
        };
    }

    // Save back to file where it was loaded from
    public int save()
    {
        return save( m_File );
    }

    // Save to some, maybe new, file
    public int save( File file )
    {
        int nBytesWritten = 0;
        try
        {
            FileOutputStream fos = new FileOutputStream(file);

            // Background graphics
            fos.write(m_baBackground);
            nBytesWritten = m_baBackground.length;

            // save track file header. This will be modified later as we know the
            // correct offset values for the newly written file.
            nBytesWritten += m_FileHeader.save(fos);

            // objects
            nBytesWritten += m_Objects.save(fos);

            // track data header
            nBytesWritten += m_DataHeader.save(fos);

            // track segments
            nBytesWritten += m_TrackSegments.save(fos);

            // CCLine data
            nBytesWritten += m_CCLine.save(fos);

            // CCSetup data
            nBytesWritten += m_CCSetup.save(fos);

            // pitlane segment data
            nBytesWritten += m_PitlaneSegments.save(fos);

            // Footer, which means all the rest, mainly unknown data.
            // Prior to writing, set checksum to 0. It will be recalculated later.
            m_Footer.setChecksum( 0 );
            nBytesWritten += m_Footer.save(fos);

            // new offset to checksum
            // determine position in file
            FileChannel fc = fos.getChannel();
            long lPos = fc.position();

            // Set checksum offset
            m_FileHeader.setChecksumOffset( lPos - 4 );

            // write offset also to file
            fc.position( 4104 ); // this is a FIXED place in the file
            m_FileHeader.saveChecksumOffset( fos );

            fos.close();

            // calculate new checksum
            calculateChecksum( file );
        }
        catch(IOException ioe)
        {
        }
        
        return nBytesWritten;
    }

    protected void calculateChecksum( File file )
    {
        try
        {
            // Open file for reading and writing
            RandomAccessFile raf = new RandomAccessFile( file, "rw" );

            try
            {
                // Get file length
                long lLength = raf.length();

                // Two checksums are calculated from the bytes of the file, except
                // the last 4 bytes which will hold the checksum.
                int nChecksum1 = 0, nChecksum2 = 0;
                int nChar;
                for ( long lPos = 0; lPos < lLength - 4; lPos++ )
                {
                    nChar = raf.read();
                    // First sum is a simple sum
                    nChecksum1 += nChar;
                    // Checksum 2: add char, then left rotate by 3
                    // can not directly rotate 16 bit value...
                    nChecksum2 = ((nChecksum2 << 3) & 0x0FFF8) + ((nChecksum2 >> 13) & 0x07);
                    nChecksum2 = nChecksum2 + nChar;
                }

                // write both checksums to file
                nChecksum1 &= 0x0FFFF;
                nChecksum2 &= 0x0FFFF;
                // must be stored low byte first, so writeShort will not work.
                raf.write( nChecksum1 & 0x0FF );
                raf.write( (nChecksum1 >> 8) & 0x0FF );
                raf.write( nChecksum2 & 0x0FF );
                raf.write( (nChecksum2 >> 8) & 0x0FF );

                // close the file
                raf.close();
            }
            catch( IOException ioe )
            {
            }
        }
        catch( FileNotFoundException fnfe )
        {
        }
    }

    /** Data members */
    File m_File;    // File where track was loaded from

    byte m_baBackground[];
    int m_nLapNumIndex;
    TrackFileHeader m_FileHeader;
    TrackObjects m_Objects;
    TrackDataHeader m_DataHeader;
    TrackSegments m_TrackSegments;
    CCLine m_CCLine;
    CCSetup m_CCSetup;
    TrackSegments m_PitlaneSegments;
    Footer m_Footer;

    /** statics */
    // factor for converting width units in meters - probably fractions of feet
    final static double s_dWIDTHSCALE = 0.0047625;

    public void calculateTrackLayout() {
        m_TrackSegments.calculateTrackLayout(
            m_DataHeader.getStartWidth(),
            m_DataHeader.getStartAngle()
            );
    };
}
