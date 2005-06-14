/*
 * BestLineTableModel.java
 *
 * Created on 14 June 2005, 01:50
 */

package chequeredflag.gui;

import chequeredflag.data.track.*;

/**
 *
 * @author barrie
 */
public class BestLineTableModel extends StandardTableModel
{
    
    private CCLineSegment currentLineSegment;
    
    /** Creates a new instance of BestLineTableModel */
    public BestLineTableModel(CCLineSegment selectedLineSegment) 
    {
        // Sets the size of the table for the editing of this item
        // Will need to be updated as more information is added
        
        tableDimension(21,2);
        
        currentLineSegment = selectedLineSegment;
        populateTable(); 
    }
    
    public void populateTable()
    {
        
        // Populate the table with track segment editing information
        
        setValueAt("Best Line Type",0,0);
        setValueAt(new String(new Integer(currentLineSegment.getType()).toString()),0,1);
        setValueAt("Best Line Length",1,0);
        setValueAt(new String(new Integer(currentLineSegment.getTlu()).toString()),1,1);
        
    }
    
}
