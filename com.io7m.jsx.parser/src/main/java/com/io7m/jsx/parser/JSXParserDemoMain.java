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

package com.io7m.jsx.parser;

import com.io7m.jsx.api.lexer.JSXLexerComment;
import com.io7m.jsx.api.lexer.JSXLexerConfiguration;
import com.io7m.jsx.api.lexer.JSXLexerSupplierType;
import com.io7m.jsx.api.parser.JSXParserConfiguration;
import com.io7m.jsx.api.parser.JSXParserException;
import com.io7m.jsx.api.parser.JSXParserSupplierType;
import com.io7m.jsx.lexer.JSXLexerSupplier;
import com.io7m.jsx.serializer.JSXSerializerTrivial;
import com.io7m.junreachable.UnreachableCodeException;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Optional;

/**
 * Simple parser demo that parses and then serializes.
 */

public final class JSXParserDemoMain
{
  private JSXParserDemoMain()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Main program.
   *
   * @param args Command line arguments
   *
   * @throws IOException On I/O errors
   */

  public static void main(
    final String[] args)
    throws IOException
  {
    final var lexer_config =
      new JSXLexerConfiguration(
        true,
        true,
        Optional.empty(),
        EnumSet.allOf(JSXLexerComment.class),
        1
      );

    final var parser_config =
      new JSXParserConfiguration(true);
    final JSXLexerSupplierType lexer_supplier =
      new JSXLexerSupplier();
    final JSXParserSupplierType parser_supplier =
      new JSXParserSupplier();

    final var parser =
      parser_supplier.createFromStreamUTF8(
        parser_config,
        lexer_config,
        lexer_supplier,
        System.in);

    final var serializer = JSXSerializerTrivial.newSerializer();

    while (true) {
      try {
        final var e_opt = parser.parseExpressionOrEOF();
        if (e_opt.isPresent()) {
          final var e = e_opt.get();
          serializer.serialize(e, System.out);
          System.out.println();
        } else {
          break;
        }
      } catch (final JSXParserException x) {
        System.err.println(
          "error: parse error: "
            + x.lexical()
            + ": "
            + x.getMessage());
      }
    }
  }
}
