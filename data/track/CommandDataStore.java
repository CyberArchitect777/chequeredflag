/*
 * CommandDataStore.java
 *
 * Created on 14 August 2006, 04:31
 */

package chequeredflag.data.track;

import java.util.ArrayList;

/**
 *
 * @author barrie
 */
public class CommandDataStore 
{
    
    private ArrayList commandCollection;
    
    /** Creates a new instance of CommandDataStore */
    public CommandDataStore() 
    {
        // Initialises the command data collection
        
        commandCollection = new ArrayList();
        populateDataStore();
        
    }
    
    public ArrayList createCollectionFromArray(String[] stringData)
    {
        ArrayList newCollection = new ArrayList();
        for (int x=0;x<stringData.length;x++)
        {
            newCollection.add(stringData[x]);
        }
        return newCollection;
    }
    
    public ArrayList createCollectionFromArray(String stringData)
    {
        ArrayList newCollection = new ArrayList();
        newCollection.add(stringData);
        return newCollection;
    }
    
    public CommandData getCommandByIndex(int commandIndex)
    {
        CommandData selectedCommand = (CommandData)commandCollection.get(commandIndex);
        return selectedCommand;        
    }
    
    public CommandData getCommandByType(int commandType)
    {
        CommandData selectedCommand = (CommandData)commandCollection.get(0);
        
        for (int x=0;x<commandCollection.size();x++)
        {
            CommandData currentCommand = (CommandData)commandCollection.get(x);
            if (currentCommand.getCommandType() == commandType)
            {
                selectedCommand = currentCommand;
            }
        }
        
        return selectedCommand;
    }
    
    public int getCommandListSize()
    {
        return commandCollection.size();
    }
    
