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

package com.io7m.jsx.combinators;

import com.io7m.immutables.styles.ImmutablesStyleType;
import com.io7m.jlexing.core.LexicalPositionType;
import org.immutables.value.Value;

import java.net.URI;

import static org.immutables.value.Value.Immutable;

/**
 * An error produced during validation of an S-expression.
 */

@Immutable
@ImmutablesStyleType
public interface JSXValidationErrorType
{
  /**
   * @return The lexical information
   */

  @Value.Parameter
  LexicalPositionType<URI> lex();

  /**
   * @return The error message
   */

  @Value.Parameter
  String message();
}
