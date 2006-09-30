/*
 * CommandData.java
 *
 * Created on 14 August 2006, 04:02
 */

package chequeredflag.data.track;

import java.util.ArrayList;

/**
 *
 * @author barrie
 */
public class CommandData {
    
    private int commandType;
    private ArrayList paramInfo;
    private String commandDesc;
    
        
    /** Creates a new instance of CommandData */
    public CommandData(int newCommandType, ArrayList newParamInfo, String newCommandDesc)
    {
        // Constructor. Initialises a new CommandData object.
        
        commandType = newCommandType;
        paramInfo = newParamInfo;
        commandDesc = newCommandDesc;
    }
    
    public int getNumberOfParams()
    {
        // Returns the number of parameters contained by this command.
        
        return paramInfo.size();
    }
    
    public int getCommandType()
    {
        // Returns an integer value of the command type.
        
        return commandType;
    }
    
    public String getParamInfo(int paramIndex)
    {
        // Returns the specified command parameter.
        
        return (String)paramInfo.get(paramIndex);
    }
    
    public String[] getParamArray()
    {
        // Returns a string array containing all command parameter information.
        
        String[] paramText = new String[paramInfo.size()];
        for (int x=0;x<paramInfo.size();x++)
        {
            paramText[x] = (String)paramInfo.get(x);
        }
        
        return paramText;        
    }
    
    public String getCommandDesc()
    {
        // Returns the command description.
        
        return commandDesc;
    }
    
}
