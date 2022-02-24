import { Guid } from "./guid.model";
import { TemporalVertex } from "./temporal-vertex.model";

export class TemporalEdge {
	public id: string;
	public label: string;
	public source: TemporalVertex;
	public target: TemporalVertex;
	public validFrom: number;
	public validTo: number;

	public constructor(label: string, source: TemporalVertex, target: TemporalVertex, validFrom: number, validTo: number) {
		this.id = Guid.generate();
		this.label = label;
		this.source = source;
		this.target = target;
		this.validFrom = validFrom;
		this.validTo = validTo;
	}
}
