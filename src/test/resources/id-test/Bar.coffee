class @Bar
  constructor: ->
  aMethod = ->
    anotherInnerMethod = ->
      Foo = "someStringValue"

      alert Foo

  test: ->
    new Roo()