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
  val DEFAULT_CONTEXT = "DEFAULT."
  val RECALL_MODE_PLAIN = 1
  val RECALL_MODE_RECURSIVE = 2

  def process(request: String): String = {
    return ""
  }

  /**
   * add context to support multiple user
   * @param command
   * @param context
   * @return
   */
  def response(command: Command, context: String): String = {
    command match {
      case Quit() => Session.cleanupResources; Session.currentSession.close; QUIT
      case Mark(hint, content) => {
        val mm = Memory.mark(context + hint, content) //same memory with a context prefix
        logger.debug("memory saved {}", mm)
        hint + "=" + content + " marked in my memory\n" //omit context, so user con't feel that
      }
      case EndMark(marks) => ("" /: marks)(_ + response(_, context))
      case Remove(hint) => Memory.remove(context + hint); hint + " removed\n"
      case Recall(hint, RECALL_MODE_RECURSIVE) => {
        strRecursive(hint, context)
      }
      case Recall(hint, RECALL_MODE_PLAIN) => {
        Memory.recall(context + hint) match {
          case Some(m: Memory) => {
            logger.debug("recalled by hint {} is {} ", context + hint, m);
            strContent(m)
          }
          case None => {
            logger.info("memory not found for hint {} , now using fuzzy recall", context + hint)
            val rs = Memory.fuzzyRecall(context + hint)
            if (rs.size > 0) {
              ("" /: rs)(_ + _.hint.drop(context.length()) + "\t") + "\n";
            } else {
              logger.debug("fuzzy recall found nothing for hint {}", context + hint)
              "memory not found for " + hint + "\n"
            }
          }
        }
      }
    }
  }

  def response(command: Command): String = {
    logger.info("response to command: " + command)
    response(command, DEFAULT_CONTEXT)
  }

  def strRecursive(hint: String, context: String): String = { //this hint is already prefixed with context
    var sb = new StringBuilder()
    val mList = Memory.findSub(context + hint + ".")
    if (mList.isEmpty) sb.append("no memory found for hint " + hint + "\n")
    else mList.foreach(memory => sb.append(memory.hint + "=" + memory.content + "\n"))
    sb.mkString
  }

  def strContent(memory: Memory): String = {
    memory.content + "\n"
  }
}

