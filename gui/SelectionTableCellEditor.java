/*
 * SelectionTableCellEditor.java
 *
 * Created on 07 July 2005, 00:48
 */

package chequeredflag.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.*;

/**
 *
 * @author barrie
 */
public class SelectionTableCellEditor extends AbstractCellEditor implements TableCellEditor
{
    
    private JComboBox selectionList;
    
    /** Creates a new instance of SelectionTableCellEditor */
    public SelectionTableCellEditor() 
    {
        selectionList = new JComboBox();
    }
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        selectionList = (JComboBox)value;
        selectionList.setAlignmentX(SwingConstants.CENTER);
        selectionList.setOpaque(true);
        
        return selectionList;
    }
    
    public Object getCellEditorValue()
    {
        return selectionList;
    }
}
