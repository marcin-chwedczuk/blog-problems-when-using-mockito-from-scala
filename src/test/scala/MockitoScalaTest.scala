import org.mockito.ArgumentMatchersSugar._
import org.mockito.DefaultAnswers
import org.mockito.MockitoSugar._
import org.mockito.stubbing.DefaultAnswer
import org.scalatest.funsuite.AnyFunSuite

class MockitoScalaTest extends AnyFunSuite {
  test("stubbing of methods that return AnyVals works") {
    val requestContext = mock[RequestContext]

    when(requestContext.customerId).thenReturn(CustomerId(123L))

    assert(requestContext.customerId == CustomerId(123L))
  }

  test("verification of method calls taking AnyVal parameters works") {
    val requestContext = mock[RequestContext]

    requestContext.setRequestId(RequestId(123L))

    verify(requestContext).setRequestId(eqTo(RequestId(123L)))
    verify(requestContext).setRequestId(any[RequestId])
    // but this will not work: verify(requestContext).setRequestId(any)
  }

  test("stubbing method calls with default parameters is easy") {
    // by default return empty Lists, None's etc instead of Java `null`
    implicit val defaultAnswer: DefaultAnswer = DefaultAnswers.ReturnsEmptyValues

    val requestContext = mock[RequestContext]

    when(requestContext.customerPermissions()).thenReturn(
      List("create-foozble"))

    assert(requestContext.customerPermissions() == List("create-foozble"))
    assert(requestContext.customerPermissions(None) == List("create-foozble"))

    // thanks to defaultAnswer we return `List()` or `Nil` here instead
    // of `null`
    assert(requestContext.customerPermissions(Some("foo")) == List())
  }

  trait SomeTrait {
    def method(a: Int, b: Int = 100): Int
  }

  test("verification of method calls with default parameters works") {
    val someTrait = mock[SomeTrait]

    someTrait.method(3)

    verify(someTrait).method(3)
    verify(someTrait).method(3, 100)

    // and let's check it one more time for RequestContext trait
    val requestContext = mock[RequestContext]

    requestContext.customerPermissions()

    verify(requestContext).customerPermissions()
    verify(requestContext).customerPermissions(None)
    verify(requestContext, never).customerPermissions(Some("foo"))
  }
}
