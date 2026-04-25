const { createDefaultPreset } = require('ts-jest');

module.exports = {
  ...createDefaultPreset(),
  testMatch: ['**/*.test.ts'],
  collectCoverage: false,
  testEnvironmentOptions: {
    nodeOptions: ['--no-experimental-webstorage'],
  },
};
