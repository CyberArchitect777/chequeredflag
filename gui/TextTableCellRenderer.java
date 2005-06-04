/*
 * TextTableCellRenderer.java
 *
 * Created on 23 May 2005, 01:42
 */

package chequeredflag.gui;

import javax.swing.*;
import chequeredflag.gui.beans.*;
import java.awt.*;
import javax.swing.table.*;

/**
 *
 * @author barrie
 */
public class TextTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer
{
    private JLabel cellLabel = new JLabel();
        
    /** Creates a new instance of TextTableCellRenderer */
    public TextTableCellRenderer() 
    {
        cellLabel = new JLabel();
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        cellLabel.setText((String)value);
        cellLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cellLabel.setOpaque(true);
        Font labelFont = cellLabel.getFont();
        cellLabel.setFont(new Font("SansSerif", Font.PLAIN, labelFont.getSize()));
        
        if (isSelected)
        {
            cellLabel.setForeground(table.getSelectionForeground());
            cellLabel.setBackground(table.getSelectionBackground());
        }
        else 
        {
            cellLabel.setForeground(table.getForeground());
            cellLabel.setBackground(table.getBackground());
        } 
        
        return cellLabel;       
    }
    
      
}
