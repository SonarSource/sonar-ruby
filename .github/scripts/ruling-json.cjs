const strictParse = JSON.parse;

JSON.parse = function parseRulingJson(text, reviver) {
  try {
    return strictParse(text, reviver);
  } catch (error) {
    if (!(error instanceof SyntaxError) || typeof text !== 'string' || !text.includes("'")) {
      throw error;
    }
    const normalized = text
      .replace(/'([^'\\\r\n]+)'\s*:/g, (_match, key) => `${JSON.stringify(key)}:`)
      .replace(/,(\s*[\]}])/g, '$1');
    let result;
    try {
      result = strictParse(normalized);
    } catch {
      throw error;
    }
    if (
      result === null ||
      typeof result !== 'object' ||
      Array.isArray(result) ||
      !Object.entries(result).every(([key, values]) =>
        key.includes(':') && Array.isArray(values) && values.every(Number.isSafeInteger))
    ) {
      throw error;
    }
    return strictParse(normalized, reviver);
  }
};
