/*
 * TrackDataHeader.java
 *
 * Created on 16. Februar 2005, 00:12
 */

package chequeredflag.data.track;

import java.io.*;
import chequeredflag.data.*;

/**
 *
 * @author Klaus
 */
public class TrackDataHeader extends CFDataObject {

    /** Creates a new instance of TrackDataHeader */
    public TrackDataHeader() {
    }

    public void load( FileInputStream fis )
    {
        try {
            m_nStartAngle       = fis.read() + fis.read() * 256;
            m_nStartHeight      = fis.read() + fis.read() * 256;
            m_nStartY           = fis.read() + fis.read() * 256;
            m_nStartZ           = fis.read() + fis.read() * 256;
            m_nStartX           = fis.read() + fis.read() * 256;
            m_nStartWidth       = fis.read() + fis.read() * 256;
            m_nPoleWidth        = fis.read() + fis.read() * 256;
            m_nPitSide          = fis.read();
            m_nTrSurround       = fis.read();
            m_nFenceDistR       = fis.read();
            m_nFenceDistL       = fis.read();
            m_nKerbCNum         = fis.read() + fis.read() * 256;
            m_nUnk1             = fis.read() + fis.read() * 256;
            m_nKerbTopColor     = fis.read() + fis.read() * 256;
            m_nUnk2             = fis.read() + fis.read() * 256;
            m_nKerbBottomColor  = fis.read() + fis.read() * 256;
            if ( m_nKerbCNum == 4 )
            {
                // read two more colours
                m_nKerbTopColor2    = fis.read() + fis.read() * 256;
                m_nKerbBottomColor2 = fis.read() + fis.read() * 256;
            }
            else
            {
                m_nKerbTopColor2    = m_nKerbTopColor;
                m_nKerbBottomColor2 = m_nKerbBottomColor;
            };

            // other values calculated from those read from file
            m_dWidth = m_nStartWidth * 2 * Track.s_dWIDTHSCALE;
        }
        catch( IOException ioe )
        {
        }
    }

    public int save( FileOutputStream fos ) throws IOException
    {
        // double-byte values
        write( fos, m_nStartAngle );
        write( fos, m_nStartHeight );
        write( fos, m_nStartY );
        write( fos, m_nStartZ );
        write( fos, m_nStartX );
        write( fos, m_nStartWidth );
        write( fos, m_nPoleWidth );
        // single-byte values
        fos.write( m_nPitSide );
        fos.write( m_nTrSurround );
        fos.write( m_nFenceDistR );
        fos.write( m_nFenceDistL );
        // double-byte values, again
        write( fos, m_nKerbCNum );
        write( fos, m_nUnk1 );
        write( fos, m_nKerbTopColor );
        write( fos, m_nUnk2 );
        write( fos, m_nKerbBottomColor );
        // 28 Bytes written so far
        int nResult = 28;
        if ( m_nKerbCNum == 4 )
        {
            // write two more colours
            write( fos, m_nKerbTopColor2 );
            write( fos, m_nKerbBottomColor2 );
            nResult += 4; // 4 more bytes written
        }
        return nResult;
    }
    
    protected int m_nStartAngle;
    protected int m_nStartHeight;
    protected int m_nStartX, m_nStartY, m_nStartZ;
    protected int m_nStartWidth;
    protected int m_nPoleWidth;
    protected int m_nPitSide;
    protected int m_nTrSurround;
    protected int m_nFenceDistR, m_nFenceDistL;
    protected int m_nKerbCNum;
    protected int m_nUnk1, m_nUnk2;
    protected int m_nKerbTopColor, m_nKerbBottomColor;
    protected int m_nKerbTopColor2, m_nKerbBottomColor2;

    // calculated values
    protected double m_dWidth;
    
    public void setUnknown1(int unknownValue1)
    {
        // Sets the value of variable Unknown1
        
        m_nUnk1 = unknownValue1;        
    }
    
    public void setUnknown2(int unknownValue2)
    {
        // Sets the value of variable Unknown2
        
        m_nUnk2 = unknownValue2;        
    }
    
    public int getUnknown1()
    {
        // Returns the value of variable Unknown1
        
        return m_nUnk1;
    }
    
    public int getUnknown2()
    {
        
        // Return the value of variable Unknown2
        
        return m_nUnk2;
    }
    
