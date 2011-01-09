package org.basex.api.dom;

import org.basex.query.item.Nod;
import org.w3c.dom.CDATASection;

/**
 * DOM - CData implementation.
 *
 * @author BaseX Team 2005-11, ISC License
 * @author Christian Gruen
 */
public final class BXCData extends BXText implements CDATASection {
  /**
   * Constructor.
   * @param n node reference
   */
  public BXCData(final Nod n) {
    super(n);
  }

  @Override
  protected int kind() {
    // type not specified in database
    return 6;
  }
}
