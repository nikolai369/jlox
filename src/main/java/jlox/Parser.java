package jlox;

import java.util.ArrayList;
import java.util.List;
import jlox.Expression.*;

import static jlox.TokenType.*;

public class Parser {
  public static class ParseError extends RuntimeException {
  }

  private List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public Expression parse() {
    try {
      return comma();
    } catch (ParseError e) {
      return null;
    }
  }

  private Expression comma() {
    Expression expr = expression();

    if (peek().type != COMMA) {
      return expr;
    }

    List<Expression> exprs = new ArrayList<>();
    exprs.add(expr);

    while (match(COMMA)) {
      Expression next = expression();
      exprs.add(next);
    }

    return new Comma(exprs);
  }

  private Expression expression() {
    return ternary();
  }

  // tern -> equal ? expr : expr
  private Expression ternary() {
    Expression expr = equality();
    if (!match(Q_MARK))
      return expr;

    Expression right = expression();
    consume(COLON, "Expected ':' after expression");
    Expression left = expression();

    return new Ternary(expr, left, right);
  }

  private Expression equality() {
    Expression expr = comparison();
    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      Token operator = previous();
      Expression right = comparison();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  private Expression comparison() {
    Expression expr = term();
    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      Token operator = previous();
      Expression right = term();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  private Expression term() {
    Expression expr = factor();
    while (match(MINUS, PLUS)) {
      Token operator = previous();
      Expression right = factor();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  private Expression factor() {
    Expression expr = unary();
    while (match(SLASH, STAR)) {
      Token operator = previous();
      Expression right = unary();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  private Expression unary() {
    if (match(MINUS, BANG)) {
      Token operator = previous();
      Expression expr = unary();
      return new Unary(operator, expr);
    }

    return primary();
  }

  private Expression primary() {
    if (match(TRUE))
      return new Literal(true);
    if (match(FALSE))
      return new Literal(false);
    if (match(NIL))
      return new Literal(null);

    if (match(STRING, NUMBER)) {
      Token token = previous();
      return new Literal(token.literal);
    }

    if (match(LEFT_PAREN)) {
      Expression expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return expr;
    }

    throw error(peek(), "Expect expression");
  }

  private Token consume(TokenType type, String message) {
    if (check(type))
      return advance();

    throw error(peek(), message);
  }

  private ParseError error(Token token, String message) {
    Lox.error(token, message);
    return new ParseError();
  }

  // Discard unwanted tokens and continue parsing
  private void synchroinze() {
    // Go to the next token
    advance();

    while (!isAtEnd()) {

      // Statment boundary found, finish sync
      if (peek().type == SEMICOLON)
        return;

      switch (peek().type) {
        case FOR:
        case IF:
        case WHILE:
        case VAR:
        case FUN:
        case PRINT:
        case RETURN:
          return;
        default:
      }

      advance();
    }
  }

  private boolean match(TokenType... types) {

    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }

  private boolean check(TokenType type) {
    if (isAtEnd())
      return false;
    return peek().type == type;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  private Token advance() {
    if (!isAtEnd())
      current++;

    return previous();
  }

  private Token previous() {
    return tokens.get(current - 1);
  }

}
