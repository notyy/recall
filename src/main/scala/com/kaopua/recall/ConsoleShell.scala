package com.kaopua.recall
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ConsoleShell {
  val logger = LoggerFactory.getLogger(ConsoleShell.getClass())
  val prompt = "recall>"
  val explaner = InputExplainer
  val recallServer = Interpreter
  val welcomeWords =
    """welcome to recall
input xxx=some content to mark a memory
input xxx to recall it, 
input :h for list of comands
      :q to quit"""

  def welcome(): Unit = {
    println(welcomeWords)
    print(prompt)
  }

  def askForCommand(): Unit = {
    explaner.explain(readLine()) match {
      case Quit() => recallServer.response(Quit()); System.exit(0)
      case Help() => welcome()
      case PlainText(s) => print(prompt + s)
      case command: Command => print(recallServer.response(command)); print(prompt)
    }
    askForCommand()
  }

}