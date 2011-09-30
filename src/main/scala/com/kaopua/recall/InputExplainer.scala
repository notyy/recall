package com.kaopua.recall
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object InputExplainer {
  val logger = LoggerFactory.getLogger(InputExplainer.getClass())
  val QUIT = ":QUIT"
  val WELCOME = ":WELCOME"
  val STYLE_WEB = "STYLEWEB"
  val STYLE_CONSOLE = "STYLECONSOLE"
  val recallServer = Interpreter;
  var lastMemory: Memory = null
  var indents = List[String]()
  var indent = ""
  var markStack = List[Mark]()

  def explain(userInput: String, style: String): Command = {
    logger.info("explain userInput={},style={}", userInput, style)
    val MarkPattern = """(.*)=(.*)""".r
    (userInput, style) match {
      case (":h", _) => Help()
      case (":q", _) => Quit()
      case ("", STYLE_CONSOLE) => {
        if (indents.size > 1) {
          indents = indents.tail
          PlainText(" " * indents.head.length() + ".")
        } else {
          val marks = markStack
          indents = List[String](); markStack = List[Mark](); this.indent = ""
          EndMark(marks)
        }
      }
      case (MarkPattern(hint, "None"), _) => Remove(hint)
      case (MarkPattern(hint, content), STYLE_CONSOLE) => {
        logger.info("processing hint {},content {}", hint, content)
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
          PlainText(" " * indent.length() + ".")
        } else if (indents.size > 0) {
          markStack = Mark(indents.head + "." + hint, content) :: markStack
          PlainText(" " * indents.head.length() + ".")
        } else Mark(hint, content)
      }
      case (MarkPattern(hint, content), STYLE_WEB) => {
        Mark(hint, content)
      }
      case _ => { //should be some kind of recall
        if (userInput.endsWith(".*")) Recall(userInput.substring(0, userInput.indexOf(".*")), recallServer.RECALL_MODE_RECURSIVE)
        else Recall(userInput, recallServer.RECALL_MODE_PLAIN)
      }
    }
  }

  /**
   * default to console mode
   */
  def explain(userInput: String): Command = {
    logger.info("explaining userInput: " + userInput)
    explain(userInput, STYLE_CONSOLE)
  }

}