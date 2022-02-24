import { TemporalEdge } from "./temporal-edge.model";
import { TemporalGraph } from "./temporal-graph.model";
import { TemporalVertex } from "./temporal-vertex.model";

export class TestGraph extends TemporalGraph {
	public constructor() {
		super([], []);
		this.buildGraph();
	}

	private buildGraph() {
		const vertices: TemporalVertex[] = [];
		vertices.push(new TemporalVertex("A", -1, -1));
		vertices.push(new TemporalVertex("B", -1, -1));
		vertices.push(new TemporalVertex("C", -1, -1));
		vertices.push(new TemporalVertex("D", -1, -1));
		vertices.push(new TemporalVertex("E", -1, -1));
		vertices.push(new TemporalVertex("F", -1, -1));
		vertices.push(new TemporalVertex("G", -1, -1));
		vertices.push(new TemporalVertex("H", -1, -1));
		vertices.push(new TemporalVertex("I", -1, -1));
		vertices.push(new TemporalVertex("J", -1, -1));
		this.vertices = vertices;

		const edges: TemporalEdge[] = [];
		// 1+
		edges.push(new TemporalEdge("Edge 1", TestGraph.find(vertices, "E"), TestGraph.find(vertices, "F"), 7, 12));
		edges.push(new TemporalEdge("Edge 2", TestGraph.find(vertices, "E"), TestGraph.find(vertices, "A"), 13, 41));
		edges.push(new TemporalEdge("Edge 3", TestGraph.find(vertices, "C"), TestGraph.find(vertices, "A"), 26, 39));
		edges.push(new TemporalEdge("Edge 4", TestGraph.find(vertices, "H"), TestGraph.find(vertices, "B"), 3, 33));
		edges.push(new TemporalEdge("Edge 5", TestGraph.find(vertices, "J"), TestGraph.find(vertices, "H"), 5, 15));
		edges.push(new TemporalEdge("Edge 6", TestGraph.find(vertices, "G"), TestGraph.find(vertices, "D"), 32, 38));
		edges.push(new TemporalEdge("Edge 7", TestGraph.find(vertices, "E"), TestGraph.find(vertices, "E"), 3, 34));
		edges.push(new TemporalEdge("Edge 8", TestGraph.find(vertices, "F"), TestGraph.find(vertices, "J"), 16, 31));
		edges.push(new TemporalEdge("Edge 9", TestGraph.find(vertices, "H"), TestGraph.find(vertices, "I"), 13, 27));
		edges.push(new TemporalEdge("Edge 10", TestGraph.find(vertices, "D"), TestGraph.find(vertices, "I"), 22, 30));
		// 11+
		edges.push(new TemporalEdge("Edge 11", TestGraph.find(vertices, "A"), TestGraph.find(vertices, "H"), 14, 27));
		edges.push(new TemporalEdge("Edge 12", TestGraph.find(vertices, "D"), TestGraph.find(vertices, "I"), 20, 40));
		edges.push(new TemporalEdge("Edge 13", TestGraph.find(vertices, "B"), TestGraph.find(vertices, "H"), 17, 44));
		edges.push(new TemporalEdge("Edge 14", TestGraph.find(vertices, "I"), TestGraph.find(vertices, "A"), 32, 38));
		edges.push(new TemporalEdge("Edge 15", TestGraph.find(vertices, "H"), TestGraph.find(vertices, "H"), 3, 23));
		edges.push(new TemporalEdge("Edge 16", TestGraph.find(vertices, "B"), TestGraph.find(vertices, "I"), 12, 21));
		edges.push(new TemporalEdge("Edge 17", TestGraph.find(vertices, "J"), TestGraph.find(vertices, "F"), 27, 39));
		edges.push(new TemporalEdge("Edge 18", TestGraph.find(vertices, "D"), TestGraph.find(vertices, "G"), 22, 34));
		edges.push(new TemporalEdge("Edge 19", TestGraph.find(vertices, "A"), TestGraph.find(vertices, "A"), 8, 32));
		edges.push(new TemporalEdge("Edge 20", TestGraph.find(vertices, "C"), TestGraph.find(vertices, "D"), 28, 36));
		// 21+
		edges.push(new TemporalEdge("Edge 21", TestGraph.find(vertices, "F"), TestGraph.find(vertices, "I"), 19, 32));
		edges.push(new TemporalEdge("Edge 22", TestGraph.find(vertices, "J"), TestGraph.find(vertices, "A"), 5, 43));
		edges.push(new TemporalEdge("Edge 23", TestGraph.find(vertices, "E"), TestGraph.find(vertices, "H"), 3, 6));
		edges.push(new TemporalEdge("Edge 24", TestGraph.find(vertices, "F"), TestGraph.find(vertices, "H"), 13, 36));
		edges.push(new TemporalEdge("Edge 25", TestGraph.find(vertices, "D"), TestGraph.find(vertices, "G"), 10, 36));
		edges.push(new TemporalEdge("Edge 26", TestGraph.find(vertices, "C"), TestGraph.find(vertices, "H"), 10, 34));
		edges.push(new TemporalEdge("Edge 27", TestGraph.find(vertices, "H"), TestGraph.find(vertices, "H"), 11, 24));
		edges.push(new TemporalEdge("Edge 28", TestGraph.find(vertices, "G"), TestGraph.find(vertices, "H"), 39, 41));
		edges.push(new TemporalEdge("Edge 29", TestGraph.find(vertices, "C"), TestGraph.find(vertices, "I"), 37, 38));
		edges.push(new TemporalEdge("Edge 30", TestGraph.find(vertices, "G"), TestGraph.find(vertices, "G"), 4, 47));
		// 31+
		edges.push(new TemporalEdge("Edge 31", TestGraph.find(vertices, "H"), TestGraph.find(vertices, "J"), 33, 47));
		edges.push(new TemporalEdge("Edge 32", TestGraph.find(vertices, "I"), TestGraph.find(vertices, "J"), 17, 28));
		edges.push(new TemporalEdge("Edge 33", TestGraph.find(vertices, "I"), TestGraph.find(vertices, "A"), 22, 31));
		edges.push(new TemporalEdge("Edge 34", TestGraph.find(vertices, "C"), TestGraph.find(vertices, "G"), 10, 41));
		edges.push(new TemporalEdge("Edge 35", TestGraph.find(vertices, "A"), TestGraph.find(vertices, "G"), 2, 17));
		edges.push(new TemporalEdge("Edge 36", TestGraph.find(vertices, "F"), TestGraph.find(vertices, "G"), 16, 30));
		edges.push(new TemporalEdge("Edge 37", TestGraph.find(vertices, "C"), TestGraph.find(vertices, "D"), 22, 43));
		edges.push(new TemporalEdge("Edge 38", TestGraph.find(vertices, "H"), TestGraph.find(vertices, "A"), 11, 18));
		edges.push(new TemporalEdge("Edge 39", TestGraph.find(vertices, "H"), TestGraph.find(vertices, "B"), 7, 40));
		edges.push(new TemporalEdge("Edge 40", TestGraph.find(vertices, "C"), TestGraph.find(vertices, "E"), 26, 46));
		// 41+
		edges.push(new TemporalEdge("Edge 41", TestGraph.find(vertices, "A"), TestGraph.find(vertices, "J"), 39, 46));
		edges.push(new TemporalEdge("Edge 42", TestGraph.find(vertices, "C"), TestGraph.find(vertices, "H"), 27, 44));
		edges.push(new TemporalEdge("Edge 43", TestGraph.find(vertices, "B"), TestGraph.find(vertices, "E"), 31, 43));
		edges.push(new TemporalEdge("Edge 44", TestGraph.find(vertices, "H"), TestGraph.find(vertices, "J"), 6, 20));
		edges.push(new TemporalEdge("Edge 45", TestGraph.find(vertices, "E"), TestGraph.find(vertices, "G"), 13, 16));
		edges.push(new TemporalEdge("Edge 46", TestGraph.find(vertices, "I"), TestGraph.find(vertices, "I"), 15, 22));
		edges.push(new TemporalEdge("Edge 47", TestGraph.find(vertices, "J"), TestGraph.find(vertices, "F"), 4, 28));
		edges.push(new TemporalEdge("Edge 48", TestGraph.find(vertices, "D"), TestGraph.find(vertices, "G"), 9, 25));
		edges.push(new TemporalEdge("Edge 49", TestGraph.find(vertices, "D"), TestGraph.find(vertices, "F"), 12, 18));
		edges.push(new TemporalEdge("Edge 50", TestGraph.find(vertices, "G"), TestGraph.find(vertices, "B"), 8, 46));

		this.edges = edges;
	}

	public static find(list: TemporalVertex[], label: string): TemporalVertex {
		const result = list.find(v => v.label === label);
		if (result == null) {
			throw "NOT FOUND";
		}
		else {
			return result;
		}
	}
}
