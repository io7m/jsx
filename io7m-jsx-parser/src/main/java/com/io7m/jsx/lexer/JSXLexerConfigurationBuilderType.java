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

package com.io7m.jsx.lexer;

import java.nio.file.Path;
import java.util.Optional;

/**
 * The type of mutable builders for lexer configurations.
 */

public interface JSXLexerConfigurationBuilderType
{
  /**
   * @return A new immutable configuration based on all of the values given so
   * far.
   */

  JSXLexerConfiguration build();

  /**
   * Allow or disallow the use of newlines in quoted strings. If disallowed,
   * newlines must be indicated with escape codes.
   *
   * @param e {@code true} if newlines should be allowed.
   */

  void setNewlinesInQuotedStrings(
    boolean e);

  /**
   * Allow or disallow the use of square brackets to denote lists. If
   * disallowed, only '(' (U+0028) and ')' (U+0029) denote lists.
   *
   * @param e {@code true} if square brackets should be allowed.
   */

  void setSquareBrackets(
    boolean e);

  /**
   * Set the file name that will appear in lexical information.
   *
   * @param file The file, if any
   */

  void setFile(Optional<Path> file);
}