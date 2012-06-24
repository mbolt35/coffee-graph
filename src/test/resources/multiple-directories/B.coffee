class @Foo

    test: "testString"

    constructor: (@name) ->

    aBoundMethod: (input) =>
        @test = "testStringAgain"


# Another class in the file
class @Bar
    constructor: (@name) ->

    anotherTestMethod: -> console.log "Test!"


@isOn = on
@goodTimes = yes


