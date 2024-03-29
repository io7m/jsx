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

import com.io7m.jsx.api.lexer.JSXLexerException;

import java.util.Objects;

/**
 * The type of parser errors caused by {@link JSXLexerException} throws.
 */

public final class JSXParserLexicalException extends JSXParserException
{
  private static final long serialVersionUID = 7904039867120509695L;

  /**
   * Construct an exception.
   *
   * @param in_cause The cause
   */

  public JSXParserLexicalException(
    final JSXLexerException in_cause)
  {
    super(
      in_cause.lexical(),
      Objects.requireNonNull(in_cause.getMessage(), "Message"),
      in_cause);
  }
}
