package es.weso.schema
import es.weso.rdf.PrefixMap
import es.weso.rdf.nodes._

import util._

abstract class ValidationTrigger {

  def explain: String
  def extractNode: String =
    maybeFocusNode.getOrElse("")

  def extractShape: String =
    maybeShape.getOrElse("")

  def maybeFocusNode: Option[String]
  def maybeShape: Option[String]

  def name: String
}

/**
 * Validates only scope declarations
 */
case object TargetDeclarations extends ValidationTrigger {
  override def explain = "Only SHACL target declarations"
  override def maybeFocusNode = None
  override def maybeShape = None
  override def name = "TargetDecls"
}

/**
 * Validates a node against a specific shape
 */
case class NodeShape(node: IRI, shape: String) extends ValidationTrigger {
  override def explain = "A node with a shape"
  override def maybeFocusNode = Some(node.toString)
  override def maybeShape = Some(shape)
  override def name = "NodeShape"
}


/**
  * Validates a nodes against the start shape
  */
case class NodeStart(node: RDFNode) extends ValidationTrigger {
  override def explain = "Nodes agains start shape"
  override def maybeFocusNode = Some(node.toString)
  override def maybeShape = None
  override def name = "NodeStart"
}

object ValidationTrigger {

 lazy val default: ValidationTrigger = TargetDeclarations

 def nodeShape(node: String, shape: String): ValidationTrigger =
   NodeShape(IRI(node), shape)

 lazy val targetDeclarations: ValidationTrigger = TargetDeclarations

 def findTrigger(name: String,
                 node: Option[String],
                 shape: Option[String],
                 pm: PrefixMap = PrefixMap.empty): Either[String,ValidationTrigger] = {
   (name.toUpperCase,node,shape) match {
     case ("TARGETDECLS",_,_) => Right(TargetDeclarations)
     case ("NODESHAPE",Some(node),Some(shape)) => {
       val eitherNode = removeLTGT(node,pm)
       val eitherShape = removeLTGT(shape,pm)
       (eitherNode,eitherShape) match {
         case (Right(iriNode),Right(iriShape)) => Right(NodeShape(iriNode,iriShape.str))
         case (Left(e), Right(_)) => Left(e)
         case (Right(_), Left(e)) => Left(e)
         case (Left(e1),Left(e2)) => Left(e1 + "\n" + e2)
       }
     }
     case ("NODESTART",Some(node),_) => {
       removeLTGT(node,pm).right.map(NodeStart(_))
     }
     case _ =>
       Left(s"Cannot understand trigger mode\ntrigger = $name, node: $node, shape: $shape")
   }
 }

 /*
  * Remove < and > from string...if it is: "<http://example.org> -> http://example.org"
  */
 def removeLTGT(str: String, pm: PrefixMap): Either[String,IRI] = {
   val iriPattern = "<(.*)>".r
   str match {
     case iriPattern(c) => Right(IRI(c))
     case _ => pm.qname(str) match {
       case None =>
         Left(s"Can't obtain IRI from $str. Available prefixes: ${pm.prefixes.mkString(",")}")
       case Some(iri) =>
         Right(iri)
       }
   }
 }

 def triggerValues: List[(String,String)] = {
   List(
     TargetDeclarations,
     NodeShape(IRI(""),""),
     NodeStart(IRI(""))).map(
      vt => (vt.name,vt.explain)
     )
 }

}
