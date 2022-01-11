package importing.api;

import org.gradoop.temporal.model.impl.pojo.TemporalEdge;
import org.gradoop.temporal.model.impl.pojo.TemporalVertex;

import java.util.ArrayList;

public interface IImporter {
    public ArrayList<TemporalEdge> getEdges();
    public ArrayList<TemporalVertex> getVertices();
}
