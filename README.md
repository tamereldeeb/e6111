# Project 1 Group 11

## Team Members
- Tamer Eldeeb, `te2251`
- Nancy Hsieh, `nh2452`


## Files in Submission
- group11-proj1
	- ???.java
- README.md
- transcript.txt


## Run Instructions

1. cd to project root
3. package program: <br/>`mvn clean package`
4. execute jar: <br/>`java -jar target/project1-1.0-SNAPSHOT.jar <bing account key> <precision> <query>`



## Internal Design
The main component of the system is the QueryService class.
It accepts the bing key as well as the user's initial query and desired precision and performs the bing search on the user's behalf.
It is responsible for displaying search results and collecting relevant feedback, then modifying the query if the desired precision is not reached (see method below).
Internally, the QueryService uses a BingService class whose responsibility is to execute a single Bing query and retrieve the top 10 results.


## Query-Modification Method
Our method is inspired by Rocchio's algorithm. In our case, since we don't have access to the entire collection of web documents or any other such large collection we couldn't realistically compute or approximate idf, so we came up with the following simplified approach.

For every word (that is not already part of the query, and that is not a stop word*) that appears in the result title or summary, we calculate a triplet <rdp, idp, tf> where:
<br/>rdp: percentage of relevant documents the word appears in
<br/>idp: percentage of irrelevant documents the word appears in
<br/>rtf: total number of occurrences in relevant documents.

In every iteration, we choose the word that has the highest value of (rdp - idp) to add to the query. This formula gives a high weight to words that appear many relevant documents, while penalizing those that appear in irrelevant ones. We use the rtf to break ties.<br/>

Notes:<br/>
- In case more than one word have the exact same score, we pick any two to add to the query.)
- we maintain an explicit stop word list.

TODO: explain word ordering


## Bing Search Account Key
`4LqedL4leJhU1WKa1lSuKxpbEGlvWxFZjXYc++Xk4sk`
