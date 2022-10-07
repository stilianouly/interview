# Assumptions / Simplifications / Choices

- Using the sbt-extras script for ease of downloading the appropriate sbt version https://github.com/dwijnand/sbt-extras
- Updated the project sbt version since the old one had issues with newer version of java
- The services ultimately looked like the place to put implementation details, so I added an extra package level to be specifically about one-frame


- My one-frame service requests all possible combinations of currencies, for the following reasons:
  - Each request to one-frame, no matter the number of currency pairs, still counts as 1 token request
  - If the output of my algebra is a list of rates then I can easily cache this list of rates, to avoid unnecessary calls to my service
  - A day can be split into 288, 5 minute blocks, in the worst case with this solution we will be making 288 api calls and the rest should be cached


- Currency parser and string pattern match were unsafe, so I modified them and the query parsers to use Option


- I have low exposure to the tagless final pattern, so my initial implementations to get things working end to end are using other patterns, I then converted them to conform to the idioms of the codebase, this is reflected in my git commits. 

# Setup

From the `forex-mtl` directory:
> chmod +x ./sbt

# Tests

> ./sbt test

# Dev

1. Start the one-frame service on port 8090

> docker run -p 8090:8080 paidyinc/one-frame

2. Run the forex-mtl program (It will run on port 8080)

> ./sbt run

# Usage

> curl -H "token: 10dc303535874aeccc86a8251e6992f5" 'localhost:8080/rates?from=AUD&to=USD'

