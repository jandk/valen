package be.twofold.valen.ui.component.rawview.text;

import be.twofold.valen.ui.component.rawview.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import jfx.incubator.scene.control.richtext.*;
import jfx.incubator.scene.control.richtext.model.*;

import java.util.regex.*;

public final class TextView {
    private static final KeyCombination FIND_KEY =
        new KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN);

    private final VBox view = new VBox();
    private final RawCodeArea codeArea = new RawCodeArea();
    private final FindBar findBar = new FindBar(this::find, this::hideFindBar);
    private final CheckMenuItem wrapItem = new CheckMenuItem("Wrap lines");

    private Lines lines;
    private String patternQuery;
    private Pattern pattern;

    public TextView() {
        buildUI();
        clear();
    }

    public Parent getFXNode() {
        return view;
    }

    public void setText(String text) {
        this.lines = Lines.parse(text);

        var model = new CodeTextModel(new TextContent(lines));
        model.setDecorator(new TextSyntaxDecorator(RawCodeArea.MONOSPACED));
        codeArea.setModel(model);
        codeArea.setWrapText(wrapItem.isSelected());
    }

    public void clear() {
        setText("");
        hideFindBar();
    }

    private void onKeyPressed(KeyEvent event) {
        if (FIND_KEY.match(event)) {
            showFindBar();
            event.consume();
        } else if (event.getCode() == KeyCode.ESCAPE && view.getChildren().contains(findBar)) {
            hideFindBar();
            event.consume();
        }
    }

    private void showFindBar() {
        if (!view.getChildren().contains(findBar)) {
            view.getChildren().add(findBar);
        }
        findBar.focusField();
    }

    private void hideFindBar() {
        if (view.getChildren().remove(findBar)) {
            codeArea.requestFocus();
        }
    }

    private boolean find(String query, boolean forward, boolean fromCurrent) {
        if (query.isEmpty()) {
            codeArea.clearSelection();
            return true;
        }

        var pattern = compile(query);
        if (pattern == null) {
            return false;
        }

        var text = lines.text();
        var selection = codeArea.getSelection();

        var from = selection == null
            ? 0
            : offsetOf(forward && !fromCurrent ? selection.getMax() : selection.getMin());

        var found = forward
            ? firstMatch(pattern, text, from)
            : lastMatch(pattern, text, from);

        if (found == null) {
            // Wrap around
            found = forward
                ? firstMatch(pattern, text, 0)
                : lastMatch(pattern, text, text.length());
        }

        if (found == null) {
            return false;
        }

        codeArea.select(positionOf(found.start()), positionOf(found.end()));
        return true;
    }

    private MatchResult firstMatch(Pattern pattern, String text, int from) {
        var matcher = pattern.matcher(text);
        for (int at = Math.max(from, 0); at <= text.length() && matcher.find(at); at = matcher.end() + 1) {
            if (matcher.end() > matcher.start()) {
                return matcher.toMatchResult();
            }
        }
        return null;
    }

    private MatchResult lastMatch(Pattern pattern, String text, int to) {
        var matcher = pattern.matcher(text);
        MatchResult found = null;
        for (int at = 0; at <= text.length() && matcher.find(at); ) {
            if (matcher.end() > matcher.start()) {
                if (matcher.end() > to) {
                    break;
                }
                found = matcher.toMatchResult();
                at = matcher.end();
            } else {
                at = matcher.end() + 1;
            }
        }
        return found;
    }

    private int offsetOf(TextPos pos) {
        return lines.start(pos.index()) + pos.offset();
    }

    private TextPos positionOf(int offset) {
        int index = lines.row(offset);
        int column = Math.min(offset - lines.start(index), lines.get(index).length());
        return TextPos.ofLeading(index, column);
    }

    private Pattern compile(String query) {
        if (query.equals(patternQuery)) {
            return pattern;
        }

        patternQuery = query;
        try {
            pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
        } catch (PatternSyntaxException e) {
            pattern = null;
        }
        return pattern;
    }

    //region UI

    private void buildUI() {
        codeArea.setLineNumbersEnabled(true);
        codeArea.setContextMenu(buildContextMenu());

        VBox.setVgrow(codeArea, Priority.ALWAYS);

        view.getChildren().add(codeArea);
        view.addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    }

    private ContextMenu buildContextMenu() {
        var copyItem = new MenuItem("Copy");
        copyItem.setOnAction(_ -> codeArea.copy());

        wrapItem.setOnAction(_ -> codeArea.setWrapText(wrapItem.isSelected()));

        var findItem = new MenuItem("Find...");
        findItem.setOnAction(_ -> showFindBar());

        return new ContextMenu(
            copyItem,
            new SeparatorMenuItem(),
            wrapItem,
            findItem
        );
    }

    //endregion

}
