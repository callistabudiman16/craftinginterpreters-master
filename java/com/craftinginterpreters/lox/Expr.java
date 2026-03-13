//> Appendix II expr
package com.craftinginterpreters.lox;

import java.util.List;

abstract class Expr {
  interface Visitor<R> {
    R visitAssignExpr(Assign expr);
    R visitBinaryExpr(Binary expr);
    R visitCallExpr(Call expr);
    R visitFunctionExpr(Function expr);
    R visitGetExpr(Get expr);
    R visitGroupingExpr(Grouping expr);
    R visitLiteralExpr(Literal expr);
    R visitLogicalExpr(Logical expr);
    R visitSetExpr(Set expr);
    R visitSuperExpr(Super expr);
    R visitThisExpr(This expr);
    R visitUnaryExpr(Unary expr);
    R visitVariableExpr(Variable expr);
    R visitListExpr(List expr);
    R visitIndexExpr(Index expr);
    R visitIndexSetExpr(IndexSet expr);
  }

  // Nested Expr classes here...

    static class List extends Expr {
    final java.util.List<Expr> elements;
    List(java.util.List<Expr> elements) { this.elements = elements; }
    @Override <R> R accept(Visitor<R> visitor) { return visitor.visitListExpr(this); }
  }

  static class Index extends Expr {
    final Expr object;
    final Token bracket;   // the '[' token, for error reporting
    final Expr index;
    Index(Expr object, Token bracket, Expr index) {
      this.object = object; this.bracket = bracket; this.index = index;
    }
    @Override <R> R accept(Visitor<R> visitor) { return visitor.visitIndexExpr(this); }
  }

  static class IndexSet extends Expr {
    final Expr object;
    final Token bracket;
    final Expr index;
    final Expr value;
    IndexSet(Expr object, Token bracket, Expr index, Expr value) {
      this.object = object; this.bracket = bracket; this.index = index; this.value = value;
    }
    @Override <R> R accept(Visitor<R> visitor) { return visitor.visitIndexSetExpr(this); }
  }
//> expr-assign
  static class Assign extends Expr {
    Assign(Token name, Expr value) {
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpr(this);
    }

    final Token name;
    final Expr value;
  }
//< expr-assign
//> expr-binary
  static class Binary extends Expr {
    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }

    final Expr left;
    final Token operator;
    final Expr right;
  }
//< expr-binary
//> expr-call
  static class Call extends Expr {
    Call(Expr callee, Token paren, java.util.List<Expr> arguments) {
      this.callee = callee;
      this.paren = paren;
      this.arguments = arguments;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitCallExpr(this);
    }

    final Expr callee;
    final Token paren;
    final java.util.List<Expr> arguments;
  }
//< expr-call
//> expr-function
  static class Function extends Expr {
    Function(java.util.List<Token> params, java.util.List<Stmt> body) {
      this.params = params;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFunctionExpr(this);
    }

    final java.util.List<Token> params;
    final java.util.List<Stmt> body;
  }
//< expr-function
//> expr-get
  static class Get extends Expr {
    Get(Expr object, Token name) {
      this.object = object;
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGetExpr(this);
    }

    final Expr object;
    final Token name;
  }
//< expr-get
//> expr-grouping
  static class Grouping extends Expr {
    Grouping(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
    }

    final Expr expression;
  }
//< expr-grouping
//> expr-literal
  static class Literal extends Expr {
    Literal(Object value) {
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }

    final Object value;
  }
//< expr-literal
//> expr-logical
  static class Logical extends Expr {
    Logical(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLogicalExpr(this);
    }

    final Expr left;
    final Token operator;
    final Expr right;
  }
//< expr-logical
//> expr-set
  static class Set extends Expr {
    Set(Expr object, Token name, Expr value) {
      this.object = object;
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitSetExpr(this);
    }

    final Expr object;
    final Token name;
    final Expr value;
  }
//< expr-set
//> expr-super
  static class Super extends Expr {
    Super(Token keyword, Token method) {
      this.keyword = keyword;
      this.method = method;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitSuperExpr(this);
    }

    final Token keyword;
    final Token method;
  }
//< expr-super
//> expr-this
  static class This extends Expr {
    This(Token keyword) {
      this.keyword = keyword;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitThisExpr(this);
    }

    final Token keyword;
  }
//< expr-this
//> expr-unary
  static class Unary extends Expr {
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }

    final Token operator;
    final Expr right;
  }
//< expr-unary
//> expr-variable
  static class Variable extends Expr {
    Variable(Token name) {
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariableExpr(this);
    }

    final Token name;
  }
//< expr-variable

  abstract <R> R accept(Visitor<R> visitor);
}
//< Appendix II expr
