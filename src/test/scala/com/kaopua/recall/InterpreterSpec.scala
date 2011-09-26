package com.kaopua.recall

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.BeforeAndAfter
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class InterpreterSpec extends FlatSpec with ShouldMatchers {
  val logger = LoggerFactory.getLogger(this.getClass())

  "Interpreter" should " response to user input(cause side effect,will be test manually)" is (pending)

  it should "show content in single line without index when having single content" in {
    val memory = Memory(0, "testHint", "testContent", 1, new java.util.Date())
    Interpreter.strContent(memory) should be("testContent\n")
  }

  it should "show content recursively is using :r command" is (pending)
}

