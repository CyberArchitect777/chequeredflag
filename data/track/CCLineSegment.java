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
            m_nParam[ 0 ] = loadInt( fis );
            m_nParam[ 1 ] = loadInt( fis );
            // Rest depends on CCLine segment type
            if ( m_nType != 0 )
            {
                // Should be a type 0x80 (first segment) or 0x40 (32bit radius) segment
                m_nParam[ 2 ] = loadInt( fis );
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

    public int getShift()
    {
        int nShift;
        if ( ( m_nType & 0x80 ) > 0 )
            nShift = m_nParam[1];
        else
            nShift = m_nParam[0];
        return nShift;
    }

    // Params are stored and retrieved by 0-based index
    public int getParam( int nIndex )
    { return m_nParam[ nIndex ]; }
    public void setParam( int nIndex, int nValue )
    { m_nParam[ nIndex ] = nValue; }

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

    public double getRadius() {
        return m_dRadius;
    }

    public void setRadius(double dRadius) {
        m_dRadius = dRadius;
    }

    public double getPosXStart() {
        return m_dPosXStart;
    }

    public void setPosXStart(double dPosXStart) {
        m_dPosXStart = dPosXStart;
    }

    public double getPosYStart() {
        return m_dPosYStart;
    }

    public void setPosYStart(double dPosYStart) {
        m_dPosYStart = dPosYStart;
    }

    public double getPosXEnd() {
        return m_dPosXEnd;
    }

    public void setPosXEnd(double dPosXEnd) {
        m_dPosXEnd = dPosXEnd;
    }

    public double getPosYEnd() {
        return m_dPosYEnd;
    }

    public void setPosYEnd(double dPosYEnd) {
        m_dPosYEnd = dPosYEnd;
    }

    public double getPosXCenter() {
        return m_dPosXCenter;
    }

    public void setPosXCenter(double dPosXCenter) {
        m_dPosXCenter = dPosXCenter;
    }

    public double getPosYCenter() {
        return m_dPosYCenter;
    }

    public void setPosYCenter(double dPosYCenter) {
        m_dPosYCenter = dPosYCenter;
    }

    public double getAngleStart() {
        return m_dAngleStart;
    }

    public void setAngleStart(double dAngleStart ) {
        m_dAngleStart = dAngleStart;
    }

    public double getAngleEnd() {
        return m_dAngleEnd;
    }

    public void setAngleEnd(double dAngleEnd ) {
        m_dAngleEnd = dAngleEnd;
    }

    // instance data members
    protected int m_nType, m_nTlu;
    protected int m_nParam[];

    // calculated values
    private double m_dRadius;
    private double m_dPosXStart, m_dPosYStart;
    private double m_dPosXEnd, m_dPosYEnd;
    private double m_dPosXCenter, m_dPosYCenter;
    private double m_dAngleStart, m_dAngleEnd;
}
