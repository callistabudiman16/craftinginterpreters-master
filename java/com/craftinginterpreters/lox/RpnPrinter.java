package com.craftinginterpreters.lox;

public class RpnPrinter implements Expr.Visitor<String> {

  public String print(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public String visitBinaryExpr(Expr.Binary expr) {
    String left = expr.left.accept(this);
    String right = expr.right.accept(this);
    return left + " " + right + " " + expr.operator.lexeme;
  }

  @Override
  public String visitGroupingExpr(Expr.Grouping expr) {
    return expr.expression.accept(this);
  }

  @Override
  public String visitLiteralExpr(Expr.Literal expr) {
    if (expr.value == null) return "nil";
    return expr.value.toString();
  }

  @Override
  public String visitUnaryExpr(Expr.Unary expr) {
    String right = expr.right.accept(this);
    return right + " " + expr.operator.lexeme;
  }

  @Override
  public String visitVariableExpr(Expr.Variable expr) {
    return expr.name.lexeme;
  }

  @Override
  public String visitAssignExpr(Expr.Assign expr) {
    String value = expr.value.accept(this);
    return value + " " + expr.name.lexeme + " =";
  }

  @Override
  public String visitLogicalExpr(Expr.Logical expr) {
    String left = expr.left.accept(this);
    String right = expr.right.accept(this);
    return left + " " + right + " " + expr.operator.lexeme;
  }

  @Override
  public String visitCallExpr(Expr.Call expr) {
    StringBuilder sb = new StringBuilder();
    for (Expr arg : expr.arguments) {
      sb.append(arg.accept(this)).append(" ");
    }
    sb.append(expr.callee.accept(this));
    sb.append(" call");
    return sb.toString();
  }

  @Override
  public String visitGetExpr(Expr.Get expr) {
    String obj = expr.object.accept(this);
    return obj + " " + expr.name.lexeme + " get";
  }

  @Override
  public String visitSetExpr(Expr.Set expr) {

    String value = expr.value.accept(this);
    String obj = expr.object.accept(this);
    return value + " " + obj + " " + expr.name.lexeme + " set";
  }

  @Override
  public String visitThisExpr(Expr.This expr) {
    return "this";
  }

  @Override
  public String visitSuperExpr(Expr.Super expr) {

    return "super " + expr.method.lexeme;
  }

  @Override
  public String visitFunctionExpr(Expr.Function expr) {
    return "fun";
  }

  @Override
  public String visitListExpr(Expr.List expr) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < expr.elements.size(); i++) {
      if (i > 0) sb.append(", ");
      sb.append(expr.elements.get(i).accept(this));
    }
    sb.append("]");
    return sb.toString();
  }

  @Override
  public String visitIndexExpr(Expr.Index expr) {
    return expr.object.accept(this) + " " + expr.index.accept(this) + " index";
  }

  @Override
  public String visitIndexSetExpr(Expr.IndexSet expr) {
    return expr.value.accept(this) + " " + expr.object.accept(this) + " " +
           expr.index.accept(this) + " index-set";
  }

  public static void main(String[] args) {

    Expr expression =
        new Expr.Binary(
            new Expr.Binary(
                new Expr.Literal(1.0),
                new Token(TokenType.PLUS, "+", null, 1),
                new Expr.Literal(2.0)),
            new Token(TokenType.STAR, "*", null, 1),
            new Expr.Binary(
                new Expr.Literal(4.0),
                new Token(TokenType.MINUS, "-", null, 1),
                new Expr.Literal(3.0)));

    System.out.println(new RpnPrinter().print(expression));

  }
}
