package com.kaopua.recall

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.squeryl.PrimitiveTypeMode._
import org.squeryl._
import MemoryDb.memories
import org.squeryl.adapters.H2Adapter

case class Memory(val id: Int, val hint: String, var content: String, var level: Int,
  var lastUpdate: java.util.Date) extends KeyedEntity[Int] {

}

object Memory {
  val logger = LoggerFactory.getLogger(Memory.getClass())

  //  {
  //    logger.info("init session in Memory")
  //    Class.forName("org.h2.Driver");
  //
  //    SessionFactory.concreteFactory = Some(() =>
  //      Session.create(
  //        java.sql.DriverManager.getConnection("jdbc:h2:~/recall_test"),
  //        new H2Adapter))
  //    //    SessionFactory.newSession.bindToCurrentThread
  //  }

  def mark(hint: String, content: String): Memory = {
    logger.info("mark: hint={} ,content={}", hint, content)
    val m = recall(hint)
    m match {
      case Some(am: Memory) => am.content = content; update(am)
      case None => {
        transaction {
          memories.insert(Memory(0, hint, content, 1, new java.util.Date()))
        }
      }
    }
  }

  def remove(hint: String) = {
    recall(hint) match {
      case Some(m: Memory) => memories.delete(m.id)
      case None =>
    }
  }

  def update(memory: Memory): Memory = {
    logger.info("update memory {}", memory)
    transaction {
      memories.update(memory)
    }
    return memory
  }

  def recall(hint: String): Option[Memory] = {
    logger.info("recalling for hint {}", hint)
    transaction {
      val rs =
        from(MemoryDb.memories)(m => where(m.hint === hint) select (m))
      if (rs.size >= 1) Some(rs.head)
      else None
    }
  }

  def fuzzyRecall(hint: String): List[Memory] = {
    logger.info("fuzzy recalling {}", hint);
    transaction {
      from(MemoryDb.memories)(m => where(m.hint like "%" + hint + "%") select (m)).toList
    }
  }

  def findSub(hint: String): List[Memory] = {
    logger.info("find sub memory for hint {}", hint);
    transaction {
      from(MemoryDb.memories)(m => where(m.hint like hint + "%") select (m)).toList
    }
  }

  def list(): List[String] = {
    logger.info("listing hints")
    transaction {
      from(MemoryDb.memories)(m => select(m.hint)).toList
    }
  }
}
