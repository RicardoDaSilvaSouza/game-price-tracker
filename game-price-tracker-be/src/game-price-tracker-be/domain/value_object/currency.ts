export class Currency {
    constructor(public amount: number, public code: string) {
        if (amount <= 0) {
            throw new Error('Amount must be positive.');
        }
    }

    isValid(): boolean {
        return this.amount > 0;
    }
}
