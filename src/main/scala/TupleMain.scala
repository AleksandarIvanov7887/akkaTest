object TupleMain extends App {
  type SchedulerMeta = (String, Int, Long)

  functionThis(t => ("a", 3, t._3))

  def functionThis(updater: SchedulerMeta  => SchedulerMeta): Unit = {
    println(updater("this", 1, 10L))
  }
}
