/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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

import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jsx.tokens.TokenEOF;
import com.io7m.jsx.tokens.TokenType;
import com.io7m.junreachable.UnreachableCodeException;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Simple lexer demo that prints all tokens.
 */

public final class LexerDemoMain
{
  private LexerDemoMain()
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
    try {
      final LexerConfigurationBuilderType cb = LexerConfiguration.newBuilder();
      cb.setNewlinesInQuotedStrings(false);
      final LexerConfiguration c = cb.build();

      final UnicodeCharacterReaderPushBackType r =
        UnicodeCharacterReader.newReader(new InputStreamReader(System.in));
      final LexerType lex = Lexer.newLexer(c, r);

      while (true) {
        final TokenType t = lex.token();
        System.out.println(t);
        if (t instanceof TokenEOF) {
          break;
        }
      }
    } catch (final LexerException e) {
      System.err.println(
        "error: lexical error: "
        + e.getFile()
        + ":"
        + e.getPosition()
        + ": "
        + e.getMessage());
    }
  }
}