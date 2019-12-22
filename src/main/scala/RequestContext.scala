trait RequestContext {
  val customerId: CustomerId

  def requestId: RequestId
  def setRequestId(requestId: RequestId)

  def customerPermissions(filter: Option[String] = None): List[String]
}

case class FakeRequestContext(override val customerId: CustomerId) extends RequestContext {
  private var _requestId: RequestId = RequestId(-1)

  override def requestId: RequestId = _requestId

  override def setRequestId(requestId: RequestId): Unit = {
    _requestId = requestId
  }

  override def customerPermissions(filter: Option[String]): List[String] = {
    val perms = List("create-foo", "update-foo", "delete-foo",
                     "create-bar", "update-bar", "delete-bar")

    val matchingPerms = perms.filter(_.contains(filter.getOrElse("")))

    matchingPerms
  }
}
