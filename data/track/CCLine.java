/*
 * CCLine.java
 *
 * Created on 17. Februar 2005, 23:46
 */

package chequeredflag.data.track;

import java.io.*;
import java.util.*;


/**
 *
 * @author Klaus
 */
public class CCLine extends Vector {

    /** Creates a new instance of CCLine */
    public CCLine() {
    }

    public void load( FileInputStream fis)
    {
        int nType;
        int nTlu;
        // initialize cumulated length
        m_nCumTlu = 0;
        try {
            nTlu = fis.read();
            nType = fis.read();
            while ( nTlu != 0 )
            {
                // Create CCLine segment
                CCLineSegment seg = new CCLineSegment( nType );
                // transfer length
                seg.setTlu( nTlu );
                // add tlu value to cumulated length
                m_nCumTlu += nTlu;
                // load other data from file
                seg.load( fis );
                // Store segment in list
                add(seg);
                // read next type/tlu data
                nTlu = fis.read();
                nType = fis.read();
            }
        }
        catch( IOException ioe )
        {
        }
    }

    public int save(FileOutputStream fos) throws IOException
    {
        int nBytesWritten = 0;
        for( Enumeration e = elements(); e.hasMoreElements(); )
        {
            nBytesWritten += ((CCLineSegment) e.nextElement()).save(fos);
        }
        // save end of list pattern
        fos.write( 0 ); // Type
        fos.write( 0 ); // Tlu
        nBytesWritten += 2;
        return nBytesWritten;
    }

    // retrieves CCLineSegment by 1-based index
    public CCLineSegment getAt( int nIndex )
    {
        if ( ( nIndex > elementCount ) || ( nIndex < 1 ) )
            return null;
        else
            //return (CCLineSegment) elementAt( nIndex + 1 );
            // Code bug found above by barrie. Suspected line is below
            return (CCLineSegment) elementAt( nIndex - 1);
    }

    /** inserts new CCline segment at given index (1-based).
        returns newly created segment. */
    public CCLineSegment insertAt( int i )
    {
        CCLineSegment newSeg;
        // creating a straight segment of length 1.
        newSeg = new CCLineSegment( 0 );
        newSeg.m_nTlu = 1;
        if ( i > elementCount )
            add( newSeg );
        else
        {
            try {
                add( i - 1, newSeg );
            }
            catch ( ArrayIndexOutOfBoundsException e )
            {
                newSeg = null;
            }
        }
        return null;
    }

    /** delete segment at given position (1-based) */
    public void deleteAt(int i)
    {
        try {
            remove( i - 1 );
        }
        catch( ArrayIndexOutOfBoundsException e )
        {
        }
    }

    /** instance data members */
    protected int m_nCumTlu;
}
