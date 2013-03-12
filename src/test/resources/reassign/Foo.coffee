root = exports ? this

boo = root
too = boo

boo.Foo = class Foo
  constructor: ->

  aMethod: ->
    console.log "hello";

too.FooToo = class FooToo
  constructor: ->

  tooMethod: ->
    console.log "hello again"

root.testMethod = ->
  a = 5 + 5
  a * 15;


