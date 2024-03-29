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

/**
 * S-expression parsing (Parser)
 */

module com.io7m.jsx.parser
{
  requires static org.osgi.annotation.bundle;
  requires static org.osgi.annotation.versioning;
  requires static org.osgi.service.component.annotations;

  requires com.io7m.jsx.core;
  requires com.io7m.jsx.parser.api;
  requires com.io7m.jeucreader.core;
  requires com.io7m.jlexing.core;
  requires com.io7m.junreachable.core;

  exports com.io7m.jsx.parser;
  exports com.io7m.jsx.serializer;
  exports com.io7m.jsx.lexer;

  provides com.io7m.jsx.api.lexer.JSXLexerSupplierType
    with com.io7m.jsx.lexer.JSXLexerSupplier;
  provides com.io7m.jsx.api.parser.JSXParserSupplierType
    with com.io7m.jsx.parser.JSXParserSupplier;
  provides com.io7m.jsx.api.serializer.JSXSerializerSupplierType
    with com.io7m.jsx.serializer.JSXSerializerTrivialSupplier;
}