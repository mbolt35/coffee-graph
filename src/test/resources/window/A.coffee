class window.A
  constructor: ->

  anotherMethod: ->
    b = new B()
    b.aMethod()

a = new A()
a.anotherMethod()