/*
 * TextTableCellEditor.java
 *
 * Created on 04 June 2005, 01:04
 */

package chequeredflag.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.*;

/**
 *
 * @author barrie
 */
public class TextTableCellEditor extends AbstractCellEditor implements TableCellEditor
{
    
    private JTextField cellField;
    
    /** Creates a new instance of TextTableCellEditor */
    public TextTableCellEditor() 
    {
        cellField = new JTextField();
    }
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        cellField.setHorizontalAlignment(SwingConstants.CENTER);
        cellField.setOpaque(true);
        Font labelFont = cellField.getFont();
        cellField.setFont(new Font("SansSerif", Font.PLAIN, labelFont.getSize()));
        cellField.setText((String)value);
        return cellField;
    }
    
    public Object getCellEditorValue()
    {
        return cellField.getText();
    }
}
