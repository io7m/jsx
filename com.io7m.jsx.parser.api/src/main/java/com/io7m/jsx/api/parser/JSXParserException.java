/*
 * Copyright © 2016 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.jsx.api.parser;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jlexing.core.LexicalType;
import com.io7m.jsx.JSXException;

import java.net.URI;
import java.util.Objects;

/**
 * The type of exceptions raised by the parser.
 */

public abstract class JSXParserException
  extends JSXException implements LexicalType<URI>
{
  private static final long serialVersionUID = -5821503109066196034L;
  private final LexicalPosition<URI> lex;

  /**
   * Construct an exception.
   *
   * @param in_lex     The lexical information
   * @param in_message The exception message
   */

  public JSXParserException(
    final LexicalPosition<URI> in_lex,
    final String in_message)
  {
    super(Objects.requireNonNull(in_message, "Message"));
    this.lex = Objects.requireNonNull(in_lex, "Lexical information");
  }

  /**
   * Construct an exception.
   *
   * @param in_lex     The lexical information
   * @param in_message The exception message
   * @param in_cause   The cause
   */

  public JSXParserException(
    final LexicalPosition<URI> in_lex,
    final String in_message,
    final Throwable in_cause)
  {
    super(
      Objects.requireNonNull(in_message, "Message"),
      Objects.requireNonNull(in_cause, "Cause"));
    this.lex = Objects.requireNonNull(in_lex, "Lexical information");
  }

  @Override
  public final LexicalPosition<URI> lexical()
  {
    return this.lex;
  }
}
