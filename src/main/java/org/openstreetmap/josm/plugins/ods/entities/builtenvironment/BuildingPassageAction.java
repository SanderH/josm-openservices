package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

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

    // protected static void warnCombiningImpossible() {
    // String msg = tr("Could not combine ways<br>"
    // + "(They could not be merged into a single string of nodes)");
    // new Notification(msg)
    // .setIcon(JOptionPane.INFORMATION_MESSAGE)
    // .show();
    // return;
    // }

    // protected static Way getTargetWay(Collection<Way> combinedWays) {
    // // init with an arbitrary way
    // Way targetWay = combinedWays.iterator().next();
    //
    // // look for the first way already existing on
    // // the server
    // for (Way w : combinedWays) {
    // targetWay = w;
    // if (!w.isNew()) {
    // break;
    // }
    // }
    // return targetWay;
    // }

//    /**
//     * @param ways
//     * @return null if ways cannot be combined. Otherwise returns the combined
//     *         ways and the commands to combine
//     * @throws UserCancelException
//     */
//    public static Pair<Way, Command> combineWaysWorker(Collection<Way> ways)
//            throws UserCancelException {
//
//        // prepare and clean the list of ways to combine
//        //
//        if (ways == null || ways.isEmpty())
//            return null;
//        ways.remove(null); // just in case - remove all null ways from the
//                           // collection
//
//        // remove duplicates, preserving order
//        ways = new LinkedHashSet<Way>(ways);
//
//        // try to build a new way which includes all the combined
//        // ways
//        //
//        NodeGraph graph = NodeGraph.createUndirectedGraphFromNodeWays(ways);
//        List<Node> path = graph.buildSpanningPath();
//        if (path == null) {
//            warnCombiningImpossible();
//            return null;
//        }
//        // check whether any ways have been reversed in the process
//        // and build the collection of tags used by the ways to combine
//        //
//        TagCollection wayTags = TagCollection.unionOfAllPrimitives(ways);
//
//        List<Way> reversedWays = new LinkedList<Way>();
//        List<Way> unreversedWays = new LinkedList<Way>();
//        for (Way w : ways) {
//            // Treat zero or one-node ways as unreversed as Combine action
//            // action is a good way to fix them (see #8971)
//            if (w.getNodesCount() < 2
//                    || (path.indexOf(w.getNode(0)) + 1) == path.lastIndexOf(w
//                            .getNode(1))) {
//                unreversedWays.add(w);
//            } else {
//                reversedWays.add(w);
//            }
//        }
//        // reverse path if all ways have been reversed
//        if (unreversedWays.isEmpty()) {
//            Collections.reverse(path);
//            unreversedWays = reversedWays;
//            reversedWays = null;
//        }
//        if ((reversedWays != null) && !reversedWays.isEmpty()) {
//            if (!confirmChangeDirectionOfWays())
//                return null;
//            // filter out ways that have no direction-dependent tags
//            unreversedWays = ReverseWayTagCorrector
//                    .irreversibleWays(unreversedWays);
//            reversedWays = ReverseWayTagCorrector
//                    .irreversibleWays(reversedWays);
//            // reverse path if there are more reversed than unreversed ways with
//            // direction-dependent tags
//            if (reversedWays.size() > unreversedWays.size()) {
//                Collections.reverse(path);
//                List<Way> tempWays = unreversedWays;
//                unreversedWays = reversedWays;
//                reversedWays = tempWays;
//            }
//            // if there are still reversed ways with direction-dependent tags,
//            // reverse their tags
//            if (!reversedWays.isEmpty() && PROP_REVERSE_WAY.get()) {
//                List<Way> unreversedTagWays = new ArrayList<Way>(ways);
//                unreversedTagWays.removeAll(reversedWays);
//                ReverseWayTagCorrector reverseWayTagCorrector = new ReverseWayTagCorrector();
//                List<Way> reversedTagWays = new ArrayList<Way>(
//                        reversedWays.size());
//                Collection<Command> changePropertyCommands = null;
//                for (Way w : reversedWays) {
//                    Way wnew = new Way(w);
//                    reversedTagWays.add(wnew);
//                    changePropertyCommands = reverseWayTagCorrector.execute(w,
//                            wnew);
//                }
//                if ((changePropertyCommands != null)
//                        && !changePropertyCommands.isEmpty()) {
//                    for (Command c : changePropertyCommands) {
//                        c.executeCommand();
//                    }
//                }
//                wayTags = TagCollection.unionOfAllPrimitives(reversedTagWays);
//                wayTags.add(TagCollection
//                        .unionOfAllPrimitives(unreversedTagWays));
//            }
//        }
//
//        // create the new way and apply the new node list
//        //
//        Way targetWay = getTargetWay(ways);
//        Way modifiedTargetWay = new Way(targetWay);
//        modifiedTargetWay.setNodes(path);
//
//        List<Command> resolution = CombinePrimitiveResolverDialog
//                .launchIfNecessary(wayTags, ways,
//                        Collections.singleton(targetWay));
//
//        LinkedList<Command> cmds = new LinkedList<Command>();
//        LinkedList<Way> deletedWays = new LinkedList<Way>(ways);
//        deletedWays.remove(targetWay);
//
//        cmds.add(new ChangeCommand(targetWay, modifiedTargetWay));
//        cmds.addAll(resolution);
//        cmds.add(new DeleteCommand(deletedWays));
//        final SequenceCommand sequenceCommand = new SequenceCommand(/*
//                                                                     * for
//                                                                     * correct
//                                                                     * i18n of
//                                                                     * plural
//                                                                     * forms -
//                                                                     * see #9110
//                                                                     */
//        trn("Combine {0} way", "Combine {0} ways", ways.size(), ways.size()),
//                cmds);
//
//        return new Pair<Way, Command>(targetWay, sequenceCommand);
//    }

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
