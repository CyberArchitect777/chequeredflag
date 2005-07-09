/*
 * SelectionTableCellRenderer.java
 *
 * Created on 06 July 2005, 03:23
 */

package chequeredflag.gui;

import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author barrie
 */
public class SelectionTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer
{
    
    private JComboBox selectionList;
        
    /** Creates a new instance of SelectionTableCellRenderer */
    public SelectionTableCellRenderer() 
    {
        selectionList = new JComboBox();
    }
    
     public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
     {
        
        if (value instanceof JComboBox)
        {
            selectionList = (JComboBox)value;
            selectionList.setAlignmentX(SwingConstants.CENTER);
            selectionList.setOpaque(true);
            selectionList.setForeground(table.getForeground());
            selectionList.setBackground(table.getBackground());
            return selectionList;
        }
        else
        {
            return this;
        }     
    }
    
}