    public void setTotalKerbColours(int totalKerbColours)
    {
        // Sets the total number of colours used for the kerbs
        
        m_nKerbCNum = totalKerbColours;
    }
    
    public int getTotalKerbColours()
    {
        // Return the total number of colours used for the kerbs
        
        return m_nKerbCNum;
    }
    
    public void setKerbColor(boolean upperSide, boolean firstKerb, int colourValue)
    {
        // Sets the colour of the selected kerb element
        
        if (upperSide == true)
        {
            if (firstKerb == true)
            {
                m_nKerbTopColor = colourValue;
            }
            else
            {
                m_nKerbTopColor2 = colourValue;
            }
        }
        else
        {
            if (firstKerb == true)
            {
                m_nKerbBottomColor = colourValue;
            }
            else
            {
                m_nKerbBottomColor2 = colourValue;
            }
        }
        
    }
    
    public int getKerbColor(boolean upperSide, boolean firstKerb)
    {
        // Return the colour of the selected kerb element
        
        if (upperSide == true)
        {
            if (firstKerb == true)
            {
                return m_nKerbTopColor;
            }
            else
            {
                return m_nKerbTopColor2;
            }
        }
        else
        {
            if (firstKerb == true)
            {
                return m_nKerbBottomColor;
            }
            else
            {
                return m_nKerbBottomColor2;
            }
        }
    }
    
    public int getTrSurround()
    {
        // Return TrSurround data header (currently unknown)
        
        return m_nTrSurround;
    }
    
    public void setTrSurround(int trSurround)
    {
        // Sets TrSurround data header (currently unknown)
        
        m_nTrSurround = trSurround;
    }
    
    public int getStartHeight()
    {
        // Return starting height of first segment
        
        return m_nStartHeight;        
    }
    
    public void setStartHeight(int startingHeight)
    {
        // Sets the starting height of the first segment
        
        m_nStartHeight = startingHeight;        
    }
    
    public int getStartPos(int posIndex)
    {
        // Returns the X, Y or Z coordinates position of the start/finish line
        // Point corresponds to the track center position
        
        int posData = 0;
        
        switch (posIndex)
        {
            case 0: posData = m_nStartX; break;
            case 1: posData = m_nStartY; break;
            case 2: posData = m_nStartZ; break;
        }      
        
        return posData;        
    }
    
    public void setStartPos(int posIndex, int posValue)
    {
        // Sets the X, Y or Z coordinate position of the start/finish line
        // Point corresponds to the track center position
        
        switch (posIndex)
        {
            case 0: m_nStartX = posValue; break;
            case 1: m_nStartY = posValue; break;
            case 2: m_nStartZ = posValue; break;
        }
    }
    
    public int getPoleWidth()
    {
        // Returns the pole width (currently unknown setting)
        
        return m_nPoleWidth;
    }
    
    public void setPoleWidth(int poleWidth)
    {
        // Sets the pole width (currently unknown setting)
        
        m_nPoleWidth = poleWidth;
    }

    public int getStartWidth() {
        return m_nStartWidth;
    }
    
    public void setStartWidth(int startingWidth)
    {
        // Sets the starting width of the first segment
        
        m_nStartWidth = startingWidth;        
    }    

    public int getStartAngle() {
        return m_nStartAngle;
    }
    
    public void setStartAngle(int startingAngle)
    {
        // Sets the starting angle of the first segment
        
        m_nStartAngle = startingAngle;
    }
    

    public boolean getPitSide() {
        if ( m_nPitSide > 0  )
            return true;
        else
            return false;
    }
    
    public void setPitSide(boolean pitSide)
    {
        // Sets the side in which the pitlane exists on the track
        
        if (pitSide == false)
        {
            m_nPitSide = 0;
        }
        else
        {
            m_nPitSide = 1;
        }
    }
    
    public int getFenceDistL() {
        return m_nFenceDistL;
    }
    
    public void setFenceDistL(int fenceDistL)
    {
        // Sets the initial distance between the track and fence on the left side
        
        m_nFenceDistL = fenceDistL;
    }

    public int getFenceDistR() {
        return m_nFenceDistR;
    }
    
    public void setFenceDistR(int fenceDistR)
    {
        // Sets the initial distance between the track and fence on the right side
        
        m_nFenceDistR = fenceDistR;
        
    }
}
