package jlox;

import java.util.List;

abstract class Expression {
  interface Visitor<R> {
    R visitBinaryExpression(Binary expression);

    R visitGroupingExpression(Grouping expression);

    R visitLiteralExpression(Literal expression);

    R visitUnaryExpression(Unary expression);

    R visitCommaExpression(Comma expression);

    R visitTernaryExpression(Ternary expression);

  }

  static class Binary extends Expression {

    Binary(Expression left, Token operator, Expression right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpression(this);
    }

    final Expression left;
    final Token operator;
    final Expression right;
  }

  static class Grouping extends Expression {

    Grouping(Expression expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpression(this);
    }

    final Expression expression;
  }

  static class Literal extends Expression {

    Literal(Object value) {
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpression(this);
    }

    final Object value;
  }

  static class Unary extends Expression {

    Unary(Token operator, Expression right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpression(this);
    }

    final Token operator;
    final Expression right;
  }

  static class Comma extends Expression {

    Comma(List<Expression> expressions) {
      this.expressions = expressions;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitCommaExpression(this);
    }

    final List<Expression> expressions;
  }

  static class Ternary extends Expression {

    Ternary(Expression condition, Expression left, Expression right) {
      this.condition = condition;
      this.left = left;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitTernaryExpression(this);
    }

    final Expression condition;
    final Expression left;
    final Expression right;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
