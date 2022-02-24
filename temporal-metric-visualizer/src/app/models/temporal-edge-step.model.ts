import { TemporalEdge } from "./temporal-edge.model";

export class TemporalEdgeStep {
	public selected: number | null = null;
	public edges: TemporalEdge[];
	public from: number | null = null;
	public to: number | null = null;

	public constructor(edges: TemporalEdge[]) {
		this.edges = edges;
	}

	public select(edge: TemporalEdge) {
		this.selected = this.edges.findIndex(e => e.id === edge.id);
	}

	public inserBoundariesInner(from: number, to: number) {
		this.from = this.from == null ? from : Math.max(this.from, from);
		this.to = this.to == null ? to : Math.min(this.to, to);
	}

	public inserBoundariesOuter(from: number, to: number) {
		this.from = this.from == null ? from : Math.min(this.from, from);
		this.to = this.to == null ? to : Math.max(this.to, to);
	}
}
