package com.kaopua.recall

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.squeryl.Session

/*
 * this object should be refactored,it's difficult to test now
 */
object Interpreter {
  val logger = LoggerFactory.getLogger(Interpreter.getClass())
  val QUIT = ":QUIT"
  val RECALL_MODE_PLAIN = 1
  val RECALL_MODE_RECURSIVE = 2

  def process(request: String): String = {
    return ""
  }

  def response(command: Command): String = {
    logger.info("response to command: " + command)
    command match {
      case Quit() => Session.cleanupResources; Session.currentSession.close; QUIT
      case Mark(hint, content) => {
        val mm = Memory.mark(hint, content)
        logger.debug("memory saved {}", mm)
        hint + "=" + content + " marked in my memory\n"
      }
      case EndMark(marks) => ("" /: marks)(_ + response(_))
      case Remove(hint) => Memory.remove(hint); hint + " removed\n"
      case Recall(hint, RECALL_MODE_RECURSIVE) => {
        strRecursive(hint)
      }
      case Recall(hint, RECALL_MODE_PLAIN) => {
        Memory.recall(hint) match {
          case Some(m: Memory) => {
            logger.debug("recalled by hint {} is {} ", hint, m);
            strContent(m)
          }
          case None => {
            logger.info("memory not found for hint {} , now using fuzzy recall", hint)
            val rs = Memory.fuzzyRecall(hint)
            if (rs.size > 0) {
              ("" /: rs)(_ + _.hint + "\t") + "\n";
            } else {
              logger.debug("fuzzy recall found nothing for hint {}", hint)
              "memory not found for " + hint + "\n"
            }
          }
        }
      }
    }
  }

  def strRecursive(hint: String): String = {
    var sb = new StringBuilder()
    val mList = Memory.findSub(hint + ".")
    if (mList.isEmpty) sb.append("no memory found for hint " + hint + "\n")
    else mList.foreach(memory => sb.append(memory.hint + "=" + memory.content + "\n"))
    sb.mkString
  }

  def strContent(memory: Memory): String = {
    memory.content + "\n"
  }
}

