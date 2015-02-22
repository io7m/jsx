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

package com.io7m.jsx.lexer;

import java.io.File;

import com.io7m.jnull.NullCheck;
import com.io7m.jsx.JSXException;

public abstract class LexerException extends JSXException
{
  private static final long serialVersionUID = -5821503109066196034L;
  private final File        file;
  private final Position    position;

  public LexerException(
    final Position in_position,
    final File in_file,
    final String in_message)
  {
    super(NullCheck.notNull(in_message));
    this.position = NullCheck.notNull(in_position);
    this.file = NullCheck.notNull(in_file);
  }

  public File getFile()
  {
    return this.file;
  }

  public Position getPosition()
  {
    return this.position;
  }
}
