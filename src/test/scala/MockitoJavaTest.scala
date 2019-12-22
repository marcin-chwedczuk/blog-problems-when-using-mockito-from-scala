import java.lang.Object

import org.mockito.ArgumentMatchers.{any, anyLong}
import org.mockito.{ArgumentMatcher, ArgumentMatchers, Mockito}
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.funsuite.AnyFunSuite

class MockitoJavaTest extends AnyFunSuite {
  test("stubbing of methods that return AnyVals does not work out-of-the-box") {
    val requestContext = mock(classOf[RequestContext])

    /* Fails with:
     *
     * CustomerId cannot be returned by customerId()
     * customerId() should return long
     */
    assertThrows[org.mockito.exceptions.misusing.WrongTypeOfReturnValue] {
      when(requestContext.customerId).thenReturn(CustomerId(123L))
    }
  }

  test("hack to make stubbing of methods that return AnyVals work") {
    val requestContext = mock(classOf[RequestContext])

    when(requestContext.customerId.asInstanceOf[Object]).thenReturn(Long.box(123L))
    assert(requestContext.customerId == CustomerId(123L))

    // or with a helper method - I wouldn't call it maintainable:
    def whenLongAnyVal(value: AnyVal): OngoingStubbing[Long] = {
      when(1L)
    }

    whenLongAnyVal(requestContext.requestId).thenReturn(222L)
    assert(requestContext.requestId == RequestId(222L))
  }

  test("verifying calls of methods that contain AnyVal parameters does not work out-of-the-box") {
    val requestContext = mock(classOf[RequestContext])

    requestContext.setRequestId(RequestId(123L))

    // works perfectly when we pass values of the parameters:
    // verify(requestContext).setRequestId(RequestId(123L))

    // does NOT WORK when we try to use matchers like `any()` or `eq()`:
    // verify(requestContext).setRequestId(ArgumentMatchers.eq(RequestId(123L)))

    // Code below is equal to:
    // verify(requestContext).setRequestId(any())
    // but makes sure that we call some method on
    // the verifier before finishing the test,
    // otherwise we will screw up Mockito
    // global state...

    // verify(requestContext).setRequestId(any())
    val verifier = verify(requestContext)
    assertThrows[NullPointerException] {
      verifier.setRequestId(any())
    }

    // Clean up Mockito internal state after NPE
    verifier.setRequestId(RequestId(123L))
    // Make sure Mockito internal state is valid
    Mockito.validateMockitoUsage()
 }

  test("hack to make verifying of calls containing AnyVal parameters work again") {
    val requestContext = mock(classOf[RequestContext])

    requestContext.setRequestId(RequestId(123L))

    // works again
    verify(requestContext).setRequestId(RequestId(anyLong()))
    verify(requestContext).setRequestId(RequestId(ArgumentMatchers.eq(123L)))
  }

  test("stubbing of methods with default parameters does not work") {
    val requestContext = mock(classOf[RequestContext])

    when(requestContext.customerPermissions()).thenReturn(
      List("create-foozble"))

    assert(requestContext.customerPermissions() == List("create-foozble"))

    // Looks like it works, but:
    assert(requestContext.customerPermissions(None) == null)
  }

  test("hack to make stubbing of methods with default parameters work") {
    val requestContext = mock(classOf[RequestContext])

    // we need to mock two times, for `null` and `None`
    when(requestContext.customerPermissions()).thenReturn(
      List("create-foozble"))
    when(requestContext.customerPermissions(None)).thenReturn(
      List("create-foozble"))

    assert(requestContext.customerPermissions() == List("create-foozble"))
    assert(requestContext.customerPermissions(None) == List("create-foozble"))
  }

  trait SomeTrait {
    def method(a: Int, b: Int = 100): Int
  }

  test("verification of method calls containing default parameters is not working out-of-the-box") {
    val someTrait = mock(classOf[SomeTrait])

    someTrait.method(3)

    /* This call fails with:
     * Argument(s) are different! Wanted:
     *  someTrait.method(3, 100);
     * Actual invocations have different arguments:
     *  someTrait.method(3, 0);
     */
    // verify(someTrait).method(3, 100)

    verify(someTrait).method(3, 0)
  }
}
