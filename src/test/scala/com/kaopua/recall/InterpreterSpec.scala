package com.kaopua.recall

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.BeforeAndAfter

class InterpreterSpec extends FlatSpec with ShouldMatchers {
  "Interpreter" should " response to user input(cause side effect,will be test manually)" is (pending)

  it should "explain userinput using 'Command'" in {
    Interpreter.explain(":h") should be(Help())
    Interpreter.explain(":q") should be(Quit())
    Interpreter.explain("testHint=testContent") should be(Mark("testHint", "testContent"))
    Interpreter.explain("testHint") should be(Recall("testHint"))
    Interpreter.explain("_") should be(LastMemory())
    //    Interpreter.explain("+testContent") should be(Append("testContent"))
    Interpreter.explain("_+testContent") should be(Accumulate("testContent"))
    Interpreter.lastMemory = Memory(0, "testHint", "testContent", 1, new java.util.Date())
    Interpreter.explain("_1") should be(SubContent(Interpreter.lastMemory, 1))
  }

}

