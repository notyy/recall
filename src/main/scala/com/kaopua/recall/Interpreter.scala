package com.kaopua.recall

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.squeryl.Session

object Interpreter {
  val logger =  LoggerFactory.getLogger(Interpreter.getClass())
  def welcome() = println(
    """welcome to recall
input xxx=some content to mark a memory
input xxx to recall it, 
input :h for list of comands
      :t to start training your brain,
      :s to sync your local memory with your cloud
      :q to quit""")

  def askForCommand():Unit = {
    print("recall>")
    response(explain(readLine()))
    askForCommand()
  }
  
  def explain(userInput:String): Command = {
    logger.info("explaining userInput: " + userInput)
    userInput match {
      case ":h" => Help()
      case ":q" => Quit()
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
        println(hint + "=" + content + " marked in my memory")
      }
      case Recall(hint) => {
        Memory.recall(hint) match {
          case Some(m:Memory) => logger.debug("recalled by hint {} is {} ",hint,m); println(m.content)
          case None => println("memory not found for hint '" + hint +"'")
        }
      }
    }
  }
}
