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

package com.io7m.jsx.serializer;

import com.io7m.jsx.SExpressionListType;
import com.io7m.jsx.SExpressionMatcherType;
import com.io7m.jsx.SExpressionQuotedStringType;
import com.io7m.jsx.SExpressionSymbolType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.api.serializer.JSXSerializerType;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A trivial serializer with no features.
 */

public final class JSXSerializerTrivial implements JSXSerializerType
{
  private JSXSerializerTrivial()
  {

  }

  /**
   * @return A new serializer
   */

  public static JSXSerializerType newSerializer()
  {
    return new JSXSerializerTrivial();
  }

  private static void serializeWithWriter(
    final SExpressionType e,
    final PrintWriter w)
    throws IOException
  {
    e.matchExpression(new SerializingMatcher(w));
  }

  @Override
  public void serialize(
    final SExpressionType e,
    final OutputStream s)
    throws IOException
  {
    final BufferedOutputStream bs =
      new BufferedOutputStream(s);
    final OutputStreamWriter os =
      new OutputStreamWriter(bs, StandardCharsets.UTF_8);
    final PrintWriter w = new PrintWriter(os);
    serializeWithWriter(e, w);
    w.flush();
  }

  private static final class SerializingMatcher
    implements SExpressionMatcherType<Integer, IOException>
  {
    private final PrintWriter writer;

    SerializingMatcher(final PrintWriter in_writer)
    {
      this.writer = Objects.requireNonNull(in_writer, "writer");
    }

    @Override
    public Integer list(
      final SExpressionListType xs)
      throws IOException
    {
      if (xs.isSquare()) {
        this.writer.print("[");
      } else {
        this.writer.print("(");
      }

      final int max = xs.size();
      for (int index = 0; index < max; ++index) {
        final SExpressionType es = xs.get(index);
        serializeWithWriter(es, this.writer);
        if ((index + 1) < max) {
          this.writer.print(" ");
        }
      }

      if (xs.isSquare()) {
        this.writer.print("]");
      } else {
        this.writer.print(")");
      }

      return Integer.valueOf(0);
    }

    @Override
    public Integer quotedString(
      final SExpressionQuotedStringType qs)
    {
      this.writer.print('"');
      this.writer.print(qs.text());
      this.writer.print('"');
      return Integer.valueOf(0);
    }

    @Override
    public Integer symbol(
      final SExpressionSymbolType ss)
    {
      this.writer.print(ss.text());
      return Integer.valueOf(0);
    }
  }
}
