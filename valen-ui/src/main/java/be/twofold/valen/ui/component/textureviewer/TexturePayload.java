package be.twofold.valen.ui.component.textureviewer;

import be.twofold.valen.ui.component.*;

/**
 * Display-ready result of decoding a texture: the first frame plus the counts
 * and status the view needs.
 */
record TexturePayload(int sliceCount, int mipCount, DecodedImage image, String status) {
}
