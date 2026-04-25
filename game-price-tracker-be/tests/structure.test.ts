import * as fs from 'fs';
import * as path from 'path';

// The test is run from the tests directory
// Calculate the project root (parent of tests directory)
const testsDir = __dirname;
const projectRoot = path.dirname(testsDir);

// Calculate paths relative to project root
const srcDir = path.join(projectRoot, 'src/game-price-tracker-be');

const valueObjectDir = path.join(srcDir, 'domain/value_object');
const entityDir = path.join(srcDir, 'domain/entity');
const portsDir = path.join(srcDir, 'ports');
const applicationDir = path.join(srcDir, 'application');
const infrastructureDir = path.join(srcDir, 'infrastructure');

describe('Project Structure Check', () => {
  it('should contain the core architectural directories', () => {
    expect(fs.existsSync(valueObjectDir)).toBe(true);
    expect(fs.existsSync(entityDir)).toBe(true);
    expect(fs.existsSync(portsDir)).toBe(true);
    expect(fs.existsSync(applicationDir)).toBe(true);
    expect(fs.existsSync(infrastructureDir)).toBe(true);
  });
});
