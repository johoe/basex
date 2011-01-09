package org.basex.util;

import java.util.Arrays;

/**
 * This is a simple hash map, extending the even simpler
 * {@link TokenSet hash set}.
 * @param <E> generic value type
 *
 * @author BaseX Team 2005-11, ISC License
 * @author Christian Gruen
 */
public final class TokenObjMap<E> extends TokenSet {
  /** Values. */
  private Object[] values = new Object[CAP];

  /**
   * Indexes the specified keys and values.
   * If the key exists, the value is updated.
   * @param key key
   * @param val value
   */
  public void add(final byte[] key, final E val) {
    // array bounds are checked before array is resized..
    final int i = add(key);
    values[Math.abs(i)] = val;
  }

  /**
   * Returns the value for the specified key.
   * @param key key to be found
   * @return value or {@code null} if nothing was found
   */
  @SuppressWarnings("unchecked")
  public E get(final byte[] key) {
    return key != null ? (E) values[id(key)] : null;
  }

  /**
   * Returns the specified value.
   * @param p value index
   * @return value
   */
  @SuppressWarnings("unchecked")
  public E value(final int p) {
    return (E) values[p];
  }

  @Override
  protected void rehash() {
    super.rehash();
    values = Arrays.copyOf(values, size << 1);
  }
}
