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

package com.io7m.jsx.prettyprint;

import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jsx.SExpressionListType;
import com.io7m.jsx.SExpressionMatcherType;
import com.io7m.jsx.SExpressionQuotedStringType;
import com.io7m.jsx.SExpressionSymbolType;
import com.io7m.jsx.SExpressionType;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.WriterBackend;

import java.io.IOException;
import java.io.Writer;

/**
 * A pretty printer tailored to printing expressions that represent tree-like
 * documents.
 */

public final class JSXPrettyPrinterMarkupStyle implements JSXPrettyPrinterType
{
  private final Writer out;
  private final WriterBackend backend;
  private final Layouter<IOException> layout;
  private final SExpressionMatcherType<Unit, IOException> matcher;

  /**
   * Create a new pretty printer.
   *
   * @param in_out    The output writer
   * @param in_width  The maximum output width (note that unbreakable lines may
   *                  exceed this width)
   * @param in_indent The indent for nested expressions
   *
   * @return A new printer
   */

  public static JSXPrettyPrinterType newPrinterWithWidthIndent(
    final Writer in_out,
    final int in_width,
    final int in_indent)
  {
    return new JSXPrettyPrinterMarkupStyle(in_out, in_width, in_indent);
  }

  private JSXPrettyPrinterMarkupStyle(
    final Writer in_out,
    final int width,
    final int indent)
  {
    this.out = NullCheck.notNull(in_out);
    this.backend = new WriterBackend(this.out, width);
    this.layout = new Layouter<>(this.backend, indent);
    this.matcher = new SExpressionMatcherType<Unit, IOException>()
    {
      @Override
      public Unit list(final SExpressionListType e)
        throws IOException
      {
        final Layouter<IOException> x = JSXPrettyPrinterMarkupStyle.this.layout;

        x.begin(
          Layouter.BreakConsistency.INCONSISTENT,
          Layouter.IndentationBase.FROM_POS,
          0);

        if (e.isSquare()) {
          x.print("[");
        } else {
          x.print("(");
        }

        final int size = e.size();
        if (size > 0) {
          x.begin(
            Layouter.BreakConsistency.INCONSISTENT,
            Layouter.IndentationBase.FROM_POS,
            0);

          for (int index = 0; index < size; ++index) {
            final SExpressionType current = e.get(index);
            current.matchExpression(this);
            if (index + 1 < size) {
              x.brk();
            }
          }

          x.end();
        }

        if (e.isSquare()) {
          x.print("]");
        } else {
          x.print(")");
        }

        x.end();
        return Unit.unit();
      }

      @Override
      public Unit quotedString(final SExpressionQuotedStringType e)
        throws IOException
      {
        JSXPrettyPrinterMarkupStyle.this.layout.print(
          String.format("\"%s\"", e.getText()));
        return Unit.unit();
      }

      @Override
      public Unit symbol(final SExpressionSymbolType e)
        throws IOException
      {
        JSXPrettyPrinterMarkupStyle.this.layout.print(e.getText());
        return Unit.unit();
      }
    };
  }

  @Override
  public void print(final SExpressionType e)
    throws IOException
  {
    e.matchExpression(this.matcher);
  }

  @Override
  public void close()
    throws IOException
  {
    this.backend.flush();
  }
}
