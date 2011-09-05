package com.kaopua.recall

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class MemorySpec extends FlatSpec with ShouldMatchers {

  val memoryObject = Memory

  "memoryObject" should "initialzed as empty memory store" in {
    memoryObject.memoryMap should be ('empty)
  }

  it should "has 1 memory after mark" in {
    memoryObject.mark("testHint","testContent")
    memoryObject.memoryMap should have ('size(1))
  }
}
