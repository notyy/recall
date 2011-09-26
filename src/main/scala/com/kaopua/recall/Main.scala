package com.kaopua.recall

import ConsoleShell._
import org.squeryl._
import org.squeryl.adapters.H2Adapter
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File

object Main extends App {
  val logger = LoggerFactory.getLogger(Main.getClass())
  logger.info("initializing database session")
  Class.forName("org.h2.Driver")
  SessionFactory.concreteFactory = Some(() =>
    Session.create(
      java.sql.DriverManager.getConnection("jdbc:h2:~/com.kaopua/recall"),
      new H2Adapter))
  SessionFactory.newSession.bindToCurrentThread
  //the path is decided by h2 database, these lines here are just to check where the db files 
  //have already been created
  logger.info("checking whether db file is alread created?")
  val dbDir = System.getProperty("user.home") + "/com.kaopua"
  val dbFlag = dbDir + "/recall_initialized"
  val file = new File(dbFlag)
  if (file.exists() && file.isFile()) {
    logger.info("db already initialized! {}", file.getAbsolutePath())
  } else {
    logger.info("db table not initialized,will create new table now")
    MemoryDb.drop
    MemoryDb.create
    file.createNewFile()
  }
  logger.info("db initialized")
  welcome()
  askForCommand()
}
