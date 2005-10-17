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
    // factor for converting angle units in radians.
    final static double s_dANGLE_SCALE = (2 * Math.PI) / 65536;
    // factor for converting radius units to tlu
    final static double s_dRADIUS_SCALE = 1 / 128.0;

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

    // Intersect ccLine segment with end of track segment.
    // Calculate end point and angle for ccLineSegment and store into segment.
    public void intersect( CCLineSegment ccLineSegment, double dTrackPosX, double dTrackPosY, double dTrackAngle )
    {
        double ANGLE_SCALE = (2 * Math.PI) / 65536;
        double dRadius = ccLineSegment.getRadius();
        double dCClineAngle = ccLineSegment.getAngleStart(); // * ANGLE_SCALE;

        // End of track segment is represented by point T (dTrackPosX, dTrackPosY) and
        // direction vector D (dDx, dDy). All points Q that lie on the track segment
        // end are described by Q = T + k * D with some factor k.
        // Calculate components of direction vector.
        double dDx, dDy, k;
        dDx = Math.cos( Math.PI * 2 - dTrackAngle );
        dDy = Math.sin( Math.PI * 2 - dTrackAngle );

        if ( dRadius != 0.0 )
        {
            // curved segment.
            // Let P be a point on the CCLine circle with center C and radius r.
            // This gives for the CCline circle: (P - C)^2 = r^2
            // For the track segment border: Q = T + k * D as above
            // Quadratic equation for intersection of both:
            //  P = Q  =>  (T + k * D - C)^2 = r^2
            // Calculate coefficients of equation:            
            double a, b, c;
            a = dDx * dDx + dDy * dDy;
            b = 2 * ((dTrackPosX - ccLineSegment.getPosXCenter()) * dDx
                     + (dTrackPosY - ccLineSegment.getPosYCenter()) * dDy);
            c = dTrackPosX * dTrackPosX
                - 2 * dTrackPosX * ccLineSegment.getPosXCenter()
                + ccLineSegment.getPosXCenter() * ccLineSegment.getPosXCenter()
                + dTrackPosY * dTrackPosY
                - 2 * dTrackPosY * ccLineSegment.getPosYCenter()
                + ccLineSegment.getPosYCenter() * ccLineSegment.getPosYCenter()
                - dRadius * dRadius;
            // calculate the solutions for the quadratic equation
            double dDeterminant;
            dDeterminant = b*b - 4*a*c;
            if ( dDeterminant >= 0 )
            {
                // There is at least one solution to the quadratic expression.
                // We expect ALWAYS at least one solution!
                double k1, k2;
                k1 = (Math.sqrt(dDeterminant) - b)/ (2*a);
                k2 = (-Math.sqrt(dDeterminant) - b)/ (2*a);
                // The solution with the smaller absolute value is the solution we are looking for because
                // it is nearer to the middle of the track.
                if ( Math.abs(k1) < Math.abs(k2) )
                    k = k1;
                else
                    k = k2;
                // Use this solution for calculating the ccLine end point.
                ccLineSegment.setPosXEnd( dTrackPosX + k * dDx );
                ccLineSegment.setPosYEnd( dTrackPosY + k * dDy );
            }
            else
            {
                // We should never end up here because in this case the ccline circle does not
                // intersect with the end of the track segment!
                // Anyway, if it happens, we take the track end point as ending point
                // for the ccLine Segment
                ccLineSegment.setPosXEnd( dTrackPosX );
                ccLineSegment.setPosYEnd( dTrackPosY );
            }

            // Calculate ending angle for ccline segment.
            ccLineSegment.setAngleEnd( - Math.atan( (ccLineSegment.getPosYEnd() - ccLineSegment.getPosYCenter())
                                                    / (ccLineSegment.getPosXEnd() - ccLineSegment.getPosXCenter()) ) );
            // atan gives values between -90 and +90 degrees.
            // angles between 90 and 270 degrees have to be detected by negative X difference.
            // In this case, half circle hat to be added.
            if ( ccLineSegment.getPosXEnd() < ccLineSegment.getPosXCenter() )
                ccLineSegment.setAngleEnd( Math.PI + ccLineSegment.getAngleEnd() );
            // For right turns, half circle has to be added because center of the circle
            // is on other side of the line.
            if ( dRadius > 0.0 )
                ccLineSegment.setAngleEnd( Math.PI + ccLineSegment.getAngleEnd() );
        }
        else
        {
            // straight: ccLine angle does not change
            ccLineSegment.setAngleEnd( ccLineSegment.getAngleStart() );

            // Describe ccLine segment members by starting point S (StartX/YPos
            // of ccLine segment) and direction vector E calculated from ccLine angle.
            // Points P on ccLine segment are then described by P = S + l * E.
            // Calculate direction vector E (dEx, dEy) for ccLine segment
            double dEx = Math.cos( Math.PI / 2 - dCClineAngle );
            double dEy = Math.sin( Math.PI / 2 - dCClineAngle );

            // calculate factor k for intersection point
            if ( dEx == 0.0 )
            {
                // ccLine going straight up/down: X-Pos does not change
                ccLineSegment.setPosXEnd( ccLineSegment.getPosXStart() );
                k = (ccLineSegment.getPosXStart() - dTrackPosX) / dDx;
            }
            else
            {
                // Division by dEx is allowed: use general formula
                k = (ccLineSegment.getPosYStart()
                     + (dEy / dEx) * ( dTrackPosX - ccLineSegment.getPosXStart())
                     - dTrackPosY)
                    / ( dDy - (dEy / dEx ) * dDx );
            }
            // use factor to calculate end points for ccLineSegment
            ccLineSegment.setPosXEnd( dTrackPosX + k * dDx);
            ccLineSegment.setPosYEnd( dTrackPosY + k * dDy);
        }
    }

    // Calculate coordinates for start/end/center points of CCLine segments.
    public void calculateCCLine()
    {
        double ANGLE_SCALE = (2 * Math.PI) / 65536;

        if ( m_CCLine.size() == 0 )
            // emptry CCLine
            return;

        // To calculate the CCline, both CCline data itself and
        // track segment data is needed.
        CCLineSegment ccLineSegment;
        TrackSegment trackSegment;
        int nCumulatedTrackTlu = 0, nCumulatedCCLineTlu = 0;
        int nTrackSegmentIndex = 1, nCCLineSegmentIndex = 1;
        double dXPos, dYPos, dOffset, dAngleStart, dAngleEnd, dRadius;
        
        // Get the first segments
        ccLineSegment = m_CCLine.getAt( nCCLineSegmentIndex++ );
        nCumulatedCCLineTlu = ccLineSegment.getTlu();
        trackSegment = m_TrackSegments.getAt( nTrackSegmentIndex++ );
        nCumulatedTrackTlu = trackSegment.getTlu();

        // First ccLine segment is related to the first track segment:
        // starting point is on start/finish line, with an offset to the
        // middle of the road.
        dXPos = trackSegment.getPosXStart();
        dYPos = trackSegment.getPosYStart();
        dOffset = ccLineSegment.getParam( 0 ) / 1024.0 ; // given in width scales, i.e. 1/1024 tlu
        dAngleStart = trackSegment.getAngleStart() * s_dANGLE_SCALE;
        ccLineSegment.setAngleStart( dAngleStart );
        ccLineSegment.setPosXStart( dXPos + Math.cos( dAngleStart ) * dOffset );
        ccLineSegment.setPosYStart( dYPos + Math.sin( dAngleStart ) * dOffset );

        do {
            // retrieve starting values
            dXPos = ccLineSegment.getPosXStart();
            dYPos = ccLineSegment.getPosYStart();
            dAngleStart = ccLineSegment.getAngleStart();

            // calculate radius
            if ( (ccLineSegment.getType() & 0x40 ) != 0 )
            {
                // 32Bit radius
                dRadius = ccLineSegment.getParam( 1 ) << 16 + ccLineSegment.getParam( 2 );
            }
            else if ( ( ccLineSegment.getType() & 0x80 ) != 0 )
            {
                // Type 0x80 segment (first in list) carries radius in param 2
                dRadius = ccLineSegment.getParam( 2 );
            }
            else
            {
                // normal type 0 segment
                dRadius = ccLineSegment.getParam( 1 );
            }
            // scale to tlu and store into segment.
            dRadius = dRadius * s_dRADIUS_SCALE;
            ccLineSegment.setRadius( dRadius );

            // for curved segments, calculate arc center
            if ( dRadius != 0.0 )
            {
                // Segment is possibly shifted along the current direction.
                
                ccLineSegment.setPosXCenter( dXPos
                                             + Math.cos( Math.PI / 2 - dAngleStart ) * ccLineSegment.getShift() / 256.0
                                             - Math.cos( Math.PI - dAngleStart ) * dRadius );
                ccLineSegment.setPosYCenter( dYPos
                                             + Math.sin( Math.PI / 2 - dAngleStart ) * ccLineSegment.getShift() / 256.0
                                             - Math.sin( Math.PI - dAngleStart ) * dRadius );
            }
            else
            {
                ccLineSegment.setPosXCenter( 0.0 );
                ccLineSegment.setPosYCenter( 0.0 );
                // apply angle correction, if present
                dAngleStart += (ccLineSegment.getShift() << 2) * s_dANGLE_SCALE;
                ccLineSegment.setAngleStart( dAngleStart );
            }

            // calculate end point and end angle.
            // TLU value from CCLine segment is used for TrackSegments lookup, not
            // direct length calculations. Intersect CCLine segment with end
            // of corresponding track segment to find end point. For curved segments,
            // this calculation also gives the end angle.

            // Find the track segment where the CCLine segment ends.
            while ( nCumulatedCCLineTlu > nCumulatedTrackTlu )
            {
                trackSegment = m_TrackSegments.getAt( nTrackSegmentIndex++ );
                if ( trackSegment == null )
                {
                    // end of track reached
                    nCumulatedTrackTlu = nCumulatedCCLineTlu;
                    // previous TrackSegmentIndex pointed already behind the list and
                    // was incremented once. Last track segment is a dummy segment, so
                    // subtract 3 to get the last real segment.
                    trackSegment = m_TrackSegments.getAt( nTrackSegmentIndex - 3 );
                }
                else
                {
                    nCumulatedTrackTlu += trackSegment.getTlu();
                }
            }
            
            // Calculate angle of track segment border at the end of the ccLine segment.
            double dTrackAngle = trackSegment.getAngleEnd()
                                 - (nCumulatedTrackTlu - nCumulatedCCLineTlu) * trackSegment.getCurvature();
            dTrackAngle = dTrackAngle * ANGLE_SCALE;
            // Calculate point on the track segment border at the end of the ccLine segment.
            double dTrackPosX = trackSegment.getPosXEnd();
            double dTrackPosY = trackSegment.getPosYEnd();
            // Track segment end behind CCLine segment?
            if ( nCumulatedTrackTlu > nCumulatedCCLineTlu )
            {
                // otherwise, the end point of the track is already the point we are looking for!
                // calculation depends on whether tracksegment is curved or not.
                if ( trackSegment.getCurvature() == 0 )
                {
                    // straight
                    dTrackPosX = dTrackPosX
                                 - (nCumulatedTrackTlu - nCumulatedCCLineTlu) * Math.sin( dTrackAngle );
                    dTrackPosY = dTrackPosY
                                 - (nCumulatedTrackTlu - nCumulatedCCLineTlu) * Math.cos( dTrackAngle );
                }
                else
                {
                    // curved: use center and radius for calculations
                    dTrackPosX = trackSegment.getPosXCenter()
                                 + trackSegment.getRadius() * Math.cos( Math.PI - dTrackAngle );
                    dTrackPosY = trackSegment.getPosYCenter()
                                 + trackSegment.getRadius() * Math.sin( Math.PI - dTrackAngle );
                }
            }

            // Intersect CCLine with end of track segment. Sets values
            // for end point and end angle.
            intersect( ccLineSegment, dTrackPosX, dTrackPosY, dTrackAngle );
            
            // get next ccline segment
            if ( nCCLineSegmentIndex <= m_CCLine.size() )
            {
                // get end values of last segment
                dXPos = ccLineSegment.getPosXEnd();
                dYPos = ccLineSegment.getPosYEnd();
                dAngleEnd = ccLineSegment.getAngleEnd();
                // get next segment
                ccLineSegment = m_CCLine.getAt( nCCLineSegmentIndex++ );
                // add tlu's to cumulated value
                nCumulatedCCLineTlu += ccLineSegment.getTlu();
                // transfer end values of previous segment to new one.
                ccLineSegment.setPosXStart( dXPos );
                ccLineSegment.setPosYStart( dYPos );
                ccLineSegment.setAngleStart( dAngleEnd );
            }
            else
                ccLineSegment = null;
        } while ( ccLineSegment != null );
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
