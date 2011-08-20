package org.scalatra

import org.scalatest.FunSuite
import org.scalatest.matchers.MustMatchers

class RailsLikeUrlGeneratorTest extends FunSuite with MustMatchers {

  def url(path: String, params: Pair[String, String]*): String =
    url(path, params.toMap)

  def url(path: String, splat: String, moreSplats: String*): String =
    url(path, Map[String, String](), splat +: moreSplats)

  def url(path: String, params: Map[String, String] = Map(), splats: Iterable[String] = Seq()): String =
    new RailsRouteMatcher(path, "/").reverse(params, splats.toList)

  test("static string") {
    url("/foo") must equal ("/foo")
  }

  test("dynamic segment") {
    url(":foo.example.com", "foo" -> "vanilla") must equal ("vanilla.example.com")
  }

  test("dynamic segment with leading underscore") {
    url(":_foo.example.com", "_foo" -> "vanilla") must equal ("vanilla.example.com")
  }

  test("skip invalid group names: 123") {
    url(":123.example.com") must equal (":123.example.com")
  }

  test("skip invalid group names: $") {
    url(":$.example.com") must equal (":$.example.com")
  }

  test("escaped dynamic segment") {
    url("""\:foo.example.com""") must equal (":foo.example.com")
  }

  test("dynamic segment inside optional segment") {
    url("foo(.:extension)", "extension" -> "json") must equal ("foo.json")
  }

  test("glob segment") {
    url("src/*files", "files" -> "a/b/c.txt") must equal ("src/a/b/c.txt")
  }

  test("glob segment at the beginning") {
    url("*files/foo.txt", "files" -> "/home/thib") must equal ("/home/thib/foo.txt")
  }

  test("glob segment in the middle") {
    url("src/*files/foo.txt", "files" -> "a/b/c") must equal ("src/a/b/c/foo.txt")
  }

  test("multiple glob segments") {
    url("src/*files/dir/*morefiles/foo.txt", "files" -> "a/b", "morefiles" -> "c/d") must equal ("src/a/b/dir/c/b/foo.txt")
  }

  test("escaped glob segment") {
    url("""src/\*files""") must equal ("src/*files")
  }

  test("optional segment") {
    url("/foo(/bar)") must equal ("/foo")
  }

  test("consecutive optional segment") {
    url("/foo(/bar)(/baz)") must equal ("/foo")
  }

  test("multiple optional segments") {
    url("(/foo)(/bar)(/baz)") must equal ("")
  }

  test("escapes optional segment parentheses") {
    url("""/foo\(/bar\)""") must equal ("/foo(/bar)")
  }

  test("escapes one optional segment parenthesis") {
    url("""/foo\((/bar)""") must equal ("/foo(")
  }
}
