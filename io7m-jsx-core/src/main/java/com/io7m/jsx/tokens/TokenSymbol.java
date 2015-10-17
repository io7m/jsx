/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jsx.tokens;

import com.io7m.jnull.NullCheck;
import com.io7m.jsx.lexer.Position;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A symbol.
 */

public final class TokenSymbol implements TokenType
{
  private final Optional<Path> file;
  private final Position       position;
  private final String         text;

  /**
   * @param in_file     The token file
   * @param in_position The token position
   * @param in_text     The token text
   */

  public TokenSymbol(
    final Optional<Path> in_file,
    final Position in_position,
    final String in_text)
  {
    this.file = NullCheck.notNull(in_file);
    this.position = NullCheck.notNull(in_position);
    this.text = NullCheck.notNull(in_text);
  }

  @Override public Optional<Path> getFile()
  {
    return this.file;
  }

  @Override public Position getPosition()
  {
    return this.position;
  }

  /**
   * @return The text content of the token
   */

  public String getText()
  {
    return this.text;
  }

  @Override public <A, E extends Exception> A matchToken(
    final TokenMatcherType<A, E> m)
    throws E
  {
    return m.symbol(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder(64);
    builder.append("[TokenSymbol file=");
    builder.append(this.file);
    builder.append(" position=");
    builder.append(this.position);
    builder.append(" text=");
    builder.append(this.text);
    builder.append("]");
    return NullCheck.notNull(builder.toString());
  }
}
