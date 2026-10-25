package be.twofold.valen.core.text;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class PeekingReaderTest {
    @Test
    void testReadsCodePointsInOrder() {
        assertThat(drain(reader("abc"))).containsExactly((int) 'a', (int) 'b', (int) 'c');
    }

    @Test
    void testPeekDoesNotConsume() {
        var reader = reader("ab");
        assertThat(reader.peek()).isEqualTo('a');
        assertThat(reader.peek()).isEqualTo('a');
        assertThat(reader.read()).isEqualTo('a');
        assertThat(reader.peek()).isEqualTo('b');
    }

    @Test
    void testPeekNextLooksTwoAhead() {
        var reader = reader("/*x");
        assertThat(reader.peek()).isEqualTo('/');
        assertThat(reader.peekNext()).isEqualTo('*');
        assertThat(reader.read()).isEqualTo('/');
        assertThat(reader.read()).isEqualTo('*');
        assertThat(reader.read()).isEqualTo('x');
    }

    @Test
    void testPeekNextIsStable() {
        var reader = reader("abc");
        assertThat(reader.peekNext()).isEqualTo('b');
        assertThat(reader.peekNext()).isEqualTo('b');
        assertThat(reader.peek()).isEqualTo('a');
        assertThat(drain(reader)).containsExactly((int) 'a', (int) 'b', (int) 'c');
    }

    @Test
    void testPeekNextWithoutPeekingFirst() {
        var reader = reader("ab");
        assertThat(reader.peekNext()).isEqualTo('b');
        assertThat(reader.read()).isEqualTo('a');
        assertThat(reader.read()).isEqualTo('b');
    }

    @Test
    void testPeekNextAtLastCodePoint() {
        var reader = reader("a");
        assertThat(reader.peek()).isEqualTo('a');
        assertThat(reader.peekNext()).isEqualTo(-1);
        assertThat(reader.read()).isEqualTo('a');
        assertThat(reader.isEof()).isTrue();
    }

    @Test
    void testTracksPositionWithoutPeeking() {
        var reader = reader("ab\ncd");
        assertThat(reader.line()).isEqualTo(1);
        assertThat(reader.column()).isEqualTo(1);
        reader.read();
        assertThat(reader.column()).isEqualTo(2);
        reader.read();
        assertThat(reader.column()).isEqualTo(3);
        reader.read();
        assertThat(reader.line()).isEqualTo(2);
        assertThat(reader.column()).isEqualTo(1);
        reader.read();
        assertThat(reader.line()).isEqualTo(2);
        assertThat(reader.column()).isEqualTo(2);
    }

    @Test
    void testPositionMatchesWithAndWithoutPeeking() {
        var plain = reader("ab\ncd");
        var peeking = reader("ab\ncd");
        for (int i = 0; i < 5; i++) {
            peeking.peek();
            peeking.peekNext();
            plain.read();
            peeking.read();
            assertThat(peeking.line()).isEqualTo(plain.line());
            assertThat(peeking.column()).isEqualTo(plain.column());
        }
    }

    @Test
    void testPeekingDoesNotMovePosition() {
        var reader = reader("ab");
        reader.peek();
        reader.peekNext();
        assertThat(reader.line()).isEqualTo(1);
        assertThat(reader.column()).isEqualTo(1);
    }

    @Test
    void testEndOfInputIsIdempotent() {
        var reader = reader("a");
        reader.read();
        assertThat(reader.column()).isEqualTo(2);

        assertThat(reader.isEof()).isTrue();
        assertThat(reader.peek()).isEqualTo(-1);
        assertThat(reader.peekNext()).isEqualTo(-1);
        assertThat(reader.read()).isEqualTo(-1);
        assertThat(reader.read()).isEqualTo(-1);
        assertThat(reader.line()).isEqualTo(1);
        assertThat(reader.column()).isEqualTo(2);
    }

    @Test
    void testHandlesEmptyInput() {
        var reader = reader("");
        assertThat(reader.isEof()).isTrue();
        assertThat(reader.peek()).isEqualTo(-1);
        assertThat(reader.peekNext()).isEqualTo(-1);
        assertThat(reader.read()).isEqualTo(-1);
        assertThat(reader.line()).isEqualTo(1);
        assertThat(reader.column()).isEqualTo(1);
    }

    @Test
    void testCombinesSurrogatePairs() {
        var reader = reader("a😀b");
        assertThat(reader.read()).isEqualTo('a');
        assertThat(reader.peek()).isEqualTo(0x1F600);
        assertThat(reader.peekNext()).isEqualTo('b');
        assertThat(reader.read()).isEqualTo(0x1F600);
        assertThat(reader.column()).isEqualTo(3);
        assertThat(reader.read()).isEqualTo('b');
    }

    @Test
    void testSurrogatePairDoesNotStraddleLookahead() {
        var reader = reader("😀😁");
        assertThat(reader.peek()).isEqualTo(0x1F600);
        assertThat(reader.peekNext()).isEqualTo(0x1F601);
        assertThat(drain(reader)).containsExactly(0x1F600, 0x1F601);
    }

    @Test
    void testRejectsUnpairedSurrogates() {
        assertThat(List.of("\uD83D", "\uDE00", "\uD83D\uD83D", "\uD83Da", "a\uD83D"))
            .allSatisfy(source -> assertThatThrownBy(() -> drain(reader(source)))
                .isInstanceOf(MalformedTextException.class));
    }

    @Test
    void testGenuineIoFailureIsUncheckedIoException() {
        var failing = new PeekingReader(new Reader() {
            @Override
            public int read(char[] buffer, int offset, int length) throws IOException {
                throw new IOException("disk on fire");
            }

            @Override
            public void close() {
            }
        });
        assertThatThrownBy(failing::read).isInstanceOf(UncheckedIOException.class);
    }

    private PeekingReader reader(String source) {
        return new PeekingReader(new StringReader(source));
    }

    private List<Integer> drain(PeekingReader reader) {
        var result = new ArrayList<Integer>();
        while (true) {
            int c = reader.read();
            if (c == -1) {
                break;
            }
            result.add(c);
        }
        return result;
    }
}
