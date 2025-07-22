package jlox;

import jlox.Expression.Binary;
import jlox.Expression.Comma;
import jlox.Expression.Grouping;
import jlox.Expression.Literal;
import jlox.Expression.Ternary;
import jlox.Expression.Unary;

public class RPNAst {

  private static class AstPrinter implements Expression.Visitor<String> {

    @Override
    public String visitBinaryExpression(Binary expression) {
      return print(expression.operator.lexeme, expression.left, expression.right);
    }

    @Override
    public String visitGroupingExpression(Grouping expression) {
      return print("group", expression.expression);
    }

    @Override
    public String visitLiteralExpression(Literal expression) {
      return expression.value == null ? "nill" : expression.value.toString();
    }

    @Override
    public String visitUnaryExpression(Unary expression) {
      return print(expression.operator.lexeme, expression.right);
    }

    @Override
    public String visitCommaExpression(Comma expression) {
      Expression[] expressions = expression.expressions.toArray(new Expression[0]);
      return print("comma", expressions);
    }

    @Override
    public String visitTernaryExpression(Ternary expression) {
      return print("ternary", expression.left, expression.right);
    }

    private String print(String literal, Expression... expressions) {
      StringBuilder builder = new StringBuilder();

      for (Expression expression : expressions) {
        builder.append(expression.accept(this));
      }
      builder.append(literal);
      builder.append(" ");

      return builder.toString();
    }

  }

  public static void main(String[] args) {
    AstPrinter printer = new AstPrinter();
    Binary exp = new Binary(
        new Binary(new Literal(1), new Token(TokenType.MINUS, "+", null, 0), new Literal(2)),
        new Token(TokenType.STAR, "*", printer, 0),
        new Binary(new Literal(1), new Token(TokenType.MINUS, "-", null, 0), new Literal(3)));

    String result = exp.accept(printer);
    System.out.println(result);
  }
}
