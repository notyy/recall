package com.kaopua.recall

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.BeforeAndAfter

class MemorySpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {

  val memoryObject = Memory

  before {
    memoryObject.setMemoryMap(Map[String,Memory]())
  }

  "memoryObject" should "initialzed as empty memory store" in {
    memoryObject.memoryMap should be ('empty)
  }

  it should "has 1 memory after mark" in {
    memoryObject.mark("testHint","testContent")
    memoryObject.memoryMap should have size(1)
  }

  it should "recall the content it marked" in {
    memoryObject.mark("testHint","testContent")
    memoryObject.recall("testHint").get.content should be ("testContent")
  }

  it should "be None when the memory haven't been marked" in {
    memoryObject.mark("testHint","testContent")
    memoryObject.recall("badHint") should be (None)
  }
}
