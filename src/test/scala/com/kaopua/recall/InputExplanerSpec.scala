package com.kaopua.recall
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.BeforeAndAfter
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squeryl._
import org.squeryl.adapters.H2Adapter

class InputExplainerSpec extends FlatSpec with ShouldMatchers {
  val logger = LoggerFactory.getLogger(this.getClass())

  "InputExplainer" should "explain userinput using 'Command'" in {
    InputExplainer.explain(":h") should be(Help())
    InputExplainer.explain("testHint=testContent") should be(Mark("testHint", "testContent"))
    InputExplainer.explain("testHint=None") should be(Remove("testHint"))
    InputExplainer.explain("testHint") should be(Recall("testHint", Interpreter.RECALL_MODE_PLAIN))
    //    InputExplainer.explain("+testContent") should be(Append("testContent"))
    //following tests need to be run sequentially
    InputExplainer.lastMemory = Memory(0, "testHint", "testContent", 1, new java.util.Date())
    InputExplainer.explain("testHint.*") should be(Recall("testHint", Interpreter.RECALL_MODE_RECURSIVE))
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
    InputExplainer.explain("notyy.name=notyy") should be(PlainText(" " * 5 + "."))
    InputExplainer.explain("tel=8000000") should be(PlainText(" " * 5 + "."))
    InputExplainer.explain("mobile=1380000") should be(PlainText(" " * 5 + "."))
    InputExplainer.explain("address.city=shanghai") should be(PlainText(" " * 13 + "."))
    InputExplainer.explain("country=china") should be(PlainText(" " * 13 + "."))
    InputExplainer.explain("") should be(PlainText(" " * 5 + "."))
    logger.debug("should be EndMark? {}", InputExplainer.explain(""))
    InputExplainer.explain(":q") should be(Quit())
  }
}