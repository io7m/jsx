/*
 * Copyright © 2016 <code@io7m.com> http://io7m.com
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

package com.io7m.jsx.api.lexer;

import com.io7m.jlexing.core.LexicalPosition;
import java.util.Objects;
import com.io7m.jsx.JSXException;

import java.nio.file.Path;

/**
 * The type of exceptions raised by the lexer.
 */

public abstract class JSXLexerException extends JSXException
{
  private static final long serialVersionUID = -5821503109066196034L;
  private final LexicalPosition<Path> lex;

  /**
   * Construct an exception.
   *
   * @param in_lex     The lexical information
   * @param in_message The exception message
   */

  public JSXLexerException(
    final LexicalPosition<Path> in_lex,
    final String in_message)
  {
    super(Objects.requireNonNull(in_message, "Message"));
    this.lex = Objects.requireNonNull(in_lex, "Lexical information");
  }

  /**
   * @return The lexical information for the exception
   */

  public final LexicalPosition<Path> lexicalInformation()
  {
    return this.lex;
  }
}