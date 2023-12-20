package be.twofold.valen.reader.decl;

import be.twofold.valen.reader.decl.model.*;

import java.util.*;
import java.util.function.*;
import java.util.regex.*;

public final class DeclHelper {

    private static final Pattern ItemPattern = Pattern.compile("^item\\[(\\d+)]$");

    private DeclHelper() {
        throw new UnsupportedOperationException();
    }

    public static <T> List<T> parseList(DeclValue declValue, Function<DeclObject, T> parser) {
        DeclObject declObject = declValue.asObject();
        int size = declObject.get("num").asNumber().intValue();
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(null);
        }

        for (Map.Entry<String, DeclValue> entry : declObject) {
            if (entry.getKey().equals("num")) {
                continue;
            }
            Matcher matcher = ItemPattern.matcher(entry.getKey());
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid key: " + entry.getKey());
            }
            int index = Integer.parseInt(matcher.group(1));
            T value = parser.apply(entry.getValue().asObject());
            list.set(index, value);
        }
        return list;
    }

    public static <K, V> Map<K, V> parseMap(DeclValue declValue, Function<String, K> keyParser, Function<DeclObject, V> valueParser) {
        DeclObject declObject = declValue.asObject();
        Map<K, V> map = new LinkedHashMap<>();
        for (Map.Entry<String, DeclValue> entry : declObject) {
            K key = keyParser.apply(entry.getKey());
            V value = valueParser.apply(entry.getValue().asObject());
            map.put(key, value);
        }
        return map;
    }

}
