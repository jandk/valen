package be.twofold.valen.ui.viewers.data;

record PreviewItem(String name, Object value) {
    public static NoValue NOVALUE = new NoValue();

    record NoValue() {

    }
}
