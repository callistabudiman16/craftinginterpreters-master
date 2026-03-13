package com.craftinginterpreters.lox;

import java.util.Map;

class LoxMixin {
  final String name;
  private final Map<String, LoxFunction> methods;

  LoxMixin(String name, Map<String, LoxFunction> methods) {
    this.name = name;
    this.methods = methods;
  }

  LoxFunction findMethod(String name) {
    return methods.get(name);
  }

  Map<String, LoxFunction> methods() {
    return methods;
  }

  @Override
  public String toString() {
    return "<mixin " + name + ">";
  }
}