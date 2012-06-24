this.helloWorld = ->
    alert "Hello World"
this.helloWorldComplex = (name, age, job) ->
    alert "Hello World, my name is #{name}, I'm #{age} years old and I work as a #{job}"

this.compound = ->
    a = 10
    a += 35


this.a = 33
compound()

helloWorld()
helloWorldComplex(name, age, job)
helloWorldComplex name, age, job