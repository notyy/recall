package com.kaopua.recall
import com.weiglewilczek.slf4s._

object Interpreter {
  val logger = Logger("com.kaopua.recall.Interpreter")
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
      case Quit()  => System.exit(0)
      case Mark(hint,content) => {
        val mm = Memory.mark(hint,content)
        JsonStore.storeMemories(mm)
        mm
      }
      case Recall(hint) => {
        Memory.recall(hint) match {
          case Some(Memory(_, content, _, _)) => println(content)
          case None => println("memory not found for hint '" + hint +"'")
        }
      }
    }
  }
}
