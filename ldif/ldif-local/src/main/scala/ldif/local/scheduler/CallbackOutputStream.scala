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

package ldif.local.scheduler

import org.semanticweb.yars.util.CallbackNxOutputStream
import java.io.{IOException, OutputStream}
import org.semanticweb.yars.nx.Node
import collection.mutable.{HashSet, Set}

class CallbackOutputStream(val out : OutputStream) extends CallbackNxOutputStream(out) {

  var statements = 0
  var graphs : Set[String] = new HashSet[String]
  val space = " ".getBytes
	val dot_nl = ("."+System.getProperty("line.separator")).getBytes

  override def processStatement(nodes : Array[Node]) {
    try {
      for(n <- nodes){
        out.write(n.toN3.getBytes)
        out.write(space)
      }
      //graphs += nodes(3).toString
      out.write(dot_nl)
    } catch {
      case e:IOException => {
        e.printStackTrace()
        throw new RuntimeException(e)
      }
    }
    //super.processStatement(nodes)
    graphs += nodes(3).toString
    statements += 1
  }
}