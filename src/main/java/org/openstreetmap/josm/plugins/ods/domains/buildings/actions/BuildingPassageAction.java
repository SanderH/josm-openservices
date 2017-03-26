package org.openstreetmap.josm.plugins.ods.domains.buildings.actions;

import static org.openstreetmap.josm.gui.help.HelpUtil.ht;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.SplitWayAction;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.ChangeNodesCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.MainLayerManager.ActiveLayerChangeEvent;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.gui.OdsAction;
import org.openstreetmap.josm.tools.Geometry;
import org.openstreetmap.josm.tools.I18n;

/**
 * Creates a building passage for a highway crossing a building
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 * 
 */
public class BuildingPassageAction extends OdsAction {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@code CombineWayAction}.
     */
    public BuildingPassageAction(OdsModule module) {
        super(module, tr("Building passage"),
            tr("Create a tunnel=building_building passage for a highway crossing a building."));
        putValue("help", ht("/Action/BuildingPassage"));
    }


    @Override
    public void actionPerformed(ActionEvent event) {
        DataSet dataSet = Main.getLayerManager().getEditDataSet();
        if (dataSet == null) return;
        Collection<OsmPrimitive> selection = dataSet.getSelected();
        BuildingHighwayPair pair = getPair(selection);
        if (pair == null) {
            new Notification(tr("Please select exactly 1 building and 1 highway."))
                    .setIcon(JOptionPane.INFORMATION_MESSAGE)
                    .setDuration(Notification.TIME_SHORT).show();
            return;
        }
        List<Command> cmds = new LinkedList<>();
        Set<Node> nodes = Geometry.addIntersections(pair.getWays(), false, cmds);
        if (nodes.size() != 2) {
            new Notification(tr("The building and the highway should intersect at exactly 2 point."))
            .setIcon(JOptionPane.INFORMATION_MESSAGE)
            .setDuration(Notification.TIME_SHORT).show();
            return;
        }
        Command intersectionsCommand = new SequenceCommand(I18n.tr("Split way"), cmds);
        intersectionsCommand.executeCommand();
        cmds.clear();
        final Way highway = pair.getHighway();
        final List<List<Node>> wayChunks = SplitWayAction.buildSplitChunks(highway, new ArrayList<>(nodes));
        assert wayChunks.size() == 3;
        final List<Node> passageChunk = wayChunks.get(1);
        final List<Node> longChunk;
        final List<Node> shortChunk;
        if (wayChunks.get(0).size() > wayChunks.get(2).size()) {
            longChunk = wayChunks.get(0);
            shortChunk = wayChunks.get(2);
        }
        else {
            longChunk = wayChunks.get(2);
            shortChunk = wayChunks.get(0);
        }
        // Add a command to update the existing highway
        cmds.add(new ChangeNodesCommand(highway, longChunk));
        Way shortWay = new Way();
        shortWay.setNodes(shortChunk);
        shortWay.setKeys(highway.getKeys());
        Way passageWay = new Way();
        passageWay.setNodes(passageChunk);
        passageWay.setKeys(highway.getKeys());
        passageWay.put("tunnel", "building_passage");
        cmds.add(new AddCommand(shortWay));
        cmds.add(new AddCommand(passageWay));
        // Undo the intersections command, so we can add it to the combined command
        intersectionsCommand.undoCommand();
        cmds.add(0, intersectionsCommand);
        Command cmd = new SequenceCommand(I18n.tr("Add building passage"), cmds);
        Main.main.undoRedo.add(cmd);
    }

//    @Override
//    protected void updateEnabledState() {
//        DataSet dataSet = Main.getLayerManager().getEditDataSet();
//        if (dataSet == null) {
//            setEnabled(false);
//            return;
//        }
//        Collection<OsmPrimitive> selection = dataSet.getSelected();
//        updateEnabledState(selection);
//    }
//
//    @Override
//    protected void updateEnabledState(
//            Collection<? extends OsmPrimitive> selection) {
//        BuildingHighwayPair pair = getPair(selection);
//        setEnabled(pair != null);
//    }

    /**
     * Get the selected building and highway.
     * 
     * @param selection
     * @return
     * A BuildingHighwayPair if exactly 1 building and 1 higway are selected.
     * null otherwise.
     */
    protected BuildingHighwayPair getPair(
            Collection<? extends OsmPrimitive> selection) {
        if (selection.size() != 2) {
            return null;
        }
        Way building = null;
        Way highway = null;
        for (OsmPrimitive osm : selection) {
            if (osm instanceof Way) {
                if (osm.hasKey("building")) {
                    building = (Way) osm;
                    if (!building.isClosed()) {
                        building = null;
                    }
                }
                if (osm.hasKey("highway")) {
                    highway = (Way) osm;
                }
            }
        }
        if (building == null || highway == null) {
            return null;
        }
        return new BuildingHighwayPair(building, highway);
    }
    
    @Override
    public void activeOrEditLayerChanged(ActiveLayerChangeEvent e) {
        Layer newLayer = Main.getLayerManager().getActiveLayer();
        LayerManager layerManager = getModule().getLayerManager(newLayer);
        this.setEnabled(layerManager != null && layerManager.isOsm());
    }

    
    /**
     * The building and highway to create a building passage for.
     * 
     * @author Gertjan Idema <mail@gertjanidema.nl>
     *
     */
    private class BuildingHighwayPair {
        private final ArrayList<Way> ways = new ArrayList<>(2);

        public BuildingHighwayPair(Way building, Way highway) {
            super();
            ways.add(building);
            ways.add(highway);
        }

        public Way getBuilding() {
            return ways.get(0);
        }

        public Way getHighway() {
            return ways.get(1);
        }

        public List<Way> getWays() {
            return ways;
        }
    }
}
