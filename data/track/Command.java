/*
 * Command.java
 *
 * Created on 16. Februar 2005, 23:17
 */

package chequeredflag.data.track;

import java.io.*;
import chequeredflag.data.*;

/**
 *
 * @author Klaus
 */
public class Command extends CFDataObject {

    /** Creates a new instance of Command */
    public Command(int nType, int nParam0) {
        m_nType = nType;
        // Always allocate 6 parameters, regardless of type.
        // This makes type change easy.
        m_nParam = new int[6];
        // store first parameter
        m_nParam[ 0 ] = nParam0;
    }

    public void load( FileInputStream fis )
    {
        try {
            // Number of parameters depends on type
            switch (m_nType)
            {
            case 0x83:  // disable drawing of background picture
            case 0x84:  // enable drawing of background picture
            case 0x86:  // pit start
            case 0x87:  // pit end
            case 0x96:  // Unk
            case 0x97:  // Unk
            case 0x9B:  // Unk
            case 0x9C:  // Unk
            case 0x9D:  // Unk
            case 0x9E:  // Unk
            case 0x9F:  // Unk
            case 0xA0:  // Unk
            case 0xA1:  // pit lane entry, join right pit lane fence
            case 0xA2:  // pit lane entry, join left pit lane fence
            case 0xA3:  // pit lane exit, join right pit lane fence
            case 0xA4:  // pit lane exit, join left pit lane fence
            case 0xA5:  // Unk
            case 0xA8:  // marshal with a flag
                // no further parameters
                break;

            case 0x80:  // object placement
            case 0x81:  // display distance front
            case 0x82:  // display distance back
            case 0x88:  // Unk
            case 0x89:  // Unk
            case 0x8C:  // Unk
            case 0x8D:  // Unk
            case 0x90:  // Unk
            case 0x91:  // Unk
            case 0x92:  // Unk
            case 0x93:  // Unk
            case 0x94:  // curve polygon subdivision (1=many polygon, 8=few polygon)
            case 0x95:  // may be same as 94 (?)
            case 0x98:  // left fence height change
            case 0x99:  // right fence height change
            case 0xA9:  // Unk
                // one 2-byte int parameter
                m_nParam[ 1 ] = fis.read() + fis.read() * 256;
                break;

            case 0x85: // track width change
            case 0x8E: // left kerbs begin/length
            case 0x8F: // right kerbs begin/length
            case 0x9A: // Unk
            case 0xA6: // Unk
            case 0xA7: // Unk
            case 0xAB: // Unk
                // 2 Parameters
                m_nParam[ 1 ] = fis.read() + fis.read() * 256;
                m_nParam[ 2 ] = fis.read() + fis.read() * 256;
                break;

            case 0xAA: // Unk
                // 3 Parameters
                m_nParam[ 1 ] = fis.read() + fis.read() * 256;
                m_nParam[ 2 ] = fis.read() + fis.read() * 256;
                m_nParam[ 3 ] = fis.read() + fis.read() * 256;
                break;

            case 0xAC: // palette change
                // 4 Parameters
                m_nParam[ 1 ] = fis.read() + fis.read() * 256;
                m_nParam[ 2 ] = fis.read() + fis.read() * 256;
                m_nParam[ 3 ] = fis.read() + fis.read() * 256;
                m_nParam[ 4 ] = fis.read() + fis.read() * 256;
                break;

            case 0x8A: // track markings
            case 0x8B: // starting grid markings
                // 5 Parameters
                m_nParam[ 1 ] = fis.read() + fis.read() * 256;
                m_nParam[ 2 ] = fis.read() + fis.read() * 256;
                m_nParam[ 3 ] = fis.read() + fis.read() * 256;
                m_nParam[ 4 ] = fis.read() + fis.read() * 256;
                m_nParam[ 5 ] = fis.read() + fis.read() * 256;
                break;
            }
        }
        catch(IOException ioe)
        {
        }
    }

    public int save( FileOutputStream fos ) throws IOException
    {
        int nWritten = 0;
        // First parameter is always there (one byte)
        fos.write( m_nParam[ 0 ] );
        // Write type
        fos.write( m_nType );
        nWritten = 2;
        // Rest depends on type
        switch (m_nType)
        {
        case 0x83:  // disable drawing of background picture
        case 0x84:  // enable drawing of background picture
        case 0x86:  // pit start
        case 0x87:  // pit end
        case 0x96:  // Unk
        case 0x97:  // Unk
        case 0x9B:  // Unk
        case 0x9C:  // Unk
        case 0x9D:  // Unk
        case 0x9E:  // Unk
        case 0x9F:  // Unk
        case 0xA0:  // Unk
        case 0xA1:  // pit lane entry, join right pit lane fence
        case 0xA2:  // pit lane entry, join left pit lane fence
        case 0xA3:  // pit lane exit, join right pit lane fence
        case 0xA4:  // pit lane exit, join left pit lane fence
        case 0xA5:  // Unk
        case 0xA8:  // marshal with a flag
            // no further parameters
            break;

        case 0x80:  // object placement
        case 0x81:  // display distance front
        case 0x82:  // display distance back
        case 0x88:  // Unk
        case 0x89:  // Unk
        case 0x8C:  // Unk
        case 0x8D:  // Unk
        case 0x90:  // Unk
        case 0x91:  // Unk
        case 0x92:  // Unk
        case 0x93:  // Unk
        case 0x94:  // curve polygon subdivision (1=many polygon, 8=few polygon)
        case 0x95:  // may be same as 94 (?)
        case 0x98:  // left fence height change
        case 0x99:  // right fence height change
        case 0xA9:  // Unk
            // one 2-byte int parameter
            write( fos, m_nParam[ 1 ] );
            nWritten += 2;
            break;

        case 0x85: // track width change
        case 0x8E: // left kerbs begin/length
        case 0x8F: // right kerbs begin/length
        case 0x9A: // Unk
        case 0xA6: // Unk
        case 0xA7: // Unk
        case 0xAB: // Unk
            // 2 Parameters
            write( fos, m_nParam[ 1 ] );
            write( fos, m_nParam[ 2 ] );
            nWritten += 4;
            break;

        case 0xAA: // Unk
            // 3 Parameters
            write( fos, m_nParam[ 1 ] );
            write( fos, m_nParam[ 2 ] );
            write( fos, m_nParam[ 3 ] );
            nWritten += 6;
            break;

        case 0xAC: // palette change
            // 4 Parameters
            write( fos, m_nParam[ 1 ] );
            write( fos, m_nParam[ 2 ] );
            write( fos, m_nParam[ 3 ] );
            write( fos, m_nParam[ 4 ] );
            nWritten += 8;
            break;

        case 0x8A: // track markings
        case 0x8B: // starting grid markings
            // 5 Parameters
            write( fos, m_nParam[ 1 ] );
            write( fos, m_nParam[ 2 ] );
            write( fos, m_nParam[ 3 ] );
            write( fos, m_nParam[ 4 ] );
            write( fos, m_nParam[ 5 ] );
            nWritten += 10;
            break;
        }
        return nWritten;
    }

    // instance data members
    int m_nType;
    int m_nParam[];

    public int getParam(int nParam) {
        if ( nParam <= 5 )
            return m_nParam[ nParam ];
        else
            return 0;
    }

    public int getType() {
        return m_nType;
    }
}
