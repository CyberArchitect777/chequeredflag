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
        
        segmentDatabase = new Object[5][2];
        
        currentSegment = selectedSegment;
        populateTable();        
    }
    
    public void populateTable()
    {
        
        // Populate the table with track segment editing information
        
        String binaryCode = currentSegment.getDetailedFlags();
        System.out.println("Code received: " + binaryCode);
        segmentDatabase[0][0] = "Right Pit Wall";
        segmentDatabase[0][1] = getTrackFlagState(binaryCode, 15);
        segmentDatabase[1][0] = "Left Pit Wall";
        segmentDatabase[1][1] = getTrackFlagState(binaryCode, 14);
        segmentDatabase[2][0] = "No Right Wall";
        segmentDatabase[2][1] = getTrackFlagState(binaryCode, 3);
        segmentDatabase[3][0] = "No Left Wall";
        segmentDatabase[3][1] = getTrackFlagState(binaryCode, 2);
        segmentDatabase[4][0] = "Testing...";
        segmentDatabase[4][1] = "String";
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
    
}
