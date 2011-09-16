package com.kaopua.recall

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squeryl.PrimitiveTypeMode._
import org.squeryl._
import MemoryDb.memories

case class Memory(val id:Int, val hint:String, var content:String, var level:Int, 
  var lastUpdate:java.util.Date) extends KeyedEntity[Int]{
  
  def append(moreContent:String):Memory = {
    content += (Memory.CONTENT_SEPERATOR + moreContent)
    return this
  }
  
  /*
   * index start from 1
   */
  def getSubContent(index:Int):Option[String] = {
    val contents = content.split(Memory.CONTENT_SEPERATOR)
    if(index > contents.length) None
    else Some(contents(index-1))
  }
}

object Memory {
  val logger = LoggerFactory.getLogger(Memory.getClass())
  val CONTENT_SEPERATOR = ",;"

  def mark(hint:String, content:String): Memory = {
    logger.info("mark: hint={} ,content={}", hint, content)
    val m = recall(hint)
    m match {
      case Some(am:Memory) => am.content = content; update(am)
      case None =>{
        transaction {
        	memories.insert(Memory(0, hint, content, 1, new java.util.Date()))
        }
      }
    }
  }
  
  def update(memory:Memory): Memory = {
    logger.info("update memory {}",memory)
    transaction {
      memories.update(memory)
    }
    return memory
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
