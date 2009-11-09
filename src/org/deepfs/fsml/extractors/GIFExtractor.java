package org.deepfs.fsml.extractors;

import java.io.File;
import java.io.IOException;
import org.basex.build.Builder;
import org.basex.io.BufferInput;
import org.basex.util.Token;
import static org.basex.build.fs.FSText.*;

/**
 * GIF meta data extractor.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-09, ISC License
 * @author Christian Gruen
 */
public final class GIFExtractor extends AbstractExtractor {
  /** Byte array for GIF header. */
  private final byte[] data = new byte[10];

  @Override
  public void extract(final Builder build, final File f) throws IOException {
    BufferInput.read(f, data);

    // check if the header is valid
    if(!Token.startsWith(data, HEADERGIF87) &&
       !Token.startsWith(data, HEADERGIF89)) return;

    // open image tag
    build.startElem(IMAGE, atts.set(TYPE, TYPEGIF));

    // extract image dimensions
    final int w = (data[6] & 0xFF) + ((data[7] & 0xFF) << 8);
    final int h = (data[8] & 0xFF) + ((data[9] & 0xFF) << 8);

    build.nodeAndText(WIDTH, atts.reset(), Token.token(w));
    build.nodeAndText(HEIGHT, atts, Token.token(h));

    build.endElem(IMAGE);
  }
}