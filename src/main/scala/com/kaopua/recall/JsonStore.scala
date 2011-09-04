package com.kaopua.recall
import java.io._
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Serialization.{read, write}
import scala.io._
import com.weiglewilczek.slf4s._

object JsonStore {

  val logger = Logger("com.kaopua.recall.JsonStore")
  def srcDir = System.getProperty("user.home") + "/com.kaopua.recall"
  def srcFile = srcDir + "/memory.json"

  def loadMemories(): Map[String,Memory] = {
    implicit val formats = DefaultFormats
    val file = new File(srcFile)
      if(file.exists() && file.isFile()) {
        val s = Source.fromFile(file)
        //logger.info("file: " + file.getName() + " length: " + s.length +" ,content is " + s.mkString)
        logger.info("is s empty:" + s.isEmpty) 
        if(!s.isEmpty) {
          logger.info("file not empty,will deserialize it")
          val mm = read[Map[String,Memory]](s.mkString)
          logger.info("rs is " + mm)
          s.close()
          return mm
        } else logger.info("file content is empty");Map[String,Memory]()
      } else logger.info("file not exist");Map[String,Memory]()
  }

  def storeMemories(memoryMap:Map[String,Memory]) = {
    logger.info("storing memories:" + memoryMap)
    val file = new File(srcFile)
    if(! (file.exists() && file.isFile())) {
      val dir = new File(srcDir)
      dir.mkdir()
      file.createNewFile()
    }
    val pw = new PrintWriter(file)
    implicit val formats = DefaultFormats
    write(memoryMap, pw)
    pw.close()
  }
}
