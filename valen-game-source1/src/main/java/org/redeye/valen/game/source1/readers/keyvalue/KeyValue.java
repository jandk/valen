package org.redeye.valen.game.source1.readers.keyvalue;

import java.util.*;

public sealed interface KeyValue {
    static Obj parse(String string) {
        var source = new Source(string);
        var lexer = new KeyValueLexer(source);
        var parser = new KeyValueParser(lexer);
        return parser.parse();
    }

    record Str(
        String value
    ) implements KeyValue {
    }

    record Obj(
        List<Map.Entry<String, KeyValue>> values
    ) implements KeyValue {
        public List<KeyValue> get(String key) {
            return values.stream()
                .filter(entry -> entry.getKey().equals(key))
                .map(Map.Entry::getValue)
                .toList();
        }

        public String getString(String key) {
            var keyValue = getOne(key);
            if (!(keyValue instanceof Str(String value))) {
                throw new KeyValueException(key + " is not a string");
            }
            return value;
        }

        public Obj getObject(String key) {
            var keyValue = getOne(key);
            if (!(keyValue instanceof Obj object)) {
                throw new KeyValueException(key + " is not an object");
            }
            return object;
        }

        private KeyValue getOne(String key) {
            var value = get(key);
            if (value == null) {
                throw new KeyValueException("Key '" + key + "' not found");
            }
            if (value.size() > 1) {
                throw new KeyValueException("Multiple values for key '" + key + "'");
            }
            return value.getFirst();
        }
    }
}
