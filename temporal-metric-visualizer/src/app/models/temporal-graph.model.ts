import { TemporalEdge } from "./temporal-edge.model";
import { TemporalVertex } from "./temporal-vertex.model";

export class TemporalGraph {
	public vertices: TemporalVertex[];
	public edges: TemporalEdge[];

	public constructor(vertices: TemporalVertex[], edges: TemporalEdge[]) {
		this.vertices = vertices;
		this.edges = edges;
	}
}
