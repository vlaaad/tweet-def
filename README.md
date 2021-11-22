# Use tweets as a dependency

## Rationale

Clojure is so concise and expressive some libraries can fit in a tweet. Tweets are also immutable —
this makes them a very safe and reliable dependency source. Why go through the hassle of setting up a repo 
for a one-liner when you can tweet it?

## Usage:

Here is an example [tweet library](https://twitter.com/gigasquid/status/557897741511454724) authored by @gigasquid:

> Today's word of the day is "penultimate".
> 
> Clojure definition: (defn penultimate [x] (last (butlast x)))
> 
> &#35;wordOfTheDayDefinedInClojure

You can now depend on this library in your code:

``` 
$ clj -Sdeps '{:deps {io.github.vlaaad/tweet-def {:git/tag "v2" :git/sha "134a803"}}}'
Clojure 1.10.3
user=> (require '[io.github.vlaaad.tweet-def :as tweet])
nil
user=> (tweet/def "https://twitter.com/gigasquid/status/557897741511454724")
#'user/penultimate
user=> (penultimate [1 2 3])
2
```
