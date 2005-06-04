/*
 * SegmentTableModel.java
 *
 * Created on 13 May 2005, 23:55
 */

package chequeredflag.gui;
import javax.swing.table.*;
import chequeredflag.data.track.*;

/**
 *
 * @author barrie
 */
public class SegmentTableModel extends AbstractTableModel
{
    
    private Object[][] segmentDatabase;
    private TrackSegment currentSegment;
    
    /** Creates a new instance of SegmentTableModel */
    /** This class is designed to provide a database like interface for */
    /** F1GP/WC track segments */
    public SegmentTableModel(TrackSegment selectedSegment) 
    {
        // Will need to be updated as more information is added
        
        segmentDatabase = new Object[21][2];
        
        currentSegment = selectedSegment;
        populateTable();        
    }
    
    public void populateTable()
    {
        
        // Populate the table with track segment editing information
        
        String binaryCode = currentSegment.getDetailedFlags();
        System.out.println("Code received: " + binaryCode);
        
        segmentDatabase[0][0] = "Segment Length";
        segmentDatabase[0][1] = new String(new Integer(currentSegment.getTlu()).toString());
        segmentDatabase[1][0] = "Curvature";
        segmentDatabase[1][1] = new String(new Integer(currentSegment.getCurvature()).toString());
        segmentDatabase[2][0] = "Height Difference";
        segmentDatabase[2][1] = new String(new Integer(currentSegment.getHeightChange()).toString());
        segmentDatabase[3][0] = "Left Run-Off Area";
        segmentDatabase[3][1] = new String(new Integer(currentSegment.getFenceDistL()).toString());
        segmentDatabase[4][0] = "Right Run-Off Area";
        segmentDatabase[4][1] = new String(new Integer(currentSegment.getFenceDistR()).toString());
        segmentDatabase[5][0] = "Unknown 1 (0x8000)";
        segmentDatabase[5][1] = getTrackFlagState(binaryCode, 0);
        segmentDatabase[6][0] = "Unknown 2 (0x4000)";
        segmentDatabase[6][1] = getTrackFlagState(binaryCode, 1);
        segmentDatabase[7][0] = "No Left Wall";
        segmentDatabase[7][1] = getTrackFlagState(binaryCode, 2);
        segmentDatabase[8][0] = "No Right Wall";
        segmentDatabase[8][1] = getTrackFlagState(binaryCode, 3);
        segmentDatabase[9][0] = "Left Kerb";
        segmentDatabase[9][1] = getTrackFlagState(binaryCode, 4);
        segmentDatabase[10][0] = "Right Kerb";
        segmentDatabase[10][1] = getTrackFlagState(binaryCode, 5);
        segmentDatabase[11][0] = "Unknown 3 (0x0200)";
        segmentDatabase[11][1] = getTrackFlagState(binaryCode, 6);
        segmentDatabase[12][0] = "Unknown 4 (0x0100)";
        segmentDatabase[12][1] = getTrackFlagState(binaryCode, 7);
        segmentDatabase[13][0] = "Unknown 5 (0x0080)";
        segmentDatabase[13][1] = getTrackFlagState(binaryCode, 8);
        segmentDatabase[14][0] = "Unknown 6 (0x0040)";
        segmentDatabase[14][1] = getTrackFlagState(binaryCode, 9);
        segmentDatabase[15][0] = "Left Non-Parallel Wall";
        segmentDatabase[15][1] = getTrackFlagState(binaryCode, 10);
        segmentDatabase[16][0] = "Right Non-Parallel Wall";
        segmentDatabase[16][1] = getTrackFlagState(binaryCode, 11);
        segmentDatabase[17][0] = "Unknown 7 (0x0008)";
        segmentDatabase[17][1] = getTrackFlagState(binaryCode, 12);
        segmentDatabase[18][0] = "Kerb Type";
        segmentDatabase[18][1] = getTrackFlagState(binaryCode, 13);
        segmentDatabase[19][0] = "Left Pit Wall";
        segmentDatabase[19][1] = getTrackFlagState(binaryCode, 14);
        segmentDatabase[20][0] = "Right Pit Wall";
        segmentDatabase[20][1] = getTrackFlagState(binaryCode, 15);
        
    }
    
    public Boolean getTrackFlagState(String binaryCode, int flagNo)
    {
        
        // Refine the track segment flag information down to providing the state of one individual selected flag
        // (simulated binary digit 0 to 15)
        
        char uniqueCode = binaryCode.charAt(flagNo);
        if (uniqueCode == '1')
        {
            return new Boolean(true);
        }
        else
        {
            return new Boolean(false);
        }       
    }
    
    public int getRowCount()
    {
        return segmentDatabase.length;        
    }
    
    public int getColumnCount()
    {
        return segmentDatabase[0].length;
    }
    
    public Class getColumnClass(int columnIndex)
    {
        return segmentDatabase[0][columnIndex].getClass();
    }
    
    public Object getValueAt(int row, int column)
    {
        return segmentDatabase[row][column];
    }
    
    public void setValueAt(Object aValue, int row, int column)
    {
        if (aValue instanceof Boolean)
        {
            if (((Boolean)aValue).booleanValue() == true)
            {
                segmentDatabase[row][column] = new Boolean(true);
            }
            else
            {
                segmentDatabase[row][column] = new Boolean(false);
            }
        }
        else
        {
            segmentDatabase[row][column] = aValue;
        }
    }
    
    public boolean isCellEditable(int row, int column)
    {
        if (column==0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
}
