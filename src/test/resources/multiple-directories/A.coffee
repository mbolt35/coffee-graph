###
# A HEADER
# FooBar (C) 2012
#
# Stuff ...
###

# Test Referencing class in other file
class Widget extends Foo
    prop: "aProperty"
    anotherProp: "anotherProp"
    
    constructor: (@name) ->

    testMethod: => console.log "#{@name}.testMethod"

this.Widget = Widget;