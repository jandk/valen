package be.twofold.valen.ui.component;

import wtf.reversed.toolbox.collect.*;

/**
 * A decoded, display-ready image: BGRA pixels with premultiplied alpha, laid
 * out top-to-bottom with a stride of {@code width * 4} bytes.
 */
public record DecodedImage(int width, int height, Bytes.Mutable pixels) {
}
