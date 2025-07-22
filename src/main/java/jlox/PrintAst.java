package jlox;

import jlox.Expression.Binary;
import jlox.Expression.Comma;
import jlox.Expression.Grouping;
import jlox.Expression.Literal;
import jlox.Expression.Unary;

public class PrintAst {

  public static class AstPrinter implements Expression.Visitor<String> {

    @Override
    public String visitBinaryExpression(Binary expression) {
      return parenthesize(expression.operator.lexeme, expression.left, expression.right);
    }

    @Override
    public String visitGroupingExpression(Grouping expression) {
      return parenthesize("group", expression.expression);
    }

    @Override
    public String visitLiteralExpression(Literal expression) {
      return expression.value == null ? "nill" : expression.value.toString();
    }

    @Override
    public String visitUnaryExpression(Unary expression) {
      return parenthesize(expression.operator.lexeme, expression.right);
    }

    @Override
    public String visitCommaExpression(Comma expression) {
      Expression[] expressions = expression.expressions.toArray(new Expression[0]);
      return parenthesize("comma", expressions);
    }

    private String parenthesize(String name, Expression... expressions) {
      StringBuilder builder = new StringBuilder();

      builder.append("(").append(name);
      for (Expression expression : expressions) {
        builder.append(" ");
        builder.append(expression.accept(this));
      }

      builder.append(")");

      return builder.toString();
    }

  }

  public static void main(String[] args) {
    AstPrinter printer = new AstPrinter();
    Binary exp = new Binary(
        new Unary(new Token(TokenType.MINUS, "-", null, 0), new Literal(420)),
        new Token(TokenType.STAR, "*", null, 0), new Literal(69));

    String result = exp.accept(printer);
    System.out.println(result);
  }
}
