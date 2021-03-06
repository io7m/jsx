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

package com.io7m.jsx.tests.parser;

import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jsx.SExpressionListType;
import com.io7m.jsx.SExpressionQuotedStringType;
import com.io7m.jsx.SExpressionSymbolType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.api.lexer.JSXLexerBareCarriageReturnException;
import com.io7m.jsx.api.lexer.JSXLexerComment;
import com.io7m.jsx.api.lexer.JSXLexerConfiguration;
import com.io7m.jsx.api.lexer.JSXLexerConfigurationType;
import com.io7m.jsx.api.lexer.JSXLexerType;
import com.io7m.jsx.api.parser.JSXParserConfiguration;
import com.io7m.jsx.api.parser.JSXParserConfigurationType;
import com.io7m.jsx.api.parser.JSXParserGrammarException;
import com.io7m.jsx.api.parser.JSXParserLexicalException;
import com.io7m.jsx.api.parser.JSXParserType;
import com.io7m.jsx.lexer.JSXLexer;
import com.io7m.jsx.parser.JSXParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public final class ParserTest
{
  private static final LexicalPosition<URI> DEFAULT_LEX =
    LexicalPosition.of(1, 0, Optional.empty());

  private static UnicodeCharacterReaderPushBackType stringReader(
    final String s)
  {
    return UnicodeCharacterReader.newReader(new StringReader(s));
  }

  private static JSXLexerConfigurationType defaultLexerConfig()
  {
    final JSXLexerConfiguration.Builder cb =
      JSXLexerConfiguration.builder();
    final JSXLexerConfiguration c = cb.build();
    return c;
  }

  private static JSXParserConfigurationType defaultParserConfig()
  {
    final JSXParserConfiguration.Builder cb =
      JSXParserConfiguration.builder();
    return cb.build();
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testEOF0()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex = JSXLexer.newLexer(lc, ParserTest.stringReader(""));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testEOF1()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex = JSXLexer.newLexer(
      lc, ParserTest.stringReader("(a b "));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test
  public void testEOF2()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("(a b c)"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    final Optional<SExpressionType> r0 = p.parseExpressionOrEOF();
    Assert.assertTrue(r0.isPresent());
    final SExpressionListType r0l = (SExpressionListType) r0.get();
    final Optional<SExpressionType> r1 = p.parseExpressionOrEOF();
    Assert.assertFalse(r1.isPresent());
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testEOF3()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);
    final JSXLexerConfiguration lc = cb.build();

    final JSXLexerType lex = JSXLexer.newLexer(
      lc, ParserTest.stringReader("[a b "));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = JSXParserLexicalException.class)
  public void testLexError0()
    throws Exception
  {
    final JSXLexerType lex = () -> {
      throw new JSXLexerBareCarriageReturnException(
        LexicalPosition.of(0, 0, Optional.empty()), "Error!");
    };
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = JSXParserLexicalException.class)
  public void testLexError1()
    throws Exception
  {
    final JSXLexerType lex = () -> {
      throw new JSXLexerBareCarriageReturnException(
        LexicalPosition.of(0, 0, Optional.empty()), "Error!");
    };
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpressionOrEOF();
  }

  @Test(expected = JSXParserLexicalException.class)
  public void testLexError2()
    throws Exception
  {
    final JSXLexerType lex = () -> {
      throw new JSXLexerBareCarriageReturnException(
        LexicalPosition.of(0, 0, Optional.empty()), "Error!");
    };
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpressions();
  }

  @Test
  public void testParseList0()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("(a b c)"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionListType s = (SExpressionListType) p.parseExpression();
    final LexicalPosition<URI> sl0 = s.lexical();
    Assert.assertEquals(0L, (long) sl0.line());
    Assert.assertEquals(1L, (long) sl0.column());
    Assert.assertEquals(3L, (long) s.size());
    Assert.assertEquals(Optional.empty(), sl0.file());

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(0);
      Assert.assertEquals("a", ss.text());
      final LexicalPosition<URI> sl = ss.lexical();
      Assert.assertEquals(Optional.empty(), sl.file());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(1);
      Assert.assertEquals("b", ss.text());
      final LexicalPosition<URI> sl = ss.lexical();
      Assert.assertEquals(Optional.empty(), sl.file());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(2);
      Assert.assertEquals("c", ss.text());
      final LexicalPosition<URI> sl = ss.lexical();
      Assert.assertEquals(Optional.empty(), sl.file());
    }
  }

  @Test
  public void testParseListNoLex0()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("(a b c)"));
    final JSXParserConfiguration.Builder pcb =
      JSXParserConfiguration.builder();
    pcb.setPreserveLexical(false);

    final JSXParserConfigurationType pc = pcb.build();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionListType s = (SExpressionListType) p.parseExpression();
    Assert.assertEquals(DEFAULT_LEX, s.lexical());
    Assert.assertEquals(3L, (long) s.size());

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(0);
      Assert.assertEquals("a", ss.text());
      Assert.assertEquals(DEFAULT_LEX, ss.lexical());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(1);
      Assert.assertEquals("b", ss.text());
      Assert.assertEquals(DEFAULT_LEX, ss.lexical());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(2);
      Assert.assertEquals("c", ss.text());
      Assert.assertEquals(DEFAULT_LEX, ss.lexical());
    }
  }

  @Test
  public void testParseListSquareNoLex1()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);
    final JSXLexerConfiguration lc = cb.build();

    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("[a b c]"));
    final JSXParserConfiguration.Builder pcb =
      JSXParserConfiguration.builder();
    pcb.setPreserveLexical(false);
    final JSXParserConfigurationType pc = pcb.build();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionListType s = (SExpressionListType) p.parseExpression();
    Assert.assertEquals(DEFAULT_LEX, s.lexical());
    Assert.assertEquals(3L, (long) s.size());

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(0);
      Assert.assertEquals("a", ss.text());
      Assert.assertEquals(DEFAULT_LEX,ss.lexical());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(1);
      Assert.assertEquals("b", ss.text());
      Assert.assertEquals(DEFAULT_LEX,ss.lexical());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(2);
      Assert.assertEquals("c", ss.text());
      Assert.assertEquals(DEFAULT_LEX,ss.lexical());
    }
  }

  @Test
  public void testParseSquareList0()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);
    final JSXLexerConfiguration lc = cb.build();

    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("[a b c]"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionListType s = (SExpressionListType) p.parseExpression();
    final LexicalPosition<URI> sl0 = s.lexical();
    Assert.assertEquals(0L, (long) sl0.line());
    Assert.assertEquals(1L, (long) sl0.column());
    Assert.assertEquals(3L, (long) s.size());
    Assert.assertEquals(Optional.empty(), sl0.file());

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(0);
      Assert.assertEquals("a", ss.text());
      final LexicalPosition<URI> sl = ss.lexical();
      Assert.assertEquals(Optional.empty(), sl.file());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(1);
      Assert.assertEquals("b", ss.text());
      final LexicalPosition<URI> sl = ss.lexical();
      Assert.assertEquals(Optional.empty(), sl.file());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(2);
      Assert.assertEquals("c", ss.text());
      final LexicalPosition<URI> sl = ss.lexical();
      Assert.assertEquals(Optional.empty(), sl.file());
    }
  }

  @Test
  public void testParseSymbol0()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("a"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionSymbolType s = (SExpressionSymbolType) p.parseExpression();
    final LexicalPosition<URI> sl = s.lexical();
    Assert.assertEquals(0L, (long) sl.line());
    Assert.assertEquals(1L, (long) sl.column());
    Assert.assertEquals("a", s.text());
    Assert.assertEquals(Optional.empty(), sl.file());
  }

  @Test
  public void testParseSymbolNoLex0()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("a"));
    final JSXParserConfiguration.Builder pcb =
      JSXParserConfiguration.builder();
    pcb.setPreserveLexical(false);
    final JSXParserConfigurationType pc = pcb.build();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionSymbolType s = (SExpressionSymbolType) p.parseExpression();
    Assert.assertEquals(DEFAULT_LEX, s.lexical());
    Assert.assertEquals("a", s.text());
  }

  @Test
  public void testQuotedString0()
    throws Exception
  {
    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append("a");
    sb.append('"');

    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader(sb.toString()));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionQuotedStringType s =
      (SExpressionQuotedStringType) p.parseExpression();
    final LexicalPosition<URI> sl = s.lexical();
    Assert.assertEquals(0L, (long) sl.line());
    Assert.assertEquals(1L, (long) sl.column());
    Assert.assertEquals("a", s.text());
    Assert.assertEquals(Optional.empty(), sl.file());
  }

  @Test
  public void testQuotedStringNoLex0()
    throws Exception
  {
    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append("a");
    sb.append('"');

    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader(sb.toString()));

    final JSXParserConfiguration.Builder pcb =
      JSXParserConfiguration.builder();
    pcb.setPreserveLexical(false);
    final JSXParserConfigurationType pc = pcb.build();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final SExpressionQuotedStringType s =
      (SExpressionQuotedStringType) p.parseExpression();
    Assert.assertEquals(DEFAULT_LEX, s.lexical());
    Assert.assertEquals("a", s.text());
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testUnbalancedRoundSquare0()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);
    final JSXLexerConfiguration lc = cb.build();

    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("(]"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testUnbalancedRoundSquare1()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);
    final JSXLexerConfiguration lc = cb.build();

    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("[)"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testUnexpectedRight0()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader(")"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test(expected = JSXParserGrammarException.class)
  public void testUnexpectedRightSquare0()
    throws Exception
  {
    final JSXLexerConfiguration.Builder cb = JSXLexerConfiguration.builder();
    cb.setSquareBrackets(true);
    final JSXLexerConfiguration lc = cb.build();

    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("]"));
    final JSXParserConfigurationType pc = ParserTest.defaultParserConfig();
    final JSXParserType p = JSXParser.newParser(pc, lex);
    p.parseExpression();
  }

  @Test
  public void testParseExpressions()
    throws Exception
  {
    final JSXLexerConfigurationType lc = ParserTest.defaultLexerConfig();
    final JSXLexerType lex =
      JSXLexer.newLexer(lc, ParserTest.stringReader("a b c"));
    final JSXParserConfiguration.Builder pcb =
      JSXParserConfiguration.builder();
    pcb.setPreserveLexical(false);
    final JSXParserConfigurationType pc = pcb.build();
    final JSXParserType p = JSXParser.newParser(pc, lex);

    final List<SExpressionType> s = p.parseExpressions();
    Assert.assertEquals(3L, (long) s.size());

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(0);
      Assert.assertEquals("a", ss.text());
      Assert.assertEquals(DEFAULT_LEX, ss.lexical());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(1);
      Assert.assertEquals("b", ss.text());
      Assert.assertEquals(DEFAULT_LEX, ss.lexical());
    }

    {
      final SExpressionSymbolType ss = (SExpressionSymbolType) s.get(2);
      Assert.assertEquals("c", ss.text());
      Assert.assertEquals(DEFAULT_LEX, ss.lexical());
    }
  }

  @Test
  public void testParseCommented()
    throws Exception
  {
    final JSXLexerConfigurationType lc =
      JSXLexerConfiguration.builder()
        .setSquareBrackets(true)
        .setNewlinesInQuotedStrings(true)
        .setComments(EnumSet.allOf(JSXLexerComment.class))
        .build();

    try (Reader reader =
           new InputStreamReader(
             ParserTest.class.getResourceAsStream(
               "/com/io7m/jsx/tests/commented0.txt"),
             StandardCharsets.UTF_8)) {

      final JSXLexerType lex =
        JSXLexer.newLexer(lc, UnicodeCharacterReader.newReader(reader));

      final JSXParserConfiguration.Builder pcb =
        JSXParserConfiguration.builder();
      pcb.setPreserveLexical(false);
      final JSXParserConfigurationType pc = pcb.build();
      final JSXParserType p = JSXParser.newParser(pc, lex);

      final List<SExpressionType> s = p.parseExpressions();
      Assert.assertEquals(23L, (long) s.size());
    }
  }

  @Test
  public void testParseCommentedOrEOF()
    throws Exception
  {
    final JSXLexerConfigurationType lc =
      JSXLexerConfiguration.builder()
        .setSquareBrackets(true)
        .setNewlinesInQuotedStrings(true)
        .setComments(EnumSet.allOf(JSXLexerComment.class))
        .build();

    try (Reader reader =
           new InputStreamReader(
             ParserTest.class.getResourceAsStream(
               "/com/io7m/jsx/tests/commented0.txt"),
             StandardCharsets.UTF_8)) {

      final JSXLexerType lex =
        JSXLexer.newLexer(lc, UnicodeCharacterReader.newReader(reader));

      final JSXParserConfiguration.Builder pcb =
        JSXParserConfiguration.builder();
      pcb.setPreserveLexical(false);
      final JSXParserConfigurationType pc = pcb.build();
      final JSXParserType p = JSXParser.newParser(pc, lex);

      long count = 0L;
      while (true) {
        final Optional<SExpressionType> s = p.parseExpressionOrEOF();
        if (s.isPresent()) {
          ++count;
        } else {
          break;
        }
      }

      Assert.assertEquals(23L, count);
    }
  }
}
