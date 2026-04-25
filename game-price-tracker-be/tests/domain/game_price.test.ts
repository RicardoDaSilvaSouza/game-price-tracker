import { Currency } from '../../src/game-price-tracker-be/domain/value_object/currency';
import { GamePrice } from '../../src/game-price-tracker-be/domain/entity/game_price';

describe('GamePrice Entity', () => {
  it('should accept a valid Currency object', () => {
    const validCurrency = new Currency(100, 'USD');
    const gamePrice = new GamePrice('Bitcoin', validCurrency);
    expect(gamePrice.name).toBe('Bitcoin');
    expect(gamePrice.currency.amount).toBe(100);
  });
});
