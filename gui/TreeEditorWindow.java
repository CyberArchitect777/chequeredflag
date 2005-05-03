/*
 * TreeEditorWindow.java
 *
 * Created on 24 March 2005, 01:26
 */

package chequeredflag.gui;

import chequeredflag.data.track.*;
import javax.swing.tree.*;

/**
 *
 * @author  barrie
 */
public class TreeEditorWindow extends javax.swing.JInternalFrame {
    
    private DefaultMutableTreeNode rootNode;
    private Track currentTrack;
    
    /** Creates new form TreeEditorWindow */
    public TreeEditorWindow() {
        initComponents();
        setDefaultElement();    
        createTree();
    }
    
    public void populateTree()
    {
        // Creates tree elements from information obtained from the current track
        
        DefaultMutableTreeNode mainTrackSegmentNode = new DefaultMutableTreeNode("Track Segments");
        DefaultMutableTreeNode mainPitSegmentNode = new DefaultMutableTreeNode("Pit Segments");
        DefaultMutableTreeNode mainLineSegmentNode = new DefaultMutableTreeNode("Best Line Segments");
        rootNode.add(mainTrackSegmentNode);
        rootNode.add(mainPitSegmentNode);
        rootNode.add(mainLineSegmentNode);
        TrackSegments currentTrackSegments = currentTrack.getTrackSegments();
        DefaultMutableTreeNode[] trackSegmentNode = new DefaultMutableTreeNode[currentTrackSegments.size()];
        for (int x=0;x<currentTrackSegments.size();x++)
        {
            TrackSegment selectedTrackSegment = currentTrackSegments.getAt(x+1);
            trackSegmentNode[x] = new DefaultMutableTreeNode(new Integer(x+1).toString() + " " + getSegmentDirection(selectedTrackSegment));
            mainTrackSegmentNode.add(trackSegmentNode[x]);
        }       
        TrackSegments currentPitSegments = currentTrack.getPitlaneSegments();
        DefaultMutableTreeNode[] pitSegmentNode = new DefaultMutableTreeNode[currentPitSegments.size()];
        for (int x=0;x<currentPitSegments.size();x++)
        {
            TrackSegment selectedPitSegment = currentPitSegments.getAt(x+1);
            pitSegmentNode[x] = new DefaultMutableTreeNode(new Integer(x+1).toString() + " " + getSegmentDirection(selectedPitSegment));
            mainPitSegmentNode.add(pitSegmentNode[x]);
        }    
        CCLine currentLineSegments = currentTrack.getCCLine();
        DefaultMutableTreeNode[] lineSegmentNode = new DefaultMutableTreeNode[currentLineSegments.size()];
        for (int x=0;x<currentLineSegments.size();x++)
        {
            CCLineSegment selectedLineSegment = currentLineSegments.getAt(x+1);
            lineSegmentNode[x] = new DefaultMutableTreeNode(new Integer(x+1).toString() + " " + getBestLineDirection(selectedLineSegment));
            mainLineSegmentNode.add(lineSegmentNode[x]);
        }
    }
    
    public String getBestLineDirection(CCLineSegment currentSegment)
    {
        
        // Returns the CC Line direction from the passed segment
        
        System.out.println(currentSegment.getType() + " " + currentSegment.getParam(0) + " " + currentSegment.getParam(1));
        if (currentSegment.getType() == 128) // 0x80 in hexidecimal 
        {
            if (currentSegment.getParam(1) == 0)
            {
                return "Straight";
            }
            else
            {
                if (currentSegment.getParam(1) > 32768)
                {
                    return "Turn Left";
                }
                else
                {
                    return "Turn Right";
                }
            }
        }
        else
        {
            if (currentSegment.getParam(0) == 0)
            {
                return "Straight";
            }
            else
            {
                if (currentSegment.getParam(0) > 32768)
                {
                    return "Turn Left";
                }
                else
                {
                    return "Turn Right";
                }
            }
            
        }
    }
    
    public String getSegmentDirection(TrackSegment currentSegment)
    {
        
        // Returns the direction from the passed segment
        
        if (currentSegment.getCurvature() < 0)
        {
            return "Turn Left";
        }
        else
        {
            if (currentSegment.getCurvature() > 0)
            {
                return "Turn Right";
            }
            else
            {
                return "Straight";
            }
        }
    }
    
    public void setTrack(Track selectedTrack)
    {
        // Passes the current track 
        
        currentTrack = selectedTrack;
    }
    
    private void setDefaultElement()
    {
        // Adds a single root node to the tree as part of initial configuration.
        rootNode = new DefaultMutableTreeNode("Track Data");        
    }
    
    private void createTree()
    {
        // Writes all current tree nodes to the tree interface
        
        trackDetails.setModel(new DefaultTreeModel(rootNode));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        treeViewScroll = new javax.swing.JScrollPane();
        trackDetails = new javax.swing.JTree();

        setTitle("Object View");
        trackDetails.setShowsRootHandles(true);
        treeViewScroll.setViewportView(trackDetails);

        getContentPane().add(treeViewScroll, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree trackDetails;
    private javax.swing.JScrollPane treeViewScroll;
    // End of variables declaration//GEN-END:variables
    
}
