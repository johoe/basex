package org.basex.query.item;

import org.basex.query.QueryException;
import org.basex.query.expr.Expr;
import org.basex.util.ByteList;
import org.basex.util.InputInfo;
import org.basex.util.Token;
import org.basex.util.Util;

/**
 * Base64Binary item. Derived from java.util.prefs.Base64.
 *
 * @author BaseX Team 2005-11, ISC License
 * @author Christian Gruen
 */
public final class B64 extends Item {
  /** Ending characters. */
  private static final byte[] ENDING = Token.token("AQgw");
  /** Binary data. */
  byte[] val;

  /**
   * Constructor.
   * @param d textual data
   * @param ii input info
   * @throws QueryException query exception
   */
  public B64(final byte[] d, final InputInfo ii) throws QueryException {
    super(Type.B6B);
    final ByteList bl = new ByteList();
    for(final byte c : d) if(c < 0 || c > ' ') bl.add(c);
    b2h(bl.toArray(), ii);
  }

  /**
   * Constructor.
   * @param d binary data
   */
  public B64(final byte[] d) {
    super(Type.B6B);
    val = d;
  }

  /**
   * Constructor.
   * @param h hex item
   */
  B64(final Hex h) {
    this(h.val);
  }

  @Override
  public byte[] atom() {
    return h2b();
  }

  @Override
  public boolean eq(final InputInfo ii, final Item it) {
    // at this stage, item will always be of the same type
    return Token.eq(val, ((B64) it).val);
  }

  @Override
  public byte[] toJava() {
    return val;
  }

  /**
   * Hex to byte conversion.
   * @return base64 array
   */
  private byte[] h2b() {
    final ByteList bl = new ByteList();
    final int a = val.length;
    final int f = a / 3;
    final int p = a - 3 * f;

    int c = 0;
    for(int i = 0; i < f; ++i) {
      final int b0 = val[c++] & 0xff;
      final int b1 = val[c++] & 0xff;
      final int b2 = val[c++] & 0xff;
      bl.add(H2B[b0 >> 2]);
      bl.add(H2B[b0 << 4 & 0x3f | b1 >> 4]);
      bl.add(H2B[b1 << 2 & 0x3f | b2 >> 6]);
      bl.add(H2B[b2 & 0x3f]);
    }

    if(p != 0) {
      final int b0 = val[c++] & 0xff;
      bl.add(H2B[b0 >> 2]);
      if(p == 1) {
        bl.add(H2B[b0 << 4 & 0x3f]);
        bl.add('=');
        bl.add('=');
      } else {
        final int b1 = val[c++] & 0xff;
        bl.add(H2B[b0 << 4 & 0x3f | b1 >> 4]);
        bl.add(H2B[b1 << 2 & 0x3f]);
        bl.add('=');
      }
    }
    return bl.toArray();
  }

  /** Hex to byte conversion table. */
  private static final byte[] H2B = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
      'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
      'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
      'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

  /**
   * Byte to hex conversion.
   * @param s base64 array
   * @param ii input info
   * @throws QueryException query exception
   */
  private void b2h(final byte[] s, final InputInfo ii) throws QueryException {
    if((s.length & 3) != 0) castErr(s, ii);
    final int l = s.length;
    final int g = l >>> 2;
    int m = 0, n = g;
    if(l != 0) {
      if(s[l - 1] == '=') {
        ++m;
        --n;
      }
      if(s[l - 2] == '=') ++m;
      if(m == 2 && !Token.contains(ENDING, s[l - 3])) castErr(
          Token.substring(s, l - 3), ii);
    }
    val = new byte[3 * g - m];

    int c = 0, o = 0;
    for(int i = 0; i < n; ++i) {
      final int c0 = b2h(s[c++], ii);
      final int c1 = b2h(s[c++], ii);
      final int c2 = b2h(s[c++], ii);
      final int c3 = b2h(s[c++], ii);
      val[o++] = (byte) (c0 << 2 | c1 >> 4);
      val[o++] = (byte) (c1 << 4 | c2 >> 2);
      val[o++] = (byte) (c2 << 6 | c3);
    }

    if(m != 0) {
      final int c0 = b2h(s[c++], ii);
      final int c1 = b2h(s[c++], ii);
      val[o++] = (byte) (c0 << 2 | c1 >> 4);
      if(m == 1) val[o++] = (byte) (c1 << 4 | b2h(s[c++], ii) >> 2);
    }
  }

  /**
   * Byte to hex conversion.
   * @param c character to be encoded
   * @param ii input info
   * @return encoded value
   * @throws QueryException query exception
   */
  private int b2h(final byte c, final InputInfo ii) throws QueryException {
    if(c < 0 || c >= B2H.length) castErr((char) c, ii);
    final int result = B2H[c];
    if(result < 0) castErr((char) c, ii);
    return result;
  }

  /** Byte to hex conversion table. */
  private static final byte[] B2H = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1,
      -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
      -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
      20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31,
      32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
      50, 51};

  @Override
  public String toString() {
    return Util.info("\"%\"", h2b());
  }

  @Override
  public boolean sameAs(final Expr cmp) {
    if(!(cmp instanceof B64)) return false;
    final B64 i = (B64) cmp;
    return type == i.type && Token.eq(val, i.val);
  }

}
