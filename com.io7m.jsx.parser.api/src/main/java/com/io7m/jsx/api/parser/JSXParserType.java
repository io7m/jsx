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

import com.io7m.jsx.SExpressionType;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * The type of parsers.
 */

public interface JSXParserType
{
  /**
   * @return The next expression
   *
   * @throws JSXParserException On parse errors
   * @throws IOException        On I/O errors
   */

  SExpressionType parseExpression()
    throws JSXParserException, IOException;

  /**
   * @return The next expression, or {@link Optional#empty()} if EOF is reached
   *
   * @throws JSXParserException On parse errors
   * @throws IOException        On I/O errors
   */

  Optional<SExpressionType> parseExpressionOrEOF()
    throws JSXParserException, IOException;

  /**
   * @return All of the expressions up to EOF
   *
   * @throws JSXParserException On parse errors
   * @throws IOException        On I/O errors
   */

  List<SExpressionType> parseExpressions()
    throws JSXParserException, IOException;
}
