package importing.api;

import org.gradoop.temporal.model.impl.pojo.TemporalEdge;
import org.gradoop.temporal.model.impl.pojo.TemporalVertex;

import java.util.ArrayList;

/**
 * Interface for implementations of importers of TPGM.
 */
public interface IImporter {
    /**
     * Gets the edges of the graph.
     * @return List of TemporalEdge
     */
    public ArrayList<TemporalEdge> getEdges();

    /**
     * Gets the vertices of the graph.
     * @return List of TemporalVertex
     */
    public ArrayList<TemporalVertex> getVertices();
}
