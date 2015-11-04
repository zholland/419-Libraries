package core.stocks;

import core.components.Edge;
import core.components.Vertex;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.*;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;


/**
 * Create a JFrame to visualize the visibility graph.
 * By Michael Nowicki
 */
public class VisibilityGraphViewer {

    /**
     * @param graph The graph to visualize
     */
    public static void viewGraph(Graph<Vertex, Edge> graph) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();

		Layout<Vertex, Edge> layout = new ISOMLayout<>(graph);
		layout.setSize(new Dimension((int)width, (int)height)); // sets the initial size of the space
		
		VisualizationViewer<Vertex, Edge> vv =	new VisualizationViewer<>(layout);
		vv.setPreferredSize(new Dimension((int)width, (int)height)); //Sets the viewing area size

		Transformer<Vertex, Paint> vertexPaint = v -> Color.RED;

		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());

		// Set structure of JUNG pane and mouse control.
		GraphZoomScrollPane zoomPane = new GraphZoomScrollPane(vv);

		PluggableGraphMouse pm = new PluggableGraphMouse();
		pm.add(new PickingGraphMousePlugin<Vertex, Edge>());
		pm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1 / 1.1f, 1.1f));   
		pm.add(new TranslatingGraphMousePlugin());      

		vv.setGraphMouse(pm);

		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
				
		JMenuBar menuPanel = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem exit = new JMenuItem("Exit");
		
		exit.addActionListener(e -> {
            frame.setVisible(false);
            frame.dispose();
        });
		
		file.add(exit);	
		menuPanel.add(file);
		frame.setJMenuBar(menuPanel);

		frame.getContentPane().add(zoomPane);
		frame.setContentPane(vv);
		
		frame.pack();
		frame.setVisible(true); 

	}
	
}
