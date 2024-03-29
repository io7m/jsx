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

package com.io7m.jsx.lexer;

import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jlexing.core.LexicalPositionMutable;
import com.io7m.jsx.api.lexer.JSXLexerBareCarriageReturnException;
import com.io7m.jsx.api.lexer.JSXLexerComment;
import com.io7m.jsx.api.lexer.JSXLexerConfiguration;
import com.io7m.jsx.api.lexer.JSXLexerException;
import com.io7m.jsx.api.lexer.JSXLexerInvalidCodePointException;
import com.io7m.jsx.api.lexer.JSXLexerNewLinesInStringsException;
import com.io7m.jsx.api.lexer.JSXLexerNotHexCharException;
import com.io7m.jsx.api.lexer.JSXLexerType;
import com.io7m.jsx.api.lexer.JSXLexerUnexpectedEOFException;
import com.io7m.jsx.api.lexer.JSXLexerUnknownEscapeCodeException;
import com.io7m.jsx.api.tokens.TokenComment;
import com.io7m.jsx.api.tokens.TokenEOF;
import com.io7m.jsx.api.tokens.TokenLeftParenthesis;
import com.io7m.jsx.api.tokens.TokenLeftSquare;
import com.io7m.jsx.api.tokens.TokenQuotedString;
import com.io7m.jsx.api.tokens.TokenRightParenthesis;
import com.io7m.jsx.api.tokens.TokenRightSquare;
import com.io7m.jsx.api.tokens.TokenSymbol;
import com.io7m.jsx.api.tokens.TokenType;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

/**
 * The default implementation of the {@link JSXLexerType} type.
 */

public final class JSXLexer implements JSXLexerType
{
  private final StringBuilder buffer;
  private final JSXLexerConfiguration config;
  private final UnicodeCharacterReaderPushBackType reader;
  private final LexicalPositionMutable<URI> position;
  private final LexicalPositionMutable<URI> buffer_position;
  private State state;
  private JSXLexerComment buffer_comment;

  private JSXLexer(
    final JSXLexerConfiguration c,
    final UnicodeCharacterReaderPushBackType r)
  {
    this.config = Objects.requireNonNull(c, "Configuration");
    this.reader = Objects.requireNonNull(r, "Reader");

    this.state = State.STATE_INITIAL;
    this.buffer =
      new StringBuilder(256);

    final int start = c.startAtLine();
    this.position =
      LexicalPositionMutable.create(start, 0, Optional.empty());
    this.buffer_position =
      LexicalPositionMutable.create(start, 0, Optional.empty());

    this.position.setFile(c.file());
    this.buffer_position.setFile(c.file());
  }

  /**
   * Construct a new lexer.
   *
   * @param c The lexer configuration
   * @param r The unicode character reader
   *
   * @return A new lexer
   */

  public static JSXLexerType newLexer(
    final JSXLexerConfiguration c,
    final UnicodeCharacterReaderPushBackType r)
  {
    return new JSXLexer(c, r);
  }

  private void completeNewline()
  {
    this.state = State.STATE_INITIAL;
    this.position.setLine(this.position.line() + 1);
    this.position.setColumn(0);
  }

  private TokenType completeQuotedString()
  {
    this.state = State.STATE_INITIAL;
    final String text = Objects.requireNonNull(this.buffer.toString(), "Text");
    this.buffer.setLength(0);
    return new TokenQuotedString(this.buffer_position.toImmutable(), text);
  }

  private TokenType completeSymbol()
  {
    this.state = State.STATE_INITIAL;
    final String text = Objects.requireNonNull(this.buffer.toString(), "Text");
    this.buffer.setLength(0);
    return new TokenSymbol(this.buffer_position.toImmutable(), text);
  }

  private TokenType completeComment()
  {
    this.state = State.STATE_INITIAL;
    final String text = Objects.requireNonNull(this.buffer.toString(), "Text");
    this.buffer.setLength(0);
    return new TokenComment(
      this.buffer_position.toImmutable(), this.buffer_comment, text);
  }

  private JSXLexerBareCarriageReturnException errorBareCarriageReturn()
  {
    final StringBuilder sb = new StringBuilder(32);
    sb.append("Bare carriage return (U+000D) in source");
    final String s = Objects.requireNonNull(sb.toString(), "Message");
    return new JSXLexerBareCarriageReturnException(
      this.snapshotPosition(), s);
  }

  private JSXLexerInvalidCodePointException errorInvalidCodePoint(
    final long cp)
  {
    final StringBuilder sb = new StringBuilder(32);
    sb.append("Invalid code point given in escape (U+");
    sb.append(Long.toUnsignedString(cp, 16));
    sb.append(')');
    final String s = Objects.requireNonNull(sb.toString(), "Message");
    return new JSXLexerInvalidCodePointException(this.snapshotPosition(), s);
  }

