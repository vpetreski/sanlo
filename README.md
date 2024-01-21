# Sanlo

The only requirement is JDK 17, then you can run `./gradlew run`.

I spent a few hours, nothing was especially challenging, code is small, it was just to properly implement the math based on the given requirement.

We could improve our data model to be able to almost dynamically add new signals by providing additional CSV file with signal data.
Each signal would contain weighting, mapping for providing values, etc.
We would still have to refactor the code a bit - to add new signals to the final formula.

Linear Time Complexity: O(n)
