import { Guid } from "./guid.model";

export class TemporalVertex {
	public id: string;
	public label: string;
	public validFrom: number;
	public validTo: number;

	public constructor(label: string, validFrom: number, validTo: number) {
		this.id = Guid.generate();
		this.label = label;
		this.validFrom = validFrom;
		this.validTo = validTo;
	}
}
