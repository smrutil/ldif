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

package de.fuberlin.wiwiss.r2r.parser;

/**
 * Created by IntelliJ IDEA.
 * User: andreas
 * Date: 03.05.11
 * Time: 19:04
 * To change this template use File | Settings | File Templates.
 */

public class NodeTriple {
	private Node subject, predicate, object;

	public NodeTriple(Node subject, Node predicate, Node object) {
	  super();
	  this.subject = subject;
	  this.predicate = predicate;
	  this.object = object;
  }

	public Node getSubject() {
  	return subject;
  }

	public Node getPredicate() {
  	return predicate;
  }

	public Node getObject() {
  	return object;
  }

	public String toString() {
		return subject + " " + predicate + " " + object;
	}
}
