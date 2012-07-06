root = exports ? this

boo = root

boo.Foo = class Foo
  constructor: ->

  aMethod: ->
    console.log "hello";

root.testMethod = ->
  a = 5 + 5
  a * 15;


