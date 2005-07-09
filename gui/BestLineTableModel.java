/*
 * BestLineTableModel.java
 *
 * Created on 14 June 2005, 01:50
 */

package chequeredflag.gui;

import chequeredflag.data.track.*;
import java.util.Vector;
import javax.swing.JComboBox;

/**
 *
 * @author barrie
 */
public class BestLineTableModel extends StandardTableModel
{
    
    private CCLineSegment currentLineSegment;
    private JComboBox typeList;
    
    /** Creates a new instance of BestLineTableModel */
    public BestLineTableModel(CCLineSegment selectedLineSegment) 
    {
        // Sets the size of the table for the editing of this item
        // Will need to be updated as more information is added
        
        tableDimension(4,2);
        
        currentLineSegment = selectedLineSegment;
        typeList = new JComboBox();
        
        populateTable(); 
    }
    
    public void getInfoForType(int segmentType)
    {
        switch (segmentType)
        {
            case 0: tableDimension(4,2); break;
            case 1: tableDimension(5,2); break;
            case 2: tableDimension(5,2); break;
            case 3: tableDimension(6,2); break;
        }
        System.out.println("Segment Type - " + segmentType);
        setValueAt("Best Line Type",0,0);
        setValueAt(typeList,0,1);
        setValueAt("Best Line Length",1,0);
        setValueAt(new String(new Integer(currentLineSegment.getTlu()).toString()),1,1);
        
        if ((segmentType == 0) || (segmentType == 1))
        {
            setValueAt("Best Line Correction",2,0);
            setValueAt(new String(new Integer(currentLineSegment.getParam(0)).toString()),2,1);
            if (segmentType == 0)
            {
                setValueAt("Best Line Radius",3,0);
                setValueAt(new String(new Integer(currentLineSegment.getParam(1)).toString()),3,1);
            }
            else
            {
                setValueAt("Best Line High Radius",3,0);
                setValueAt(new String(new Integer(currentLineSegment.getParam(1)).toString()),3,1);
                setValueAt("Best Line Low Radius",4,0);
                setValueAt(new String(new Integer(currentLineSegment.getParam(2)).toString()),4,1);
            }
        }
        else
        {
            setValueAt("Best Line Displacement",2,0);
            setValueAt(new String(new Integer(currentLineSegment.getParam(1)).toString()),2,1);
            setValueAt("Best Line Correction",3,0);
            setValueAt(new String(new Integer(currentLineSegment.getParam(0)).toString()),3,1);
            if (segmentType == 2)
            {
                setValueAt("Best Line Radius",4,0);
                setValueAt(new String(new Integer(currentLineSegment.getParam(0)).toString()),4,1);
            }
            else
            {
                setValueAt("Best Line High Radius",4,0);
                setValueAt(new String(new Integer(currentLineSegment.getParam(1)).toString()),4,1);
                setValueAt("Best Line Low Radius",5,0);
                setValueAt(new String(new Integer(currentLineSegment.getParam(2)).toString()),5,1);
            }
        }
        this.fireTableDataChanged();
    }    
    
    public void populateTable()
    {
        
        // Populate the table with track segment editing information
        
        typeList.addItem(new String("Normal"));
        typeList.addItem(new String("Wider Radius"));
        typeList.addItem(new String("Displacement"));
        //typeList.addItem(new String("Combined"));
        getInfoForType(typeList.getSelectedIndex());
        typeList.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent typeEvent)
            {
                getInfoForType(typeList.getSelectedIndex());
            }
        });
    }
    
}
