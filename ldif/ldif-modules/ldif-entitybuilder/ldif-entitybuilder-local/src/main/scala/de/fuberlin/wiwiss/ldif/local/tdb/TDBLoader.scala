package de.fuberlin.wiwiss.ldif.local.tdb

import com.hp.hpl.jena.tdb.store.bulkloader2.{CmdIndexBuild, CmdNodeTableBuilder}
import java.io._

/**
 * Created by IntelliJ IDEA.
 * User: andreas
 * Date: 05.07.11
 * Time: 11:24
 * To change this template use File | Settings | File Templates.
 */

class TDBLoader {
  val k1 = "-k 1,1"
  val k2 = "-k 2,2"
  val k3 = "-k 3,3"
  val k4 = "-k 4,4"


//  def generateStatistics(rootDir: String): Unit = {
//    val statsFile = new File(rootDir, "stats.opt")
//    val oldOut = System.out
//    System.setOut(
//      new PrintStream(
//        new BufferedOutputStream(
//          new FileOutputStream(statsFile))));
//
//    tdb.tdbconfig.main("stats", "--loc " + rootDir)
//    System.setOut(oldOut)
//  }

  def indexData(rootDir: String, datatriples: String, dataquads: String): Unit = {
    processRows(rootDir, makeRowString(k1, k2, k3), datatriples, "SPO")
    processRows(rootDir, makeRowString(k2, k3, k1), datatriples, "POS")
    processRows(rootDir, makeRowString(k3, k1, k2), datatriples, "OSP")
    processRows(rootDir, makeRowString(k1, k2, k3, k4), dataquads, "GSPO")
    processRows(rootDir, makeRowString(k1, k3, k4, k2), dataquads, "GPOS")
    processRows(rootDir, makeRowString(k1, k4, k2, k3), dataquads, "GOSP")
    processRows(rootDir, makeRowString(k2, k3, k4, k1), dataquads, "SPOG")
    processRows(rootDir, makeRowString(k3, k4, k2, k1), dataquads, "POSG")
    processRows(rootDir, makeRowString(k4, k2, k3, k1), dataquads, "OSPG")
  }

  def loadData(rootDir: String, datatriples: String, dataquads: String, dataFile: String): Unit = {
    CmdNodeTableBuilder.main("--loc=" + rootDir, "--triples=" + datatriples, "--quads=" + dataquads, dataFile)
  }

  def createNewTDBDatabase(tdbRoot: String, databaseRoot: String, datasetFile: String) {
    val time1 = System.currentTimeMillis
    val dataTriples = tdbRoot + "/data-triples.tmp"
    val dataQuads = tdbRoot + "/data-quads.tmp"
    val rootDir = databaseRoot
    val dataFile = datasetFile
    val dt = new File(dataTriples)
    val dq = new File(dataQuads)
    dt.createNewFile
    dt.delete
    dt.createNewFile
    dq.createNewFile
    dq.delete
    dq.createNewFile


    println("-- TDB Bulk Loader Start")
    println("-- Data phase")
    cleanTarget(tdbRoot, rootDir)

    loadData(rootDir, dataTriples, dataQuads, dataFile)

    println("-- Index phase")
    indexData(rootDir, dataTriples, dataQuads)

    // Calculate overall processing time
    val timeSpan = System.currentTimeMillis - time1
    println("-- TDB Bulk Loader Finish")
    println("-- " + String.format("%.2f", (timeSpan/1000.0).asInstanceOf[AnyRef]) + "s\n")

    // Clean up
    dt.delete
    dq.delete
  }

  def cleanTarget(tdbRoot: String, databaseRoot: String) {
    val commandString = tdbRoot + "/bin/tdbclean " + databaseRoot
    executeCommand(commandString)
  }

  def processRows(rootDir: String, rowString: String, file: String, index: String) {
    val keys = rowString
    val data = file
    if(new File(data).length==0)
      return

    val idx = index
    val work = "/database/tdbtest/" + idx + "-txt"
    new File(work).createNewFile

    val sortString = "sort " + keys + " < " + data + " > " + work

    println("Index " + idx)
    val returnCode = executeCommand(sortString)

    new File(rootDir, idx + ".dat").delete
    new File(rootDir, idx + ".idn").delete

    println("Build " + idx)
    CmdIndexBuild.main(rootDir, idx, work)

    // Clean up
    new File(work).delete
  }

  def executeCommand(command: String) {
    try {
      val process = Runtime.getRuntime().exec(Array[String]("/bin/sh", "-c",command));
      val in = new BufferedReader(new InputStreamReader(process.getInputStream()));
      var line: String = in.readLine;

      while (line != null) {
        System.out.println(line);
        line = in.readLine
      }
      val returnCode = process.waitFor()
      if(returnCode!=0)
        throw new RuntimeException("Error while loading dataset into TDB for command: " + command)
    } catch {
      case e => throw new RuntimeException("Error while loading dataset into TDB for command: " + command, e)
    }
  }

  def makeRowString(k: String*): String = {
    val sb = new StringBuilder

    for(s <- k)
      sb.append(s).append(" ")

    sb.toString.trim
  }
}

object TDBLoader {
  def main(args: Array[String]) {
    val tdbRoot = "/home/andreas/install/stores/tdb/TDB-0.8.9/"
    val rootDir = "/database/tdbtest"
    val dataFile = "/home/andreas/projects/example/sources/aba_mouse_20101010_1000.nq"
    val loader = new TDBLoader
    loader.createNewTDBDatabase(tdbRoot, rootDir, dataFile)
  }
}