    public void populateDataStore()
    {
        // Populates the data store with all known track segment
        // command information.
               
        commandCollection.add(new CommandData(0x80, createCollectionFromArray("Unknown"), "Object placement"));
        commandCollection.add(new CommandData(0x81, createCollectionFromArray("Unknown"), "Display distance front"));
        commandCollection.add(new CommandData(0x82, createCollectionFromArray("Unknown"), "Display distance back"));
        commandCollection.add(new CommandData(0x83, new ArrayList(), "Disable drawing of background picture"));
        commandCollection.add(new CommandData(0x84, new ArrayList(), "Enable drawing of background picture"));
        String[] paramEightFive = {"Unknown", "Unknown"};
        commandCollection.add(new CommandData(0x85, createCollectionFromArray(paramEightFive), "Track width change"));
        commandCollection.add(new CommandData(0x86, new ArrayList(), "Connect pitline start"));
        commandCollection.add(new CommandData(0x87, new ArrayList(), "Connect pitlane end"));
        commandCollection.add(new CommandData(0x88, createCollectionFromArray("Length of pit parking zone marking 1?"), "Pit parking zone marking 1"));
        commandCollection.add(new CommandData(0x89, createCollectionFromArray("Length of pit parking zone marking 2?"), "Pit parking zone marking 2"));
        String[] paramEightA = {"Unknown", "Length of track markings?", "Unknown", "Unknown", "Unknown"};
        commandCollection.add(new CommandData(0x8A, createCollectionFromArray(paramEightA), "Track markings"));
        String[] paramEightB = {"Unknown", "Width of grid markings/length of grid slots?", "Unknown", "Unknown", "Unknown"};
        commandCollection.add(new CommandData(0x8B, createCollectionFromArray(paramEightB), "Starting grid markings"));
        commandCollection.add(new CommandData(0x8C, createCollectionFromArray("Length of unknown element change on the left?"), "Changes unknown element on the left")); 
        commandCollection.add(new CommandData(0x8D, createCollectionFromArray("Length of unknown element change on the right?"), "Changes unknown element on the right"));
        String[] paramEightE = {"Unknown", "Unknown"};
        commandCollection.add(new CommandData(0x8E, createCollectionFromArray(paramEightE), "Left kerb starts"));
        String[] paramEightF = {"Unknown", "Unknown"};
        commandCollection.add(new CommandData(0x8F, createCollectionFromArray(paramEightF), "Right kerb starts"));
        commandCollection.add(new CommandData(0x90, createCollectionFromArray("Unknown"), "Reverse object placement?"));
        commandCollection.add(new CommandData(0x91, createCollectionFromArray("Unknown"), "Unknown"));
        commandCollection.add(new CommandData(0x92, createCollectionFromArray("Length of zone in which unknown action 1 occurs?"), "Segment in which unknown action 1 is taken?"));
        commandCollection.add(new CommandData(0x93, createCollectionFromArray("Length of zone in which unknown action 2 occurs?"), "Segment in which unknown action 2 is taken?"));
        commandCollection.add(new CommandData(0x94, createCollectionFromArray("Left coaching factor? (1-8)"), "Computer car coaching left"));
        commandCollection.add(new CommandData(0x95, createCollectionFromArray("Right coaching factor? (1-8)"), "Computer car coaching right"));
        commandCollection.add(new CommandData(0x96, new ArrayList(), "Pitlane start"));
        commandCollection.add(new CommandData(0x97, new ArrayList(), "Pitlane end"));
        commandCollection.add(new CommandData(0x98, createCollectionFromArray("Left fence height modification amount (1-8)"), "Left fence height change"));
        commandCollection.add(new CommandData(0x99, createCollectionFromArray("Right fence height modification amount (1-8)"), "Right fence height change"));
        String[] paramNineA = {"Fence position in comparison to the track? (Left=1-8, Right=9-16)", "Fence height"};
        commandCollection.add(new CommandData(0x9A, createCollectionFromArray(paramNineA), "Custom fence height"));
        commandCollection.add(new CommandData(0x9B, new ArrayList(), "Unknown pitlane marker 1"));
        commandCollection.add(new CommandData(0x9C, new ArrayList(), "Unknown pitlane marker 2"));
        commandCollection.add(new CommandData(0x9D, new ArrayList(), "Unknown pitlane marker 3"));
        commandCollection.add(new CommandData(0x9E, new ArrayList(), "Unknown pitlane marker 4"));
        commandCollection.add(new CommandData(0x9F, new ArrayList(), "Pitlane fences starts"));
        commandCollection.add(new CommandData(0xA0, new ArrayList(), "Pitlane fences end"));
        commandCollection.add(new CommandData(0xA1, new ArrayList(), "Pit lane entry, join right pit lane fence"));
        commandCollection.add(new CommandData(0xA2, new ArrayList(), "Pit lane entry, join left pit lane fence"));
        commandCollection.add(new CommandData(0xA3, new ArrayList(), "Pit lane exit, join right pit lane fence"));
        commandCollection.add(new CommandData(0xA4, new ArrayList(), "Pit lane exit, join left pit lane fence"));
        commandCollection.add(new CommandData(0xA5, new ArrayList(), "Change the sign of the first two sector arguments"));
        String[] paramASix = {"Unknown", "Unknown"};
        commandCollection.add(new CommandData(0xA6, createCollectionFromArray(paramASix), "Set unknown segment flags 1"));
        String[] paramASeven = {"Unknown", "Unknown"};
        commandCollection.add(new CommandData(0xA7, createCollectionFromArray(paramASeven), "Set unknown segment flags 2"));
        commandCollection.add(new CommandData(0xA8, new ArrayList(), "Segment at which the chequered flag is shown."));
        commandCollection.add(new CommandData(0xA9, createCollectionFromArray("Distance in which the pitlane can be seen? (Default=60)"), "View distance for pitlane (Optional)"));
        String[] paramAA = {"Length for computer cars to angle towards pitlane (backwards)?", "Length for computer cars to angle out of pits?", "Computer car pitlane speed?"};
        commandCollection.add(new CommandData(0xAA, createCollectionFromArray(paramAA), "Computer cars pitlane modifier?"));
        String[] paramAB = {"Unknown, will crash if set to 42", "Unknown"};
        commandCollection.add(new CommandData(0xAB, createCollectionFromArray(paramAB), "Unknown, may be unused"));
        String[] paramAC = {"Palette Index (0-255)", "Red Colour Value? (0-63?)", "Green Colour Value (0-63?)", "Blue Colour Value (0-63?)"};
        commandCollection.add(new CommandData(0xAC, createCollectionFromArray(paramAC), "Palette Change"));
        
    }
}
        
       
