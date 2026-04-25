import { Currency } from '../value_object/currency';

export class GamePrice {
    constructor(public name: string, public currency: Currency) {
        if (!this.currency.isValid()) {
            throw new Error('GamePrice requires a valid Currency object.');
        }
    }
}
