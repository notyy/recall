package com.kaopua.recall

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squeryl.PrimitiveTypeMode._
import org.squeryl._
import MemoryDb.memories

case class Memory(val id:Int, val hint:String, val content:String, val level:Int, 
  val lastUpdate:java.util.Date) extends KeyedEntity[Int]{
}

object Memory {
  val logger = LoggerFactory.getLogger(Memory.getClass())

  def mark(hint:String, content:String): Memory = {
    logger.info("mark: hint={} ,content={}", hint, content)
    transaction {
      memories.insert(Memory(0, hint, content, 1, new java.util.Date()))
    }
  }

  def recall(hint:String): Option[Memory] = {
    logger.info("recalling for hint {} '", hint)
    transaction {
      val rs = 
        from(MemoryDb.memories)(m => where(m.hint === hint) select(m))
      if(rs.size >=1) Some(rs.head)
      else None
    }
  }
  
  def fuzzyRecall(hint:String): List[Memory] = {
    logger.info("fuzzy recalling {}",hint);
    transaction {
        from(MemoryDb.memories)(m => where(m.hint like "%" + hint +"%") select(m)).toList
    }
  }

  def list(): List[String] = {
    logger.info("listing hints")
    transaction{
      from(MemoryDb.memories)(m => select(m.hint)).toList
    }
  }
}
