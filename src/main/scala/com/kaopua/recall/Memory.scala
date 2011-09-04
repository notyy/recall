package com.kaopua.recall
import com.weiglewilczek.slf4s._

case class Memory(hint:String, content:String, level:Int, lastUpdate:java.util.Date)

object Memory {
  val logger = Logger(getClass().getName())
  var memoryMap = Map[String,Memory]()
  def setMemoryMap(mm: Map[String,Memory]) = {
    memoryMap = mm
  }

  def mark(hint:String, content:String): Map[String,Memory] = {
    logger.info("mark: hint=" + hint +" ,content=" + content)
    memoryMap += (hint -> Memory(hint, content, 1, new java.util.Date()))
    logger.info("memoryMap=" + memoryMap)
    memoryMap
  }

  def recall(hint:String): Option[Memory] = {
    logger.info("recalling for hint '" + hint +"' in memories: " + memoryMap)
    if(memoryMap.contains(hint)) Some(memoryMap(hint))
    else None
  }
}