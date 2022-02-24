import { Component } from '@angular/core';
import { TemporalEdgeStep } from './models/temporal-edge-step.model';
import { TemporalEdge } from './models/temporal-edge.model';
import { TemporalVertex } from './models/temporal-vertex.model';
import { TestGraph } from './models/test-graph.model';

@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.scss']
})
export class AppComponent {
	title = 'temporal-metric-visualizer';
	public vertices: TemporalVertex[];
	public edges: TemporalEdge[];
	public edgePath: TemporalEdgeStep[];
	public selectedVertex: TemporalVertex | undefined | null = null;
	public selectedSort = "label";
	public selectedMetric = "hc";

	public constructor() {
		const test = new TestGraph();
		this.vertices = test.vertices;
		this.edges = test.edges;
		console.log(`Loaded ${this.vertices.length} vertices and ${this.edges.length} edges.`);

		this.edgePath = [new TemporalEdgeStep(this.edges)];
	}

	public onChangeSelection() {
		let newEdges: TemporalEdge[] = this.edges;

		if (this.selectedVertex != null) {
			newEdges = newEdges.filter(e => e.source.label === this.selectedVertex?.label);
		}
		if (this.selectedSort != null) {
			switch (this.selectedSort) {
				case "label": newEdges = newEdges.sort((a, b) => a.label.localeCompare(b.label));
					break;
				case "validFrom": newEdges = newEdges.sort((a, b) => a.validFrom - b.validFrom);
					break;
				case "validTo": newEdges = newEdges.sort((a, b) => a.validTo - b.validTo);
					break;
				case "sourceLabel": newEdges = newEdges.sort((a, b) => a.source.label.localeCompare(b.source.label));
					break;
				case "targetLabel": newEdges = newEdges.sort((a, b) => a.target.label.localeCompare(b.target.label));
					break;
			}
		}

		this.edgePath = [new TemporalEdgeStep(newEdges)];
	}

	public onClickEdge(i: number, edge: TemporalEdge) {
		let newEdges = this.filter(this.selectedMetric, this.edges, edge);
		if (this.selectedSort != null) {
			switch (this.selectedSort) {
				case "label": newEdges = newEdges.sort((a, b) => a.label.localeCompare(b.label));
					break;
				case "validFrom": newEdges = newEdges.sort((a, b) => a.validFrom - b.validFrom);
					break;
				case "validTo": newEdges = newEdges.sort((a, b) => a.validTo - b.validTo);
					break;
				case "sourceLabel": newEdges = newEdges.sort((a, b) => a.source.label.localeCompare(b.source.label));
					break;
				case "targetLabel": newEdges = newEdges.sort((a, b) => a.target.label.localeCompare(b.target.label));
					break;
			}
		}

		while (this.edgePath.length > i + 1) {
			this.edgePath.pop();
		}

		if (newEdges != null && newEdges.length > 0) {
			this.edgePath.push(new TemporalEdgeStep(newEdges));
		}
		this.edgePath[i].select(edge);
	}

	public onUndo() {
		if (this.edgePath.length > 1) {
			this.edgePath.pop();
		}
		this.edgePath[this.edgePath.length-1].selected = null;
	}

	private filter(metric: string, edges: TemporalEdge[], prevEdge: TemporalEdge): TemporalEdge[] {
		switch (metric) {
			case "hc":
			case "tsp": return edges.filter(nextEdge => nextEdge.source.label === prevEdge.target.label && nextEdge.validFrom < prevEdge.validTo && nextEdge.validTo > prevEdge.validFrom);
			case "tc": return edges.filter(nextEdge => nextEdge.source.label === prevEdge.target.label && nextEdge.validFrom > prevEdge.validTo);
			default: return edges;
		}
	}
}
