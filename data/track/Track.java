/*
 * Track.java
 *
 * Created on 9. Februar 2005, 23:12
 */

package chequeredflag.data.track;

import java.io.*;
import java.nio.channels.*;
import java.awt.*;

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
            // Always read in one additional segment, like TrackSegments.
            // Pitlane does not use this segment to carry commands, so drop it.
            m_PitlaneSegments.remove(m_PitlaneSegments.size() - 1);

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
        // do all necessary calculations
        calculateTrackLayout();
        calculateCCLine();
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

    /** Methods to get several parts of the track object */
    public TrackDataHeader getTrackDataHeader()
    {
        return m_DataHeader;
    }

    public TrackSegments getTrackSegments()
    {
        return m_TrackSegments;
    }

    public TrackSegments getPitlaneSegments()
    {
        return m_PitlaneSegments;
    }

    public CCLine getCCLine()
    {
        return m_CCLine;
    }

    public CCSetup getCCSetup()
    {
        return m_CCSetup;
    }

    public TrackObjects getTrackObjects()
    {
        return m_Objects;
    }

    /** Data members */
    protected File m_File;    // File where track was loaded from

    protected byte m_baBackground[];
    protected int m_nLapNumIndex;
    protected TrackFileHeader m_FileHeader;
    protected TrackObjects m_Objects;
    protected TrackDataHeader m_DataHeader;
    protected TrackSegments m_TrackSegments;
    protected CCLine m_CCLine;
    protected CCSetup m_CCSetup;
    protected TrackSegments m_PitlaneSegments;
    protected Footer m_Footer;

    /** statics */
    // factor for converting width units in meters - probably fractions of feet
    final static double s_dWIDTHSCALE = 0.0047625;

    public void calculateTrackLayout()
    {
        // Layout of the track segments.
        m_TrackSegments.calculateTrackLayout(
            m_DataHeader.getStartWidth(),
            m_DataHeader.getStartAngle(),
            0.0, 0.0 // start coordinates
            );

        // Layout of the pit lane.
        // List of track segments is used to find
        // position and direction of pit entry.
        m_PitlaneSegments.calculatePitlaneLayout(
            m_TrackSegments, m_DataHeader.getPitSide()
            );
    };

    // Calculate coordinates for start/end/center points of CCLine segments.
    public void calculateCCLine()
    {
        // To calculate the CCline, both CCline data itself and
        // track segment data is needed.
        // @@@ not implemented yet
    }

    // Returns a rectangle that contains the whole track graphics.
    // x/y coordinates represent the minimum coordinates and heigth/width
    // give the size of the rectangle needed to contain the track.
    public Rectangle getBoundingRectangle()
    {
        double dMinX, dMinY, dMaxX, dMaxY, dX, dY;
        TrackSegments trackSegments = getTrackSegments();
        TrackSegment trackSegment;
        // Initialize coordinates with first point
        trackSegment = trackSegments.getAt( 1 );
        dMinX = trackSegment.getPosXStart();
        dMaxX = dMinX;
        dMinY = trackSegment.getPosYStart();
        dMaxY = dMinY;
        // search for min/max values
        for ( int i = 1; i <= trackSegments.size(); i++ )
        {
            trackSegment = trackSegments.getAt( i );
            dX = trackSegment.getPosXStart();
            if ( dX > dMaxX )
                dMaxX = dX;
            if ( dX < dMinX )
                dMinX = dX;
            dY = trackSegment.getPosYStart();
            if ( dY > dMaxY )
                dMaxY = dY;
            if ( dY < dMinY )
                dMinY = dY;
            dX = trackSegment.getPosXEnd();
            if ( dX > dMaxX )
                dMaxX = dX;
            if ( dX < dMinX )
                dMinX = dX;
            dY = trackSegment.getPosYEnd();
            if ( dY > dMaxY )
                dMaxY = dY;
            if ( dY < dMinY )
                dMinY = dY;
        } // for
        // Put data into rectangle
        Rectangle r = new Rectangle( new Double(dMinX).intValue(),
                                     new Double(dMinY).intValue(),
                                     new Double(dMaxX - dMinX).intValue(),
                                     new Double(dMaxY - dMinY).intValue() );
        return r;
    }
}
