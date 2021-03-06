package es.weso.shacl

import org.scalatest._
import es.weso.rdf.nodes._

class AbstractSyntaxTest extends FunSpec with Matchers {

describe("Abstract Syntax") {
  it("should be able to create a shape") {
    val c : Constraint =
      PropertyConstraint(
                id = None,
                predicate = IRI("http://example.org/p"),
                components =
                  List(NodeKind(IRIKind),
                       MinCount(1),
                       MaxCount(2)))

    val shape = Shape(
        id = Some(IRI("http://example.org/s")),
        targets = List(),
        filters = List(),
        constraints = List(c),false,List())

    shape.id shouldBe defined

  }

  }
}
