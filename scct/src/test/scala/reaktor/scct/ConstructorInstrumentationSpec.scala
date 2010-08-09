package reaktor.scct

class ConstructorInstrumentationSpec extends InstrumentationSpec {
  // TODO: auxiliary constructor instrumentation is in wrong place.
  // TODO: constructor block is instrumented for some reason.
  "Constructor instrumentation" should instrument {
    "basic auxiliary constructors" in {
      offsetsMatch("class @Foo(x: Int) { def @this(s: String) = this(s.toInt) }")
    }
    "complicated auxiliary constructors" in {
      offsetsMatch("class @Foo(s: String) { def @this(x: Int) = this((x + x).toString) }")
    }
    "auxiliary constructors with functions" in {
      offsetsMatch("""|class @X(val s: String) {
                      |  def @this(ii: Int) = this((0 to ii).map(@_.toString).mkString("/"))
                      |}
                      |""".stripMargin)
    }
    "auxiliary constructors with functions, part II" in {
      offsetsMatch("""|class @X(val s: String) {
                      |  def this(ii: Int) = @{
                      |    this((0 to ii).map(cnt => { @println("yeah"); @cnt.toString + " : "}).mkString("/"))
                      |  }
                      |}
                      |""".stripMargin)
    }
    "extending class constructors" in {
      offsetsMatch("class @Foo(x: Int)\nclass @Bar(x: Int, y: Int) extends Foo(x)")
    }
    "multiline constructors" in {
      offsetsMatch("""|class @Example(val id: String) {
                      |  def this(id:String, other:String) = @{
                      |    this(id);
                      |    @setOther(other)
                      |  }
                      |  def setOther(s: String) {
                      |    @println(s);
                      |  }
                      |}""".stripMargin)
    }
  }
}