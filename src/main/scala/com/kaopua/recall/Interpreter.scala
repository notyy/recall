package com.kaopua.recall

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.squeryl.Session

/*
 * this object should be refactored,it's difficult to test now
 */
object Interpreter {
  val logger = LoggerFactory.getLogger(Interpreter.getClass())

  var lastMemory: Memory = null
  val RECALL_MODE_PLAIN = 1
  val RECALL_MODE_RECURSIVE = 2
  var indents = List[String]()
  var indent = ""
  var markStack = List[Mark]()

  def welcome() = println(
    """welcome to recall
input xxx=some content to mark a memory
input xxx to recall it, 
input :h for list of comands
      :q to quit""")

  def askForCommand(): Unit = {
    print("recall>" + indent)
    response(explain(readLine()))
    askForCommand()
  }

  def explain(userInput: String): Command = {
    logger.info("explaining userInput: " + userInput)
    val MarkPattern = """(.*)=(.*)""".r
    userInput match {
      case ":h" => Help()
      case ":q" => Quit()
      case "_" => LastMemory()
      case s if s.startsWith("_+") => Accumulate(s.stripPrefix("_+"))
      case "" => {
        if (indents.size > 1) {
          indents = indents.tail
          Continue(" " * indents.head.length())
        } else {
          EndMark(markStack)
        }
      }
      case MarkPattern(hint, "None") => Remove(hint)
      case MarkPattern(hint, content) => {
        logger.debug("processing hint {},content {}", hint, content)
        if (hint.contains(".")) {
          logger.debug("hint contains '.'")
          var indent = hint.substring(0, hint.lastIndexOf("."))
          val newMark = indents match {
            case head :: _ =>
              indent = head + "." + indent
              Mark(head + "." + hint, content)
            case _ => Mark(hint, content)
          }
          markStack = newMark :: markStack
          indents = indent :: indents
          Continue(" " * indent.length())
        } else if (indents.size > 0) {
          markStack = Mark(indents.head + "." + hint, content) :: markStack
          Continue(" " * indents.head.length())
        } else Mark(hint, content)
      }
      case _ => { //should be some kind of recall
        if (userInput.endsWith(".*")) Recall(userInput.substring(0, userInput.indexOf(".*")), RECALL_MODE_RECURSIVE)
        else Recall(userInput, RECALL_MODE_PLAIN)
      }
    }
  }

  def response(command: Command): Unit = {
    logger.info("response to command: " + command)
    command match {
      case Help() => welcome()
      case Quit() => Session.cleanupResources; Session.currentSession.close; System.exit(0)
      case Mark(hint, content) => {
        val mm = Memory.mark(hint, content)
        logger.debug("memory saved {}", mm)
        lastMemory = mm
        println(hint + "=" + content + " marked in my memory")
      }
      case Continue(indent) => this.indent = indent + "."
      case EndMark(marks) => marks.foreach(mark => response(mark)); indents = List[String](); markStack = List[Mark](); this.indent = ""
      case Remove(hint) => Memory.remove(hint); println(hint + " removed")
      case LastMemory() => if (lastMemory != null) print(strContent(lastMemory)) else println("no last memory found")
      case SubContent(memory, index) => {
        println(memory.getSubContent(index).getOrElse("None"))
      }
      case SubMark(hint, content) => {
        //doesn't change last memory constant
        val mm = Memory.mark(hint, content)
        logger.debug("memory saved {}", mm)
        println(hint + "=" + content + " marked in my memory")
      }
      case Accumulate(moreContent: String) => {
        if (lastMemory != null) {
          lastMemory.append(moreContent)
          Memory.update(lastMemory)
          println(moreContent + " appended")
        }
      }
      case Recall(hint, RECALL_MODE_RECURSIVE) => {
        print(strRecursive(hint))
      }
      case Recall(hint, RECALL_MODE_PLAIN) => {
        Memory.recall(hint) match {
          case Some(m: Memory) => {
            logger.debug("recalled by hint {} is {} ", hint, m);
            lastMemory = m;
            print(strContent(m))
          }
          case None => {
            logger.info("memory not found for hint {} , now using fuzzy recall", hint)
            val rs = Memory.fuzzyRecall(hint)
            if (rs.size > 0) {
              rs.foreach(m => print(m.hint + "\t")); println("");
            } else {
              logger.debug("fuzzy recall found nothing for hint {}", hint)
              println("memory not found for " + hint)
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

