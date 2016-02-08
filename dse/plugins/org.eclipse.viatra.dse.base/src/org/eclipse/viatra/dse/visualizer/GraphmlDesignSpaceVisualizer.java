/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.visualizer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.ITransition;

/**
 * This class implements the {@link IDesignSpaceVisualizer} interface by storing the trace of the exploration into its
 * own data structure and exporting it to .graphml. It only supports
 * single threaded exploration.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class GraphmlDesignSpaceVisualizer implements IDesignSpaceVisualizer {

    private DesignSpaceVisualizerOptions options;
    private String fileName;
    private AtomicInteger numberOfThreads = new AtomicInteger(0);

    /**
     * Initializes a new instance of the {@link GraphmlDesignSpaceVisualizer} with custom options and file name to save.
     * 
     * @param options
     * @param fileName
     * @see GraphmlDesignSpaceVisualizer
     */
    public GraphmlDesignSpaceVisualizer(DesignSpaceVisualizerOptions options, String fileName) {
        this.options = options;
        this.fileName = fileName;
    }

    /**
     * Initializes a new instance of the {@link GraphmlDesignSpaceVisualizer} with custom options and the file name
     * "DSEDesignSpace.graphml".
     * 
     * @param options
     * @see GraphmlDesignSpaceVisualizer
     */
    public GraphmlDesignSpaceVisualizer(DesignSpaceVisualizerOptions options) {
        this(options, "DSEDesignSpace.graphml");
    }

    /**
     * Initializes a new instance of the {@link GraphmlDesignSpaceVisualizer} with custom file name and default options.
     * 
     * @param fileName
     * @see GraphmlDesignSpaceVisualizer
     */
    public GraphmlDesignSpaceVisualizer(String fileName) {
        this(new DesignSpaceVisualizerOptions(), fileName);
    }

    /**
     * Initializes a new instance of the {@link GraphmlDesignSpaceVisualizer} with default options and the file name
     * "DSEDesignSpace.graphml".
     * 
     * @param fileName
     * @see GraphmlDesignSpaceVisualizer
     */
    public GraphmlDesignSpaceVisualizer() {
        this(new DesignSpaceVisualizerOptions(), "DSEDesignSpace.graphml");
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public DesignSpaceVisualizerOptions getOptions() {
        return options;
    }

    @Override
    public void init(ThreadContext context) {
        if (!numberOfThreads.compareAndSet(0, 1)) {
            throw new DSEException("The registered visualizer " + getClass().getSimpleName()
                    + " cannot be used with multiple threads running.");
        }
    }

    @Override
    public void save() {

        PrintWriter out = null;

        StringBuilder sb = generateGraphml();

        try {
            out = new PrintWriter(fileName);
            out.write(sb.toString());
            out.flush();
        } catch (Exception e) {
            Logger.getLogger(getClass().getSimpleName()).error("Couldn't save to file.", e);
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    boolean isFirst = true;
    List<TraceElement> trace = new ArrayList<TraceElement>();
    Map<ITransition, StringBuilder> traceStrings = new HashMap<ITransition, StringBuilder>();

    class TraceElement {
        public ITransition transition;
        public boolean isUndo;

        public TraceElement(ITransition transition, boolean isUndo) {
            this.transition = transition;
            this.isUndo = isUndo;
        }

    }

    @Override
    public void transitionFired(ITransition transition) {

        trace.add(new TraceElement(transition, false));
        StringBuilder sb = traceStrings.get(transition);
        if (sb == null) {
            sb = new StringBuilder();
        }
        sb.append(trace.size());
        sb.append(',');
        traceStrings.put(transition, sb);
    }

    @Override
    public void undo(ITransition transition) {
        trace.add(new TraceElement(transition, true));
        StringBuilder sb = traceStrings.get(transition);
        sb.append(trace.size());
        sb.append("u,");
        traceStrings.put(transition, sb);

    }

    private StringBuilder generateGraphml() {
        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"
                + "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:y=\"http://www.yworks.com/xml/graphml\" xmlns:yed=\"http://www.yworks.com/xml/yed/3\" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd\">\r\n"
                + "  <key for=\"graphml\" id=\"d0\" yfiles.type=\"resources\"/>\r\n"
                + "  <key for=\"port\" id=\"d1\" yfiles.type=\"portgraphics\"/>\r\n"
                + "  <key for=\"port\" id=\"d2\" yfiles.type=\"portgeometry\"/>\r\n"
                + "  <key for=\"port\" id=\"d3\" yfiles.type=\"portuserdata\"/>\r\n"
                + "  <key attr.name=\"url\" attr.type=\"string\" for=\"node\" id=\"d4\"/>\r\n"
                + "  <key attr.name=\"description\" attr.type=\"string\" for=\"node\" id=\"d5\"/>\r\n"
                + "  <key for=\"node\" id=\"d6\" yfiles.type=\"nodegraphics\"/>\r\n"
                + "  <key attr.name=\"Description\" attr.type=\"string\" for=\"graph\" id=\"d7\"/>\r\n"
                + "  <key attr.name=\"url\" attr.type=\"string\" for=\"edge\" id=\"d8\"/>\r\n"
                + "  <key attr.name=\"description\" attr.type=\"string\" for=\"edge\" id=\"d9\"/>\r\n"
                + "  <key for=\"edge\" id=\"d10\" yfiles.type=\"edgegraphics\"/>\r\n"
                + "  <graph edgedefault=\"directed\" id=\"G\">\r\n" + "    <data key=\"d7\"/>\r\n");

        if (trace.isEmpty()) {
            sb.append("</graph>\r\n");
            sb.append("</graphml>\r\n");
            return sb;
        }
        
        IState rootState = trace.get(0).transition.getFiredFrom();
        addNode(sb, rootState);

        for (ITransition transition : traceStrings.keySet()) {
            addEdge(sb, transition);
        }

        sb.append("</graph>\r\n");
        sb.append("</graphml>\r\n");

        return sb;
    }

    int edgeIndex = 0;

    private void addEdge(StringBuilder sb, ITransition transition) {

        addNode(sb, transition.getResultsIn());
        sb.append("<edge id=\"e"
                + edgeIndex++
                + "\" source=\""
                + transition.getFiredFrom().getId()
                + "\" target=\""
                + transition.getResultsIn().getId()
                + "\">\r\n"
                + "      <data key=\"d10\">\r\n"
                + "        <y:PolyLineEdge>\r\n"
                + "          <y:LineStyle color=\"#000000\" type=\"line\" width=\"1.0\"/>\r\n"
                + "          <y:Arrows source=\"none\" target=\"standard\"/>\r\n"
                + "          <y:EdgeLabel alignment=\"center\" configuration=\"AutoFlippingLabel\" fontFamily=\"Dialog\" fontSize=\"12\" fontStyle=\"plain\" hasBackgroundColor=\"false\" hasLineColor=\"false\" modelName=\"custom\" preferredPlacement=\"anywhere\" ratio=\"0.5\" textColor=\"#000000\" visible=\"true\">"
                + (options.showExplorationTrace ? traceStrings.get(transition).toString() + "\r\n" : "")
                + (options.showTransitionCodes ? transition.getId() : "")
                + "<y:LabelModel>\r\n"
                + "              <y:SmartEdgeLabelModel autoRotationEnabled=\"false\" defaultAngle=\"0.0\" defaultDistance=\"10.0\"/>\r\n"
                + "            </y:LabelModel>\r\n"
                + "            <y:ModelParameter>\r\n"
                + "              <y:SmartEdgeLabelModelParameter angle=\"0.0\" distance=\"30.0\" distanceToCenter=\"true\" position=\"right\" ratio=\"0.5\" segment=\"0\"/>\r\n"
                + "            </y:ModelParameter>\r\n"
                + "            <y:PreferredPlacementDescriptor angle=\"0.0\" angleOffsetOnRightSide=\"0\" angleReference=\"absolute\" angleRotationOnRightSide=\"co\" distance=\"-1.0\" frozen=\"true\" placement=\"anywhere\" side=\"anywhere\" sideReference=\"relative_to_edge_flow\"/>\r\n"
                + "          </y:EdgeLabel>\r\n" + "          <y:BendStyle smoothed=\"false\"/>\r\n"
                + "        </y:PolyLineEdge>\r\n" + "      </data>\r\n" + "    </edge>");
    }

    private void addNode(StringBuilder sb, IState state) {

        sb.append("<node id=\""
                + state.getId()
                + "\">\r\n"
                + "      <data key=\"d6\">\r\n"
                + "        <y:ShapeNode>\r\n"
                + "          <y:Fill color=\"#FFCC00\" transparent=\"false\"/>\r\n"
                + "          <y:BorderStyle color=\"#000000\" type=\"line\" width=\"1.0\"/>\r\n"
                + "          <y:NodeLabel alignment=\"center\" autoSizePolicy=\"content\" fontFamily=\"Dialog\" fontSize=\"12\" fontStyle=\"plain\" hasBackgroundColor=\"false\" hasLineColor=\"false\" height=\"18.701171875\" modelName=\"custom\" textColor=\"#000000\" visible=\"true\" width=\"10.673828125\" x=\"9.6630859375\" y=\"5.6494140625\">"
                + (options.showStateCodes ? state.getId() : "")
                + "<y:LabelModel>\r\n"
                + "              <y:SmartNodeLabelModel distance=\"4.0\"/>\r\n"
                + "            </y:LabelModel>\r\n"
                + "            <y:ModelParameter>\r\n"
                + "              <y:SmartNodeLabelModelParameter labelRatioX=\"0.0\" labelRatioY=\"0.0\" nodeRatioX=\"0.0\" nodeRatioY=\"0.0\" offsetX=\"0.0\" offsetY=\"0.0\" upX=\"0.0\" upY=\"-1.0\"/>\r\n"
                + "            </y:ModelParameter>\r\n" + "          </y:NodeLabel>\r\n"
                + "          <y:Shape type=\"rectangle\"/>\r\n" + "        </y:ShapeNode>\r\n" + "      </data>\r\n"
                + "    </node>");

    }
}
