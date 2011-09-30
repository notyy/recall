package com.kaopua.recall
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object WebShell {
  val logger = LoggerFactory.getLogger(ConsoleShell.getClass())
  val prompt = "recall>"
  val explaner = InputExplainer
  val recallServer = Interpreter

  def process(request: String): String = {
    explaner.explain(readLine()) match {
      case Quit() => "session closed,please refresh to open session again"
      case PlainText(s) => prompt + s
      case command: Command => recallServer.response(command)
    }
  }
}