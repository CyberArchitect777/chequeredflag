/*
 * CFDataObject.java
 *
 * Created on 19. Februar 2005, 22:11
 *
 * Serves as a base class for ChequeredFlag (CF) data classes.
 * Provides services for reading and writing of 16 and 32bit values (int, long).
 */

package chequeredflag.data;

import java.io.*;

/**
 *
 * @author Klaus
 */
public class CFDataObject {

    /** Creates a new instance of CFDataObject */
    public CFDataObject() {
    }

    // reading data
    public int loadInt( FileInputStream fis ) throws IOException
    {
        // Convert to short for correct creation of negative values
        return (short) (fis.read() + fis.read() * 256);
    }

    public long loadLong( FileInputStream fis ) throws IOException
    {
        return fis.read() + ( fis.read() << 8 ) + ( fis.read() << 16 ) + ( fis.read() << 24 );
    }

    // writing data
    public int write( FileOutputStream fos, int nValue ) throws IOException
    {
        fos.write( nValue & 0x0FF );
        fos.write( ( nValue >> 8 ) & 0x0FF );
        return 2; // 2 bytes written
    }

    public int write( FileOutputStream fos, long lValue ) throws IOException
    {
        fos.write( (int) lValue & 0x0FF );
        fos.write( (int) ( lValue >> 8 ) & 0x0FF );
        fos.write( (int) ( lValue >> 16 ) & 0x0FF );
        fos.write( (int) ( lValue >> 24 ) & 0x0FF );
        return 4; // 4 bytes written
    }
}
