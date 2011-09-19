package com.kaopua.recall

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.BeforeAndAfterEach
import org.scalatest.BeforeAndAfterAll
import org.scalatest.prop.Checkers
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop._
import org.squeryl._
import org.squeryl.adapters.H2Adapter

class MemorySpec extends FlatSpec with ShouldMatchers with Checkers with BeforeAndAfterAll with BeforeAndAfterEach {

  val memoryObject = Memory
  val logger = LoggerFactory.getLogger(this.getClass())

  override def beforeAll() {
    Class.forName("org.h2.Driver");

    SessionFactory.concreteFactory = Some(() =>
      Session.create(
        java.sql.DriverManager.getConnection("jdbc:h2:~/recall_test"),
        new H2Adapter))
    SessionFactory.newSession.bindToCurrentThread
  }

  override def beforeEach() {
    MemoryDb.create
  }

  override def afterEach() {
    MemoryDb.drop
  }

  override def afterAll() {
    Session.cleanupResources
    Session.currentSession.close
  }

  "MemoryObject" should "return 1 memory after mark which is persisted" in {
    memoryObject.mark("testHint", "testContent") should be('persisted)
  }

  it should "recall the content it marked" in {
    memoryObject.mark("testHint", "testContent")
    memoryObject.recall("testHint").get.content should be("testContent")
    //add scalacheck test
    /* check((hint: String, content: String) => (hint.length() > 0 && content.length() > 0) ==> {
      memoryObject.mark(hint, content)
      memoryObject.recall(hint).get.content == content
    }) */
  }

  it should "remove a memory indexed by hint" in {
    memoryObject.mark("testHint", "testContent")
    memoryObject.recall("testHint").get.content should be("testContent")
    memoryObject.remove("testHint")
    memoryObject.recall("testHint") should be(None)
  }

  it should "overwrite content when marking memory whose hint already exists" in {
    memoryObject.mark("testHint", "testContent")
    memoryObject.recall("testHint").get.content should be("testContent")
    memoryObject.mark("testHint", "testContent_change")
    memoryObject.recall("testHint").get.content should be("testContent_change")
  }

  it should "update the content " in {
    val m = memoryObject.mark("testHint", "testContent")
    m.content += "_more"
    memoryObject.update(m)
    memoryObject.recall("testHint").get.content should be("testContent_more")
  }

  it should "find closing hints when using fuzzy search" in {
    memoryObject.mark("testhintlong", "test");
    memoryObject.mark("hintshort", "test");
    memoryObject.mark("hint", "test");
    memoryObject.fuzzyRecall("hin") should have size (3)
  }

  it should "find sub hints by parent hint" in {
    memoryObject.mark("notyy.name", "notyy")
    memoryObject.mark("notyy.tel=", "80000")
    memoryObject.mark("notyy.address,city", "shanghai")
    memoryObject.mark("Notnotyy.wife", "connie")
    val mlist = memoryObject.findSub("notyy.")
    mlist should have size (3)
  }

  it should "be None when the memory haven't been marked" in {
    memoryObject.mark("testHint", "testContent")
    memoryObject.recall("badHint") should be(None)
  }

  "Memory class" should "append more content to it's current content" in {
    val m = Memory(0, "testHint", "testContent", 1, new java.util.Date())
    m.append("more").content should be("testContent" + Memory.CONTENT_SEPERATOR + "more")
  }

  it should " get it's subcontent by index" in {
    val m = Memory(0, "testHint", "testContent,;secondContent", 1, new java.util.Date())
    m.getSubContent(1) should be(Some("testContent"))
    m.getSubContent(2) should be(Some("secondContent"))
    m.getSubContent(3) should be(None)
  }

}
