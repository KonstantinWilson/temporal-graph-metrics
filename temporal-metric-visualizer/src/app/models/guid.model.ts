export class Guid {
	public static generate(): string {
		const chars = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'];
		let id = "";
		for (let i = 0; i < 32; i++) {
			id += chars[Math.floor(Math.random() * chars.length)];
			if (i == 7 || i == 11 || i == 15 || i == 19) {
				id += '-';
			}
		}
		return id;
	}
}
