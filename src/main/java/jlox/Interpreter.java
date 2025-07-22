package jlox;

import jlox.Expression.*;

public class Interpreter implements Visitor<Object> {

  private Object evaluate(Expression expression) {
    return expression.accept(this);
  }

  private boolean isEqual(Object left, Object right) {
    if (left == null && right == null)
      return true;
    if (left == null)
      return false;

    return left.equals(right);
  }

  private boolean isThruthy(Object obj) {
    if (obj == null)
      return false;
    if (obj instanceof Boolean)
      return ((boolean) obj);
    if (obj instanceof Double)
      return !((Double) obj).isNaN() || ((Double) obj).doubleValue() != 0.0;
    if (obj instanceof String)
      return ((String) obj).length() > 0;

    return true;
  }

  @Override
  public Object visitBinaryExpression(Binary expression) {
    Object left = evaluate(expression.left);
    Object right = evaluate(expression.right);

    switch (expression.operator.type) {
      case GREATER:
        return ((double) left) > ((double) right);
      case GREATER_EQUAL:
        return (double) left >= (double) right;
      case LESS:
        return ((double) left) < ((double) right);
      case LESS_EQUAL:
        return (double) left <= (double) right;
      case EQUAL_EQUAL:
        return isEqual(left, right);
      case BANG_EQUAL:
        return !isEqual(left, right);

      case MINUS:
        return (double) left - (double) right;
      case SLASH:
        return (double) left / (double) right;
      case STAR:
        return (double) left * (double) right;
      case PLUS:
        if (left instanceof Double && right instanceof Double)
          return (double) left + (double) right;

        if (left instanceof String && right instanceof String)
          return (String) left + (String) right;
      default:
        return null;
    }
  }

  @Override
  public Object visitGroupingExpression(Grouping expression) {
    return evaluate(expression);
  }

  @Override
  public Object visitLiteralExpression(Literal expression) {
    return expression.value;
  }

  @Override
  public Object visitUnaryExpression(Unary expression) {
    Object right = evaluate(expression.right);

    switch (expression.operator.type) {
      case MINUS:
        return -(double) right;
      case BANG:
        return !isThruthy(right);

      default:
        return null;
    }

  }

  @Override
  public Object visitCommaExpression(Comma expression) {
    Object result = null;
    for (Expression exp : expression.expressions) {
      result = evaluate(exp);
    }

    return result;
  }

  @Override
  public Object visitTernaryExpression(Ternary expression) {
    Object condition = evaluate(expression.condition);

    // lol
    return evaluate(isThruthy(condition) ? expression.left : expression.right);
  }

}
