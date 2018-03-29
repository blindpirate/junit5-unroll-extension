# JUnit 5 Unroll Extension for Kotlin


[![Build Status](https://travis-ci.org/blindpirate/junit5-unroll-extension.svg?branch=master)](https://travis-ci.org/blindpirate/junit5-unroll-extension)
[![Apache License 2](https://img.shields.io/badge/license-APL2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)


JUnit 5 Unroll Extension is a JUnit 5 extension which supports parameterized tests in kotlin. TL;DR:

```
class Math {
    @Unroll
    fun `max number of {0} and {1} is {2}`(
            a: Int, b: Int, c: Int, param: Param = where {
                1 _ 3 _ 3
                7 _ 4 _ 7 
                0 _ 0 _ 0
            }) {
        assert(Math.max(a, b) == c)
    }
}
``` 

yields the following result:

```
max number of {0} and {1} is {2}(int, int, int, com.github.blindpirate.Param) ✔
├─ max number of 0 and 0 is 0 ✔
├─ max number of 1 and 3 is 3 ✔
└─ max number of 7 and 4 is 7 ✔
```

## How to use

- Currently it's still under development so it has not been uploaded to maven central yet.
- Add `@Unroll` to your test method. Note `@Unroll` can't be used together with `@Test` or `@ParamerizedTest`.
- Write the parameters in the last parameter as shown above.

## Why Unroll Extension

This extension is greatly inspired by [Spock](http://spockframework.org/). If you have used [Spock](http://spockframework.org/), 
you may be impressed by its powerful [Data Driven Test](http://spockframework.org/spock/docs/1.0/data_driven_testing.html):


```
class Math extends Specification {
    def "maximum of two numbers"(int a, int b, int c) {
        expect:
        Math.max(a, b) == c

        where:
        a | b | c
        1 | 3 | 3
        7 | 4 | 7 
        0 | 0 | 0
    }
}
```

Other frameworks have similar features:

[JUnit 4 Parmeterized Tests](https://github.com/junit-team/junit4/wiki/parameterized-tests):

```
@RunWith(Parameterized.class)
public class FibonacciTest {
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                 { 0, 0 }, { 1, 1 }, { 2, 1 }, { 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 }  
           });
    }

    @Parameter // first data value (0) is default
    public /* NOT private */ int fInput;

    @Parameter(1)
    public /* NOT private */ int fExpected;

    @Test
    public void test() {
        assertEquals(fExpected, Fibonacci.compute(fInput));
    }
}
```

[JUnit 5 Parameterized Tests](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests):

```
@ParameterizedTest
@ValueSource(ints = { 1, 2, 3 })
void testWithValueSource(int argument) {
    assertTrue(argument > 0 && argument < 4);
}
```

However, you can hardly add anything other than strings and numbers into `@ValueSource`. Extra `ArgumentsProvider`
and `ArgumentConverter` make things much more complex.


With this extension, you can have your parameters as they are.
