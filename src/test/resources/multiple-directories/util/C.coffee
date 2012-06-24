class this.C
    constructor: (@name) ->
    just: 1
    another: 2
    test: 3

class CC extends C
    constructor: (@name) ->
        super name

    test: 15
    anotherTest: -> new Test()

    foo: new Foo()

this.CC = CC;