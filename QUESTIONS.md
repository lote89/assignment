# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**

Yes.
I would make all database access work in the same way and move business logic out of the API classes. This makes the code easier to understand, safer to change, and easier to maintain.

2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**

OpenAPI:-
Good for clear rules and documentation.
Helps teams agree on how APIs work.
Takes more time to set up.

Manual coding:-
Faster to write at first
Easier to change quickly
Can become messy over time

My choice:-
Use OpenAPI for important APIs, manual coding only for small or internal ones.
