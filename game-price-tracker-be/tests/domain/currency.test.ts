import { Currency } from '../../src/game-price-tracker-be/domain/value_object/currency';

describe('Currency VO', () => {
  it('should throw an error if amount is not positive', () => {
    expect(() => new Currency(0, 'USD')).toThrow('Amount must be positive.');
  });

  it('should throw an error if amount is negative', () => {
    expect(() => new Currency(-10, 'USD')).toThrow('Amount must be positive.');
  });

  it('should be valid with positive amount', () => {
    const currency = new Currency(100, 'USD');
    expect(currency.isValid()).toBe(true);
  });
});
