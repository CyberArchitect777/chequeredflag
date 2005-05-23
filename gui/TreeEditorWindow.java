/*
 * TreeEditorWindow.java
 *
 * Created on 24 March 2005, 01:26
 */

package chequeredflag.gui;

import chequeredflag.data.track.*;
import javax.swing.tree.*;
import javax.swing.*;

/**
 *
 * @author  barrie
 */
public class TreeEditorWindow extends javax.swing.JInternalFrame {
    
    private DefaultMutableTreeNode rootNode;
    private Track currentTrack;
    private JDesktopPane parentFrame;
        
    /** Creates new form TreeEditorWindow */
    public TreeEditorWindow(JDesktopPane containerFrame) {
        initComponents();
        setDefaultElement();    
        createTree();
        parentFrame = containerFrame;        
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
        buttonPanel = new javax.swing.JPanel();
        addAboveButton = new javax.swing.JButton();
        addBelowButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();

        setTitle("Object View");
        trackDetails.setShowsRootHandles(true);
        trackDetails.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                setSelectedPaths(evt);
            }
        });

        treeViewScroll.setViewportView(trackDetails);

        getContentPane().add(treeViewScroll, java.awt.BorderLayout.CENTER);

        buttonPanel.setLayout(new java.awt.GridLayout(1, 3));

        addAboveButton.setText("Add Above");
        buttonPanel.add(addAboveButton);

        addBelowButton.setText("Add Below");
        buttonPanel.add(addBelowButton);

        editButton.setText("Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editObject(evt);
            }
        });

        buttonPanel.add(editButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }//GEN-END:initComponents

    private void editObject(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editObject
        // Edit the current object selected on the tree. Called natively by Edit button
        
        TreePath fullPath = trackDetails.getSelectionPath();
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)fullPath.getLastPathComponent();
        if (selectedNode.getLevel() != 0)
        {
            TreePath parentPath = fullPath.getParentPath();
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)parentPath.getLastPathComponent();
            if (parentNode.toString() == "Track Segments")
            {
                int spaceIndex = ((selectedNode.toString()).indexOf(" "));
                int segmentNo = new Integer((selectedNode.toString()).substring(0,spaceIndex)).intValue();
                TrackSegments currentTrackSegments = currentTrack.getTrackSegments();
                TrackSegment selectedTrackSegment = currentTrackSegments.getAt(segmentNo);
                SegmentEditGUI segmentEditor = new SegmentEditGUI();
                segmentEditor.editTrackSegment(selectedTrackSegment);
                segmentEditor.setVisible(true);
                segmentEditor.setTitle("Editing Track Segment " + segmentNo);
                parentFrame.add(segmentEditor);
                segmentEditor.toFront();
            }
        }
        
    }//GEN-LAST:event_editObject

    private void setSelectedPaths(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_setSelectedPaths
        // Gets the full path of the tree node upon selection by the user.
        
        TreePath fullPath = trackDetails.getSelectionPath();
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)fullPath.getLastPathComponent();
        if (selectedNode.getLevel() != 0)
        {
            TreePath parentPath = fullPath.getParentPath();
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)parentPath.getLastPathComponent();
            if (parentNode.toString() == "Track Segments")
            {
                int spaceIndex = ((selectedNode.toString()).indexOf(" "));
                int segmentNo = new Integer((selectedNode.toString()).substring(0,spaceIndex)).intValue();
            }
        }
    }//GEN-LAST:event_setSelectedPaths
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAboveButton;
    private javax.swing.JButton addBelowButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton editButton;
    private javax.swing.JTree trackDetails;
    private javax.swing.JScrollPane treeViewScroll;
    // End of variables declaration//GEN-END:variables
    
}
