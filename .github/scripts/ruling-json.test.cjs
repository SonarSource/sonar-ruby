const assert = require('node:assert/strict');
const test = require('node:test');
const strictParse = JSON.parse;
require('./ruling-json.cjs');

test('Ruby ruling bot configuration matches the CI artifact layout', async () => {
  const { rulingConfig } = await import('../ruling-bot.config.mjs');
  assert.deepEqual(rulingConfig, {
    'new-results-path': 'its/ruling/build/tmp/actual',
    'old-results-path': 'its/ruling/src/test/resources/expected',
    'sources-path': '',
    'report-workflow': 'build.yml',
  });
});

for (const text of ['null', '42', '"text"', '[1,2]', '{"run_id":42,"ok":true}']) {
  test(`strict JSON retains its behavior: ${text}`, () => {
    assert.deepEqual(JSON.parse(text), strictParse(text));
  });
}

test('Ruby expectation keys and trailing commas parse without evaluating code', () => {
  assert.deepEqual(JSON.parse("{\n'ruby-project:sources/ruby/example.rb':[\n1,\n5,\n],\n}"), {
    'ruby-project:sources/ruby/example.rb': [1, 5],
  });
});

test('revivers work for strict and legacy expectations', () => {
  const reviver = (_key, value) => typeof value === 'number' ? value + 1 : value;
  assert.deepEqual(JSON.parse('{"ruby:example.rb":[1]}', reviver), { 'ruby:example.rb': [2] });
  assert.deepEqual(JSON.parse("{'ruby:example.rb':[1,],}", reviver), { 'ruby:example.rb': [2] });
});

for (const text of [
  "{'ruby:example.rb':[process.exit(),],}",
  "{'ruby:example.rb':['text'],}",
  "{'ruby:example.rb':[true],}",
  "{'ruby:example.rb':[1.5],}",
  "{'ruby:example.rb':[9007199254740992],}",
  "{'ruby:example.rb':{'line':1},}",
  "{'run_id':[42],}",
  "['ruby:example.rb',]",
  "{'ruby:example.rb':[1,]",
  '{"run_id":42,}',
]) {
  test(`non-ruling or invalid JSON remains rejected: ${text}`, () => {
    assert.throws(() => JSON.parse(text), SyntaxError);
  });
}
