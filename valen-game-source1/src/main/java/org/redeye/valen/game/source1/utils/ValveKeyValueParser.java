package org.redeye.valen.game.source1.utils;

import java.io.*;
import java.util.*;

public class ValveKeyValueParser {

    private Map<String, List<Object>> parsedData;

    public ValveKeyValueParser() {
        parsedData = new HashMap<>();
    }

    public void parseFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        Stack<Map<String, List<Object>>> stack = new Stack<>();
        stack.push(parsedData);  // Push the root map onto the stack

        int lineNumber = 0;
        try {
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip full-line comments
                if (line.isEmpty() || line.startsWith("//") || line.startsWith("/*") || line.startsWith("*")) {
                    continue;
                }

                // Block start
                if (line.equals("{")) {
                    continue;
                }

                // Block end
                if (line.equals("}")) {
                    if (stack.size() <= 1) {  // Ensure we don't pop the root map
                        throw new IOException("Unmatched closing brace at line " + lineNumber);
                    }
                    stack.pop();
                    continue;
                }

                // Parse key-value pair or key-block pair
                String[] keyValuePair = parseKeyValuePair(line);
                String key = keyValuePair[0].toLowerCase();  // Convert key to lowercase for case insensitivity
                String value = keyValuePair[1];

                if (value == null) {
                    // This is a block key, prepare for nested data
                    Map<String, List<Object>> newBlock = new HashMap<>();
                    addToMap(stack.peek(), key, newBlock);
                    stack.push(newBlock);
                } else {
                    if (key.contains("+")) {
                        // Split key by + and add each part with the same value
                        String[] splitKeys = key.split("\\+");
                        for (String splitKey : splitKeys) {
                            addToMap(stack.peek(), splitKey.toLowerCase(), value);  // Convert split keys to lowercase
                        }
                    } else {
                        addToMap(stack.peek(), key, value);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error parsing file at line " + lineNumber + ": " + e.getMessage());
        } finally {
            reader.close();
        }

        // Ensure that the stack only contains the root map at the end
        if (stack.size() != 1) {
            throw new IOException("Unmatched opening brace(s) detected at the end of the file");
        }
    }

    private String[] parseKeyValuePair(String line) throws IOException {
        String key;
        String value = null;

        // Handle inline comments
        line = removeInlineComment(line);

        // Handle quoted keys
        if (line.startsWith("\"")) {
            int endKeyIndex = line.indexOf("\"", 1);
            if (endKeyIndex == -1) {
                throw new IOException("Unmatched quote in key: " + line);
            }
            key = line.substring(1, endKeyIndex).trim();

            int startValueIndex = line.indexOf("\"", endKeyIndex + 1);
            if (startValueIndex != -1) {
                // Quoted value
                int endValueIndex = line.indexOf("\"", startValueIndex + 1);
                if (endValueIndex == -1) {
                    throw new IOException("Unmatched quote in value: " + line);
                }
                value = line.substring(startValueIndex + 1, endValueIndex).trim();
            }
        } else {
            // Handle unquoted keys
            String[] parts = line.split("\\s+", 2);
            key = parts[0].trim();
            if (parts.length > 1) {
                value = parts[1].trim();

                // Remove surrounding quotes if present
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1).trim();
                }
            }
        }

        return new String[]{key, value};
    }

    private String removeInlineComment(String line) {
        int commentIndex = line.indexOf("//");
        if (commentIndex != -1) {
            return line.substring(0, commentIndex).trim();
        }
        return line;
    }

    private void addToMap(Map<String, List<Object>> map, String key, Object value) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    // API to fetch data by a.b.c style path with case insensitivity
    public Object getValueByPath(String path) {
        String[] keys = path.toLowerCase().split("\\.");
        Map<String, List<Object>> currentMap = parsedData;

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            List<Object> values = currentMap.get(key);

            if (values == null) {
                return null; // Path does not exist
            }

            if (i == keys.length - 1) {
                return values.size() == 1 ? values.get(0) : values;
            }

            Object next = values.get(0);
            if (next instanceof Map) {
                currentMap = (Map<String, List<Object>>) next;
            } else {
                return null; // Path does not lead to a map
            }
        }

        return null;
    }

    // API to check if a key exists with case insensitivity
    public boolean keyExists(String path) {
        return getValueByPath(path) != null;
    }

    // API to fetch all keys at a specific level with case insensitivity
    public Set<String> getKeysAtPath(String path) {
        Object value = getValueByPath(path);

        if (value instanceof Map) {
            return ((Map<String, List<Object>>) value).keySet();
        }

        return Collections.emptySet();
    }

    public Map<String, List<Object>> getParsedData() {
        return parsedData;
    }

}