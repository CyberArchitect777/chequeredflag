/*
 * StandardTableModel.java
 *
 * Created on 14 June 2005, 02:15
 */

package chequeredflag.gui;
import javax.swing.table.*;

/**
 *
 * @author barrie
 */
public class StandardTableModel extends AbstractTableModel
{
    
    protected Object[][] tableData;
    
    /** Creates a new instance of StandardTableModel */
    public StandardTableModel() 
    {
    }
    
    public void tableDimension(int rowNumber, int columnNumber)
    {
        tableData = new Object[rowNumber][columnNumber];
    }
    
    public int getRowCount()
    {
        return tableData.length;        
    }
    
    public int getColumnCount()
    {
        return tableData[0].length;
    }
    
    public Class getColumnClass(int columnIndex)
    {
        return tableData[0][columnIndex].getClass();
    }
    
    public Object getValueAt(int row, int column)
    {
        return tableData[row][column];
    }
    
    public void setValueAt(Object aValue, int row, int column)
    {
        if (aValue instanceof Boolean)
        {
            if (((Boolean)aValue).booleanValue() == true)
            {
                tableData[row][column] = new Boolean(true);
            }
            else
            {
                tableData[row][column] = new Boolean(false);
            }
        }
        else
        {
            tableData[row][column] = aValue;
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
