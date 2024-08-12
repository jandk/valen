package be.twofold.valen.reader.havokshape;

sealed interface HavokTypeTemplateParam {
    record IntValue(String name, int value) implements HavokTypeTemplateParam {
        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    record TypeValue(String name, HavokType value) implements HavokTypeTemplateParam {
        @Override
        public String toString() {
            return value.toString();
        }
    }
}