  private JSXLexerNewLinesInStringsException errorNewLinesNotInQuotedStrings()
  {
    return new JSXLexerNewLinesInStringsException(
      this.snapshotPosition(),
      "Lexer configuration does not permit newlines (U+000A or U+000D) in quoted strings");
  }

  private JSXLexerNotHexCharException errorNotHexChar(
    final int c)
  {
    final StringBuilder sb = new StringBuilder(16);
    sb.append("Expected a character [0123456789aAbBcCdDeEfF] (got ");
    sb.appendCodePoint(c);
    sb.append(')');
    final String s = Objects.requireNonNull(sb.toString(), "Message");
    return new JSXLexerNotHexCharException(this.snapshotPosition(), s);
  }

  private JSXLexerUnexpectedEOFException errorUnexpectedEOF()
  {
    final StringBuilder sb = new StringBuilder(32);
    sb.append("Unexpected EOF");
    final String s = Objects.requireNonNull(sb.toString(), "Message");
    return new JSXLexerUnexpectedEOFException(this.snapshotPosition(), s);
  }

  private JSXLexerUnknownEscapeCodeException errorUnknownEscape(
    final int c)
  {
    final StringBuilder sb = new StringBuilder(64);
    sb.append("Unknown escape code (");
    sb.appendCodePoint(c);
    sb.append(')');
    final String s = Objects.requireNonNull(sb.toString(), "Message");
    return new JSXLexerUnknownEscapeCodeException(
      this.snapshotPosition(), s);
  }

  private void parseEscape()
    throws JSXLexerException, IOException
  {
    final int c = this.readCharNotEOF();
    if (c == (int) '"') {
      this.buffer.append('"');
      return;
    }
    if (c == (int) '\\') {
      this.buffer.append('\\');
      return;
    }
    if (c == (int) 'r') {
      this.buffer.append('\r');
      return;
    }
    if (c == (int) 'n') {
      this.buffer.append('\n');
      return;
    }
    if (c == (int) 't') {
      this.buffer.append('\t');
      return;
    }
    if (c == (int) 'u') {
      this.parseUnicode4();
      return;
    }
    if (c == (int) 'U') {
      this.parseUnicode8();
      return;
    }

    throw this.errorUnknownEscape(c);
  }

  private void parseUnicode4()
    throws JSXLexerException, IOException
  {
    final StringBuilder hexbuf = new StringBuilder(16);
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    final String hex = Objects.requireNonNull(hexbuf.toString(), "Hex code");
    final int code = Integer.parseInt(hex, 16);
    this.buffer.appendCodePoint(code);
  }

  private void parseUnicode8()
    throws JSXLexerException, IOException
  {
    final StringBuilder hexbuf = new StringBuilder(16);
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    hexbuf.appendCodePoint(this.readHexCharNotEOF());
    final String hex = Objects.requireNonNull(hexbuf.toString(), "Hex code");
    final long code = Long.parseUnsignedLong(hex, 16);
    final int cp = (int) code;

    if (!Character.isValidCodePoint(cp)) {
      throw this.errorInvalidCodePoint(code);
    }

    this.buffer.appendCodePoint(cp);
  }

  private int readChar()
    throws IOException
  {
    final int c = this.reader.readCodePoint();
    if (c != -1) {
      this.position.setColumn(this.position.column() + 1);
    }
    return c;
  }

  private int readCharNotEOF()
    throws IOException, JSXLexerUnexpectedEOFException
  {
    final int c = this.readChar();
    if (c == -1) {
      throw this.errorUnexpectedEOF();
    }
    return c;
  }

  /*
   * It is not realistically possible to reduce the cyclomatic complexity
   * of this function, so Checkstyle must be disabled.
   */

  // CHECKSTYLE:OFF
  private int readHexCharNotEOF()
    throws JSXLexerException, IOException
  {
    final int c = this.readCharNotEOF();
    return switch (c) {
      case '0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        'a',
        'A',
        'b',
        'B',
        'c',
        'C',
        'd',
        'D',
        'e',
        'E',
        'f',
        'F' ->
        c;
      default -> throw this.errorNotHexChar(c);
    };
  }
  // CHECKSTYLE:ON

  private void startQuotedString()
  {
    this.state = State.STATE_IN_STRING_QUOTED;
    this.buffer_comment = null;
    this.buffer_position.setColumn(this.position.column());
    this.buffer_position.setLine(this.position.line());
    this.buffer.setLength(0);
  }

  private void startSymbol(
    final int c)
  {
    this.state = State.STATE_IN_SYMBOL;
    this.buffer_comment = null;
    this.buffer_position.setColumn(this.position.column());
    this.buffer_position.setLine(this.position.line());
    this.buffer.setLength(0);
    this.buffer.appendCodePoint(c);
  }

