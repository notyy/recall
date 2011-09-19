package com.kaopua.recall

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.BeforeAndAfter
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class InterpreterSpec extends FlatSpec with ShouldMatchers {
  val logger = LoggerFactory.getLogger(this.getClass())

  "Interpreter" should " response to user input(cause side effect,will be test manually)" is (pending)

  it should "explain userinput using 'Command'" in {
    Interpreter.explain(":h") should be(Help())
    Interpreter.explain(":q") should be(Quit())
    Interpreter.explain("testHint=testContent") should be(Mark("testHint", "testContent"))
    Interpreter.explain("testHint=None") should be(Remove("testHint"))
    Interpreter.explain("testHint") should be(Recall("testHint", Interpreter.RECALL_MODE_PLAIN))
    Interpreter.explain("_") should be(LastMemory())
    //    Interpreter.explain("+testContent") should be(Append("testContent"))
    //following tests need to be run sequentially
    Interpreter.explain("_+testContent") should be(Accumulate("testContent"))
    Interpreter.lastMemory = Memory(0, "testHint", "testContent", 1, new java.util.Date())
    Interpreter.explain("_1") should be(SubContent(Interpreter.lastMemory, 1))
    Interpreter.explain("_1=subContent") should be(SubMark("testContent", "subContent"))
    Interpreter.explain("_2=subcontent2") should be(Error("subcontent not found by index 2"))
    Interpreter.explain("testHint.*") should be(Recall("testHint", Interpreter.RECALL_MODE_RECURSIVE))
  }

  it should "ask for more input when the given hint has '.' inside it" in {
    //simulate user input following contents
    /*   
	notyy.name=notyy
         .tel=80000
         .mobile=1380000
         .address.city=shanghai
                 .country=china
                 .
         .
    //completed
	*/
    Interpreter.explain("notyy.name=notyy") should be(Continue(" " * 5))
    Interpreter.explain("tel=8000000") should be(Continue(" " * 5))
    Interpreter.explain("mobile=1380000") should be(Continue(" " * 5))
    Interpreter.explain("address.city=shanghai") should be(Continue(" " * 13))
    Interpreter.explain("country=china") should be(Continue(" " * 13))
    Interpreter.explain("") should be(Continue(" " * 5))
    logger.debug("should be EndMark? {}", Interpreter.explain(""))
  }

  it should "show content in single line without index when having single content" in {
    val memory = Memory(0, "testHint", "testContent", 1, new java.util.Date())
    Interpreter.strContent(memory) should be("testContent\n")
  }

  it should "show content recursively is using :r command" is (pending)
}

