package com.kaopua.recall

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.squeryl.Session

/*
 * this object should be refactored,it's difficult to test now
 */
object Interpreter {
  val logger =  LoggerFactory.getLogger(Interpreter.getClass())
  var lastMemory:Memory = null
  def welcome() = println(
    """welcome to recall
input xxx=some content to mark a memory
input xxx to recall it, 
input :h for list of comands
      :q to quit""")

  def askForCommand():Unit = {
    print("recall>")
    response(explain(readLine()))
    askForCommand()
  }
  
  def explain(userInput:String): Command = {
    logger.info("explaining userInput: " + userInput)
    val SubContentPattern = """_(\d+)""".r
    val SubMarkPattern = """_(\d+)=(.*)""".r
    userInput match {
      case ":h" => Help()
      case ":q" => Quit()
      case "_"  => LastMemory()
      case s if s.startsWith("_+") => Accumulate(s.stripPrefix("_+"))
      case SubContentPattern(i) => SubContent(lastMemory,i.toInt)
      case SubMarkPattern(i,content) => {
        lastMemory.getSubContent(i.toInt) match {
          case Some(subHint:String) => SubMark(subHint,content)
          case None => Error("subcontent not found by index " + i)
        }
      }
      case _    => if(userInput.contains("=")) { 
                      val markArray = userInput.split("=")
                      Mark(markArray(0),markArray(1))
                   } else Recall(userInput)
      }
  }

  def response(command:Command) = {
    logger.info("response to command: " + command)
    command match {
      case Help()  => welcome()
      case Quit()  => Session.cleanupResources;Session.currentSession.close;System.exit(0)
      case Mark(hint,content) => {
        val mm = Memory.mark(hint,content)
        logger.debug("memory saved {}",mm)
        lastMemory = mm
        println(hint + "=" + content + " marked in my memory")
      }
      case LastMemory() => if(lastMemory!=null) print(strContent(lastMemory)) else println("no last memory found")
      case SubContent(memory,index) => {
        println(memory.getSubContent(index).getOrElse("None"))
      }
      case SubMark(hint,content) => {
        //doesn't change last memory constant
        val mm = Memory.mark(hint,content)
        logger.debug("memory saved {}",mm)
        println(hint + "=" + content + " marked in my memory")
      }
      case Accumulate(moreContent:String) => {
        if(lastMemory != null) {
          lastMemory.append(moreContent)
          Memory.update(lastMemory)
        }
      }
      case Recall(hint) => {
        Memory.recall(hint) match {
          case Some(m:Memory) => {
            logger.debug("recalled by hint {} is {} ",hint,m); 
            lastMemory = m; 
            print(strContent(m))
          }
          case None => {
            logger.info("memory not found for hint {} , now using fuzzy recall",hint)
            val rs = Memory.fuzzyRecall(hint)
            if(rs.size>0) {
              rs.foreach(m => print(m.hint + "\t"));println("");
            }else{
              logger.debug("fuzzy recall found nothing for hint {}",hint)
              println("memory not found for " + hint)
            }
          }
        }
      }
    }
  }
  
  def strContent(memory:Memory):String = {
    var i = 1
    var contents = memory.content.split(Memory.CONTENT_SEPERATOR)
    var sb = new StringBuilder()
    sb.append("hint:"+memory.hint+"\n")
    sb.append("content:\n")
    for(content <- contents){
      sb.append("  (" + i + ")"+content+"\n");
      i+=1
    }
    sb.mkString
  }
}
