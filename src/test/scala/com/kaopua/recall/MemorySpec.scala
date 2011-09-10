package com.kaopua.recall

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.BeforeAndAfterEach
import org.scalatest.BeforeAndAfterAll
import org.squeryl._
import org.squeryl.adapters.H2Adapter


class MemorySpec extends FlatSpec with ShouldMatchers with BeforeAndAfterAll with BeforeAndAfterEach{

  val memoryObject = Memory
  val logger = LoggerFactory.getLogger(Memory.getClass())

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
    memoryObject.mark("testHint","testContent") should be ('persisted)
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
