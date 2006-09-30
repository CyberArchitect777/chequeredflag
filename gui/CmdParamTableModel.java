/*
 * CmdParamTableModel.java
 *
 * Created on 13 May 2005, 23:55
 */

package chequeredflag.gui;
import chequeredflag.data.track.*;

/**
 *
 * @author barrie
 */
public class CmdParamTableModel extends StandardTableModel
{
    
    private Command currentCommand;
    private String[] paramText;
    
    /** Creates a new instance of CmdParamTableModel */
    /** This class is designed to provide a database like interface for */
    /** F1GP/WC track command parameters */
    public CmdParamTableModel(Command selectedCommand, String[] selectedParamText) 
    {
        // Sets the size of the table for the editing of this item
        // Will need to be updated as more information is added
        
        currentCommand = selectedCommand;
        paramText = selectedParamText;
        
        tableDimension(paramText.length,2);
      
        populateTable();        
    }
    
    public void updateTrackData()
    {
        // Updates the track data held in memory with the information currently displayed in the table
        
        for (int x=0;x<paramText.length;x++)
        {
            currentCommand.setParam(x, new Integer((String)getValueAt(x,1)).intValue());
        }
        
    }
    
    public void populateTable()
    {
        
        // Populate the table with track segment editing information
        
        for (int x=0;x<paramText.length;x++)
        {
            setValueAt(paramText[x],x, 0);
            setValueAt(new String(new Integer(currentCommand.getParam(x)).toString()),x,1);
        }        
    }
    
}