  private void startComment(
    final JSXLexerComment comment)
  {
    this.state = State.STATE_IN_COMMENT;
    this.buffer_comment = comment;
    this.buffer_position.setColumn(this.position.column());
    this.buffer_position.setLine(this.position.line());
    this.buffer.setLength(0);
  }

  @Override
  public TokenType token()
    throws IOException, JSXLexerException
  {
    return this.tokenRead();
  }

  /*
   * It is not realistically possible to reduce the cyclomatic complexity
   * of this function, so Checkstyle must be disabled.
   */

  // CHECKSTYLE:OFF
  private TokenType tokenRead()
    throws
    IOException,
    JSXLexerException
  {
    while (true) {
      switch (this.state) {
        case STATE_IN_COMMENT -> {
          final int c = this.readChar();
          if (c == -1) {
            return this.completeComment();
          }
          if (c == (int) '\n') {
            this.completeNewline();
            return this.completeComment();
          }
          if (c == (int) '\r') {
            this.state = State.STATE_IN_CRLF;
            return this.completeComment();
          }

          this.buffer.appendCodePoint(c);
        }
        case STATE_INITIAL -> {
          final int c = this.readChar();
          if (c == -1) {
            return new TokenEOF(this.snapshotPosition());
          }

          if (c == (int) '\n') {
            this.completeNewline();
            continue;
          }
          if (c == (int) '\r') {
            this.state = State.STATE_IN_CRLF;
            continue;
          }
          if (c == (int) '"') {
            this.startQuotedString();
            continue;
          }

          if (this.appearsToBeComment(c)) {
            continue;
          }

          if (c == (int) '(') {
            return new TokenLeftParenthesis(this.snapshotPosition());
          }
          if (c == (int) ')') {
            return new TokenRightParenthesis(this.snapshotPosition());
          }
          if (c == (int) '[') {
            if (this.config.squareBrackets()) {
              return new TokenLeftSquare(this.snapshotPosition());
            }
          }
          if (c == (int) ']') {
            if (this.config.squareBrackets()) {
              return new TokenRightSquare(this.snapshotPosition());
            }
          }

          if (Character.isSpaceChar(c)) {
            continue;
          }

          this.startSymbol(c);
        }
        case STATE_IN_CRLF -> {
          final int c = this.readCharNotEOF();

          if (c == (int) '\n') {
            this.completeNewline();
            continue;
          }

          throw this.errorBareCarriageReturn();
        }
        case STATE_IN_STRING_QUOTED -> {
          final int c = this.readCharNotEOF();
          if (c == (int) '\\') {
            this.parseEscape();
            continue;
          }
          if ((c == (int) '\r') || (c == (int) '\n')) {
            if (!this.config.newlinesInQuotedStrings()) {
              throw this.errorNewLinesNotInQuotedStrings();
            }
          }
          if (c == (int) '"') {
            return this.completeQuotedString();
          }

          this.buffer.appendCodePoint(c);
        }
        case STATE_IN_SYMBOL -> {
          final int c = this.readChar();
          if (c == -1) {
            return this.completeSymbol();
          }
          if (c == (int) '\n') {
            this.completeNewline();
            return this.completeSymbol();
          }
          if (c == (int) '\r') {
            this.state = State.STATE_IN_CRLF;
            return this.completeSymbol();
          }
          if (c == (int) '"') {
            final TokenType s = this.completeSymbol();
            this.reader.pushCodePoint(c);
            return s;
          }
          if (c == (int) '(') {
            this.reader.pushCodePoint(c);
            return this.completeSymbol();
          }
          if (c == (int) ')') {
            this.reader.pushCodePoint(c);
            return this.completeSymbol();
          }
          if (c == (int) '[') {
            if (this.config.squareBrackets()) {
              this.reader.pushCodePoint(c);
              return this.completeSymbol();
            }
          }
          if (c == (int) ']') {
            if (this.config.squareBrackets()) {
              this.reader.pushCodePoint(c);
              return this.completeSymbol();
            }
          }

          if (Character.isSpaceChar(c)) {
            return this.completeSymbol();
          }

          this.buffer.appendCodePoint(c);
        }
      }
    }
  }
  // CHECKSTYLE:ON

  private boolean appearsToBeComment(
    final int c)
  {
    for (final JSXLexerComment comment : this.config.comments()) {
      if (comment.token() == c) {
        this.startComment(comment);
        return true;
      }
    }
    return false;
  }

  private LexicalPosition<URI> snapshotPosition()
  {
    return this.position.toImmutable();
  }

  private enum State
  {
    STATE_IN_CRLF,
    STATE_IN_STRING_QUOTED,
    STATE_IN_SYMBOL,
    STATE_IN_COMMENT,
    STATE_INITIAL
  }
}
