/* 
 * LDIF
 *
 * Copyright 2011 Freie Universität Berlin, MediaEvent Services GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ldif.hadoop.config;

import java.io.File
import java.util.Properties
import org.slf4j.LoggerFactory
import xml.{Node, XML}
import ldif.util.{ConfigProperties, Consts, ValidatingXMLReader}

case class HadoopIntegrationConfig(sources : String, linkSpecDir : File, mappingDir : File, sieveSpecDir : File, outputFile : String,  properties : Properties, runSchedule : String) {}

object HadoopIntegrationConfig
{
  private val log = LoggerFactory.getLogger(getClass.getName)

  private val schemaLocation = "xsd/IntegrationJob.xsd"

  def load = new ValidatingXMLReader(fromFile, schemaLocation)

  def fromFile(configFile : File) =
  {
    val baseDir = configFile.getParent
    val xml = XML.loadFile(configFile)

    // Read in properties
    var properties = new Properties
       val propertiesFile = getFile(xml, "properties", baseDir)
       if (propertiesFile != null)
         properties = ConfigProperties.loadProperties(propertiesFile)

    var runSchedule : String = (xml \ "runSchedule" text)
    if (runSchedule == "" || runSchedule == null)
      runSchedule = "onStartup"

    HadoopIntegrationConfig(      //TODO validation
      sources = (xml \ "sources" text),
      linkSpecDir = getFile(xml, "linkSpecifications", baseDir),
      mappingDir = getFile(xml, "mappings", baseDir),
      sieveSpecDir = getFile(xml, "fusion", baseDir),
      outputFile = (xml \ "output" text),
      properties = properties,
      runSchedule = runSchedule
    )
  }

  private def getFile (xml : Node, key : String, baseDir : String) : File = {
    val value : String = (xml \ key text)
    var file : File = null
    if (value != ""){
      val relativeFile = new File(baseDir + Consts.fileSeparator + value)
      val absoluteFile = new File(value)
      if (relativeFile.exists || absoluteFile.exists) {
        if (relativeFile.exists)
          file = relativeFile
        else file = absoluteFile
      }
      else {
        log.warn("\'"+key+"\' path not found. Searched: " + relativeFile.getCanonicalPath + ", " + absoluteFile.getCanonicalPath)
      }
    }
    else{
      log.warn("\'"+key+"\' is not defined in the IntegrationJob config")
    }
    file
  }

}